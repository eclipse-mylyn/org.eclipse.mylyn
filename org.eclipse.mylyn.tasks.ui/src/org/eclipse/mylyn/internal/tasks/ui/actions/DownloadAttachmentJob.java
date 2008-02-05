/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractAttachmentHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.RepositoryAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Steffen Pingel
 */
public class DownloadAttachmentJob extends Job {

	private final RepositoryAttachment attachment;

	private final File targetFile;

	public DownloadAttachmentJob(RepositoryAttachment attachment, File targetFile) {
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
	protected IStatus run(IProgressMonitor monitor) {
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(attachment.getRepositoryKind(),
				this.attachment.getRepositoryUrl());
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				this.attachment.getRepositoryKind());
		AbstractAttachmentHandler handler = connector.getAttachmentHandler();
		if (handler == null) {
			return new RepositoryStatus(repository, IStatus.INFO, TasksUiPlugin.ID_PLUGIN,
					RepositoryStatus.ERROR_INTERNAL, "The repository does not support attachments.");
		}

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(this.targetFile);
			handler.downloadAttachment(repository, attachment, out, monitor);
		} catch (final CoreException e) {
			StatusHandler.displayStatus("Download Attachment", e.getStatus());
			return Status.OK_STATUS;
		} catch (IOException e) {
			return new RepositoryStatus(repository, IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
					RepositoryStatus.ERROR_IO, "Error while writing to attachment file.", e);
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