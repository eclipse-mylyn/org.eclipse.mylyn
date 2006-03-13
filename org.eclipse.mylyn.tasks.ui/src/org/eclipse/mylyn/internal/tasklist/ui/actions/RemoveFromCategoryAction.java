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
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskCategory;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ken Sueda
 */
public class RemoveFromCategoryAction extends Action {
	public static final String ID = "org.eclipse.mylar.tasklist.actions.remove";

	private final TaskListView view;

	public RemoveFromCategoryAction(TaskListView view) {
		this.view = view;
		setText("Remove From Category");
		setId(ID);
		setImageDescriptor(TaskListImages.REMOVE);
	}

	@Override
	public void run() {
		try {
			ISelection selection = TaskListView.getDefault().getViewer().getSelection();
			for (Object selectedObject : ((IStructuredSelection) selection).toList()) {
				if (selectedObject instanceof ITask) { // && !((ITask) selectedObject).isLocal()) {
					ITask task = (ITask) selectedObject;
					if (task.isActive()) {
						MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
								MylarTaskListPlugin.TITLE_DIALOG,
								"Task must be deactivated in order to remove from category.");
						return;
					}

					TreeItem item = this.view.getViewer().getTree().getSelection()[0];
					if (item.getParentItem() != null && item.getParentItem().getData() instanceof TaskCategory) {
						TaskCategory category = (TaskCategory) item.getParentItem().getData();
						MylarTaskListPlugin.getTaskListManager().getTaskList().removeFromCategory(category, task);
					} else {
						MylarTaskListPlugin.getTaskListManager().getTaskList().removeFromRoot(task);
					} 
					// just in case, should already be there
//					MylarTaskListPlugin.getTaskListManager().getTaskList().addTaskToArchive(task);
//					ITaskContainer cat = task.getCategory();
//					if (cat != null) {
//						String message = DeleteAction.genDeleteConfirmationMessage(task);
//						boolean deleteConfirmed = MessageDialog.openQuestion(PlatformUI.getWorkbench()
//								.getActiveWorkbenchWindow().getShell(), "Confirm delete", message);
//						if (!deleteConfirmed)
//							return;
//
//						MylarTaskListPlugin.getTaskListManager().deleteTask(task);
//						MylarPlugin.getContextManager().contextDeleted(task.getHandleIdentifier()); // task.getContextPath());
//					}
				}
			}
		} catch (NullPointerException npe) {
			MylarStatusHandler.fail(npe, "Could not remove task from category, it may still be refreshing.", true);
		}
	}
}
