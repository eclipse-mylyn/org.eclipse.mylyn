/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.context.tests.support.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.TracTask;
import org.eclipse.mylyn.internal.trac.core.ITracClient.Version;
import org.eclipse.mylyn.tasks.core.AbstractAttachmentHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.FileAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.trac.tests.support.TestFixture;
import org.eclipse.mylyn.trac.tests.support.XmlRpcServer.TestData;
import org.eclipse.mylyn.web.core.AuthenticationCredentials;
import org.eclipse.mylyn.web.core.AuthenticationType;

/**
 * @author Steffen Pingel
 */
public class TracAttachmentHandlerTest extends TestCase {

	private TaskRepository repository;

	private TaskRepositoryManager manager;

	private TracRepositoryConnector connector;

	private AbstractAttachmentHandler attachmentHandler;

	private TestData data;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		manager = TasksUiPlugin.getRepositoryManager();
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		data = TestFixture.init010();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	protected void init(String url, Version version) {
		String kind = TracCorePlugin.REPOSITORY_KIND;
		Credentials credentials = TestUtil.readCredentials(PrivilegeLevel.USER);

		repository = new TaskRepository(kind, url);
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials(credentials.username,
				credentials.password), false);
		repository.setTimeZoneId(ITracClient.TIME_ZONE);
		repository.setCharacterEncoding(ITracClient.CHARSET);
		repository.setVersion(version.name());

		manager.addRepository(repository, TasksUiPlugin.getDefault().getRepositoriesFilePath());

		AbstractRepositoryConnector abstractConnector = manager.getRepositoryConnector(kind);
		connector = (TracRepositoryConnector) abstractConnector;

		attachmentHandler = connector.getAttachmentHandler();
	}

	public void testDownloadAttachmentXmlRpc010() throws Exception {
		downloadAttachmentXmlRpc(TracTestConstants.TEST_TRAC_010_URL);
	}

	public void testDownloadAttachmentXmlRpc011() throws Exception {
		downloadAttachmentXmlRpc(TracTestConstants.TEST_TRAC_011_URL);
	}

	private void downloadAttachmentXmlRpc(String url) throws Exception {
		init(url, Version.XML_RPC);
		TracTask task = (TracTask) TasksUiUtil.createTask(repository, data.attachmentTicketId + "",
				new NullProgressMonitor());
		TasksUi.synchronizeTask(connector, task, true, null);
		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataManager().getNewTaskData(task.getRepositoryUrl(),
				task.getTaskId());

		assertTrue(taskData.getAttachments().size() > 0);
		File file = File.createTempFile("attachment", null);
		file.deleteOnExit();
		attachmentHandler.downloadAttachment(repository, taskData.getAttachments().get(0), new FileOutputStream(file),
				new NullProgressMonitor());

		byte[] result = new byte[6];
		InputStream in = new FileInputStream(file);
		try {
			in.read(result);
			assertEquals("Mylar\n", new String(result));
			assertEquals(-1, in.read());
		} finally {
			in.close();
		}
	}

	public void testGetAttachmentDataXmlRpc010() throws Exception {
		getAttachmentDataXmlRpc(TracTestConstants.TEST_TRAC_010_URL);
	}

	public void testGetAttachmentDataXmlRpc011() throws Exception {
		getAttachmentDataXmlRpc(TracTestConstants.TEST_TRAC_011_URL);
	}

	private void getAttachmentDataXmlRpc(String url) throws Exception {
		init(url, Version.XML_RPC);
		TracTask task = (TracTask) TasksUiUtil.createTask(repository, data.attachmentTicketId + "",
				new NullProgressMonitor());
		TasksUi.synchronizeTask(connector, task, true, null);
		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataManager().getNewTaskData(task.getRepositoryUrl(),
				task.getTaskId());

		assertTrue(taskData.getAttachments().size() > 0);
		InputStream in = attachmentHandler.getAttachmentAsStream(repository, taskData.getAttachments().get(0),
				new NullProgressMonitor());
		byte[] result = new byte[6];
		try {
			in.read(result);
		} finally {
			in.close();
		}
		assertEquals("Mylar\n", new String(result));
	}

	public void testUploadAttachmentXmlRpc010() throws Exception {
		uploadAttachmentXmlRpc(TracTestConstants.TEST_TRAC_010_URL);
	}

	public void testUploadAttachmentXmlRpc011() throws Exception {
		uploadAttachmentXmlRpc(TracTestConstants.TEST_TRAC_011_URL);
	}

	private void uploadAttachmentXmlRpc(String url) throws Exception {
		init(url, Version.XML_RPC);
		TracTask task = (TracTask) TasksUiUtil.createTask(repository, data.attachmentTicketId + "",
				new NullProgressMonitor());
		File file = File.createTempFile("attachment", null);
		file.deleteOnExit();
		OutputStream out = new FileOutputStream(file);
		try {
			out.write("Mylar".getBytes());
		} finally {
			out.close();
		}
		FileAttachment attachment = new FileAttachment(file);
		attachment.setDescription("");
		attachmentHandler.uploadAttachment(repository, task, attachment, "comment", new NullProgressMonitor());

		ITracClient client = connector.getClientManager().getRepository(repository);
		InputStream in = client.getAttachmentData(data.attachmentTicketId, file.getName(), null);
		byte[] result = new byte[5];
		in.read(result);
		assertEquals("Mylar", new String(result));
	}

	public void testCanUploadAttachmentXmlRpc() throws CoreException {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC);
		TracTask task = (TracTask) TasksUiUtil.createTask(repository, data.attachmentTicketId + "", null);
		assertTrue(attachmentHandler.canUploadAttachment(repository, task));
	}

	public void testCanUploadAttachmentWeb() throws CoreException {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.TRAC_0_9);
		TracTask task = (TracTask) TasksUiUtil.createTask(repository, data.attachmentTicketId + "", null);
		assertFalse(attachmentHandler.canUploadAttachment(repository, task));
	}

	public void testCanDownloadAttachmentXmlRpc() throws Exception {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC);
		TracTask task = (TracTask) TasksUiUtil.createTask(repository, data.attachmentTicketId + "", null);
		assertTrue(attachmentHandler.canDownloadAttachment(repository, task));
	}

	public void testCanDownloadAttachmentWeb() throws Exception {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.TRAC_0_9);
		TracTask task = (TracTask) TasksUiUtil.createTask(repository, data.attachmentTicketId + "", null);
		assertFalse(attachmentHandler.canDownloadAttachment(repository, task));
	}

}
