/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.internal.tasklist.ui.TaskUiUtil;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskCategory;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchImages;

/**
 * @author Mik Kersten
 */
public class DeleteAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.delete";

	public DeleteAction() {
		setText("Delete");
		setId(ID);
		setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
	}

	@Override
	public void run() {
		ISelection selection = TaskListView.getFromActivePerspective().getViewer().getSelection();
		for (Object selectedObject : ((IStructuredSelection) selection).toList()) {
			if (selectedObject instanceof ITask || selectedObject instanceof AbstractQueryHit) {
				ITask task = null;
				if (selectedObject instanceof AbstractQueryHit) {
					task = ((AbstractQueryHit) selectedObject).getCorrespondingTask();
				} else {
					task = (ITask) selectedObject;
				}
				if (task == null) {
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"Mylar Tasks", "No task data to delete.");
					return;
				}

				if (task.isActive()) {
					MylarTaskListPlugin.getTaskListManager().deactivateTask(task);
					TaskListView.getFromActivePerspective().refreshAndFocus(false);
					TaskUiUtil.closeEditorInActivePage(task);					
				}

				String message = genDeleteConfirmationMessage(task);
				boolean deleteConfirmed = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getShell(), "Confirm Delete", message);
				if (!deleteConfirmed) {
					return;
				}

				MylarTaskListPlugin.getTaskListManager().deactivateTask(task);
				MylarTaskListPlugin.getTaskListManager().getTaskList().deleteTask(task);
				MylarPlugin.getContextManager().deleteContext(task.getHandleIdentifier());
				TaskUiUtil.closeEditorInActivePage(task);
			} else if (selectedObject instanceof AbstractRepositoryQuery) {
				boolean deleteConfirmed = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getShell(), "Confirm delete", "Delete the selected query? Task data will not be deleted.");
				if (deleteConfirmed) {
					MylarTaskListPlugin.getTaskListManager().getTaskList().deleteQuery((AbstractRepositoryQuery) selectedObject);
				}
			} else if (selectedObject instanceof TaskCategory) {
				boolean deleteConfirmed = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getShell(), "Confirm Delete", "Delete the selected category?  Contained tasks will be moved to the root.");
				if (!deleteConfirmed)
					return;

				TaskCategory cat = (TaskCategory) selectedObject;
				for (ITask task : cat.getChildren()) {
					MylarPlugin.getContextManager().deleteContext(task.getHandleIdentifier());
					TaskUiUtil.closeEditorInActivePage(task);
				}
				MylarTaskListPlugin.getTaskListManager().getTaskList().deleteCategory(cat);
			} else {
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Delete failed",
						"Nothing selected.");
				return;
			}
		}
	}

	public static String genDeleteConfirmationMessage(ITask task) {
		return "Delete the selected task and discard task context?\n\n" + task.getDescription();
	}
}
