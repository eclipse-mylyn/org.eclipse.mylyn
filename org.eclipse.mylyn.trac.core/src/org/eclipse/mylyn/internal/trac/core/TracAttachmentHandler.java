/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.core;

import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylar.internal.trac.core.model.TracTicket;
import org.eclipse.mylar.tasks.core.AbstractAttachmentHandler;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IMylarStatusConstants;
import org.eclipse.mylar.tasks.core.ITaskAttachment;
import org.eclipse.mylar.tasks.core.RepositoryAttachment;
import org.eclipse.mylar.tasks.core.RepositoryStatus;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class TracAttachmentHandler extends AbstractAttachmentHandler {

	private TracRepositoryConnector connector;

	public TracAttachmentHandler(TracRepositoryConnector connector) {
		this.connector = connector;
	}

	public InputStream getAttachmentAsStream(TaskRepository repository, RepositoryAttachment attachment,
			IProgressMonitor monitor) throws CoreException {
		String filename = attachment.getAttributeValue(RepositoryTaskAttribute.ATTACHMENT_FILENAME);
		if (filename == null) {
			throw new CoreException(new RepositoryStatus(repository.getUrl(), IStatus.ERROR, TracCorePlugin.PLUGIN_ID,
					IMylarStatusConstants.REPOSITORY_ERROR, "Attachment download from " + repository.getUrl()
							+ " failed, missing attachment filename."));
		}

		try {
			ITracClient client = connector.getClientManager().getRepository(repository);
			int id = Integer.parseInt(attachment.getTaskId());
			return client.getAttachmentData(id, filename);
		} catch (Exception e) {
			throw new CoreException(TracCorePlugin.toStatus(e));
		}
	}

	public void uploadAttachment(TaskRepository repository, AbstractRepositoryTask task, ITaskAttachment attachment,
			String comment, IProgressMonitor monitor) throws CoreException {
		if (!TracRepositoryConnector.hasAttachmentSupport(repository, task)) {
			throw new CoreException(new RepositoryStatus(repository.getUrl(), IStatus.INFO, TracCorePlugin.PLUGIN_ID,
					IMylarStatusConstants.REPOSITORY_ERROR,
					"Attachments are not supported by this repository access type"));
		}

		monitor.beginTask("Uploading attachment", IProgressMonitor.UNKNOWN);
		try {
			try {
				ITracClient client = connector.getClientManager().getRepository(repository);
				int id = Integer.parseInt(task.getTaskId());
				client.putAttachmentData(id, attachment.getFilename(), attachment.getDescription(), attachment.createInputStream());
				if (comment != null && comment.length() > 0) {
					TracTicket ticket = new TracTicket(id);
					client.updateTicket(ticket, comment);
				}
			} catch (Exception e) {
				throw new CoreException(TracCorePlugin.toStatus(e));
			}
		} finally {
			monitor.done();
		}
	}

	public boolean canDownloadAttachment(TaskRepository repository, AbstractRepositoryTask task) {
		if (repository == null) {
			return false;
		}
		return TracRepositoryConnector.hasAttachmentSupport(repository, task);
	}

	public boolean canUploadAttachment(TaskRepository repository, AbstractRepositoryTask task) {
		if (repository == null) {
			return false;
		}
		return TracRepositoryConnector.hasAttachmentSupport(repository, task);
	}

	public boolean canDeprecate(TaskRepository repository, RepositoryAttachment attachment) {
		return false;
	}

	public void updateAttachment(TaskRepository repository, RepositoryAttachment attachment) throws CoreException {
		throw new UnsupportedOperationException();
	}

}
