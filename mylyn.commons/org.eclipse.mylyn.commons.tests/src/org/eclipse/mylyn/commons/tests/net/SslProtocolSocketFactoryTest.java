/*******************************************************************************
 * Copyright (c) 2004, 2024 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.net;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.eclipse.mylyn.commons.sdk.util.MockServer;
import org.eclipse.mylyn.internal.commons.net.PollingSslProtocolSocketFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class SslProtocolSocketFactoryTest {

	private MockServer server;

	private InetSocketAddress proxyAddress;

	@BeforeEach
	void setUp() throws Exception {
		server = new MockServer();
		int proxyPort = server.startAndWait();
		assert proxyPort > 0;
		proxyAddress = new InetSocketAddress("localhost", proxyPort);
	}

	@AfterEach
	void tearDown() throws Exception {
		server.stop();
	}

	@Test
	public void testTrustAllSslProtocolSocketFactory() throws Exception {
		SecureProtocolSocketFactory factory = new PollingSslProtocolSocketFactory();

		try (Socket s = factory.createSocket(proxyAddress.getHostName(), proxyAddress.getPort())) {
			assertNotNull(s);
			assertTrue(s.isConnected());
		}

		try (Socket socket = new Socket()) {
			InetAddress anyHost = socket.getLocalAddress();

			try (Socket s = factory.createSocket(proxyAddress.getHostName(), proxyAddress.getPort(), anyHost, 0)) {
				assertNotNull(s);
				assertTrue(s.isConnected());
			}

			HttpConnectionParams params = new HttpConnectionParams();
			try (Socket s = factory.createSocket(proxyAddress.getHostName(), proxyAddress.getPort(), anyHost, 0, params)) {
				assertNotNull(s);
				assertTrue(s.isConnected());
			}

			params.setConnectionTimeout(1000);
			try (Socket s = factory.createSocket(proxyAddress.getHostName(), proxyAddress.getPort(), anyHost, 0, params)) {
				assertNotNull(s);
				assertTrue(s.isConnected());
			}
		}
	}
}