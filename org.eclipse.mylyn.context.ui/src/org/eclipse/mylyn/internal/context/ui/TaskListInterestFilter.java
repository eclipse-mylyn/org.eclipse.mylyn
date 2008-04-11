/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import java.util.Calendar;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;

/**
 * Goal is to have this reuse as much of the super as possible.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 */
public class TaskListInterestFilter extends AbstractTaskListFilter {

	@Override
	public boolean select(Object parent, Object child) {
		try {
			if (child instanceof ScheduledTaskContainer) {
				ScheduledTaskContainer dateRangeTaskContainer = (ScheduledTaskContainer) child;
				return isDateRangeInteresting(dateRangeTaskContainer);
			}
			if (child instanceof AbstractTask) {
				AbstractTask task = null;
				if (child instanceof AbstractTask) {
					task = (AbstractTask) child;
				}
				if (task != null) {
					if (isInteresting(parent, task)) {
						return true;
					} else {
						return false;
					}
				}
			} else if (child instanceof AbstractTaskContainer) {
				Set<AbstractTask> children = ((AbstractTaskContainer) child).getChildren();
				// Always display empty containers
				if (children.size() == 0) {
					return false;
				}

				for (AbstractTask task : children) {
					if (shouldAlwaysShow(child, task, ITasksCoreConstants.MAX_SUBTASK_DEPTH)) {
						return true;
					}
				}

			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, "Interest filter failed", t));
		}
		return false;
	}

	private boolean isDateRangeInteresting(ScheduledTaskContainer scheduleContainer) {
		if (TasksUiPlugin.getTaskActivityManager().isWeekDay(scheduleContainer)
				&& (scheduleContainer.isPresent() || scheduleContainer.isFuture())) {
			return true;
		}
		if (scheduleContainer.isPresent() && scheduleContainer.isCaptureFloating()) {
			return true;
		}
		return false;
		//return TasksUiPlugin.getTaskActivityManager().isWeekDay(container);
	}

	// TODO: make meta-context more explicit
	protected boolean isInteresting(Object parent, AbstractTask task) {
		return shouldAlwaysShow(parent, task, ITasksCoreConstants.MAX_SUBTASK_DEPTH);
	}

	public boolean shouldAlwaysShow(Object parent, AbstractTask task, int depth) {

		return task.isActive()
				|| TasksUiPlugin.getTaskActivityManager().isCompletedToday(task)
				|| hasChanges(parent, task)
				|| !task.isCompleted()
				&& (LocalRepositoryConnector.DEFAULT_SUMMARY.equals(task.getSummary())
						|| shouldShowInFocusedWorkweekDateContainer(parent, task)
						|| TasksUiPlugin.getTaskActivityManager().isOverdue(task) || isInterestingForThisWeek(parent, task) || hasInterestingSubTasks(
						parent, task, depth));
	}

	private boolean hasInterestingSubTasks(Object parent, AbstractTask task, int depth) {
		if (depth > 0) {
			if (!TasksUiPlugin.getDefault().groupSubtasks(task)) {
				return false;
			}
			if (task.getChildren() != null && task.getChildren().size() > 0) {
				for (AbstractTask subTask : task.getChildren()) {
					if (shouldAlwaysShow(parent, subTask, depth - 1)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static boolean shouldShowInFocusedWorkweekDateContainer(Object parent, AbstractTask task) {
		if (parent instanceof ScheduledTaskContainer) {
			if (((ScheduledTaskContainer) parent).isCaptureFloating()) {
				return true;
			}
			if (!TasksUiPlugin.getTaskActivityManager().isWeekDay((ScheduledTaskContainer) parent)) {
				return false;
			}
			if (TasksUiPlugin.getTaskActivityManager().isOverdue(task) || task.isPastReminder()) {
				return true;
			}

			ScheduledTaskContainer container = (ScheduledTaskContainer) parent;
			Calendar previousCal = TasksUiPlugin.getTaskActivityManager().getActivityPrevious().getEnd();
			Calendar nextCal = TasksUiPlugin.getTaskActivityManager().getActivityNextWeek().getStart();
			if (container.getEnd().compareTo(previousCal) > 0 && container.getStart().compareTo(nextCal) < 0) {
				// within workweek
				return true;
			}
		}

		return false;
	}

	public static boolean isInterestingForThisWeek(Object parent, AbstractTask task) {
		if (parent instanceof ScheduledTaskContainer) {
			return shouldShowInFocusedWorkweekDateContainer(parent, task);
		} else {
			return task.isPastReminder() || TasksUiPlugin.getTaskActivityManager().isScheduledForThisWeek(task)
					|| TasksUiPlugin.getTaskActivityManager().isDueThisWeek(task)
					|| TasksUiPlugin.getTaskActivityManager().isScheduledForToday(task);
		}
	}

	public static boolean hasChanges(Object parent, AbstractTask task) {
		if (parent instanceof ScheduledTaskContainer) {
			if (!shouldShowInFocusedWorkweekDateContainer(parent, task)) {
				return false;
			}
		}

		boolean result = false;
		if (task != null) {
			if (task.getSynchronizationState() == RepositoryTaskSyncState.OUTGOING) {
				return true;
			} else if (task.getSynchronizationState() == RepositoryTaskSyncState.INCOMING
					&& !(parent instanceof ScheduledTaskContainer)) {
				return true;
			} else if (task.getSynchronizationState() == RepositoryTaskSyncState.CONFLICT) {
				return true;
			}
			return hasChangesHelper(parent, task);
		}
		return result;
	}

	private static boolean hasChangesHelper(Object parent, AbstractTaskContainer container) {
		boolean result = false;
		for (AbstractTask task : container.getChildren()) {
			if (task != null) {
				if (task.getSynchronizationState() == RepositoryTaskSyncState.OUTGOING) {
					result = true;
				} else if (task.getSynchronizationState() == RepositoryTaskSyncState.INCOMING
						&& !(parent instanceof ScheduledTaskContainer)) {
					result = true;
				} else if (task.getSynchronizationState() == RepositoryTaskSyncState.CONFLICT) {
					result = true;
				} else if (task.getChildren() != null && task.getChildren().size() > 0) {
					result = hasChangesHelper(parent, task);
				}
			}
		}
		return result;
	}
}
