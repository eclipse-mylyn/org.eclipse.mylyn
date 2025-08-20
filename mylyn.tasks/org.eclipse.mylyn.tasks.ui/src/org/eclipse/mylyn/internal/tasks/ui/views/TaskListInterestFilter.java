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

import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskScheduleContentProvider.Completed;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskScheduleContentProvider.Incoming;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskScheduleContentProvider.Outgoing;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskScheduleContentProvider.Unscheduled;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;

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
			if (child instanceof ScheduledTaskContainer dateRangeTaskContainer) {
				return isDateRangeInteresting(dateRangeTaskContainer);
			}
			if (child instanceof ITask) {
				AbstractTask task = (AbstractTask) child;
				return isInteresting(parent, task);
			}
			if (child instanceof ITaskContainer) {
				Collection<ITask> children = ((ITaskContainer) child).getChildren();
				// Always display empty containers
				if (children.size() == 0) {
					return false;
				}

				for (ITask task : children) {
					if (shouldAlwaysShow(child, (AbstractTask) task, ITasksCoreConstants.MAX_SUBTASK_DEPTH)) {
						return true;
					}
				}
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Interest filter failed", t)); //$NON-NLS-1$
		}
		return false;
	}

	private boolean isDateRangeInteresting(ScheduledTaskContainer scheduleContainer) {
		if (scheduleContainer instanceof TaskScheduleContentProvider.Unscheduled) {
			return false;
		}
		if (scheduleContainer instanceof TaskScheduleContentProvider.Incoming
				|| scheduleContainer instanceof TaskScheduleContentProvider.Outgoing) {
			return hasChildren(scheduleContainer, scheduleContainer.getChildren());
		}
		if (TaskActivityUtil.getCurrentWeek().isCurrentWeekDay(scheduleContainer.getDateRange())) {
			if (scheduleContainer.isPresent() || scheduleContainer.isFuture()) {
				return true;
			}
		} else if (scheduleContainer.isPresent() /*&& scheduleContainer.isCaptureFloating()*/) {
			return true;
		}
		return false;
	}

	private boolean hasChildren(Object parent, Collection<ITask> children) {
		for (ITask task : children) {
			if (PresentationFilter.getInstance().select(parent, task)) {
				return true;
			}
		}
		return false;
	}

	// TODO: make meta-context more explicit
	protected boolean isInteresting(Object parent, AbstractTask task) {
		return shouldAlwaysShow(parent, task, ITasksCoreConstants.MAX_SUBTASK_DEPTH);
	}

	public boolean shouldAlwaysShow(Object parent, AbstractTask task, int depth) {
		if (!PresentationFilter.getInstance().select(parent, task)) {
			return false;
		}
		return task.isActive() || TasksUiPlugin.getTaskActivityManager().isCompletedToday(task)
				|| hasChanges(parent, task) || hasInterestingSubTasks(parent, task, depth)
				// note that following condition is wrapped in ()!
				|| !task.isCompleted() && (LocalRepositoryConnector.DEFAULT_SUMMARY.equals(task.getSummary())
						|| shouldShowInFocusedWorkweekDateContainer(parent, task)
						|| TasksUiPlugin.getTaskActivityManager().isOverdue(task)
						|| isInterestingForThisWeek(parent, task));
	}

	private boolean hasInterestingSubTasks(Object parent, AbstractTask task, int depth) {
		if (depth > 0) {
			if (!TasksUiPlugin.getDefault().groupSubtasks(task)) {
				return false;
			}
			if (task.getChildren() != null && task.getChildren().size() > 0) {
				for (ITask subTask : task.getChildren()) {
					if (shouldAlwaysShow(parent, (AbstractTask) subTask, depth - 1)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean shouldShowInFocusedWorkweekDateContainer(Object parent, ITask task) {
		if (parent instanceof Unscheduled) {
			return false;
		}

		if ((parent instanceof Incoming) || (parent instanceof Outgoing) || (parent instanceof Completed)) {
			return true;
		}

		if (parent instanceof ScheduledTaskContainer) {
			return isDateRangeInteresting((ScheduledTaskContainer) parent);
		}

		return false;
	}

	public boolean isInterestingForThisWeek(Object parent, AbstractTask task) {
		if (parent instanceof ScheduledTaskContainer) {
			return shouldShowInFocusedWorkweekDateContainer(parent, task);
		} else {
			return TasksUiPlugin.getTaskActivityManager().isPastReminder(task)
					|| TasksUiPlugin.getTaskActivityManager().isSheduledForPastWeek(task)
					|| TasksUiPlugin.getTaskActivityManager().isScheduledForThisWeek(task)
					|| TasksUiPlugin.getTaskActivityManager().isDueThisWeek(task)
					|| TasksUiPlugin.getTaskActivityManager().isScheduledForToday(task);
		}
	}

	public boolean hasChanges(Object parent, ITask task) {
		if (parent instanceof ScheduledTaskContainer && !(parent instanceof TaskScheduleContentProvider.Unscheduled)) {
			if (!shouldShowInFocusedWorkweekDateContainer(parent, task)) {
				return false;
			}
		}
		return hasChangesHelper(parent, task);
	}

	private boolean hasChangesHelper(Object parent, ITask task) {
		if (task.getSynchronizationState().isOutgoing()) {
			return true;
		} else if (TasksUiInternal.shouldShowIncoming(task)) {
			return true;
		}
		if (task instanceof ITaskContainer) {
			for (ITask child : ((ITaskContainer) task).getChildren()) {
				if (PresentationFilter.getInstance().select(task, child) && hasChangesHelper(parent, child)) {
					return true;
				}
			}
		}
		return false;
	}
}
