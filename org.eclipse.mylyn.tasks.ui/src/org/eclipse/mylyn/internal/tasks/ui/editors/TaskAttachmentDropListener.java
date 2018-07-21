/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Maarten Meijer - improvements
 *     Robert Munteanu - fix for bug 359539
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.data.FileTaskAttachmentSource;
import org.eclipse.mylyn.internal.tasks.core.data.TextTaskAttachmentSource;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewAttachmentWizardDialog;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TaskDropListener.Operation;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * @author Mik Kersten
 * @author Maarten Meijer
 * @author Steffen Pingel
 */
public class TaskAttachmentDropListener implements DropTargetListener {

	private final AbstractTaskEditorPage page;

	public TaskAttachmentDropListener(AbstractTaskEditorPage page) {
		this.page = page;
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
			if (LocalSelectionTransfer.getTransfer().isSupportedType(dataType)
					|| FileTransfer.getInstance().isSupportedType(dataType)) {
				event.currentDataType = dataType;
				// files and tasks should only be copied
				if (event.detail != DND.DROP_COPY) {
					event.detail = DND.DROP_NONE;
				}
				break;
			}
		}
	}

	public void dragOver(DropTargetEvent event) {
		event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
	}

	public void dragOperationChanged(DropTargetEvent event) {
		if ((event.detail == DND.DROP_DEFAULT) || (event.operations & DND.DROP_COPY) != 0) {
			event.detail = DND.DROP_COPY;
		} else {
			event.detail = DND.DROP_NONE;
		}
		// allow text to be moved but files should only be copied
		if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
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
		if (LocalSelectionTransfer.getTransfer().isSupportedType(event.currentDataType)) {
			TasksUiInternal.getTaskDropHandler().loadTaskDropListeners();
			ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
			List<ITask> tasksToMove = TasksUiInternal.getTasksFromSelection(selection);
			if (!tasksToMove.isEmpty()) {
				TasksUiInternal.getTaskDropHandler().fireTaskDropped(tasksToMove, page.getTask(),
						Operation.DROP_ON_TASK_EDITOR);
			} else {
				attachFirstFile(getFilesFromSelection(selection));
			}
		}
		if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
			String text = (String) event.data;
			EditorUtil.openNewAttachmentWizard(page, null, new TextTaskAttachmentSource(text));
		}
		if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
			String[] files = (String[]) event.data;
			attachFirstFile(files);
		}
	}

	protected void attachFirstFile(String[] files) {
		if (files != null && files.length > 0) {
			File file = new File(files[0]);
			NewAttachmentWizardDialog dialog = EditorUtil.openNewAttachmentWizard(page, null,
					new FileTaskAttachmentSource(file));
			if (files.length > 1) {
				dialog.setMessage(
						Messages.TaskAttachmentDropListener_Note_that_only_the_first_file_dragged_will_be_attached,
						IMessageProvider.WARNING);
			}
		}
	}

	public static String[] getFilesFromSelection(ISelection selection) {
		List<String> files = new ArrayList<String>();
		if (selection instanceof IStructuredSelection) {
			for (Object element : ((IStructuredSelection) selection).toList()) {
				IResource resource = null;
				if (element instanceof IResource) {
					resource = (IResource) element;
				} else if (element instanceof IAdaptable) {
					IAdaptable adaptable = (IAdaptable) element;
					resource = (IResource) adaptable.getAdapter(IResource.class);
				}
				if (resource != null && resource.getRawLocation() != null) {
					files.add(resource.getRawLocation().toOSString());
				}
			}
		}
		return files.toArray(new String[files.size()]);
	}

}
