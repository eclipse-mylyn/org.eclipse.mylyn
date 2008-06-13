/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Steffen Pingel
 */
public class TracAttachmentHandler extends AbstractTaskAttachmentHandler {

	private final TracRepositoryConnector connector;

	public TracAttachmentHandler(TracRepositoryConnector connector) {
		this.connector = connector;
	}

	@Override
	public InputStream getContent(TaskRepository repository, ITask task, TaskAttribute attachmentAttribute,
			IProgressMonitor monitor) throws CoreException {
		TaskAttachmentMapper mapper = TaskAttachmentMapper.createFrom(attachmentAttribute);
		String filename = mapper.getFileName();
		if (filename == null || filename.length() == 0) {
			throw new CoreException(new RepositoryStatus(repository.getRepositoryUrl(), IStatus.ERROR,
					TracCorePlugin.ID_PLUGIN, RepositoryStatus.ERROR_REPOSITORY, "Attachment download from "
							+ repository.getRepositoryUrl() + " failed, missing attachment filename."));
		}

		try {
			ITracClient client = connector.getClientManager().getTracClient(repository);
			int id = Integer.parseInt(task.getTaskId());
			return client.getAttachmentData(id, filename, monitor);
		} catch (Exception e) {
			throw new CoreException(TracCorePlugin.toStatus(e, repository));
		}
	}

	@Override
	public void postContent(TaskRepository repository, ITask task, AbstractTaskAttachmentSource source, String comment,
			TaskAttribute attachmentAttribute, IProgressMonitor monitor) throws CoreException {
		if (!TracRepositoryConnector.hasAttachmentSupport(repository, task)) {
			throw new CoreException(new RepositoryStatus(repository.getRepositoryUrl(), IStatus.INFO,
					TracCorePlugin.ID_PLUGIN, RepositoryStatus.ERROR_REPOSITORY,
					"Attachments are not supported by this repository access type"));
		}

		String filename = source.getName();
		String description = source.getDescription();
		if (attachmentAttribute != null) {
			TaskAttachmentMapper mapper = TaskAttachmentMapper.createFrom(attachmentAttribute);
			if (mapper.getFileName() != null) {
				filename = mapper.getFileName();
			}
			if (mapper.getDescription() != null) {
				description = mapper.getDescription();
			}
		}

		monitor.beginTask("Uploading attachment", IProgressMonitor.UNKNOWN);
		try {
			try {
				ITracClient client = connector.getClientManager().getTracClient(repository);
				int id = Integer.parseInt(task.getTaskId());
				client.putAttachmentData(id, filename, description, source.createInputStream(monitor), monitor);
				if (comment != null && comment.length() > 0) {
					TracTicket ticket = new TracTicket(id);
					client.updateTicket(ticket, comment, monitor);
				}
			} catch (Exception e) {
				throw new CoreException(TracCorePlugin.toStatus(e, repository));
			}
		} finally {
			monitor.done();
		}
	}

	@Override
	public boolean canGetContent(TaskRepository repository, ITask task) {
		return TracRepositoryConnector.hasAttachmentSupport(repository, task);
	}

	@Override
	public boolean canPostContent(TaskRepository repository, ITask task) {
		return TracRepositoryConnector.hasAttachmentSupport(repository, task);
	}

}
