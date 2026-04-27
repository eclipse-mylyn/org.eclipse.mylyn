/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.java.tests;

import org.eclipse.mylyn.commons.sdk.util.UiTestUtil;
import org.eclipse.mylyn.context.sdk.util.ContextTestUtil;
import org.eclipse.mylyn.java.tests.search.JUnitReferencesSearchPluginTest;
import org.eclipse.mylyn.java.tests.search.JavaImplementorsSearchPluginTest;
import org.eclipse.mylyn.java.tests.search.JavaReadAccessSearchPluginTest;
import org.eclipse.mylyn.java.tests.search.JavaReferencesSearchTest;
import org.eclipse.mylyn.java.tests.search.JavaWriteAccessSearchPluginTest;
import org.eclipse.mylyn.java.tests.tasks.JavaTaskTemplateVariableResolverTest;
import org.eclipse.mylyn.java.tests.xml.ResultUpdaterTest;
import org.eclipse.mylyn.java.tests.xml.XmlSearchPluginTest;
import org.junit.platform.suite.api.BeforeSuite;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * @author Mik Kersten
 */
@Suite
@SelectClasses({ ContentSpecificContextTest.class, ResourceStructureMappingTest.class, InterestManipulationTest.class,
	RefactoringTest.class, ContentOutlineRefreshTest.class, TypeHistoryManagerTest.class,
	PackageExplorerRefreshTest.class, ProblemsListTest.class, InterestFilterTest.class,
	InteractionContextManagerTest.class, JavaStructureTest.class, JavaImplementorsSearchPluginTest.class,
	ResultUpdaterTest.class, JavaReadAccessSearchPluginTest.class, JavaReferencesSearchTest.class,
	JavaWriteAccessSearchPluginTest.class, JUnitReferencesSearchPluginTest.class, XmlSearchPluginTest.class,
	JavaEditingMonitorTest.class, JavaStackTraceContextComputationStrategyTest.class,
	JavaTaskTemplateVariableResolverTest.class, JavaEditorManagerTest.class })
public class AllJavaTests {

	@BeforeSuite
	static void suiteSetup() {
		ContextTestUtil.triggerContextUiLazyStart();
		UiTestUtil.closeWelcomeView();
	}
}
