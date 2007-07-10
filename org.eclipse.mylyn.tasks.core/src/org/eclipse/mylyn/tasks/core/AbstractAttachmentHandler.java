/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.tasks.core.TaskDataManager;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;

/**
 * Extend to provide facility for downloading files from the task repository.
 * 
 * @author Steffen Pingel
 * @since 2.0
 */
public abstract class AbstractAttachmentHandler {

	protected static final int BUFFER_SIZE = 1024;

	private TaskDataManager taskDataManager = null;

	public static final String MESSAGE_ATTACHMENTS_NOT_SUPPORTED = "Attachments not supported by connector: ";

	public static final String MYLAR_CONTEXT_DESCRIPTION_LEGACY = "mylar/context/zip";

	public final static String MYLAR_CONTEXT_FILENAME = "mylyn-context.zip";

	// TODO: change name to Mylyn for 3.0
	public static final String MYLAR_CONTEXT_DESCRIPTION = "mylyn/context/zip";

	public abstract void uploadAttachment(TaskRepository repository, AbstractTask task, ITaskAttachment attachment,
			String comment, IProgressMonitor monitor) throws CoreException;

	public abstract InputStream getAttachmentAsStream(TaskRepository repository, RepositoryAttachment attachment,
			IProgressMonitor monitor) throws CoreException;

	public abstract boolean canUploadAttachment(TaskRepository repository, AbstractTask task);

	public abstract boolean canDownloadAttachment(TaskRepository repository, AbstractTask task);

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
					StatusHandler.fail(e, "Error closing attachment stream", false);
				}
			}
		} finally {
			monitor.done();
		}
	}

	public final boolean hasRepositoryContext(TaskRepository repository, AbstractTask task) {
		if (repository == null || task == null) {
			return false;
		} else {
			Set<RepositoryAttachment> remoteContextAttachments = getContextAttachments(repository, task);
			return (remoteContextAttachments != null && remoteContextAttachments.size() > 0);
		}
	}

	/**
	 * Implementors of this repositoryOperations must perform it locally without going to the server since it is used
	 * for frequent repositoryOperations such as decoration.
	 * 
	 * @return an empty set if no contexts
	 */
	public final Set<RepositoryAttachment> getContextAttachments(TaskRepository repository, AbstractTask task) {
		Set<RepositoryAttachment> contextAttachments = new HashSet<RepositoryAttachment>();

		if (taskDataManager != null) {
			RepositoryTaskData newData = taskDataManager.getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
			if (newData != null) {
				for (RepositoryAttachment attachment : newData.getAttachments()) {
					if (attachment.getDescription().equals(MYLAR_CONTEXT_DESCRIPTION)) {
						contextAttachments.add(attachment);
					} else if (attachment.getDescription().equals(MYLAR_CONTEXT_DESCRIPTION_LEGACY)) {
						contextAttachments.add(attachment);
					}
				}
			}
		}
		return contextAttachments;
	}

	/**
	 * Attaches the associated context to <code>task</code>.
	 * 
	 * @return false, if operation is not supported by repository
	 */
	public final boolean attachContext(TaskRepository repository, AbstractTask task, String longComment,
			IProgressMonitor monitor) throws CoreException {
		ContextCorePlugin.getContextManager().saveContext(task.getHandleIdentifier());
		final File sourceContextFile = ContextCorePlugin.getContextManager().getFileForContext(
				task.getHandleIdentifier());

		RepositoryTaskSyncState previousState = task.getSynchronizationState();

		if (sourceContextFile != null && sourceContextFile.exists()) {
			try {
				task.setSubmitting(true);
				task.setSynchronizationState(RepositoryTaskSyncState.OUTGOING);
				FileAttachment attachment = new FileAttachment(sourceContextFile);
				attachment.setDescription(AbstractAttachmentHandler.MYLAR_CONTEXT_DESCRIPTION);
				attachment.setFilename(AbstractAttachmentHandler.MYLAR_CONTEXT_FILENAME);
				uploadAttachment(repository, task, attachment, longComment, monitor);
			} catch (CoreException e) {
				// TODO: Calling method should be responsible for returning
				// state of task. Wizard will have different behaviour than
				// editor.
				task.setSynchronizationState(previousState);
				throw e;
			} catch (OperationCanceledException e) {
				return true;
			}
		}
		return true;
	}

	/**
	 * Retrieves a context stored in <code>attachment</code> from <code>task</code>.
	 * 
	 * @return false, if operation is not supported by repository
	 */
	public final boolean retrieveContext(TaskRepository repository, AbstractTask task, RepositoryAttachment attachment,
			String destinationPath, IProgressMonitor monitor) throws CoreException {

		File destinationContextFile = ContextCorePlugin.getContextManager().getFileForContext(
				task.getHandleIdentifier());

		// TODO: add functionality for not overwriting previous context
		if (destinationContextFile.exists()) {
			if (!destinationContextFile.delete()) {
				return false;
			}
		}
		FileOutputStream out;
		try {
			out = new FileOutputStream(destinationContextFile);
			try {
				downloadAttachment(repository, attachment, out, monitor);
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					StatusHandler.fail(e, "Could not close context file", false);
				}
			}
		} catch (FileNotFoundException e) {
			throw new CoreException(new RepositoryStatus(IStatus.ERROR, "org.eclipse.mylyn.tasks.core",
					RepositoryStatus.ERROR_INTERNAL, "Could not create context file", e));
		}
		return true;
	}

	public void setTaskDataManager(TaskDataManager taskDataManager) {
		this.taskDataManager = taskDataManager;
	}

}
