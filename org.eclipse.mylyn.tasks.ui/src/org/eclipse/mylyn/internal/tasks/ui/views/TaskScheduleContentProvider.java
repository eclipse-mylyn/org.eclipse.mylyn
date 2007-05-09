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

package org.eclipse.mylar.internal.tasks.ui.views;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.DateRangeActivityDelegate;
import org.eclipse.mylar.tasks.core.DateRangeContainer;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.ui.TaskListManager;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * Used by Scheduled task list presentation
 * 
 * @author Rob Elves
 */
public class TaskScheduleContentProvider extends TaskListContentProvider {

	private TaskListManager taskListManager;

	public TaskScheduleContentProvider(TaskListView view, TaskListManager taskActivityManager) {
		super(view);
		this.taskListManager = taskActivityManager;
	}

	public Object[] getElements(Object parent) {
		if (parent.equals(this.view.getViewSite())) {
			Set<ITaskListElement> ranges = new HashSet<ITaskListElement>();
			ranges.addAll(taskListManager.getDateRanges());

			ranges.add(TasksUiPlugin.getTaskListManager().getTaskList().getArchiveContainer());
			ITask activeTask = TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask();
			boolean containsActiveTask = false;
			if (activeTask != null) {
				for (ITaskListElement taskListElement : ranges) {
					if (taskListElement instanceof AbstractTaskContainer) {
						if (((AbstractTaskContainer) taskListElement).getChildren().contains(activeTask)) {
							containsActiveTask = true;
						}
					}
				}
				if (!containsActiveTask) {
					ranges.add(activeTask);
				}
			}

			return applyFilter(ranges).toArray();
		} else {
			return super.getElements(parent);
		}
	}

	public Object getParent(Object child) {
		if (child instanceof DateRangeActivityDelegate) {
			DateRangeActivityDelegate dateRangeTaskWrapper = (DateRangeActivityDelegate) child;
			return dateRangeTaskWrapper.getParent();
		} else {
			return null;
		}
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof DateRangeContainer) {
			DateRangeContainer dateRangeTaskCategory = (DateRangeContainer) parent;
			return dateRangeTaskCategory.getChildren() != null && dateRangeTaskCategory.getChildren().size() > 0;
		} else {
			return super.hasChildren(parent);
		}
	}
}
