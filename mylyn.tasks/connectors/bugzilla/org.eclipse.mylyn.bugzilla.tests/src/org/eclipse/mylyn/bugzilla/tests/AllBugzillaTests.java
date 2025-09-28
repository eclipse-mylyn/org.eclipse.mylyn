/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import org.eclipse.mylyn.bugzilla.tests.core.BugzillaXmlRpcClientTest;
import org.eclipse.mylyn.bugzilla.tests.ui.BugzillaHyperlinkDetectorTest;
import org.eclipse.mylyn.bugzilla.tests.ui.BugzillaRepositorySettingsPageTest;
import org.eclipse.mylyn.bugzilla.tests.ui.BugzillaSearchPageTest;
import org.eclipse.mylyn.bugzilla.tests.ui.BugzillaTaskEditorTest;
import org.eclipse.mylyn.bugzilla.tests.ui.BugzillaTaskHyperlinkDetectorTest;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.junit.platform.suite.api.BeforeSuite;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 * @author Frank Becker
 */
@Suite
@SelectClasses({ BugzillaTaskHyperlinkDetectorTest.class, BugzillaHyperlinkDetectorTest.class,
	AllBugzillaHeadlessStandaloneTests.class,
	// not local
	BugzillaTaskEditorTest.class, BugzillaSearchPageTest.class, BugzillaRepositorySettingsPageTest.class,
	// needs fixture
	RepositoryReportFactoryTest.class, BugzillaTaskDataHandlerTest.class, BugzillaSearchTest.class,
	EncodingTest.class, BugzillaXmlRpcClientTest.class, BugzillaRepositoryConnectorTest.class,
	BugzillaAttachmentHandlerTest.class })

public class AllBugzillaTests {

	@BeforeSuite
	static void dumpSystemInfo() {
		if (CommonTestUtil.fixProxyConfiguration()) {
			CommonTestUtil.dumpSystemInfo(System.err);
		}
	}
}
