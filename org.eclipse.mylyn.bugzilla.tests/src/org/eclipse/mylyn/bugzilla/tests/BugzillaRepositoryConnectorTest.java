/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaOperation;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTaskDataHandler;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizationSession;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskOperation;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tests.util.TestUtil.PrivilegeLevel;

/**
 * @author Rob Elves
 * @author Frank Becker
 */
public class BugzillaRepositoryConnectorTest extends AbstractBugzillaTest {

	public void testSingleRetrievalFailure() throws CoreException {
		try {
			connector.getTaskData(repository, "99999", new NullProgressMonitor());
			fail("Invalid id error should have resulted");
		} catch (CoreException e) {
			assertTrue(e.getStatus().getMessage().contains(IBugzillaConstants.ERROR_MSG_INVALID_BUG_ID));
		}

	}

	public void testMultiRetrievalFailure() throws Exception {

		TaskData taskData1 = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		TaskData taskData2 = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);

		ITask task1 = TasksUi.getRepositoryModel().createTask(repository, taskData1.getTaskId());
		ITask taskX = TasksUi.getRepositoryModel().createTask(repository, "99999");
		ITask task2 = TasksUi.getRepositoryModel().createTask(repository, taskData2.getTaskId());
		TasksUiPlugin.getTaskList().addTask(task1);
		TasksUiPlugin.getTaskList().addTask(taskX);
		TasksUiPlugin.getTaskList().addTask(task2);
		Set<ITask> tasks = new HashSet<ITask>();
		tasks.add(task1);
		tasks.add(taskX);
		tasks.add(task2);
		TasksUiInternal.synchronizeTasks(connector, tasks, true, null);

		//TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
		assertNotNull(TasksUiPlugin.getTaskDataManager().getTaskData(task1));
		assertNotNull(TasksUiPlugin.getTaskDataManager().getTaskData(task2));
		assertNull(TasksUiPlugin.getTaskDataManager().getTaskData(taskX));
		assertNull(((AbstractTask) task1).getStatus());
		assertNull(((AbstractTask) task2).getStatus());
		assertEquals(IBugzillaConstants.ERROR_MSG_NO_DATA_RETRIEVED, ((AbstractTask) taskX).getStatus().getMessage());
	}

	// FIXME: How to test with dynamic fixtures/bugs?
//	public void testBugWithoutDescription218() throws Exception {
//		init218();
//		doBugWithoutDescription("57");
//	}
//
//	public void testBugWithoutDescription222() throws Exception {
//		init222();
//		doBugWithoutDescription("391");
//	}
//
//	public void testBugWithoutDescription32() throws Exception {
//		init32();
//		doBugWithoutDescription("293");
//	}
//
//	private void doBugWithoutDescription(String taskNumber) throws CoreException {
//		ITask task = generateLocalTaskAndDownload(taskNumber);
//		assertNotNull(task);
//		TaskDataModel model = createModel(task);
//		TaskData taskData = model.getTaskData();
//		assertNotNull(taskData);
//		int numComment = taskData.getAttributeMapper().getAttributesByType(taskData, TaskAttribute.TYPE_COMMENT).size();
//
//		String newCommentText = "new Comment for Bug Without an Description " + (new Date()).toString();
//		TaskAttribute comment = taskData.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW);
//		comment.setValue(newCommentText);
//		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
//		changed.add(comment);
//
//		// Submit changes
//		submit(task, taskData, changed);
//		task = generateLocalTaskAndDownload(taskNumber);
//		assertNotNull(task);
//		model = createModel(task);
//		taskData = model.getTaskData();
//		assertNotNull(taskData);
//		int numCommentNew = taskData.getAttributeMapper()
//				.getAttributesByType(taskData, TaskAttribute.TYPE_COMMENT)
//				.size();
//		assertEquals(numComment + 1, numCommentNew);
//	}

	//testReassign Bugs
	//Version	BugNum	assigned				reporter
	//2.22		92		user@mylar.eclipse.org	tests@mylar.eclipse.org
	//3.0		 5		tests@mylar.eclipse.org	tests2@mylar.eclipse.org
	//3.1		 1		rob.elves@eclipse.org	tests@mylar.eclipse.org

//	public void testReassign() throws Exception {
//		TaskData taskData = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
//		assertNotNull(taskData);
//		String taskNumber = taskData.getTaskId();
//		doReassignOld(taskNumber, "user@mylar.eclipse.org");
//	}
//
//	public void testReassign30() throws CoreException {
//		init30();
//		String taskNumber = "5";
//		doReassignOld(taskNumber, "tests@mylyn.eclipse.org");
//	}
//
//	public void testReassign32() throws CoreException {
//		init32();
//		String taskNumber = "1";
//
//		// Get the task
//		ITask task = generateLocalTaskAndDownload(taskNumber);
//		assertNotNull(task);
//		TaskDataModel model = createModel(task);
//		TaskData taskData = model.getTaskData();
//		assertNotNull(taskData);
//		TaskMapper mapper = new TaskMapper(taskData);
//
//		if (mapper.getOwner().equals("rob.elves@eclipse.org")) {
//			assertEquals("rob.elves@eclipse.org", mapper.getOwner());
//			reassingToUser31(task, taskData);
//			task = generateLocalTaskAndDownload(taskNumber);
//			assertNotNull(task);
//			model = createModel(task);
//			taskData = model.getTaskData();
//			assertNotNull(taskData);
//			mapper = new TaskMapper(taskData);
//			assertEquals("tests2@mylyn.eclipse.org", mapper.getOwner());
//
//			reassignToDefault31(task, taskData);
//			task = generateLocalTaskAndDownload(taskNumber);
//			assertNotNull(task);
//			model = createModel(task);
//			taskData = model.getTaskData();
//			assertNotNull(taskData);
//			mapper = new TaskMapper(taskData);
//			assertEquals("rob.elves@eclipse.org", mapper.getOwner());
//		} else if (mapper.getOwner().equals("tests2@mylyn.eclipse.org")) {
//			assertEquals("tests2@mylyn.eclipse.org", mapper.getOwner());
//			reassignToDefault31(task, taskData);
//			task = generateLocalTaskAndDownload(taskNumber);
//			assertNotNull(task);
//			model = createModel(task);
//			taskData = model.getTaskData();
//			assertNotNull(taskData);
//			mapper = new TaskMapper(taskData);
//			assertEquals("rob.elves@eclipse.org", mapper.getOwner());
//
//			reassingToUser31(task, taskData);
//			task = generateLocalTaskAndDownload(taskNumber);
//			assertNotNull(task);
//			model = createModel(task);
//			taskData = model.getTaskData();
//			assertNotNull(taskData);
//			mapper = new TaskMapper(taskData);
//			assertEquals("tests2@mylyn.eclipse.org", mapper.getOwner());
//		} else {
//			fail("Bug with unexpected user assigned");
//		}
//	}
//
//	private void reassignToDefault31(ITask task, TaskData taskData) throws CoreException {
//		// Modify it (reassignbycomponent)
//		String newCommentText = "BugzillaRepositoryClientTest.testReassign31(): reassignbycomponent "
//				+ (new Date()).toString();
//		TaskAttribute comment = taskData.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW);
//		comment.setValue(newCommentText);
//		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
//		changed.add(comment);
//
//		TaskAttribute assignee = taskData.getRoot().getAttribute(BugzillaAttribute.SET_DEFAULT_ASSIGNEE.getKey());
//		assignee.setValue("1");
//		changed.add(assignee);
//		// Submit changes
//		submit(task, taskData, changed);
//	}
//
//	private void reassingToUser31(ITask task, TaskData taskData) throws CoreException {
//		// Modify it (reassign to tests2@mylyn.eclipse.org)
//		String newCommentText = "BugzillaRepositoryClientTest.testReassign31(): reassign " + (new Date()).toString();
//		TaskAttribute comment = taskData.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW);
//		comment.setValue(newCommentText);
//		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
//		changed.add(comment);
//
//		TaskAttribute assignedAttribute = taskData.getRoot().getAttribute(BugzillaAttribute.ASSIGNED_TO.getKey());
//		assignedAttribute.setValue("tests2@mylyn.eclipse.org");
//		changed.add(assignedAttribute);
//
//		// Submit changes
//		submit(task, taskData, changed);
//	}
//
//	private void doReassignOld(String taskNumber, String defaultAssignee) throws CoreException {
//		// Get the task
//		ITask task = generateLocalTaskAndDownload(taskNumber);
//		assertNotNull(task);
//		TaskDataModel model = createModel(task);
//		TaskData taskData = model.getTaskData();
//		assertNotNull(taskData);
//		TaskMapper mapper = new TaskMapper(taskData);
//
//		if (mapper.getOwner().equals(defaultAssignee)) {
//			assertEquals(defaultAssignee, mapper.getOwner());
//			reassingToUserOld(task, taskData);
//			task = generateLocalTaskAndDownload(taskNumber);
//			assertNotNull(task);
//			model = createModel(task);
//			taskData = model.getTaskData();
//			assertNotNull(taskData);
//			mapper = new TaskMapper(taskData);
//			assertEquals("tests2@mylyn.eclipse.org", mapper.getOwner());
//
//			reassignToDefaultOld(task, taskData);
//			task = generateLocalTaskAndDownload(taskNumber);
//			assertNotNull(task);
//			model = createModel(task);
//			taskData = model.getTaskData();
//			assertNotNull(taskData);
//			mapper = new TaskMapper(taskData);
//			assertEquals(defaultAssignee, mapper.getOwner());
//		} else if (mapper.getOwner().equals("tests2@mylyn.eclipse.org")) {
//			assertEquals("tests2@mylyn.eclipse.org", mapper.getOwner());
//			reassignToDefaultOld(task, taskData);
//			task = generateLocalTaskAndDownload(taskNumber);
//			assertNotNull(task);
//			model = createModel(task);
//			taskData = model.getTaskData();
//			assertNotNull(taskData);
//			mapper = new TaskMapper(taskData);
//			assertEquals(defaultAssignee, mapper.getOwner());
//
//			reassingToUserOld(task, taskData);
//			task = generateLocalTaskAndDownload(taskNumber);
//			assertNotNull(task);
//			model = createModel(task);
//			taskData = model.getTaskData();
//			assertNotNull(taskData);
//			mapper = new TaskMapper(taskData);
//			assertEquals("tests2@mylyn.eclipse.org", mapper.getOwner());
//		} else {
//			fail("Bug with unexpected user assigned");
//		}
//	}
//
//	private void reassignToDefaultOld(ITask task, TaskData taskData) throws CoreException {
//		// Modify it (reassignbycomponent)
//		String newCommentText = "BugzillaRepositoryClientTest.testReassignOld(): reassignbycomponent "
//				+ (new Date()).toString();
//		TaskAttribute comment = taskData.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW);
//		comment.setValue(newCommentText);
//		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
//		changed.add(comment);
//		TaskAttribute selectedOperationAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
//		TaskOperation.applyTo(selectedOperationAttribute, BugzillaOperation.reassignbycomponent.toString(),
//				BugzillaOperation.reassignbycomponent.getLabel());
//		changed.add(selectedOperationAttribute);
//
//		// Submit changes
//		submit(task, taskData, null);
//	}
//
//	private void reassingToUserOld(ITask task, TaskData taskData) throws CoreException {
//		// Modify it (reassign to tests2@mylyn.eclipse.org)
//		String newCommentText = "BugzillaRepositoryClientTest.testReassignOld(): reassign " + (new Date()).toString();
//		TaskAttribute comment = taskData.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW);
//		comment.setValue(newCommentText);
//		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
//		changed.add(comment);
//		TaskAttribute selectedOperationAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
//		TaskOperation.applyTo(selectedOperationAttribute, BugzillaOperation.reassign.toString(),
//				BugzillaOperation.reassign.getLabel());
//		TaskAttribute assignedAttribute = taskData.getRoot().getAttribute("reassignInput");
//		assignedAttribute.setValue("tests2@mylyn.eclipse.org");
//		changed.add(selectedOperationAttribute);
//		changed.add(assignedAttribute);
//
//		// Submit changes
//		submit(task, taskData, null);
//	}

	/*
	 * Test for the following State transformation
	 * NEW -> ASSIGNED -> RESOLVED DUPLICATE -> VERIFIED -> CLOSED -> REOPENED -> RESOLVED FIXED
	 * 
	 */

	// FIXME
//	public void testStdWorkflow222() throws Exception {
//		init222();
//		doStdWorkflow("101");
//	}
//
	public void testStdWorkflow32() throws Exception {
		if (BugzillaFixture.current() != BugzillaFixture.BUGS_3_6_CUSTOM_WF) {
			doStdWorkflow("3");
		}
	}

	private void doStdWorkflow(String DupBugID) throws Exception {
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
				return "test the std workflow";
			}

			@Override
			public String getDescription() {
				return "The Description of the std workflow task";
			}

		};

		final TaskData[] taskDataNew = new TaskData[1];
		// create Task
		taskDataNew[0] = TasksUiInternal.createTaskData(repository, taskMappingInit, taskMappingSelect, null);
		ITask taskNew = TasksUiUtil.createOutgoingNewTask(taskDataNew[0].getConnectorKind(),
				taskDataNew[0].getRepositoryUrl());

		//set Color to a legal value if exists
//		TaskAttribute colorAttribute = taskDataNew[0].getRoot().getAttribute("cf_multiselect");
//		if (colorAttribute != null) {
//			colorAttribute.setValue("Green");
//		}

		ITaskDataWorkingCopy workingCopy = TasksUi.getTaskDataManager().createWorkingCopy(taskNew, taskDataNew[0]);
		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
		workingCopy.save(changed, null);

		RepositoryResponse response = BugzillaFixture.current().submitTask(taskDataNew[0], client);//connector.getTaskDataHandler().postTaskData(repository, taskDataNew[0], changed,
		//new NullProgressMonitor());
		((AbstractTask) taskNew).setSubmitting(true);

		assertNotNull(response);
		assertEquals(ResponseKind.TASK_CREATED.toString(), response.getReposonseKind().toString());
		String taskId = response.getTaskId();

		// change Status from NEW -> ASSIGNED
		ITask task = generateLocalTaskAndDownload(taskId);
		assertNotNull(task);
		TaskDataModel model = createModel(task);
		TaskData taskData = model.getTaskData();
		assertNotNull(taskData);
		TaskAttribute statusAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		assertEquals("NEW", statusAttribute.getValue());
		TaskAttribute selectedOperationAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
		TaskOperation.applyTo(selectedOperationAttribute, BugzillaOperation.accept.toString(),
				BugzillaOperation.accept.getLabel());
		model.attributeChanged(selectedOperationAttribute);
		changed.clear();
		changed.add(selectedOperationAttribute);
		workingCopy.save(changed, null);
		response = BugzillaFixture.current().submitTask(taskData, client);
//		response = submit(taskNew, taskData, changed);
//		assertNotNull(response);
		assertEquals(ResponseKind.TASK_UPDATED.toString(), response.getReposonseKind().toString());

		// change Status from ASSIGNED -> RESOLVED DUPLICATE
		task = generateLocalTaskAndDownload(taskId);
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);
		statusAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		assertEquals("ASSIGNED", statusAttribute.getValue());
		selectedOperationAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
		TaskOperation.applyTo(selectedOperationAttribute, BugzillaOperation.duplicate.toString(),
				BugzillaOperation.duplicate.getLabel());
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

		// change Status from RESOLVED DUPLICATE -> VERIFIED
		task = generateLocalTaskAndDownload(taskId);
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);
		statusAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		assertEquals("RESOLVED", statusAttribute.getValue());
		TaskAttribute resolution = taskData.getRoot().getMappedAttribute(TaskAttribute.RESOLUTION);
		assertEquals("DUPLICATE", resolution.getValue());
		selectedOperationAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
		TaskOperation.applyTo(selectedOperationAttribute, BugzillaOperation.verify.toString(),
				BugzillaOperation.verify.getLabel());
		model.attributeChanged(selectedOperationAttribute);
		changed.clear();
		changed.add(selectedOperationAttribute);
		workingCopy.save(changed, null);
		response = BugzillaFixture.current().submitTask(taskData, client);
		assertNotNull(response);
		assertEquals(ResponseKind.TASK_UPDATED.toString(), response.getReposonseKind().toString());

		// change Status from VERIFIED -> CLOSE
		task = generateLocalTaskAndDownload(taskId);
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);
		statusAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		assertEquals("VERIFIED", statusAttribute.getValue());
		selectedOperationAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
		TaskOperation.applyTo(selectedOperationAttribute, BugzillaOperation.close.toString(),
				BugzillaOperation.close.getLabel());
		model.attributeChanged(selectedOperationAttribute);
		changed.clear();
		changed.add(selectedOperationAttribute);
		workingCopy.save(changed, null);
		response = BugzillaFixture.current().submitTask(taskData, client);
		assertNotNull(response);
		assertEquals(ResponseKind.TASK_UPDATED.toString(), response.getReposonseKind().toString());

		// change Status from CLOSE -> REOPENED
		task = generateLocalTaskAndDownload(taskId);
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);
		statusAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		assertEquals("CLOSED", statusAttribute.getValue());
		selectedOperationAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
		TaskOperation.applyTo(selectedOperationAttribute, BugzillaOperation.reopen.toString(),
				BugzillaOperation.reopen.getLabel());
		model.attributeChanged(selectedOperationAttribute);
		changed.clear();
		changed.add(selectedOperationAttribute);
		workingCopy.save(changed, null);
		response = BugzillaFixture.current().submitTask(taskData, client);
		assertNotNull(response);
		assertEquals(ResponseKind.TASK_UPDATED.toString(), response.getReposonseKind().toString());

		// change Status from REOPENED -> RESOLVED FIXED
		task = generateLocalTaskAndDownload(taskId);
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);
		statusAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		assertEquals("REOPENED", statusAttribute.getValue());
		selectedOperationAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
		TaskOperation.applyTo(selectedOperationAttribute, BugzillaOperation.resolve.toString(),
				BugzillaOperation.resolve.getLabel());
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

		// test last state
		task = generateLocalTaskAndDownload(taskId);
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);
		statusAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		assertEquals("RESOLVED", statusAttribute.getValue());
		resolution = taskData.getRoot().getMappedAttribute(TaskAttribute.RESOLUTION);
		assertEquals("FIXED", resolution.getValue());
	}

	public void testGetTaskData() throws Exception {
		TaskData data = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		assertNotNull(data);

		TaskData data2 = connector.getTaskData(repository, data.getTaskId(), new NullProgressMonitor());
		assertNotNull(data2);

		for (TaskAttribute attribute : data.getRoot().getAttributes().values()) {
			if (!attribute.getId().equals(BugzillaAttribute.TOKEN.getKey())) {
				TaskAttribute attr = data2.getRoot().getAttribute(attribute.getId());
				assertNotNull(attr);
				assertEquals(attribute.getValue(), attr.getValue());
			}
		}
	}

	public void testMidAirCollision() throws Exception {
		TaskData data = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		assertNotNull(data);
//		// Get the task
//		ITask task = generateLocalTaskAndDownload(data.getTaskId());
//
//		ITaskDataWorkingCopy workingCopy = TasksUiPlugin.getTaskDataManager().getWorkingCopy(task);
//		TaskData taskData = workingCopy.getLocalData();
//		assertNotNull(taskData);

		String newCommentText = "BugzillaRepositoryClientTest.testMidAirCollision(): test " + (new Date()).toString();
		TaskAttribute attrNewComment = data.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW);
		attrNewComment.setValue(newCommentText);
		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
		changed.add(attrNewComment);
		TaskAttribute attrDeltaTs = data.getRoot().getMappedAttribute(TaskAttribute.DATE_MODIFICATION);
		attrDeltaTs.setValue("2007-01-01 00:00:00");
		changed.add(attrDeltaTs);

		//workingCopy.save(changed, new NullProgressMonitor());

		try {
			// Submit changes
			RepositoryResponse response = BugzillaFixture.current().submitTask(data, client);
			assertNotNull(response);
			//assertEquals(ResponseKind.TASK_UPDATED, response.getReposonseKind());
			System.err.println("\n\ntestMidAirCollision >>> ResponseKind:" + response.getReposonseKind().toString()
					+ "\n\n");
			fail("Mid-air collision expected");
		} catch (CoreException e) {
			assertTrue(e.getStatus().getMessage().indexOf("Mid-air collision occurred while submitting") != -1);
			return;
		}
		fail("Mid-air collision expected");
	}

	public void testAuthenticationCredentials() throws Exception {
		TaskData data = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		assertNotNull(data);
		ITask task = generateLocalTaskAndDownload(data.getTaskId());
		assertNotNull(task);
		TasksUiPlugin.getTaskActivityManager().activateTask(task);
		File sourceContextFile = ContextCorePlugin.getContextStore().getFileForContext(task.getHandleIdentifier());
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
		sourceContextFile.createNewFile();
		sourceContextFile.deleteOnExit();
		AuthenticationCredentials oldCreds = repository.getCredentials(AuthenticationType.REPOSITORY);
		AuthenticationCredentials wrongCreds = new AuthenticationCredentials("wrong", "wrong");
		repository.setCredentials(AuthenticationType.REPOSITORY, wrongCreds, false);
		TasksUiPlugin.getRepositoryManager().notifyRepositorySettingsChanged(repository);
		TaskData taskData = TasksUiPlugin.getTaskDataManager().getTaskData(task);
		TaskAttributeMapper mapper = taskData.getAttributeMapper();
		TaskAttribute attribute = mapper.createTaskAttachment(taskData);
		try {
			AttachmentUtil.postContext(connector, repository, task, "test", attribute, new NullProgressMonitor());
		} catch (CoreException e) {
			assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
			assertTrue(e.getStatus().getMessage().indexOf("invalid username or password") != -1);
			return;
		} finally {
			repository.setCredentials(AuthenticationType.REPOSITORY, oldCreds, false);
			TasksUiPlugin.getRepositoryManager().notifyRepositorySettingsChanged(repository);
		}
		fail("Should have failed due to invalid userid and password.");
	}

	public void testSynchronize() throws CoreException, Exception {

		// Get the task

		TaskData data = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		assertNotNull(data);
		ITask task = generateLocalTaskAndDownload(data.getTaskId());
		TasksUi.getTaskDataManager().discardEdits(task);
		TaskDataModel model = createModel(task);
		TaskData taskData = model.getTaskData();
		assertNotNull(taskData);

//		int numComments = taskData.getAttributeMapper()
//				.getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT)
//				.size();

		// Modify it
		String newCommentText = "BugzillaRepositoryClientTest.testSynchronize(): " + (new Date()).toString();
		TaskAttribute attrNewComment = taskData.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW);
		attrNewComment.setValue(newCommentText);
		model.attributeChanged(attrNewComment);
		model.save(new NullProgressMonitor());
		assertEquals(SynchronizationState.OUTGOING, task.getSynchronizationState());
		submit(model);
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());

//		TaskData taskData2 = workingCopy.getRepositoryData();
//		TaskMapper taskData2Mapper = new TaskMapper(taskData2);
//		TaskMapper taskData1Mapper = new TaskMapper(taskData);
//		assertFalse(taskData2Mapper.getModificationDate().equals(taskData1Mapper.getModificationDate()));
//		// Still not read
//		assertFalse(taskData2.getLastModified().equals(task.getLastReadTimeStamp()));
//		TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
//		assertEquals(taskData2.getLastModified(), task.getLastReadTimeStamp());
//		assertTrue(taskData2.getComments().size() > numComments);
//
//		// Has no outgoing changes or conflicts yet needs synch
//		// because task doesn't have bug report (new query hit)
//		// Result: retrieved with no incoming status
//		// task.setSyncState(SynchronizationState.SYNCHRONIZED);
//		TasksUiPlugin.getTaskDataStorageManager().remove(task.getRepositoryUrl(), task.getTaskId());
//		TasksUiInternal.synchronizeTask(connector, task, false, null);
//		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
//		RepositoryTaskData bugReport2 = null;
//		bugReport2 = TasksUiPlugin.getTaskDataStorageManager()
//				.getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
//		assertNotNull(bugReport2);
//		assertEquals(task.getTaskId(), bugReport2.getTaskId());
//
//		assertEquals(newCommentText, bugReport2.getComments().get(numComments).getText());
//		// TODO: Test that comment was appended
//		// ArrayList<Comment> comments = task.getTaskData().getComments();
//		// assertNotNull(comments);
//		// assertTrue(comments.size() > 0);
//		// Comment lastComment = comments.get(comments.size() - 1);
//		// assertEquals(newCommentText, lastComment.getText());

	}

	public void testMissingHits() throws Exception {
		String queryString = "http://mylyn.eclipse.org/bugs323/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=&product=TestProduct&long_desc_type=substring&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&deadlinefrom=&deadlineto=&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&emailassigned_to1=1&emailtype1=substring&email1=&emailassigned_to2=1&emailreporter2=1&emailcc2=1&emailtype2=substring&email2=&bugidtype=include&bug_id=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=";
		RepositoryQuery query = new RepositoryQuery(BugzillaCorePlugin.CONNECTOR_KIND, "test");
		query.setRepositoryUrl(repository.getRepositoryUrl());
		query.setUrl(queryString);
		TasksUiPlugin.getTaskList().addQuery(query);

		TasksUiInternal.synchronizeQuery(connector, query, null, true);

		for (ITask task : query.getChildren()) {
			assertTrue(task.getSynchronizationState() == SynchronizationState.INCOMING_NEW);
			TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
			assertTrue(task.getSynchronizationState() == SynchronizationState.SYNCHRONIZED);
		}

		repository.setSynchronizationTimeStamp("1970-01-01");
		TasksUiInternal.synchronizeQuery(connector, query, null, true);
		for (ITask task : query.getChildren()) {
			assertTrue(task.getSynchronizationState() == SynchronizationState.SYNCHRONIZED);
		}
	}

	public void testAnonymousRepositoryAccess() throws Exception {
		assertNotNull(repository);
		AuthenticationCredentials anonymousCreds = new AuthenticationCredentials("", "");
		repository.setCredentials(AuthenticationType.REPOSITORY, anonymousCreds, false);
		TasksUiPlugin.getRepositoryManager().notifyRepositorySettingsChanged(repository);
		// test anonymous task retrieval
		TaskData data = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		assertNotNull(data);
		ITask task = this.generateLocalTaskAndDownload(data.getTaskId());
		assertNotNull(task);

		// test anonymous update of configuration
		RepositoryConfiguration config = connector.getRepositoryConfiguration(repository, false, null);
		assertNotNull(config);
		assertTrue(config.getComponents().size() > 0);
	}

	public void testTimeTracker() throws Exception {

		BugzillaVersion version = new BugzillaVersion(BugzillaFixture.current().getVersion());
		if (version.isSmallerOrEquals(BugzillaVersion.BUGZILLA_3_2)) {
			return;
		}

		boolean enableDeadline = true;
		TaskData data = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		assertNotNull(data);
		ITask task = generateLocalTaskAndDownload(data.getTaskId());
		assertNotNull(task);

		//ITask task = generateLocalTaskAndDownload("" + taskid);
		//assertNotNull(task);
		TaskDataModel model = createModel(task);
		TaskData taskData = model.getTaskData();
		assertNotNull(taskData);
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());

		Set<ITask> tasks = new HashSet<ITask>();
		tasks.add(task);

		synchAndAssertState(tasks, SynchronizationState.SYNCHRONIZED);

		float estimatedTime, remainingTime, actualTime, addTime;
		String deadline = null;

		estimatedTime = Float.parseFloat(taskData.getRoot()
				.getAttribute(BugzillaAttribute.ESTIMATED_TIME.getKey())
				.getValue());
		remainingTime = Float.parseFloat(taskData.getRoot()
				.getAttribute(BugzillaAttribute.REMAINING_TIME.getKey())
				.getValue());
		actualTime = Float.parseFloat(taskData.getRoot()
				.getAttribute(BugzillaAttribute.ACTUAL_TIME.getKey())
				.getValue());

		if (enableDeadline) {
			deadline = taskData.getRoot().getAttribute(BugzillaAttribute.DEADLINE.getKey()).getValue();
		}

		estimatedTime += 2;
		remainingTime += 1.5;
		addTime = 0.75f;
		if (enableDeadline) {
			deadline = generateNewDay();
		}

		taskData.getRoot().getAttribute(BugzillaAttribute.ESTIMATED_TIME.getKey()).setValue("" + estimatedTime);
		taskData.getRoot().getAttribute(BugzillaAttribute.REMAINING_TIME.getKey()).setValue("" + remainingTime);
		TaskAttribute workTime = taskData.getRoot().getAttribute(BugzillaAttribute.WORK_TIME.getKey());
		if (workTime == null) {
			BugzillaTaskDataHandler.createAttribute(taskData.getRoot(), BugzillaAttribute.WORK_TIME);
			workTime = taskData.getRoot().getAttribute(BugzillaAttribute.WORK_TIME.getKey());
		}
		workTime.setValue("" + addTime);
		if (enableDeadline) {
			taskData.getRoot().getAttribute(BugzillaAttribute.DEADLINE.getKey()).setValue("" + deadline);
		}

		taskData.getRoot()
				.getAttribute(BugzillaAttribute.NEW_COMMENT.getKey())
				.setValue("New Estimate: " + estimatedTime + "\nNew Remaining: " + remainingTime + "\nAdd: " + addTime);
		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
		changed.add(taskData.getRoot().getAttribute(BugzillaAttribute.ESTIMATED_TIME.getKey()));
		changed.add(taskData.getRoot().getAttribute(BugzillaAttribute.REMAINING_TIME.getKey()));
		changed.add(taskData.getRoot().getAttribute(BugzillaAttribute.WORK_TIME.getKey()));
		if (enableDeadline) {
			changed.add(taskData.getRoot().getAttribute(BugzillaAttribute.DEADLINE.getKey()));
		}
		changed.add(taskData.getRoot().getAttribute(BugzillaAttribute.NEW_COMMENT.getKey()));

		BugzillaFixture.current().submitTask(taskData, client);

		synchAndAssertState(tasks, SynchronizationState.SYNCHRONIZED);
		model = createModel(task);
		taskData = model.getTaskData();

		assertEquals(estimatedTime,
				Float.parseFloat(taskData.getRoot().getAttribute(BugzillaAttribute.ESTIMATED_TIME.getKey()).getValue()));
		assertEquals(remainingTime,
				Float.parseFloat(taskData.getRoot().getAttribute(BugzillaAttribute.REMAINING_TIME.getKey()).getValue()));
		assertEquals(actualTime + addTime,
				Float.parseFloat(taskData.getRoot().getAttribute(BugzillaAttribute.ACTUAL_TIME.getKey()).getValue()));
		if (enableDeadline) {
			assertEquals(deadline, taskData.getRoot().getAttribute(BugzillaAttribute.DEADLINE.getKey()).getValue());
		}

	}

	private String generateNewDay() {
		int year = 2006;
		int month = (int) (Math.random() * 12 + 1);
		int day = (int) (Math.random() * 28 + 1);
		return "" + year + "-" + ((month <= 9) ? "0" : "") + month + "-" + ((day <= 9) ? "0" : "") + day;
	}

	public void testSynchChangedReports() throws Exception {
		TaskData data = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		assertNotNull(data);
		ITask task4 = generateLocalTaskAndDownload(data.getTaskId());
		assertNotNull(task4);
		TaskDataModel model4 = createModel(task4);
		TaskData taskData4 = model4.getTaskData();
		assertNotNull(taskData4);
		assertEquals(SynchronizationState.SYNCHRONIZED, task4.getSynchronizationState());

		TaskData data2 = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		assertNotNull(data2);
		ITask task5 = generateLocalTaskAndDownload(data2.getTaskId());
		assertNotNull(task5);
		TaskDataModel model5 = createModel(task5);
		TaskData taskData5 = model5.getTaskData();
		assertNotNull(taskData5);
		assertEquals(SynchronizationState.SYNCHRONIZED, task5.getSynchronizationState());

		Set<ITask> tasks = new HashSet<ITask>();
		tasks.add(task4);
		tasks.add(task5);

		// Precondition for test passing is that task5's modification data is
		// AFTER
		// task4's
		DateFormat timeDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date lastModTime4 = null;
		Date lastModTime5 = null;
		String mostRecentTimeStamp4 = taskData4.getRoot().getAttribute(BugzillaAttribute.DELTA_TS.getKey()).getValue();
		String mostRecentTimeStamp = taskData5.getRoot().getAttribute(BugzillaAttribute.DELTA_TS.getKey()).getValue();
		lastModTime4 = timeDateFormat.parse(mostRecentTimeStamp4);
		lastModTime5 = timeDateFormat.parse(mostRecentTimeStamp);
		assertTrue("Precondition not mached", lastModTime5.after(lastModTime4));

		repository.setSynchronizationTimeStamp(mostRecentTimeStamp);

		SynchronizationSession event = new SynchronizationSession();
		event.setTasks(tasks);
		event.setNeedsPerformQueries(true);
		event.setTaskRepository(repository);
		event.setFullSynchronization(true);
		connector.preSynchronization(event, null);
		assertTrue(event.needsPerformQueries());
		// Always last known changed returned
		assertFalse(event.getStaleTasks().contains(task4));
		assertTrue(event.getStaleTasks().contains(task5));

		String priority4 = null;
		if (task4.getPriority().equals("P1")) {
			priority4 = "P2";
			taskData4.getRoot().getAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue(priority4);
		} else {
			priority4 = "P1";
			taskData4.getRoot().getAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue(priority4);
		}

		String priority5 = null;
		if (task5.getPriority().equals("P1")) {
			priority5 = "P2";
			taskData5.getRoot().getAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue(priority5);
		} else {
			priority5 = "P1";
			taskData5.getRoot().getAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue(priority5);
		}
		Set<TaskAttribute> changed4 = new HashSet<TaskAttribute>();
		Set<TaskAttribute> changed5 = new HashSet<TaskAttribute>();

		changed4.add(taskData4.getRoot().getAttribute(BugzillaAttribute.PRIORITY.getKey()));
		changed5.add(taskData5.getRoot().getAttribute(BugzillaAttribute.PRIORITY.getKey()));

		BugzillaFixture.current().submitTask(taskData4, client);
		BugzillaFixture.current().submitTask(taskData5, client);

		event = new SynchronizationSession();
		event.setTasks(tasks);
		event.setNeedsPerformQueries(true);
		event.setTaskRepository(repository);
		event.setFullSynchronization(true);
		connector.preSynchronization(event, null);
		assertTrue("Expected: " + task4.getTaskId() + ", " + task5.getTaskId() + ", got: " + event.getStaleTasks(),
				event.getStaleTasks().contains(task4) && event.getStaleTasks().contains(task5));

		TasksUiInternal.synchronizeTasks(connector, tasks, true, null);

		for (ITask task : tasks) {
			if (task.getTaskId() == "4") {
				assertEquals(priority4, taskData4.getRoot()
						.getAttribute(BugzillaAttribute.PRIORITY.getKey())
						.getValue());
			}
			if (task.getTaskId() == "5") {
				assertEquals(priority5, taskData5.getRoot()
						.getAttribute(BugzillaAttribute.PRIORITY.getKey())
						.getValue());
			}
		}
	}

}
