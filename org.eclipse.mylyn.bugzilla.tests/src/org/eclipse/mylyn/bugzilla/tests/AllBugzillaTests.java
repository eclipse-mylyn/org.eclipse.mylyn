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

package org.eclipse.mylyn.bugzilla.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.bugzilla.tests.core.BugzillaRepositoryConnectorStandaloneTest;
import org.eclipse.mylyn.bugzilla.tests.ui.BugzillaRepositorySettingsPageTest;
import org.eclipse.mylyn.bugzilla.tests.ui.BugzillaSearchPageTest;
import org.eclipse.mylyn.bugzilla.tests.ui.BugzillaTaskHyperlinkDetectorTest;
import org.eclipse.mylyn.bugzilla.tests.ui.TaskEditorTest;

/**
 * @author Mik Kersten
 */
public class AllBugzillaTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for org.eclipse.mylyn.bugzilla.tests");
		suite.addTest(AllBugzillaHeadlessStandaloneTests.suite());
		suite.addTestSuite(BugzillaTaskCompletionTest.class);
		suite.addTestSuite(BugzillaTaskDataHandlerTest.class);
		suite.addTestSuite(TaskEditorTest.class);
		suite.addTestSuite(BugzillaRepositoryConnectorStandaloneTest.class);
		suite.addTestSuite(BugzillaRepositorySettingsPageTest.class);
		suite.addTestSuite(RepositoryReportFactoryTest.class);
		suite.addTestSuite(BugzillaTaskHyperlinkDetectorTest.class);
		suite.addTestSuite(BugzillaSearchTest.class);
		suite.addTestSuite(BugzillaRepositoryConnectorTest.class);
		suite.addTestSuite(BugzillaAttachmentHandlerTest.class);
		suite.addTestSuite(EncodingTest.class);
		suite.addTestSuite(BugzillaSearchPageTest.class);
		suite.addTestSuite(BugzillaRepository32Test.class);
		suite.addTestSuite(BugzillaDateTimeTests.class);
		return suite;
	}

}
