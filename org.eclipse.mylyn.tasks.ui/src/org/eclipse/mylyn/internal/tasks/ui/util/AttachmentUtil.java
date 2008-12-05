/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskAttachment;
import org.eclipse.mylyn.internal.tasks.core.data.FileTaskAttachmentSource;
import org.eclipse.mylyn.internal.tasks.core.sync.SubmitTaskAttachmentJob;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.sync.SubmitJob;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Steffen Pingel
 */
public class AttachmentUtil {

	protected static final int BUFFER_SIZE = 1024;

	public static final String CONTEXT_DESCRIPTION = "mylyn/context/zip"; //$NON-NLS-1$

	private static final String CONTEXT_DESCRIPTION_LEGACY = "mylar/context/zip"; //$NON-NLS-1$

	private static final String CONTEXT_FILENAME = "mylyn-context.zip"; //$NON-NLS-1$

	private static final String CONTEXT_CONTENT_TYPE = "application/octet-stream"; //$NON-NLS-1$

	public static boolean postContext(AbstractRepositoryConnector connector, TaskRepository repository, ITask task,
			String comment, TaskAttribute attribute, IProgressMonitor monitor) throws CoreException {
		AbstractTaskAttachmentHandler attachmentHandler = connector.getTaskAttachmentHandler();
		ContextCorePlugin.getContextStore().saveActiveContext();

		File file = ContextCorePlugin.getContextStore().getFileForContext(task.getHandleIdentifier());
		if (attachmentHandler != null && file != null && file.exists()) {
			FileTaskAttachmentSource attachment = new FileTaskAttachmentSource(file);
			attachment.setDescription(CONTEXT_DESCRIPTION);
			attachment.setName(CONTEXT_FILENAME);
			attachmentHandler.postContent(repository, task, attachment, comment, attribute, monitor);
			return true;
		}
		return false;
	}

	public static List<ITaskAttachment> getContextAttachments(TaskRepository repository, ITask task) {
		List<ITaskAttachment> contextAttachments = new ArrayList<ITaskAttachment>();
		TaskData taskData;
		try {
			taskData = TasksUi.getTaskDataManager().getTaskData(task);
		} catch (CoreException e) {
			// ignore
			return contextAttachments;
		}
		if (taskData != null) {
			List<TaskAttribute> taskAttachments = taskData.getAttributeMapper().getAttributesByType(taskData,
					TaskAttribute.TYPE_ATTACHMENT);
			for (TaskAttribute attribute : taskAttachments) {
				TaskAttachment taskAttachment = new TaskAttachment(repository, task, attribute);
				taskData.getAttributeMapper().updateTaskAttachment(taskAttachment, attribute);
				if (isContext(taskAttachment)) {
					contextAttachments.add(taskAttachment);
				}
			}
		}
		return contextAttachments;
	}

	public static boolean hasContextAttachment(ITask task) {
		Assert.isNotNull(task);
		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		List<ITaskAttachment> contextAttachments = getContextAttachments(repository, task);
		return contextAttachments.size() > 0;
	}

	public static boolean downloadContext(final ITask task, final ITaskAttachment attachment,
			final IRunnableContext context) {
		if (task.isActive()) {
			TasksUi.getTaskActivityManager().deactivateTask(task);
		}
		try {
			context.run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					File targetFile = ContextCorePlugin.getContextStore().getFileForContext(task.getHandleIdentifier());
					try {
						OutputStream out = new BufferedOutputStream(new FileOutputStream(targetFile));
						try {
							AttachmentUtil.downloadAttachment(attachment, out, monitor);
						} catch (CoreException e) {
							throw new InvocationTargetException(e);
						} finally {
							out.close();
						}
					} catch (OperationCanceledException e) {
						throw new InterruptedException();
					} catch (IOException e) {
						throw new InvocationTargetException(
								new CoreException(new RepositoryStatus(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
										RepositoryStatus.ERROR_IO, "Error writing to context file", e))); //$NON-NLS-1$
					}
				}
			});
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof CoreException) {
				TasksUiInternal.displayStatus(Messages.AttachmentUtil_Mylyn_Information, ((CoreException) e.getCause()).getStatus());
			} else {
				StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Unexpected error while retrieving context", e)); //$NON-NLS-1$
			}
			return false;
		} catch (InterruptedException ignored) {
			// canceled
			return false;
		}
		TasksUiInternal.getTaskList().notifyElementChanged(task);
		TasksUi.getTaskActivityManager().activateTask(task);
		return true;
	}

	public static boolean uploadContext(final TaskRepository repository, final ITask task, final String comment,
			final IRunnableContext context) {
		ContextCorePlugin.getContextStore().saveActiveContext();
		File sourceContextFile = ContextCorePlugin.getContextStore().getFileForContext(task.getHandleIdentifier());
		if (!sourceContextFile.exists()) {
			TasksUiInternal.displayStatus(Messages.AttachmentUtil_Mylyn_Information, new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
					Messages.AttachmentUtil_The_context_is_empty));
			return false;
		}

		FileTaskAttachmentSource source = new FileTaskAttachmentSource(sourceContextFile);
		source.setName(CONTEXT_FILENAME);
		source.setDescription(CONTEXT_DESCRIPTION);
		source.setContentType(CONTEXT_CONTENT_TYPE);
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());
		final SubmitJob submitJob = TasksUiInternal.getJobFactory().createSubmitTaskAttachmentJob(connector,
				repository, task, source, comment, null);
		try {
			context.run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					if (((SubmitTaskAttachmentJob) submitJob).run(monitor) == Status.CANCEL_STATUS) {
						throw new InterruptedException();
					}
				}
			});
		} catch (InvocationTargetException e) {
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Unexpected error while attaching context", e)); //$NON-NLS-1$
			return false;
		} catch (InterruptedException ignored) {
			// canceled
			return false;
		}
		if (submitJob.getStatus() != null) {
			TasksUiInternal.displayStatus(Messages.AttachmentUtil_Mylyn_Information, submitJob.getStatus());
			return false;
		}
		return true;
	}

	public static boolean hasLocalContext(ITask task) {
		Assert.isNotNull(task);
		return ContextCore.getContextManager().hasContext(task.getHandleIdentifier());
	}

	public static boolean isContext(ITaskAttachment attachment) {
		return CONTEXT_DESCRIPTION.equals(attachment.getDescription())
				|| CONTEXT_DESCRIPTION_LEGACY.equals(attachment.getDescription());
	}

	public static boolean canUploadAttachment(ITask task) {
		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());
		AbstractTaskAttachmentHandler attachmentHandler = connector.getTaskAttachmentHandler();
		if (attachmentHandler != null) {
			return attachmentHandler.canPostContent(repository, task);
		}
		return false;
	}

	public static boolean canDownloadAttachment(ITask task) {
		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());
		AbstractTaskAttachmentHandler attachmentHandler = connector.getTaskAttachmentHandler();
		if (attachmentHandler != null) {
			return attachmentHandler.canGetContent(repository, task);
		}
		return false;
	}

	public static void downloadAttachment(ITaskAttachment attachment, OutputStream out, IProgressMonitor monitor)
			throws CoreException {
		try {
			monitor.beginTask(Messages.AttachmentUtil_Downloading_attachment, IProgressMonitor.UNKNOWN);

			AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
					attachment.getConnectorKind());
			AbstractTaskAttachmentHandler handler = connector.getTaskAttachmentHandler();
			if (handler == null) {
				throw new CoreException(new RepositoryStatus(IStatus.INFO, TasksUiPlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_INTERNAL, "The repository does not support attachments.")); //$NON-NLS-1$
			}

			InputStream in = handler.getContent(attachment.getTaskRepository(), attachment.getTask(),
					attachment.getTaskAttribute(), monitor);
			try {
				byte[] buffer = new byte[BUFFER_SIZE];
				while (true) {
					Policy.checkCanceled(monitor);
					int count = in.read(buffer);
					if (count == -1) {
						return;
					}
					out.write(buffer, 0, count);
				}
			} catch (IOException e) {
				throw new CoreException(new RepositoryStatus(attachment.getTaskRepository(), IStatus.ERROR,
						TasksUiPlugin.ID_PLUGIN, RepositoryStatus.ERROR_IO, "IO error reading attachment: " //$NON-NLS-1$
								+ e.getMessage(), e));
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
							"Error closing attachment stream", e)); //$NON-NLS-1$
				}
			}
		} finally {
			monitor.done();
		}
	}

}
