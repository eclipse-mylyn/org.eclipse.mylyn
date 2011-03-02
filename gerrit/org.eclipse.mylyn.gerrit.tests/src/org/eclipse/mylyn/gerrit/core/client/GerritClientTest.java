/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.core.client;

import junit.framework.TestCase;

import org.eclipse.mylyn.gerrit.core.support.GerritFixture;
import org.eclipse.mylyn.gerrit.core.support.GerritHarness;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;

import com.google.gerrit.common.data.GerritConfig;

/**
 * @author Steffen Pingel
 */
public class GerritClientTest extends TestCase {

	private GerritHarness harness;

	private GerritClient client;

	@Override
	protected void setUp() throws Exception {
		harness = GerritFixture.current().harness();
		client = harness.client();
	}

	@Override
	protected void tearDown() throws Exception {
		harness.dispose();
	}

	public void testRefreshConfig() throws Exception {
		GerritConfig config = client.refreshConfig(null);
		assertNotNull(config);
	}

}
