/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.hudson.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.ManagedTestSuite;
import org.eclipse.mylyn.hudson.tests.client.HudsonClientTest;
import org.eclipse.mylyn.hudson.tests.client.HudsonValidationTest;
import org.eclipse.mylyn.hudson.tests.core.HudsonServerBehaviourTest;
import org.eclipse.mylyn.hudson.tests.integration.HudsonIntegrationTest;
import org.eclipse.mylyn.hudson.tests.support.HudsonFixture;

/**
 * @author Steffen Pingel
 */
public class AllHudsonTests {

	public static Test suite() {
		TestSuite suite = new ManagedTestSuite(AllHudsonTests.class.getName());
		addTests(CommonTestUtil.runHeartbeatTestsOnly(), suite);
		return suite;
	}

	public static Test suite(boolean defaultOnly) {
		TestSuite suite = new TestSuite(AllHudsonTests.class.getName());
		addTests(defaultOnly, suite);
		return suite;
	}

	private static void addTests(boolean defaultOnly, TestSuite suite) {
		suite.addTestSuite(HudsonServerBehaviourTest.class);
		// network tests
		suite.addTestSuite(HudsonValidationTest.class);
		for (HudsonFixture fixture : HudsonFixture.ALL) {
			fixture.createSuite(suite);
			fixture.add(HudsonClientTest.class);
			fixture.add(HudsonIntegrationTest.class);
			fixture.done();
		}
		for (HudsonFixture fixture : HudsonFixture.MISC) {
			fixture.createSuite(suite);
			fixture.add(HudsonClientTest.class);
			fixture.done();
		}
	}

}
