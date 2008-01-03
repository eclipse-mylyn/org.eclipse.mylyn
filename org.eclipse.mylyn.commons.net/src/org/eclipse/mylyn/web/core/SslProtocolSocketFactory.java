/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.web.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

/**
 * Provides support for managing SSL connections.
 * 
 * @author Nathan Hapke
 * @author Rob Elves
 * @author Steffen Pingel
 * @since 2.0
 */
// API-3.0 move to internal package and merge with PollingSslProtocolSocketFactory
public class SslProtocolSocketFactory implements SecureProtocolSocketFactory {

	private static final String KEY_STORE = "javax.net.ssl.keyStore";

	private static final String KEY_STORE_TYPE = "javax.net.ssl.keyStoreType";

	private static final String KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword";

	static SslProtocolSocketFactory factory = new SslProtocolSocketFactory();

	public static SslProtocolSocketFactory getInstance() {
		return factory;
	}

	private SSLSocketFactory socketFactory;

	private final boolean hasKeyManager;

	private SslProtocolSocketFactory() {
		KeyManager[] keymanagers = null;
		if (System.getProperty(KEY_STORE) != null && System.getProperty(KEY_STORE_PASSWORD) != null) {
			try {
				String type = System.getProperty(KEY_STORE_TYPE, KeyStore.getDefaultType());
				KeyStore keyStore = KeyStore.getInstance(type);
				char[] password = System.getProperty(KEY_STORE_PASSWORD).toCharArray();
				keyStore.load(new FileInputStream(System.getProperty(KEY_STORE)), password);
				KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				keyManagerFactory.init(keyStore, password);
				keymanagers = keyManagerFactory.getKeyManagers();
			} catch (Exception e) {
				WebCorePlugin.log(0, "Could not initialize keystore", e);
			}
		}

		hasKeyManager = keymanagers != null;

		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(keymanagers, new TrustManager[] { new TrustAllTrustManager() }, null);
			this.socketFactory = sslContext.getSocketFactory();
		} catch (Exception e) {
			WebCorePlugin.log(0, "Could not initialize SSL context", e);
		}
	}

	/**
	 * @since 2.3
	 */
	public SSLSocketFactory getSocketFactory() throws IOException {
		if (socketFactory == null) {
			throw new IOException("Could not initialize SSL context");
		}
		return socketFactory;
	}

	public Socket createSocket(String remoteHost, int remotePort) throws IOException, UnknownHostException {
		return getSocketFactory().createSocket(remoteHost, remotePort);
	}

	public Socket createSocket(String remoteHost, int remotePort, InetAddress clientHost, int clientPort)
			throws IOException, UnknownHostException {
		return getSocketFactory().createSocket(remoteHost, remotePort, clientHost, clientPort);
	}

	public Socket createSocket(String remoteHost, int remotePort, InetAddress clientHost, int clientPort,
			HttpConnectionParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
		if (params == null) {
			throw new IllegalArgumentException("Parameters may not be null");
		}

		int timeout = params.getConnectionTimeout();
		if (timeout == 0) {
			return getSocketFactory().createSocket(remoteHost, remotePort, clientHost, clientPort);
		} else {
			Socket socket = getSocketFactory().createSocket();
			socket.bind(new InetSocketAddress(clientHost, clientPort));
			socket.connect(new InetSocketAddress(remoteHost, remotePort), timeout);
			return socket;
		}
	}

	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException,
			UnknownHostException {
		return getSocketFactory().createSocket(socket, host, port, autoClose);
	}

	public boolean hasKeyManager() {
		return hasKeyManager;
	}

}
