/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.deprecated;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Steffen Pingel
 */
public class DownloadAttachmentJob extends Job {

	private static final int BUFFER_SIZE = 1024;

	private final ITaskAttachment attachment;

	private final File targetFile;

	public DownloadAttachmentJob(ITaskAttachment attachment, File targetFile) {
		super("Downloading Attachment");

		if (attachment == null) {
			throw new IllegalArgumentException("attachment must not be null");
		}
		if (targetFile == null) {
			throw new IllegalArgumentException("target must not be null");
		}

		this.attachment = attachment;
		this.targetFile = targetFile;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(attachment.getConnectorKind(),
				this.attachment.getRepositoryUrl());
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				this.attachment.getConnectorKind());
		AbstractTaskAttachmentHandler handler = connector.getTaskAttachmentHandler();
		if (handler == null) {
			return new RepositoryStatus(repository, IStatus.INFO, TasksUiPlugin.ID_PLUGIN,
					RepositoryStatus.ERROR_INTERNAL, "The repository does not support attachments.");
		}

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(this.targetFile);
			try {
				InputStream in = handler.getContent(repository, attachment.getTask(), attachment.getTaskAttribute(),
						monitor);

				try {
					int len;
					byte[] buffer = new byte[BUFFER_SIZE];
					while ((len = in.read(buffer)) != -1) {
						out.write(buffer, 0, len);
					}
				} finally {
					in.close();
				}
			} finally {
				out.close();
			}

		} catch (final CoreException e) {
			TasksUiInternal.displayStatus("Download Attachment", e.getStatus());
			return Status.OK_STATUS;
		} catch (IOException e) {
			return new RepositoryStatus(repository, IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
					RepositoryStatus.ERROR_IO, "Error while retrieving attachment file.", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
							"Could not close attachment file: " + this.targetFile.getAbsolutePath(), e));
				}
			}
		}

		return Status.OK_STATUS;
	}

}