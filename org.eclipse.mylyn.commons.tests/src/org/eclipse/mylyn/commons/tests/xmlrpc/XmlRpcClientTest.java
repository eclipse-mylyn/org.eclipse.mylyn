/*******************************************************************************
 * Copyright (c) 2010 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.xmlrpc;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.internal.commons.xmlrpc.CommonXmlRpcClient;
import org.eclipse.mylyn.internal.commons.xmlrpc.XmlRpcNoSuchMethodException;

/**
 * @author Steffen Pingel
 */
public class XmlRpcClientTest extends TestCase {

	private CommonXmlRpcClient client;

	private int port;

	@Override
	protected void setUp() throws Exception {
		port = XmlRpcTestServer.start();
		client = new CommonXmlRpcClient(new WebLocation("http://localhost:" + port + "/xmlrpc"));
	}

	@Override
	protected void tearDown() throws Exception {
		//webServer.shutdown();
	}

	public void testCall() throws Exception {
		int i = (Integer) client.call(new NullProgressMonitor(), "Test.identity", 5);
		assertEquals(5, i);
	}

	public void testNoSuchMethod() throws Exception {
		try {
			Object response = client.call(new NullProgressMonitor(), "Test.noSuchMethod", 5);
			fail("Expected XmlRpcNoSuchMethodExecption, got " + response);
		} catch (XmlRpcNoSuchMethodException e) {
			// expected
		}
	}

}
