/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker         - bug# 395029
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Rob Elves
 * @author Frank Becker
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
		BugzillaClient client;
		try {
			monitor.beginTask(Messages.BugzillaTaskAttachmentHandler_Getting_attachment, IProgressMonitor.UNKNOWN);
			TaskAttachmentMapper attachment = TaskAttachmentMapper.createFrom(attachmentAttribute);
			client = connector.getClientManager().getClient(repository, monitor);
			return client.getAttachmentData(attachment.getAttachmentId(), monitor);
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					"Unable to retrieve attachment", e)); //$NON-NLS-1$
		} finally {
			monitor.done();
		}
	}

	@Override
	public void postContent(TaskRepository repository, ITask task, AbstractTaskAttachmentSource source, String comment,
			TaskAttribute attachmentAttribute, IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask(Messages.BugzillaTaskAttachmentHandler_Sending_attachment, IProgressMonitor.UNKNOWN);
			BugzillaClient client = connector.getClientManager().getClient(repository,
					new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));

			client.postAttachment(task.getTaskId(), comment, source, attachmentAttribute, monitor);
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					Messages.BugzillaTaskAttachmentHandler_unable_to_submit_attachment, e));
		} finally {
			monitor.done();
		}
	}
}
