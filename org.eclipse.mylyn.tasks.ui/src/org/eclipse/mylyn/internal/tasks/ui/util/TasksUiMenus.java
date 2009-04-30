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

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class TasksUiMenus {

	private static final String ATTACHMENT_DEFAULT_NAME = "attachment"; //$NON-NLS-1$

	private static final String CTYPE_ZIP = "zip"; //$NON-NLS-1$

	private static final String CTYPE_OCTET_STREAM = "octet-stream"; //$NON-NLS-1$

	private static final String CTYPE_TEXT = "text"; //$NON-NLS-1$

	private static final String CTYPE_HTML = "html"; //$NON-NLS-1$

	public static void fillTaskAttachmentMenu(IMenuManager manager) {
		final Action saveAction = new Action(Messages.TasksUiMenus_Save_) {
			@Override
			public void run() {
				ITaskAttachment attachment = getSelectedAttachment();
				if (attachment == null) {
					return;
				}

				/* Launch Browser */
				FileDialog fileChooser = new FileDialog(WorkbenchUtil.getShell(), SWT.SAVE);
				String fname = attachment.getFileName();
				// default name if none is found
				if (fname.equals("")) { //$NON-NLS-1$
					String ctype = attachment.getContentType();
					if (ctype.endsWith(CTYPE_HTML)) {
						fname = ATTACHMENT_DEFAULT_NAME + ".html"; //$NON-NLS-1$
					} else if (ctype.startsWith(CTYPE_TEXT)) {
						fname = ATTACHMENT_DEFAULT_NAME + ".txt"; //$NON-NLS-1$
					} else if (ctype.endsWith(CTYPE_OCTET_STREAM)) {
						fname = ATTACHMENT_DEFAULT_NAME;
					} else if (ctype.endsWith(CTYPE_ZIP)) {
						fname = ATTACHMENT_DEFAULT_NAME + "." + CTYPE_ZIP; //$NON-NLS-1$
					} else {
						fname = ATTACHMENT_DEFAULT_NAME + "." + ctype.substring(ctype.indexOf("/") + 1); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				fileChooser.setFileName(fname);
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

				DownloadAttachmentJob job = new DownloadAttachmentJob(attachment, file);
				job.setUser(true);
				job.schedule();
			}
		};

		final Action copyURLToClipAction = new Action(Messages.TasksUiMenus_Copy_URL) {
			@Override
			public void run() {
				ITaskAttachment attachment = getSelectedAttachment();
				if (attachment != null) {
					Clipboard clip = new Clipboard(PlatformUI.getWorkbench().getDisplay());
					clip.setContents(new Object[] { attachment.getUrl() },
							new Transfer[] { TextTransfer.getInstance() });
					clip.dispose();
				}
			}
		};

		final Action copyToClipAction = new Action(Messages.TasksUiMenus_Copy_Contents) {
			@Override
			public void run() {
				ITaskAttachment attachment = getSelectedAttachment();
				if (attachment != null) {
					CopyAttachmentToClipboardJob job = new CopyAttachmentToClipboardJob(attachment);
					job.setUser(true);
					job.schedule();
				}
			}
		};

		manager.add(new Separator("group.open")); //$NON-NLS-1$
		manager.add(new Separator("group.save")); //$NON-NLS-1$
		manager.add(saveAction);
		manager.add(copyURLToClipAction);
		manager.add(copyToClipAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private static ITaskAttachment getSelectedAttachment() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelection windowSelection = window.getSelectionService().getSelection();
		IStructuredSelection selection = null;
		if (windowSelection instanceof IStructuredSelection) {
			selection = (IStructuredSelection) windowSelection;
		}
		if (selection == null || selection.isEmpty()) {
			return null;
		}
		if (selection.getFirstElement() instanceof ITaskAttachment) {
			return (ITaskAttachment) selection.getFirstElement();
		}
		return null;
	}

}
