/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.io.File;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.ui.deprecated.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewAttachmentWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewAttachmentWizardDialog;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Control;

/**
 * Not API.
 * 
 * @author Mik Kersten
 * @author Maarten Meijer
 */
public class RepositoryTaskEditorDropListener implements DropTargetListener {

	private final AbstractRepositoryTaskEditor editor;

	private final FileTransfer fileTransfer;

	private final TextTransfer textTransfer;

	private final Control control;

	private final TaskRepository repository;

	private final RepositoryTaskData taskData;

	public RepositoryTaskEditorDropListener(AbstractRepositoryTaskEditor editor, TaskRepository repository,
			RepositoryTaskData taskData, FileTransfer fileTransfer, TextTransfer textTransfer, Control control) {
		this.editor = editor;
		this.repository = repository;
		this.taskData = taskData;
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
		for (TransferData dataType : event.dataTypes) {
			if (fileTransfer.isSupportedType(dataType)) {
				event.currentDataType = dataType;
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
			AbstractTask task = TasksUi.getTaskList().getTask(repository.getRepositoryUrl(),
					taskData.getTaskId());
			if (!(task != null)) {
				// Should not happen
				return;
			}

			editor.setGlobalBusy(true);
			NewAttachmentWizard naw = new NewAttachmentWizard(repository, task, text);
			openDialog(naw, null);
		}
		if (fileTransfer.isSupportedType(event.currentDataType)) {
			String[] files = (String[]) event.data;
			if (files.length > 0) {
				AbstractTask task = TasksUi.getTaskList().getTask(repository.getRepositoryUrl(),
						taskData.getTaskId());
				if (task == null) {
					// Should not happen
					return;
				}

				NewAttachmentWizard naw = new NewAttachmentWizard(repository, task, new File(files[0]));
				String error = null;
				if (files.length > 1) {
					error = "Note that only the first file dragged will be attached.";
				}
				openDialog(naw, error);
			}
		}
	}

	/**
	 * @param naw
	 * 		wizard to attach dialog to.
	 * @param message
	 * 		error to display or none if <code>null</code>
	 */
	private void openDialog(NewAttachmentWizard naw, String message) {
		editor.setGlobalBusy(true);
		NewAttachmentWizardDialog dialog = new NewAttachmentWizardDialog(control.getShell(), naw, true);
		naw.setDialog(dialog);
		dialog.create();
		if (null != message) {
			dialog.setMessage(message, IMessageProvider.WARNING);
		}
		int result = dialog.open();
		if (result != Window.OK) {
			editor.setGlobalBusy(false);
		}
	}
}