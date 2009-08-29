/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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

import org.eclipse.mylyn.jira.tests.AllJiraTests;
import org.eclipse.mylyn.trac.tests.AllTracTests;

/**
 * @author Shawn Minto
 */
public class AllConnectorTests {

	public static Test suite() {
		// the order of these tests might still matter, but shouldn't
		TestSuite suite = new TestSuite("All Connector Tests for org.eclipse.mylyn.tests");
		// FIXME re-enable suite.addTest(AllBugzillaTests.suite());
		suite.addTest(AllJiraTests.suite());
		suite.addTest(AllTracTests.suite());
		// FIXME re-enable suite.addTest(AllXPlannerTests.suite());
		return suite;
	}
}
