/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.bugzilla.tests.core.BugzillaClientTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaConfigurationTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaRepositoryConnectorStandaloneTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaTaskHistoryTest;
import org.eclipse.mylyn.bugzilla.tests.core.BugzillaVersionTest;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;

/**
 * @author Steffen Pingel
 */
public class AllBugzillaHeadlessStandaloneTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Headless Standalone Tests for org.eclipse.mylyn.bugzilla.tests");
		suite.addTestSuite(BugzillaConfigurationTest.class);
		suite.addTestSuite(BugzillaVersionTest.class);
		for (BugzillaFixture fixture : BugzillaFixture.ALL) {
			TestSuite fixtureSuite = fixture.createSuite();
			fixture.add(fixtureSuite, BugzillaClientTest.class);
			fixture.add(fixtureSuite, BugzillaTaskHistoryTest.class);
			fixture.add(fixtureSuite, BugzillaRepositoryConnectorStandaloneTest.class);
			suite.addTest(fixtureSuite);
		}
		return suite;
	}

}
