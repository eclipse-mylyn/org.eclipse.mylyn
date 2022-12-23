/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests;

import org.eclipse.mylyn.commons.sdk.util.ManagedSuite;
import org.eclipse.mylyn.commons.sdk.util.ManagedTestSuite;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Steffen Pingel
 */
public class AllLocalTests {

	public static Test suite() {
		TestConfiguration configuration = ManagedSuite.getTestConfiguration();
		if (configuration == null) {
			configuration = new TestConfiguration();
			configuration.setLocalOnly(true);
			ManagedSuite.setTestConfiguration(configuration);
		}

		TestSuite suite = new ManagedTestSuite(AllLocalTests.class.getName());
		AllNonConnectorTests.addTests(suite, configuration);
		addTests(suite, configuration);
		return suite;
	}

	static void addTests(TestSuite suite, TestConfiguration configuration) {
		//FIXME: AF: remove? we already did these tests during component build
//		suite.addTest(AllBugzillaTests.suite(configuration));
//		suite.addTest(AllTracTests.suite(configuration));
//		suite.addTest(AllHudsonTests.suite(configuration));
//		suite.addTest(AllGerritTests.suite(configuration));
		//FIXME: AF: enable tests back
		//https://github.com/eclipse-mylyn/.github/issues/3
//		suite.addTest(new JUnit4TestAdapter(AllBugzillaRestCoreTests.class));
	}

}
