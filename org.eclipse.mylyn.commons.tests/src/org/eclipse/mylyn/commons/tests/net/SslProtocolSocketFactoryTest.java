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

package org.eclipse.mylyn.commons.tests.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import junit.framework.TestCase;

import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.eclipse.mylyn.commons.tests.support.TestProxy;
import org.eclipse.mylyn.internal.commons.net.SslProtocolSocketFactory;

/**
 * @author Steffen Pingel
 */
public class SslProtocolSocketFactoryTest extends TestCase {

	private TestProxy testProxy;

	private InetSocketAddress proxyAddress;

	@Override
	protected void setUp() throws Exception {
		testProxy = new TestProxy();
		int proxyPort = testProxy.startAndWait();
		assert proxyPort > 0;
		proxyAddress = new InetSocketAddress("localhost", proxyPort);
	}

	@Override
	protected void tearDown() throws Exception {
		testProxy.stop();
	}

	public void testTrustAllSslProtocolSocketFactory() throws Exception {
		SslProtocolSocketFactory factory = SslProtocolSocketFactory.getInstance();
		Socket s;

		s = factory.createSocket(proxyAddress.getHostName(), proxyAddress.getPort());
		assertNotNull(s);
		assertTrue(s.isConnected());
		s.close();

		InetAddress anyHost = new Socket().getLocalAddress();

		s = factory.createSocket(proxyAddress.getHostName(), proxyAddress.getPort(), anyHost, 0);
		assertNotNull(s);
		assertTrue(s.isConnected());
		s.close();

		HttpConnectionParams params = new HttpConnectionParams();
		s = factory.createSocket(proxyAddress.getHostName(), proxyAddress.getPort(), anyHost, 0, params);
		assertNotNull(s);
		assertTrue(s.isConnected());
		s.close();

		params.setConnectionTimeout(1000);
		s = factory.createSocket(proxyAddress.getHostName(), proxyAddress.getPort(), anyHost, 0, params);
		assertNotNull(s);
		assertTrue(s.isConnected());
		s.close();
	}

}
