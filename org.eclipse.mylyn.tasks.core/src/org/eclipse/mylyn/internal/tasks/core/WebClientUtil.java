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

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.protocol.Protocol;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class WebClientUtil {

	private static final int HTTP_PORT = 80;

	private static final int HTTPS_PORT = 443;

	private static final int COM_TIME_OUT = 30000;

	public static final String ENCODING_GZIP = "gzip";

	public static void initCommonsLoggingSettings() {
		// TODO: move?
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "off");
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "off");
	}

	/**
	 * Returns an opened HttpURLConnection. If the proxy fails a direct connection
	 * is attempted.
	 */
	public static HttpURLConnection openUrlConnection(URL url, Proxy proxy, boolean useTls) throws IOException,
			KeyManagementException, GeneralSecurityException {
		
		if (proxy == null) {
			proxy = Proxy.NO_PROXY;
		}
		
		HttpURLConnection remoteConnection = getUrlConnection(url, proxy, useTls);
		try {
			remoteConnection = openConnection(url, proxy);
		} catch (ConnectException e) {
			remoteConnection = openConnection(url, Proxy.NO_PROXY);
		}

		return remoteConnection;
	}

	/**
	 * Returns connection that has yet to be opened (can still set connection parameters).
	 * Catch ConnectException and retry with Proxy.NO_PROXY if necessary.
	 */
	public static HttpURLConnection getUrlConnection(URL url, Proxy proxy, boolean useTls) throws IOException,
			KeyManagementException, GeneralSecurityException {
		SSLContext ctx;
		if (useTls) {
			ctx = SSLContext.getInstance("TLS");
		} else {
			ctx = SSLContext.getInstance("SSL");
		}

		javax.net.ssl.TrustManager[] tm = new javax.net.ssl.TrustManager[] { new RepositoryTrustManager() };
		ctx.init(null, tm, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());

		if (proxy == null) {
			proxy = Proxy.NO_PROXY;
		}
		
		URLConnection connection = url.openConnection(proxy);
		
		if (connection == null || !(connection instanceof HttpURLConnection)) {
			throw new MalformedURLException();
		}		
		return (HttpURLConnection)connection;
	}

	private static HttpURLConnection openConnection(URL url, Proxy proxy) throws IOException {
		URLConnection connection = url.openConnection(proxy);
		if (connection == null || !(connection instanceof HttpURLConnection)) {
			throw new MalformedURLException();
		}
		HttpURLConnection remoteConnection = (HttpURLConnection) connection;
		remoteConnection.addRequestProperty("Accept-Encoding", ENCODING_GZIP);
		remoteConnection.setConnectTimeout(COM_TIME_OUT);
		remoteConnection.setReadTimeout(COM_TIME_OUT);
		remoteConnection.connect();
		return remoteConnection;
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

	public static void setupHttpClient(HttpClient client, Proxy proxySettings, String repositoryUrl) {
		if (proxySettings != null && proxySettings.address() instanceof InetSocketAddress) {
			InetSocketAddress address = (InetSocketAddress) proxySettings.address();
			client.getHostConfiguration().setProxy(address.getHostName(), address.getPort());
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

}
