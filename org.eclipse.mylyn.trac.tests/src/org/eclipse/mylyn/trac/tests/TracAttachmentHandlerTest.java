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
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.Proxy;

import junit.framework.TestCase;

import org.eclipse.mylar.core.core.tests.support.MylarTestUtils;
import org.eclipse.mylar.core.core.tests.support.MylarTestUtils.Credentials;
import org.eclipse.mylar.core.core.tests.support.MylarTestUtils.PrivilegeLevel;
import org.eclipse.mylar.internal.trac.TracRepositoryConnector;
import org.eclipse.mylar.internal.trac.TracTask;
import org.eclipse.mylar.internal.trac.TracUiPlugin;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.ui.TaskRepositoryManager;
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

	private Proxy proxySettings;

	private TestData data;

	protected void setUp() throws Exception {
		super.setUp();

		manager = TasksUiPlugin.getRepositoryManager();
		manager.clearRepositories();

		proxySettings = TasksUiPlugin.getDefault().getProxySettings();

		data = TestFixture.init010();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	protected void init(String url, Version version) {
		String kind = TracUiPlugin.REPOSITORY_KIND;
		Credentials credentials = MylarTestUtils.readCredentials(PrivilegeLevel.USER);

		repository = new TaskRepository(kind, url);
		repository.setAuthenticationCredentials(credentials.username, credentials.password);
		repository.setTimeZoneId(ITracClient.TIME_ZONE);
		repository.setCharacterEncoding(ITracClient.CHARSET);
		repository.setVersion(version.name());

		manager.addRepository(repository);

		AbstractRepositoryConnector abstractConnector = manager.getRepositoryConnector(kind);
		connector = (TracRepositoryConnector) abstractConnector;
		connector.setForceSyncExec(true);

		attachmentHandler = connector.getAttachmentHandler();
	}

	public void testDownloadAttachment() throws Exception {
		// TODO needs RepositoryTaskData
	}

	public void testUploadAttachment() throws Exception {
		init(Constants.TEST_TRAC_010_URL, Version.XML_RPC);
		TracTask task = (TracTask) connector.createTaskFromExistingKey(repository, data.attachmentTicketId + "");
		File file = File.createTempFile("attachment", null);
		OutputStream out = new FileOutputStream(file);
		try {
			out.write("Mylar".getBytes());
		} finally {
			out.close();
		}
		attachmentHandler.uploadAttachment(repository, task, "comment", "description", file, "", false, proxySettings);

		ITracClient client = connector.getClientManager().getRepository(repository);
		byte[] result = client.getAttachmentData(data.attachmentTicketId, file.getName());
		assertEquals("Mylar", new String(result));
	}

	public void testCanUploadAttachment010() {
		init(Constants.TEST_TRAC_010_URL, Version.XML_RPC);
		TracTask task = (TracTask) connector.createTaskFromExistingKey(repository, data.attachmentTicketId + "");
		assertTrue(attachmentHandler.canUploadAttachment(repository, task));
	}

	public void testCanUploadAttachment096() {
		init(Constants.TEST_TRAC_010_URL, Version.TRAC_0_9);
		TracTask task = (TracTask) connector.createTaskFromExistingKey(repository, data.attachmentTicketId + "");
		assertFalse(attachmentHandler.canUploadAttachment(repository, task));
	}

	public void testCanDownloadAttachment() throws Exception {
		init(Constants.TEST_TRAC_010_URL, Version.XML_RPC);
		TracTask task = (TracTask) connector.createTaskFromExistingKey(repository, data.attachmentTicketId + "");
		assertFalse(attachmentHandler.canDownloadAttachment(repository, task));

		init(Constants.TEST_TRAC_010_URL, Version.TRAC_0_9);
		task = (TracTask) connector.createTaskFromExistingKey(repository, data.attachmentTicketId + "");
		assertFalse(attachmentHandler.canDownloadAttachment(repository, task));
	}

}
