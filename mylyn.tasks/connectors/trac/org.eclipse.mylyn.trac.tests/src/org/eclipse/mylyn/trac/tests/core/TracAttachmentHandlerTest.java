/*******************************************************************************
 * Copyright (c) 2006, 2010 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
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

import org.eclipse.mylyn.internal.tasks.core.data.FileTaskAttachmentSource;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.trac.tests.support.TracFixture;
import org.eclipse.mylyn.trac.tests.support.TracHarness;
import org.eclipse.mylyn.trac.tests.support.TracTestUtil;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
public class TracAttachmentHandlerTest extends TestCase {

	private TaskRepository repository;

	private TracRepositoryConnector connector;

	private AbstractTaskAttachmentHandler attachmentHandler;

	private TracHarness harness;

	@Override
	protected void setUp() throws Exception {
		harness = TracFixture.current().createHarness();
		connector = harness.connector();
		attachmentHandler = connector.getTaskAttachmentHandler();
		repository = harness.repository();
	}

	@Override
	protected void tearDown() throws Exception {
		harness.dispose();
	}

	public void testGetContent() throws Exception {
		TracTicket ticket = harness.createTicket("GetContent");
		harness.attachFile(ticket.getId(), "attachment.txt", "Mylar\n");

		ITask task = harness.getTask(ticket);
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

	public void testPostConent() throws Exception {
		ITask task = harness.createTask("GetContent");
		File file = File.createTempFile("attachment", null);
		file.deleteOnExit();
		try (OutputStream out = new FileOutputStream(file)) {
			out.write("Mylar".getBytes());
		}
		attachmentHandler.postContent(repository, task, new FileTaskAttachmentSource(file), "comment", null, null);

		ITracClient client = connector.getClientManager().getTracClient(repository);
		try (InputStream in = client.getAttachmentData(Integer.parseInt(task.getTaskId()), file.getName(), null)) {
			byte[] result = new byte[5];
			in.read(result);
			assertEquals("Mylar", new String(result));
		}
	}

	public void testCanUploadAttachment() throws Exception {
		ITask task = harness.createTask("canUploadAttachment");
		if (harness.isXmlRpc()) {
			assertTrue(attachmentHandler.canPostContent(repository, task));
		} else {
			assertFalse(attachmentHandler.canPostContent(repository, task));
		}
	}

	public void testCanDownloadAttachmentXmlRpc() throws Exception {
		ITask task = harness.createTask("canDownloadAttachment");
		if (harness.isXmlRpc()) {
			assertTrue(attachmentHandler.canGetContent(repository, task));
		} else {
			assertFalse(attachmentHandler.canGetContent(repository, task));
		}
	}

}
