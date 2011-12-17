/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionManagerFactory;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.mylyn.internal.commons.http.AuthenticatedProxy;
import org.eclipse.mylyn.internal.commons.http.CommonsHttpPlugin;
import org.eclipse.mylyn.internal.commons.http.MonitoredRequest;
import org.eclipse.mylyn.internal.commons.http.PollingInputStream;
import org.eclipse.mylyn.internal.commons.http.PollingProtocolSocketFactory;
import org.eclipse.mylyn.internal.commons.http.PollingSslProtocolSocketFactory;
import org.eclipse.mylyn.internal.commons.http.TimeoutInputStream;

/**
 * @author Steffen Pingel
 * @author Shawn Minto
 * @since 3.6
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class HttpUtil {

	public static final class SingleConnectionManagerFactory implements ClientConnectionManagerFactory {

		public ClientConnectionManager newInstance(HttpParams params, SchemeRegistry schemeRegistry) {
			return new ThreadSafeClientConnManager(schemeRegistry);
		}

	}

	private static final int HTTPS_PORT = 443;

	private static final int HTTP_PORT = 80;

	// FIXME remove this again
	private static final boolean TEST_MODE;

	static {
		String application = System.getProperty("eclipse.application", ""); //$NON-NLS-1$ //$NON-NLS-2$
		if (application.length() > 0) {
			TEST_MODE = application.endsWith("testapplication"); //$NON-NLS-1$
		} else {
			// eclipse 3.3 does not the eclipse.application property
			String commands = System.getProperty("eclipse.commands", ""); //$NON-NLS-1$ //$NON-NLS-2$
			TEST_MODE = commands.contains("testapplication\n"); //$NON-NLS-1$
		}
	}

	private static final String USER_AGENT_PREFIX;

	private static final String USER_AGENT_POSTFIX;

	private static final String USER_AGENT;

	static {
		initCommonsLoggingSettings();

		StringBuilder sb = new StringBuilder();
		sb.append("Mylyn"); //$NON-NLS-1$
		sb.append(getBundleVersion(CommonsHttpPlugin.getDefault()));

		USER_AGENT_PREFIX = sb.toString();
		sb.setLength(0);

		if (System.getProperty("org.osgi.framework.vendor") != null) { //$NON-NLS-1$
			sb.append(" "); //$NON-NLS-1$
			sb.append(System.getProperty("org.osgi.framework.vendor")); //$NON-NLS-1$
			sb.append(stripQualifier(System.getProperty("osgi.framework.version"))); //$NON-NLS-1$

			if (System.getProperty("eclipse.product") != null) { //$NON-NLS-1$
				sb.append(" ("); //$NON-NLS-1$
				sb.append(System.getProperty("eclipse.product")); //$NON-NLS-1$
				sb.append(")"); //$NON-NLS-1$
			}
		}

		HttpParams params = new BasicHttpParams();
		DefaultHttpClient.setDefaultHttpParams(params);
		String parameter = HttpProtocolParams.getUserAgent(params);
		if (parameter != null) {
			String userAgent = parameter.toString();
			if (userAgent != null) {
				// shorten default "Apache-HttpClient/4.1 (java 1.5)"
				if (userAgent.startsWith("Apache-HttpClient/")) { //$NON-NLS-1$
					sb.append(" "); //$NON-NLS-1$
					sb.append(userAgent.substring(8));
				} else {
					sb.append(" "); //$NON-NLS-1$
					sb.append(parameter.toString());
				}
			}
		}

		sb.append(" Java/"); //$NON-NLS-1$
		sb.append(System.getProperty("java.version")); //$NON-NLS-1$
		sb.append(" ("); //$NON-NLS-1$
		sb.append(System.getProperty("java.vendor").split(" ")[0]); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append(") "); //$NON-NLS-1$

		sb.append(System.getProperty("os.name")); //$NON-NLS-1$
		sb.append("/"); //$NON-NLS-1$
		sb.append(System.getProperty("os.version")); //$NON-NLS-1$
		sb.append(" ("); //$NON-NLS-1$
		sb.append(System.getProperty("os.arch")); //$NON-NLS-1$
		if (System.getProperty("osgi.nl") != null) { //$NON-NLS-1$
			sb.append("; "); //$NON-NLS-1$
			sb.append(System.getProperty("osgi.nl")); //$NON-NLS-1$
		}
		sb.append(")"); //$NON-NLS-1$

		USER_AGENT_POSTFIX = sb.toString();

		USER_AGENT = USER_AGENT_PREFIX + USER_AGENT_POSTFIX;
	}

	private static SchemeSocketFactory sslSocketFactory = new PollingSslProtocolSocketFactory();

	private static SchemeSocketFactory socketFactory = new PollingProtocolSocketFactory();

	private static final int CONNNECT_TIMEOUT = 60 * 1000;

	private static final int SOCKET_TIMEOUT = 3 * 60 * 1000;

	private static final int POLL_INTERVAL = 500;

	private static final int POLL_ATTEMPTS = SOCKET_TIMEOUT / POLL_INTERVAL;

	private static final int BUFFER_SIZE = 4096;

	private static final long CLOSE_TIMEOUT = -1;

	public static void configureHttpClient(AbstractHttpClient client, String userAgent) {
		client.getParams().setParameter(ClientPNames.CONNECTION_MANAGER_FACTORY_CLASS_NAME,
				SingleConnectionManagerFactory.class.getName());

		client.getParams().setBooleanParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		HttpProtocolParams.setUserAgent(client.getParams(), getUserAgent(userAgent));

//		client.getParams().setLongParameter(AllClientPNames.CONNECTION_TIMEOUT, CONNNECT_TIMEOUT_INTERVAL);

		// TODO consider setting this as the default
		//client.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		configureHttpClientConnectionManager(client);
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

		monitor = Policy.monitorFor(monitor);

		MonitoredRequest<HttpResponse> executor = new MonitoredRequest<HttpResponse>(monitor) {
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

	/**
	 * @since 3.6
	 */
	public static <T> T execute(IProgressMonitor monitor, WebRequest<T> request) throws Throwable {
		// check for legacy reasons
		SubMonitor subMonitor = (monitor instanceof SubMonitor) ? (SubMonitor) monitor : SubMonitor.convert(null);

		Future<T> future = CommonsHttpPlugin.getExecutorService().submit(request);
		while (true) {
			if (monitor.isCanceled()) {
				request.abort();

				// wait for executor to finish
				future.cancel(false);
				try {
					if (!future.isCancelled()) {
						future.get();
					}
				} catch (CancellationException e) {
					// ignore
				} catch (InterruptedException e) {
					// ignore
				} catch (ExecutionException e) {
					// ignore
				}
				throw new OperationCanceledException();
			}

			try {
				return future.get(POLL_INTERVAL, TimeUnit.MILLISECONDS);
			} catch (ExecutionException e) {
				throw e.getCause();
			} catch (TimeoutException ignored) {
			}

			subMonitor.setWorkRemaining(20);
			subMonitor.worked(1);
		}
	}

	/**
	 * @since 3.6
	 */
	public static String getHost(String repositoryUrl) {
		String result = repositoryUrl;
		int colonSlashSlash = repositoryUrl.indexOf("://"); //$NON-NLS-1$

		if (colonSlashSlash >= 0) {
			result = repositoryUrl.substring(colonSlashSlash + 3);
		}

		int colonPort = result.indexOf(':');
		int requestPath = result.indexOf('/');

		int substringEnd;

		// minimum positive, or string length
		if (colonPort > 0 && requestPath > 0) {
			substringEnd = Math.min(colonPort, requestPath);
		} else if (colonPort > 0) {
			substringEnd = colonPort;
		} else if (requestPath > 0) {
			substringEnd = requestPath;
		} else {
			substringEnd = result.length();
		}

		return result.substring(0, substringEnd);
	}

	public static Credentials getHttpClientCredentials(AuthenticationCredentials credentials, String host) {
		String username = credentials.getUserName();
		String password = credentials.getPassword();
		int i = username.indexOf("\\"); //$NON-NLS-1$
		if (i > 0 && i < username.length() - 1 && host != null) {
			return new NTCredentials(username.substring(i + 1), password, host, username.substring(0, i));
		} else {
			return new UsernamePasswordCredentials(username, password);
		}
	}

	public static HttpContext getHttpContext(AbstractHttpClient client, AbstractWebLocation location,
			HttpContext previousContext, IProgressMonitor progressMonitor) {

		Assert.isNotNull(client);
		Assert.isNotNull(location);

		String url = location.getUrl();
		String host = getHost(url);
		int port = getPort(url);

		configureHttpClientConnectionManager(client);

		HttpContext context = previousContext;
		if (context == null) {
			context = new BasicHttpContext();
		}
		configureHttpClientProxy(client, context, location);

		AuthenticationCredentials authCreds = location.getCredentials(AuthenticationType.HTTP);
		if (authCreds != null) {
			AuthScope authScope = new AuthScope(host, port, AuthScope.ANY_REALM);
			Credentials credentials = getHttpClientCredentials(authCreds, host);

			if (credentials instanceof NTCredentials) {
				List<String> authpref = new ArrayList<String>();
				authpref.add(AuthPolicy.NTLM);
				authpref.add(AuthPolicy.BASIC);
				authpref.add(AuthPolicy.DIGEST);
				client.getParams().setParameter(AuthPNames.TARGET_AUTH_PREF, authpref);
			} else {
				List<String> authpref = new ArrayList<String>();
				authpref.add(AuthPolicy.BASIC);
				authpref.add(AuthPolicy.DIGEST);
				authpref.add(AuthPolicy.NTLM);
				client.getParams().setParameter(AuthPNames.TARGET_AUTH_PREF, authpref);
			}
			client.getCredentialsProvider().setCredentials(authScope, credentials);
		}

		if (isRepositoryHttps(url)) {
			Scheme sch = new Scheme("https", HTTPS_PORT, sslSocketFactory); //$NON-NLS-1$
			client.getConnectionManager().getSchemeRegistry().register(sch);
		} else {
			Scheme sch = new Scheme("http", HTTP_PORT, socketFactory); //$NON-NLS-1$
			client.getConnectionManager().getSchemeRegistry().register(sch);
		}

		return context;

	}

	/**
	 * @since 3.6
	 */
	public static int getPort(String repositoryUrl) {
		int colonSlashSlash = repositoryUrl.indexOf("://"); //$NON-NLS-1$
		int firstSlash = repositoryUrl.indexOf("/", colonSlashSlash + 3); //$NON-NLS-1$
		int colonPort = repositoryUrl.indexOf(':', colonSlashSlash + 1);
		if (firstSlash == -1) {
			firstSlash = repositoryUrl.length();
		}
		if (colonPort < 0 || colonPort > firstSlash) {
			return isRepositoryHttps(repositoryUrl) ? HTTPS_PORT : HTTP_PORT;
		}

		int requestPath = repositoryUrl.indexOf('/', colonPort + 1);
		int end = requestPath < 0 ? repositoryUrl.length() : requestPath;
		String port = repositoryUrl.substring(colonPort + 1, end);
		if (port.length() == 0) {
			return isRepositoryHttps(repositoryUrl) ? HTTPS_PORT : HTTP_PORT;
		}

		return Integer.parseInt(port);
	}

	/**
	 * @since 3.6
	 */
	public static String getRequestPath(String repositoryUrl) {
		int colonSlashSlash = repositoryUrl.indexOf("://"); //$NON-NLS-1$
		int requestPath = repositoryUrl.indexOf('/', colonSlashSlash + 3);

		if (requestPath < 0) {
			return ""; //$NON-NLS-1$
		} else {
			return repositoryUrl.substring(requestPath);
		}
	}

	public static InputStream getResponseBodyAsStream(HttpEntity entity, IProgressMonitor monitor) throws IOException {
		monitor = Policy.monitorFor(monitor);
		return new PollingInputStream(new TimeoutInputStream(entity.getContent(), BUFFER_SIZE, POLL_INTERVAL,
				CLOSE_TIMEOUT), POLL_ATTEMPTS, monitor);
	}

	/**
	 * Returns a user agent string that contains information about the platform and operating system. The
	 * <code>product</code> parameter allows to additional specify custom text that is inserted into the returned
	 * string. The exact return value depends on the environment.
	 * <p>
	 * Examples:
	 * <ul>
	 * <li>Headless: <code>Mylyn MyProduct HttpClient/3.1 Java/1.5.0_13 (Sun) Linux/2.6.22-14-generic (i386)</code>
	 * <li>Eclipse:
	 * <code>Mylyn/2.2.0 Eclipse/3.4.0 (org.eclipse.sdk.ide) HttpClient/3.1 Java/1.5.0_13 (Sun) Linux/2.6.22-14-generic (i386; en_CA)</code>
	 * 
	 * @param product
	 *            an identifier that is inserted into the returned user agent string
	 * @return a user agent string
	 * @since 3.6
	 */
	public static String getUserAgent(String product) {
		if (product != null && product.length() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(USER_AGENT_PREFIX);
			sb.append(" "); //$NON-NLS-1$
			sb.append(product);
			sb.append(USER_AGENT_POSTFIX);
			return sb.toString();
		} else {
			return USER_AGENT;
		}
	}

	private static void configureHttpClientConnectionManager(AbstractHttpClient client) {

		ClientConnectionManager connectionManager = client.getConnectionManager();

		HttpConnectionParams.setConnectionTimeout(client.getParams(), CONNNECT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(client.getParams(), SOCKET_TIMEOUT);

		if (connectionManager instanceof ThreadSafeClientConnManager) {
			ThreadSafeClientConnManager conMgr = (ThreadSafeClientConnManager) connectionManager;
			// FIXME fix connection leaks
			if (TEST_MODE) {
				conMgr.setDefaultMaxPerRoute(2);
			} else {
				conMgr.setDefaultMaxPerRoute(100);
				conMgr.setMaxTotal(1000);
			}
		}
	}

	private static void configureHttpClientProxy(AbstractHttpClient client, HttpContext context,
			AbstractWebLocation location) {
		String host = getHost(location.getUrl());

		Proxy proxy;
		if (isRepositoryHttps(location.getUrl())) {
			proxy = location.getProxyForHost(host, IProxyData.HTTPS_PROXY_TYPE);
		} else {
			proxy = location.getProxyForHost(host, IProxyData.HTTP_PROXY_TYPE);
		}

		if (proxy != null && !Proxy.NO_PROXY.equals(proxy)) {
			InetSocketAddress address = (InetSocketAddress) proxy.address();

			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					new HttpHost(address.getHostName(), address.getPort()));

			if (proxy instanceof AuthenticatedProxy) {
				AuthenticatedProxy authProxy = (AuthenticatedProxy) proxy;
				Credentials credentials = getCredentials(authProxy.getUserName(), authProxy.getPassword(),
						address.getAddress());
				if (credentials instanceof NTCredentials) {
					List<String> authpref = new ArrayList<String>();
					authpref.add(AuthPolicy.NTLM);
					authpref.add(AuthPolicy.BASIC);
					authpref.add(AuthPolicy.DIGEST);
					client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authpref);
				} else {
					List<String> authpref = new ArrayList<String>();
					authpref.add(AuthPolicy.BASIC);
					authpref.add(AuthPolicy.DIGEST);
					authpref.add(AuthPolicy.NTLM);
					client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authpref);
				}
				AuthScope proxyAuthScope = new AuthScope(address.getHostName(), address.getPort(), AuthScope.ANY_REALM);
				client.getCredentialsProvider().setCredentials(proxyAuthScope, credentials);
			}
		} else {
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, null);
		}
	}

	/**
	 * Only sets system property if they are not already set to a value.
	 */
	private static void defaultSystemProperty(String key, String defaultValue) {
		if (System.getProperty(key) == null) {
			System.setProperty(key, defaultValue);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T executeInternal(IProgressMonitor monitor, WebRequest<?> request) throws IOException {
		try {
			return (T) execute(monitor, request);
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

	private static String getBundleVersion(Plugin plugin) {
		if (null == plugin) {
			return ""; //$NON-NLS-1$
		}
		Object bundleVersion = plugin.getBundle().getHeaders().get("Bundle-Version"); //$NON-NLS-1$
		if (null == bundleVersion) {
			return ""; //$NON-NLS-1$
		}
		return stripQualifier((String) bundleVersion);
	}

	/**
	 * Disables logging by default. Set these system properties on launch enables verbose logging of HTTP communication:
	 * 
	 * <pre>
	 * -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog
	 * -Dorg.apache.commons.logging.simplelog.showlogname=true 
	 * -Dorg.apache.commons.logging.simplelog.defaultlog=off
	 * -Dorg.apache.commons.logging.simplelog.log.httpclient.wire=debug
	 * -Dorg.apache.commons.logging.simplelog.log.org.apache.commons.httpclient=off
	 * -Dorg.apache.commons.logging.simplelog.log.org.apache.axis.message=debug
	 * </pre>
	 */
	private static void initCommonsLoggingSettings() {
		defaultSystemProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static boolean isRepositoryHttps(String repositoryUrl) {
		return repositoryUrl.matches("https.*"); //$NON-NLS-1$
	}

	private static String stripQualifier(String longVersion) {
		if (longVersion == null) {
			return ""; //$NON-NLS-1$
		}

		String parts[] = longVersion.split("\\."); //$NON-NLS-1$
		StringBuilder version = new StringBuilder();
		if (parts.length > 0) {
			version.append("/"); //$NON-NLS-1$
			version.append(parts[0]);
			if (parts.length > 1) {
				version.append("."); //$NON-NLS-1$
				version.append(parts[1]);
				if (parts.length > 2) {
					version.append("."); //$NON-NLS-1$
					version.append(parts[2]);
				}
			}
		}
		return version.toString();

	}

	static Credentials getCredentials(final String username, final String password, final InetAddress address) {
		int i = username.indexOf("\\"); //$NON-NLS-1$
		if (i > 0 && i < username.length() - 1 && address != null) {
			return new NTCredentials(username.substring(i + 1), password, address.getHostName(), username.substring(0,
					i));
		} else {
			return new UsernamePasswordCredentials(username, password);
		}
	}
}
