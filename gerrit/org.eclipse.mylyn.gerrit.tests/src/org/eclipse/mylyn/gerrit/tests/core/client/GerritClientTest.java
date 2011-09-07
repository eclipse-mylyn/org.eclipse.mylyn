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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.gerrit.tests.support.GerritHarness;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritLoginException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gerrit.common.data.GerritConfig;
import com.google.gerrit.reviewdb.Account;

/**
 * @author Steffen Pingel
 */
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
		GerritConfig config = client.refreshConfig(null);
		assertNotNull(config);
	}

	@Test
	public void testGetAccount() throws Exception {
		try {
			Account account = client.getAccount(null);
			fail("Expected GerritLoginException, got: " + account.getUserName()); //$NON-NLS-1$
		} catch (GerritLoginException expected) {
		}
	}

}
