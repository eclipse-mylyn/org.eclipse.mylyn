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

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.net.WebUtil;

/**
 * @author Steffen Pingel
 */
public class PollingSslProtocolSocketFactory implements SecureProtocolSocketFactory {

	private final IProgressMonitor monitor;

	private final SslProtocolSocketFactory factory;

	public PollingSslProtocolSocketFactory(IProgressMonitor monitor) {
		this.monitor = monitor;
		this.factory = SslProtocolSocketFactory.getInstance();
	}

	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException,
			UnknownHostException {
		return factory.createSocket(socket, host, port, autoClose);
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
		Socket socket = factory.getSocketFactory().createSocket();
		socket.bind(new InetSocketAddress(localAddress, localPort));
		WebUtil.connect(socket, new InetSocketAddress(host, port), timeout, monitor);
		return socket;
	}

}
