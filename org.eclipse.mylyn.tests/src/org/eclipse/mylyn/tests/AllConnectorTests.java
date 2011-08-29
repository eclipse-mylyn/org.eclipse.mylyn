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
import org.eclipse.mylyn.hudson.tests.AllHudsonTests;
import org.eclipse.mylyn.tests.util.TestFixture;
import org.eclipse.mylyn.trac.tests.AllTracTests;

/**
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class AllConnectorTests {

	public static Test suite() {
		return suite(false);
	}

	public static Test suite(boolean defaultOnly) {
		TestFixture.initializeTasksSettings();

		TestSuite suite = new ManagedTestSuite(AllConnectorTests.class.getName());
		addTests(suite, defaultOnly);
		return suite;
	}

	static void addTests(TestSuite suite, boolean defaultOnly) {
		suite.addTest(AllBugzillaTests.suite(defaultOnly));
		suite.addTest(AllTracTests.suite(defaultOnly));
		suite.addTest(AllHudsonTests.suite(defaultOnly));
		//suite.addTest(AllGerritTests.suite(defaultOnly));
	}

}
