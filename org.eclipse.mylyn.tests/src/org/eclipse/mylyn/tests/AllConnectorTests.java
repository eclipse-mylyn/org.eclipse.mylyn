/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.bugzilla.tests.AllBugzillaTests;
import org.eclipse.mylyn.commons.sdk.util.ManagedTestSuite;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration.TestKind;
import org.eclipse.mylyn.gerrit.tests.AllGerritTests;
import org.eclipse.mylyn.hudson.tests.AllHudsonTests;
import org.eclipse.mylyn.tests.util.TestFixture;
import org.eclipse.mylyn.trac.tests.AllTracTests;

/**
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class AllConnectorTests {

	public static Test suite() {
		TestFixture.initializeTasksSettings();

		TestConfiguration configuration = new TestConfiguration(TestKind.INTEGRATION);

		TestSuite suite = new ManagedTestSuite(AllConnectorTests.class.getName());
		addTests(suite, configuration);
		return suite;
	}

	static void addTests(TestSuite suite, TestConfiguration configuration) {
		suite.addTest(AllBugzillaTests.suite(configuration));
		suite.addTest(AllTracTests.suite(configuration));
		suite.addTest(AllHudsonTests.suite(configuration));
		suite.addTest(AllGerritTests.suite(configuration));
	}

}
