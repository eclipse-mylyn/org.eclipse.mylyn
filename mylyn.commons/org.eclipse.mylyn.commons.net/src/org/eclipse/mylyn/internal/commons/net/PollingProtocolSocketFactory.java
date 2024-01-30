/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

/**
 * @author Steffen Pingel
 */
public class PollingProtocolSocketFactory implements ProtocolSocketFactory {

	private final static SocketFactory factory = SocketFactory.getDefault();

	public PollingProtocolSocketFactory() {
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		return factory.createSocket(host, port);
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localAddress, int localPort)
			throws IOException, UnknownHostException {
		return factory.createSocket(host, port, localAddress, localPort);
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localAddress, int localPort,
			HttpConnectionParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
		if (params == null) {
			throw new IllegalArgumentException("Parameters may not be null"); //$NON-NLS-1$
		}

		int timeout = params.getConnectionTimeout();
		Socket socket = factory.createSocket();
		socket.bind(new InetSocketAddress(localAddress, localPort));
		MonitoredRequest.connect(socket, new InetSocketAddress(host, port), timeout);
		return socket;
	}

}
