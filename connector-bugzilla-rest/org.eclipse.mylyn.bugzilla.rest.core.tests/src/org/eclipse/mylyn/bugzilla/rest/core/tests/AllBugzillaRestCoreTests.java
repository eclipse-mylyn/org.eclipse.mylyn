/*******************************************************************************
 * Copyright (c) 2016 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.rest.core.tests;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.ManagedTestSuite;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllBugzillaRestCoreTests {
	public static Test suite() {
		if (CommonTestUtil.fixProxyConfiguration()) {
			CommonTestUtil.dumpSystemInfo(System.err);
		}

		TestSuite suite = new ManagedTestSuite(AllBugzillaRestCoreTests.class.getName());
		addTests(suite, TestConfiguration.getDefault());
		return suite;
	}

	public static Test suite(TestConfiguration configuration) {
		TestSuite suite = new TestSuite(AllBugzillaRestCoreTests.class.getName());
		addTests(suite, configuration);
		return suite;
	}

	public static void addTests(TestSuite suite, TestConfiguration configuration) {
		// Tests that only need to run once (i.e. no network io so doesn't matter which repository)
		suite.addTest(new JUnit4TestAdapter(RepositoryKeyTest.class));

		// network tests
		if (!configuration.isLocalOnly()) {
			suite.addTest(new JUnit4TestAdapter(BugzillaRestClientTest.class));
			suite.addTest(new JUnit4TestAdapter(BugzillaRestConfigurationTest.class));
			suite.addTest(new JUnit4TestAdapter(BugzillaRestConnectorTest.class));
		}
	}

}
