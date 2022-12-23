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
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.repositories.http.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSchemeSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.commons.core.net.NetUtil;
import org.eclipse.mylyn.commons.core.net.SslSupport;
import org.eclipse.mylyn.commons.core.net.TrustAllTrustManager;

/**
 * Provides support for managing SSL connections.
 * 
 * @author Steffen Pingel
 */
public class PollingSslProtocolSocketFactory implements LayeredSchemeSocketFactory {

	private final SslSupport defaultSslSupport;

	public PollingSslProtocolSocketFactory() {
		this(new SslSupport(new TrustManager[] { new TrustAllTrustManager() }));
	}

	public PollingSslProtocolSocketFactory(SslSupport sslSupport) {
		this.defaultSslSupport = sslSupport;
	}

	public Socket connectSocket(Socket sock, InetSocketAddress remoteAddress, InetSocketAddress localAddress,
			HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
		Assert.isNotNull(params);
		final Socket socket = (sock != null) ? sock : createSocket(params);

		if (localAddress != null) {
			socket.setReuseAddress(HttpConnectionParams.getSoReuseaddr(params));
			socket.bind(localAddress);
		}

		int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
		socket.connect(remoteAddress, connTimeout);

		if (socket instanceof SSLSocket) {
			return socket;
		} else {
			return getSslSupport(params).getSocketFactory().createSocket(socket, remoteAddress.getHostName(),
					remoteAddress.getPort(), true);
		}
	}

	public Socket createLayeredSocket(Socket socket, String target, int port, boolean autoClose) throws IOException,
			UnknownHostException {
		return NetUtil.configureSocket(getDefaultSocketFactory().createSocket(socket, target, port, autoClose));
	}

	public Socket createSocket(HttpParams params) throws IOException {
		Assert.isNotNull(params);
		return NetUtil.configureSocket(getSslSupport(params).getSocketFactory().createSocket());
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
		PollingSslProtocolSocketFactory other = (PollingSslProtocolSocketFactory) obj;
		if (defaultSslSupport == null) {
			if (other.defaultSslSupport != null) {
				return false;
			}
		} else if (!defaultSslSupport.equals(other.defaultSslSupport)) {
			return false;
		}
		return true;
	}

	public SSLSocketFactory getDefaultSocketFactory() throws IOException {
		return defaultSslSupport.getSocketFactory();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((defaultSslSupport == null) ? 0 : defaultSslSupport.hashCode());
		return result;
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

	private SslSupport getSslSupport(HttpParams params) {
		SslSupport sslSupport = (SslSupport) params.getParameter(SslSupport.class.getName());
		return (sslSupport != null) ? sslSupport : defaultSslSupport;
	}

}
