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

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.trac.core.TracClientFactory;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.client.TracLoginException;
import org.eclipse.mylyn.internal.trac.core.client.TracWebClient;
import org.eclipse.mylyn.internal.trac.core.client.TracXmlRpcClient;
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
		WebLocation location = new WebLocation(fixture.getRepositoryUrl(), "user", "password");
		ITracClient client = TracClientFactory.createClient(location, null);
		assertEquals(Version.XML_RPC, client.getAccessMode());
	}

	public void testProbeClient() throws Exception {
		String url = fixture.getRepositoryUrl();

		UserCredentials credentials = CommonTestUtil.getCredentials(PrivilegeLevel.USER);
		WebLocation location = new WebLocation(url, credentials.getUserName(), credentials.getPassword());
		Version version = TracClientFactory.probeClient(location);
		if (fixture.isXmlRpcEnabled()) {
			// assertion is only meaningful for XML-RPC since web fixtures will also probe XML-RPC if available
			assertEquals(Version.XML_RPC, version);
		}
	}

	public void testProbeClientNoCredentials() throws Exception {
		String url = fixture.getRepositoryUrl();
		WebLocation location = new WebLocation(url, "", "");
		try {
			Version version = TracClientFactory.probeClient(location);
			if (fixture.requiresAuthentication()) {
				fail("Expected TracLoginException");
			}
			assertEquals(Version.TRAC_0_9, version);
		} catch (TracLoginException e) {
			if (fixture.requiresAuthentication()) {
				// the remainder of the 
				return;
			}
			throw e;
		}
	}

	public void testProbeClientInvalidCredentials() throws Exception {
		try {
			WebLocation location = new WebLocation(fixture.getRepositoryUrl(), "invaliduser", "password");
			Version version = TracClientFactory.probeClient(location);
			fail("Expected TracLoginException, got " + version);
		} catch (TracLoginException e) {
		}
	}

	public void testProbeClientInvalidLocation() throws Exception {
		try {
			WebLocation location = new WebLocation(fixture.getRepositoryUrl() + "/nonexistant", "", "");
			Version version = TracClientFactory.probeClient(location);
			fail("Expected TracException, got " + version);
		} catch (TracException e) {
		}
	}

}
