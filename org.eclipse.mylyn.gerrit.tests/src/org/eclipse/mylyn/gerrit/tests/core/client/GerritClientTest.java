/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Sascha Scholz (SAP) - improvements
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.core.client;

import junit.framework.TestCase;

import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.gerrit.tests.support.GerritHarness;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gerrit.reviewdb.Account;

/**
 * @author Steffen Pingel
 * @author Sascha Scholz
 */
public class GerritClientTest extends TestCase {

	private GerritHarness harness;

	private GerritClient client;

	@Override
	@Before
	public void setUp() throws Exception {
		harness = GerritFixture.current().harness();
		client = harness.client();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		harness.dispose();
	}

	@Test
	public void testRefreshConfig() throws Exception {
		GerritConfiguration config = client.refreshConfig(null);
		assertNotNull(config);
		assertNotNull(config.getGerritConfig());
		assertNotNull(config.getProjects());
	}

	@Test
	public void testGetAccount() throws Exception {
		if (!GerritFixture.current().canAuthenticate()) {
			// skip
			return;
		}
		Account account = client.getAccount(null);
		assertEquals(harness.readCredentials().getShortUserName(), account.getUserName());
	}

	@Test
	public void testGetAccountAnonymous() throws Exception {
		client = harness.clientAnonymous();
		try {
			client.getAccount(null);
			fail("Expected GerritException");
		} catch (GerritException e) {
			assertEquals("Not Signed In", e.getMessage());
		}
	}

}
