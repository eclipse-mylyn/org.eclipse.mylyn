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

import org.eclipse.mylyn.tasks.tests.core.ITasksCoreConstantsTest;
import org.eclipse.mylyn.tasks.tests.ui.ContextPerspectiveManagerTest;
import org.eclipse.mylyn.tasks.tests.ui.TaskAttachmentPropertyTesterTest;
import org.eclipse.mylyn.tasks.tests.ui.TaskHyperlinkDetectorTest;
import org.eclipse.mylyn.tasks.tests.ui.TaskRelationHyperlinkDetectorTest;
import org.eclipse.mylyn.tasks.tests.ui.editor.TaskEditorPartDescriptorTest;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class AllTasksTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.tasks.tests");
		suite.addTestSuite(TaskRepositoryTest.class);
		suite.addTestSuite(LinkProviderTest.class);
		suite.addTestSuite(TaskActivationActionTest.class);
		suite.addTestSuite(TaskListPresentationTest.class);
		suite.addTestSuite(TaskRepositorySorterTest.class);
		suite.addTestSuite(CopyDetailsActionTest.class);
		suite.addTestSuite(NewTaskFromSelectionActionTest.class);
		suite.addTestSuite(TaskListTest.class);
		suite.addTestSuite(ProjectRepositoryAssociationTest.class);
		suite.addTestSuite(TaskPlanningEditorTest.class);
		suite.addTestSuite(TaskListExternalizationTest.class);
		suite.addTestSuite(TaskDataManagerTest.class);
		suite.addTestSuite(TaskRepositoryManagerTest.class);
		suite.addTestSuite(TaskRepositoriesExternalizerTest.class);
		suite.addTestSuite(TaskListContentProviderTest.class);
		suite.addTestSuite(TaskListBackupManagerTest.class);
		suite.addTestSuite(TableSorterTest.class);
		suite.addTestSuite(TaskKeyComparatorTest.class);
		suite.addTestSuite(TaskTest.class);
		suite.addTestSuite(TaskListUiTest.class);
		suite.addTestSuite(TaskListDropAdapterTest.class);
		suite.addTestSuite(TasksUiUtilTest.class);
		suite.addTestSuite(TaskDataExportTest.class);
		suite.addTestSuite(ScheduledPresentationTest.class);
		suite.addTestSuite(TaskActivityTimingTest.class);
		suite.addTestSuite(TaskAttachmentTest.class);
		suite.addTestSuite(RepositorySettingsPageTest.class);
		suite.addTestSuite(CommentQuoterTest.class);
		suite.addTestSuite(TaskDataStoreTest.class);
		suite.addTestSuite(TaskExportImportTest.class);
		suite.addTestSuite(PersonProposalProviderTest.class);
		suite.addTestSuite(TaskRepositoryLocationTest.class);
		suite.addTestSuite(AttachmentSizeFormatterTest.class);
		suite.addTestSuite(TaskMapperTest.class);
		suite.addTestSuite(OrphanedTasksTest.class);
		suite.addTestSuite(TaskWorkingSetTest.class);
		suite.addTestSuite(TaskActivationHistoryTest.class);
		suite.addTestSuite(TaskActivityManagerTest.class);
		suite.addTestSuite(TaskRepositoryFilterTests.class);
		suite.addTestSuite(TaskDiffUtilTest.class);
		suite.addTestSuite(RefactorRepositoryUrlOperationTest.class);
		suite.addTestSuite(StackTraceDuplicateDetectorTest.class);
		// XXX fix and reenable
		//suite.addTestSuite(MarkTaskHandlerTest.class);
		suite.addTestSuite(RepositoryTemplateManagerTest.class);
		suite.addTestSuite(TaskHyperlinkDetectorTest.class);
		suite.addTestSuite(TaskRelationHyperlinkDetectorTest.class);
		suite.addTestSuite(TaskEditorPartDescriptorTest.class);
		suite.addTestSuite(TaskAttachmentPropertyTesterTest.class);
		suite.addTestSuite(CommentGroupStrategyTest.class);
		suite.addTestSuite(ContextPerspectiveManagerTest.class);
		suite.addTestSuite(ITasksCoreConstantsTest.class);

		// XXX long running tests, put back?
		//suite.addTestSuite(TaskDataImportTest.class);
		//suite.addTestSuite(QueryExportImportTest.class);
		//suite.addTestSuite(BackgroundSaveTest.class);

		return suite;
	}

}
