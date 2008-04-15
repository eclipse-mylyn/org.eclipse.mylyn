/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ken Sueda
 * @author Mik Kersten
 */
public class RemoveFromCategoryAction extends Action {
	private static final String LABEL = "Remove From Category";

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.remove";

	private final TaskListView view;

	public RemoveFromCategoryAction(TaskListView view) {
		this.view = view;
		setText(LABEL);
		setId(ID);
		setImageDescriptor(TasksUiImages.REMOVE);
	}

	@Override
	public void run() {
		try {
			ISelection selection = TaskListView.getFromActivePerspective().getViewer().getSelection();
			for (Object selectedObject : ((IStructuredSelection) selection).toList()) {
				if (selectedObject instanceof AbstractTask) { // && !((ITask) selectedObject).isLocal()) {
					AbstractTask task = (AbstractTask) selectedObject;
					if (task.isActive()) {
						MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
								ITasksUiConstants.TITLE_DIALOG,
								"Task must be deactivated in order to remove from category.");
						return;
					}

					TreeItem item = this.view.getViewer().getTree().getSelection()[0];
					Set<AbstractTaskContainer> parentContainers = task.getParentContainers();

					if (item.getParentItem() != null && item.getParent().getData() instanceof TaskCategory) {
						TaskCategory category = (TaskCategory) item.getParentItem().getData();
						TasksUi.getTaskListManager().getTaskList().removeFromContainer(category, task);
					}
//						TasksUiPlugin.getTaskListManager().getTaskList().moveToContainer(task,
//								TasksUiPlugin.getTaskListManager().getTaskList().getDefaultCategory());
					else if (!parentContainers.isEmpty() && TaskCategory.getParentTaskCategory(task) != null) {
						TasksUi.getTaskListManager().getTaskList().removeFromContainer(
								TaskCategory.getParentTaskCategory(task), task);
						//TasksUiPlugin.getTaskListManager().getTaskList().moveToContainer(task, null);
					} else if (!task.isLocal()) {
						TasksUi.getTaskListManager().getTaskList().addTask(task, null);
					} else {
						TasksUi.getTaskListManager().getTaskList().addTask(task,
								TasksUiPlugin.getTaskListManager().getTaskList().getDefaultCategory());
					}

				}
			}
		} catch (NullPointerException e) {
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Could not remove task from category, it may still be refreshing.", e));
		}
	}
}
