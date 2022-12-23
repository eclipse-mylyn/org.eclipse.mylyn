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
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import org.eclipse.mylyn.commons.sdk.util.ManagedTestSuite;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
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

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 * @author Steffen Pingel
 * @author Benjamin Muskalla
 * @author Miles Parker
 */
public class AllTasksTests {

	public static Test suite() {
		TestSuite suite = new ManagedTestSuite(AllTasksTests.class.getName());
		addTests(suite);
		return suite;
	}

	public static TestSuite suite(TestConfiguration configuration) {
		TestSuite suite = new TestSuite(AllTasksTests.class.getName());
		addTests(suite);
		return suite;
	}

	public static void addTests(TestSuite suite) {
		suite.addTestSuite(TasksUiUtilTest.class);
		suite.addTestSuite(TaskListUiTest.class);
		suite.addTestSuite(TaskRepositoryCredentialsTest.class);
		suite.addTestSuite(LinkProviderTest.class);
		suite.addTestSuite(TaskActivationActionTest.class);
		suite.addTestSuite(TaskListPresentationTest.class);
		suite.addTestSuite(TaskRepositorySorterTest.class);
		suite.addTestSuite(CopyDetailsActionTest.class);
		suite.addTestSuite(NewTaskFromSelectionActionTest.class);
		suite.addTestSuite(TaskListTest.class);
		suite.addTestSuite(ProjectRepositoryAssociationTest.class);
		// FIXME re-enable test
		//suite.addTestSuite(TaskPlanningEditorTest.class);
		suite.addTestSuite(TaskListExternalizationTest.class);
		suite.addTestSuite(TaskDataManagerTest.class);
		suite.addTestSuite(TaskRepositoryManagerTest.class);
		suite.addTestSuite(TaskRepositoriesExternalizerTest.class);
		suite.addTestSuite(TaskListContentProviderTest.class);
		suite.addTestSuite(TaskListBackupManagerTest.class);
		suite.addTestSuite(TaskListSorterTest.class);
		suite.addTestSuite(TaskKeyComparatorTest.class);
		suite.addTestSuite(TaskTest.class);
		suite.addTestSuite(TaskListDropAdapterTest.class);
		suite.addTestSuite(TaskDataExportTest.class);
		suite.addTestSuite(TaskDataImportTest.class);
		suite.addTestSuite(ScheduledPresentationTest.class);
		suite.addTestSuite(TaskAttachmentTest.class);
		suite.addTestSuite(RepositorySettingsPageTest.class);
		suite.addTestSuite(CommentQuoterTest.class);
		suite.addTestSuite(TaskDataStoreTest.class);
		suite.addTestSuite(TaskExportImportTest.class);
		suite.addTestSuite(PersonProposalProviderTest.class);
		suite.addTestSuite(OptionsProposalProviderTest.class);
		suite.addTestSuite(TaskRepositoryLocationTest.class);
		suite.addTestSuite(TaskRepositoryTest.class);
		suite.addTestSuite(AttachmentSizeFormatterTest.class);
		suite.addTestSuite(TaskMapperTest.class);
		suite.addTestSuite(TaskListUnmatchedContainerTest.class);
		suite.addTestSuite(TaskWorkingSetTest.class);
		suite.addTestSuite(TaskActivationHistoryTest.class);
		suite.addTestSuite(TaskActivityManagerTest.class);
		suite.addTestSuite(TaskRepositoryFilterTests.class);
		suite.addTestSuite(TaskDiffUtilTest.class);
		suite.addTestSuite(RefactorRepositoryUrlOperationTest.class);
		suite.addTestSuite(StackTraceDuplicateDetectorTest.class);
		suite.addTestSuite(RepositoryCompletionProcessorTest.class);
		// XXX fix and reenable
		//suite.addTestSuite(MarkTaskHandlerTest.class);
		suite.addTestSuite(RepositoryTemplateManagerTest.class);
		suite.addTestSuite(TaskHyperlinkDetectorTest.class);
		suite.addTestSuite(TaskRelationHyperlinkDetectorTest.class);
		suite.addTestSuite(TaskUrlHyperlinkDetectorTest.class);
		suite.addTestSuite(TaskEditorPartDescriptorTest.class);
		suite.addTestSuite(TaskAttachmentPropertyTesterTest.class);
		suite.addTestSuite(CommentGroupStrategyTest.class);
		suite.addTestSuite(ITasksCoreConstantsTest.class);
		// FIXME re-enable: bug 380390
		//suite.addTestSuite(RetrieveTitleFromUrlTest.class);
		suite.addTestSuite(EditorUtilTest.class);
		suite.addTestSuite(FileTaskAttachmentSourceTest.class);
		suite.addTestSuite(TaskListSynchronizationSchedulerTest.class);
		suite.addTestSuite(PlanningPartTest.class);
		suite.addTestSuite(RepositoryCompletionProcessorTest.class);
		suite.addTestSuite(TaskDiffUtilTest.class);
		// XXX re-enable
		//suite.addTestSuite(ServiceMessageManagerTest.class);
		suite.addTestSuite(TaskMigratorTest.class);
		suite.addTestSuite(TaskListViewTest.class);
		suite.addTestSuite(AttachmentTableLabelProviderTest.class);
		suite.addTestSuite(TaskDataExternalizerTest.class);
		suite.addTestSuite(Xml11InputStreamTest.class);
		// XXX long running tests, put back?
		//suite.addTestSuite(QueryExportImportTest.class);
		//suite.addTestSuite(BackgroundSaveTest.class);
		suite.addTestSuite(MultipleTaskHyperlinkDetectorTest.class);
		suite.addTestSuite(RegionComparatorTest.class);
		suite.addTestSuite(PriorityLevelTest.class);
		suite.addTestSuite(TaskAttributeTest.class);
		suite.addTestSuite(TaskAttributeMapperTest.class);
		suite.addTestSuite(SupportHandlerManagerTest.class);
		suite.addTestSuite(TaskAttributeMetaDataTest.class);
		suite.addTestSuite(AttributeEditorTest.class);
		suite.addTestSuite(RepositoryClientManagerTest.class);
		suite.addTestSuite(AbstractRepositoryConnectorUiTest.class);
		suite.addTestSuite(SynchronizeTasksJobTest.class);
		suite.addTestSuite(TaskAttributeTest.class);
		suite.addTestSuite(ScheduledTaskContainerTest.class);
		suite.addTestSuite(RepositoryConnectorContributorTest.class);
		suite.addTestSuite(TaskInitializationDataTest.class);
		suite.addTestSuite(TaskDataDiffTest.class);
		suite.addTestSuite(SynchronizationMangerTest.class);
		suite.addTestSuite(TaskEditorExtensionsTest.class);
		suite.addTestSuite(AbstractRepositoryConnectorTest.class);
		suite.addTestSuite(TaskJobFactoryTest.class);
	}

}
