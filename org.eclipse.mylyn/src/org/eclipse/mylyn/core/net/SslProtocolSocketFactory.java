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

package org.eclipse.mylar.core.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.ProxyClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.eclipse.mylar.core.MylarStatusHandler;

/**
 * @author Nathan Hapke
 * @author Rob Elves
 */
public class SslProtocolSocketFactory implements SecureProtocolSocketFactory {

	private static SSLContext sslContext;

	static {
		try {
			sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[] { new TrustAllTrustManager() }, null);
		} catch (Exception e) {
			MylarStatusHandler.log(e, "Could not initialize SSL context");
		}
	}

	private Proxy proxy;

	public SslProtocolSocketFactory(Proxy proxy) {
		super();
		this.proxy = proxy;
	}

	private SSLContext getSslContext() throws IOException {
		if (sslContext == null) {
			throw new IOException("Could not initialize SSL context");
		}
		return sslContext;
	}

	public Socket createSocket(String remoteHost, int remotePort) throws IOException, UnknownHostException {
		return getSslContext().getSocketFactory().createSocket(remoteHost, remotePort);
	}

	public Socket createSocket(String remoteHost, int remotePort, InetAddress clientHost, int clientPort)
			throws IOException, UnknownHostException {
		return getSslContext().getSocketFactory().createSocket(remoteHost, remotePort, clientHost, clientPort);
	}

	public Socket createSocket(String remoteHost, int remotePort, InetAddress clientHost, int clientPort,
			HttpConnectionParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
		if (params == null || params.getConnectionTimeout() == 0)
			return getSslContext().getSocketFactory().createSocket(remoteHost, remotePort, clientHost, clientPort);

		if (proxy != null && !Proxy.NO_PROXY.equals(proxy) && proxy.address() instanceof InetSocketAddress) {
			ProxyClient proxyClient = new ProxyClient();

			InetSocketAddress address = (InetSocketAddress) proxy.address();
			proxyClient.getHostConfiguration().setProxy(WebClientUtil.getDomain(address.getHostName()),
					address.getPort());
			proxyClient.getHostConfiguration().setHost(remoteHost, remotePort);
			if (proxy instanceof AuthenticatedProxy) {
				AuthenticatedProxy authProxy = (AuthenticatedProxy) proxy;
				Credentials credentials = new UsernamePasswordCredentials(authProxy.getUserName(), authProxy
						.getPassword());
				AuthScope proxyAuthScope = new AuthScope(address.getHostName(), address.getPort(), AuthScope.ANY_REALM);
				proxyClient.getState().setProxyCredentials(proxyAuthScope, credentials);
			}

			ProxyClient.ConnectResponse response = proxyClient.connect();
			if (response.getSocket() != null) {
				// tunnel SSL via the resultant socket
				Socket sslsocket = getSslContext().getSocketFactory().createSocket(response.getSocket(), remoteHost,
						remotePort, true);
				return sslsocket;
			} else {
				MylarStatusHandler.log("Could not make proxy connection. Trying direct...", this);
			}

		}

		Socket socket = getSslContext().getSocketFactory().createSocket();
		socket.bind(new InetSocketAddress(clientHost, clientPort));
		socket.connect(new InetSocketAddress(remoteHost, remotePort), params.getConnectionTimeout());
		return socket;
	}

	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException,
			UnknownHostException {
		return getSslContext().getSocketFactory().createSocket(socket, host, port, autoClose);
	}

}
