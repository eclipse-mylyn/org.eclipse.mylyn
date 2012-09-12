/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.commons.sdk.util.ManagedTestSuite;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.gerrit.tests.core.GerritConnectorTest;
import org.eclipse.mylyn.gerrit.tests.core.GerritSynchronizationTest;
import org.eclipse.mylyn.gerrit.tests.core.client.GerritClientTest;
import org.eclipse.mylyn.gerrit.tests.core.client.OpenIdAuthenticationTest;
import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.gerrit.tests.ui.GerritUrlHandlerTest;

/**
 * @author Steffen Pingel
 */
public class AllGerritTests {

	public static Test suite() {
		TestSuite suite = new ManagedTestSuite(AllGerritTests.class.getName());
		addTests(suite, TestConfiguration.getDefault());
		return suite;
	}

	public static Test suite(TestConfiguration configuration) {
		TestSuite suite = new TestSuite(AllGerritTests.class.getName());
		addTests(suite, configuration);
		return suite;
	}

	private static void addTests(TestSuite suite, TestConfiguration configuration) {
		if (!configuration.isLocalOnly()) {
			// network tests
			suite.addTestSuite(GerritUrlHandlerTest.class);
			suite.addTestSuite(OpenIdAuthenticationTest.class);
			if (configuration.isDefaultOnly()) {
				addTests(suite, GerritFixture.DEFAULT);
			} else {
				for (GerritFixture fixture : GerritFixture.ALL) {
					addTests(suite, fixture);
				}
			}
		}
	}

	private static void addTests(TestSuite suite, GerritFixture fixture) {
		fixture.createSuite(suite);
		fixture.add(GerritClientTest.class);
		fixture.add(GerritConnectorTest.class);
		fixture.add(GerritSynchronizationTest.class);
		fixture.done();
	}

}
