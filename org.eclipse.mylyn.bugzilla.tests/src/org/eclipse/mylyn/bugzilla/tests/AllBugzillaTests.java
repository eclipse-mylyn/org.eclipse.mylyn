/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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

import org.eclipse.mylyn.bugzilla.tests.headless.BugzillaQueryTest;
import org.eclipse.mylyn.bugzilla.tests.headless.BugzillaTaskHistoryTest;

/**
 * @author Mik Kersten
 */
public class AllBugzillaTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test suite for org.eclipse.mylyn.bugzilla.tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(RepositoryTaskHandleTest.class);
		suite.addTestSuite(BugzillaTaskCompletionTest.class);
		suite.addTestSuite(BugzillaTaskDataHandlerTest.class);
		suite.addTestSuite(TaskListStandaloneTest.class);
		suite.addTestSuite(BugzillaTaskListTest.class);
		suite.addTestSuite(TaskEditorTest.class);
		suite.addTestSuite(BugzillaQueryTest.class);
		suite.addTestSuite(RepositoryEditorWizardTest.class);
		suite.addTestSuite(RepositoryReportFactoryTest.class);
		suite.addTestSuite(BugzillaConfigurationTest.class);
		suite.addTestSuite(BugzillaTaskHyperlinkDetectorTest.class);
		suite.addTestSuite(BugzillaSearchTest.class);
		suite.addTestSuite(BugzillaRepositoryConnectorTest.class);
		suite.addTestSuite(EncodingTest.class);
		suite.addTestSuite(BugzillaProductParserTest.class);
		suite.addTestSuite(BugzillaSearchDialogTest.class);
		suite.addTestSuite(BugzillaTaskHistoryTest.class);
		suite.addTestSuite(BugzillaRepository32Test.class);
		// $JUnit-END$
		return suite;
	}
}
