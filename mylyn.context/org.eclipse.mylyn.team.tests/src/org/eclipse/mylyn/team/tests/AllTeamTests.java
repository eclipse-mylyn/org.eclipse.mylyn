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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.team.tests;

import org.eclipse.mylyn.context.sdk.util.ContextTestUtil;
import org.junit.platform.suite.api.BeforeSuite;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * @author Mik Kersten
 */
@Suite
@SelectClasses({ ChangeSetManagerTest.class, CommitTemplateTest.class, CommitTemplateVariablesTest.class,
	CopyCommitMessageHandlerTest.class, TaskFinderTest.class, TeamPropertiesLinkProviderTest.class,
	TestSyncViewRefresh.class })
public class AllTeamTests {

	@BeforeSuite
	static void suite() {
		ContextTestUtil.triggerContextUiLazyStart();
	}

}
