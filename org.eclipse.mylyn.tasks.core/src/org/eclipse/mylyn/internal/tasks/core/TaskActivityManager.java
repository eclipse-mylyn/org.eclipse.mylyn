/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.ITaskActivityManager;

/**
 * Manages task elapsed time, scheduling, due dates, and the date ranges
 * 
 * @since 2.1
 * @author Rob Elves
 */
public class TaskActivityManager implements ITaskActivityManager {

	private static final int NUM_WEEKS_PREVIOUS_START = -1;

	private static final int NUM_WEEKS_PREVIOUS_END = -1;

	private static final int NUM_WEEKS_NEXT = 1;

	private static final int NUM_WEEKS_FUTURE_START = 2;

	private static final int NUM_WEEKS_PAST_END = -2;

	private static final String DESCRIPTION_THIS_WEEK = "This Week";

	private static final String DESCRIPTION_PREVIOUS_WEEK = "Previous Week";

	private static final String DESCRIPTION_NEXT_WEEK = "Next Week";

	private static final String DESCRIPTION_FUTURE = "Future";

	private static final String DESCRIPTION_PAST = "Past";

	private final TaskActivationHistory taskActivationHistory = new TaskActivationHistory();

	private final List<ITaskActivityListener> activityListeners = new ArrayList<ITaskActivityListener>();

	private final Set<ITask> allScheduledTasks = new HashSet<ITask>();

	private final Set<ITask> allDueTasks = new HashSet<ITask>();

	private final SortedMap<Calendar, Set<ITask>> scheduledTasks = Collections.synchronizedSortedMap(new TreeMap<Calendar, Set<ITask>>());

	private final SortedMap<Calendar, Set<ITask>> dueTasks = Collections.synchronizedSortedMap(new TreeMap<Calendar, Set<ITask>>());

	// Map of Calendar (hour) to Tasks active during that hour
	private final SortedMap<Calendar, Set<AbstractTask>> activeTasks = Collections.synchronizedSortedMap(new TreeMap<Calendar, Set<AbstractTask>>());

	// For a given task maps Calendar Hour to duration of time spent (milliseconds) with task active 
	private final Map<AbstractTask, SortedMap<Calendar, Long>> taskElapsedTimeMap = new ConcurrentHashMap<AbstractTask, SortedMap<Calendar, Long>>();

	private final List<ScheduledTaskContainer> scheduleWeekDays = new ArrayList<ScheduledTaskContainer>();

	private final ArrayList<ScheduledTaskContainer> scheduleContainers = new ArrayList<ScheduledTaskContainer>();

	private ScheduledTaskContainer scheduledThisWeek;

	private ScheduledTaskContainer scheduledNextWeek;

	private ScheduledTaskContainer scheduledFuture;

	private ScheduledTaskContainer scheduledPast;

	private ScheduledTaskContainer scheduledPrevious;

//	private int startHour = 9;

	private int endHour = 17;

	private final TaskList taskList;

	private ITask activeTask;

	private final TaskRepositoryManager repositoryManager;

	private int startDay = Calendar.MONDAY;

	private Date startTime = new Date();

	public static ITaskActivityManager INSTANCE;

	public TaskActivityManager(TaskRepositoryManager repositoryManager, TaskList taskList) {
		this.taskList = taskList;
		this.repositoryManager = repositoryManager;
		clear();
	}

	/**
	 * Get the user specified first day of the week (Calendar.SUNDAY | Calendar.MONDAY)
	 * 
	 * @see http://en.wikipedia.org/wiki/Days_of_the_week#First_day_of_the_week
	 */
	public int getWeekStartDay() {
		return startDay;
	}

	/**
	 * Set the first day of the week (Calendar.SUNDAY | Calendar.MONDAY)
	 * 
	 * @see http://en.wikipedia.org/wiki/Days_of_the_week#First_day_of_the_week
	 * 
	 * @param startDay
	 * 		(Calendar.SUNDAY | Calendar.MONDAY)
	 */
	public void setWeekStartDay(int startDay) {
		this.startDay = startDay;
	}

//	public int getEndDay() {
//		return endDay;
//	}

//	public void setEndDay(int endDay) {
//		this.endDay = endDay;
//	}

//	public int getStartHour() {
//		return startHour;
//	}
//
//	public void setStartHour(int startHour) {
//		this.startHour = startHour;
//	}
//
	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}

	public int getEndHour() {
		return endHour;
	}

	public void addActivityListener(ITaskActivityListener listener) {
		activityListeners.add(listener);
	}

	public void removeActivityListener(ITaskActivityListener listener) {
		activityListeners.remove(listener);
	}

	public void setStartTime(Date time) {
		this.startTime = time;
		// TODO: separate concerns
		setupCalendarRanges();
		reloadTimingData();
	}

	public void clear() {
		clear(new Date());
	}

	public void clear(Date date) {
		dueTasks.clear();
		scheduledTasks.clear();
		allScheduledTasks.clear();
		activeTasks.clear();
		taskActivationHistory.clear();
		taskElapsedTimeMap.clear();
		setStartTime(date);
	}

	private void reloadTimingData() {
		reloadScheduledData();
		for (ITaskActivityListener listener : activityListeners) {
			listener.activityReset();
		}
	}

	public void removeElapsedTime(ITask task, Date startDate, Date endDate) {
		Assert.isNotNull(task);
		Assert.isNotNull(startDate);
		Assert.isNotNull(endDate);
		// remove any time that has already accumulated in data structures
		SortedMap<Calendar, Long> activityMap = taskElapsedTimeMap.get(task);
		if (activityMap != null) {
			Calendar start = TaskActivityUtil.getCalendar();
			start.setTime(startDate);
			TaskActivityUtil.snapStartOfHour(start);
			Calendar end = TaskActivityUtil.getCalendar();
			end.setTime(endDate);
			TaskActivityUtil.snapEndOfHour(end);
			activityMap = activityMap.subMap(start, end);
			for (Calendar cal : new HashSet<Calendar>(activityMap.keySet())) {
				activityMap.remove(cal);
			}
			for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(activityListeners)) {
				try {
					listener.elapsedTimeUpdated(task, getElapsedTime(task));
				} catch (Throwable t) {
					StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
							"Task activity listener failed: \"" + listener + "\"", t));
				}
			}
		}

	}

	public void addElapsedTime(AbstractTask task, Date startDate, Date endDate) {
		Assert.isNotNull(task);
		Assert.isNotNull(startDate);
		Assert.isNotNull(endDate);

		SortedMap<Calendar, Long> activityMap = taskElapsedTimeMap.get(task);
		if (activityMap == null) {
			activityMap = Collections.synchronizedSortedMap(new TreeMap<Calendar, Long>());
			taskElapsedTimeMap.put(task, activityMap);
		}

		long attentionSpan = endDate.getTime() - startDate.getTime();

		// Ignore any potential negative or zero times
		if (attentionSpan <= 0) {
			return;
		}

		// granularity to the hour
		Calendar hourOfDay = TaskActivityUtil.getCalendar();
		hourOfDay.setTime(startDate);
		snapToStartOfHour(hourOfDay);
		Long daysActivity = activityMap.get(hourOfDay);
		if (daysActivity == null) {
			daysActivity = new Long(0);
		}

		daysActivity = daysActivity.longValue() + attentionSpan;

		activityMap.put(hourOfDay, daysActivity);

		Set<AbstractTask> active = activeTasks.get(hourOfDay);
		if (active == null) {
			active = new HashSet<AbstractTask>();
			activeTasks.put(hourOfDay, active);
		}
		active.add(task);

		long totalElapsed = getElapsedTime(activityMap);

		for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(activityListeners)) {
			try {
				listener.elapsedTimeUpdated(task, totalElapsed);
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
						"Task activity listener failed: \"" + listener + "\"", t));
			}
		}
	}

	private Calendar getNewInstance(Calendar cal) {
		Calendar newCal = TaskActivityUtil.getCalendar();
		newCal.setTimeInMillis(cal.getTimeInMillis());
		return newCal;
	}

	public void addScheduledTask(ITask task) {
		Calendar time = TaskActivityUtil.getCalendar();
		time.setTime(task.getScheduledForDate());
		snapToStartOfHour(time);
		Set<ITask> tasks = scheduledTasks.get(time);
		if (tasks == null) {
			tasks = new CopyOnWriteArraySet<ITask>();
			scheduledTasks.put(time, tasks);
		}
		tasks.add(task);
		allScheduledTasks.add(task);
	}

	public void removeScheduledTask(ITask task) {
		synchronized (scheduledTasks) {
			for (Set<ITask> setOfTasks : scheduledTasks.values()) {
				setOfTasks.remove(task);
			}
			allScheduledTasks.remove(task);
		}
	}

	public void addDueTask(ITask task) {
		Calendar time = TaskActivityUtil.getCalendar();
		time.setTime(task.getDueDate());
		snapToStartOfHour(time);
		synchronized (dueTasks) {
			Set<ITask> tasks = dueTasks.get(time);
			if (tasks == null) {
				tasks = new CopyOnWriteArraySet<ITask>();
				dueTasks.put(time, tasks);
			}
			tasks.add(task);
			allDueTasks.add(task);
		}

	}

	public void removeDueTask(ITask task) {
		synchronized (dueTasks) {
			for (Set<ITask> setOfTasks : dueTasks.values()) {
				setOfTasks.remove(task);
			}
			allDueTasks.remove(task);
		}
	}

	public void activateTask(ITask task) {
		activateTask(task, true);
	}

	private void activateTask(ITask task, boolean addToHistory) {
		deactivateActiveTask();

		// notify that a task is about to be activated
		for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(activityListeners)) {
			try {
				listener.preTaskActivated(task);
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
						"Task activity listener failed: " + listener, t));
			}
		}

		activeTask = task;
		activeTask.setActive(true);

		for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(activityListeners)) {
			try {
				listener.taskActivated(task);
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
						"Task activity listener failed: " + listener, t));
			}
		}
	}

	public void deactivateActiveTask() {
		if (activeTask != null) {
			deactivateTask(activeTask);
		}
	}

	public void deactivateTask(ITask task) {
		if (task == null) {
			return;
		}

		if (task.isActive() && task == activeTask) {
			// notify that a task is about to be deactivated
			for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(activityListeners)) {
				try {
					listener.preTaskDeactivated(task);
				} catch (Throwable t) {
					StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
							"Notification failed for: " + listener, t));
				}
			}

			activeTask.setActive(false);
			activeTask = null;

			for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(activityListeners)) {
				try {
					listener.taskDeactivated(task);
				} catch (Throwable t) {
					StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
							"Notification failed for: " + listener, t));
				}
			}
		}
	}

	/**
	 * returns active tasks from start to end (exclusive) where both are snapped to the beginning of the hour
	 */
	public Set<AbstractTask> getActiveTasks(Calendar start, Calendar end) {
		Set<AbstractTask> resultingTasks = new HashSet<AbstractTask>();
		Calendar startInternal = TaskActivityUtil.getCalendar();
		startInternal.setTimeInMillis(start.getTimeInMillis());
		TaskActivityUtil.snapStartOfHour(startInternal);

		Calendar endInternal = TaskActivityUtil.getCalendar();
		endInternal.setTimeInMillis(end.getTimeInMillis());
		TaskActivityUtil.snapStartOfHour(endInternal);

		synchronized (activeTasks) {
			SortedMap<Calendar, Set<AbstractTask>> result = activeTasks.subMap(startInternal, endInternal);
			for (Set<AbstractTask> set : result.values()) {
				resultingTasks.addAll(set);
			}
		}
		return resultingTasks;
	}

	public Set<ITask> getScheduledTasks(Calendar start, Calendar end) {
		Set<ITask> resultingTasks = new HashSet<ITask>();
		synchronized (scheduledTasks) {
			SortedMap<Calendar, Set<ITask>> result = scheduledTasks.subMap(start, end);
			for (Set<ITask> set : result.values()) {
				resultingTasks.addAll(set);
			}
		}
		return resultingTasks;
	}

	public Set<ITask> getDueTasks(Calendar start, Calendar end) {
		Set<ITask> resultingTasks = new HashSet<ITask>();
		SortedMap<Calendar, Set<ITask>> result = dueTasks.subMap(start, end);
		synchronized (dueTasks) {
			for (Set<ITask> set : result.values()) {
				resultingTasks.addAll(set);
			}
		}
		return resultingTasks;
	}

	/** total elapsed time based on activation history */
	public long getElapsedTime(ITask task) {
		SortedMap<Calendar, Long> activityMap = taskElapsedTimeMap.get(task);
		return getElapsedTime(activityMap);
	}

	private long getElapsedTime(SortedMap<Calendar, Long> activityMap) {
		// TODO: Keep a running total instead of recalculating all the time
		long result = 0;
		if (activityMap != null) {
			synchronized (activityMap) {
				for (Long time : activityMap.values()) {
					if (time != null) {
						result += time.longValue();
					}
				}
			}
		}
		return result;
	}

	/** total elapsed time based on activation history */
	public long getElapsedTime(ITask task, Calendar start, Calendar end) {
		long result = 0;

		Calendar startRange = snapToStartOfHour(getNewInstance(start));

		Calendar endRange = snapToEndOfHour(getNewInstance(end));

		SortedMap<Calendar, Long> activityMap = taskElapsedTimeMap.get(task);
		if (activityMap != null) {
			synchronized (activityMap) {
				activityMap = activityMap.subMap(startRange, endRange);
				for (Long time : activityMap.values()) {
					if (time != null) {
						result += time.longValue();
					}
				}
			}
		}
		return result;
	}

	// TODO: remove, copied from TaskListManager
	private Calendar snapToStartOfHour(Calendar cal) {
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.getTime();
		return cal;
	}

	// TODO: remove, copied from TaskListManager
	private Calendar snapToEndOfHour(Calendar cal) {
		cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
		cal.getTime();
		return cal;
	}

	// TODO: copy from TaskListManager
	public void snapToNextDay(Calendar cal) {
		cal.add(Calendar.DAY_OF_MONTH, 1);
		TaskActivityUtil.snapStartOfDay(cal);
	}

	public ITask getActiveTask() {
		return activeTask;
	}

	private void reloadScheduledData() {
		for (AbstractTask task : taskList.getAllTasks()) {
			if (task.getScheduledForDate() != null) {
				addScheduledTask(task);
			}
			if (task.getDueDate() != null) {
				addDueTask(task);
			}
		}
	}

	public void setScheduledFor(ITask task, Date reminderDate) {
		setScheduledFor(task, reminderDate, false);
	}

	public void setScheduledFor(ITask task, Date reminderDate, boolean floating) {
		// API-3.0: remove check
		if (task == null) {
			return;
		}

		if (reminderDate != null && !reminderDate.equals(task.getScheduledForDate())) {
			task.setReminded(false);
		}

		task.setScheduledForDate(reminderDate);
		task.internalSetFloatingScheduledDate(floating);
		if (reminderDate == null) {
			removeScheduledTask(task);
		} else {
			removeScheduledTask(task);
			addScheduledTask(task);
		}
		taskList.notifyTaskChanged(task, false);
	}

	public void setDueDate(ITask task, Date dueDate) {
		task.setDueDate(dueDate);
		if (dueDate == null) {
			removeDueTask(task);
		} else {
			removeDueTask(task);
			addDueTask(task);
		}
		taskList.notifyTaskChanged(task, false);
	}

	/**
	 * @return if a repository task, will only return true if the user is a
	 */
	public boolean isCompletedToday(ITask task) {
		if (task != null) {
			boolean isOwnedByUser = repositoryManager.isOwnedByUser(task);
			if (!isOwnedByUser) {
				return false;
			} else {

				Date completionDate = task.getCompletionDate();
				if (completionDate != null) {
					Calendar completedTime = TaskActivityUtil.getCalendar();
					completedTime.setTime(completionDate);
					return TaskActivityUtil.isToday(completedTime);
				}
			}
		}
		return false;
	}

	public boolean isPastReminder(ITask task) {
		if (task == null || task.isCompleted() || task.getScheduledForDate() == null) {
			return false;
		} else {
			return isPastReminder(task.getScheduledForDate(), task.isCompleted());
		}
	}

	public boolean isPastReminder(Date date, boolean isComplete) {
		if (date == null || isComplete) {
			return false;
		} else {
			Date now = new Date();
			if (/*!internalIsFloatingScheduledDate() && */date.compareTo(now) < 0) {
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean isDueToday(ITask task) {
		if (repositoryManager.isOwnedByUser(task) && !task.isCompleted() && task.getDueDate() != null) {
			Calendar cal = TaskActivityUtil.getCalendar();
			cal.setTimeInMillis(task.getDueDate().getTime());
			if (TaskActivityUtil.isToday(cal)) {
				return true;
			}
		}
		return false;
	}

	public boolean isOverdue(ITask task) {
		return (!task.isCompleted() && task.getDueDate() != null && new Date().after(task.getDueDate()))
				&& repositoryManager.isOwnedByUser(task);
	}

	public boolean isOwnedByUser(ITask task) {
		return repositoryManager.isOwnedByUser(task);
	}

	public boolean isActiveThisWeek(ITask task) {
		Calendar calStart = TaskActivityUtil.getCalendar();
		TaskActivityUtil.snapStartOfWorkWeek(calStart);
		Calendar calEnd = TaskActivityUtil.getCalendar();
		TaskActivityUtil.snapEndOfWeek(calEnd);
		return getElapsedTime(task, calStart, calEnd) > 0;
	}

	public boolean isScheduledForToday(ITask task) {
		if (task != null) {
			Date reminder = task.getScheduledForDate();
			if (reminder != null && !task.internalIsFloatingScheduledDate()) {
				Calendar time = TaskActivityUtil.getCalendar();
				time.setTime(reminder);
				return TaskActivityUtil.isToday(time);
			}
		}
		return false;
	}

	public boolean isScheduledForToday(Date date, boolean floating) {
		if (date != null) {
			if (!floating) {
				Calendar time = TaskActivityUtil.getCalendar();
				time.setTime(date);
				return TaskActivityUtil.isToday(time);
			}
		}
		return false;
	}

	public boolean isScheduledAfterThisWeek(ITask task) {
		if (task.getScheduledForDate() != null) {
			return isScheduledAfterThisWeek(task.getScheduledForDate());
		}

		return false;
	}

	public boolean isScheduledAfterThisWeek(Date date) {
		Calendar cal = TaskActivityUtil.getCalendar();
		if (date != null) {
			cal.setTime(date);
			return TaskActivityUtil.isAfterCurrentWeek(cal);
		}
		return false;
	}

	public boolean isScheduledForFuture(ITask task) {
		if (task != null) {
			Date reminder = task.getScheduledForDate();
			return isScheduledForFuture(reminder);
		}
		return false;
	}

	public boolean isScheduledForFuture(Date reminder) {
		if (reminder != null) {
			Calendar cal = TaskActivityUtil.getCalendar();
			cal.setTime(reminder);
			return TaskActivityUtil.isFuture(cal);
		}
		return false;
	}

	public boolean isScheduledForThisWeek(ITask task) {
		boolean result = false;
		if (task != null) {
			Date reminder = task.getScheduledForDate();
			result = isScheduledForThisWeek(reminder);
		}
		return result;
	}

	public boolean isScheduledForThisWeek(Date reminder) {
		if (reminder != null) {
			Calendar time = TaskActivityUtil.getCalendar();
			time.setTime(reminder);
			return TaskActivityUtil.isThisWeek(time);
		}
		return false;
	}

	public boolean isScheduledForNextWeek(ITask task) {
		if (task != null) {
			Date reminder = task.getScheduledForDate();
			if (reminder != null) {
				Calendar time = TaskActivityUtil.getCalendar();
				time.setTime(reminder);
				return TaskActivityUtil.isNextWeek(time);
			}
		}
		return false;
	}

	/**
	 * TODO: move to activity manager
	 */
	private void setupCalendarRanges() {

		scheduleContainers.clear();
		scheduleWeekDays.clear();

		int startDay = getWeekStartDay();
		//int endDay = TaskActivityManager.getInstance().getEndDay();
		// scheduledStartHour =
		// TasksUiPlugin.getDefault().getPreferenceStore().getInt(
		// TaskListPreferenceConstants.PLANNING_STARTHOUR);

//		scheduledEndHour = TasksUiPlugin.getDefault().getPreferenceStore().getInt(
//				TasksUiPreferenceConstants.PLANNING_ENDHOUR);

		Calendar pastStart = TaskActivityUtil.getCalendar();
		pastStart.setTimeInMillis(0);
//		pastStart.setFirstDayOfWeek(startDay);
//		pastStart.setTime(startTime);
//		pastStart.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_PAST_START);
//		TaskActivityUtil.snapToStartOfWeek(pastStart);
		Calendar pastEnd = TaskActivityUtil.getStartOfCurrentWeek();
		pastEnd.setFirstDayOfWeek(startDay);
		pastEnd.setTime(startTime);
		pastEnd.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_PAST_END);
		TaskActivityUtil.snapEndOfWeek(pastEnd);
		scheduledPast = new ScheduledTaskContainer(this, pastStart.getTime(), pastEnd.getTime(), DESCRIPTION_PAST);
		//scheduleContainers.add(scheduledPast);

		scheduleWeekDays.clear();
		for (int x = startDay; x < (startDay + 7); x++) {
			Calendar dayStart = TaskActivityUtil.getCalendar();
			Calendar dayEnd = TaskActivityUtil.getCalendar();

			if (x > 7) {
				dayStart.set(Calendar.DAY_OF_WEEK, x % 7);
				dayEnd.set(Calendar.DAY_OF_WEEK, x % 7);
			} else {
				dayStart.set(Calendar.DAY_OF_WEEK, x);
				dayEnd.set(Calendar.DAY_OF_WEEK, x);
			}

			TaskActivityUtil.snapStartOfDay(dayStart);
			TaskActivityUtil.snapEndOfDay(dayEnd);

			String summary = "<unknown>";
			switch (dayStart.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				summary = "Monday";
				break;
			case Calendar.TUESDAY:
				summary = "Tuesday";
				break;
			case Calendar.WEDNESDAY:
				summary = "Wednesday";
				break;
			case Calendar.THURSDAY:
				summary = "Thursday";
				break;
			case Calendar.FRIDAY:
				summary = "Friday";
				break;
			case Calendar.SATURDAY:
				summary = "Saturday";
				break;
			case Calendar.SUNDAY:
				summary = "Sunday";
				break;
			}
			ScheduledTaskContainer day = new ScheduledTaskContainer(this, dayStart, dayEnd, summary);
			scheduleWeekDays.add(day);
			scheduleContainers.add(day);
		}

		Calendar currentBegin = TaskActivityUtil.getCalendar();
		currentBegin.setTime(startTime);
		TaskActivityUtil.snapStartOfWorkWeek(currentBegin);
		Calendar currentEnd = TaskActivityUtil.getCalendar();
		currentEnd.setTime(startTime);
		TaskActivityUtil.snapEndOfWeek(currentEnd);
		scheduledThisWeek = new ScheduledTaskContainer(this, currentBegin, currentEnd, DESCRIPTION_THIS_WEEK);
		scheduledThisWeek.setCaptureFloating(true);
		//scheduleContainers.add(scheduledThisWeek);

//		GregorianCalendar currentBegin = new GregorianCalendar();
//		currentBegin.setFirstDayOfWeek(startDay);
//		currentBegin.setTime(startTime);
//		TaskActivityUtil.snapStartOfWorkWeek(currentBegin);
//		GregorianCalendar currentEnd = new GregorianCalendar();
//		currentEnd.setFirstDayOfWeek(startDay);
//		currentEnd.setTime(startTime);
//		TaskActivityUtil.snapEndOfWeek(currentEnd);
//		scheduledThisWeek = new ScheduledTaskContainer(this, currentBegin, currentEnd, DESCRIPTION_THIS_WEEK);
//		scheduledThisWeek.setCaptureFloating(true);
//		//scheduleContainers.add(scheduledThisWeek);

		Calendar nextStart = TaskActivityUtil.getCalendar();
		nextStart.setTime(startTime);
		nextStart.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_NEXT);
		TaskActivityUtil.snapStartOfWorkWeek(nextStart);
		Calendar nextEnd = TaskActivityUtil.getCalendar();
		nextEnd.setTime(startTime);
		nextEnd.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_NEXT);
		TaskActivityUtil.snapEndOfWeek(nextEnd);
		scheduledNextWeek = new ScheduledTaskContainer(this, nextStart.getTime(), nextEnd.getTime(),
				DESCRIPTION_NEXT_WEEK);
		scheduledNextWeek.setCaptureFloating(true);
		scheduleContainers.add(scheduledNextWeek);

		Calendar futureStart = TaskActivityUtil.getCalendar();
		futureStart.setTime(startTime);
		futureStart.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_FUTURE_START);
		TaskActivityUtil.snapStartOfWorkWeek(futureStart);
		Calendar futureEnd = TaskActivityUtil.getCalendar();
		futureEnd.setTime(startTime);
		futureEnd.add(Calendar.YEAR, 1);
		TaskActivityUtil.snapEndOfWeek(futureEnd);
		scheduledFuture = new ScheduledTaskContainer(this, futureStart.getTime(), futureEnd.getTime(),
				DESCRIPTION_FUTURE);
		scheduledFuture.setCaptureFloating(true);
		scheduleContainers.add(scheduledFuture);

		Calendar previousStart = TaskActivityUtil.getCalendar();
		previousStart.setTime(startTime);
		previousStart.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_PREVIOUS_START);
		TaskActivityUtil.snapStartOfWorkWeek(previousStart);
		Calendar previousEnd = TaskActivityUtil.getCalendar();
		previousEnd.setTime(startTime);
		previousEnd.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_PREVIOUS_END);
		TaskActivityUtil.snapEndOfWeek(previousEnd);
		scheduledPrevious = new ScheduledTaskContainer(this, previousStart.getTime(), previousEnd.getTime(),
				DESCRIPTION_PREVIOUS_WEEK);
		//scheduleContainers.add(scheduledPrevious);
	}

	public List<ScheduledTaskContainer> getDateRanges() {
		return scheduleContainers;
	}

	public List<ScheduledTaskContainer> getActivityWeekDays() {
		return scheduleWeekDays;
	}

	public boolean isWeekDay(ScheduledTaskContainer dateRangeTaskContainer) {
		return scheduleWeekDays.contains(dateRangeTaskContainer);
	}

	/** public for testing */
	public ScheduledTaskContainer getActivityThisWeek() {
		return scheduledThisWeek;
	}

	/** public for testing * */
	public ScheduledTaskContainer getActivityPast() {
		return scheduledPast;
	}

	/** public for testing */
	public ScheduledTaskContainer getActivityFuture() {
		return scheduledFuture;
	}

	/** public for testing */
	public ScheduledTaskContainer getActivityNextWeek() {
		return scheduledNextWeek;
	}

	/** public for testing */
	public ScheduledTaskContainer getActivityPrevious() {
		return scheduledPrevious;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void scheduleNewTask(AbstractTask newTask) {
		newTask.setCreationDate(new Date());
		// TODO: set based on preference? see bug#158461
		setScheduledFor(newTask, TaskActivityUtil.getCalendar().getTime());
	}

	public boolean isDueThisWeek(ITask task) {
		Date due = task.getDueDate();
		if (due != null && repositoryManager.isOwnedByUser(task)) {
			Calendar cal = TaskActivityUtil.getCalendar();
			cal.setTime(due);
			return TaskActivityUtil.isThisWeek(cal);
		}
		return false;
	}

	public boolean isFloatingThisWeek(AbstractTask singleTaskSelection) {
		if (singleTaskSelection != null && singleTaskSelection.getScheduledForDate() != null) {
			if (singleTaskSelection.internalIsFloatingScheduledDate()
					&& isScheduledForThisWeek(singleTaskSelection.getScheduledForDate())) {
				return true;
			}
		}
		return false;
	}

	public Set<ITask> getScheduledForThisWeek() {
		Calendar startWeek = TaskActivityUtil.getStartOfCurrentWeek();
		Calendar endWeek = TaskActivityUtil.getEndOfCurrentWeek();
		return getScheduledTasks(startWeek, endWeek);
	}

	public ScheduledTaskContainer getActivityToday() {
		for (ScheduledTaskContainer container : scheduleWeekDays) {
			if (container.isToday()) {
				return container;
			}
		}
		return null;
	}

	public ScheduledTaskContainer getActivityContainer(Calendar calendar, boolean isFloating) {
		if (calendar == null) {
			return null;
		}
		if (isPastReminder(calendar.getTime(), false)) {
			return getActivityToday();
		}

		if (!isFloating) {
			for (ScheduledTaskContainer container : scheduleWeekDays) {
				if (container.includes(calendar)) {
					return container;
				}
			}
		} else {
			if (scheduledThisWeek.includes(calendar)) {
				return scheduledThisWeek;
			} else if (scheduledNextWeek.includes(calendar)) {
				return scheduledNextWeek;
			} else {
				return scheduledFuture;
			}
		}

		return null;
	}

	public TaskActivationHistory getTaskActivationHistory() {
		return taskActivationHistory;
	}

	public Set<ITask> getAllScheduledTasks() {
		return new HashSet<ITask>(allScheduledTasks);
	}

	public Set<ITask> getAllDueTasks() {
		return new HashSet<ITask>(allDueTasks);
	}
}
