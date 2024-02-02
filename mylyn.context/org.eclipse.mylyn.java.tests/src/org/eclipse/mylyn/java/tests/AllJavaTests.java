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
import org.eclipse.mylyn.java.tests.xml.XmlSearchPluginTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 */
public class AllJavaTests {

	public static Test suite() {
		ContextTestUtil.triggerContextUiLazyStart();
		UiTestUtil.closeWelcomeView();

		TestSuite suite = new TestSuite(AllJavaTests.class.getName());
		suite.addTestSuite(ContentSpecificContextTest.class);
		suite.addTestSuite(ResourceStructureMappingTest.class);
		suite.addTestSuite(InterestManipulationTest.class);
		suite.addTestSuite(RefactoringTest.class);
		suite.addTestSuite(ContentOutlineRefreshTest.class);
		suite.addTestSuite(TypeHistoryManagerTest.class);
		suite.addTestSuite(PackageExplorerRefreshTest.class);
		// XXX 3.5 re-enable test case?
		//suite.addTestSuite(ResultUpdaterTest.class);
		suite.addTestSuite(ProblemsListTest.class);
		suite.addTestSuite(InterestFilterTest.class);
		suite.addTestSuite(InteractionContextManagerTest.class);
		suite.addTestSuite(JavaStructureTest.class);
		suite.addTestSuite(JavaImplementorsSearchPluginTest.class);
		suite.addTestSuite(JavaReadAccessSearchPluginTest.class);
		suite.addTestSuite(JavaReferencesSearchTest.class);
		suite.addTestSuite(JavaWriteAccessSearchPluginTest.class);
		suite.addTestSuite(JUnitReferencesSearchPluginTest.class);
		suite.addTestSuite(XmlSearchPluginTest.class);
		suite.addTestSuite(JavaEditingMonitorTest.class);
		suite.addTestSuite(JavaStackTraceContextComputationStrategyTest.class);
		suite.addTestSuite(JavaTaskTemplateVariableResolverTest.class);
		suite.addTestSuite(JavaEditorManagerTest.class);
		return suite;
	}

}
