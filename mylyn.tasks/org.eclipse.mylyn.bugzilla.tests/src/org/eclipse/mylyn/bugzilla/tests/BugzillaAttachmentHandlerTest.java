/*******************************************************************************
 * Copyright (c) 2009, 2016 Tasktop Technologies and others.
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

import static org.junit.Assume.assumeTrue;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaTestSupportUtil;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaStatus;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTaskDataHandler;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskAttachment;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.core.data.FileTaskAttachmentSource;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Robert Elves
 * @author Frank Becker
 */
public class BugzillaAttachmentHandlerTest extends AbstractBugzillaTest {

	@SuppressWarnings("null")
	public void testUpdateAttachmentFlags() throws Exception {
		TaskData taskData = BugzillaFixture.current()
				.createTask(PrivilegeLevel.USER, "update of Attachment Flags",
						"description for testUpdateAttachmentFlags");
		assertNotNull(taskData);
		int numAttached = taskData.getAttributeMapper()
				.getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT)
				.size();
		assertEquals(0, numAttached);
		assertNotNull(repository.getCredentials(AuthenticationType.REPOSITORY));
		assertNotNull(repository.getCredentials(AuthenticationType.REPOSITORY).getUserName());
		assertNotNull(repository.getCredentials(AuthenticationType.REPOSITORY).getPassword());
		BugzillaClient client = connector.getClientManager().getClient(repository, new NullProgressMonitor());

		TaskAttribute attrAttachment = taskData.getAttributeMapper().createTaskAttachment(taskData);
		TaskAttachmentMapper attachmentMapper = TaskAttachmentMapper.createFrom(attrAttachment);
		attachmentMapper.setComment("test Update AttachmentFlags");

		/* Test uploading a proper file */
		String fileName = "test-attach-1.txt";
		File attachFile = new File(fileName);
		attachFile.createNewFile();
		attachFile.deleteOnExit();
		BufferedWriter write = new BufferedWriter(new FileWriter(attachFile));
		write.write("test file from " + System.currentTimeMillis());
		write.close();

		FileTaskAttachmentSource attachment = new FileTaskAttachmentSource(attachFile);
		attachment.setContentType("text/plain");
		attachment.setDescription("Description");
		attachment.setName("My Attachment 1");

		client.postAttachment(taskData.getTaskId(), attachmentMapper.getComment(), attachment, attrAttachment,
				new NullProgressMonitor());
		taskData = BugzillaFixture.current().getTask(taskData.getTaskId(), client);
		assertNotNull(taskData);
		numAttached = taskData.getAttributeMapper().getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT).size();
		assertEquals(1, numAttached);
		TaskAttribute attachmentAttribute = taskData.getAttributeMapper()
				.getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT)
				.get(0);
		int flagCount = 0;
		int flagCountUnused = 0;
		TaskAttribute attachmentFlag1 = null;
		TaskAttribute attachmentFlag2 = null;
		for (TaskAttribute attribute : attachmentAttribute.getAttributes().values()) {
			if (!attribute.getId().startsWith(BugzillaAttribute.KIND_FLAG)) {
				continue;
			}
			flagCount++;
			if (attribute.getId().startsWith(BugzillaAttribute.KIND_FLAG_TYPE)) {
				flagCountUnused++;
				TaskAttribute stateAttribute = taskData.getAttributeMapper().getAssoctiatedAttribute(attribute);
				if (stateAttribute.getMetaData().getLabel().equals("AttachmentFlag1")) {
					attachmentFlag1 = attribute;
				}
				if (stateAttribute.getMetaData().getLabel().equals("AttachmentFlag2")) {
					attachmentFlag2 = attribute;
				}
			}
		}
		assertEquals(2, flagCount);
		assertEquals(2, flagCountUnused);
		assertNotNull(attachmentFlag1);
		assertNotNull(attachmentFlag2);
		TaskAttribute stateAttribute1 = taskData.getAttributeMapper().getAssoctiatedAttribute(attachmentFlag1);
		stateAttribute1.setValue("?");
		TaskAttribute requestee = attachmentFlag1.getAttribute("requestee"); //$NON-NLS-1$
		requestee.setValue("guest@mylyn.eclipse.org");
		client.postUpdateAttachment(attachmentAttribute, "update", null);
		taskData = BugzillaFixture.current().getTask(taskData.getTaskId(), client);
		assertNotNull(taskData);
		attachmentAttribute = taskData.getAttributeMapper()
				.getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT)
				.get(0);
		assertNotNull(attachmentAttribute);
		flagCount = 0;
		flagCountUnused = 0;
		attachmentFlag1 = null;
		attachmentFlag2 = null;
		TaskAttribute attachmentFlag1used = null;
		TaskAttribute attachmentFlag2used = null;

		for (TaskAttribute attribute : attachmentAttribute.getAttributes().values()) {
			if (!attribute.getId().startsWith(BugzillaAttribute.KIND_FLAG)) {
				continue;
			}
			flagCount++;
			if (attribute.getId().startsWith(BugzillaAttribute.KIND_FLAG_TYPE)) {
				flagCountUnused++;
				TaskAttribute stateAttribute = taskData.getAttributeMapper().getAssoctiatedAttribute(attribute);
				if (stateAttribute.getMetaData().getLabel().equals("AttachmentFlag1")) {
					attachmentFlag1 = attribute;
				}
				if (stateAttribute.getMetaData().getLabel().equals("AttachmentFlag2")) {
					attachmentFlag2 = attribute;
				}
			} else {
				TaskAttribute stateAttribute = taskData.getAttributeMapper().getAssoctiatedAttribute(attribute);
				if (stateAttribute.getMetaData().getLabel().equals("AttachmentFlag1")) {
					attachmentFlag1used = attribute;
				}
				if (stateAttribute.getMetaData().getLabel().equals("AttachmentFlag2")) {
					attachmentFlag2used = attribute;
				}
			}

		}
		assertEquals(3, flagCount);
		assertEquals(2, flagCountUnused);
		assertNotNull(attachmentFlag1);
		assertNotNull(attachmentFlag2);
		assertNotNull(attachmentFlag1used);
		assertNull(attachmentFlag2used);
		TaskAttribute stateAttribute1used = taskData.getAttributeMapper().getAssoctiatedAttribute(attachmentFlag1used);
		TaskAttribute requesteeused = attachmentFlag1used.getAttribute("requestee"); //$NON-NLS-1$
		assertNotNull(stateAttribute1used);
		assertNotNull(requesteeused);
		assertEquals("?", stateAttribute1used.getValue());
		assertEquals("guest@mylyn.eclipse.org", requesteeused.getValue());
		stateAttribute1used.setValue(" ");
		client.postUpdateAttachment(attachmentAttribute, "update", null);
		taskData = BugzillaFixture.current().getTask(taskData.getTaskId(), client);
		assertNotNull(taskData);
		attachmentAttribute = taskData.getAttributeMapper()
				.getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT)
				.get(0);
		assertNotNull(attachmentAttribute);
		flagCount = 0;
		flagCountUnused = 0;
		attachmentFlag1 = null;
		attachmentFlag2 = null;
		attachmentFlag1used = null;
		attachmentFlag2used = null;

		for (TaskAttribute attribute : attachmentAttribute.getAttributes().values()) {
			if (!attribute.getId().startsWith(BugzillaAttribute.KIND_FLAG)) {
				continue;
			}
			flagCount++;
			if (attribute.getId().startsWith(BugzillaAttribute.KIND_FLAG_TYPE)) {
				flagCountUnused++;
				TaskAttribute stateAttribute = taskData.getAttributeMapper().getAssoctiatedAttribute(attribute);
				if (stateAttribute.getMetaData().getLabel().equals("AttachmentFlag1")) {
					attachmentFlag1 = attribute;
				}
				if (stateAttribute.getMetaData().getLabel().equals("AttachmentFlag2")) {
					attachmentFlag2 = attribute;
				}
			} else {
				TaskAttribute stateAttribute = taskData.getAttributeMapper().getAssoctiatedAttribute(attribute);
				if (stateAttribute.getMetaData().getLabel().equals("AttachmentFlag1")) {
					attachmentFlag1used = attribute;
				}
				if (stateAttribute.getMetaData().getLabel().equals("AttachmentFlag2")) {
					attachmentFlag2used = attribute;
				}
			}

		}
		assertEquals(2, flagCount);
		assertEquals(2, flagCountUnused);
		assertNotNull(attachmentFlag1);
		assertNotNull(attachmentFlag2);
		assertNull(attachmentFlag1used);
		assertNull(attachmentFlag2used);
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
			assertFileEmptyError(e);
		}

		taskData = BugzillaFixture.current().getTask(taskData.getTaskId(), client);
		assertNotNull(taskData);
		assertEquals(numAttached,
				taskData.getAttributeMapper().getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT).size());

		/* Test attempt to upload an empty file */
		String fileName = "test-attach-" + System.currentTimeMillis() + ".txt";
		File attachFile = new File(fileName);
		attachFile.createNewFile();
		attachFile.deleteOnExit();
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
			assertFileEmptyError(e);
		}

		taskData = BugzillaFixture.current().getTask(taskData.getTaskId(), client);
		assertNotNull(taskData);
		assertEquals(numAttached,
				taskData.getAttributeMapper().getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT).size());

		/* Test uploading a proper file */
		write.write("test file");
		write.close();
		client.postAttachment(taskData.getTaskId(), attachmentMapper.getComment(), attachment, attrAttachment,
				new NullProgressMonitor());

		taskData = BugzillaFixture.current().getTask(taskData.getTaskId(), client);
		assertNotNull(taskData);
		assertEquals(numAttached + 1,
				taskData.getAttributeMapper().getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT).size());
		// use assertion to track clean-up
		assertTrue(attachFile.delete());
	}

	public void testAttachmentWithUnicode() throws Exception {
		String osName = System.getProperty("os.name").toLowerCase();
		assumeTrue(!osName.startsWith("mac os x"));
		// macos X with APFS can not handle this
		testAttachmentWithSpecialCharacters(
				"\u00E7\u00F1\u00A5\u20AC\u00A3\u00BD\u00BC\u03B2\u03B8\u53F0\u5317\u3096\u3097\uFF73");
	}

	public void testAttachmentWithSpecialCharacters() throws Exception {
		testAttachmentWithSpecialCharacters("~`!@#$%^&()_-+={[}];',");
	}

	private void testAttachmentWithSpecialCharacters(String specialCharacters) throws Exception {
		TaskData taskData = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		assertNotNull(taskData);

		TaskAttribute attachmentAttr = taskData.getAttributeMapper().createTaskAttachment(taskData);
		TaskAttachmentMapper attachmentMapper = TaskAttachmentMapper.createFrom(attachmentAttr);

		String description = "Test attachment " + specialCharacters + System.currentTimeMillis();
		attachmentMapper.setDescription(description);
		attachmentMapper.setContentType("text/plain");
		attachmentMapper.setPatch(false);
		attachmentMapper.applyTo(attachmentAttr);

		String filename = "test" + specialCharacters + System.currentTimeMillis() + ".txt";
		File attachFile = new File(filename);
		attachFile.createNewFile();
		attachFile.deleteOnExit();
		BufferedWriter write = new BufferedWriter(new FileWriter(attachFile));
		write.write("test file content");
		write.close();

		FileTaskAttachmentSource attachment = new FileTaskAttachmentSource(attachFile);
		attachment.setContentType("text/plain");
		attachment.setDescription(description);
		attachment.setName(filename);

		client.postAttachment(taskData.getTaskId(), attachmentMapper.getComment(), attachment, attachmentAttr,
				new NullProgressMonitor());

		taskData = BugzillaFixture.current().getTask(taskData.getTaskId(), client);
		assertNotNull(taskData);
		List<TaskAttribute> attachmentAttrs = taskData.getAttributeMapper()
				.getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT);
		assertEquals(1, attachmentAttrs.size());

		attachmentMapper = TaskAttachmentMapper.createFrom(attachmentAttrs.get(0));
		assertEquals(description, attachmentMapper.getDescription());
		assertEquals(filename, attachmentMapper.getFileName());
		assertEquals("text/plain", attachmentMapper.getContentType());
		assertEquals(Boolean.FALSE, attachmentMapper.isPatch());

		assertTrue(attachFile.delete());
	}

	private void assertFileEmptyError(Exception e) {
		if (BugzillaFixture.current().getBugzillaVersion().compareTo(BugzillaVersion.BUGZILLA_4_5_2) >= 0) {
			assertEquals("An unknown repository error has occurred: file is empty", e.getMessage());
		} else {
			assertEquals(
					"file is empty:  The file you are trying to attach is empty, does not exist, or you don't have permission to read it.",
					e.getMessage());
		}
	}

	public void testAttachmentToken() throws Exception {
		TaskData taskData = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		assertNotNull(taskData);

		doAttachment(taskData);

		taskData = BugzillaFixture.current().getTask(taskData.getTaskId(), client);
		assertNotNull(taskData);

		TaskAttribute attachment = taskData.getAttributeMapper()
				.getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT)
				.get(0);
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
		((BugzillaTaskDataHandler) connector.getTaskDataHandler()).postUpdateAttachment(repository, attachment,
				"update", new NullProgressMonitor());

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
		attachFile.deleteOnExit();
		BufferedWriter write = new BufferedWriter(new FileWriter(attachFile));
		/* Test uploading a proper file */
		write.write("test file");
		write.close();

		FileTaskAttachmentSource attachment = new FileTaskAttachmentSource(attachFile);
		attachment.setContentType(FileTaskAttachmentSource.APPLICATION_OCTET_STREAM);
		attachment.setDescription(AttachmentUtil.CONTEXT_DESCRIPTION);
		attachment.setName("mylyn-context.zip");

		client.postAttachment(taskData.getTaskId(), attachmentMapper.getComment(), attachment, attrAttachment,
				new NullProgressMonitor());
	}

	public void testObsoleteAttachment() throws Exception {
		TaskData taskData = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		assertNotNull(taskData);

		doAttachment(taskData);

		taskData = BugzillaFixture.current().getTask(taskData.getTaskId(), client);
		assertNotNull(taskData);
		TaskAttribute attachment = taskData.getAttributeMapper()
				.getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT)
				.get(0);
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
		String taskId = harness.taskAttachmentAttributesExists();
		if (taskId == null) {
			taskId = harness.createAttachmentAttributesTask();
		}

		String taskNumber = taskId;
		TaskData taskData = BugzillaFixture.current().getTask(taskNumber, client);
		assertNotNull(taskData);
		ITask task = TasksUi.getRepositoryModel().createTask(repository, taskData.getTaskId());
		boolean isPatch[] = { false, false, true, true };
		boolean isObsolete[] = { false, true, false, true };

		int index = 0;
		for (TaskAttribute attribute : taskData.getAttributeMapper()
				.getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT)) {
			assertTrue(validateAttachmentAttributes(taskData, attribute, isPatch[index], isObsolete[index], task));
			index++;
		}
		assertEquals(4, index);
	}

	private boolean validateAttachmentAttributes(TaskData data, TaskAttribute taskAttribute, boolean isPatch,
			boolean isObsolete, ITask task) {
		TaskAttachment taskAttachment = new TaskAttachment(BugzillaFixture.current().repository(), task, taskAttribute);
		data.getAttributeMapper().updateTaskAttachment(taskAttachment, taskAttribute);
		return (taskAttachment.isPatch() == isPatch) && (taskAttachment.isDeprecated() == isObsolete);
	}

	public void testContextAttachFailure() throws Exception {
		// use the client's repository when setting credentials below
		repository = client.getTaskRepository();
		TaskData taskData = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		assertNotNull(taskData);
		ITask task = TasksUi.getRepositoryModel().createTask(repository, taskData.getTaskId());
		TasksUiPlugin.getTaskList().addTask(task);
		TasksUi.getTaskActivityManager().activateTask(task);
		File sourceContextFile = TasksUiPlugin.getContextStore().getFileForContext(task);
		sourceContextFile.createNewFile();
		sourceContextFile.deleteOnExit();
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("wrong", "wrong"),
				false);
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

			connector.getTaskAttachmentHandler()
					.postContent(repository, task, attachment, attachmentMapper.getComment(), attrAttachment,
							new NullProgressMonitor());
		} catch (CoreException e) {
			assertTrue(BugzillaTestSupportUtil.isInvalidLogon(e));
			assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
			return;
		}
		fail("Should have failed due to invalid userid and password.");
	}

	public void testDownloadAttachmentFile() throws Exception {
		TaskData taskData = BugzillaFixture.current()
				.createTask(PrivilegeLevel.USER, "update of Attachment Flags",
						"description for testUpdateAttachmentFlags");
		assertNotNull(taskData);
		int numAttached = taskData.getAttributeMapper()
				.getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT)
				.size();
		assertEquals(0, numAttached);
		assertNotNull(repository.getCredentials(AuthenticationType.REPOSITORY));
		assertNotNull(repository.getCredentials(AuthenticationType.REPOSITORY).getUserName());
		assertNotNull(repository.getCredentials(AuthenticationType.REPOSITORY).getPassword());
		BugzillaClient client = connector.getClientManager().getClient(repository, new NullProgressMonitor());

		TaskAttribute attrAttachment = taskData.getAttributeMapper().createTaskAttachment(taskData);
		TaskAttachmentMapper attachmentMapper = TaskAttachmentMapper.createFrom(attrAttachment);
		attachmentMapper.setComment("test Update AttachmentFlags");

		/* Test uploading a proper file */
		String fileName = "test-attach-1.txt";
		File attachFile = new File(fileName);
		attachFile.createNewFile();
		attachFile.deleteOnExit();
		BufferedWriter write = new BufferedWriter(new FileWriter(attachFile));
		String expected = "test file from " + System.currentTimeMillis();
		write.write(expected);
		write.close();

		FileTaskAttachmentSource attachment = new FileTaskAttachmentSource(attachFile);
		attachment.setContentType("text/plain");
		attachment.setDescription("Description");
		attachment.setName("My Attachment 1");

		client.postAttachment(taskData.getTaskId(), attachmentMapper.getComment(), attachment, attrAttachment,
				new NullProgressMonitor());
		taskData = BugzillaFixture.current().getTask(taskData.getTaskId(), client);
		assertNotNull(taskData);
		numAttached = taskData.getAttributeMapper().getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT).size();
		assertEquals(1, numAttached);
		TaskAttribute attachmentAttribute = taskData.getAttributeMapper()
				.getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT)
				.get(0);

		File file = File.createTempFile("mylyn", null);
		ITask iTask = new TaskTask(repository.getConnectorKind(), repository.getRepositoryUrl(), taskData.getTaskId());

		ITaskAttachment taskAttachment;
		taskAttachment = new TaskAttachment(repository, iTask, attachmentAttribute);

		try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
			AttachmentUtil.downloadAttachment(taskAttachment, out, new NullProgressMonitor());
		}
		FileInputStream raf = new FileInputStream(file);

		byte[] data = new byte[expected.length()];
		try {
			raf.read(data);
		}

		finally {
			raf.close();
			file.delete();
		}
		assertEquals(expected, new String(data));
	}

	public void testDownloadNonExsistingAttachmentFile() throws Exception {
		TaskData taskData = BugzillaFixture.current()
				.createTask(PrivilegeLevel.USER, "update of Attachment Flags",
						"description for testUpdateAttachmentFlags");
		assertNotNull(taskData);
		int numAttached = taskData.getAttributeMapper()
				.getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT)
				.size();
		assertEquals(0, numAttached);
		assertNotNull(repository.getCredentials(AuthenticationType.REPOSITORY));
		assertNotNull(repository.getCredentials(AuthenticationType.REPOSITORY).getUserName());
		assertNotNull(repository.getCredentials(AuthenticationType.REPOSITORY).getPassword());
		BugzillaClient client = connector.getClientManager().getClient(repository, new NullProgressMonitor());

		TaskAttribute attrAttachment = taskData.getAttributeMapper().createTaskAttachment(taskData);
		TaskAttachmentMapper attachmentMapper = TaskAttachmentMapper.createFrom(attrAttachment);
		attachmentMapper.setComment("test Update AttachmentFlags");

		/* Test uploading a proper file */
		String fileName = "test-attach-1.txt";
		File attachFile = new File(fileName);
		attachFile.createNewFile();
		attachFile.deleteOnExit();
		BufferedWriter write = new BufferedWriter(new FileWriter(attachFile));
		String expected = "test file from " + System.currentTimeMillis();
		write.write(expected);
		write.close();

		FileTaskAttachmentSource attachment = new FileTaskAttachmentSource(attachFile);
		attachment.setContentType("text/plain");
		attachment.setDescription("Description");
		attachment.setName("My Attachment 1");

		client.postAttachment(taskData.getTaskId(), attachmentMapper.getComment(), attachment, attrAttachment,
				new NullProgressMonitor());
		taskData = BugzillaFixture.current().getTask(taskData.getTaskId(), client);
		assertNotNull(taskData);
		numAttached = taskData.getAttributeMapper().getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT).size();
		assertEquals(1, numAttached);
		TaskAttribute attachmentAttribute = taskData.getAttributeMapper()
				.getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT)
				.get(0);

		attachmentAttribute.setValue("99999999");
		File file = File.createTempFile("mylyn", null);
		ITask iTask = new TaskTask(repository.getConnectorKind(), repository.getRepositoryUrl(), taskData.getTaskId());

		ITaskAttachment taskAttachment;
		taskAttachment = new TaskAttachment(repository, iTask, attachmentAttribute);

		try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
			AttachmentUtil.downloadAttachment(taskAttachment, out, new NullProgressMonitor());
		} catch (CoreException e) {
			String message = e.getMessage();
			assertTrue(message.startsWith("invalid attachment id: "));
		}
	}
}
