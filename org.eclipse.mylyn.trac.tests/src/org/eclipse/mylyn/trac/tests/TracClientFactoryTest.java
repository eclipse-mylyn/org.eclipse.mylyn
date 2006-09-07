/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.trac.tests;

import junit.framework.TestCase;

import org.eclipse.mylar.context.tests.support.MylarTestUtils;
import org.eclipse.mylar.context.tests.support.MylarTestUtils.Credentials;
import org.eclipse.mylar.context.tests.support.MylarTestUtils.PrivilegeLevel;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.Trac09Client;
import org.eclipse.mylar.internal.trac.core.TracClientFactory;
import org.eclipse.mylar.internal.trac.core.TracException;
import org.eclipse.mylar.internal.trac.core.TracLoginException;
import org.eclipse.mylar.internal.trac.core.TracXmlRpcClient;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;

/**
 * @author Steffen Pingel
 */
public class TracClientFactoryTest extends TestCase {

	public void testCreateClient() throws Exception {
		ITracClient client = TracClientFactory.createClient(Constants.TEST_TRAC_010_URL, Version.TRAC_0_9, "user",
				"password");
		assertTrue(client instanceof Trac09Client);
		client = TracClientFactory.createClient(Constants.TEST_TRAC_010_SSL_URL, Version.TRAC_0_9, "user", "password");
		assertTrue(client instanceof Trac09Client);

		client = TracClientFactory.createClient(Constants.TEST_TRAC_010_URL, Version.XML_RPC, "user", "password");
		assertTrue(client instanceof TracXmlRpcClient);
		client = TracClientFactory.createClient(Constants.TEST_TRAC_010_SSL_URL, Version.XML_RPC, "user", "password");
		assertTrue(client instanceof TracXmlRpcClient);
	}

	public void testCreateClientNull() throws Exception {
		try {
			TracClientFactory.createClient(Constants.TEST_TRAC_010_URL, null, "user", "password");
			fail("Expected Exception");
		} catch (Exception e) {
		}
	}

	public void testProbeClient096() throws Exception {
		probeClient(Constants.TEST_TRAC_096_URL, false);
	}

	public void testProbeClient010() throws Exception {
		probeClient(Constants.TEST_TRAC_010_URL, true);
	}

	public void testProbeClient010DigestAuth() throws Exception {
		probeClient(Constants.TEST_TRAC_010_DIGEST_AUTH_URL, true);
	}

	protected void probeClient(String url, boolean xmlrpcInstalled) throws Exception {
		Credentials credentials = MylarTestUtils.readCredentials(PrivilegeLevel.USER);
		Version version = TracClientFactory.probeClient(url, credentials.username, credentials.password);
		if (xmlrpcInstalled) {
			assertEquals(Version.XML_RPC, version);
		} else {
			assertEquals(Version.TRAC_0_9, version);
		}

		version = TracClientFactory.probeClient(url, "", "");
		assertEquals(Version.TRAC_0_9, version);

		try {
			version = TracClientFactory.probeClient(url, "invaliduser", "password");
			fail("Expected TracLoginException, got " + version);
		} catch (TracLoginException e) {
		}

		try {
			version = TracClientFactory.probeClient(url + "/nonexistant", "", "");
			fail("Expected TracException, got " + version);
		} catch (TracException e) {
		}
	}

}
