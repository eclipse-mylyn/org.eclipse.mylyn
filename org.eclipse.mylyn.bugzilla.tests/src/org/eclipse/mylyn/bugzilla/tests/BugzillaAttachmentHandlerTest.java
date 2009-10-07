/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaStatus;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTaskDataHandler;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.tasks.core.TaskAttachment;
import org.eclipse.mylyn.internal.tasks.core.data.FileTaskAttachmentSource;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Robert Elves
 * @author Frank Becker
 */
public class BugzillaAttachmentHandlerTest extends AbstractBugzillaTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAttachmentToken323() throws Exception {
		init323();
		ITask task = generateLocalTaskAndDownload("2");
		assertNotNull(task);
		TaskDataModel model = createModel(task);
		TaskData taskData = model.getTaskData();
		assertNotNull(taskData);
		TaskAttribute attachment = taskData.getAttributeMapper().getAttributesByType(taskData,
				TaskAttribute.TYPE_ATTACHMENT).get(0);
		assertNotNull(attachment);
		TaskAttribute obsolete = attachment.getMappedAttribute(TaskAttribute.ATTACHMENT_IS_DEPRECATED);
		assertNotNull(obsolete);
		TaskAttribute token = attachment.getAttribute(BugzillaAttribute.TOKEN.getKey());
		assertNotNull(token);
		attachment.removeAttribute(BugzillaAttribute.TOKEN.getKey());
		token = attachment.getAttribute(BugzillaAttribute.TOKEN.getKey());
		assertNull(token);
		boolean oldObsoleteOn = obsolete.getValue().equals("1");
		if (oldObsoleteOn) {
			obsolete.setValue("0"); //$NON-NLS-1$
		} else {
			obsolete.setValue("1"); //$NON-NLS-1$
		}
		try {
			((BugzillaTaskDataHandler) connector.getTaskDataHandler()).postUpdateAttachment(repository, attachment,
					"update", new NullProgressMonitor());
			fail("CoreException expected but not reached");
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			IStatus status = e.getStatus();
			assertTrue(status instanceof BugzillaStatus);
			assertEquals(IBugzillaConstants.REPOSITORY_STATUS_SUSPICIOUS_ACTION, status.getCode());
		}

		task = generateLocalTaskAndDownload("2");
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);
		attachment = taskData.getAttributeMapper().getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT).get(0);
		assertNotNull(attachment);
		obsolete = attachment.getMappedAttribute(TaskAttribute.ATTACHMENT_IS_DEPRECATED);
		assertNotNull(obsolete);
		token = attachment.getAttribute(BugzillaAttribute.TOKEN.getKey());
		assertNotNull(token);
		oldObsoleteOn = obsolete.getValue().equals("1");
		if (oldObsoleteOn) {
			obsolete.setValue("0"); //$NON-NLS-1$
		} else {
			obsolete.setValue("1"); //$NON-NLS-1$
		}
		try {
			((BugzillaTaskDataHandler) connector.getTaskDataHandler()).postUpdateAttachment(repository, attachment,
					"update", new NullProgressMonitor());
		} catch (CoreException e) {
			fail("CoreException expected reached");
		}

	}

	public void testObsoleteAttachment222() throws Exception {
		init222();
		doObsoleteAttachment("81");
	}

	public void testObsoleteAttachment32() throws Exception {
		init32();
		doObsoleteAttachment("2");
	}

	private void doObsoleteAttachment(String taskNumber) throws CoreException {
		ITask task = generateLocalTaskAndDownload(taskNumber);
		assertNotNull(task);
		TaskDataModel model = createModel(task);
		TaskData taskData = model.getTaskData();
		assertNotNull(taskData);
		TaskAttribute attachment = taskData.getAttributeMapper().getAttributesByType(taskData,
				TaskAttribute.TYPE_ATTACHMENT).get(0);
		assertNotNull(attachment);
		TaskAttribute obsolete = attachment.getMappedAttribute(TaskAttribute.ATTACHMENT_IS_DEPRECATED);
		assertNotNull(obsolete);
		boolean oldObsoleteOn = obsolete.getValue().equals("1");
		if (oldObsoleteOn) {
			obsolete.setValue("0"); //$NON-NLS-1$
		} else {
			obsolete.setValue("1"); //$NON-NLS-1$
		}
		((BugzillaTaskDataHandler) connector.getTaskDataHandler()).postUpdateAttachment(repository, attachment,
				"update", new NullProgressMonitor()); //$NON-NLS-1$

		task = generateLocalTaskAndDownload(taskNumber);
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);
		attachment = taskData.getAttributeMapper().getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT).get(0);
		assertNotNull(attachment);
		obsolete = attachment.getMappedAttribute(TaskAttribute.ATTACHMENT_IS_DEPRECATED);
		assertNotNull(obsolete);
		boolean newObsoleteOn = obsolete.getValue().equals("1");
		assertEquals(true, oldObsoleteOn != newObsoleteOn);
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
		int numAttached = taskData.getAttributeMapper()
				.getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT)
				.size();
		String fileName = "test-attach-" + System.currentTimeMillis() + ".txt";

		assertNotNull(repository.getCredentials(AuthenticationType.REPOSITORY));
		assertNotNull(repository.getCredentials(AuthenticationType.REPOSITORY).getUserName());
		assertNotNull(repository.getCredentials(AuthenticationType.REPOSITORY).getPassword());
		BugzillaClient client = connector.getClientManager().getClient(repository, new NullProgressMonitor());

		TaskAttribute attrAttachment = taskData.getAttributeMapper().createTaskAttachment(taskData);
		TaskAttachmentMapper attachmentMapper = TaskAttachmentMapper.createFrom(attrAttachment);

		/* Initialize a local attachment */
		attachmentMapper.setDescription("Test attachment " + new Date());
		attachmentMapper.setContentType("text/plain");
		attachmentMapper.setPatch(false);
		attachmentMapper.setComment("Automated JUnit attachment test");
		attachmentMapper.applyTo(attrAttachment);

		/* Test attempt to upload a non-existent file */
		String filePath = "/this/is/not/a/real-file";

		FileTaskAttachmentSource attachment = new FileTaskAttachmentSource(new File(filePath));
		attachment.setContentType(FileTaskAttachmentSource.APPLICATION_OCTET_STREAM);
		attachment.setDescription(AttachmentUtil.CONTEXT_DESCRIPTION);
		attachment.setName("mylyn-context.zip");

		try {
			client.postAttachment(taskNumber, attachmentMapper.getComment(), attachment, attrAttachment,
					new NullProgressMonitor());
			fail("never reach this!");
		} catch (Exception e) {
			assertEquals("A repository error has occurred.", e.getMessage());
		}
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());

		task = TasksUi.getRepositoryModel().createTask(repository, taskNumber);
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);
		assertEquals(numAttached, taskData.getAttributeMapper().getAttributesByType(taskData,
				TaskAttribute.TYPE_ATTACHMENT).size());

		/* Test attempt to upload an empty file */
		File attachFile = new File(fileName);
		attachFile.createNewFile();
		BufferedWriter write = new BufferedWriter(new FileWriter(attachFile));

		attachment = new FileTaskAttachmentSource(attachFile);
		attachment.setContentType(FileTaskAttachmentSource.APPLICATION_OCTET_STREAM);
		attachment.setDescription(AttachmentUtil.CONTEXT_DESCRIPTION);
		attachment.setName("mylyn-context.zip");

		try {
			client.postAttachment(taskNumber, attachmentMapper.getComment(), attachment, attrAttachment,
					new NullProgressMonitor());
			fail("never reach this!");
		} catch (Exception e) {
			assertEquals("A repository error has occurred.", e.getMessage());
		}
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());

		task = generateLocalTaskAndDownload(taskNumber);
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);
		assertEquals(numAttached, taskData.getAttributeMapper().getAttributesByType(taskData,
				TaskAttribute.TYPE_ATTACHMENT).size());

		/* Test uploading a proper file */
		write.write("test file");
		write.close();
		try {
			client.postAttachment(taskNumber, attachmentMapper.getComment(), attachment, attrAttachment,
					new NullProgressMonitor());
		} catch (Exception e) {
			fail("never reach this!");
		}
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());

		task = generateLocalTaskAndDownload(taskNumber);
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);
		assertEquals(numAttached + 1, taskData.getAttributeMapper().getAttributesByType(taskData,
				TaskAttribute.TYPE_ATTACHMENT).size());
		// use assertion to track clean-up
		assertTrue(attachFile.delete());
	}

	/**
	 * Ensure obsoletes and patches are marked as such by the parser.
	 */
	public void testAttachmentAttributes() throws Exception {
		init222();
		String taskNumber = "19";
		ITask task = generateLocalTaskAndDownload(taskNumber);
		assertNotNull(task);
		TaskDataModel model = createModel(task);
		TaskData taskData = model.getTaskData();
		assertNotNull(taskData);

		boolean isPatch[] = { false, true, false, false, false, false, false, true, false, false, false };
		boolean isObsolete[] = { false, true, false, true, false, false, false, false, false, false, false };

		int index = 0;
		for (TaskAttribute attribute : taskData.getAttributeMapper().getAttributesByType(taskData,
				TaskAttribute.TYPE_ATTACHMENT)) {
			assertTrue(validateAttachmentAttributes(model, attribute, isPatch[index], isObsolete[index]));
			index++;
		}
	}

	private boolean validateAttachmentAttributes(TaskDataModel model, TaskAttribute taskAttribute, boolean isPatch,
			boolean isObsolete) {
		TaskAttachment taskAttachment = new TaskAttachment(model.getTaskRepository(), model.getTask(), taskAttribute);
		model.getTaskData().getAttributeMapper().updateTaskAttachment(taskAttachment, taskAttribute);
		return (taskAttachment.isPatch() == isPatch) && (taskAttachment.isDeprecated() == isObsolete);
	}

	public void testContextAttachFailure() throws Exception {
		init218();
		ITask task = this.generateLocalTaskAndDownload("3");
		assertNotNull(task);
		TaskDataModel model = createModel(task);
		TaskData taskData = model.getTaskData();
		assertNotNull(taskData);
//		assertNotNull(TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(),
//				task.getTaskId()));
		TasksUi.getTaskActivityManager().activateTask(task);
		File sourceContextFile = ContextCorePlugin.getContextStore().getFileForContext(task.getHandleIdentifier());
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
		sourceContextFile.createNewFile();
		sourceContextFile.deleteOnExit();
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("wrong", "wrong"), false);
		try {
			FileTaskAttachmentSource attachment = new FileTaskAttachmentSource(sourceContextFile);
			attachment.setContentType(FileTaskAttachmentSource.APPLICATION_OCTET_STREAM);

			attachment.setDescription(AttachmentUtil.CONTEXT_DESCRIPTION);
			attachment.setName("mylyn-context.zip");

			TaskAttribute attrAttachment = taskData.getAttributeMapper().createTaskAttachment(taskData);
			TaskAttachmentMapper attachmentMapper = TaskAttachmentMapper.createFrom(attrAttachment);

			/* Initialize a local attachment */
			attachmentMapper.setDescription("Test attachment " + new Date());
			attachmentMapper.setContentType(AttachmentUtil.CONTEXT_DESCRIPTION);
			attachmentMapper.setPatch(false);
			attachmentMapper.setComment("Context attachment failure Test");
			attachmentMapper.applyTo(attrAttachment);

			connector.getTaskAttachmentHandler().postContent(repository, task, attachment,
					attachmentMapper.getComment(), attrAttachment, new NullProgressMonitor());
		} catch (CoreException e) {
			assertEquals(
					"Unable to login to http://mylyn.eclipse.org/bugs218.\n\ninvalid username or password \n\nPlease validate credentials via Task Repositories view.",
					e.getMessage());
			assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
			return;
		}
		fail("Should have failed due to invalid userid and password.");
	}
}
