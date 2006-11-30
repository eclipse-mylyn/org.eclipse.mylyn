/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.core;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.protocol.Protocol;
import org.eclipse.update.internal.core.UpdateCore;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class WebClientUtil {

	private static final int HTTP_PORT = 80;

	private static final int HTTPS_PORT = 443;

	// private static final int COM_TIME_OUT = 30000;

	public static final String ENCODING_GZIP = "gzip";

	public static void initCommonsLoggingSettings() {
		// TODO: move?
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "off");
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "off");
	}

	/**
	 * public for testing
	 */
	public static boolean repositoryUsesHttps(String repositoryUrl) {
		return repositoryUrl.matches("https.*");
	}

	public static int getPort(String repositoryUrl) {
		int colonSlashSlash = repositoryUrl.indexOf("://");
		int colonPort = repositoryUrl.indexOf(':', colonSlashSlash + 1);
		if (colonPort < 0)
			return repositoryUsesHttps(repositoryUrl) ? HTTPS_PORT : HTTP_PORT;

		int requestPath = repositoryUrl.indexOf('/', colonPort + 1);

		int end;
		if (requestPath < 0)
			end = repositoryUrl.length();
		else
			end = requestPath;

		return Integer.parseInt(repositoryUrl.substring(colonPort + 1, end));
	}

	public static String getDomain(String repositoryUrl) {
		int colonSlashSlash = repositoryUrl.indexOf("://");

		int colonPort = repositoryUrl.indexOf(':', colonSlashSlash + 1);
		int requestPath = repositoryUrl.indexOf('/', colonSlashSlash + 3);

		int substringEnd;

		// minimum positive, or string length
		if (colonPort > 0 && requestPath > 0)
			substringEnd = Math.min(colonPort, requestPath);
		else if (colonPort > 0)
			substringEnd = colonPort;
		else if (requestPath > 0)
			substringEnd = requestPath;
		else
			substringEnd = repositoryUrl.length();

		return repositoryUrl.substring(colonSlashSlash + 3, substringEnd);
	}

	public static String getRequestPath(String repositoryUrl) {
		int colonSlashSlash = repositoryUrl.indexOf("://");
		int requestPath = repositoryUrl.indexOf('/', colonSlashSlash + 3);

		if (requestPath < 0)
			return "";
		else
			return repositoryUrl.substring(requestPath);
	}

	public static void setupHttpClient(HttpClient client, Proxy proxySettings, String repositoryUrl, String user,
			String password) {

		// Note: The following debug code requires http commons-logging and
		// commons-logging-api jars
		// System.setProperty("org.apache.commons.logging.Log",
		// "org.apache.commons.logging.impl.SimpleLog");
		// System.setProperty("org.apache.commons.logging.simplelog.showdatetime",
		// "true");
		// System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire",
		// "debug");
		// System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header",
		// "debug");
		// System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient",
		// "debug");
		

		if (proxySettings != null && proxySettings.address() instanceof InetSocketAddress) {
			InetSocketAddress address = (InetSocketAddress) proxySettings.address();
			client.getHostConfiguration().setProxy(WebClientUtil.getDomain(address.getHostName()), address.getPort());
			if (proxySettings instanceof AuthenticatedProxy) {
				AuthenticatedProxy authProxy = (AuthenticatedProxy) proxySettings;
				Credentials credentials = new UsernamePasswordCredentials(authProxy.getUserName(), authProxy
						.getPassword());
				AuthScope proxyAuthScope = new AuthScope(address.getHostName(), address.getPort(), AuthScope.ANY_REALM);
				client.getState().setProxyCredentials(proxyAuthScope, credentials);
			}
		}

		if (user != null && password != null) {
			AuthScope authScope = new AuthScope(WebClientUtil.getDomain(repositoryUrl), WebClientUtil
					.getPort(repositoryUrl), AuthScope.ANY_REALM);
			client.getState().setCredentials(authScope, new UsernamePasswordCredentials(user, password));
		}

		if (WebClientUtil.repositoryUsesHttps(repositoryUrl)) {
			Protocol acceptAllSsl = new Protocol("https", new SslProtocolSocketFactory(), WebClientUtil
					.getPort(repositoryUrl));
			client.getHostConfiguration().setHost(WebClientUtil.getDomain(repositoryUrl),
					WebClientUtil.getPort(repositoryUrl), acceptAllSsl);
		} else {
			client.getHostConfiguration().setHost(WebClientUtil.getDomain(repositoryUrl),
					WebClientUtil.getPort(repositoryUrl));
		}
	}

	
	/** utility method, should use TaskRepository.getProxy() */
	public static Proxy getSystemProxy() {
		Proxy proxy = Proxy.NO_PROXY;
		if (UpdateCore.getPlugin() != null && UpdateCore.getPlugin().getPluginPreferences().getBoolean(UpdateCore.HTTP_PROXY_ENABLE)) {
			String proxyHost = UpdateCore.getPlugin().getPluginPreferences().getString(UpdateCore.HTTP_PROXY_HOST);
			int proxyPort = UpdateCore.getPlugin().getPluginPreferences().getInt(UpdateCore.HTTP_PROXY_PORT);

			InetSocketAddress sockAddr = new InetSocketAddress(proxyHost, proxyPort);
			proxy = new Proxy(Type.HTTP, sockAddr);
		}
		return proxy;
	}

	/** utility method, should use TaskRepository.getProxy() */
	public static Proxy getProxy(String proxyHost, String proxyPort, String proxyUsername, String proxyPassword) {
		boolean authenticated = (proxyUsername != null && proxyPassword != null && proxyUsername.length() > 0 && proxyPassword
				.length() > 0);
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

}

// /**
// * Returns an opened HttpURLConnection. If the proxy fails a direct
// * connection is attempted.
// */
// public static HttpURLConnection openUrlConnection(URL url, Proxy proxy,
// boolean useTls, String htAuthUser,
// String htAuthPass) throws IOException, KeyManagementException,
// GeneralSecurityException {
//
// if (proxy == null) {
// proxy = Proxy.NO_PROXY;
// }
//
// HttpURLConnection remoteConnection = getUrlConnection(url, proxy, useTls,
// htAuthUser, htAuthPass);
// try {
// remoteConnection = openConnection(url, proxy);
// } catch (ConnectException e) {
// remoteConnection = openConnection(url, Proxy.NO_PROXY);
// }
//
// return remoteConnection;
// }

// /**
// * Returns connection that has yet to be opened (can still set connection
// * parameters). Catch ConnectException and retry with Proxy.NO_PROXY if
// * necessary.
// */
// public static HttpURLConnection getUrlConnection(URL url, Proxy proxy,
// boolean useTls, String htAuthUser,
// String htAuthPass) throws IOException, KeyManagementException,
// GeneralSecurityException {
// SSLContext ctx;
// if (useTls) {
// ctx = SSLContext.getInstance("TLS");
// } else {
// ctx = SSLContext.getInstance("SSL");
// }
//
// javax.net.ssl.TrustManager[] tm = new javax.net.ssl.TrustManager[] { new
// RepositoryTrustManager() };
// ctx.init(null, tm, null);
// HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
//
// if (proxy == null) {
// proxy = Proxy.NO_PROXY;
// }
//
// URLConnection connection = url.openConnection(proxy);
//
// // Add http basic authentication credentials if supplied
// // Ref: http://www.javaworld.com/javaworld/javatips/jw-javatip47.html
// if (htAuthUser != null && htAuthPass != null && !htAuthUser.equals("")) {
// String authenticationString = htAuthUser + ":" + htAuthPass;
// String encodedAuthenticationString = null;
// try {
// sun.misc.BASE64Encoder encoder = (sun.misc.BASE64Encoder)
// Class.forName("sun.misc.BASE64Encoder")
// .newInstance();
// encodedAuthenticationString =
// encoder.encode(authenticationString.getBytes());
// connection.setRequestProperty("Authorization", "Basic " +
// encodedAuthenticationString);
// } catch (Exception ex) {
// // ignore, encoder not available
// }
// }
//
// if (connection == null || !(connection instanceof HttpURLConnection)) {
// throw new MalformedURLException();
// }
// return (HttpURLConnection) connection;
// }

// private static HttpURLConnection openConnection(URL url, Proxy proxy)
// throws IOException {
// URLConnection connection = url.openConnection(proxy);
// if (connection == null || !(connection instanceof HttpURLConnection)) {
// throw new MalformedURLException();
// }
// HttpURLConnection remoteConnection = (HttpURLConnection) connection;
// remoteConnection.addRequestProperty("Accept-Encoding", ENCODING_GZIP);
// remoteConnection.setConnectTimeout(COM_TIME_OUT);
// remoteConnection.setReadTimeout(COM_TIME_OUT);
// remoteConnection.connect();
// return remoteConnection;
// }
