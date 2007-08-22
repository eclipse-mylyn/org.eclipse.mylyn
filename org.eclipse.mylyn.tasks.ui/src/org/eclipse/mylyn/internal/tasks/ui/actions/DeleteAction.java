/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchImages;

/**
 * @author Mik Kersten
 */
public class DeleteAction extends Action {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.delete";

	public DeleteAction() {
		setText("Delete");
		setId(ID);
		setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
	}

	@Override
	public void run() {
		ISelection selection = TaskListView.getFromActivePerspective().getViewer().getSelection();

		List<?> toDelete = ((IStructuredSelection) selection).toList();

		String elements = "";
		int i = 0;
		for (Object object : toDelete) {
			i++;
			if (i < 20) {
				if (object instanceof AbstractTaskContainer) {
					elements += "    " + ((AbstractTaskContainer) object).getSummary() + "\n";
				}
			} else {
				elements += "...";
				break;
			}
		}
	
		String message;
		
		if (toDelete.size() == 1) {
			Object object = toDelete.get(0);
			if (object instanceof AbstractTask) {
				if (((AbstractTask)object).isLocal()) {
					message = "Permanently delete the task listed below?";
				} else {
					message = "Delete the planning information and context for the repository task?  The server" +
							" copy will not be deleted and the task will remain in queries that match it.";
				}
			} else if (object instanceof TaskCategory) {
				message = "Permanently delete the category?  Contained tasks will still be available in the Archive";
			} else if (object instanceof AbstractRepositoryQuery) {
				message = "Permanently delete the query?  Matching tasks will still be available in the Archive";
			} else {
				message = "Permanently delete the element listed below?";
			}
		} else {
			message = "Delete the elements listed below?  If categories or queries are selected contained tasks"
			+ " will not be deleted.  Contexts will be deleted for selected tasks.";
		}
		
		message += "\n\n" + elements;
		
		boolean deleteConfirmed = MessageDialog.openQuestion(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getShell(), "Confirm Delete", message);
		if (!deleteConfirmed) {
			return;
		}

		for (Object selectedObject : toDelete) {
			if (selectedObject instanceof AbstractTask) {
				AbstractTask task = null;
				task = (AbstractTask) selectedObject;
				TasksUiPlugin.getTaskListManager().deactivateTask(task);
				TasksUiPlugin.getTaskListManager().getTaskList().deleteTask(task);
				ContextCorePlugin.getContextManager().deleteContext(task.getHandleIdentifier());
				TasksUiUtil.closeEditorInActivePage(task, false);
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
				for (AbstractTask task : cat.getChildren()) {
					ContextCorePlugin.getContextManager().deleteContext(task.getHandleIdentifier());
					TasksUiUtil.closeEditorInActivePage(task, false);
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
