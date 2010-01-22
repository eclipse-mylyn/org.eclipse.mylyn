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
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaStatus;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTaskDataHandler;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskAttachment;
import org.eclipse.mylyn.internal.tasks.core.data.FileTaskAttachmentSource;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tests.util.TestUtil.PrivilegeLevel;

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

	public void testAttachToExistingReport() throws Exception {
		TaskData taskData = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		assertNotNull(taskData);
		int numAttached = taskData.getAttributeMapper()
				.getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT)
				.size();

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
			client.postAttachment(taskData.getTaskId(), attachmentMapper.getComment(), attachment, attrAttachment,
					new NullProgressMonitor());
			fail("never reach this!");
		} catch (Exception e) {
			assertEquals("An unknown repository error has occurred: ", e.getMessage());
		}

		taskData = BugzillaFixture.current().getTask(taskData.getTaskId(), client);
		assertNotNull(taskData);
		assertEquals(numAttached, taskData.getAttributeMapper().getAttributesByType(taskData,
				TaskAttribute.TYPE_ATTACHMENT).size());

		/* Test attempt to upload an empty file */
		String fileName = "test-attach-" + System.currentTimeMillis() + ".txt";
		File attachFile = new File(fileName);
		attachFile.createNewFile();
		BufferedWriter write = new BufferedWriter(new FileWriter(attachFile));

		attachment = new FileTaskAttachmentSource(attachFile);
		attachment.setContentType(FileTaskAttachmentSource.APPLICATION_OCTET_STREAM);
		attachment.setDescription(AttachmentUtil.CONTEXT_DESCRIPTION);
		attachment.setName("mylyn-context.zip");

		try {
			client.postAttachment(taskData.getTaskId(), attachmentMapper.getComment(), attachment, attrAttachment,
					new NullProgressMonitor());
			fail("never reach this!");
		} catch (Exception e) {
			assertEquals("An unknown repository error has occurred: ", e.getMessage());
		}

		taskData = BugzillaFixture.current().getTask(taskData.getTaskId(), client);
		assertNotNull(taskData);
		assertEquals(numAttached, taskData.getAttributeMapper().getAttributesByType(taskData,
				TaskAttribute.TYPE_ATTACHMENT).size());

		/* Test uploading a proper file */
		write.write("test file");
		write.close();
		try {
			client.postAttachment(taskData.getTaskId(), attachmentMapper.getComment(), attachment, attrAttachment,
					new NullProgressMonitor());
		} catch (Exception e) {
			fail("never reach this!");
		}

		taskData = BugzillaFixture.current().getTask(taskData.getTaskId(), client);
		assertNotNull(taskData);
		assertEquals(numAttached + 1, taskData.getAttributeMapper().getAttributesByType(taskData,
				TaskAttribute.TYPE_ATTACHMENT).size());
		// use assertion to track clean-up
		assertTrue(attachFile.delete());
	}

	public void testAttachmentToken() throws Exception {
		TaskData taskData = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		assertNotNull(taskData);

		doAttachment(taskData);

		taskData = BugzillaFixture.current().getTask(taskData.getTaskId(), client);
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

		taskData = BugzillaFixture.current().getTask(taskData.getTaskId(), client);
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

	private void doAttachment(TaskData taskData) throws Exception {
		TaskAttribute attrAttachment = taskData.getAttributeMapper().createTaskAttachment(taskData);
		TaskAttachmentMapper attachmentMapper = TaskAttachmentMapper.createFrom(attrAttachment);

		/* Initialize a local attachment */
		attachmentMapper.setDescription("Test attachment " + new Date());
		attachmentMapper.setContentType("text/plain");
		attachmentMapper.setPatch(false);
		attachmentMapper.setComment("Automated JUnit attachment test");
		attachmentMapper.applyTo(attrAttachment);

		String fileName = "test-attach-" + System.currentTimeMillis() + ".txt";
		File attachFile = new File(fileName);
		attachFile.createNewFile();
		BufferedWriter write = new BufferedWriter(new FileWriter(attachFile));
		/* Test uploading a proper file */
		write.write("test file");
		write.close();

		FileTaskAttachmentSource attachment = new FileTaskAttachmentSource(attachFile);
		attachment.setContentType(FileTaskAttachmentSource.APPLICATION_OCTET_STREAM);
		attachment.setDescription(AttachmentUtil.CONTEXT_DESCRIPTION);
		attachment.setName("mylyn-context.zip");

		try {
			client.postAttachment(taskData.getTaskId(), attachmentMapper.getComment(), attachment, attrAttachment,
					new NullProgressMonitor());
		} catch (Exception e) {
			fail("never reach this!");
		}

	}

	public void testObsoleteAttachment() throws Exception {
		TaskData taskData = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		assertNotNull(taskData);

		doAttachment(taskData);

		taskData = BugzillaFixture.current().getTask(taskData.getTaskId(), client);
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

		taskData = BugzillaFixture.current().getTask(taskData.getTaskId(), client);
		assertNotNull(taskData);
		attachment = taskData.getAttributeMapper().getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT).get(0);
		assertNotNull(attachment);
		obsolete = attachment.getMappedAttribute(TaskAttribute.ATTACHMENT_IS_DEPRECATED);
		assertNotNull(obsolete);
		boolean newObsoleteOn = obsolete.getValue().equals("1");
		assertEquals(true, oldObsoleteOn != newObsoleteOn);
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
		TaskData taskData = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		assertNotNull(taskData);
		ITask task = TasksUi.getRepositoryModel().createTask(repository, taskData.getTaskId());
		TasksUiPlugin.getTaskList().addTask(task);
		TasksUi.getTaskActivityManager().activateTask(task);
		File sourceContextFile = ContextCorePlugin.getContextStore().getFileForContext(
				RepositoryTaskHandleUtil.getHandle(repository.getRepositoryUrl(), taskData.getTaskId()));
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
			assertTrue(e.getMessage().contains("invalid username or password"));
			assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
			return;
		}
		fail("Should have failed due to invalid userid and password.");
	}
}
