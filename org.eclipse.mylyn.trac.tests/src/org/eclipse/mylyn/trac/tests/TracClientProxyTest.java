/*******************************************************************************
 * Copyright (c) 2005, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.trac.tests;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;

public class TracClientProxyTest extends AbstractTracClientTest {

	private TestProxy testProxy;

	private Proxy proxy;

	private int proxyPort;

	public TracClientProxyTest() {
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		testProxy = new TestProxy();
		proxyPort = testProxy.startAndWait();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		testProxy.stop();
	}

	public void testConnectProxyWeb() throws Exception {
		version = Version.TRAC_0_9;
		connectProxy(TracTestConstants.TEST_TRAC_010_URL, "GET");
	}

	public void testConnectProxyXmlRpc() throws Exception {
		version = Version.XML_RPC;
		connectProxy(TracTestConstants.TEST_TRAC_010_URL, "POST");
	}

	public void testConnectProxySslWeb() throws Exception {
		version = Version.TRAC_0_9;
		connectProxy(TracTestConstants.TEST_TRAC_010_SSL_URL, "CONNECT");
	}

	public void testConnectProxySslXmlRpc() throws Exception {
		version = Version.XML_RPC;
		connectProxy(TracTestConstants.TEST_TRAC_010_SSL_URL, "CONNECT");
	}

	private void connectProxy(String url, String expectedMethod) throws Exception {
		testProxy.setResponse(TestProxy.NOT_FOUND);
		proxy = new Proxy(Type.HTTP, new InetSocketAddress("localhost", proxyPort));
		ITracClient client = connect(url, proxy);
		try {
			client.validate(callback);
		} catch (TracException e) {
		}

		assertEquals(expectedMethod, testProxy.getRequest().getMethod());
	}

}
