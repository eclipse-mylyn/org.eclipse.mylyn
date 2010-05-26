/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.ITaskFilter;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.WeekDateRange;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
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

	private final List<AbstractTaskContainer> containers = new ArrayList<AbstractTaskContainer>();

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
		unscheduled = new Unscheduled(taskActivityManager, new DateRange(END_OF_TIME));

		INCOMING_TIME = TaskActivityUtil.getCalendar();
		INCOMING_TIME.setTimeInMillis(END_OF_TIME.getTimeInMillis() - 1);
		incoming = new Incoming();

		OUTGOING_TIME = TaskActivityUtil.getCalendar();
		OUTGOING_TIME.setTimeInMillis(END_OF_TIME.getTimeInMillis() - 2);
		outgoing = new Outgoing();

		COMPLETED_TIME = TaskActivityUtil.getCalendar();
		COMPLETED_TIME.setTimeInMillis(END_OF_TIME.getTimeInMillis() + 2);
		completed = new Completed();

	}

	@Override
	public Object[] getElements(Object parent) {

		containers.clear();

		if (parent != null && parent.equals(this.taskListView.getViewSite())) {

			//Set<AbstractTaskContainer> containers = new HashSet<AbstractTaskContainer>();

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

			Collection<AbstractTask> tasks = TasksUiPlugin.getTaskList().getAllTasks();
			Set<AbstractTask> allocated = new HashSet<AbstractTask>();

			for (AbstractTaskContainer container : containers) {
				container.clear();
				for (AbstractTask abstractTask : tasks) {
					if (!allocated.contains(abstractTask)) {
						if (container instanceof ITaskFilter) {
							if (((ITaskFilter) container).select(abstractTask)) {
								allocated.add(abstractTask);
								container.internalAddChild(abstractTask);
							}
						}
					}
				}
			}

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

	public class Unscheduled extends ScheduledTaskContainer {

		private final TaskActivityManager activityManager;

		boolean cached = false;

		public Unscheduled(TaskActivityManager activityManager, DateRange range) {
			super(activityManager, range, Messages.TaskScheduleContentProvider_Unscheduled);
			this.activityManager = activityManager;
		}

		@Override
		public boolean select(ITask task) {
			if (activityManager.getUnscheduled().contains(task)) {
				return !task.isCompleted();
			}
			return false;
		}

	}

	public class Incoming extends ScheduledTaskContainer {

		public Incoming() {
			super(taskActivityManager, new DateRange(INCOMING_TIME), Messages.TaskScheduleContentProvider_Incoming);
		}

		@Override
		public boolean select(ITask task) {
			return (task.getSynchronizationState() == SynchronizationState.INCOMING || task.getSynchronizationState() == SynchronizationState.INCOMING_NEW);

		}

	}

	public class Outgoing extends ScheduledTaskContainer {

		public Outgoing() {
			super(taskActivityManager, new DateRange(OUTGOING_TIME), Messages.TaskScheduleContentProvider_Outgoing);
		}

		@Override
		public boolean select(ITask task) {
			return (task.getSynchronizationState() == SynchronizationState.OUTGOING || task.getSynchronizationState() == SynchronizationState.OUTGOING_NEW);

		}

	}

	public class Completed extends ScheduledTaskContainer {

		public Completed() {
			super(taskActivityManager, new DateRange(COMPLETED_TIME), Messages.TaskScheduleContentProvider_Completed);
		}

		@Override
		public boolean select(ITask task) {
			return true;
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
