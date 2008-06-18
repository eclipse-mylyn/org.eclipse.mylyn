/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.deprecated;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @deprecated Do not use. This class is pending for removal: see bug 237552.
 */
@Deprecated
public abstract class AbstractAttachmentHandler {

	protected static final int BUFFER_SIZE = 1024;

	public abstract void uploadAttachment(TaskRepository repository, ITask task, ITaskAttachment attachment,
			String comment, IProgressMonitor monitor) throws CoreException;

	public abstract InputStream getAttachmentAsStream(TaskRepository repository, RepositoryAttachment attachment,
			IProgressMonitor monitor) throws CoreException;

	public abstract boolean canUploadAttachment(TaskRepository repository, ITask task);

	public abstract boolean canDownloadAttachment(TaskRepository repository, ITask task);

	public abstract boolean canDeprecate(TaskRepository repository, RepositoryAttachment attachment);

	/**
	 * To deprecate, change the attribute on the RepositoryAttachment and pass to this method
	 */
	public abstract void updateAttachment(TaskRepository repository, RepositoryAttachment attachment)
			throws CoreException;

	public void downloadAttachment(TaskRepository repository, RepositoryAttachment attachment, OutputStream out,
			IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Downloading attachment", IProgressMonitor.UNKNOWN);
		try {
			InputStream in = new BufferedInputStream(getAttachmentAsStream(repository, attachment,
					new SubProgressMonitor(monitor, 1)));
			try {
				byte[] buffer = new byte[BUFFER_SIZE];
				while (true) {
					int count = in.read(buffer);
					if (count == -1) {
						return;
					}
					if (monitor.isCanceled()) {
						throw new OperationCanceledException();
					}
					out.write(buffer, 0, count);
					if (monitor.isCanceled()) {
						throw new OperationCanceledException();
					}
				}
			} catch (IOException e) {
				throw new CoreException(RepositoryStatus.createStatus(repository, IStatus.ERROR,
						"org.eclipse.mylyn.tasks.core", "IO error reading attachment: " + e.getMessage()));
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
							"Error closing attachment stream", e));
				}
			}
		} finally {
			monitor.done();
		}
	}

}
