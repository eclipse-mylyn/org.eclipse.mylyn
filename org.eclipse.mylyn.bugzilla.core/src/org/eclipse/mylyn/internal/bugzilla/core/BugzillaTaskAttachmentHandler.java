/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Rob Elves
 * @since 3.0
 */
public class BugzillaTaskAttachmentHandler extends AbstractTaskAttachmentHandler {

	private final BugzillaRepositoryConnector connector;

	public BugzillaTaskAttachmentHandler(BugzillaRepositoryConnector connector) {
		this.connector = connector;
	}

	@Override
	public boolean canGetContent(TaskRepository repository, ITask task) {
		// ignore
		return true;
	}

	@Override
	public boolean canPostContent(TaskRepository repository, ITask task) {
		// ignore
		return true;
	}

	@Override
	public InputStream getContent(TaskRepository repository, ITask task, TaskAttribute attachmentAttribute,
			IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask("Getting attachment", IProgressMonitor.UNKNOWN);
			TaskAttachmentMapper attachment = TaskAttachmentMapper.createFrom(attachmentAttribute);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			downloadAttachment(repository, task, attachment.getAttachmentId(), out, monitor);
			return new ByteArrayInputStream(out.toByteArray());
		} finally {
			monitor.done();
		}
	}

	@Override
	public void postContent(TaskRepository repository, ITask task, AbstractTaskAttachmentSource source, String comment,
			TaskAttribute attachmentAttribute, IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask("Sending attachment", IProgressMonitor.UNKNOWN);
			BugzillaClient client = connector.getClientManager().getClient(repository,
					new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
			client.postAttachment(task.getTaskId(), comment, TaskAttachmentMapper.createFrom(attachmentAttribute),
					new AttachmentPartSource(source), monitor);
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					"Unable to submit attachment", e));
		} finally {
			monitor.done();
		}

	}

	private void downloadAttachment(TaskRepository repository, ITask task, String attachmentId, OutputStream out,
			IProgressMonitor monitor) throws CoreException {
		BugzillaClient client;
		try {
			client = connector.getClientManager().getClient(repository,
					new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
			client.getAttachmentData(attachmentId, out, monitor);
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					"Unable to retrieve attachment", e));
		}
	}

	public static class AttachmentPartSource implements PartSource {

		private final AbstractTaskAttachmentSource attachment;

		public AttachmentPartSource(AbstractTaskAttachmentSource attachment) {
			this.attachment = attachment;
		}

		public InputStream createInputStream() throws IOException {
			try {
				return attachment.createInputStream(null);
			} catch (CoreException e) {
				StatusHandler.log(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID,
						"Error submitting attachment", e));
				throw new IOException("Failed to create source stream");
			}
		}

		public String getFileName() {
			return attachment.getName();
		}

		public long getLength() {
			return attachment.getLength();
		}

	}
}
