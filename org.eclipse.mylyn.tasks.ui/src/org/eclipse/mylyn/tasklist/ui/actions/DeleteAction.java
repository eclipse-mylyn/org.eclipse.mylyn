/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskListElement;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.TaskListImages;
import org.eclipse.mylar.tasklist.internal.TaskCategory;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class DeleteAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.delete";

	private final TaskListView view;

	public DeleteAction(TaskListView view) {
		this.view = view;
		setText("Delete");
		setId(ID);
		setImageDescriptor(TaskListImages.REMOVE);
	}

	@Override
	public void run() {

		//		MylarPlugin.getDefault().actionObserved(this);
		Object selectedObject = ((IStructuredSelection) this.view.getViewer().getSelection()).getFirstElement();
		if (selectedObject instanceof ITaskListElement && MylarTasklistPlugin.getDefault().getTaskHandlerForElement((ITaskListElement) selectedObject) != null) {
			MylarTasklistPlugin.getDefault().getTaskHandlerForElement((ITaskListElement) selectedObject).itemDeleted((ITaskListElement) selectedObject);
		} else if (selectedObject instanceof ITask) {
			ITask task = (ITask) selectedObject;
			if (task.isActive()) {
				MessageDialog.openError(Workbench.getInstance().getActiveWorkbenchWindow().getShell(), "Delete failed",
						"Task must be deactivated in order to delete.");
				return;
			}

			String message = task.getDeleteConfirmationMessage();
			boolean deleteConfirmed = MessageDialog.openQuestion(Workbench.getInstance().getActiveWorkbenchWindow().getShell(), "Confirm delete", message);
			if (!deleteConfirmed)
				return;

			MylarTasklistPlugin.getTaskListManager().deleteTask(task);
			MylarPlugin.getContextManager().contextDeleted(task.getHandleIdentifier(), task.getPath());
			IWorkbenchPage page = MylarTasklistPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();

			if (page == null) {
				this.view.getViewer().refresh();
				return;
			}
			try {
				this.view.closeTaskEditors((ITask) selectedObject, page);
			} catch (Exception e) {
				MylarPlugin.log(e, " deletion failed");
			}
		} else if (selectedObject instanceof TaskCategory) {
			boolean deleteConfirmed = MessageDialog.openQuestion(Workbench.getInstance().getActiveWorkbenchWindow().getShell(), "Confirm delete",
					"Delete the selected category and all contained tasks?");
			if (!deleteConfirmed)
				return;

			TaskCategory cat = (TaskCategory) selectedObject;
			for (ITask task : cat.getChildren()) {
				MylarPlugin.getContextManager().contextDeleted(task.getHandleIdentifier(), task.getPath());
				IWorkbenchPage page = MylarTasklistPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
				if (page != null) {
					try {
						this.view.closeTaskEditors(task, page);
					} catch (Exception e) {
						MylarPlugin.log(e, " deletion failed");
					}
				}
			}
			MylarTasklistPlugin.getTaskListManager().deleteCategory(cat);
		} else {
			MessageDialog.openError(Workbench.getInstance().getActiveWorkbenchWindow().getShell(), "Delete failed", "Nothing selected.");
			return;
		}
		this.view.getViewer().refresh();
	}
}