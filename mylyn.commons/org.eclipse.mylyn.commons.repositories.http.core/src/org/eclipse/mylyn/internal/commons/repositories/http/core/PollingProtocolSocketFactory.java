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

import javax.net.SocketFactory;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.eclipse.mylyn.commons.core.net.NetUtil;

/**
 * @author Steffen Pingel
 */
public class PollingProtocolSocketFactory implements SchemeSocketFactory {

	private final static SocketFactory factory = SocketFactory.getDefault();

	public Socket createSocket(HttpParams params) throws IOException {
		return NetUtil.configureSocket(factory.createSocket());
	}

	public Socket connectSocket(Socket sock, InetSocketAddress remoteAddress, InetSocketAddress localAddress,
			HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {

		if (params == null) {
			throw new IllegalArgumentException("Parameters may not be null"); //$NON-NLS-1$
		}

		final Socket socket = sock != null ? sock : NetUtil.configureSocket(factory.createSocket());

		int connTimeout = HttpConnectionParams.getConnectionTimeout(params);

		socket.bind(localAddress);
		socket.connect(remoteAddress, connTimeout);
		return socket;
	}

	public boolean isSecure(Socket sock) throws IllegalArgumentException {
		return false;
	}

}
