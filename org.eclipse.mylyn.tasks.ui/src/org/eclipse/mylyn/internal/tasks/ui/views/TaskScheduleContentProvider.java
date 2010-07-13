/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.WeekDateRange;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.ui.PlatformUI;

/**
 * Used by Scheduled task list presentation
 * 
 * @author Rob Elves
 */
public class TaskScheduleContentProvider extends TaskListContentProvider implements ITaskActivityListener {

	private final TaskActivityManager taskActivityManager;

	private final Unscheduled unscheduled;

	private final Calendar END_OF_TIME;

	private final Calendar INCOMING_TIME;

	private final Calendar OUTGOING_TIME;

	private final Calendar COMPLETED_TIME;

	private Job rolloverJob;

	private final Incoming incoming;

	private final Outgoing outgoing;

	private final Completed completed;

	public TaskScheduleContentProvider(TaskListView taskListView) {
		super(taskListView);
		this.taskActivityManager = TasksUiPlugin.getTaskActivityManager();
		taskActivityManager.addActivityListener(this);
		END_OF_TIME = TaskActivityUtil.getCalendar();
		END_OF_TIME.add(Calendar.YEAR, 5000);
		END_OF_TIME.getTime();
		unscheduled = new Unscheduled();

		INCOMING_TIME = TaskActivityUtil.getCalendar();
		INCOMING_TIME.setTimeInMillis(END_OF_TIME.getTimeInMillis() - 1);
		incoming = new Incoming();

		OUTGOING_TIME = TaskActivityUtil.getCalendar();
		//OUTGOING_TIME.setTimeInMillis(TaskActivityUtil.getCurrentWeek().getToday().getStartDate().getTimeInMillis() - 1);
		OUTGOING_TIME.setTimeInMillis(END_OF_TIME.getTimeInMillis() - 2);
		outgoing = new Outgoing();

		COMPLETED_TIME = TaskActivityUtil.getCalendar();
		COMPLETED_TIME.setTimeInMillis(END_OF_TIME.getTimeInMillis() + 2);
		completed = new Completed();

	}

	@Override
	public Object[] getElements(Object parent) {

		if (parent != null && parent.equals(this.taskListView.getViewSite())) {
			Set<AbstractTaskContainer> containers = new HashSet<AbstractTaskContainer>();
			WeekDateRange week = TaskActivityUtil.getCurrentWeek();
			WeekDateRange nextWeek = TaskActivityUtil.getNextWeek();

			synchronized (this) {
				if (rolloverJob != null) {
					rolloverJob.cancel();
					rolloverJob = null;
				}

				long delay = week.getToday().getEndDate().getTime().getTime() - new Date().getTime();
				rolloverJob = new RolloverCheck();
				rolloverJob.setSystem(true);
				rolloverJob.setPriority(Job.SHORT);
				rolloverJob.schedule(delay);
			}

			for (DateRange day : week.getRemainingDays()) {
				containers.add(new ScheduledTaskContainer(TasksUiPlugin.getTaskActivityManager(), day));
			}

			// This Week
			containers.add(new ScheduledTaskContainer(TasksUiPlugin.getTaskActivityManager(), week));

			for (DateRange day : nextWeek.getDaysOfWeek()) {
				containers.add(new ScheduledTaskContainer(TasksUiPlugin.getTaskActivityManager(), day));
			}

			ScheduledTaskContainer nextWeekContainer = new ScheduledTaskContainer(taskActivityManager, nextWeek);
			containers.add(nextWeekContainer);

			ScheduledTaskContainer twoWeeksContainer = new ScheduledTaskContainer(taskActivityManager, week.next()
					.next(), Messages.TaskScheduleContentProvider_Two_Weeks);
			containers.add(twoWeeksContainer);

			Calendar startDate = TaskActivityUtil.getCalendar();
			startDate.setTimeInMillis(twoWeeksContainer.getEnd().getTimeInMillis());
			TaskActivityUtil.snapNextDay(startDate);
			Calendar endDate = TaskActivityUtil.getCalendar();
			endDate.add(Calendar.YEAR, 4999);
			DateRange future = new DateRange(startDate, endDate);

			ScheduledTaskContainer futureContainer = new ScheduledTaskContainer(taskActivityManager, future,
					Messages.TaskScheduleContentProvider_Future);
			containers.add(futureContainer);

			// Outgoing
			containers.add(outgoing);

			// Incoming
			containers.add(incoming);

			// Unscheduled
			containers.add(unscheduled);

			// Completed
			containers.add(completed);

			return applyFilter(containers).toArray();

		} else {
			return getChildren(parent);
		}
	}

	@Override
	public Object getParent(Object child) {
//		for (Object o : getElements(null)) {
//			ScheduledTaskContainer container = ((ScheduledTaskContainer) o);
//			if (container.getChildren().contains(((ITask) child).getHandleIdentifier())) {
//				return container;
//			}
//		}
		return null;
	}

	@Override
	public boolean hasChildren(Object parent) {
		return getChildren(parent).length > 0;
	}

	@Override
	public Object[] getChildren(Object parent) {
		Set<ITask> result = new HashSet<ITask>();
		if (parent instanceof ITask) {
			// flat presentation (no subtasks revealed in Scheduled mode)
		} else if (parent instanceof ScheduledTaskContainer) {
			for (ITask child : ((ScheduledTaskContainer) parent).getChildren()) {
				if (!filter(parent, child)) {
					result.add(child);
				}
			}

		} else if (parent instanceof ITaskContainer) {
			for (ITask child : ((ITaskContainer) parent).getChildren()) {
				result.add(child);
			}
		}
		return result.toArray();
	}

	private void refresh() {
		if (Platform.isRunning() && PlatformUI.getWorkbench() != null && !PlatformUI.getWorkbench().isClosing()) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					taskListView.refresh();
				}
			});
		}
	}

	@Override
	public void dispose() {
		synchronized (this) {
			if (rolloverJob != null) {
				rolloverJob.cancel();
			}
		}
		taskActivityManager.removeActivityListener(this);
		super.dispose();
	}

	public void activityReset() {
		refresh();
	}

	public void elapsedTimeUpdated(ITask task, long newElapsedTime) {
		// ignore
	}

	public class Unscheduled extends StateTaskContainer {

		public Unscheduled() {
			super(new DateRange(END_OF_TIME), Messages.TaskScheduleContentProvider_Unscheduled);
		}

		@Override
		protected boolean select(ITask task) {
			return task.getSynchronizationState() == SynchronizationState.SYNCHRONIZED && !task.isCompleted();
		}

	}

	public abstract class StateTaskContainer extends ScheduledTaskContainer {

		Calendar temp = TaskActivityUtil.getCalendar();

		public StateTaskContainer(DateRange range, String summary) {
			super(taskActivityManager, range, summary);
		}

		@Override
		public Collection<ITask> getChildren() {
			Set<ITask> children = new HashSet<ITask>();
			for (ITask task : TasksUiPlugin.getTaskList().getAllTasks()) {
				if (select(task) && include(task)) {
					children.add(task);
				}
			}
			return children;
		}

		private boolean include(ITask task) {
			// ensure that completed tasks always show somewhere
			if (task.isCompleted()) {
				return true;
			}

			DateRange scheduledForDate = ((AbstractTask) task).getScheduledForDate();

			// in focused mode fewer container are displayed, include additional tasks
			if (taskListView.isFocusedMode()) {
				if (scheduledForDate != null) {
					if (TaskActivityUtil.isAfterCurrentWeek(scheduledForDate.getStartDate())) {
						// scheduled for next week or later
						return true;
					} else {
						// scheduled for this week or earlier
						return false;
					}
				} else if (task.getDueDate() != null && taskActivityManager.isOwnedByUser(task)) {
					temp.setTime(task.getDueDate());
					if (TaskActivityUtil.isAfterCurrentWeek(temp)) {
						// not scheduled, due next week or later
						return true;
					} else {
						// due this week or earlier
						return false;
					}
				}
			}

			if (scheduledForDate == null) {
				if (task.getDueDate() != null && !taskActivityManager.isOwnedByUser(task)) {
					// not scheduled, due but not owned by user
					return true;
				} else if (task.getDueDate() == null) {
					// not scheduled, not due
					return true;
				}
			}

			return false;
		}

		protected abstract boolean select(ITask task);

	}

	public class Incoming extends StateTaskContainer {

		public Incoming() {
			super(new DateRange(INCOMING_TIME), Messages.TaskScheduleContentProvider_Incoming);
		}

		@Override
		protected boolean select(ITask task) {
			SynchronizationState state = task.getSynchronizationState();
			return state == SynchronizationState.INCOMING || state == SynchronizationState.INCOMING_NEW;
		}

	}

	public class Outgoing extends StateTaskContainer {

		public Outgoing() {
			super(new DateRange(OUTGOING_TIME), Messages.TaskScheduleContentProvider_Outgoing);
		}

		@Override
		public boolean select(ITask task) {
			SynchronizationState state = task.getSynchronizationState();
			return state == SynchronizationState.OUTGOING || state == SynchronizationState.OUTGOING_NEW
					|| state == SynchronizationState.CONFLICT;
		}
	}

	public class Completed extends StateTaskContainer {

		public Completed() {
			super(new DateRange(COMPLETED_TIME), Messages.TaskScheduleContentProvider_Completed);
		}

		@Override
		public boolean select(ITask task) {
			return (task.isCompleted() && task.getSynchronizationState().equals(SynchronizationState.SYNCHRONIZED));
		}
	}

	private class RolloverCheck extends Job {

		public RolloverCheck() {
			super("Calendar Rollover Job"); //$NON-NLS-1$
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			refresh();
			return Status.OK_STATUS;
		}
	}

}
