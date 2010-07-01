/*******************************************************************************
 * Copyright (c) 2006, 2009 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.client;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.internal.trac.core.TracClientFactory;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.client.TracLoginException;
import org.eclipse.mylyn.internal.trac.core.client.TracWebClient;
import org.eclipse.mylyn.internal.trac.core.client.TracXmlRpcClient;
import org.eclipse.mylyn.tests.util.TestUtil;
import org.eclipse.mylyn.tests.util.TestUtil.Credentials;
import org.eclipse.mylyn.tests.util.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.trac.tests.support.TracFixture;

/**
 * @author Steffen Pingel
 */
public class TracClientFactoryTest extends TestCase {

	private TracFixture fixture;

	@Override
	protected void setUp() throws Exception {
		fixture = TracFixture.current();
	}

	public void testCreateClient() throws Exception {
		WebLocation location = new WebLocation(fixture.getRepositoryUrl(), "user", "password");
		ITracClient client = TracClientFactory.createClient(location, fixture.getAccessMode());
		if (fixture.getAccessMode() == Version.TRAC_0_9) {
			assertTrue(client instanceof TracWebClient);
		} else {
			assertTrue(client instanceof TracXmlRpcClient);
		}
	}

	public void testCreateClientNull() throws Exception {
		try {
			WebLocation location = new WebLocation(fixture.getRepositoryUrl(), "user", "password");
			TracClientFactory.createClient(location, null);
			fail("Expected Exception");
		} catch (Exception e) {
		}
	}

	public void testProbeClient() throws Exception {
		String url = fixture.getRepositoryUrl();
		boolean xmlrpcInstalled = (fixture.getAccessMode() == Version.XML_RPC);

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
