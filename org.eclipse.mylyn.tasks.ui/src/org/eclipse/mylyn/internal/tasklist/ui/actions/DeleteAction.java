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
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.TaskCategory;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.IRepositoryQuery;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchImages;

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
		setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
	}

	@Override
	public void run() {
		ISelection selection = TaskListView.getDefault().getViewer().getSelection();
		for (Object selectedObject : ((IStructuredSelection) selection).toList()) {
			if (selectedObject instanceof ITask || selectedObject instanceof IQueryHit) {
				ITask task = null;
				if (selectedObject instanceof IQueryHit) {
					task = ((IQueryHit) selectedObject).getCorrespondingTask();
				} else {
					task = (ITask) selectedObject;
				}
				if (task == null) {
					MessageDialog.openError(Workbench.getInstance().getActiveWorkbenchWindow().getShell(),
							"Mylar Tasks", "No task data to delte.");
					return;
				}
				if (task.isActive()) {
					MessageDialog.openError(Workbench.getInstance().getActiveWorkbenchWindow().getShell(),
							"Mylar Tasks", "Task must be deactivated in order to delete.");
					return;
				}

				String message = genDeleteConfirmationMessage(task);
				boolean deleteConfirmed = MessageDialog.openQuestion(Workbench.getInstance().getActiveWorkbenchWindow()
						.getShell(), "Confirm Delete", message);
				if (!deleteConfirmed) {
					return;
				}

				MylarTaskListPlugin.getTaskListManager().deleteTask(task);
				MylarPlugin.getContextManager().contextDeleted(task.getHandleIdentifier());
				IWorkbenchPage page = MylarTaskListPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
						.getActivePage();

				if (page == null) {
					return;
				}
				try {
					view.closeTaskEditors(task, page);
				} catch (Exception e) {
					MylarStatusHandler.log(e, "closing editors failed");
				}
				view.getViewer().refresh();
			} else if (selectedObject instanceof IRepositoryQuery) {
				boolean deleteConfirmed = MessageDialog.openQuestion(Workbench.getInstance().getActiveWorkbenchWindow()
						.getShell(), "Confirm delete", "Delete the selected query? Task data will not be deleted.");
				if (deleteConfirmed) {
					MylarTaskListPlugin.getTaskListManager().deleteQuery((IRepositoryQuery) selectedObject);
				}
			} else if (selectedObject instanceof TaskCategory) {
				boolean deleteConfirmed = MessageDialog.openQuestion(Workbench.getInstance().getActiveWorkbenchWindow()
						.getShell(), "Confirm Delete", "Delete the selected category and all contained tasks?");
				if (!deleteConfirmed)
					return;

				TaskCategory cat = (TaskCategory) selectedObject;
				for (ITask task : cat.getChildren()) {
					MylarPlugin.getContextManager().contextDeleted(task.getHandleIdentifier());
					IWorkbenchPage page = MylarTaskListPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
							.getActivePage();
					if (page != null) {
						try {
							this.view.closeTaskEditors(task, page);
						} catch (Exception e) {
							MylarStatusHandler.log(e, " deletion failed");
						}
					}
				}
				MylarTaskListPlugin.getTaskListManager().deleteCategory(cat);
				view.getViewer().refresh();
			} else {
				MessageDialog.openError(Workbench.getInstance().getActiveWorkbenchWindow().getShell(), "Delete failed",
						"Nothing selected.");
				return;
			}
		}
	}

	public static String genDeleteConfirmationMessage(ITask task) {
		return "Delete the selected task and discard task context?\n\n" + task.getDescription();
	}
}
