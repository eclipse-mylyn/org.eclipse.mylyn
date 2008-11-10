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

package org.eclipse.mylyn.bugzilla.tests;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaOperation;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.data.FileTaskAttachmentSource;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentPartSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.data.TaskOperation;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

public class BugzillaRepositoryConnectorTest2 extends AbstractBugzillaTest {

	/*
	 * Test for the following State transformation
	 * NEW -> ASSIGNED -> RESOLVED DUPLICATE -> VERIFIED -> CLOSED -> REOPENED -> RESOLVED FIXED
	 * 
	 */

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

		TasksUiPlugin.getRepositoryManager().addRepository(repository);
		final TaskData[] taskDataNew = new TaskData[1];
		// create Task
		taskDataNew[0] = TasksUiInternal.createTaskData(repository, taskMappingInit, taskMappingSelect, null);
		ITask taskNew = TasksUiUtil.createOutgoingNewTask(taskDataNew[0].getConnectorKind(),
				taskDataNew[0].getRepositoryUrl());
		ITaskDataWorkingCopy workingCopy = TasksUi.getTaskDataManager().createWorkingCopy(taskNew, taskDataNew[0]);
		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
		workingCopy.save(changed, null);
		RepositoryResponse response = submit(taskNew, taskDataNew[0], changed);
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
		response = submit(taskNew, taskData, changed);
		assertNotNull(response);
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
		response = submit(taskNew, taskData, changed);
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
		response = submit(taskNew, taskData, changed);
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
		response = submit(taskNew, taskData, changed);
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
		response = submit(taskNew, taskData, changed);
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
		response = submit(taskNew, taskData, changed);
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

	public void testStdWorkflow222() throws Exception {
		init222();
		doStdWorkflow("101");
	}

	public void testStdWorkflow32() throws Exception {
		init32();
		doStdWorkflow("3");
	}

	public void testAttachToExistingReport() throws Exception {
		init222();
		String taskNumber = "33";
		ITask task = generateLocalTaskAndDownload(taskNumber);
		assertNotNull(task);
		TaskDataModel model = createModel(task);
		TaskData taskData = model.getTaskData();
		assertNotNull(taskData);
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
		assertEquals(taskNumber, taskData.getTaskId());
//		int numAttached = taskData.getAttributeMapper()
//				.getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT)
//				.size();
//		String fileName = "test-attach-" + System.currentTimeMillis() + ".txt";

		assertNotNull(repository.getUserName());
		assertNotNull(repository.getPassword());

		TaskAttribute attrAttachment = taskData.getAttributeMapper().createTaskAttachment(taskData);
		TaskAttachmentMapper attachmentMapper = TaskAttachmentMapper.createFrom(attrAttachment);

		/* Initialize a local attachment */
		attachmentMapper.setDescription("Test attachment " + new Date());
		attachmentMapper.setContentType("text/plain");
		attachmentMapper.setPatch(false);
		attachmentMapper.setComment("Automated JUnit attachment test");

		/* Test attempt to upload a non-existent file */
		String filePath = "/this/is/not/a/real-file";
		TaskAttachmentPartSource source = new TaskAttachmentPartSource(
				new FileTaskAttachmentSource(new File(filePath)), "real-file");
		BugzillaClient client = connector.getClientManager().getClient(repository, new NullProgressMonitor());
		try {
			client.postAttachment(taskNumber, attachmentMapper.getComment(), attachmentMapper.getDescription(),
					"application/octet-stream", false, source, new NullProgressMonitor());
			fail();
		} catch (Exception e) {
		}
//		// attachmentHandler.uploadAttachment(repository, task, comment,
//		// summary, file, contentType, isPatch, proxySettings)
//		// assertFalse(attachmentHandler.uploadAttachment(attachment,
//		// repository.getUserName(), repository.getPassword(),
//		// Proxy.NO_PROXY));
//		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
//		task = TasksUiInternal.createTask(repository, taskNumber, new NullProgressMonitor());
//		TasksUiInternal.synchronizeTask(connector, task, true, null);
//
//		assertEquals(numAttached, taskData.getAttachments().size());
//
//		/* Test attempt to upload an empty file */
//		File attachFile = new File(fileName);
//		attachment.setFilePath(attachFile.getAbsolutePath());
//		BufferedWriter write = new BufferedWriter(new FileWriter(attachFile));
//		attachFile = new File(attachment.getFilePath());
//		attachment.setFile(attachFile);
//		attachment.setFilename(attachFile.getName());
//		// assertFalse(attachmentHandler.uploadAttachment(attachment,
//		// repository.getUserName(), repository.getPassword(),
//		// Proxy.NO_PROXY));
//		try {
//			client.postAttachment(attachment.getReport().getTaskId(), attachment.getComment(), attachment, null);
//			fail();
//		} catch (Exception e) {
//		}
//		task = TasksUiInternal.createTask(repository, taskNumber, new NullProgressMonitor());
//		TasksUiInternal.synchronizeTask(connector, task, true, null);
//		taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
//		assertEquals(numAttached, taskData.getAttachments().size());
//
//		/* Test uploading a proper file */
//		write.write("test file");
//		write.close();
//		attachment.setFilePath(attachFile.getAbsolutePath());
//		// assertTrue(attachmentHandler.uploadAttachment(attachment,
//		// repository.getUserName(), repository.getPassword(),
//		// Proxy.NO_PROXY));
//		File fileToAttach = new File(attachment.getFilePath());
//		assertTrue(fileToAttach.exists());
//		attachment.setFile(fileToAttach);
//		attachment.setFilename(fileToAttach.getName());
//		client.postAttachment(attachment.getReport().getTaskId(), attachment.getComment(), attachment, null);
//
//		task = TasksUiInternal.createTask(repository, taskNumber, new NullProgressMonitor());
//		TasksUiInternal.synchronizeTask(connector, task, true, null);
//		taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
//		assertEquals(numAttached + 1, taskData.getAttachments().size());
//
//		// use assertion to track clean-up
//		assertTrue(attachFile.delete());
	}

	public void testDataRetrieval() throws CoreException, ParseException {
		init(IBugzillaConstants.TEST_BUGZILLA_30_URL);
		TaskData data = connector.getTaskData(repository, "2", new NullProgressMonitor());
		assertNotNull(data);
		TaskMapper mapper = new TaskMapper(data);
		assertEquals("2", data.getTaskId());
		assertEquals("New bug submit", mapper.getSummary());
		assertEquals("Test new bug submission", mapper.getDescription());
		assertEquals(PriorityLevel.P2, mapper.getPriorityLevel());
		assertEquals("TestComponent", mapper.getComponent());
		assertEquals("nhapke@cs.ubc.ca", mapper.getOwner());
		assertEquals("TestProduct", mapper.getProduct());
		assertEquals("PC", mapper.getTaskData()
				.getRoot()
				.getMappedAttribute(BugzillaAttribute.REP_PLATFORM.getKey())
				.getValue());
		assertEquals("Windows", mapper.getTaskData()
				.getRoot()
				.getMappedAttribute(BugzillaAttribute.OP_SYS.getKey())
				.getValue());
		assertEquals("ASSIGNED", mapper.getTaskData().getRoot().getMappedAttribute(
				BugzillaAttribute.BUG_STATUS.getKey()).getValue());
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		assertEquals(format1.parse("2007-03-20 16:37"), mapper.getCreationDate());
		assertEquals(format2.parse("2008-09-24 13:33:02"), mapper.getModificationDate());

		//assertEquals("", mapper.getTaskUrl());
		//assertEquals("bugzilla", mapper.getTaskKind());
		//assertEquals("", mapper.getTaskKey());

		// test comments
		List<TaskAttribute> comments = data.getAttributeMapper().getAttributesByType(data, TaskAttribute.TYPE_COMMENT);
		assertEquals(12, comments.size());
		TaskCommentMapper commentMap = TaskCommentMapper.createFrom(comments.get(0));
		assertEquals("Rob Elves", commentMap.getAuthor().getName());
		assertEquals("Created an attachment (id=1)\ntest\n\ntest attachments", commentMap.getText());
		commentMap = TaskCommentMapper.createFrom(comments.get(10));
		assertEquals("Tests", commentMap.getAuthor().getName());
		assertEquals("test", commentMap.getText());
	}

	public void testMidAirCollision() throws Exception {
		init30();
		String taskNumber = "5";

		// Get the task
		ITask task = generateLocalTaskAndDownload(taskNumber);

		ITaskDataWorkingCopy workingCopy = TasksUiPlugin.getTaskDataManager().getWorkingCopy(task);
		TaskData taskData = workingCopy.getLocalData();
		assertNotNull(taskData);

//		TasksUiPlugin.getTaskList().addTask(task);

		String newCommentText = "BugzillaRepositoryClientTest.testMidAirCollision(): test " + (new Date()).toString();
		TaskAttribute attrNewComment = taskData.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW);
		attrNewComment.setValue(newCommentText);
		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
		changed.add(attrNewComment);
		TaskAttribute attrDeltaTs = taskData.getRoot().getMappedAttribute(TaskAttribute.DATE_MODIFICATION);
		attrDeltaTs.setValue("2007-01-01 00:00:00");
		changed.add(attrDeltaTs);

		workingCopy.save(changed, new NullProgressMonitor());

		try {
			// Submit changes
			submit(task, taskData, changed);
			fail("Mid-air collision expected");
		} catch (CoreException e) {
			assertTrue(e.getStatus().getMessage().indexOf("Mid-air collision occurred while submitting") != -1);
			return;
		}
		fail("Mid-air collision expected");
	}

	public void testAuthenticationCredentials() throws Exception {
		init218();
		ITask task = generateLocalTaskAndDownload("3");
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
		init222();

		// Get the task
		ITask task = generateLocalTaskAndDownload("3");
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
		init(IBugzillaConstants.ECLIPSE_BUGZILLA_URL);
		//repository.setAuthenticationCredentials("username", "password");
		String queryString = "https://bugs.eclipse.org/bugs/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=&classification=Tools&product=Mylyn&component=Bugzilla&long_desc_type=allwordssubstr&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&status_whiteboard_type=allwordssubstr&status_whiteboard=&keywords_type=allwords&keywords=&bug_status=NEW&priority=P1&priority=P2&emailtype1=substring&email1=&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=";
		RepositoryQuery query = new RepositoryQuery(BugzillaCorePlugin.CONNECTOR_KIND, "test");
		query.setRepositoryUrl(repository.getRepositoryUrl());
		query.setUrl(queryString);
		TasksUiPlugin.getTaskList().addQuery(query);

		TasksUiInternal.synchronizeQuery(connector, query, null, true);

		for (ITask task : query.getChildren()) {
			assertTrue(task.getSynchronizationState() == SynchronizationState.INCOMING);
			TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
			assertTrue(task.getSynchronizationState() == SynchronizationState.SYNCHRONIZED);
		}

		repository.setSynchronizationTimeStamp("1970-01-01");//getSynchronizationTimeStamp();
		TasksUiInternal.synchronizeQuery(connector, query, null, true);
		for (ITask task : query.getChildren()) {
			assertTrue(task.getSynchronizationState() == SynchronizationState.INCOMING);
		}
	}
}
