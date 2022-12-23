/*******************************************************************************
 * Copyright (c) 2010, 2011 Peter Stibrany and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Peter Stibrany - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.commons.core.ICoreRunnable;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * @author Peter Stibrany
 */
class DownloadAndOpenTaskAttachmentJob implements ICoreRunnable {

	private final ITaskAttachment attachment;

	private final IWorkbenchPage page;

	private final String editorID;

	private final String jobName;

	DownloadAndOpenTaskAttachmentJob(String jobName, ITaskAttachment attachment, IWorkbenchPage page, String editorID) {
		this.jobName = jobName;
		this.attachment = attachment;
		this.page = page;
		this.editorID = editorID;
	}

	public void run(IProgressMonitor monitor) throws CoreException {
		monitor.beginTask(jobName, IProgressMonitor.UNKNOWN);
		try {
			IStatus result = execute(new SubProgressMonitor(monitor, 100));
			if (result != null && !result.isOK()) {
				throw new CoreException(result);
			}
		} finally {
			monitor.done();
		}
	}

	protected IStatus execute(IProgressMonitor monitor) {
		final String attachmentFilename = AttachmentUtil.getAttachmentFilename(attachment);

		File file = null;
		try {
			// create temporary filename like 'attach-127562364-attachment-name.txt'
			// This has correct extension based on attachment filename, resembles attachment name, but
			// also indicates that it is temporary file
			file = File.createTempFile("tmpattach-", "-" + attachmentFilename); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (IOException e) {
			return new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					Messages.DownloadAndOpenTaskAttachmentJob_failedToDownloadAttachment, e);
		}
		file.deleteOnExit();

		boolean ok = false;
		BufferedOutputStream fos = null;
		try {
			fos = new BufferedOutputStream(new FileOutputStream(file));
			AttachmentUtil.downloadAttachment(attachment, fos, monitor);
			ok = true;

		} catch (IOException e) {
			return new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					Messages.DownloadAndOpenTaskAttachmentJob_failedToDownloadAttachment, e);
		} catch (CoreException e) {
			int s = IStatus.ERROR;
			if (e.getStatus() != null && e.getStatus().getCode() == IStatus.CANCEL) {
				throw new OperationCanceledException();
			}
			return new Status(s, TasksUiPlugin.ID_PLUGIN,
					Messages.DownloadAndOpenTaskAttachmentJob_failedToDownloadAttachment, e);
		} finally {
			// (fos != null) only when there is some problem, in other cases we nulled fos already
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					if (ok) {
						file.delete();
						return new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
								Messages.DownloadAndOpenTaskAttachmentJob_failedToDownloadAttachment, e);
					}
				}
			}

			if (!ok) {
				file.delete();
			}
		}

		// mark file read-only to warn user that he is working with local copy
		file.setReadOnly();

		Display disp = page.getWorkbenchWindow().getWorkbench().getDisplay();
		if (disp.isDisposed()) {
			return new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
					Messages.DownloadAndOpenTaskAttachmentJob_cannotOpenEditor);
		}

		if (disp.getThread() == Thread.currentThread()) {
			return openEditor(file, attachmentFilename);
		} else {
			final AtomicReference<IStatus> status = new AtomicReference<IStatus>();
			final File tmpFile = file;

			disp.syncExec(new Runnable() {
				public void run() {
					status.set(openEditor(tmpFile, attachmentFilename));
				};
			});

			if (status.get() == null) {
				return new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						Messages.DownloadAndOpenTaskAttachmentJob_cannotOpenEditor);
			}

			return status.get();
		}
	}

	IStatus openEditor(File file, String attachmentName) {
		try {
			String taskLabel = getTaskLabel(attachment.getTask());
			String repoLabel = getRepositoryLabel(attachment.getTask());

			String tooltip = MessageFormat.format(Messages.DownloadAndOpenTaskAttachmentJob_editorTooltip, taskLabel,
					repoLabel);

			page.openEditor(new AttachmentFileEditorInput(file, attachmentName, tooltip), editorID);
			return Status.OK_STATUS;
		} catch (PartInitException e) {
			return new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					Messages.DownloadAndOpenTaskAttachmentJob_cannotOpenEditor, e);
		}
	}

	private String getTaskLabel(ITask task) {
		AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(task.getConnectorKind());
		StringBuilder taskLabel = new StringBuilder();
		if (connectorUi != null) {
			taskLabel.append(connectorUi.getTaskKindLabel(task));
		}

		String key = task.getTaskKey();
		if (key != null) {
			if (taskLabel.length() > 0) {
				taskLabel.append(" "); //$NON-NLS-1$
			}
			taskLabel.append(key);
		}
		return taskLabel.toString();
	}

	// copied from TaskListToolTip
	private String getRepositoryLabel(ITask task) {
		String repositoryKind = task.getConnectorKind();
		String repositoryUrl = task.getRepositoryUrl();

		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(repositoryKind, repositoryUrl);
		if (repository != null) {
			String label = repository.getRepositoryLabel();
			if (label.indexOf("//") != -1) { //$NON-NLS-1$
				return label.substring((repository.getRepositoryUrl().indexOf("//") + 2)); //$NON-NLS-1$
			}
			return label;
		}
		return ""; //$NON-NLS-1$
	}
}
