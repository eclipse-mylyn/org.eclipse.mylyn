/*******************************************************************************
 * Copyright (c) 2010, 2013 Peter Stibrany and others.
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

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Peter Stibrany
 */
public class TaskAttachmentEditorViewer implements ITaskAttachmentViewer {

	private static final String PREF_DO_NOT_WARN_BEFORE_OPENING_ATTACHMENTS = "do.not.warn.before.opening.attachments"; //$NON-NLS-1$

	private final IEditorDescriptor descriptor;

	private final boolean isWorkbenchDefault;

	private final boolean isSystem;

	TaskAttachmentEditorViewer(IEditorDescriptor descriptor) {
		this(descriptor, false);
	}

	TaskAttachmentEditorViewer(IEditorDescriptor descriptor, boolean isWorkbenchDefault) {
		this(descriptor, isWorkbenchDefault, false);
	}

	TaskAttachmentEditorViewer(IEditorDescriptor descriptor, boolean isWorkbenchDefault, boolean isSystem) {
		this.descriptor = descriptor;
		this.isWorkbenchDefault = isWorkbenchDefault;
		this.isSystem = isSystem;
	}

	public String getId() {
		return descriptor.getId();
	}

	public String getLabel() {
		return descriptor.getLabel();
	}

	public void openAttachment(final IWorkbenchPage page, final ITaskAttachment attachment) throws CoreException {
		if (promptToConfirmOpen(attachment)) {
			DownloadAndOpenTaskAttachmentJob job = new DownloadAndOpenTaskAttachmentJob(MessageFormat.format(
					Messages.TaskAttachmentEditorViewer_openingAttachment,
					AttachmentUtil.getAttachmentFilename(attachment)), attachment, page, descriptor.getId());
			WorkbenchUtil.busyCursorWhile(job);
		}
	}

	private boolean promptToConfirmOpen(final ITaskAttachment attachment) {
		if (isSystem()) {
			IPreferenceStore store = TasksUiPlugin.getDefault().getPreferenceStore();
			if (!store.getBoolean(PREF_DO_NOT_WARN_BEFORE_OPENING_ATTACHMENTS)) {
				MessageDialogWithToggle dialog = MessageDialogWithToggle.openYesNoQuestion(
						WorkbenchUtil.getShell(),
						Messages.TaskAttachmentEditorViewer_Open_Attachment,
						NLS.bind(Messages.TaskAttachmentEditorViewer_Some_files_can_harm_your_computer,
								attachment.getFileName()), Messages.TaskAttachmentEditorViewer_Do_not_warn_me_again,
						false, null, null);
				if (dialog.getReturnCode() == IDialogConstants.YES_ID) {
					if (dialog.getToggleState()) {
						store.setValue(PREF_DO_NOT_WARN_BEFORE_OPENING_ATTACHMENTS, true);
						TasksUiPlugin.getDefault().savePluginPreferences();
					}
				} else {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isWorkbenchDefault() {
		return isWorkbenchDefault;
	}

	protected boolean isSystem() {
		return isSystem;
	}
}
