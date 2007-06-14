/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewAttachmentWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewAttachmentWizardDialog;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Control;

/**
 * @author Mik Kersten
 */
class RepositoryTaskEditorDropListener implements DropTargetListener {
	/**
	 * 
	 */
	private final AbstractTaskEditor AbstractTaskEditor;

	private final FileTransfer fileTransfer;

	private final TextTransfer textTransfer;

	private final Control control;

	public RepositoryTaskEditorDropListener(AbstractTaskEditor AbstractTaskEditor,
			FileTransfer fileTransfer, TextTransfer textTransfer, Control control) {
		this.AbstractTaskEditor = AbstractTaskEditor;
		this.fileTransfer = fileTransfer;
		this.textTransfer = textTransfer;
		this.control = control;
	}

	public void dragEnter(DropTargetEvent event) {
		if (event.detail == DND.DROP_DEFAULT) {
			if ((event.operations & DND.DROP_COPY) != 0) {
				event.detail = DND.DROP_COPY;
			} else {
				event.detail = DND.DROP_NONE;
			}
		}
		// will accept text but prefer to have files dropped
		for (int i = 0; i < event.dataTypes.length; i++) {
			if (fileTransfer.isSupportedType(event.dataTypes[i])) {
				event.currentDataType = event.dataTypes[i];
				// files should only be copied
				if (event.detail != DND.DROP_COPY) {
					event.detail = DND.DROP_NONE;
				}
				break;
			}
		}
	}

	public void dragOver(DropTargetEvent event) {
		event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
		// if (textTransfer.isSupportedType(event.currentDataType)) {
		// // NOTE: on unsupported platforms this will return null
		// Object o = textTransfer.nativeToJava(event.currentDataType);
		// String t = (String)o;
		// if (t != null) System.out.println(t);
		// }
	}

	public void dragOperationChanged(DropTargetEvent event) {
		if ((event.detail == DND.DROP_DEFAULT) || (event.operations & DND.DROP_COPY) != 0) {

			event.detail = DND.DROP_COPY;
		} else {
			event.detail = DND.DROP_NONE;
		}

		// allow text to be moved but files should only be copied
		if (fileTransfer.isSupportedType(event.currentDataType)) {
			if (event.detail != DND.DROP_COPY) {
				event.detail = DND.DROP_NONE;
			}
		}
	}

	public void dragLeave(DropTargetEvent event) {
	}

	public void dropAccept(DropTargetEvent event) {
	}

	public void drop(DropTargetEvent event) {
		if (textTransfer.isSupportedType(event.currentDataType)) {
			String text = (String) event.data;
			AbstractTask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(
					this.AbstractTaskEditor.repository.getUrl(),
					this.AbstractTaskEditor.taskData.getId());
			if (!(task instanceof AbstractTask)) {
				// Should not happen
				return;
			}

			AbstractTaskEditor.setGlobalBusy(true);
			NewAttachmentWizard naw = new NewAttachmentWizard(this.AbstractTaskEditor.repository,
					(AbstractTask) task, text);
			openDialog(naw);
		}
		if (fileTransfer.isSupportedType(event.currentDataType)) {
			String[] files = (String[]) event.data;
			if (files.length > 0) {
				AbstractTask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(
						this.AbstractTaskEditor.repository.getUrl(),
						this.AbstractTaskEditor.taskData.getId());
				if (!(task instanceof AbstractTask)) {
					// Should not happen
					return;
				}

				NewAttachmentWizard naw = new NewAttachmentWizard(this.AbstractTaskEditor.repository,
						(AbstractTask) task, new File(files[0]));
				openDialog(naw);

			}
		}
	}

	private void openDialog(NewAttachmentWizard naw) {
		AbstractTaskEditor.setGlobalBusy(true);
		NewAttachmentWizardDialog dialog = new NewAttachmentWizardDialog(control.getShell(), naw);
		naw.setDialog(dialog);
		dialog.create();
		int result = dialog.open();
		if (result != MessageDialog.OK) {
			AbstractTaskEditor.setGlobalBusy(false);
		}
	}
}