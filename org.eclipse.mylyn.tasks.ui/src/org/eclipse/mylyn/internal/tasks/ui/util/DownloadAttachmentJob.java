/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
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
		super(Messages.DownloadAttachmentJob_Downloading_Attachment);
		this.attachment = attachment;
		this.targetFile = targetFile;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			try {
				boolean exceptionThrown = true;
				OutputStream out = new BufferedOutputStream(new FileOutputStream(targetFile));
				try {
					AttachmentUtil.downloadAttachment(attachment, out, monitor);
					exceptionThrown = false;
				} finally {
					out.close();
					if (exceptionThrown) {
						targetFile.delete();
					}
				}
			} catch (IOException e) {
				throw new CoreException(new RepositoryStatus(attachment.getTaskRepository(), IStatus.ERROR,
						TasksUiPlugin.ID_PLUGIN, RepositoryStatus.ERROR_IO, "IO error writing attachment: " //$NON-NLS-1$
								+ e.getMessage(), e));
			}
		} catch (final CoreException e) {
			TasksUiInternal.asyncDisplayStatus(Messages.DownloadAttachmentJob_Copy_Attachment_to_Clipboard,
					e.getStatus());
			return Status.OK_STATUS;
		}

		return Status.OK_STATUS;
	}

}
