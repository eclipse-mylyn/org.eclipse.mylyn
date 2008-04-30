/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.commons.net;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.net.Proxy.Type;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.DefaultHttpParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.mylyn.internal.commons.net.CommonsNetPlugin;
import org.eclipse.mylyn.internal.commons.net.SslProtocolSocketFactory;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 * @author Leo Dos Santos - getFaviconForUrl
 * @author Rob Elves
 * @since 2.0
 * @deprecated use {@link WebUtil} instead
 */
@Deprecated
public class WebClientUtil {

	/**
	 * like Mylyn/2.1.0 (Rally Connector 1.0) Eclipse/3.3.0 (JBuilder 2007) HttpClient/3.0.1 Java/1.5.0_11 (Sun)
	 * Linux/2.6.20-16-lowlatency (i386; en)
	 */
	public static final String USER_AGENT;

	public static final String CONTENT_ENCODING_GZIP = "gzip";

	public static final int CONNNECT_TIMEOUT = 60000;

	public static final int SOCKET_TIMEOUT = 60000;

	private static final int HTTP_PORT = 80;

	private static final int HTTPS_PORT = 443;

	private static final int SOCKS_PORT = 1080;

	private static OutputStream logOutputStream = System.err;

	private static boolean loggingEnabled = false;

	private static final String USER_AGENT_PREFIX;

	private static final String USER_AGENT_POSTFIX;

	private static String stripQualifier(String longVersion) {
		if (longVersion == null) {
			return "";
		}

		String parts[] = longVersion.split("\\.");
		StringBuilder version = new StringBuilder();
		if (parts.length > 0) {
			version.append("/");
			version.append(parts[0]);
			if (parts.length > 1) {
				version.append(".");
				version.append(parts[1]);
				if (parts.length > 2) {
					version.append(".");
					version.append(parts[2]);
				}
			}
		}
		return version.toString();

	}

	private static String getBundleVersion(Plugin plugin) {
		if (null == plugin) {
			return "";
		}
		Object bundleVersion = plugin.getBundle().getHeaders().get("Bundle-Version");
		if (null == bundleVersion) {
			return "";
		}
		return stripQualifier((String) bundleVersion);
	}

	static {
		initCommonsLoggingSettings();

		StringBuilder sb = new StringBuilder();
		sb.append("Mylyn");
		sb.append(getBundleVersion(CommonsNetPlugin.getDefault()));

		USER_AGENT_PREFIX = sb.toString();
		sb.setLength(0);

		if (System.getProperty("org.osgi.framework.vendor") != null) {
			sb.append(" ");
			sb.append(System.getProperty("org.osgi.framework.vendor"));
			sb.append(stripQualifier(System.getProperty("osgi.framework.version")));

			if (System.getProperty("eclipse.product") != null) {
				sb.append(" (");
				sb.append(System.getProperty("eclipse.product"));
				sb.append(")");
			}
		}

		sb.append(" ");
		sb.append(DefaultHttpParams.getDefaultParams().getParameter(HttpMethodParams.USER_AGENT).toString().split("-")[1]);

		sb.append(" Java/");
		sb.append(System.getProperty("java.version"));
		sb.append(" (");
		sb.append(System.getProperty("java.vendor").split(" ")[0]);
		sb.append(") ");

		sb.append(System.getProperty("os.name"));
		sb.append("/");
		sb.append(System.getProperty("os.version"));
		sb.append(" (");
		sb.append(System.getProperty("os.arch"));
		if (System.getProperty("osgi.nl") != null) {
			sb.append("; ");
			sb.append(System.getProperty("osgi.nl"));
		}
		sb.append(")");

		USER_AGENT_POSTFIX = sb.toString();

		USER_AGENT = USER_AGENT_PREFIX + USER_AGENT_POSTFIX;
	}

	public static void initCommonsLoggingSettings() {
		// Remove?
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.defaultlog", "off");
		System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "off");
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "off");

		// FIXME this does not work with the commons logging Orbit bundle which does not see the WebClientLog class
		// Update our assigned logger to use custom WebClientLog
//		LogFactory logFactory = LogFactory.getFactory();
//		logFactory.setAttribute("org.apache.commons.logging.Log", "org.eclipse.mylyn.web.core.WebClientLog");
		// Note: level being set by Web
		// logFactory.setAttribute("org.apache.commons.logging.simplelog.showdatetime",
		// "true");
		// logFactory.setAttribute("org.apache.commons.logging.simplelog.log.httpclient.wire",
		// "debug");
		// logFactory.setAttribute("org.apache.commons.logging.simplelog.log.httpclient.wire.header",
		// "debug");
		// logFactory.setAttribute("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient",
		// "debug");
		// logFactory.setAttribute(
		// "org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient.HttpConnection",
		// "trace");
//		logFactory.release();
	}

	public static OutputStream getLogStream() {
		return logOutputStream;
	}

	public static void setLogStream(OutputStream stream) {
		logOutputStream = stream;
	}

	public static void setLoggingEnabled(boolean enabled) {
		loggingEnabled = enabled;
	}

	static boolean isRepositoryHttps(String repositoryUrl) {
		return repositoryUrl.matches("https.*");
	}

	public static int getPort(String repositoryUrl) {
		int colonSlashSlash = repositoryUrl.indexOf("://");
		int firstSlash = repositoryUrl.indexOf("/", colonSlashSlash + 3);
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

	public static String getDomain(String repositoryUrl) {
		String result = repositoryUrl;
		int colonSlashSlash = repositoryUrl.indexOf("://");

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

	public static String getRequestPath(String repositoryUrl) {
		int colonSlashSlash = repositoryUrl.indexOf("://");
		int requestPath = repositoryUrl.indexOf('/', colonSlashSlash + 3);

		if (requestPath < 0) {
			return "";
		} else {
			return repositoryUrl.substring(requestPath);
		}
	}

	/**
	 * @deprecated use {@link #createHostConfiguration(HttpClient, String, AbstractWebLocation, IProgressMonitor)}
	 *             instead
	 */
	@Deprecated
	public static void setupHttpClient(HttpClient client, Proxy proxySettings, String repositoryUrl, String user,
			String password) {

		WebUtil.configureHttpClient(client, null);

		if (proxySettings != null && !Proxy.NO_PROXY.equals(proxySettings)
		/* && !WebClientUtil.repositoryUsesHttps(repositoryUrl) */
		&& proxySettings.address() instanceof InetSocketAddress) {
			InetSocketAddress address = (InetSocketAddress) proxySettings.address();
			client.getHostConfiguration().setProxy(WebClientUtil.getDomain(address.getHostName()), address.getPort());
			if (proxySettings instanceof AuthenticatedProxy) {
				AuthenticatedProxy authProxy = (AuthenticatedProxy) proxySettings;
				Credentials credentials = getCredentials(authProxy.getUserName(), authProxy.getPassword(),
						address.getAddress());
				AuthScope proxyAuthScope = new AuthScope(address.getHostName(), address.getPort(), AuthScope.ANY_REALM);
				client.getState().setProxyCredentials(proxyAuthScope, credentials);
			}
		}

		if (user != null && password != null) {
			AuthScope authScope = new AuthScope(WebClientUtil.getDomain(repositoryUrl),
					WebClientUtil.getPort(repositoryUrl), AuthScope.ANY_REALM);
			try {
				client.getState().setCredentials(authScope, getCredentials(user, password, InetAddress.getLocalHost()));
			} catch (UnknownHostException e) {
				client.getState().setCredentials(authScope, getCredentials(user, password, null));
			}
		}

		if (WebClientUtil.isRepositoryHttps(repositoryUrl)) {
			Protocol acceptAllSsl = new Protocol("https",
					(ProtocolSocketFactory) SslProtocolSocketFactory.getInstance(),
					WebClientUtil.getPort(repositoryUrl));
			client.getHostConfiguration().setHost(WebClientUtil.getDomain(repositoryUrl),
					WebClientUtil.getPort(repositoryUrl), acceptAllSsl);
			Protocol.registerProtocol("https", acceptAllSsl);
		} else {
			client.getHostConfiguration().setHost(WebClientUtil.getDomain(repositoryUrl),
					WebClientUtil.getPort(repositoryUrl));
		}
	}

	public static Credentials getCredentials(AuthenticatedProxy authProxy, InetSocketAddress address) {
		return getCredentials(authProxy.getUserName(), authProxy.getPassword(), address.getAddress());
	}

	private static Credentials getCredentials(final String username, final String password, final InetAddress address) {
		int i = username.indexOf("\\");
		if (i > 0 && i < username.length() - 1 && address != null) {
			return new NTCredentials(username.substring(i + 1), password, address.getHostName(), username.substring(0,
					i));
		} else {
			return new UsernamePasswordCredentials(username, password);
		}
	}

	/**
	 * @since 2.2
	 */
	public static Credentials getHttpClientCredentials(AuthenticationCredentials credentials, String host) {
		String username = credentials.getUserName();
		String password = credentials.getPassword();
		int i = username.indexOf("\\");
		if (i > 0 && i < username.length() - 1 && host != null) {
			return new NTCredentials(username.substring(i + 1), password, host, username.substring(0, i));
		} else {
			return new UsernamePasswordCredentials(username, password);
		}
	}

	/** utility method, should use TaskRepository.getProxy() */
	public static Proxy getProxy(String proxyHost, String proxyPort, String proxyUsername, String proxyPassword) {
		boolean authenticated = (proxyUsername != null && proxyPassword != null && proxyUsername.length() > 0 && proxyPassword.length() > 0);
		if (proxyHost != null && proxyHost.length() > 0 && proxyPort != null && proxyPort.length() > 0) {
			int proxyPortNum = Integer.parseInt(proxyPort);
			InetSocketAddress sockAddr = new InetSocketAddress(proxyHost, proxyPortNum);
			if (authenticated) {
				return new AuthenticatedProxy(Type.HTTP, sockAddr, proxyUsername, proxyPassword);
			} else {
				return new Proxy(Type.HTTP, sockAddr);
			}
		}
		return Proxy.NO_PROXY;
	}

	/**
	 * utility method, proxy should be obtained via TaskRepository.getProxy()
	 * 
	 * TODO: deprecate
	 * 
	 * @return proxy as defined in platform proxy settings property page, Proxy.NO_PROXY otherwise
	 */
	public static Proxy getPlatformProxy() {
		Proxy proxy = Proxy.NO_PROXY;
		IProxyService service = CommonsNetPlugin.getProxyService();
		if (service != null && service.isProxiesEnabled()) {
			IProxyData data = service.getProxyData(IProxyData.HTTP_PROXY_TYPE);
			if (data.getHost() != null) {
				String proxyHost = data.getHost();
				int proxyPort = data.getPort();
				// Change the IProxyData default port to the Java default port
				if (proxyPort == -1) {
					proxyPort = 0;
				}

				InetSocketAddress sockAddr = new InetSocketAddress(proxyHost, proxyPort);
				proxy = new Proxy(Type.HTTP, sockAddr);
			}
		}
		return proxy;
	}

	/**
	 * utility method, proxy should be obtained via TaskRepository.getProxy()
	 * 
	 * @return proxy as defined in platform proxy settings property page, Proxy.NO_PROXY otherwise
	 * @since 2.1
	 */
	public static Proxy getPlatformProxy(String url) {
		Proxy proxy = Proxy.NO_PROXY;
		Type proxyType = Type.DIRECT;
		IProxyService service = CommonsNetPlugin.getProxyService();
		if (service != null && service.isProxiesEnabled()) {
			IProxyData proxyDataInUse = null;

			IProxyData httpProxy = service.getProxyDataForHost(getDomain(url), IProxyData.HTTP_PROXY_TYPE);
			IProxyData httpsProxy = service.getProxyDataForHost(getDomain(url), IProxyData.HTTPS_PROXY_TYPE);
			// See TODO below regarding socks
			// IProxyData socksProxy =
			// service.getProxyDataForHost(getDomain(url),
			// IProxyData.SOCKS_PROXY_TYPE);

			if (url.startsWith("https")) {
				if (httpsProxy != null) {
					proxyDataInUse = httpsProxy;
				} else if (httpProxy != null) {
					proxyDataInUse = httpProxy;
				}
			} else if (url.startsWith("http")) {
				if (httpProxy != null) {
					proxyDataInUse = httpProxy;
				}
			}
			// TODO: Support for SOCKS handled by Eclipse platform
			// currently hosts in exception list may not be excluded so will
			// require custom socket construction in httpclient.
			// else {
			// if (socksProxy != null) {
			// proxyDataInUse = socksProxy;
			// }
			// if (httpsProxy != null) {
			// proxyDataInUse = httpsProxy;
			// } else if (httpProxy != null) {
			// proxyDataInUse = httpProxy;
			// }
			// }

			if (proxyDataInUse != null) {
				int proxyPort = proxyDataInUse.getPort();
				if (proxyDataInUse.getType().equals(IProxyData.HTTP_PROXY_TYPE)) {
					proxyType = Type.HTTP;
					if (proxyPort == -1) {
						proxyPort = HTTP_PORT;
					}
				} else if (proxyDataInUse.getType().equals(IProxyData.HTTPS_PROXY_TYPE)) {
					proxyType = Type.HTTP;
					if (proxyPort == -1) {
						proxyPort = HTTPS_PORT;
					}
				} else {
					proxyType = Type.SOCKS;
					if (proxyPort == -1) {
						proxyPort = SOCKS_PORT;
					}
				}

				String proxyHost = proxyDataInUse.getHost();
				String proxyUserName = proxyDataInUse.getUserId();
				String proxyPassword = proxyDataInUse.getPassword();

				// Change the IProxyData default port to the Java default port
				// if (proxyPort == -1)
				// proxyPort = 0;

				InetSocketAddress sockAddr = new InetSocketAddress(proxyHost, proxyPort);
				if (proxyUserName != null && proxyUserName.length() > 0 && proxyPassword != null
						&& proxyPassword.length() > 0) {
					proxy = new AuthenticatedProxy(proxyType, sockAddr, proxyUserName, proxyPassword);
				} else {
					proxy = new Proxy(proxyType, sockAddr);
				}

			}
		}
		return proxy;
	}

	/**
	 * @since 2.2
	 */
	public static boolean isLoggingEnabled() {
		return loggingEnabled;
	}

	/**
	 * @deprecated use {@link #createHostConfiguration(HttpClient, String, AbstractWebLocation, IProgressMonitor)}
	 *             instead
	 */
	@Deprecated
	public static void setupHttpClient(HttpClient client, String userAgent, AbstractWebLocation location) {
		if (client == null || location == null) {
			throw new IllegalArgumentException();
		}

		String url = location.getUrl();
		String host = WebClientUtil.getDomain(url);
		int port = WebClientUtil.getPort(url);

		WebUtil.configureHttpClient(client, userAgent);
		setupHttpClientProxy(client, client.getHostConfiguration(), location);

		AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.HTTP);
		if (credentials != null) {
			AuthScope authScope = new AuthScope(host, port, AuthScope.ANY_REALM);
			client.getState().setCredentials(authScope, getHttpClientCredentials(credentials, host));
		}

		if (WebClientUtil.isRepositoryHttps(url)) {
			Protocol acceptAllSsl = new Protocol("https",
					(ProtocolSocketFactory) SslProtocolSocketFactory.getInstance(), port);
			client.getHostConfiguration().setHost(host, port, acceptAllSsl);

			// globally register handler, unfortunately Axis requires this
			Protocol.registerProtocol("https", acceptAllSsl);
		} else {
			client.getHostConfiguration().setHost(host, port);
		}
	}

	/**
	 * @since 2.3
	 * @deprecated use {@link #createHostConfiguration(HttpClient, AbstractWebLocation, IProgressMonitor)} and
	 *             {@link #configureHttpClient(HttpClient, String)}
	 */
	@Deprecated
	public static HostConfiguration createHostConfiguration(HttpClient client, String userAgent,
			AbstractWebLocation location, IProgressMonitor monitor) {
		WebUtil.configureHttpClient(client, userAgent);
		return createHostConfiguration(client, userAgent, location, monitor);
	}

	private static void setupHttpClientProxy(HttpClient client, HostConfiguration hostConfiguration,
			AbstractWebLocation location) {
		String host = WebClientUtil.getDomain(location.getUrl());

		Proxy proxy;
		if (WebClientUtil.isRepositoryHttps(location.getUrl())) {
			proxy = location.getProxyForHost(host, IProxyData.HTTP_PROXY_TYPE);
		} else {
			proxy = location.getProxyForHost(host, IProxyData.HTTPS_PROXY_TYPE);
		}

		if (proxy != null && !Proxy.NO_PROXY.equals(proxy)) {
			InetSocketAddress address = (InetSocketAddress) proxy.address();
			hostConfiguration.setProxy(address.getHostName(), address.getPort());
			if (proxy instanceof AuthenticatedProxy) {
				AuthenticatedProxy authProxy = (AuthenticatedProxy) proxy;
				Credentials credentials = getCredentials(authProxy.getUserName(), authProxy.getPassword(),
						address.getAddress());
				AuthScope proxyAuthScope = new AuthScope(address.getHostName(), address.getPort(), AuthScope.ANY_REALM);
				client.getState().setProxyCredentials(proxyAuthScope, credentials);
			}
		} else {
			hostConfiguration.setProxyHost(null);
		}
	}

	/**
	 * Returns a user agent string that contains information about the platform and operating system. The
	 * <code>product</code> parameter allows to additional specify custom text that is inserted into the returned
	 * string. The exact return value depends on the environment.
	 * 
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
	 * @since 2.3
	 */
	public static String getUserAgent(String product) {
		if (product != null && product.length() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(USER_AGENT_PREFIX);
			sb.append(" ");
			sb.append(product);
			sb.append(USER_AGENT_POSTFIX);
			return sb.toString();
		} else {
			return USER_AGENT;
		}
	}

}
