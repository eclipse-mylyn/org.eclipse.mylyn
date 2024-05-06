/*******************************************************************************
 * Copyright (c) 2004, 2016 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import org.eclipse.mylyn.tasks.tests.bugs.SupportHandlerManagerTest;
import org.eclipse.mylyn.tasks.tests.core.AbstractRepositoryConnectorTest;
import org.eclipse.mylyn.tasks.tests.core.FileTaskAttachmentSourceTest;
import org.eclipse.mylyn.tasks.tests.core.ITasksCoreConstantsTest;
import org.eclipse.mylyn.tasks.tests.core.PriorityLevelTest;
import org.eclipse.mylyn.tasks.tests.core.RepositoryClientManagerTest;
import org.eclipse.mylyn.tasks.tests.core.RepositoryConnectorContributorTest;
import org.eclipse.mylyn.tasks.tests.core.SynchronizeTasksJobTest;
import org.eclipse.mylyn.tasks.tests.core.TaskAttributeMetaDataTest;
import org.eclipse.mylyn.tasks.tests.core.TaskInitializationDataTest;
import org.eclipse.mylyn.tasks.tests.core.TaskJobFactoryTest;
import org.eclipse.mylyn.tasks.tests.core.TaskListUnmatchedContainerTest;
import org.eclipse.mylyn.tasks.tests.core.TaskRepositoryLocationTest;
import org.eclipse.mylyn.tasks.tests.core.TaskRepositoryTest;
import org.eclipse.mylyn.tasks.tests.data.SynchronizationMangerTest;
import org.eclipse.mylyn.tasks.tests.data.TaskAttributeMapperTest;
import org.eclipse.mylyn.tasks.tests.data.TaskAttributeTest;
import org.eclipse.mylyn.tasks.tests.data.TaskDataDiffTest;
import org.eclipse.mylyn.tasks.tests.data.TaskDataExternalizerTest;
import org.eclipse.mylyn.tasks.tests.data.Xml11InputStreamTest;
import org.eclipse.mylyn.tasks.tests.ui.AbstractRepositoryConnectorUiTest;
import org.eclipse.mylyn.tasks.tests.ui.AttributeEditorTest;
import org.eclipse.mylyn.tasks.tests.ui.MultipleTaskHyperlinkDetectorTest;
import org.eclipse.mylyn.tasks.tests.ui.ScheduledTaskContainerTest;
import org.eclipse.mylyn.tasks.tests.ui.TaskAttachmentPropertyTesterTest;
import org.eclipse.mylyn.tasks.tests.ui.TaskHyperlinkDetectorTest;
import org.eclipse.mylyn.tasks.tests.ui.TaskListSynchronizationSchedulerTest;
import org.eclipse.mylyn.tasks.tests.ui.TaskListViewTest;
import org.eclipse.mylyn.tasks.tests.ui.TaskRelationHyperlinkDetectorTest;
import org.eclipse.mylyn.tasks.tests.ui.editor.AttachmentTableLabelProviderTest;
import org.eclipse.mylyn.tasks.tests.ui.editor.EditorUtilTest;
import org.eclipse.mylyn.tasks.tests.ui.editor.PlanningPartTest;
import org.eclipse.mylyn.tasks.tests.ui.editor.RegionComparatorTest;
import org.eclipse.mylyn.tasks.tests.ui.editor.RepositoryCompletionProcessorTest;
import org.eclipse.mylyn.tasks.tests.ui.editor.TaskEditorExtensionsTest;
import org.eclipse.mylyn.tasks.tests.ui.editor.TaskEditorPartDescriptorTest;
import org.eclipse.mylyn.tasks.tests.ui.editor.TaskMigratorTest;
import org.eclipse.mylyn.tasks.tests.ui.editor.TaskUrlHyperlinkDetectorTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 * @author Steffen Pingel
 * @author Benjamin Muskalla
 * @author Miles Parker
 */

@RunWith(Suite.class)
@SuiteClasses({ ScheduledTaskContainerTest.class, TasksUiUtilTest.class, TaskListUiTest.class,
		TaskRepositoryCredentialsTest.class, LinkProviderTest.class, TaskActivationActionTest.class,
		TaskListPresentationTest.class, TaskRepositorySorterTest.class, CopyDetailsActionTest.class,
		NewTaskFromSelectionActionTest.class, TaskListTest.class, ProjectRepositoryAssociationTest.class,
		TaskListExternalizationTest.class, TaskDataManagerTest.class, TaskRepositoryManagerTest.class,
		TaskRepositoriesExternalizerTest.class, TaskListContentProviderTest.class, TaskListBackupManagerTest.class,
		TaskListSorterTest.class, TaskKeyComparatorTest.class, TaskTest.class, TaskListDropAdapterTest.class,
		TaskDataExportTest.class, TaskDataImportTest.class, ScheduledPresentationTest.class, TaskAttachmentTest.class,
		RepositorySettingsPageTest.class, CommentQuoterTest.class, TaskDataStoreTest.class, TaskExportImportTest.class,
		PersonProposalProviderTest.class, OptionsProposalProviderTest.class, TaskRepositoryLocationTest.class,
		TaskRepositoryTest.class, AttachmentSizeFormatterTest.class, TaskMapperTest.class,
		TaskListUnmatchedContainerTest.class, TaskWorkingSetTest.class, TaskActivationHistoryTest.class,
		TaskActivityManagerTest.class, TaskRepositoryFilterTests.class, TaskDiffUtilTest.class,
		RefactorRepositoryUrlOperationTest.class, StackTraceDuplicateDetectorTest.class,
		RepositoryCompletionProcessorTest.class,

		RepositoryTemplateManagerTest.class, TaskHyperlinkDetectorTest.class, TaskRelationHyperlinkDetectorTest.class,
		TaskUrlHyperlinkDetectorTest.class, TaskEditorPartDescriptorTest.class, TaskAttachmentPropertyTesterTest.class,
		CommentGroupStrategyTest.class, ITasksCoreConstantsTest.class, EditorUtilTest.class,
		FileTaskAttachmentSourceTest.class, TaskListSynchronizationSchedulerTest.class, PlanningPartTest.class,
		RepositoryCompletionProcessorTest.class, TaskDiffUtilTest.class,

		TaskMigratorTest.class, TaskListViewTest.class, AttachmentTableLabelProviderTest.class,
		TaskDataExternalizerTest.class, Xml11InputStreamTest.class, MultipleTaskHyperlinkDetectorTest.class,
		RegionComparatorTest.class, PriorityLevelTest.class, TaskAttributeTest.class, TaskAttributeMapperTest.class,
		SupportHandlerManagerTest.class, TaskAttributeMetaDataTest.class, AttributeEditorTest.class,
		RepositoryClientManagerTest.class, AbstractRepositoryConnectorUiTest.class, SynchronizeTasksJobTest.class,
		TaskAttributeTest.class, RepositoryConnectorContributorTest.class, TaskInitializationDataTest.class,
		TaskDataDiffTest.class, SynchronizationMangerTest.class, TaskEditorExtensionsTest.class,
		AbstractRepositoryConnectorTest.class, TaskJobFactoryTest.class
})
public class AllTasksTests {
	public static void addTests(TestSuite suite) {

		// FIXME re-enable test
		//TaskPlanningEditorTest.class,

		// XXX fix and reenable
		//MarkTaskHandlerTest.class,

		// FIXME re-enable: bug 380390
		//RetrieveTitleFromUrlTest.class,

		// XXX re-enable
		//ServiceMessageManagerTest.class,

		// XXX long running tests, put back?
		//QueryExportImportTest.class,
		//BackgroundSaveTest.class,

	}

}
