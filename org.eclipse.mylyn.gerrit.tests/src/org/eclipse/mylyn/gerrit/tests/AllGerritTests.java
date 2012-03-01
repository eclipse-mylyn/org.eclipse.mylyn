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

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.ManagedTestSuite;
import org.eclipse.mylyn.gerrit.tests.core.GerritConnectorTest;
import org.eclipse.mylyn.gerrit.tests.core.client.GerritClientTest;
import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.gerrit.tests.ui.GerritUrlHandlerTest;

/**
 * @author Steffen Pingel
 */
public class AllGerritTests {

	public static Test suite() {
		TestSuite suite = new ManagedTestSuite(AllGerritTests.class.getName());
		addTests(false, CommonTestUtil.runHeartbeatTestsOnly(), suite);
		return suite;
	}

	public static Test suite(boolean defaultOnly) {
		TestSuite suite = new TestSuite(AllGerritTests.class.getName());
		addTests(false, defaultOnly, suite);
		return suite;
	}

	public static Test localSuite() {
		TestSuite suite = new TestSuite(AllGerritTests.class.getName());
		addTests(true, CommonTestUtil.runHeartbeatTestsOnly(), suite);
		return suite;
	}

	private static void addTests(boolean localOnly, boolean defaultOnly, TestSuite suite) {
		suite.addTestSuite(GerritUrlHandlerTest.class);
		if (!localOnly) {
			// network tests
			for (GerritFixture fixture : GerritFixture.ALL) {
				fixture.createSuite(suite);
				fixture.add(GerritClientTest.class);
				fixture.add(GerritConnectorTest.class);
				fixture.done();
			}
		}
	}

}
