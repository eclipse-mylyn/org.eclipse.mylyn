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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylar.internal.trac.core.model.TracTicket;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.IMylarStatusConstants;
import org.eclipse.mylar.tasks.core.RepositoryAttachment;
import org.eclipse.mylar.tasks.core.RepositoryStatus;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class TracAttachmentHandler implements IAttachmentHandler {

	private TracRepositoryConnector connector;

	public TracAttachmentHandler(TracRepositoryConnector connector) {
		this.connector = connector;
	}

	public void downloadAttachment(TaskRepository repository, RepositoryAttachment attachment, File file, IProgressMonitor monitor)
			throws CoreException {
		byte[] data = getAttachmentData(repository, attachment);
		try {
			writeData(file, data);
		} catch (IOException e) {
			throw new CoreException(TracCorePlugin.toStatus(e));
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
			String description, File file, String contentType, boolean isPatch, IProgressMonitor monitor) throws CoreException {
		if (!TracRepositoryConnector.hasAttachmentSupport(repository, task)) {
			throw new CoreException(new RepositoryStatus(repository.getUrl(), IStatus.INFO, TracCorePlugin.PLUGIN_ID,
					IMylarStatusConstants.REPOSITORY_ERROR,
					"Attachments are not supported by this repository access type"));
		}

		try {
			ITracClient client = connector.getClientManager().getRepository(repository);
			int id = Integer.parseInt(task.getTaskId());
			byte[] data = readData(file);
			client.putAttachmentData(id, file.getName(), description, data);
			if (comment != null && comment.length() > 0) {
				TracTicket ticket = new TracTicket(id);
				client.updateTicket(ticket, comment);
			}
		} catch (Exception e) {
			throw new CoreException(TracCorePlugin.toStatus(e));
		}
	}

	private byte[] readData(File file) throws IOException {
		if (file.length() > Integer.MAX_VALUE) {
			throw new IOException("Can not upload files larger than " + Integer.MAX_VALUE + " bytes");
		}

		InputStream in = new FileInputStream(file);
		try {
			byte[] data = new byte[(int) file.length()];
			in.read(data, 0, (int) file.length());
			return data;
		} finally {
			in.close();
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

	public byte[] getAttachmentData(TaskRepository repository, RepositoryAttachment attachment) throws CoreException {
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

}
