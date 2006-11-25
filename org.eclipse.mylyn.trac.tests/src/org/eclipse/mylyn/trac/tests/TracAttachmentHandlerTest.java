/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.trac.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.context.tests.support.MylarTestUtils;
import org.eclipse.mylar.context.tests.support.MylarTestUtils.Credentials;
import org.eclipse.mylar.context.tests.support.MylarTestUtils.PrivilegeLevel;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.TracCorePlugin;
import org.eclipse.mylar.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylar.internal.trac.core.TracTask;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.TaskRepositoryManager;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.trac.tests.support.TestFixture;
import org.eclipse.mylar.trac.tests.support.XmlRpcServer.TestData;

/**
 * @author Steffen Pingel
 */
public class TracAttachmentHandlerTest extends TestCase {

	private TaskRepository repository;

	private TaskRepositoryManager manager;

	private TracRepositoryConnector connector;

	private IAttachmentHandler attachmentHandler;

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
		Credentials credentials = MylarTestUtils.readCredentials(PrivilegeLevel.USER);

		repository = new TaskRepository(kind, url);
		repository.setAuthenticationCredentials(credentials.username, credentials.password);
		repository.setTimeZoneId(ITracClient.TIME_ZONE);
		repository.setCharacterEncoding(ITracClient.CHARSET);
		repository.setVersion(version.name());

		manager.addRepository(repository, TasksUiPlugin.getDefault().getRepositoriesFilePath());

		AbstractRepositoryConnector abstractConnector = manager.getRepositoryConnector(kind);
		connector = (TracRepositoryConnector) abstractConnector;
		TasksUiPlugin.getSynchronizationManager().setForceSyncExec(true);

		attachmentHandler = connector.getAttachmentHandler();
	}

	public void testDownloadAttachmentXmlRpc() throws Exception {
		init(Constants.TEST_TRAC_010_URL, Version.XML_RPC);
		TracTask task = (TracTask) connector.createTaskFromExistingKey(repository, data.attachmentTicketId + "", null);
		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);
		assertTrue(task.getTaskData().getAttachments().size() > 0);
		File file = File.createTempFile("attachment", null);
		file.deleteOnExit();
		attachmentHandler.downloadAttachment(repository, AbstractRepositoryTask.getTaskId(task.getHandleIdentifier()), task.getTaskData().getAttachments().get(0), file);

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

	public void testUploadAttachmentXmlRpc() throws Exception {
		init(Constants.TEST_TRAC_010_URL, Version.XML_RPC);
		TracTask task = (TracTask) connector.createTaskFromExistingKey(repository, data.attachmentTicketId + "", null);
		File file = File.createTempFile("attachment", null);
		file.deleteOnExit();
		OutputStream out = new FileOutputStream(file);
		try {
			out.write("Mylar".getBytes());
		} finally {
			out.close();
		}
		attachmentHandler.uploadAttachment(repository, task, "comment", "description", file, "", false);

		ITracClient client = connector.getClientManager().getRepository(repository);
		byte[] result = client.getAttachmentData(data.attachmentTicketId, file.getName());
		assertEquals("Mylar", new String(result));
	}

	public void testCanUploadAttachmentXmlRpc() throws CoreException {
		init(Constants.TEST_TRAC_010_URL, Version.XML_RPC);
		TracTask task = (TracTask) connector.createTaskFromExistingKey(repository, data.attachmentTicketId + "", null);
		assertTrue(attachmentHandler.canUploadAttachment(repository, task));
	}

	public void testCanUploadAttachmentWeb() throws CoreException {
		init(Constants.TEST_TRAC_010_URL, Version.TRAC_0_9);
		TracTask task = (TracTask) connector.createTaskFromExistingKey(repository, data.attachmentTicketId + "", null);
		assertFalse(attachmentHandler.canUploadAttachment(repository, task));
	}

	public void testCanDownloadAttachmentXmlRpc() throws Exception {
		init(Constants.TEST_TRAC_010_URL, Version.XML_RPC);
		TracTask task = (TracTask) connector.createTaskFromExistingKey(repository, data.attachmentTicketId + "", null);
		assertTrue(attachmentHandler.canDownloadAttachment(repository, task));
	}
	
	public void testCanDownloadAttachmentWeb() throws Exception {
		init(Constants.TEST_TRAC_010_URL, Version.TRAC_0_9);
		TracTask task = (TracTask) connector.createTaskFromExistingKey(repository, data.attachmentTicketId + "", null);
		assertFalse(attachmentHandler.canDownloadAttachment(repository, task));
	}

}
