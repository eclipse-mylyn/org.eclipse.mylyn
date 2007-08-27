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
import org.eclipse.mylyn.tasks.core.AbstractTask;
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

//	private UnscheduledCategory unscheduledCategory = new UnscheduledCategory();
//
//	private static final class UnscheduledCategory extends AbstractTaskCategory {
//
//		public static final String LABEL = "<Unscheduled>";
//
//		public static final String HANDLE = "unscheduled";
//
//		private AbstractTask activeTask = null;
//
//		public UnscheduledCategory() {
//			super(HANDLE);
//		}
//
//		public void setActiveTask(AbstractTask activeTask) {
//			this.activeTask = activeTask;
//		}
//
//		@Override
//		public Set<AbstractTask> getChildren() {
//			Set<AbstractTask> customChildren = new HashSet<AbstractTask>();
//			if (activeTask != null && activeTask.isActive()) {
//				customChildren.add(activeTask);
//			}
//			return customChildren;
//		}
//
//		@Override
//		public String getPriority() {
//			return PriorityLevel.P1.toString();
//		}
//
//		@Override
//		public String getHandleIdentifier() {
//			return HANDLE;
//		}
//
//		@Override
//		public String getSummary() {
//			return LABEL;
//		}
//
//		@Override
//		public boolean isUserDefined() {
//			return false;
//		}
//	}

	public TaskScheduleContentProvider() {
		this.taskListManager = TasksUiPlugin.getTaskListManager();
	}

	@Override
	public Object[] getElements(Object parent) {
		if (parent.equals(this.view.getViewSite())) {
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
	public void setView(TaskListView view) {
		super.setView(view);
	}

	@Override
	public Object[] getChildren(Object parent) {
		Set<AbstractTask> children = new HashSet<AbstractTask>();
		if (parent instanceof ScheduledTaskContainer) {
			ScheduledTaskContainer container = (ScheduledTaskContainer) parent;
			if (container.isPresent()) {
				// add all overdue
				for (AbstractTask task : taskListManager.getDueTasks(taskListManager.getActivityPast().getStart(),
						container.getStart())) {
					if (taskListManager.isOverdue(task)) {
						children.add(task);
					}
				}

				// add all over scheduled
				for (AbstractTask task : taskListManager.getScheduledTasks(
						taskListManager.getActivityPast().getStart(), container.getStart())) {
					if (task.isPastReminder() && !task.isCompleted()) {
						children.add(task);
					}
				}
				children.addAll(taskListManager.getActiveTasks(container.getStart(), container.getEnd()));
				children.addAll(taskListManager.getScheduledTasks(container.getStart(), container.getEnd()));
				children.addAll(taskListManager.getDueTasks(container.getStart(), container.getEnd()));

				// if not scheduled or due in future, and is active, place in today bin
				AbstractTask activeTask = TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask();
				if (activeTask != null && !children.contains(activeTask)) {
					Set<AbstractTask> futureScheduled = taskListManager.getDueTasks(container.getStart(),
							taskListManager.getActivityFuture().getEnd());
					futureScheduled.addAll(taskListManager.getScheduledTasks(container.getStart(),
							taskListManager.getActivityFuture().getEnd()));
					if (!futureScheduled.contains(activeTask)) {
						children.add(activeTask);
					}
				}

				Set<ScheduledTaskDelegate> delegates = new HashSet<ScheduledTaskDelegate>();
				for (AbstractTask abstractTask : children) {
					delegates.add(new ScheduledTaskDelegate(container, abstractTask, container.getStart(),
							container.getEnd(), 0));
				}
				return delegates.toArray();
			} else if (container.isFuture()) {
				children.addAll(taskListManager.getScheduledTasks(container.getStart(), container.getEnd()));
				children.addAll(taskListManager.getDueTasks(container.getStart(), container.getEnd()));
				Set<ScheduledTaskDelegate> delegates = new HashSet<ScheduledTaskDelegate>();
				for (AbstractTask abstractTask : children) {
					delegates.add(new ScheduledTaskDelegate(container, abstractTask, container.getStart(),
							container.getEnd(), 0));
				}
				return delegates.toArray();
			} else {
				children.addAll(taskListManager.getActiveTasks(container.getStart(), container.getEnd()));
				Set<ScheduledTaskDelegate> delegates = new HashSet<ScheduledTaskDelegate>();
				for (AbstractTask abstractTask : children) {
					delegates.add(new ScheduledTaskDelegate(container, abstractTask, container.getStart(),
							container.getEnd(), 0));
				}
				return delegates.toArray();

			}
		}
//		else if (parent.equals(unscheduledCategory)) {
//			return unscheduledCategory.getChildren().toArray();
//		}
		return new Object[0];
	}
}
