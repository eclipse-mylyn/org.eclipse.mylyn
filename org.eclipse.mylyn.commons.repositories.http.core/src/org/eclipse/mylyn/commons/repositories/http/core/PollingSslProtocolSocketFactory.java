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
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.core.net.NetUtil;
import org.eclipse.mylyn.commons.core.net.TrustAllTrustManager;
import org.eclipse.mylyn.commons.core.operations.MonitoredOperation;

/**
 * Provides support for managing SSL connections.
 * 
 * @author Nathan Hapke
 * @author Rob Elves
 * @author Steffen Pingel
 */
class PollingSslProtocolSocketFactory implements LayeredSchemeSocketFactory {

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
				StatusHandler.log(new Status(IStatus.ERROR, HttpUtil.ID_PLUGIN, "Could not initialize keystore", e)); //$NON-NLS-1$
			}
		}

		hasKeyManager = keymanagers != null;

		try {
			SSLContext sslContext = SSLContext.getInstance("SSL"); //$NON-NLS-1$
			sslContext.init(keymanagers, new TrustManager[] { new TrustAllTrustManager() }, null);
			this.socketFactory = sslContext.getSocketFactory();
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, HttpUtil.ID_PLUGIN, "Could not initialize SSL context", e)); //$NON-NLS-1$
		}
	}

	public Socket createSocket(HttpParams params) throws IOException {
		return NetUtil.configureSocket(getSocketFactory().createSocket());
	}

	public Socket connectSocket(Socket sock, InetSocketAddress remoteAddress, InetSocketAddress localAddress,
			HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
		Assert.isNotNull(params);

		final Socket socket = NetUtil.configureSocket(getSocketFactory().createSocket());
		int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
		socket.bind(localAddress);
		NetUtil.connect(socket, remoteAddress, connTimeout, MonitoredOperation.getCurrentOperation());
		return socket;
	}

	public boolean isSecure(Socket socket) throws IllegalArgumentException {
		Assert.isNotNull(socket);
		if (!(socket instanceof SSLSocket)) {
			throw new IllegalArgumentException("Socket is not secure: " + socket.getClass()); //$NON-NLS-1$
		}
		if (socket.isClosed()) {
			throw new IllegalArgumentException("Socket is closed"); //$NON-NLS-1$
		}
		return true;
	}

	public Socket createLayeredSocket(Socket socket, String target, int port, boolean autoClose) throws IOException,
			UnknownHostException {
		return NetUtil.configureSocket(getSocketFactory().createSocket(socket, target, port, autoClose));
	}

	public SSLSocketFactory getSocketFactory() throws IOException {
		if (socketFactory == null) {
			throw new IOException("Could not initialize SSL context"); //$NON-NLS-1$
		}
		return socketFactory;
	}

}
