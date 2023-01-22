/*******************************************************************************
 * Copyright (c) 2006, 2009 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.client;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.IProxyProvider;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.internal.trac.core.TracClientFactory;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.trac.tests.support.TestProxy;

/**
 * @author Steffen Pingel
 */
public class TracClientProxyTest extends TestCase {

	private TestProxy testProxy;

	private Proxy proxy;

	private int proxyPort;

	private Version version;

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
		connectProxy("http://mylyn.org/trac", "GET");
	}

	public void testConnectProxyXmlRpc() throws Exception {
		version = Version.XML_RPC;
		connectProxy("http://mylyn.org/trac", "POST");
	}

	public void testConnectProxySslWeb() throws Exception {
		version = Version.TRAC_0_9;
		connectProxy("https://mylyn.org/trac", "CONNECT");
	}

	public void testConnectProxySslXmlRpc() throws Exception {
		version = Version.XML_RPC;
		connectProxy("https://mylyn.org/trac", "CONNECT");
	}

	private void connectProxy(String url, String expectedMethod) throws Exception {
		testProxy.setResponse(TestProxy.NOT_FOUND);
		proxy = new Proxy(Type.HTTP, new InetSocketAddress("localhost", proxyPort));
		WebLocation location = new WebLocation(url, "", "", new IProxyProvider() {
			public Proxy getProxyForHost(String host, String proxyType) {
				return proxy;
			}
		});
		ITracClient client = TracClientFactory.createClient(location, version);
		try {
			client.validate(new NullProgressMonitor());
		} catch (TracException e) {
		}
		assertEquals(expectedMethod, testProxy.getRequest().getMethod());
	}

}
