/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;

/**
 * @author Steffen Pingel
 */
public class DownloadAttachmentJob extends Job {

	private final ITaskAttachment attachment;

	private final File targetFile;

	public DownloadAttachmentJob(ITaskAttachment attachment, File targetFile) {
		super("Downloading Attachment");
		this.attachment = attachment;
		this.targetFile = targetFile;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			try {
				OutputStream out = new BufferedOutputStream(new FileOutputStream(targetFile));
				try {
					AttachmentUtil.downloadAttachment(attachment, out, monitor);
				} finally {
					out.close();
				}
			} catch (IOException e) {
				throw new CoreException(new RepositoryStatus(attachment.getTaskRepository(), IStatus.ERROR,
						TasksUiPlugin.ID_PLUGIN, RepositoryStatus.ERROR_IO, "IO error writing attachment: "
								+ e.getMessage(), e));
			}
		} catch (final CoreException e) {
			TasksUiInternal.asyncDisplayStatus("Copy Attachment to Clipboard", e.getStatus());
			return Status.OK_STATUS;
		}

		return Status.OK_STATUS;
	}

}