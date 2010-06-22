/*******************************************************************************
 * Copyright (c) 2010 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.internal.bugzilla.core.Messages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskOperation;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

/**
 * @author Charley Wang
 */
public class BugzillaCustomRepositoryTest extends AbstractBugzillaTest {

	public void testWorkflow() throws Exception {
		doCustomWorkflow("3");
	}

	private void doCustomWorkflow(String DupBugID) throws Exception {
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
				return "test the custom workflow";
			}

			@Override
			public String getDescription() {
				return "The Description of the custom workflow task";
			}

		};

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

//		RepositoryConfiguration rc = BugzillaFixture.current().connector().getRepositoryConfiguration(
//				BugzillaFixture.TEST_BUGZILLA_36_URL + "-custom-wf-and-status");
//		if (rc != null) {
//			rc.setValidTransitions(BugzillaFixture.getFile(
//					"testdata/descriptor/" + BugzillaFixture.current().getInfo() + "Transition.txt").getCanonicalPath());
//		}

		// change Status from NEW -> ASSIGNED
		ITask task = generateLocalTaskAndDownload(taskId);
		assertNotNull(task);
		TaskDataModel model = createModel(task);
		TaskData taskData = model.getTaskData();
		assertNotNull(taskData);
		TaskAttribute statusAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		assertEquals("NEW", statusAttribute.getValue());
		TaskAttribute selectedOperationAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
		TaskOperation.applyTo(selectedOperationAttribute, "accept", Messages.BugzillaOperation_Accept_to_ASSIGNED);
		model.attributeChanged(selectedOperationAttribute);
		changed.clear();
		changed.add(selectedOperationAttribute);
		workingCopy.save(changed, null);
		response = BugzillaFixture.current().submitTask(taskData, client);
//		response = submit(taskNew, taskData, changed);
//		assertNotNull(response);
		assertEquals(ResponseKind.TASK_UPDATED.toString(), response.getReposonseKind().toString());

		// Customization case 1: Change workflow for standard statuses
		// change Status from ASSIGNED -> NEW should not work and should not throw an error
		task = generateLocalTaskAndDownload(taskId);
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);
		statusAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		assertEquals("ASSIGNED", statusAttribute.getValue());
		selectedOperationAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
		TaskOperation.applyTo(selectedOperationAttribute, "markNew", Messages.BugzillaOperation_Mark_as_NEW);
		model.attributeChanged(selectedOperationAttribute);
		changed.clear();
		changed.add(selectedOperationAttribute);
		workingCopy.save(changed, null);
		response = BugzillaFixture.current().submitTask(taskData, client);
		assertNotNull(response);
		assertEquals(ResponseKind.TASK_UPDATED.toString(), response.getReposonseKind().toString());

		// Customization case 3: Special Bugzilla information (duplicateStatus)
		// change Status from ASSIGNED -> DUPLICATE
		task = generateLocalTaskAndDownload(taskId);
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);
		statusAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		assertEquals("ASSIGNED", statusAttribute.getValue());
		selectedOperationAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
		TaskOperation.applyTo(selectedOperationAttribute, "duplicate", Messages.BugzillaOperation_Duplicate_of);
		TaskAttribute duplicateAttribute = taskData.getRoot().getAttribute("dup_id");
		duplicateAttribute.setValue(DupBugID);
		model.attributeChanged(selectedOperationAttribute);
		model.attributeChanged(duplicateAttribute);
		changed.clear();
		changed.add(selectedOperationAttribute);
		changed.add(duplicateAttribute);
		workingCopy.save(changed, null);
		response = BugzillaFixture.current().submitTask(taskData, client);
		assertNotNull(response);
		assertEquals(ResponseKind.TASK_UPDATED.toString(), response.getReposonseKind().toString());

		// Customization case 3: Special Bugzilla information (duplicateStatus)
		// change Status from [CLOSED] DUPLICATE -> VERIFIED
		task = generateLocalTaskAndDownload(taskId);
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);
		statusAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		assertEquals("CLOSED", statusAttribute.getValue());
		TaskAttribute resolution = taskData.getRoot().getMappedAttribute(TaskAttribute.RESOLUTION);
		assertEquals("DUPLICATE", resolution.getValue());
		selectedOperationAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
		TaskOperation.applyTo(selectedOperationAttribute, "verify", Messages.BugzillaOperation_Mark_as_VERIFIED);
		model.attributeChanged(selectedOperationAttribute);
		changed.clear();
		changed.add(selectedOperationAttribute);
		workingCopy.save(changed, null);
		response = BugzillaFixture.current().submitTask(taskData, client);
		assertNotNull(response);
		assertEquals(ResponseKind.TASK_UPDATED.toString(), response.getReposonseKind().toString());

		// Customization case 2: Custom name, open bug_status
		// change Status from VERIFIED -> MODIFIED
		task = generateLocalTaskAndDownload(taskId);
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);
		statusAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		assertEquals("VERIFIED", statusAttribute.getValue());
		selectedOperationAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
		TaskOperation.applyTo(selectedOperationAttribute, "MODIFIED", Messages.BugzillaOperation_Reopen_bug);
		model.attributeChanged(selectedOperationAttribute);
		changed.clear();
		changed.add(selectedOperationAttribute);
		workingCopy.save(changed, null);
		response = BugzillaFixture.current().submitTask(taskData, client);
		assertNotNull(response);
		assertEquals(ResponseKind.TASK_UPDATED.toString(), response.getReposonseKind().toString());

		// Customization case 2: Custom name, open bug_status
		// change Status from MODIFIED -> ON_DEV
		task = generateLocalTaskAndDownload(taskId);
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);
		statusAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		assertEquals("MODIFIED", statusAttribute.getValue());
		selectedOperationAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
		TaskOperation.applyTo(selectedOperationAttribute, "ON_DEV", Messages.BugzillaOperation_Reopen_bug);
		model.attributeChanged(selectedOperationAttribute);
		changed.clear();
		changed.add(selectedOperationAttribute);
		workingCopy.save(changed, null);
		response = BugzillaFixture.current().submitTask(taskData, client);
		assertNotNull(response);
		assertEquals(ResponseKind.TASK_UPDATED.toString(), response.getReposonseKind().toString());

		// Customization case 2: Custom name, closed bug_status
		// change Status from ON_DEV -> POST FIXED
		task = generateLocalTaskAndDownload(taskId);
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);
		statusAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		assertEquals("ON_DEV", statusAttribute.getValue());
		selectedOperationAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
		TaskOperation.applyTo(selectedOperationAttribute, "POST", Messages.BugzillaOperation_Resolve_as);
		resolution = taskData.getRoot().getMappedAttribute(TaskAttribute.RESOLUTION);
		resolution.setValue("FIXED");
		model.attributeChanged(selectedOperationAttribute);
		model.attributeChanged(resolution);
		changed.clear();
		changed.add(selectedOperationAttribute);
		changed.add(resolution);
		workingCopy.save(changed, null);
		response = BugzillaFixture.current().submitTask(taskData, client);
		assertNotNull(response);
		assertEquals(ResponseKind.TASK_UPDATED.toString(), response.getReposonseKind().toString());

		// Customization case 2: Custom name, closing bug
		// change Status from POST FIXED -> CLOSED FIXED
		task = generateLocalTaskAndDownload(taskId);
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);
		statusAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		assertEquals("POST", statusAttribute.getValue());
		resolution = taskData.getRoot().getMappedAttribute(TaskAttribute.RESOLUTION);
		assertEquals("FIXED", resolution.getValue());
		selectedOperationAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
		TaskOperation.applyTo(selectedOperationAttribute, "close", Messages.BugzillaOperation_Resolve_as);
		resolution = taskData.getRoot().getMappedAttribute(TaskAttribute.RESOLUTION);
		resolution.setValue("FIXED");
		model.attributeChanged(selectedOperationAttribute);
		model.attributeChanged(resolution);
		changed.clear();
		changed.add(selectedOperationAttribute);
		changed.add(resolution);
		workingCopy.save(changed, null);
		response = BugzillaFixture.current().submitTask(taskData, client);
		assertNotNull(response);
		assertEquals(ResponseKind.TASK_UPDATED.toString(), response.getReposonseKind().toString());

		// Customization case 1: Change workflow for standard statuses
		// change Status from CLOSE -> REOPENED should not work
		task = generateLocalTaskAndDownload(taskId);
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);
		statusAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		assertEquals("CLOSED", statusAttribute.getValue());
		resolution = taskData.getRoot().getMappedAttribute(TaskAttribute.RESOLUTION);
		assertEquals("FIXED", resolution.getValue());
		selectedOperationAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
		TaskOperation.applyTo(selectedOperationAttribute, "reopen", Messages.BugzillaOperation_Reopen_bug);
		model.attributeChanged(selectedOperationAttribute);
		changed.clear();
		changed.add(selectedOperationAttribute);
		workingCopy.save(changed, null);
		response = BugzillaFixture.current().submitTask(taskData, client);
		assertNotNull(response);
		assertEquals(ResponseKind.TASK_UPDATED.toString(), response.getReposonseKind().toString());

		// test last state has not changed
		task = generateLocalTaskAndDownload(taskId);
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);
		statusAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		assertEquals("CLOSED", statusAttribute.getValue());
		resolution = taskData.getRoot().getMappedAttribute(TaskAttribute.RESOLUTION);
		assertEquals("FIXED", resolution.getValue());

	}
}
