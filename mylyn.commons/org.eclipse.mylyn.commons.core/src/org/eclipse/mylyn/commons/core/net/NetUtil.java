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

package org.eclipse.mylyn.commons.core.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLSocket;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.core.operations.ICancellable;
import org.eclipse.mylyn.commons.core.operations.MonitoredOperation;
import org.eclipse.mylyn.internal.commons.core.CommonsCorePlugin;
import org.eclipse.osgi.util.NLS;

/**
 * Provides network access related utility methods.
 * 
 * @since 3.7
 * @author Steffen Pingel
 */
public class NetUtil {

	private static final String PROPERTY_MAX_HTTP_HOST_CONNECTIONS = "org.eclipse.mylyn.http.connections.per.host"; //$NON-NLS-1$

	private static final String PROPERTY_MAX_HTTP_TOTAL_CONNECTIONS = "org.eclipse.mylyn.http.total.connections"; //$NON-NLS-1$

	private static final int HTTPS_PORT = 443;

	private static final int HTTP_PORT = 80;

	private static final int MAX_HTTP_HOST_CONNECTIONS_DEFAULT = 100;

	private static final int MAX_HTTP_TOTAL_CONNECTIONS_DEFAULT = 1000;

	private final static String[] enabledProtocols;

	private final static AtomicBoolean loggedEnabledProtocolsException = new AtomicBoolean();

	static {
		String value = System.getProperty("org.eclipse.mylyn.https.protocols"); //$NON-NLS-1$
		enabledProtocols = (value != null) ? value.split(",") : null; //$NON-NLS-1$
	}

	/**
	 * Invokes {@link Socket#connect(java.net.SocketAddress, int)} on <code>socket</code> to connect to
	 * <code>address</code>.
	 * <p>
	 * If an operation is provided a cancellation listener is attached that aborts the connect in case the operation is
	 * aborted while connecting.
	 * 
	 * @param socket
	 *            the socket
	 * @param address
	 *            the address to connect to
	 * @param timeout
	 *            the connect timeout
	 * @param operation
	 *            the current operation or null
	 * @throws IOException
	 * @see {@link Socket#connect(java.net.SocketAddress, int)}
	 * @deprecated
	 */
	@Deprecated
	public static void connect(final Socket socket, InetSocketAddress address, int timeout,
			MonitoredOperation<?> operation) throws IOException {
		if (operation != null) {
			ICancellable listener = new ICancellable() {
				public void abort() {
					try {
						socket.close();
					} catch (IOException e) {
						// ignore
					}
				}
			};
			try {
				operation.addListener(listener);
				socket.connect(address, timeout);
			} finally {
				operation.removeListener(listener);
			}
		} else {
			socket.connect(address, timeout);
		}
	}

	public static Proxy createProxy(String proxyHost, int proxyPort) {
		return createProxy(proxyHost, proxyPort, null, null, null);
	}

	public static Proxy createProxy(String proxyHost, int proxyPort, String username, String password, String domain) {
		if (proxyHost != null && proxyHost.length() > 0) {
			InetSocketAddress sockAddr = new InetSocketAddress(proxyHost, proxyPort);
			boolean authenticated = (username != null && password != null && username.length() > 0 && password.length() > 0);
			if (authenticated) {
				return new AuthenticatedProxy(Type.HTTP, sockAddr, username, password, domain);
			} else {
				return new Proxy(Type.HTTP, sockAddr);
			}
		}
		return Proxy.NO_PROXY;
	}

	/**
	 * Returns the host portion of <code>url</code>.
	 * 
	 * @return the host portion of <code>url</code>; empty string, if url is not valid
	 * @since 3.7
	 */
	public static String getHost(String url) {
		Assert.isNotNull(url);

		String result = url;
		int colonSlashSlash = url.indexOf("://"); //$NON-NLS-1$

		if (colonSlashSlash >= 0) {
			result = url.substring(colonSlashSlash + 3);
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

	/**
	 * Returns the connection port for <code>url</code>. If no port is specified, 443 is returned for URLs that use the
	 * https protocol; otherwise, 80 is returned.
	 * 
	 * @return the port portion of <code>url</code>
	 * @throws NumberFormatException
	 *             if the port is not a parseable integer
	 * @since 3.7
	 */
	public static int getPort(String url) {
		Assert.isNotNull(url);

		int colonSlashSlash = url.indexOf("://"); //$NON-NLS-1$
		int firstSlash = url.indexOf("/", colonSlashSlash + 3); //$NON-NLS-1$
		int colonPort = url.indexOf(':', colonSlashSlash + 1);
		if (firstSlash == -1) {
			firstSlash = url.length();
		}
		if (colonPort < 0 || colonPort > firstSlash) {
			return isUrlHttps(url) ? HTTPS_PORT : HTTP_PORT;
		}

		int requestPath = url.indexOf('/', colonPort + 1);
		int end = requestPath < 0 ? url.length() : requestPath;
		String port = url.substring(colonPort + 1, end);
		if (port.length() == 0) {
			return isUrlHttps(url) ? HTTPS_PORT : HTTP_PORT;
		}

		return Integer.parseInt(port);
	}

	public static Proxy getProxy(String host, Proxy.Type proxyType) {
		Assert.isNotNull(host);
		Assert.isNotNull(proxyType);
		return getProxy(host, getPlatformProxyType(proxyType));
	}

	@SuppressWarnings("deprecation")
	public static Proxy getProxy(String host, String proxyType) {
		Assert.isNotNull(host);
		Assert.isNotNull(proxyType);
		IProxyService service = CommonsCorePlugin.getProxyService();
		if (service != null && service.isProxiesEnabled()) {
			// TODO e3.5 move to new proxy API
			IProxyData data = service.getProxyDataForHost(host, proxyType);
			if (data != null && data.getHost() != null) {
				String proxyHost = data.getHost();
				int proxyPort = data.getPort();
				// change the IProxyData default port to the Java default port
				if (proxyPort == -1) {
					proxyPort = 0;
				}
				return createProxy(proxyHost, proxyPort, data.getUserId(), data.getPassword(), null);
			}
		} else {
			try {
				// fall back to JDK proxy selector
				URI uri = new URI(proxyType, "//" + host, null); //$NON-NLS-1$
				List<Proxy> proxies = ProxySelector.getDefault().select(uri);
				if (proxies != null && proxies.size() > 0) {
					Proxy proxy = proxies.iterator().next();
					if (proxy != Proxy.NO_PROXY) {
						return proxy;
					}
				}
			} catch (URISyntaxException e) {
				// ignore
			}
		}
		return null;
	}

	/**
	 * Returns the platform default proxy for <code>url</code> or <code>null</code> if none.
	 */
	public static Proxy getProxyForUrl(String url) {
		String host = getHost(url);
		Proxy proxy;
		if (isUrlHttps(url)) {
			proxy = getProxy(host, IProxyData.HTTPS_PROXY_TYPE);
		} else {
			proxy = getProxy(host, IProxyData.HTTP_PROXY_TYPE);
		}
		return proxy;
	}

	/**
	 * Returns the request path part of <code>url</code>.
	 * 
	 * @return the request path portion of <code>url</code>; empty string, if url is not valid or not path is specified
	 * @since 3.7
	 */
	public static String getRequestPath(String url) {
		int colonSlashSlash = url.indexOf("://"); //$NON-NLS-1$
		int requestPath = url.indexOf('/', colonSlashSlash + 3);

		if (requestPath < 0) {
			return ""; //$NON-NLS-1$
		} else {
			return url.substring(requestPath);
		}
	}

	/**
	 * Returns true if <code>url</code> uses https as the protocol.
	 * 
	 * @since 3.7
	 */
	public static boolean isUrlHttps(String url) {
		return url.matches("https.*"); //$NON-NLS-1$
	}

	private static String getPlatformProxyType(Type type) {
		return type == Type.SOCKS ? IProxyData.SOCKS_PROXY_TYPE : IProxyData.HTTP_PROXY_TYPE;
	}

	public static Socket configureSocket(Socket socket) {
		if (socket instanceof SSLSocket && enabledProtocols != null) {
			try {
				((SSLSocket) socket).setEnabledProtocols(enabledProtocols);
			} catch (IllegalArgumentException e) {
				if (!loggedEnabledProtocolsException.getAndSet(true)) {
					StatusHandler.log(new Status(IStatus.ERROR, CommonsCorePlugin.ID_PLUGIN, NLS.bind(
							"Failed to configure SSL protocols ''{0}''", Arrays.toString(enabledProtocols)))); //$NON-NLS-1$
				}
			}
		}
		return socket;
	}

	/**
	 * @since 3.12
	 */
	public static int getMaxHttpConnectionsPerHost() {
		return getSystemPropertyAndParseInt(PROPERTY_MAX_HTTP_HOST_CONNECTIONS, MAX_HTTP_HOST_CONNECTIONS_DEFAULT);
	}

	/**
	 * @since 3.12
	 */
	public static int getMaxHttpConnections() {
		return getSystemPropertyAndParseInt(PROPERTY_MAX_HTTP_TOTAL_CONNECTIONS, MAX_HTTP_TOTAL_CONNECTIONS_DEFAULT);
	}

	private static int getSystemPropertyAndParseInt(String key, int defaultValue) {
		String property = System.getProperty(key);
		if (property != null) {
			try {
				return Integer.parseInt(property);
			} catch (NumberFormatException e) {
				StatusHandler.log(new Status(IStatus.WARNING, CommonsCorePlugin.ID_PLUGIN, NLS.bind(
						"Unable to parse property {0}", key))); //$NON-NLS-1$
			}
		}
		return defaultValue;
	}
}
