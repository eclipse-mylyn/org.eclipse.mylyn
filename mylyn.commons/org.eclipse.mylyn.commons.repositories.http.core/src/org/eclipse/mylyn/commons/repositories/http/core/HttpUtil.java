/*******************************************************************************
 * Copyright (c) 2011, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.http.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.core.net.AuthenticatedProxy;
import org.eclipse.mylyn.commons.core.net.NetUtil;
import org.eclipse.mylyn.commons.core.operations.CancellableOperationMonitorThread;
import org.eclipse.mylyn.commons.core.operations.ICancellableOperation;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.core.operations.OperationUtil;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.internal.commons.repositories.http.core.IdleConnectionMonitorThread;
import org.eclipse.mylyn.internal.commons.repositories.http.core.PollingProtocolSocketFactory;
import org.eclipse.mylyn.internal.commons.repositories.http.core.PollingSslProtocolSocketFactory;

/**
 * @author Steffen Pingel
 * @author Shawn Minto
 * @author Christian Janz
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class HttpUtil {

	static {
		CoreUtil.initializeLoggingSettings();
	}

	@SuppressWarnings("unused")
	private static final int BUFFER_SIZE = 4096;

	@SuppressWarnings("unused")
	private static final long CLOSE_TIMEOUT = -1;

	/**
	 * @see IdleConnectionMonitorThread
	 */
	private static final int CONNECTION_TIMEOUT_INTERVAL = 1 * 30 * 1000;

	private static final int CONNNECT_TIMEOUT = 60 * 1000;

	private static final int HTTP_PORT = 80;

	private static final int HTTPS_PORT = 443;

	private static final int POLL_INTERVAL = 500;

	private static final int SOCKET_TIMEOUT = 3 * 60 * 1000;

	@SuppressWarnings("unused")
	private static final int POLL_ATTEMPTS = SOCKET_TIMEOUT / POLL_INTERVAL;

	private static SchemeSocketFactory socketFactory = new PollingProtocolSocketFactory();

	private static SchemeSocketFactory sslSocketFactory = new PollingSslProtocolSocketFactory();

	static final String ID_PLUGIN = "org.eclipse.mylyn.commons.repositories.http"; //$NON-NLS-1$

	private static ThreadSafeClientConnManager connectionManager;

	static final String CONTEXT_KEY_MONITOR_THREAD = CancellableOperationMonitorThread.class.getName();

	public static void configureClient(AbstractHttpClient client, String userAgent) {
		HttpClientParams.setCookiePolicy(client.getParams(), CookiePolicy.BEST_MATCH);

		if (userAgent != null) {
			HttpProtocolParams.setUserAgent(client.getParams(), userAgent);
		}
		HttpProtocolParams.setUseExpectContinue(client.getParams(), true);
		client.getParams().setBooleanParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

		HttpConnectionParams.setConnectionTimeout(client.getParams(), CONNNECT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(client.getParams(), SOCKET_TIMEOUT);

		//AuthParams.setCredentialCharset(client.getParams(), "UTF-8");
	}

	public static void configureAuthentication(AbstractHttpClient client, RepositoryLocation location) {
		UserCredentials credentials = location.getCredentials(AuthenticationType.HTTP);
		if (credentials != null) {
			configureAuthentication(client, location, credentials);
		}
	}

	public static void configureAuthentication(AbstractHttpClient client, RepositoryLocation location,
			UserCredentials credentials) {
		Assert.isNotNull(client);
		Assert.isNotNull(location);
		Assert.isNotNull(credentials);
		String url = location.getUrl();
		Assert.isNotNull(url, "The location url must not be null"); //$NON-NLS-1$

		String host = NetUtil.getHost(url);
		int port = NetUtil.getPort(url);

		NTCredentials ntlmCredentials = getNtCredentials(credentials, ""); //$NON-NLS-1$
		if (ntlmCredentials != null) {
			AuthScope authScopeNtlm = new AuthScope(host, port, AuthScope.ANY_REALM, AuthPolicy.NTLM);
			client.getCredentialsProvider().setCredentials(authScopeNtlm, ntlmCredentials);
		}

		UsernamePasswordCredentials usernamePasswordCredentials = getUserNamePasswordCredentials(credentials);
		AuthScope authScopeAny = new AuthScope(host, port, AuthScope.ANY_REALM);
		client.getCredentialsProvider().setCredentials(authScopeAny, usernamePasswordCredentials);
	}

	public static HttpHost createHost(HttpRequestBase method) {
		URI uri = method.getURI();
		return new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
	}

	public static HttpResponse execute(final AbstractHttpClient client, final HttpContext context,
			final HttpRequestBase method, IProgressMonitor monitor) throws IOException {
		return execute(client, createHost(method), context, method, monitor);
	}

	public static HttpResponse execute(final AbstractHttpClient client, final HttpHost host, final HttpContext context,
			final HttpRequestBase method, final IProgressMonitor progress) throws IOException {
		Assert.isNotNull(client);
		Assert.isNotNull(method);

		final IOperationMonitor monitor = OperationUtil.convert(progress);
		ICancellableOperation operation = new ICancellableOperation() {
			@Override
			public void abort() {
				method.abort();
			}

			@Override
			public boolean isCanceled() {
				return monitor.isCanceled();
			}
		};

		CancellableOperationMonitorThread thread = null;
		if (context != null) {
			thread = (CancellableOperationMonitorThread) context.getAttribute(CONTEXT_KEY_MONITOR_THREAD);
		}
		if (thread != null) {
			thread.addOperation(operation);
		}
		try {
			return client.execute(host, method, context);
		} catch (InterruptedIOException e) {
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			throw e;
		} finally {
			if (thread != null) {
				thread.removeOperation(operation);
			}
		}
	}

	public static NTCredentials getNtCredentials(UserCredentials credentials, String workstation) {
		String username = credentials.getUserName();
		int i = username.indexOf("\\"); //$NON-NLS-1$
		if (i > 0 && i < username.length() - 1) {
//			try {
//				InetAddress localHost = InetAddress.getLocalHost();
//				if (localHost != null) {
//					hostName = localHost.getHostName();
//				}
//			} catch (UnknownHostException e) {
//				StatusHandler.log(new Status(IStatus.ERROR, ID_PLUGIN,
//						"Unable to get hostname.  Defaulting to servers host.", e));
//			}
			return new NTCredentials(username.substring(i + 1), credentials.getPassword(), workstation,
					username.substring(0, i));
		}
		return null;
	}

	public static UsernamePasswordCredentials getUserNamePasswordCredentials(UserCredentials credentials) {
		return new UsernamePasswordCredentials(credentials.getUserName(), credentials.getPassword());
	}

	public static InputStream getResponseBodyAsStream(HttpEntity entity, IProgressMonitor monitor) throws IOException {
		monitor = OperationUtil.convert(monitor);
		return entity.getContent();
//		return new PollingInputStream(new TimeoutInputStream(entity.getContent(), BUFFER_SIZE, POLL_INTERVAL,
//				CLOSE_TIMEOUT), POLL_ATTEMPTS, monitor);
	}

	public static SchemeRegistry getSchemeRegistry() {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", HTTP_PORT, socketFactory)); //$NON-NLS-1$
		schemeRegistry.register(new Scheme("https", HTTPS_PORT, sslSocketFactory)); //$NON-NLS-1$
		return schemeRegistry;
	}

	public static void configureProxy(AbstractHttpClient client, RepositoryLocation location) {
		Assert.isNotNull(client);
		Assert.isNotNull(location);
		String url = location.getUrl();
		Assert.isNotNull(url, "The location url must not be null"); //$NON-NLS-1$

		String host = NetUtil.getHost(url);
		Proxy proxy;
		if (NetUtil.isUrlHttps(url)) {
			proxy = location.getProxyForHost(host, IProxyData.HTTPS_PROXY_TYPE);
		} else {
			proxy = location.getProxyForHost(host, IProxyData.HTTP_PROXY_TYPE);
		}

		if (proxy != null && !Proxy.NO_PROXY.equals(proxy)) {
			InetSocketAddress address = (InetSocketAddress) proxy.address();

			client.getParams()
					.setParameter(ConnRoutePNames.DEFAULT_PROXY,
							new HttpHost(address.getHostName(), address.getPort()));

			if (proxy instanceof AuthenticatedProxy authProxy) {
				Credentials credentials = getCredentials(authProxy.getUserName(), authProxy.getPassword(),
						address.getAddress(), false);
				if (credentials instanceof NTCredentials) {
					AuthScope proxyAuthScopeNTLM = new AuthScope(address.getHostName(), address.getPort(),
							AuthScope.ANY_REALM, AuthPolicy.NTLM);
					client.getCredentialsProvider().setCredentials(proxyAuthScopeNTLM, credentials);

					AuthScope proxyAuthScopeAny = new AuthScope(address.getHostName(), address.getPort(),
							AuthScope.ANY_REALM);
					Credentials usernamePasswordCredentials = getCredentials(authProxy.getUserName(),
							authProxy.getPassword(), address.getAddress(), true);
					client.getCredentialsProvider().setCredentials(proxyAuthScopeAny, usernamePasswordCredentials);

				} else {
					AuthScope proxyAuthScope = new AuthScope(address.getHostName(), address.getPort(),
							AuthScope.ANY_REALM);
					client.getCredentialsProvider().setCredentials(proxyAuthScope, credentials);
				}
			}
		} else {
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, null);
		}
	}

	static Credentials getCredentials(final String username, final String password, final InetAddress address,
			boolean forceUserNamePassword) {
		int i = username.indexOf("\\"); //$NON-NLS-1$
		if (i > 0 && i < username.length() - 1 && address != null && !forceUserNamePassword) {
			String hostName = address.getHostName();
			try {
				InetAddress localHost = InetAddress.getLocalHost();
				if (localHost != null) {
					hostName = localHost.getHostName();
				}
			} catch (UnknownHostException e) {
				StatusHandler.log(new Status(IStatus.ERROR, ID_PLUGIN,
						"Unable to get hostname.  Defaulting to servers host.", e)); //$NON-NLS-1$
			}
			if (hostName == null) {
				hostName = address.getHostName();
			}
			return new NTCredentials(username.substring(i + 1), password, hostName, username.substring(0, i));
		} else {
			return new UsernamePasswordCredentials(username, password);
		}
	}

	public static synchronized ThreadSafeClientConnManager getConnectionManager() {
		if (connectionManager == null) {
			connectionManager = new ThreadSafeClientConnManager(HttpUtil.getSchemeRegistry());
			if (CoreUtil.TEST_MODE) {
				connectionManager.setDefaultMaxPerRoute(2);
			} else {
				connectionManager.setDefaultMaxPerRoute(NetUtil.getMaxHttpConnectionsPerHost());
				connectionManager.setMaxTotal(NetUtil.getMaxHttpConnections());
			}

			IdleConnectionMonitorThread thread = new IdleConnectionMonitorThread(CONNECTION_TIMEOUT_INTERVAL);
			thread.setTimeout(CONNNECT_TIMEOUT);
			thread.addConnectionManager(connectionManager);
			thread.start();
		}
		return connectionManager;
	}

	public static String getStatusText(int statusCode) {
		return EnglishReasonPhraseCatalog.INSTANCE.getReason(statusCode, Locale.getDefault());
	}

	public static void release(HttpRequest request, HttpResponse response, IProgressMonitor monitor) {
		Assert.isNotNull(request);
		Assert.isNotNull(response);
		if (monitor != null && monitor.isCanceled() && request instanceof HttpUriRequest) {
			// force a connection close on cancel to avoid blocking to do reading the remainder of the response
			try {
				((HttpUriRequest) request).abort();
			} catch (UnsupportedOperationException e) {
				// fall back to standard close
				consume(request, response);
			}
		} else {
			consume(request, response);
		}
	}

	private static void consume(HttpRequest request, HttpResponse response) {
		try {
			EntityUtils.consume(response.getEntity());
		} catch (IOException e) {
			// if construction of the stream fails the connection has to be aborted to be released
			try {
				((HttpUriRequest) request).abort();
			} catch (UnsupportedOperationException e2) {
			}
		} catch (NullPointerException e2) {
			// XXX work-around for bug 368830
		}
	}

}
