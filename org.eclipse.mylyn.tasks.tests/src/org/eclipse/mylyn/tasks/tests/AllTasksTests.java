/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasks.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 */
public class AllTasksTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylar.tasklist.tests");

		// $JUnit-BEGIN$
		suite.addTestSuite(TaskPlanningEditorTest.class);
		suite.addTestSuite(TaskListManagerTest.class);
		suite.addTestSuite(RepositoryTaskSynchronizationTest.class);
		suite.addTestSuite(TaskRepositoryManagerTest.class);
		suite.addTestSuite(TaskRepositoriesExternalizerTest.class);
		suite.addTestSuite(TaskListContentProviderTest.class);
		suite.addTestSuite(TaskListBackupManagerTest.class);
		suite.addTestSuite(TableSorterTest.class);
		suite.addTestSuite(TaskKeyComparatorTest.class); 
		suite.addTestSuite(TaskTest.class);
		suite.addTestSuite(TaskListUiTest.class);
		suite.addTestSuite(TaskListDnDTest.class);
		suite.addTestSuite(TaskDataExportTest.class);
		suite.addTestSuite(TaskDataImportTest.class);
		suite.addTestSuite(BackgroundSaveTest.class);
		suite.addTestSuite(TaskActivityTimingTest.class);
		suite.addTestSuite(TaskActivityViewTest.class);
		suite.addTestSuite(TaskAttachmentActionsTest.class);
		suite.addTestSuite(RepositorySettingsPageTest.class);
		// suite.addTestSuite(RetrieveTitleFromUrlTest.class);
		suite.addTestSuite(TaskHistoryTest.class);
		suite.addTestSuite(UrlConnectionUtilTest.class);
		// $JUnit-END$
		return suite;
	}
}
