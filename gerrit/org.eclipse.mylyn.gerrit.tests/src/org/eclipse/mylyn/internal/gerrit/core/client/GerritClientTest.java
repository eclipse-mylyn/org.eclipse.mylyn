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

package org.eclipse.mylyn.internal.gerrit.core.client;

import static org.junit.Assert.assertNotNull;

import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.support.GerritFixture;
import org.eclipse.mylyn.internal.gerrit.core.support.GerritHarness;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gerrit.common.data.GerritConfig;

/**
 * @author Steffen Pingel
 */
@Ignore("needs to be checked")
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

}
