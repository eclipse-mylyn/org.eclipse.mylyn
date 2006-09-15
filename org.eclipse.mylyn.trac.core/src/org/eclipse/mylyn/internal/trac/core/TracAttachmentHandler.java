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
import java.net.Proxy;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.internal.trac.core.model.TracTicket;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.RepositoryAttachment;
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

	public void downloadAttachment(TaskRepository repository, AbstractRepositoryTask task, RepositoryAttachment attachment, File file, Proxy proxySettings) throws CoreException {
		String filename = attachment.getAttributeValue(RepositoryTaskAttribute.ATTACHMENT_FILENAME);
		if (filename == null) {
			throw new CoreException(new Status(IStatus.ERROR, TracCorePlugin.PLUGIN_ID, IStatus.OK, "Attachment download from " + task.getRepositoryUrl() + " failed, missing attachment filename.", null));
		}
		
		try {
			ITracClient client = connector.getClientManager().getRepository(repository);
			int id = Integer.parseInt(AbstractRepositoryTask.getTaskId(task.getHandleIdentifier()));
			byte[] data = client.getAttachmentData(id, filename);
			writeData(file, data);
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, TracCorePlugin.PLUGIN_ID, 0, "Attachment download from " + task.getRepositoryUrl() + " failed, please see details.", e ));
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

	public void uploadAttachment(TaskRepository repository, AbstractRepositoryTask task, String comment, String description, File file, String contentType, boolean isPatch, Proxy proxySettings) throws CoreException {
		if (!connector.hasAttachmentSupport(repository, task)) {
			throw new CoreException(new Status(IStatus.INFO, TracCorePlugin.PLUGIN_ID, IStatus.OK, "Attachments are not supported by this repository access type.", null));
		}

		try {
			ITracClient client = connector.getClientManager().getRepository(repository);
			int id = Integer.parseInt(AbstractRepositoryTask.getTaskId(task.getHandleIdentifier()));
			byte[] data = readData(file); 
			client.putAttachmentData(id, file.getName(), description, data);
			if (comment != null && comment.length() > 0) {
				TracTicket ticket = new TracTicket(id);
				client.updateTicket(ticket, comment);
			}
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, TracCorePlugin.PLUGIN_ID, 0, "Attachment upload to " + task.getRepositoryUrl() + " failed, please see details.", e ));
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
		return connector.hasAttachmentSupport(repository, task);
	}

	public boolean canUploadAttachment(TaskRepository repository, AbstractRepositoryTask task) {
		return connector.hasAttachmentSupport(repository, task);
	}

	public boolean canDeprecate(TaskRepository repository, RepositoryAttachment attachment) {		
		return false;
	}

	public void updateAttachment(TaskRepository repository, RepositoryAttachment attachment) throws CoreException {
		// ignore
	}
	
}
