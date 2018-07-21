/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.bugzilla.tests;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tests.util.TestFixture;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;

public class BugzillaRepositoryConnectorTestWithGuest extends AbstractBugzillaTest {

	@Override
	protected void setUp() throws Exception {
		TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.setValue(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, false);
		manager = TasksUiPlugin.getRepositoryManager();
		TestFixture.resetTaskListAndRepositories();
		this.client = BugzillaFixture.current().client(PrivilegeLevel.GUEST);
		this.connector = BugzillaFixture.current().connector();
		this.repository = BugzillaFixture.current().repository();
		TasksUi.getRepositoryManager().addRepository(repository);
	}

	@Override
	protected void tearDown() throws Exception {
		TestFixture.resetTaskList();
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
	}

	public void testPrivateDescription() throws Exception {
		final TaskMapping taskMappingInit = new TaskMapping() {

			@Override
			public String getProduct() {
				return "TestProduct";
			}
		};
		final TaskMapping taskMappingSelect = new TaskMapping() {
			@Override
			public String getComponent() {
				return "TestComponent";
			}

			@Override
			public String getSummary() {
				return "test private comments";
			}

			@Override
			public String getDescription() {
				return "The Description of the private comments task";
			}

		};

		repository.setProperty(IBugzillaConstants.BUGZILLA_INSIDER_GROUP, "true");
		final TaskData[] taskDataNew = new TaskData[1];
		// create Task
		taskDataNew[0] = TasksUiInternal.createTaskData(repository, taskMappingInit, taskMappingSelect, null);
		ITask taskNew = TasksUiUtil.createOutgoingNewTask(taskDataNew[0].getConnectorKind(),
				taskDataNew[0].getRepositoryUrl());

		ITaskDataWorkingCopy workingCopy = TasksUi.getTaskDataManager().createWorkingCopy(taskNew, taskDataNew[0]);
		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
		workingCopy.save(changed, null);

		RepositoryResponse response = BugzillaFixture.current().submitTask(taskDataNew[0], client);//connector.getTaskDataHandler().postTaskData(repository, taskDataNew[0], changed,
		//new NullProgressMonitor());
		((AbstractTask) taskNew).setSubmitting(true);

		assertNotNull(response);
		assertEquals(ResponseKind.TASK_CREATED.toString(), response.getReposonseKind().toString());
		String taskId = response.getTaskId();

		ITask task = generateLocalTaskAndDownload(taskId);
		assertNotNull(task);
		TaskDataModel model = createModel(task);
		TaskData taskData = model.getTaskData();
		assertNotNull(taskData);
		TaskAttribute description = taskData.getRoot().getAttribute(BugzillaAttribute.LONG_DESC.getKey());
		TaskAttribute isPrivateAttribute = description.getAttribute(IBugzillaConstants.BUGZILLA_DESCRIPTION_IS_PRIVATE);
		assertEquals("0", isPrivateAttribute.getValue());
		TaskAttribute idAttribute = description.getAttribute(IBugzillaConstants.BUGZILLA_DESCRIPTION_ID);

		String value = idAttribute.getValue();
		TaskAttribute definedIsPrivate = description.getAttribute(IBugzillaConstants.BUGZILLA_PREFIX_DEFINED_ISPRIVATE
				+ value);
		if (definedIsPrivate == null) {
			definedIsPrivate = description.createAttribute(IBugzillaConstants.BUGZILLA_PREFIX_DEFINED_ISPRIVATE + value);
		}
		TaskAttribute isPrivate = description.getAttribute(IBugzillaConstants.BUGZILLA_PREFIX_ISPRIVATE + value);
		if (isPrivate == null) {
			isPrivate = description.createAttribute(IBugzillaConstants.BUGZILLA_PREFIX_ISPRIVATE + value);
		}
		definedIsPrivate.setValue("1"); //$NON-NLS-1$
		isPrivate.setValue("1"); //$NON-NLS-1$ 

		model.attributeChanged(description);
		changed = new HashSet<TaskAttribute>();
		changed.clear();
		changed.add(description);
		workingCopy = TasksUi.getTaskDataManager().createWorkingCopy(taskNew, taskDataNew[0]);
		workingCopy.save(changed, null);
		try {
			BugzillaFixture.current().submitTask(taskData, client);

		} catch (CoreException e) {
			assertTrue(e.getStatus()
					.getMessage()
					.indexOf("Sorry, but you are not allowed to (un)mark comments or attachments as private.") != -1);
			return;
		}

		fail("CoreException not found!");
	}

}
