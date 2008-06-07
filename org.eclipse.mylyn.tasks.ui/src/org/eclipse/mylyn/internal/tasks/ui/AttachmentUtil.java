/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.TaskAttachment;
import org.eclipse.mylyn.internal.tasks.core.TaskDataStorageManager;
import org.eclipse.mylyn.internal.tasks.core.data.FileTaskAttachmentSource;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractAttachmentHandler;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractLegacyRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.FileAttachment;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryAttachment;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.core.sync.SubmitTaskAttachmentJob;
import org.eclipse.mylyn.internal.tasks.ui.actions.DownloadAttachmentJob;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.sync.SubmitJob;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class AttachmentUtil {

	private static final String CONTEXT_DESCRIPTION = "mylyn/context/zip";

	private static final String CONTEXT_DESCRIPTION_LEGACY = "mylar/context/zip";

	private static final String CONTEXT_FILENAME = "mylyn-context.zip";

	private static final String CONTEXT_CONTENT_TYPE = "application/octet-stream";

	/**
	 * Attaches the associated context to <code>task</code>.
	 * 
	 * @return false, if operation is not supported by repository
	 */
	@SuppressWarnings("restriction")
	@Deprecated
	public static boolean attachContext(AbstractAttachmentHandler attachmentHandler, TaskRepository repository,
			ITask task, String longComment, IProgressMonitor monitor) throws CoreException {
		ContextCorePlugin.getContextStore().saveContext(task.getHandleIdentifier());
		final File sourceContextFile = ContextCorePlugin.getContextStore()
				.getFileForContext(task.getHandleIdentifier());

		SynchronizationState previousState = task.getSynchronizationState();

		if (sourceContextFile != null && sourceContextFile.exists()) {
			try {
				((AbstractTask) task).setSubmitting(true);
				((AbstractTask) task).setSynchronizationState(SynchronizationState.OUTGOING);
				FileAttachment attachment = new FileAttachment(sourceContextFile);
				attachment.setDescription(CONTEXT_DESCRIPTION);
				attachment.setFilename(CONTEXT_FILENAME);
				attachmentHandler.uploadAttachment(repository, task, attachment, longComment, monitor);
			} catch (CoreException e) {
				// TODO: Calling method should be responsible for returning
				// state of task. Wizard will have different behaviour than
				// editor.
				((AbstractTask) task).setSynchronizationState(previousState);
				throw e;
			} catch (OperationCanceledException e) {
				return true;
			}
		}
		return true;
	}

	public static boolean postContext(AbstractRepositoryConnector connector, TaskRepository repository, ITask task,
			String comment, TaskAttribute attribute, IProgressMonitor monitor) throws CoreException {
		AbstractTaskAttachmentHandler attachmentHandler = connector.getTaskAttachmentHandler();
		ContextCorePlugin.getContextStore().saveContext(task.getHandleIdentifier());
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

	/**
	 * Implementors of this repositoryOperations must perform it locally without going to the server since it is used
	 * for frequent repositoryOperations such as decoration.
	 * 
	 * @return an empty set if no contexts
	 */
	public static Set<RepositoryAttachment> getLegacyContextAttachments(TaskRepository repository, ITask task) {
		TaskDataStorageManager taskDataManager = TasksUiPlugin.getTaskDataStorageManager();
		Set<RepositoryAttachment> contextAttachments = new HashSet<RepositoryAttachment>();
		if (taskDataManager != null) {
			RepositoryTaskData newData = taskDataManager.getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
			if (newData != null) {
				for (RepositoryAttachment attachment : newData.getAttachments()) {
					if (attachment.getDescription().equals(CONTEXT_DESCRIPTION)) {
						contextAttachments.add(attachment);
					} else if (attachment.getDescription().equals(CONTEXT_DESCRIPTION_LEGACY)) {
						contextAttachments.add(attachment);
					}
				}
			}
		}
		return contextAttachments;
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

	public static boolean hasContext(TaskRepository repository, ITask task) {
		if (repository == null || task == null) {
			return false;
		} else {
			Set<RepositoryAttachment> remoteContextAttachments = getLegacyContextAttachments(repository, task);
			return (remoteContextAttachments != null && remoteContextAttachments.size() > 0);
		}
	}

	public static boolean hasContextAttachment(ITask task) {
		Assert.isNotNull(task);
		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());
		if (connector instanceof AbstractLegacyRepositoryConnector) {
			Set<RepositoryAttachment> remoteContextAttachments = getLegacyContextAttachments(repository, task);
			return (remoteContextAttachments != null && remoteContextAttachments.size() > 0);
		} else {
			List<ITaskAttachment> contextAttachments = getContextAttachments(repository, task);
			return contextAttachments.size() > 0;
		}
	}

	private static final String MESSAGE_ATTACHMENTS_NOT_SUPPORTED = "Attachments not supported by connector: ";

	private static final String TITLE_DIALOG = "Mylyn Information";

	public static boolean downloadContext(final ITask task, final ITaskAttachment attachment,
			final IRunnableContext context) {
		final AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				task.getConnectorKind());
		final TaskRepository repository = TasksUi.getRepositoryManager().getRepository(attachment.getConnectorKind(),
				attachment.getRepositoryUrl());
		final String directory = TasksUiPlugin.getDefault().getDataDirectory();
		if (task.isActive()) {
			TasksUi.getTaskActivityManager().deactivateTask(task);
		}
		boolean result = false;

		if (connector instanceof AbstractRepositoryConnector) {
			if (connector.getTaskAttachmentHandler() != null) {
				result = AttachmentUtil.retrieveContext(connector.getTaskAttachmentHandler(), repository, task,
						attachment, directory, context);
			}
		}

		if (!result) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					TITLE_DIALOG, MESSAGE_ATTACHMENTS_NOT_SUPPORTED + connector.getLabel());
		} else {
			TasksUiInternal.getTaskList().notifyElementChanged(task);
			TasksUi.getTaskActivityManager().activateTask(task);
		}
		return true;
	}

	public static boolean uploadContext(final TaskRepository repository, final ITask task, final String comment,
			final IRunnableContext context) {

		final AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());

		ContextCorePlugin.getContextStore().saveContext(task.getHandleIdentifier());
		final File sourceContextFile = ContextCorePlugin.getContextStore()
				.getFileForContext(task.getHandleIdentifier());

		if (!sourceContextFile.exists()) {
			return false;
		}

		FileTaskAttachmentSource source = new FileTaskAttachmentSource(sourceContextFile);
		source.setDescription(CONTEXT_DESCRIPTION);
		source.setContentType(CONTEXT_CONTENT_TYPE);
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
			if (e.getCause() instanceof CoreException) {
				TasksUiInternal.displayStatus(TITLE_DIALOG, ((CoreException) e.getCause()).getStatus());
			} else {
				StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Unexpected error while attaching context", e));
			}
			return false;
		} catch (InterruptedException ignored) {
			// canceled
			return false;
		}
		return true;
	}

	public static boolean hasLocalContext(ITask task) {
		Assert.isNotNull(task);
		return ContextCore.getContextManager().hasContext(task.getHandleIdentifier());
	}

	@SuppressWarnings("restriction")
	@Deprecated
	public static boolean isContext(RepositoryAttachment attachment) {
		return CONTEXT_DESCRIPTION.equals(attachment.getDescription())
				|| CONTEXT_DESCRIPTION_LEGACY.equals(attachment.getDescription());
	}

	public static boolean isContext(ITaskAttachment attachment) {
		return CONTEXT_DESCRIPTION.equals(attachment.getDescription())
				|| CONTEXT_DESCRIPTION_LEGACY.equals(attachment.getDescription());
	}

	/**
	 * Retrieves a context stored in <code>attachment</code> from <code>task</code>.
	 * 
	 * @return false, if operation is not supported by repository
	 */
	public static boolean retrieveContext(AbstractTaskAttachmentHandler attachmentHandler, TaskRepository repository,
			ITask task, ITaskAttachment attachment, String destinationPath, IRunnableContext context) {

		File destinationContextFile = ContextCorePlugin.getContextStore().getFileForContext(task.getHandleIdentifier());
		// TODO: add functionality for not overwriting previous context
		if (destinationContextFile.exists()) {
			if (!destinationContextFile.delete()) {
				return false;
			}
		}

		final DownloadAttachmentJob downloadJob = new DownloadAttachmentJob(attachment, destinationContextFile);

		try {
			context.run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					if ((downloadJob).run(monitor) == Status.CANCEL_STATUS) {
						throw new InterruptedException();
					}
				}
			});

		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof CoreException) {
				TasksUiInternal.displayStatus(TITLE_DIALOG, ((CoreException) e.getCause()).getStatus());
			} else {
				StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Unexpected error while retrieving context", e));
			}
			return false;
		} catch (InterruptedException ignored) {
			// canceled
			return false;
		}

		return true;
	}

//	public static boolean getContext(AbstractRepositoryConnector connector, TaskRepository repository, ITask task,
//			TaskAttribute attribute, IProgressMonitor monitor) throws CoreException {
//		AbstractTaskAttachmentHandler attachmentHandler = connector.getTaskAttachmentHandler();
//		File file = ContextCorePlugin.getContextStore().getFileForContext(task.getHandleIdentifier());
//		try {
//			FileOutputStream out = new FileOutputStream(file);
//			try {
//				InputStream in = attachmentHandler.getContent(repository, task, attribute, monitor);
//				try {
//					int len;
//					byte[] buffer = new byte[BUFFER_SIZE];
//					while ((len = in.read(buffer)) != -1) {
//						out.write(buffer, 0, len);
//					}
//				} finally {
//					in.close();
//				}
//			} finally {
//				out.close();
//			}
//		} catch (IOException e) {
//			throw new CoreException(new RepositoryStatus(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
//					RepositoryStatus.ERROR_INTERNAL, "Could not create context file", e));
//		}
//		return true;
//	}

	public static boolean canUploadAttachment(ITask task) {
		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());
		if (connector instanceof AbstractLegacyRepositoryConnector) {
			AbstractAttachmentHandler attachmentHandler = ((AbstractLegacyRepositoryConnector) connector).getAttachmentHandler();
			if (attachmentHandler != null) {
				return attachmentHandler.canUploadAttachment(repository, task);
			}
		} else {
			AbstractTaskAttachmentHandler attachmentHandler = connector.getTaskAttachmentHandler();
			if (attachmentHandler != null) {
				return attachmentHandler.canPostContent(repository, task);
			}
		}
		return false;
	}

	public static boolean canDownloadAttachment(ITask task) {
		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());
		if (connector instanceof AbstractLegacyRepositoryConnector) {
			AbstractAttachmentHandler attachmentHandler = ((AbstractLegacyRepositoryConnector) connector).getAttachmentHandler();
			if (attachmentHandler != null) {
				return attachmentHandler.canDownloadAttachment(repository, task);
			}
		} else {
			AbstractTaskAttachmentHandler attachmentHandler = connector.getTaskAttachmentHandler();
			if (attachmentHandler != null) {
				return attachmentHandler.canGetContent(repository, task);
			}
		}
		return false;
	}
}
