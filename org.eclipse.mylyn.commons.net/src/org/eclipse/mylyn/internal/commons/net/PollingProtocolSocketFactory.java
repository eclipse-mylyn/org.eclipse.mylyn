/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.net.WebUtil;

/**
 * @author Steffen Pingel
 */
public class PollingProtocolSocketFactory implements ProtocolSocketFactory {

	private final static SocketFactory factory = SocketFactory.getDefault();

	private final IProgressMonitor monitor;

	public PollingProtocolSocketFactory(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		return factory.createSocket(host, port);
	}

	public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException,
			UnknownHostException {
		return factory.createSocket(host, port, localAddress, localPort);
	}

	public Socket createSocket(String host, int port, InetAddress localAddress, int localPort,
			HttpConnectionParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
		if (params == null) {
			throw new IllegalArgumentException("Parameters may not be null"); //$NON-NLS-1$
		}

		int timeout = params.getConnectionTimeout();
		Socket socket = factory.createSocket();
		socket.bind(new InetSocketAddress(localAddress, localPort));
		// FIME convert OperationCanceledException?
		WebUtil.connect(socket, new InetSocketAddress(host, port), timeout, monitor);
		return socket;
	}

}
