/*******************************************************************************
 * Copyright (c) 2010, 2024 Steffen Pingel and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Steffen Pingel - initial API and implementation
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.xmlrpc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.internal.commons.xmlrpc.CommonXmlRpcClient;
import org.eclipse.mylyn.internal.commons.xmlrpc.XmlRpcNoSuchMethodException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class XmlRpcClientTest {

	private CommonXmlRpcClient client;

	private int port;

	@BeforeEach
	void setUp() throws Exception {
		port = XmlRpcTestServer.start();
		client = new CommonXmlRpcClient(new WebLocation("http://localhost:" + port + "/xmlrpc"));
	}

	@AfterEach
	void tearDown() throws Exception {
		//webServer.shutdown();
	}

	@Test
	public void testCall() throws Exception {
		int i = (Integer) client.call(new NullProgressMonitor(), "Test.identity", 5);
		assertEquals(5, i);
	}

	@Test
	public void testNoSuchMethod() throws Exception {
		AtomicReference<Object> response = new AtomicReference<>();
		assertThrows(XmlRpcNoSuchMethodException.class,
				() -> response.set(client.call(new NullProgressMonitor(), "Test.noSuchMethod", 5)),
				() -> "Expected XmlRpcNoSuchMethodException, got " + response.get());
	}

}
