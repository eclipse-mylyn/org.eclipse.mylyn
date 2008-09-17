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

package org.eclipse.mylyn.tasks.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class AllTasksTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.tasks.tests");

		// $JUnit-BEGIN$
		suite.addTestSuite(LinkProviderTest.class);
		suite.addTestSuite(TaskActivationActionTest.class);
		suite.addTestSuite(TaskListPresentationTest.class);
		suite.addTestSuite(TaskRepositoryTest.class);
		suite.addTestSuite(TaskRepositorySorterTest.class);
		suite.addTestSuite(TaskDataStorageManagerTest.class);
		suite.addTestSuite(CopyDetailsActionTest.class);
		suite.addTestSuite(NewTaskFromSelectionActionTest.class);
		suite.addTestSuite(TaskListTest.class);
		suite.addTestSuite(ProjectRepositoryAssociationTest.class);
		suite.addTestSuite(TaskList06DataMigrationTest.class);
		suite.addTestSuite(TaskPlanningEditorTest.class);
		suite.addTestSuite(TaskListManagerTest.class);
		suite.addTestSuite(TaskListExternalizationTest.class);
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
		suite.addTestSuite(TasksUiUtilTest.class);
		suite.addTestSuite(TaskDataExportTest.class);
		// XXX: Put back
		//suite.addTestSuite(TaskDataImportTest.class);
		suite.addTestSuite(ScheduledPresentationTest.class);
		suite.addTestSuite(TaskActivityTimingTest.class);
		suite.addTestSuite(AttachmentJobTest.class);
		suite.addTestSuite(RepositorySettingsPageTest.class);
		suite.addTestSuite(TaskHistoryTest.class);
		suite.addTestSuite(CommentQuoterTest.class);
		suite.addTestSuite(OfflineStorageTest.class);
		suite.addTestSuite(OfflineCachingStorageTest.class);
		suite.addTestSuite(QueryExportImportTest.class);
		suite.addTestSuite(TaskExportImportTest.class);
		suite.addTestSuite(PersonProposalProviderTest.class);
		suite.addTestSuite(TaskRepositoryLocationTest.class);
		suite.addTestSuite(AttachmentSizeFormatterTest.class);
		suite.addTestSuite(TaskMappingTest.class);
		suite.addTestSuite(OrphanedTasksTest.class);
		suite.addTestSuite(TaskWorkingSetTest.class);
		suite.addTestSuite(TaskActivationHistoryTest.class);
		// $JUnit-END$

		// suite.addTestSuite(BackgroundSaveTest.class);
		// suite.addTestSuite(RetrieveTitleFromUrlTest.class);

		suite.addTestSuite(org.eclipse.mylyn.tasks.tests.web.NamedPatternTest.class);
		suite.addTestSuite(org.eclipse.mylyn.tasks.tests.web.HtmlDecodeEntityTest.class);
		suite.addTestSuite(org.eclipse.mylyn.tasks.tests.web.WebRepositoryTest.class);

		suite.addTestSuite(TaskActivityListenerTest.class);
		suite.addTestSuite(TaskRepositoryFilterTests.class);
		suite.addTestSuite(TaskDiffUtilTest.class);
		return suite;
	}
}
