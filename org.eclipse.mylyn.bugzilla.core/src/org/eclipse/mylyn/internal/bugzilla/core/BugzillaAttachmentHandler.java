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

package org.eclipse.mylar.internal.bugzilla.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.IMylarStatusConstants;
import org.eclipse.mylar.tasks.core.RepositoryAttachment;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaAttachmentHandler implements IAttachmentHandler {

	private BugzillaRepositoryConnector connector;

	public BugzillaAttachmentHandler(BugzillaRepositoryConnector connector) {
		this.connector = connector;
	}

	public byte[] getAttachmentData(TaskRepository repository, RepositoryAttachment attachment) throws CoreException {
		try {
			BugzillaClient client = connector.getClientManager().getClient(repository);
			byte[] data = client.getAttachmentData(attachment.getId());
			return data;
		} catch (IOException e) {
			throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					IMylarStatusConstants.IO_ERROR, repository.getUrl(), e));			
		}
	}

	public void downloadAttachment(TaskRepository repository, RepositoryAttachment attachment, File file)
			throws CoreException {
		if (repository == null || attachment == null || file == null) {
			MylarStatusHandler.log("Unable to download. Null argument.", this);
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, IStatus.ERROR,
					"Unable to download attachment", null));
		}
		String filename = attachment.getAttributeValue(RepositoryTaskAttribute.ATTACHMENT_FILENAME);
		if (filename == null) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, IStatus.ERROR,
					"Attachment download from " + repository.getUrl() + " failed, missing attachment filename.", null));
		}

		try {
			BugzillaClient client = connector.getClientManager().getClient(repository);
			byte[] data = client.getAttachmentData("" + attachment.getId());
			writeData(file, data);
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, 0,
					"Attachment download from " + repository.getUrl() + " failed.", e));
		}
	}

	private void writeData(File file, byte[] data) throws IOException {
		OutputStream out = new FileOutputStream(file);
		try {
			out.write(data);
		} finally {
			out.close();
		}
	}

	public void uploadAttachment(TaskRepository repository, AbstractRepositoryTask task, String comment,
			String description, File file, String contentType, boolean isPatch) throws CoreException {
		try {
			String bugId = task.getTaskId();
			BugzillaClient client = connector.getClientManager().getClient(repository);
			client.postAttachment(bugId, comment, description, file, contentType, isPatch);
		} catch (IOException e) {
			throw new CoreException(new BugzillaStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					IMylarStatusConstants.IO_ERROR, repository.getUrl(), e));
		}
	}

	public boolean canDownloadAttachment(TaskRepository repository, AbstractRepositoryTask task) {
		return true;
	}

	public boolean canUploadAttachment(TaskRepository repository, AbstractRepositoryTask task) {
		return true;
	}

	public boolean canDeprecate(TaskRepository repository, RepositoryAttachment attachment) {
		return false;
	}

	public void updateAttachment(TaskRepository repository, RepositoryAttachment attachment) throws CoreException {
		// implement
	}
}

