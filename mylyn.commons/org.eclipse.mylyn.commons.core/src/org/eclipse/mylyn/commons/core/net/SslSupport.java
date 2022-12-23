/*******************************************************************************
 * Copyright (c) 2011, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.commons.core.net;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.eclipse.osgi.util.NLS;

/**
 * Provides support for managing SSL connections.
 * 
 * @author Steffen Pingel
 * @author Torsten Kalix
 * @since 3.7
 */
public class SslSupport {

	private static final String KEY_STORE_FILE_NAME = "javax.net.ssl.keyStore"; //$NON-NLS-1$

	private static final String KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword"; //$NON-NLS-1$

	private static final String KEY_STORE_TYPE = "javax.net.ssl.keyStoreType"; //$NON-NLS-1$

	private final String keyStoreFileName;

	private final String keyStorePassword;

	private final String keyStoreType;

	private SSLSocketFactory socketFactory;

	private final TrustManager[] trustManagers;

	public SslSupport(TrustManager[] trustManagers) {
		this(trustManagers, System.getProperty(KEY_STORE_FILE_NAME), System.getProperty(KEY_STORE_PASSWORD),
				System.getProperty(KEY_STORE_TYPE));
	}

	public SslSupport(TrustManager[] trustManagers, String keyStoreFileName, String keyStorePassword,
			String keyStoreType) {
		this.trustManagers = trustManagers;
		this.keyStoreFileName = keyStoreFileName;
		this.keyStorePassword = keyStorePassword;
		this.keyStoreType = keyStoreType;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SslSupport other = (SslSupport) obj;
		if (keyStoreFileName == null) {
			if (other.keyStoreFileName != null) {
				return false;
			}
		} else if (!keyStoreFileName.equals(other.keyStoreFileName)) {
			return false;
		}
		if (keyStorePassword == null) {
			if (other.keyStorePassword != null) {
				return false;
			}
		} else if (!keyStorePassword.equals(other.keyStorePassword)) {
			return false;
		}
		if (keyStoreType == null) {
			if (other.keyStoreType != null) {
				return false;
			}
		} else if (!keyStoreType.equals(other.keyStoreType)) {
			return false;
		}
		return true;
	}

	public synchronized SSLSocketFactory getSocketFactory() throws IOException {
		if (socketFactory == null) {
			KeyManager[] keyManagers = null;

			// load keystore from file
			if (keyStoreFileName != null && keyStorePassword != null) {
				try {
					if (keyStoreType != null) {
						keyManagers = openKeyStore(keyStoreType);
					} else {
						try {
							keyManagers = openKeyStore(KeyStore.getDefaultType());
						} catch (Exception e) {
							keyManagers = openKeyStore("pkcs12"); //$NON-NLS-1$
						}
					}
				} catch (Exception cause) {
					IOException e = new SslCertificateException(NLS.bind("Error accessing keystore: {0}", //$NON-NLS-1$
							cause.getMessage()));
					e.initCause(cause);
					throw e;
				}
			}

			try {
				SSLContext sslContext = SSLContext.getInstance("SSL"); //$NON-NLS-1$
				sslContext.init(keyManagers, trustManagers, null);

				this.socketFactory = sslContext.getSocketFactory();
			} catch (Exception cause) {
				IOException e = new SslCertificateException();
				e.initCause(cause);
				throw e;
			}
		}
		return socketFactory;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyStoreFileName == null) ? 0 : keyStoreFileName.hashCode());
		result = prime * result + ((keyStorePassword == null) ? 0 : keyStorePassword.hashCode());
		result = prime * result + ((keyStoreType == null) ? 0 : keyStoreType.hashCode());
		return result;
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

}
