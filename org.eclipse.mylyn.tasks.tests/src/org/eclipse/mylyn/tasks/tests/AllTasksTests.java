/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 */
public class AllTasksTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.tasks.tests");

		// $JUnit-BEGIN$
		suite.addTestSuite(TaskRepositoryTest.class);
		suite.addTestSuite(TaskRepositorySorterTest.class);
		suite.addTestSuite(TaskDataManagerTest.class);
		suite.addTestSuite(CopyDetailsActionTest.class);
		suite.addTestSuite(TaskListTest.class);
		suite.addTestSuite(ProjectRepositoryAssociationTest.class);
		suite.addTestSuite(TaskList06DataMigrationTest.class);
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
		// XXX: Put back
		//suite.addTestSuite(TaskDataImportTest.class);
		suite.addTestSuite(TaskActivityTest.class);
		suite.addTestSuite(AttachmentJobTest.class);
		suite.addTestSuite(RepositorySettingsPageTest.class);
		suite.addTestSuite(TaskHistoryTest.class);
		suite.addTestSuite(UrlConnectionUtilTest.class);
		suite.addTestSuite(CommentQuoterTest.class);
		suite.addTestSuite(OfflineStorageTest.class);
		suite.addTestSuite(OfflineCachingStorageTest.class);
		suite.addTestSuite(QueryExportImportTest.class);
		// $JUnit-END$

		// suite.addTestSuite(BackgroundSaveTest.class);
		// suite.addTestSuite(RetrieveTitleFromUrlTest.class);

		suite.addTestSuite(org.eclipse.mylyn.tasks.tests.web.NamedPatternTest.class);

		return suite;
	}
}
