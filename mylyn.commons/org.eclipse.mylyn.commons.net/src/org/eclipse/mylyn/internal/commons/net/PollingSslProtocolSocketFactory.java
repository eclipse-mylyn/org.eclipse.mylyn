/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     BREDEX GmbH - fix for bug 295050
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.net;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Objects;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.core.net.NetUtil;
import org.eclipse.mylyn.commons.net.SslCertificateException;

/**
 * Provides support for managing SSL connections.
 * 
 * @author Nathan Hapke
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class PollingSslProtocolSocketFactory implements SecureProtocolSocketFactory {

	private static final String KEY_STORE = "javax.net.ssl.keyStore"; //$NON-NLS-1$

	private static final String KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword"; //$NON-NLS-1$

	private static final String KEY_STORE_TYPE = "javax.net.ssl.keyStoreType"; //$NON-NLS-1$

	private final boolean hasKeyManager;

	private String keyStoreFileName;

	private String keyStorePassword;

	private String keyStoreType;

	private SSLSocketFactory socketFactory;

	public PollingSslProtocolSocketFactory() {
		KeyManager[] keymanagers = null;
		if (System.getProperty(KEY_STORE) != null && System.getProperty(KEY_STORE_PASSWORD) != null) {
			try {
				String type = System.getProperty(KEY_STORE_TYPE, KeyStore.getDefaultType());
				KeyStore keyStore = KeyStore.getInstance(type);
				char[] password = System.getProperty(KEY_STORE_PASSWORD).toCharArray();
				keyStore.load(new FileInputStream(System.getProperty(KEY_STORE)), password);
				KeyManagerFactory keyManagerFactory = KeyManagerFactory
						.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				keyManagerFactory.init(keyStore, password);
				keymanagers = keyManagerFactory.getKeyManagers();
			} catch (Exception e) {
				CommonsNetPlugin.log(IStatus.ERROR, "Could not initialize keystore", e); //$NON-NLS-1$
			}
		}

		hasKeyManager = keymanagers != null;

		try {
			SSLContext sslContext = SSLContext.getInstance("SSL"); //$NON-NLS-1$
			sslContext.init(keymanagers, new TrustManager[] { new TrustAllTrustManager() }, null);
			socketFactory = sslContext.getSocketFactory();
		} catch (Exception e) {
			CommonsNetPlugin.log(IStatus.ERROR, "Could not initialize SSL context", e); //$NON-NLS-1$
		}
	}

	public PollingSslProtocolSocketFactory(String keyStoreFileName, String keyStorePassword, String keyStoreType) {
		this.keyStoreFileName = keyStoreFileName;
		this.keyStorePassword = keyStorePassword;
		this.keyStoreType = keyStoreType;
		hasKeyManager = false;
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
			throws IOException, UnknownHostException {
		return NetUtil.configureSocket(getSocketFactory().createSocket(socket, host, port, autoClose));
	}

	@Override
	public Socket createSocket(String remoteHost, int remotePort) throws IOException, UnknownHostException {
		return NetUtil.configureSocket(getSocketFactory().createSocket(remoteHost, remotePort));
	}

	@Override
	public Socket createSocket(String remoteHost, int remotePort, InetAddress clientHost, int clientPort)
			throws IOException, UnknownHostException {
		return NetUtil.configureSocket(getSocketFactory().createSocket(remoteHost, remotePort, clientHost, clientPort));
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localAddress, int localPort,
			HttpConnectionParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
		if (params == null) {
			throw new IllegalArgumentException("Parameters may not be null"); //$NON-NLS-1$
		}

		int timeout = params.getConnectionTimeout();
		final Socket socket = NetUtil.configureSocket(getSocketFactory().createSocket());
		socket.bind(new InetSocketAddress(localAddress, localPort));
		MonitoredRequest.connect(socket, new InetSocketAddress(host, port), timeout);
		return socket;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		PollingSslProtocolSocketFactory other = (PollingSslProtocolSocketFactory) obj;
		if (!Objects.equals(keyStoreFileName, other.keyStoreFileName)) {
			return false;
		}
		if (!Objects.equals(keyStorePassword, other.keyStorePassword)) {
			return false;
		}
		if (!Objects.equals(keyStoreType, other.keyStoreType)) {
			return false;
		}
		return true;
	}

	public synchronized SSLSocketFactory getSocketFactory() throws IOException {
		if (socketFactory == null) {
			if (keyStoreFileName != null && keyStorePassword != null) {
				KeyManager[] keymanagers = null;
				try {
					if (keyStoreType == null) {
						try {
							keymanagers = openKeyStore(KeyStore.getDefaultType());
						} catch (Exception e) {
							keymanagers = openKeyStore("pkcs12"); //$NON-NLS-1$
						}
					} else {
						keymanagers = openKeyStore(keyStoreType);
					}
				} catch (Exception cause) {
					IOException e = new SslCertificateException();
					e.initCause(cause);
					throw e;
				}

				try {
					SSLContext sslContext = SSLContext.getInstance("SSL"); //$NON-NLS-1$
					sslContext.init(keymanagers, new TrustManager[] { new TrustAllTrustManager() }, null);
					socketFactory = sslContext.getSocketFactory();
				} catch (Exception cause) {
					IOException e = new SslCertificateException();
					e.initCause(cause);
					throw e;
				}
			}
			throw new IOException("Could not initialize SSL context"); //$NON-NLS-1$
		}
		return socketFactory;
	}

	private KeyManager[] openKeyStore(String type) throws KeyStoreException, IOException, NoSuchAlgorithmException,
			CertificateException, FileNotFoundException, UnrecoverableKeyException {
		KeyStore keyStore = KeyStore.getInstance(type);
		char[] password = keyStorePassword.toCharArray();
		keyStore.load(new FileInputStream(keyStoreFileName), password);
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keyStore, password);
		return keyManagerFactory.getKeyManagers();
	}

	@Override
	public int hashCode() {
		return Objects.hash(keyStoreFileName, keyStorePassword, keyStoreType);
	}

	/**
	 * Public for testing only.
	 */
	public boolean hasKeyManager() {
		return hasKeyManager;
	}

}
