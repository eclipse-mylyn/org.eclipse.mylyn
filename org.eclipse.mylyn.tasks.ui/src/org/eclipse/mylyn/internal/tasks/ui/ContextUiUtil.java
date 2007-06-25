/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractAttachmentHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * 
 * @author Steffen Pingel
 */
public class ContextUiUtil {

	public static boolean downloadContext(final AbstractTask task, final RepositoryAttachment attachment,
			final IRunnableContext context) {
		final AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(task);
		final TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
				attachment.getRepositoryUrl());
		final String directory = TasksUiPlugin.getDefault().getDataDirectory();
		try {
			if (task.isActive()) {
				TasksUiPlugin.getTaskListManager().deactivateTask(task);
			}

			final boolean[] result = new boolean[1];
			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						if (connector.getAttachmentHandler() != null) {
							result[0] = connector.getAttachmentHandler().retrieveContext(repository, task, attachment,
									directory, monitor);
						} else {
							result[0] = false;
						}
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					}
				}
			};
			context.run(true, true, runnable);

			if (!result[0]) {
				MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						ITasksUiConstants.TITLE_DIALOG, AbstractAttachmentHandler.MESSAGE_ATTACHMENTS_NOT_SUPPORTED
								+ connector.getLabel());
			} else {
				TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(task, false);
				TasksUiPlugin.getTaskListManager().activateTask(task);
			}
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof CoreException) {
				StatusHandler.displayStatus(ITasksUiConstants.TITLE_DIALOG, ((CoreException) e.getCause()).getStatus());
			} else {
				StatusHandler.fail(e, "Unexpected error while attaching context", true);
			}
			return false;
		} catch (InterruptedException ignored) {
			// canceled
			return false;
		}
		return true;
	}

	public static boolean uploadContext(final TaskRepository repository, final AbstractTask task, final String comment,
			final IRunnableContext context) {
		final AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());
		try {
			final boolean[] result = new boolean[1];
			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						if (connector.getAttachmentHandler() != null) {
							result[0] = connector.getAttachmentHandler().attachContext(repository, task, comment,
									monitor);
						} else {
							result[0] = false;
						}
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					}
				}
			};
			context.run(true, true, runnable);

			if (!result[0]) {
				MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						ITasksUiConstants.TITLE_DIALOG, AbstractAttachmentHandler.MESSAGE_ATTACHMENTS_NOT_SUPPORTED
								+ connector.getLabel());
			} else {
				task.setSynchronizationState(RepositoryTaskSyncState.SYNCHRONIZED);
				IWorkbenchSite site = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow()
						.getActivePage()
						.getActivePart()
						.getSite();
				if (site instanceof IViewSite) {
					IStatusLineManager statusLineManager = ((IViewSite) site).getActionBars().getStatusLineManager();
					statusLineManager.setMessage(TasksUiImages.getImage(TasksUiImages.TASKLIST),
							"Context attached to task: " + task.getSummary());
					TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);
				}
			}
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof CoreException) {
				StatusHandler.displayStatus(ITasksUiConstants.TITLE_DIALOG, ((CoreException) e.getCause()).getStatus());
			} else {
				StatusHandler.fail(e, "Unexpected error while attaching context", true);
			}
			return false;
		} catch (InterruptedException ignored) {
			// canceled
			return false;
		}
		return true;
	}

}