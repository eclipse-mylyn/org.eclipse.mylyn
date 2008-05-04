/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractAttachmentHandler;
import org.eclipse.mylyn.internal.tasks.core.deprecated.ITaskAttachment;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryAttachment;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaAttachmentHandler extends AbstractAttachmentHandler {

	private BugzillaRepositoryConnector connector;

	public BugzillaAttachmentHandler(BugzillaRepositoryConnector connector) {
		this.connector = connector;
	}

	@Override
	public InputStream getAttachmentAsStream(TaskRepository repository, RepositoryAttachment attachment,
			IProgressMonitor monitor) throws CoreException {
		try {
			BugzillaClient client = connector.getClientManager().getClient(repository, monitor);
			return client.getAttachmentData(attachment.getId(), monitor);
		} catch (IOException e) {
			throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					RepositoryStatus.ERROR_IO, repository.getRepositoryUrl(), e));
		}
	}

	@Override
	public void uploadAttachment(TaskRepository repository, AbstractTask task, ITaskAttachment attachment,
			String comment, IProgressMonitor monitor) throws CoreException {
		try {
			String bugId = task.getTaskId();
			BugzillaClient client = connector.getClientManager().getClient(repository, monitor);
			client.postAttachment(bugId, comment, attachment, monitor);
		} catch (IOException e) {
			throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					RepositoryStatus.ERROR_IO, repository.getRepositoryUrl(), e));
		}
	}

	@Override
	public boolean canDownloadAttachment(TaskRepository repository, AbstractTask task) {
		return true;
	}

	@Override
	public boolean canUploadAttachment(TaskRepository repository, AbstractTask task) {
		return true;
	}

	@Override
	public boolean canDeprecate(TaskRepository repository, RepositoryAttachment attachment) {
		return false;
	}

	@Override
	public void updateAttachment(TaskRepository repository, RepositoryAttachment attachment) throws CoreException {
		// implement
	}

}
