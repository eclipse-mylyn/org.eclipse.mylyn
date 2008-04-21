/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttachment;

/**
 * @author Steffen Pingel
 */
public class SubmitTaskAttachmentJob extends Job {

	private static final String LABEL_JOB_SUBMIT = "Submitting to repository";

	private final TaskAttachment taskAttachment;

	private final AbstractRepositoryConnector connector;

	private final TaskRepository taskRepository;

	private AbstractTask task;

	public SubmitTaskAttachmentJob(AbstractRepositoryConnector connector, TaskRepository taskRepository,
			TaskAttachment taskAttachment) {
		super(LABEL_JOB_SUBMIT);
		this.connector = connector;
		this.taskRepository = taskRepository;
		this.taskAttachment = taskAttachment;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		return null;
//		try {
//			if (monitor == null) {
//				monitor = new NullProgressMonitor();
//			}
//			monitor.beginTask("Attaching file...", 2);
//			task.setSubmitting(true);
//			task.setSynchronizationState(RepositoryTaskSyncState.OUTGOING);
//
//			if (screenshotMode || InputAttachmentSourcePage.SCREENSHOT_LABEL.equals(path)) {
//				((ImageAttachment) taskAttachment).ensureImageFileWasCreated();
//			} else if (InputAttachmentSourcePage.CLIPBOARD_LABEL.equals(path)) {
//				String contents = inputPage.getClipboardContents();
//				if (contents == null) {
//					throw new InvocationTargetException(new CoreException(new RepositoryStatus(IStatus.ERROR,
//							TasksUiPlugin.ID_PLUGIN, RepositoryStatus.ERROR_INTERNAL, "Clipboard is empty", null)));
//				}
//				taskAttachment.setContent(contents.getBytes());
//				taskAttachment.setFilename(CLIPBOARD_FILENAME);
//			} else {
//				File file = new File(path);
//				taskAttachment.setFile(file);
//				taskAttachment.setFilename(file.getName());
//			}
//
//			attachmentHandler.uploadAttachment(taskRepository, task, taskAttachment, taskAttachment.getComment(),
//					new SubProgressMonitor(monitor, 1));
//
//			if (monitor.isCanceled()) {
//				throw new OperationCanceledException();
//			}
//
//			if (attachContext && connector.getAttachmentHandler() != null) {
//				connector.getAttachmentHandler().attachContext(taskRepository, task, "",
//						new SubProgressMonitor(monitor, 1));
//			}
//		} catch (CoreException e) {
//			return e.getStatus();
//		} finally {
//			task.setSubmitting(false);
//			task.setSynchronizationState(RepositoryTaskSyncState.SYNCHRONIZED);
//
//			monitor.done();
//		}
	}

//	private void synchronizeTask() {
//		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, false, new JobChangeAdapter() {
//			@Override
//			public void done(final IJobChangeEvent event) {
//				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//					public void run() {
//						if (event.getResult().getException() != null) {
//
//							MessageDialog.openError(Display.getDefault().getActiveShell(),
//									ITasksUiConstants.TITLE_DIALOG, event.getResult().getMessage());
//
//						}
//						forceRefreshInplace(task);
//					}
//				});
//			}
//		});
//	}

}
