/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Peter Stibrany - improvements for bug 271197
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.io.File;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.DownloadAttachmentJob;
import org.eclipse.mylyn.internal.tasks.ui.util.Messages;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Peter Stibrany
 */
public class SaveAttachmentsAction extends Action {

	public SaveAttachmentsAction(String text) {
		super(text);
	}

	@Override
	public void run() {
		List<ITaskAttachment> attachments = AttachmentUtil.getSelectedAttachments(null);
		if (attachments.isEmpty()) {
			return;
		} else if (attachments.size() == 1) {
			saveSingleAttachment(attachments.get(0));
		} else {
			saveAllAttachments(attachments);
		}
	}

	/**
	 * Displays Save File dialog, then downloads and saves single attachment.
	 */
	private void saveSingleAttachment(ITaskAttachment attachment) {
		FileDialog fileChooser = new FileDialog(WorkbenchUtil.getShell(), SWT.SAVE);
		fileChooser.setFileName(AttachmentUtil.getAttachmentFilename(attachment));

		File initDirectory = getInitialDirectory();
		if (initDirectory != null) {
			fileChooser.setFilterPath(initDirectory.getAbsolutePath());
		}

		String filePath = fileChooser.open();

		// check if the dialog was canceled or an error occurred
		if (filePath == null) {
			return;
		}

		File file = new File(filePath);
		if (file.exists()) {
			if (!MessageDialog.openConfirm(WorkbenchUtil.getShell(), Messages.TasksUiMenus_File_exists_,
					Messages.TasksUiMenus_Overwrite_existing_file_ + file.getName())) {
				return;
			}
		}

		initDirectory = file.getParentFile();
		if (initDirectory != null) {
			saveInitialDirectory(initDirectory.getAbsolutePath());
		}

		DownloadAttachmentJob job = new DownloadAttachmentJob(attachment, file);
		job.setUser(true);
		job.schedule();
	}

	private void saveAllAttachments(List<ITaskAttachment> attachments) {
		DirectoryDialog dialog = new DirectoryDialog(WorkbenchUtil.getShell());
		dialog.setText(Messages.SaveAttachmentsAction_selectDirectory);
		dialog.setMessage(Messages.SaveAttachmentsAction_selectDirectoryHint);

		File initDirectory = getInitialDirectory();
		if (initDirectory != null) {
			dialog.setFilterPath(initDirectory.getAbsolutePath());
		}

		String directoryPath = dialog.open();
		if (directoryPath == null) {
			return;
		}

		saveInitialDirectory(directoryPath);

		final File directory = new File(directoryPath);
		if (!directory.exists()) {
			MessageDialog.openError(WorkbenchUtil.getShell(), Messages.SaveAttachmentsAction_directoryDoesntExist,
					NLS.bind(Messages.SaveAttachmentsAction_directoryDoesntExist0, directoryPath));
			return;
		}

		for (ITaskAttachment attachment : attachments) {
			String filename = AttachmentUtil.getAttachmentFilename(attachment);
			File file = getTargetFile(WorkbenchUtil.getShell(), directory, filename);
			if (file != null) {
				DownloadAttachmentJob job = new DownloadAttachmentJob(attachment, file);
				job.setUser(true);
				job.schedule();
			}
		}
	}

	private File getTargetFile(Shell shell, File directory, String filename) {
		File attachFile = new File(directory, filename);
		while (true) {
			if (!attachFile.exists()) {
				return attachFile;
			}

			boolean overwrite = MessageDialog.openQuestion(
					shell,
					NLS.bind(Messages.SaveAttachmentsAction_overwriteFile0, attachFile.getName()),
					NLS.bind(Messages.SaveAttachmentsAction_fileExists_doYouWantToOverwrite0,
							attachFile.getAbsolutePath()));
			if (overwrite) {
				return attachFile;
			}

			FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
			fileDialog.setFilterPath(directory.getAbsolutePath());
			fileDialog.setFileName(attachFile.getName());

			filename = fileDialog.open();
			if (filename == null) {
				return null;
			}

			attachFile = new File(filename);
		}
	}

	private File getInitialDirectory() {
		String dirName = TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.getString(ITasksUiPreferenceConstants.DEFAULT_ATTACHMENTS_DIRECTORY);

		if (dirName == null || dirName.trim().length() == 0) {
			return null;
		}

		File dirFile = new File(dirName).getAbsoluteFile();

		// if file
		while (dirFile != null && !dirFile.exists()) {
			dirFile = dirFile.getParentFile();
		}

		return dirFile;
	}

	private void saveInitialDirectory(String directory) {
		TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.putValue(ITasksUiPreferenceConstants.DEFAULT_ATTACHMENTS_DIRECTORY, directory);
	}
}