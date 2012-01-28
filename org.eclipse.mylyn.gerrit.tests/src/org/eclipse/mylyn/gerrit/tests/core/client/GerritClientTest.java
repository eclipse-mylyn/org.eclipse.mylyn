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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.gerrit.tests.support.GerritHarness;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gerrit.reviewdb.Account;

/**
 * @author Steffen Pingel
 * @author Sascha Scholz
 */
@Ignore("Credentials are required, but not configured on Hudson")
public class GerritClientTest {

	private GerritHarness harness;

	private GerritClient client;

	@Before
	public void setUp() throws Exception {
		harness = GerritFixture.current().harness();
		client = harness.client();
	}

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
		Account account = client.getAccount(null);
		assertEquals(harness.readCredentials().username, account.getUserName());
	}
}
