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

package org.eclipse.mylyn.internal.commons.http;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSchemeSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.eclipse.core.runtime.IStatus;

/**
 * Provides support for managing SSL connections.
 * 
 * @author Nathan Hapke
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class PollingSslProtocolSocketFactory implements LayeredSchemeSocketFactory {

	private static final String KEY_STORE = "javax.net.ssl.keyStore"; //$NON-NLS-1$

	private static final String KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword"; //$NON-NLS-1$

	private static final String KEY_STORE_TYPE = "javax.net.ssl.keyStoreType"; //$NON-NLS-1$

	private final boolean hasKeyManager;

	private SSLSocketFactory socketFactory;

	public PollingSslProtocolSocketFactory() {
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
				CommonsHttpPlugin.log(IStatus.ERROR, "Could not initialize keystore", e); //$NON-NLS-1$
			}
		}

		hasKeyManager = keymanagers != null;

		try {
			SSLContext sslContext = SSLContext.getInstance("SSL"); //$NON-NLS-1$
			sslContext.init(keymanagers, new TrustManager[] { new TrustAllTrustManager() }, null);
			this.socketFactory = sslContext.getSocketFactory();
		} catch (Exception e) {
			CommonsHttpPlugin.log(IStatus.ERROR, "Could not initialize SSL context", e); //$NON-NLS-1$
		}
	}

	public Socket createSocket(HttpParams params) throws IOException {
		return getSocketFactory().createSocket();
	}

	public Socket connectSocket(Socket sock, InetSocketAddress remoteAddress, InetSocketAddress localAddress,
			HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {

		if (params == null) {
			throw new IllegalArgumentException("Parameters may not be null"); //$NON-NLS-1$
		}

		final Socket socket = getSocketFactory().createSocket();

		int connTimeout = HttpConnectionParams.getConnectionTimeout(params);

		socket.bind(localAddress);
		MonitoredRequest.connect(socket, remoteAddress, connTimeout);
		return socket;
	}

	/**
	 * From SSLSocketFactory
	 */
	public boolean isSecure(Socket sock) throws IllegalArgumentException {
		if (sock == null) {
			throw new IllegalArgumentException("Socket may not be null");
		}
		// This instanceof check is in line with createSocket() above.
		if (!(sock instanceof SSLSocket)) {
			throw new IllegalArgumentException("Socket not created by this factory");
		}
		// This check is performed last since it calls the argument object.
		if (sock.isClosed()) {
			throw new IllegalArgumentException("Socket is closed");
		}
		return true;
	}

	public Socket createLayeredSocket(Socket socket, String target, int port, boolean autoClose) throws IOException,
			UnknownHostException {
		return getSocketFactory().createSocket(socket, target, port, autoClose);
	}

	public SSLSocketFactory getSocketFactory() throws IOException {
		if (socketFactory == null) {
			throw new IOException("Could not initialize SSL context"); //$NON-NLS-1$
		}
		return socketFactory;
	}

}
