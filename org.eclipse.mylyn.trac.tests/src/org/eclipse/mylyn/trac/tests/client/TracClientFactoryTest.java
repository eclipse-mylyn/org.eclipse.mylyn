/*******************************************************************************
* Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.client;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.context.tests.support.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.trac.core.TracClientFactory;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.client.TracLoginException;
import org.eclipse.mylyn.internal.trac.core.client.TracWebClient;
import org.eclipse.mylyn.internal.trac.core.client.TracXmlRpcClient;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.trac.tests.support.TracTestConstants;

/**
 * @author Steffen Pingel
 */
public class TracClientFactoryTest extends TestCase {

	public void testCreateClient() throws Exception {
		WebLocation location = new WebLocation(TracTestConstants.TEST_TRAC_010_URL, "user", "password");
		ITracClient client = TracClientFactory.createClient(location, Version.TRAC_0_9);
		assertTrue(client instanceof TracWebClient);

		location = new WebLocation(TracTestConstants.TEST_TRAC_010_SSL_URL, "user", "password");
		client = TracClientFactory.createClient(location, Version.TRAC_0_9);
		assertTrue(client instanceof TracWebClient);

		location = new WebLocation(TracTestConstants.TEST_TRAC_010_URL, "user", "password");
		client = TracClientFactory.createClient(location, Version.XML_RPC);
		assertTrue(client instanceof TracXmlRpcClient);

		location = new WebLocation(TracTestConstants.TEST_TRAC_010_SSL_URL, "user", "password");
		client = TracClientFactory.createClient(location, Version.XML_RPC);
		assertTrue(client instanceof TracXmlRpcClient);
	}

	public void testCreateClientNull() throws Exception {
		try {
			WebLocation location = new WebLocation(TracTestConstants.TEST_TRAC_010_URL, "user", "password");
			TracClientFactory.createClient(location, null);
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
		WebLocation location = new WebLocation(url, credentials.username, credentials.password);
		Version version = TracClientFactory.probeClient(location);
		if (xmlrpcInstalled) {
			assertEquals(Version.XML_RPC, version);
		} else {
			assertEquals(Version.TRAC_0_9, version);
		}

		location = new WebLocation(url, "", "");
		version = TracClientFactory.probeClient(location);
		assertEquals(Version.TRAC_0_9, version);

		try {
			location = new WebLocation(url, "invaliduser", "password");
			version = TracClientFactory.probeClient(location);
			fail("Expected TracLoginException, got " + version);
		} catch (TracLoginException e) {
		}

		try {
			location = new WebLocation(url + "/nonexistant", "", "");
			version = TracClientFactory.probeClient(location);
			fail("Expected TracException, got " + version);
		} catch (TracException e) {
		}
	}

}
