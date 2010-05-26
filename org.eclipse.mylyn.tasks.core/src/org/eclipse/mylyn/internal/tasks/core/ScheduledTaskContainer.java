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

package org.eclipse.mylyn.internal.tasks.core;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class ScheduledTaskContainer extends AbstractTaskContainer implements ITaskFilter {

	private final TaskActivityManager activityManager;

	private final String summary;

	private final DateRange range;

	public ScheduledTaskContainer(TaskActivityManager activityManager, DateRange range, String summary) {
		super(summary == null ? range.toString(false) : summary);
		this.activityManager = activityManager;
		this.range = range;
		if (summary == null) {
			this.summary = range.toString(false);
		} else {
			this.summary = summary;
		}
	}

	public ScheduledTaskContainer(TaskActivityManager taskActivityManager, DateRange day) {
		this(taskActivityManager, day, null);
	}

	public boolean isFuture() {
		return !isPresent() && range.getStartDate().after(Calendar.getInstance());
	}

	public boolean isPresent() {
		return range.getStartDate().before(Calendar.getInstance()) && range.getEndDate().after(Calendar.getInstance());
	}

	public boolean select(ITask selectTask) {

		Set<ITask> children = new HashSet<ITask>();

		// All tasks scheduled for this date range
		for (ITask task : activityManager.getScheduledTasks(range)) {
			if (!task.isCompleted()
					|| (task.isCompleted() && TaskActivityUtil.getDayOf(task.getCompletionDate()).isPresent())) {
				children.add(task);
			}
		}

		// Add due tasks if not the This Week container
		if (!(range instanceof WeekDateRange && ((WeekDateRange) range).isPresent())) {
			for (ITask task : activityManager.getDueTasks(range.getStartDate(), range.getEndDate())) {
				if (activityManager.isOwnedByUser(task)) {
					children.add(task);
				}
			}
		}

		// All over due/scheduled tasks are present in the Today folder
		if ((range instanceof DayDateRange) && ((DayDateRange) range).isPresent()) {
			for (ITask task : activityManager.getOverScheduledTasks()) {
				if (task instanceof AbstractTask
						&& !(((AbstractTask) task).getScheduledForDate() instanceof WeekDateRange)) {
					children.add(task);
				}
			}
			children.addAll(activityManager.getOverDueTasks());
			// if not scheduled or due in future, and is active, place in today bin
			ITask activeTask = activityManager.getActiveTask();
			if (activeTask != null && !children.contains(activeTask)) {
				children.add(activeTask);
			}
		}

		if (range instanceof WeekDateRange && ((WeekDateRange) range).isThisWeek()) {
			for (ITask task : activityManager.getOverScheduledTasks()) {
				if (task instanceof AbstractTask
						&& ((AbstractTask) task).getScheduledForDate() instanceof WeekDateRange) {
					children.add(task);
				}
			}
		}

		return children.contains(selectTask);

	}

	@Override
	public String getSummary() {
		if (summary != null) {
			return summary;
		}
		return range.toString();
	}

	@Override
	public String getHandleIdentifier() {
		return summary;
	}

	@Override
	public String getPriority() {
		return ""; //$NON-NLS-1$
	}

	@Override
	public String getUrl() {
		return ""; //$NON-NLS-1$
	}

	@Override
	public int compareTo(IRepositoryElement element) {
		if (element instanceof ScheduledTaskContainer) {
			ScheduledTaskContainer container = ((ScheduledTaskContainer) element);
			return range.compareTo(container.getDateRange());
		}
		return 0;
	}

	public DateRange getDateRange() {
		return range;
	}

	public Calendar getEnd() {
		return range.getEndDate();
	}

	public Calendar getStart() {
		return range.getStartDate();
	}

	public boolean includes(Calendar pastWeeksTaskStart) {
		return range.includes(pastWeeksTaskStart);
	}

}
