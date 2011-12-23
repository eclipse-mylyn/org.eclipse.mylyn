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
import org.eclipse.mylyn.commons.core.operations.MonitoredOperation;

/**
 * Provides support for managing SSL connections.
 * 
 * @author Steffen Pingel
 */
class PollingSslProtocolSocketFactory implements LayeredSchemeSocketFactory {

	private final SslSupport sslSupport;

	public PollingSslProtocolSocketFactory() {
		this(new SslSupport(new TrustManager[] { new TrustAllTrustManager() }));
	}

	public PollingSslProtocolSocketFactory(SslSupport sslSupport) {
		this.sslSupport = sslSupport;
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

	public Socket createLayeredSocket(Socket socket, String target, int port, boolean autoClose) throws IOException,
			UnknownHostException {
		return NetUtil.configureSocket(getSocketFactory().createSocket(socket, target, port, autoClose));
	}

	public Socket createSocket(HttpParams params) throws IOException {
		return NetUtil.configureSocket(getSocketFactory().createSocket());
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
		if (sslSupport == null) {
			if (other.sslSupport != null) {
				return false;
			}
		} else if (!sslSupport.equals(other.sslSupport)) {
			return false;
		}
		return true;
	}

	public SSLSocketFactory getSocketFactory() throws IOException {
		return sslSupport.getSocketFactory();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sslSupport == null) ? 0 : sslSupport.hashCode());
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

}
