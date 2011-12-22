/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.http.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.AuthPolicy;
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
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.core.io.PollingInputStream;
import org.eclipse.mylyn.commons.core.io.TimeoutInputStream;
import org.eclipse.mylyn.commons.core.net.AuthenticatedProxy;
import org.eclipse.mylyn.commons.core.net.NetUtil;
import org.eclipse.mylyn.commons.core.operations.MonitoredOperation;
import org.eclipse.mylyn.commons.core.operations.Operation;
import org.eclipse.mylyn.commons.core.operations.OperationUtil;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;

/**
 * @author Steffen Pingel
 * @author Shawn Minto
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class HttpUtil {

	private static final int BUFFER_SIZE = 4096;

	private static final long CLOSE_TIMEOUT = -1;

	/**
	 * @see IdleConnectionTimeoutThread#setTimeoutInterval(long)
	 */
	private static final int CONNECTION_TIMEOUT_INTERVAL = 30 * 1000;

	private static final int CONNNECT_TIMEOUT = 60 * 1000;

	private static final int HTTP_PORT = 80;

	private static final int HTTPS_PORT = 443;

	private static final int POLL_INTERVAL = 500;

	private static final int SOCKET_TIMEOUT = 3 * 60 * 1000;

	private static final int POLL_ATTEMPTS = SOCKET_TIMEOUT / POLL_INTERVAL;

	private static SchemeSocketFactory socketFactory = new PollingProtocolSocketFactory();

	private static SchemeSocketFactory sslSocketFactory = new PollingSslProtocolSocketFactory();

	static final String ID_PLUGIN = "org.eclipse.mylyn.commons.repositories.http"; //$NON-NLS-1$

	private static ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager(
			HttpUtil.getSchemeRegistry());

	static {
		if (CoreUtil.TEST_MODE) {
			connectionManager.setDefaultMaxPerRoute(2);
		} else {
			connectionManager.setDefaultMaxPerRoute(100);
			connectionManager.setMaxTotal(1000);
		}
	}

	public static void configureClient(AbstractHttpClient client, String userAgent) {
		HttpClientParams.setCookiePolicy(client.getParams(), CookiePolicy.RFC_2109);

		HttpProtocolParams.setUserAgent(client.getParams(), userAgent);
		HttpProtocolParams.setUseExpectContinue(client.getParams(), true);

		HttpConnectionParams.setConnectionTimeout(client.getParams(), CONNNECT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(client.getParams(), SOCKET_TIMEOUT);
	}

	public static void configureProxyAndAuthentication(AbstractHttpClient client, RepositoryLocation location,
			IProgressMonitor progressMonitor) {
		Assert.isNotNull(client);
		Assert.isNotNull(location);

		String url = location.getUrl();
		Assert.isNotNull("The location url must not be null", url);

		configureProxy(client, location, url);

		org.eclipse.mylyn.commons.repositories.core.auth.UsernamePasswordCredentials authCreds = location.getCredentials(
				AuthenticationType.HTTP,
				org.eclipse.mylyn.commons.repositories.core.auth.UsernamePasswordCredentials.class);
		if (authCreds != null) {
			String host = NetUtil.getHost(url);
			int port = NetUtil.getPort(url);
			Credentials credentials = getHttpClientCredentials(authCreds, host, false);
			if (credentials instanceof NTCredentials) {
				List<String> authpref = new ArrayList<String>();
				authpref.add(AuthPolicy.NTLM);
				authpref.add(AuthPolicy.BASIC);
				authpref.add(AuthPolicy.DIGEST);
				client.getParams().setParameter(AuthPNames.TARGET_AUTH_PREF, authpref);

				AuthScope authScopeNTLM = new AuthScope(host, port, AuthScope.ANY_REALM, AuthPolicy.NTLM);
				client.getCredentialsProvider().setCredentials(authScopeNTLM, credentials);

				AuthScope authScopeAny = new AuthScope(host, port, AuthScope.ANY_REALM);
				Credentials usernamePasswordCredentials = getHttpClientCredentials(authCreds, host, true);
				client.getCredentialsProvider().setCredentials(authScopeAny, usernamePasswordCredentials);
			} else {
				List<String> authpref = new ArrayList<String>();
				authpref.add(AuthPolicy.BASIC);
				authpref.add(AuthPolicy.DIGEST);
				authpref.add(AuthPolicy.NTLM);
				client.getParams().setParameter(AuthPNames.TARGET_AUTH_PREF, authpref);
				AuthScope authScope = new AuthScope(host, port, AuthScope.ANY_REALM);
				client.getCredentialsProvider().setCredentials(authScope, credentials);
			}
		}
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
			final HttpRequestBase method, IProgressMonitor monitor) throws IOException {
		Assert.isNotNull(client);
		Assert.isNotNull(method);

		monitor = OperationUtil.convert(monitor);

		MonitoredOperation<HttpResponse> executor = new MonitoredOperation<HttpResponse>(monitor) {
			@Override
			public void abort() {
				super.abort();
				method.abort();
			}

			@Override
			public HttpResponse execute() throws Exception {
				return client.execute(host, method, context);
			}
		};

		return executeInternal(monitor, executor);
	}

	public static Credentials getHttpClientCredentials(
			org.eclipse.mylyn.commons.repositories.core.auth.UsernamePasswordCredentials credentials, String host,
			boolean forceUserNamePassword) {
		String username = credentials.getUserName();
		String password = credentials.getPassword();
		int i = username.indexOf("\\"); //$NON-NLS-1$
		if (i > 0 && i < username.length() - 1 && host != null && !forceUserNamePassword) {
			String hostName = host;
			try {
				InetAddress localHost = InetAddress.getLocalHost();
				if (localHost != null) {
					hostName = localHost.getHostName();
				}
			} catch (UnknownHostException e) {
				StatusHandler.log(new Status(IStatus.ERROR, ID_PLUGIN,
						"Unable to get hostname.  Defaulting to servers host.", e));
			}
			if (hostName == null) {
				hostName = host;
			}
			return new NTCredentials(username.substring(i + 1), password, hostName, username.substring(0, i));
		} else {
			return new UsernamePasswordCredentials(username, password);
		}
	}

	public static InputStream getResponseBodyAsStream(HttpEntity entity, IProgressMonitor monitor) throws IOException {
		monitor = OperationUtil.convert(monitor);
		return new PollingInputStream(new TimeoutInputStream(entity.getContent(), BUFFER_SIZE, POLL_INTERVAL,
				CLOSE_TIMEOUT), POLL_ATTEMPTS, monitor);
	}

	public static SchemeRegistry getSchemeRegistry() {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", HTTP_PORT, socketFactory)); //$NON-NLS-1$
		schemeRegistry.register(new Scheme("https", HTTPS_PORT, sslSocketFactory)); //$NON-NLS-1$
		return schemeRegistry;
	}

	private static void configureProxy(AbstractHttpClient client, RepositoryLocation location, String url) {
		String host = NetUtil.getHost(url);

		Proxy proxy;
		if (NetUtil.isUrlHttps(location.getUrl())) {
			proxy = location.getService().getProxyForHost(host, IProxyData.HTTPS_PROXY_TYPE);
		} else {
			proxy = location.getService().getProxyForHost(host, IProxyData.HTTP_PROXY_TYPE);
		}

		if (proxy != null && !Proxy.NO_PROXY.equals(proxy)) {
			InetSocketAddress address = (InetSocketAddress) proxy.address();

			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					new HttpHost(address.getHostName(), address.getPort()));

			if (proxy instanceof AuthenticatedProxy) {
				AuthenticatedProxy authProxy = (AuthenticatedProxy) proxy;
				Credentials credentials = getCredentials(authProxy.getUserName(), authProxy.getPassword(),
						address.getAddress(), false);
				if (credentials instanceof NTCredentials) {
					List<String> authpref = new ArrayList<String>();
					authpref.add(AuthPolicy.NTLM);
					authpref.add(AuthPolicy.BASIC);
					authpref.add(AuthPolicy.DIGEST);
					client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authpref);
					AuthScope proxyAuthScopeNTLM = new AuthScope(address.getHostName(), address.getPort(),
							AuthScope.ANY_REALM, AuthPolicy.NTLM);
					client.getCredentialsProvider().setCredentials(proxyAuthScopeNTLM, credentials);

					AuthScope proxyAuthScopeAny = new AuthScope(address.getHostName(), address.getPort(),
							AuthScope.ANY_REALM);
					Credentials usernamePasswordCredentials = getCredentials(authProxy.getUserName(),
							authProxy.getPassword(), address.getAddress(), true);
					client.getCredentialsProvider().setCredentials(proxyAuthScopeAny, usernamePasswordCredentials);

				} else {
					List<String> authpref = new ArrayList<String>();
					authpref.add(AuthPolicy.BASIC);
					authpref.add(AuthPolicy.DIGEST);
					authpref.add(AuthPolicy.NTLM);
					client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authpref);
					AuthScope proxyAuthScope = new AuthScope(address.getHostName(), address.getPort(),
							AuthScope.ANY_REALM);
					client.getCredentialsProvider().setCredentials(proxyAuthScope, credentials);
				}
			}
		} else {
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, null);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T executeInternal(IProgressMonitor monitor, Operation<?> request) throws IOException {
		try {
			return (T) OperationUtil.execute(monitor, request);
		} catch (IOException e) {
			throw e;
		} catch (RuntimeException e) {
			throw e;
		} catch (Error e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
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
						"Unable to get hostname.  Defaulting to servers host.", e));
			}
			if (hostName == null) {
				hostName = address.getHostName();
			}
			return new NTCredentials(username.substring(i + 1), password, hostName, username.substring(0, i));
		} else {
			return new UsernamePasswordCredentials(username, password);
		}
	}

	public static ThreadSafeClientConnManager getConnectionManager() {
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
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e2) {
					// ignore
				}
			}
		} else {
			try {
				EntityUtils.consume(response.getEntity());
			} catch (IOException e) {
				// ignore
			}
		}
	}

}
