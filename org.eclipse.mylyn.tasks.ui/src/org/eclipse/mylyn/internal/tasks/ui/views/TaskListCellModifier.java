/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.Arrays;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Mik Kersten
 */
class TaskListCellModifier implements ICellModifier {

	private final TaskListView taskListView;

	private boolean disableActivateForParentTasks = false;

	TaskListCellModifier(TaskListView taskListView) {
		this.taskListView = taskListView;

		if (SWT.getPlatform().equals("gtk")) { //$NON-NLS-1$
			disableActivateForParentTasks = true;
		}
	}

	public boolean canModify(Object element, String property) {
		return taskListView.isInRenameAction;
	}

	public Object getValue(Object element, String property) {
		try {
			int columnIndex = Arrays.asList(this.taskListView.columnNames).indexOf(property);
			if (element instanceof IRepositoryElement) {
				final IRepositoryElement taskListElement = (IRepositoryElement) element;
				switch (columnIndex) {
				case 0:
					return taskListElement.getSummary();
				case 1:
					return ""; //$NON-NLS-1$
				case 2:
					return ""; //$NON-NLS-1$
				}
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, e.getMessage(), e));
		}
		return ""; //$NON-NLS-1$
	}

	public void modify(Object element, String property, Object value) {
		if (element instanceof TreeItem && ((TreeItem) element).isDisposed()) {
			return;
		}

		int columnIndex = Arrays.asList(this.taskListView.columnNames).indexOf(property);
		Object data = ((TreeItem) element).getData();
		if (data instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) data;
			switch (columnIndex) {
			case 0:
				if (task != null) {
					task.setSummary(((String) value).trim());
					TasksUiPlugin.getTaskList().notifyElementChanged(task);
				}
				break;
			case 1:
				break;
			case 2:
				toggleTaskActivation((TreeItem) element, null);
				break;
			}
		} else if (data instanceof AbstractTaskCategory || data instanceof IRepositoryQuery) {
			AbstractTaskContainer container = (AbstractTaskContainer) data;
			switch (columnIndex) {
			case 0:
				TasksUiPlugin.getTaskList().renameContainer(container, ((String) value).trim());
			case 1:
				break;
			case 2:
				break;
			}
		}

		// XXX trigger a full refresh to ensure correct sorting
		this.taskListView.refresh();
	}

	public void toggleTaskActivation(TreeItem element, MouseEvent event) {
		AbstractTask task = null;
		if (element.getData() instanceof AbstractTask) {
			task = (AbstractTask) element.getData();

			if (disableActivateForParentTasks) {
				// check if activation column overlaps with tree expander control: element is on second hierarchy level and has children  
				TreeItem parent = element.getParentItem();
				if (parent != null
						&& (parent.getData() instanceof IRepositoryQuery || parent.getData() instanceof AbstractTaskCategory)
						&& element.getItemCount() > 0) {
					return;
				}
			}

			if (task.isActive()) {
				TasksUi.getTaskActivityManager().deactivateTask(task);
			} else {
				TasksUiInternal.activateTaskThroughCommand(task);
				if ((event.stateMask & SWT.SHIFT) != 0 || event.button == 2) {
					TasksUiUtil.openTask(task);
				}
			}
		}
	}
}