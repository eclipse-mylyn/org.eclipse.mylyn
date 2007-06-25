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

package org.eclipse.mylyn.trac.tests;

import java.net.Proxy;

import junit.framework.TestCase;

import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.context.tests.support.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.TracWebClient;
import org.eclipse.mylyn.internal.trac.core.TracClientFactory;
import org.eclipse.mylyn.internal.trac.core.TracException;
import org.eclipse.mylyn.internal.trac.core.TracLoginException;
import org.eclipse.mylyn.internal.trac.core.TracXmlRpcClient;
import org.eclipse.mylyn.internal.trac.core.ITracClient.Version;

/**
 * @author Steffen Pingel
 */
public class TracClientFactoryTest extends TestCase {

	public void testCreateClient() throws Exception {
		ITracClient client = TracClientFactory.createClient(TracTestConstants.TEST_TRAC_010_URL, Version.TRAC_0_9,
				"user", "password", Proxy.NO_PROXY);
		assertTrue(client instanceof TracWebClient);
		client = TracClientFactory.createClient(TracTestConstants.TEST_TRAC_010_SSL_URL, Version.TRAC_0_9, "user",
				"password", Proxy.NO_PROXY);
		assertTrue(client instanceof TracWebClient);

		client = TracClientFactory.createClient(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC, "user",
				"password", Proxy.NO_PROXY);
		assertTrue(client instanceof TracXmlRpcClient);
		client = TracClientFactory.createClient(TracTestConstants.TEST_TRAC_010_SSL_URL, Version.XML_RPC, "user",
				"password", Proxy.NO_PROXY);
		assertTrue(client instanceof TracXmlRpcClient);
	}

	public void testCreateClientNull() throws Exception {
		try {
			TracClientFactory.createClient(TracTestConstants.TEST_TRAC_010_URL, null, "user", "password",
					Proxy.NO_PROXY);
			fail("Expected Exception");
		} catch (Exception e) {
		}
	}

	public void testProbeClient096() throws Exception {
		probeClient(TracTestConstants.TEST_TRAC_096_URL, false);
	}

	public void testProbeClient010() throws Exception {
		probeClient(TracTestConstants.TEST_TRAC_010_URL, true);
	}

	public void testProbeClient010DigestAuth() throws Exception {
		probeClient(TracTestConstants.TEST_TRAC_010_DIGEST_AUTH_URL, true);
	}

	protected void probeClient(String url, boolean xmlrpcInstalled) throws Exception {
		Credentials credentials = TestUtil.readCredentials(PrivilegeLevel.USER);
		Version version = TracClientFactory.probeClient(url, credentials.username, credentials.password, Proxy.NO_PROXY);
		if (xmlrpcInstalled) {
			assertEquals(Version.XML_RPC, version);
		} else {
			assertEquals(Version.TRAC_0_9, version);
		}

		version = TracClientFactory.probeClient(url, "", "", Proxy.NO_PROXY);
		assertEquals(Version.TRAC_0_9, version);

		try {
			version = TracClientFactory.probeClient(url, "invaliduser", "password", Proxy.NO_PROXY);
			fail("Expected TracLoginException, got " + version);
		} catch (TracLoginException e) {
		}

		try {
			version = TracClientFactory.probeClient(url + "/nonexistant", "", "", Proxy.NO_PROXY);
			fail("Expected TracException, got " + version);
		} catch (TracException e) {
		}
	}

}
