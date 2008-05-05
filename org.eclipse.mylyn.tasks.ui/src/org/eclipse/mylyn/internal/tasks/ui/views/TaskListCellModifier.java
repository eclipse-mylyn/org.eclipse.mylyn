/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.Arrays;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskDeactivateAction;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Mik Kersten
 */
class TaskListCellModifier implements ICellModifier {

	private final TaskListView taskListView;

	private boolean disableActivateForParentTasks = false;

	TaskListCellModifier(TaskListView taskListView) {
		this.taskListView = taskListView;

		if (SWT.getPlatform().equals("gtk")) {
			disableActivateForParentTasks = true;
		}
	}

	public boolean canModify(Object element, String property) {
		return taskListView.isInRenameAction;
	}

	public Object getValue(Object element, String property) {
		try {
			int columnIndex = Arrays.asList(this.taskListView.columnNames).indexOf(property);
			if (element instanceof ITaskElement) {
				final ITaskElement taskListElement = (ITaskElement) element;
				switch (columnIndex) {
				case 0:
					return taskListElement.getSummary();
				case 1:
					return "";
				case 2:
					return "";
				}
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, e.getMessage(), e));
		}
		return "";
	}

	public void modify(Object element, String property, Object value) {
		int columnIndex = -1;
		try {
			if (element instanceof TreeItem && ((TreeItem) element).isDisposed()) {
				return;
			}
			columnIndex = Arrays.asList(this.taskListView.columnNames).indexOf(property);
			if (((TreeItem) element).getData() instanceof ITask) {
				final ITaskElement taskListElement = (ITaskElement) ((TreeItem) element).getData();
				AbstractTask task = (AbstractTask) taskListElement;
				switch (columnIndex) {
				case 0:
					if (task != null) {
						task.setSummary(((String) value).trim());
						TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(task, false);
					}
					break;
				case 1:
					break;
				case 2:
					toggleTaskActivation((TreeItem) element);
					break;
				}
			} else if (((TreeItem) element).getData() instanceof AbstractTaskCategory
					|| ((TreeItem) element).getData() instanceof AbstractRepositoryQuery) {
				AbstractTaskContainer container = (AbstractTaskContainer) ((TreeItem) element).getData();
				switch (columnIndex) {
				case 0:
					TasksUiPlugin.getTaskListManager()
							.getTaskList()
							.renameContainer(container, ((String) value).trim());
				case 1:
					break;
				case 2:
					break;
				}
			}
		} catch (Exception e) {
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, e.getMessage(), e));
		}
		this.taskListView.refresh();//.getViewer().refresh();
	}

	public void toggleTaskActivation(TreeItem element) {
		ITaskElement taskListElement = (ITaskElement) element.getData();

		AbstractTask task = null;
		if (taskListElement instanceof ITask) {
			task = (AbstractTask) taskListElement;
		}

		if (task != null) {
			if (disableActivateForParentTasks) {
				// check if activation column overlaps with tree expander control: element is on second hierarchy level and has children  
				TreeItem parent = element.getParentItem();
				if (parent != null
						&& (parent.getData() instanceof AbstractRepositoryQuery || parent.getData() instanceof AbstractTaskCategory)
						&& element.getItemCount() > 0) {
					return;
				}
			}

			if (task.isActive()) {
				new TaskDeactivateAction().run(task);
//				this.taskListView.previousTaskAction.setButtonStatus();
			} else {
				new TaskActivateAction().run(task);
//				this.taskListView.addTaskToHistory(task);
//				this.taskListView.previousTaskAction.setButtonStatus();
			}
		}
	}
}