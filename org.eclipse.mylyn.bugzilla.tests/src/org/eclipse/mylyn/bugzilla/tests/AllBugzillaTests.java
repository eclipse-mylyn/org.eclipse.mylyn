/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.bugzilla.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.bugzilla.tests.headless.BugzillaQueryTest;
import org.eclipse.mylyn.bugzilla.tests.headless.BugzillaTaskHistoryTest;

/**
 * @author Mik Kersten
 */
public class AllBugzillaTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.bugzilla.tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(RepositoryTaskHandleTest.class);
		suite.addTestSuite(TaskListNotificationManagerTest.class);
		suite.addTestSuite(BugzillaTaskTest.class);
		suite.addTestSuite(BugzillaTaskDataHandlerTest.class);
		suite.addTestSuite(TaskListStandaloneTest.class);
		suite.addTestSuite(BugzillaTaskListManagerTest.class);
		suite.addTestSuite(TaskEditorTest.class);
		suite.addTestSuite(BugzillaQueryTest.class);
		suite.addTestSuite(RepositoryEditorWizardTest.class);
		suite.addTestSuite(RepositoryReportFactoryTest.class);
		suite.addTestSuite(BugzillaConfigurationTest.class);
		suite.addTestSuite(BugzillaTaskHyperlinkDetectorTest.class);
		suite.addTestSuite(BugzillaSearchEngineTest.class);
		// suite.addTestSuite(Bugzilla220ParserTest.class);
		suite.addTestSuite(BugzillaRepositoryConnectorTest.class);
		suite.addTestSuite(EncodingTest.class);
		// suite.addTestSuite(NewBugWizardTest.class);
		// suite.addTestSuite(RegularExpressionMatchTest.class);
		// suite.addTestSuite(BugzillaNewBugParserTestCDT.class);
		// suite.addTestSuite(BugzillaNewBugParserTestEquinox.class);
		// suite.addTestSuite(BugzillaNewBugParserTestGMT.class);
		// suite.addTestSuite(BugzillaNewBugParserTestPlatform.class);
		// suite.addTestSuite(BugzillaNewBugParserTestVE.class);
		// suite.addTestSuite(BugzillaParserTestNoBug.class);
		suite.addTestSuite(BugzillaProductParserTest.class);
		// TODO: enable
		// suite.addTest(new TestSuite(BugzillaParserTest.class));
		suite.addTestSuite(BugzillaSearchDialogTest.class);
		suite.addTestSuite(DuplicateDetetionTest.class);
		suite.addTestSuite(BugzillaTaskHistoryTest.class);
		// $JUnit-END$
		return suite;
	}
}
