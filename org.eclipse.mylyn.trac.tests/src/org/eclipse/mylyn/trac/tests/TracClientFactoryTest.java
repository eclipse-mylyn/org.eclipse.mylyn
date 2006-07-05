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
		ITracClient client = TracClientFactory.createClient(Constants.TEST_REPOSITORY1_URL, Version.TRAC_0_9, "user",
				"password");
		assertTrue(client instanceof Trac09Client);
		client = TracClientFactory.createClient(Constants.TEST_REPOSITORY1_SSL_URL, Version.TRAC_0_9, "user",
				"password");
		assertTrue(client instanceof Trac09Client);

		client = TracClientFactory.createClient(Constants.TEST_REPOSITORY1_URL, Version.XML_RPC, "user", "password");
		assertTrue(client instanceof TracXmlRpcClient);
		client = TracClientFactory
				.createClient(Constants.TEST_REPOSITORY1_SSL_URL, Version.XML_RPC, "user", "password");
		assertTrue(client instanceof TracXmlRpcClient);
	}

	public void testCreateClientNull() throws Exception {
		try {
			TracClientFactory.createClient(Constants.TEST_REPOSITORY1_SSL_URL, null, "user", "password");
			fail("Expected Exception");
		} catch (Exception e) {
		}
	}

	public void testProbeClient() throws Exception {
		Version version = TracClientFactory.probeClient(Constants.TEST_REPOSITORY1_URL,
				Constants.TEST_REPOSITORY1_USERNAME, Constants.TEST_REPOSITORY1_PASSWORD);
		assertEquals(Version.XML_RPC, version);

		version = TracClientFactory.probeClient(Constants.TEST_REPOSITORY1_URL, "", "");
		assertEquals(Version.TRAC_0_9, version);

		try {
			version = TracClientFactory.probeClient(Constants.TEST_REPOSITORY1_URL, "invaliduser", "password");
			fail("Expected TracLoginException, got " + version);
		} catch (TracLoginException e) {
		}

		try {
			version = TracClientFactory.probeClient(Constants.TEST_REPOSITORY1_URL + "/nonexistant", "", "");
			fail("Expected TracException, got " + version);
		} catch (TracException e) {
		}
	}

}
