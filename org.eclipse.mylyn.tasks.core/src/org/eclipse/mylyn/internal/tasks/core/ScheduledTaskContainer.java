/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.osgi.util.NLS;

/**
 * @author Rob Elves
 * @author Mik Kersten
 * @author Sam Davis
 */
public class ScheduledTaskContainer extends AbstractTaskContainer {

	private final TaskActivityManager activityManager;

	private final String summary;

	private final DateRange range;

	private String shortSummary;

	public ScheduledTaskContainer(TaskActivityManager activityManager, DateRange range, String summary,
			String shortSummary) {
		this(activityManager, range, summary);
		if (shortSummary != null) {
			this.shortSummary = shortSummary;
		}
	}

	public ScheduledTaskContainer(TaskActivityManager activityManager, DateRange range, String summary) {
		super(summary == null ? range.toString(false) : summary);
		this.activityManager = activityManager;
		this.range = range;
		if (summary == null) {
			if (range instanceof DayDateRange && TaskActivityUtil.getNextWeek().includes(range)) {
				DayDateRange dayRange = (DayDateRange) range;
				String day = NLS.bind(Messages.ScheduledTaskContainer_Next_Day, dayRange.getDayOfWeek());
				this.summary = day;
				this.shortSummary = day;
			} else {
				this.summary = range.toString(false);
				this.shortSummary = this.summary;
			}
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

	@Override
	public Collection<ITask> getChildren() {
		// TODO: Cache this information until the next modification to pertinent data
		Calendar now = Calendar.getInstance();
		// extend range to include tasks scheduled for this container in different time zones
		// timezones range from UTC-12 to UTC+14, but we will ignore UTC+12 to +14 since we can't distinguish them from UTC-12 to -10
		// use minutes to allow for partial hour timezone offsets
		int offsetInMinutes = range.getStartDate().getTimeZone().getOffset(now.getTimeInMillis()) / 1000 / 60;
		if (offsetInMinutes > 11 * 60) {
			// when in UTC+12 to +14, show tasks scheduled in those time zones in the correct bin. This causes tasks scheduled in other time
			// zones to show a day late; tasks scheduled in other time zones for WeekDateRanges may not show at all
			offsetInMinutes = -(24 * 60 - offsetInMinutes);
		}
		int minutesForwardToDateline = 11 * 60 - offsetInMinutes;
		int minutesBackwardToDateline = 23 * 60 - minutesForwardToDateline;
		Calendar start = Calendar.getInstance();
		start.setTimeInMillis(range.getStartDate().getTimeInMillis());
		start.add(Calendar.MINUTE, -minutesForwardToDateline);
		Calendar end = Calendar.getInstance();
		end.setTimeInMillis(range.getEndDate().getTimeInMillis());
		end.add(Calendar.MINUTE, minutesBackwardToDateline);
		// All tasks scheduled for this date range
		Set<ITask> tasks = activityManager.getScheduledTasks(start, end);
		if (range instanceof WeekDateRange) {
			// remove tasks not scheduled for the week container itself, except for 2 weeks, in which case only remove
			// if they will show under future
			for (Iterator<ITask> iterator = tasks.iterator(); iterator.hasNext();) {
				ITask task = iterator.next();
				if (task instanceof AbstractTask) {
					DateRange scheduledDate = ((AbstractTask) task).getScheduledForDate();
					if (!(scheduledDate instanceof WeekDateRange)
							&& (TaskActivityUtil.getNextWeek().next().compareTo(range) != 0
									|| scheduledDate.getEndDate().after(end))) {
						iterator.remove();
					}
				}
			}
		}
		Set<ITask> children = new HashSet<ITask>();
		Calendar cal = TaskActivityUtil.getCalendar();
		for (ITask task : tasks) {
			if (!task.isCompleted() || isCompletedToday(task)) {

				if (isDueBeforeScheduled(task) && activityManager.isOwnedByUser(task)) {
					continue;
				}

				if (isThisWeekBin() && isScheduledForAWeek(task)) {
					// is due this week
					if (task.getDueDate() != null) {
						cal.setTime(task.getDueDate());
						if (range.includes(cal) && activityManager.isOwnedByUser(task)) {
							continue;
						}
					}

					addChild(children, task);
				}

				addChild(children, task);
			}
		}

		// Add due tasks if not the This Week container, and not scheduled for earlier date
		if (!TaskActivityUtil.getCurrentWeek().equals(range) && !TaskActivityUtil.getNextWeek().equals(range)) {
			// tasks are due at the start of a day, so only search in the range of times that correspond to
			// the start of the day in some time zone
			Calendar endDueSearch = Calendar.getInstance();
			endDueSearch.setTimeInMillis(range.getStartDate().getTimeInMillis());
			endDueSearch.add(Calendar.MINUTE, minutesBackwardToDateline);
			for (ITask task : activityManager.getDueTasks(start, endDueSearch)) {
				if (isScheduledBeforeDue(task)) {
					continue;
				}
				if (activityManager.isOwnedByUser(task)) {
					addChild(children, task);
				}
			}
		}

		// All over due/scheduled tasks are present in the Today folder
		if (isTodayBin()) {
			for (ITask task : activityManager.getOverScheduledTasks()) {
				if (isScheduledForADay(task)) {
					addChild(children, task);
				}
			}
			// add tasks whose scheduled date starts before today and ends today
			Calendar searchFrom = Calendar.getInstance();
			searchFrom.setTimeInMillis(start.getTimeInMillis());
			searchFrom.add(Calendar.DAY_OF_MONTH, -1);
			for (ITask task : activityManager.getScheduledTasks(searchFrom, end)) {
				if (isScheduledForADay(task)) {
					addChild(children, task);
				}
			}
			for (ITask task : activityManager.getOverDueTasks()) {
				addChild(children, task);
			}

			ITask activeTask = activityManager.getActiveTask();
			if (activeTask != null && !children.contains(activeTask)) {
				addChild(children, activeTask);
			}
		}

		if (range instanceof WeekDateRange && ((WeekDateRange) range).isThisWeek()) {
			for (ITask task : activityManager.getOverScheduledTasks()) {
				if (isScheduledForAWeek(task)) {
					addChild(children, task);
				}
			}
		}

		return children;
	}

	private boolean isTodayBin() {
		return range instanceof DayDateRange && ((DayDateRange) range).isPresent();
	}

	private boolean isThisWeekBin() {
		return range instanceof WeekDateRange && ((WeekDateRange) range).isThisWeek();
	}

	private boolean isScheduledForAWeek(ITask task) {
		return task instanceof AbstractTask && ((AbstractTask) task).getScheduledForDate() instanceof WeekDateRange;
	}

	public boolean isDueBeforeScheduled(ITask task) {
		return task.getDueDate() != null
				&& task.getDueDate().before(((AbstractTask) task).getScheduledForDate().getEndDate().getTime());
	}

	private boolean isScheduledForADay(ITask task) {
		return task instanceof AbstractTask && !(((AbstractTask) task).getScheduledForDate() instanceof WeekDateRange);
	}

	private boolean isScheduledBeforeDue(ITask task) {
		return ((AbstractTask) task).getScheduledForDate() != null
				&& ((AbstractTask) task).getScheduledForDate().before(range.getStartDate());
	}

	private boolean isCompletedToday(ITask task) {
		return (task.isCompleted() && TaskActivityUtil.getDayOf(task.getCompletionDate()).isPresent());
	}

	private void addChild(Set<ITask> collection, ITask task) {
		collection.add(task);
	}

	@Override
	public String getSummary() {
		if (summary != null) {
			return summary;
		}
		return range.toString();
	}

	public String getShortSummary() {
		if (shortSummary != null) {
			return shortSummary;
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
