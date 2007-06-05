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

package org.eclipse.mylar.internal.tasks.ui.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.core.TaskCategory;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.tasks.ui.TasksUiUtil;
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

		List<?> toDelete = ((IStructuredSelection) selection).toList();

		String message = "Delete the elements listed?  If categories or queries are selected contained tasks"
				+ " will not be deleted.  Contexts will be deleted for selected tasks.\n\n";
// + "This operation cannot be undone (the Task List can be restored from
// history).\n\n";
		int i = 0;
		for (Object object : toDelete) {
			i++;
			if (i < 20) {
				if (object instanceof ITaskListElement) {
					message += "    " + ((ITaskListElement) object).getSummary() + "\n";
				}
			} else {
				message += "...";
				break;
			}
		}
		boolean deleteConfirmed = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell(), "Confirm Delete", message);
		if (!deleteConfirmed) {
			return;
		}

		for (Object selectedObject : toDelete) {
			if (selectedObject instanceof ITask) {
				ITask task = null;
				task = (ITask) selectedObject;
				TasksUiPlugin.getTaskListManager().deactivateTask(task);
				TasksUiPlugin.getTaskListManager().getTaskList().deleteTask(task);
				ContextCorePlugin.getContextManager().deleteContext(task.getHandleIdentifier());
				TasksUiUtil.closeEditorInActivePage(task);
			} else if (selectedObject instanceof AbstractRepositoryQuery) {
				// boolean deleteConfirmed =
				// MessageDialog.openQuestion(PlatformUI.getWorkbench()
				// .getActiveWorkbenchWindow().getShell(), "Confirm delete",
				// "Delete the selected query? Task data will not be deleted.");
				// if (deleteConfirmed) {
				TasksUiPlugin.getTaskListManager().getTaskList().deleteQuery((AbstractRepositoryQuery) selectedObject);
				// }
			} else if (selectedObject instanceof TaskCategory) {
				// boolean deleteConfirmed =
				// MessageDialog.openQuestion(PlatformUI.getWorkbench()
				// .getActiveWorkbenchWindow().getShell(), "Confirm Delete",
				// "Delete the selected category? Contained tasks will be moved
				// to the root.");
				// if (!deleteConfirmed)
				// return;
				TaskCategory cat = (TaskCategory) selectedObject;
				for (ITask task : cat.getChildren()) {
					ContextCorePlugin.getContextManager().deleteContext(task.getHandleIdentifier());
					TasksUiUtil.closeEditorInActivePage(task);
				}
				TasksUiPlugin.getTaskListManager().getTaskList().deleteCategory(cat);
			} else {
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						"Delete failed", "Nothing selected.");
				return;
			}
		}
	}
}
