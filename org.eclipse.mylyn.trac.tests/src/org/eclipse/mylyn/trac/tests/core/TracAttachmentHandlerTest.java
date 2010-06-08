/*******************************************************************************
 * Copyright (c) 2006, 2010 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.data.FileTaskAttachmentSource;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.trac.tests.support.TracFixture;
import org.eclipse.mylyn.trac.tests.support.TracTestUtil;
import org.eclipse.mylyn.trac.tests.support.XmlRpcServer.TestData;

/**
 * @author Steffen Pingel
 */
public class TracAttachmentHandlerTest extends TestCase {

	private TaskRepository repository;

	private TracRepositoryConnector connector;

	private AbstractTaskAttachmentHandler attachmentHandler;

	private TestData data;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		data = TracFixture.init010();
		connector = (TracRepositoryConnector) TasksUi.getRepositoryConnector(TracCorePlugin.CONNECTOR_KIND);
		attachmentHandler = connector.getTaskAttachmentHandler();
		repository = TracFixture.current().singleRepository(connector);
	}

	public void testDownloadAttachment() throws Exception {
		ITask task = TracTestUtil.createTask(repository, data.attachmentTicketId + "");
		List<ITaskAttachment> attachments = TracTestUtil.getTaskAttachments(task);
		assertTrue(attachments.size() > 0);
		InputStream in = attachmentHandler.getContent(repository, task, attachments.get(0).getTaskAttribute(), null);
		try {
			byte[] result = new byte[6];
			in.read(result);
			assertEquals("Mylar\n", new String(result));
			assertEquals(-1, in.read());
		} finally {
			in.close();
		}
	}

	public void testGetAttachmentData() throws Exception {
		ITask task = TracTestUtil.createTask(repository, data.attachmentTicketId + "");
		List<ITaskAttachment> attachments = TracTestUtil.getTaskAttachments(task);
		assertTrue(attachments.size() > 0);
		InputStream in = attachmentHandler.getContent(repository, task, attachments.get(0).getTaskAttribute(), null);
		byte[] result = new byte[6];
		try {
			in.read(result);
		} finally {
			in.close();
		}
		assertEquals("Mylar\n", new String(result));
	}

	public void testUploadAttachment() throws Exception {
		ITask task = TracTestUtil.createTask(repository, data.attachmentTicketId + "");
		File file = File.createTempFile("attachment", null);
		file.deleteOnExit();
		OutputStream out = new FileOutputStream(file);
		try {
			out.write("Mylar".getBytes());
		} finally {
			out.close();
		}
		attachmentHandler.postContent(repository, task, new FileTaskAttachmentSource(file), "comment", null, null);

		ITracClient client = connector.getClientManager().getTracClient(repository);
		InputStream in = client.getAttachmentData(data.attachmentTicketId, file.getName(), null);
		try {
			byte[] result = new byte[5];
			in.read(result);
			assertEquals("Mylar", new String(result));
		} finally {
			in.close();
		}
	}

	public void testCanUploadAttachmentXmlRpc() throws Exception {
		repository = TracFixture.TRAC_0_10_XML_RPC.singleRepository();
		ITask task = TracTestUtil.createTask(repository, data.attachmentTicketId + "");
		assertTrue(attachmentHandler.canPostContent(repository, task));
	}

	public void testCanUploadAttachmentWeb() throws Exception {
		repository = TracFixture.TRAC_0_10_WEB.singleRepository();
		ITask task = TracTestUtil.createTask(repository, data.attachmentTicketId + "");
		assertFalse(attachmentHandler.canPostContent(repository, task));
	}

	public void testCanDownloadAttachmentXmlRpc() throws Exception {
		repository = TracFixture.TRAC_0_10_XML_RPC.singleRepository();
		ITask task = TracTestUtil.createTask(repository, data.attachmentTicketId + "");
		assertTrue(attachmentHandler.canGetContent(repository, task));
	}

	public void testCanDownloadAttachmentWeb() throws Exception {
		repository = TracFixture.TRAC_0_10_WEB.singleRepository();
		ITask task = TracTestUtil.createTask(repository, data.attachmentTicketId + "");
		assertFalse(attachmentHandler.canGetContent(repository, task));
	}

}
