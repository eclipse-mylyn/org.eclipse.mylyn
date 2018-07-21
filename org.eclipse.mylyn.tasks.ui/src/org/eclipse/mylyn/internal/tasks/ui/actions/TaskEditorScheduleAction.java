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

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.ui.ScheduleTaskMenuContributor;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskListChangeAdapter;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;

/**
 * <p>
 * <b>Note:</b> this action must be disposed.
 * 
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class TaskEditorScheduleAction extends Action implements IMenuCreator {

	private final ITask task;

	private MenuManager menuManager;

	private final ScheduleTaskMenuContributor scheduleMenuContributor = new ScheduleTaskMenuContributor();

	private final ITaskListChangeListener TASK_LIST_LISTENER = new TaskListChangeAdapter() {

		@Override
		public void containersChanged(Set<TaskContainerDelta> containers) {
			for (TaskContainerDelta taskContainerDelta : containers) {
				if (taskContainerDelta.getElement() instanceof ITask) {
					final AbstractTask updateTask = (AbstractTask) taskContainerDelta.getElement();
					if (task.equals(updateTask)) {
						if (PlatformUI.getWorkbench() != null && !PlatformUI.getWorkbench().isClosing()) {
							PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
								public void run() {
									updateImageDescriptor();
								}
							});
						}
					}
				}
			}
		}

	};

	public TaskEditorScheduleAction(ITask task) {
		Assert.isNotNull(task);
		this.task = task;
		updateImageDescriptor();
		setMenuCreator(this);
		setToolTipText(Messages.TaskEditorScheduleAction_Private_Scheduling);
		TasksUiPlugin.getTaskList().addChangeListener(TASK_LIST_LISTENER);
	}

	@Override
	public void run() {
		if (((AbstractTask) task).getScheduledForDate() == null) {
			TasksUiPlugin.getTaskList().addTaskIfAbsent(task);
			TasksUiPlugin.getTaskActivityManager().setScheduledFor((AbstractTask) task,
					TaskActivityUtil.getCurrentWeek().getToday());
		} else {
			TasksUiPlugin.getTaskActivityManager().setScheduledFor((AbstractTask) task, null);
		}
	}

	public void updateImageDescriptor() {
		if (task instanceof AbstractTask && ((AbstractTask) task).getScheduledForDate() != null) {
			setImageDescriptor(CommonImages.SCHEDULE_DAY);
		} else {
			setImageDescriptor(CommonImages.SCHEDULE);
		}
		setEnabled(!task.isCompleted());
	}

	public Menu getMenu(Control parent) {
		if (menuManager != null) {
			menuManager.dispose();
		}
		menuManager = scheduleMenuContributor.getSubMenuManager(Collections.singletonList((IRepositoryElement) task));
		menuManager.createContextMenu(parent);
		return menuManager.getMenu();
	}

	public Menu getMenu(Menu parent) {
		if (menuManager != null) {
			return menuManager.getMenu();
		}
		return null;
	}

	public void dispose() {
		if (menuManager != null) {
			menuManager.dispose();
		}
		TasksUiPlugin.getTaskList().removeChangeListener(TASK_LIST_LISTENER);
	}

}