/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.team.tests;

import org.eclipse.mylyn.context.sdk.util.ContextTestUtil;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 */
public class AllTeamTests {

	public static Test suite() {
		ContextTestUtil.triggerContextUiLazyStart();

		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.team.tests");
		suite.addTestSuite(TestSyncViewRefresh.class);
//FIXME: AF: enable test, see https://github.com/eclipse-mylyn/org.eclipse.mylyn.context/issues/9
//		suite.addTestSuite(ChangeSetManagerTest.class);
		suite.addTestSuite(CommitTemplateTest.class);
		suite.addTestSuite(TeamPropertiesLinkProviderTest.class);
		suite.addTestSuite(TaskFinderTest.class);
		suite.addTestSuite(CommitTemplateVariablesTest.class);
		suite.addTestSuite(CopyCommitMessageHandlerTest.class);
		return suite;
	}

}
