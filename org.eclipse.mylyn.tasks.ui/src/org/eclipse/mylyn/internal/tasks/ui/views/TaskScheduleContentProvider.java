/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskDelegate;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * Used by Scheduled task list presentation
 * 
 * @author Rob Elves
 */
public class TaskScheduleContentProvider extends TaskListContentProvider {

	private final TaskActivityManager taskActivityManager;

	public TaskScheduleContentProvider(TaskListView taskListView) {
		super(taskListView);
		this.taskActivityManager = TasksUiPlugin.getTaskActivityManager();
	}

	@Override
	public Object[] getElements(Object parent) {
		if (parent.equals(this.taskListView.getViewSite())) {
			List<ScheduledTaskContainer> temp = taskActivityManager.getDateRanges();
			Set<AbstractTaskContainer> ranges = new HashSet<AbstractTaskContainer>();
			for (ScheduledTaskContainer scheduledTaskContainer : temp) {
				if (scheduledTaskContainer.isPresent() || scheduledTaskContainer.isFuture()) {
					ranges.add(scheduledTaskContainer);
				}
			}
			//ranges.addAll(taskActivityManager.getDateRanges());
			ranges.add(taskActivityManager.getActivityThisWeek());
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
		Set<AbstractTask> result = new HashSet<AbstractTask>();
		if (parent instanceof AbstractTask) {
			// flat presentation (no subtasks revealed in Scheduled mode)
		} else if (parent instanceof ScheduledTaskContainer) {
			for (AbstractTask child : ((ScheduledTaskContainer) parent).getChildren()) {
				if (!filter(parent, child)) {
					result.add(child);
				}
			}

		} else if (parent instanceof AbstractTaskContainer) {
			for (AbstractTask child : ((AbstractTaskContainer) parent).getChildren()) {
				result.add(child);
			}
		}
		return result.toArray();
	}
}
