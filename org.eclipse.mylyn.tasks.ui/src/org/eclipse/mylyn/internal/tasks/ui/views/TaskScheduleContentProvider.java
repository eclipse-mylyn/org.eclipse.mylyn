/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskDelegate;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.ui.TaskListManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * Used by Scheduled task list presentation
 * 
 * @author Rob Elves
 */
public class TaskScheduleContentProvider extends TaskListContentProvider {

	private TaskListManager taskListManager;

	public TaskScheduleContentProvider(TaskListView taskListView) {
		super(taskListView);
		this.taskListManager = TasksUiPlugin.getTaskListManager();
	}

	@Override
	public Object[] getElements(Object parent) {
		if (parent.equals(this.taskListView.getViewSite())) {
			//unscheduledCategory.activeTask = null;
			Set<AbstractTaskContainer> ranges = new HashSet<AbstractTaskContainer>();

			ranges.addAll(taskListManager.getDateRanges());
			ranges.add(TasksUiPlugin.getTaskListManager().getTaskList().getArchiveContainer());
//			AbstractTask activeTask = TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask();
//			boolean containsActiveTask = false;
//			if (activeTask != null) {
//				for (AbstractTaskContainer taskListElement : ranges) {
//					if (taskListElement != null) {
//						if (taskListElement.getChildren().contains(activeTask)) {
//							containsActiveTask = true;
//						}
//					}
//				}
//				if (!containsActiveTask) {
//					unscheduledCategory.activeTask = activeTask;
//					ranges.add(unscheduledCategory);
//				}
//			}
			return applyFilter(ranges).toArray();
		} else {
			return super.getElements(parent);
		}
	}

	@Override
	public Object getParent(Object child) {
		if (child instanceof ScheduledTaskDelegate) {
			return ((ScheduledTaskDelegate) child).getDateRangeContainer();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object parent) {
		return getChildren(parent).length > 0;
	}

	@Override
	public Object[] getChildren(Object parent) {
		if (parent instanceof ScheduledTaskContainer) {
			return ((ScheduledTaskContainer) parent).getChildren().toArray();

		} else if (parent instanceof AbstractTaskContainer) {
			return ((AbstractTaskContainer) parent).getChildren().toArray();
		}
//		else if (parent.equals(unscheduledCategory)) {
//			return unscheduledCategory.getChildren().toArray();
//		}
		return new Object[0];
	}
}
