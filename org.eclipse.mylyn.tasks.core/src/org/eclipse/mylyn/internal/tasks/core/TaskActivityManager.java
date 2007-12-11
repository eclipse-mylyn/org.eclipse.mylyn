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
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextListener;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;

/**
 * Manager for Task timing, scheduling, due dates, and activation history
 * 
 * TODO: Consider extracting a TaskPlanningManager to hold scheduling and calendar ranges
 * 
 * @since 2.1
 * @author Rob Elves
 */
public class TaskActivityManager {

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

	private SortedMap<Calendar, Set<AbstractTask>> scheduledTasks = Collections.synchronizedSortedMap(new TreeMap<Calendar, Set<AbstractTask>>());

	private SortedMap<Calendar, Set<AbstractTask>> dueTasks = Collections.synchronizedSortedMap(new TreeMap<Calendar, Set<AbstractTask>>());

	private SortedMap<Calendar, Set<AbstractTask>> activeTasks = Collections.synchronizedSortedMap(new TreeMap<Calendar, Set<AbstractTask>>());

	private Map<AbstractTask, SortedMap<Calendar, Long>> taskElapsedTimeMap = new ConcurrentHashMap<AbstractTask, SortedMap<Calendar, Long>>();

	private List<ScheduledTaskContainer> scheduleWeekDays = new ArrayList<ScheduledTaskContainer>();

	private ArrayList<ScheduledTaskContainer> scheduleContainers = new ArrayList<ScheduledTaskContainer>();

	private ScheduledTaskContainer scheduledThisWeek;

	private ScheduledTaskContainer scheduledNextWeek;

	private ScheduledTaskContainer scheduledFuture;

	private ScheduledTaskContainer scheduledPast;

	private ScheduledTaskContainer scheduledPrevious;

	private int timeTicks;

	private int startHour = 9;

	private int endHour = 17;

	private boolean taskActivityHistoryInitialized = false;

	private TaskList taskList;

	private TaskRepositoryManager repositoryManager;

	private int startDay = Calendar.MONDAY;

	private int endDay = Calendar.SUNDAY;

	private Date startTime = new Date();

	private static TaskActivityManager INSTANCE;

	private TaskActivityManager() {
		// SINGLETON
	}

	public static synchronized TaskActivityManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TaskActivityManager();
		}
		return INSTANCE;
	}

	public void init(TaskRepositoryManager repositoryManager, TaskList taskList) {
		this.taskList = taskList;
		this.repositoryManager = repositoryManager;
		if (!isInitialized) {
			ContextCorePlugin.getContextManager().addActivityMetaContextListener(CONTEXT_LISTENER);
		}
		isInitialized = true;
	}

	public void dispose() {
		ContextCorePlugin.getContextManager().removeActivityMetaContextListener(CONTEXT_LISTENER);
	}

	public int getStartDay() {
		return startDay;
	}

	public void setStartDay(int startDay) {
		this.startDay = startDay;
	}

	public int getEndDay() {
		return endDay;
	}

	public void setEndDay(int endDay) {
		this.endDay = endDay;
	}

	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}

	public int getEndHour() {
		return endHour;
	}

	private void clear() {
		dueTasks.clear();
		scheduledTasks.clear();
		activeTasks.clear();
		taskElapsedTimeMap.clear();
	}

	public void reloadTimingData(Date date) {
		startTime = date;
		taskActivityHistoryInitialized = false;
		clear();
		List<InteractionEvent> events = ContextCorePlugin.getContextManager()
				.getActivityMetaContext()
				.getInteractionHistory();
		for (InteractionEvent event : events) {
			parseInteractionEvent(event);
		}
		reloadScheduledData();
		setupCalendarRanges();
		taskActivityHistoryInitialized = true;
	}

	/** public for testing * */
	public void parseInteractionEvent(InteractionEvent event) {
		try {
			if (event.getKind().equals(InteractionEvent.Kind.ATTENTION)
					&& (event.getDelta().equals(InteractionContextManager.ACTIVITY_DELTA_ADDED) || event.getDelta()
							.equals("add"))) {
				AbstractTask activatedTask = taskList.getTask(event.getStructureHandle());

				if (activatedTask != null) {

					addElapsedTimeForEvent(activatedTask, event);

					timeTicks++;
					if (taskActivityHistoryInitialized && timeTicks > 3) {
						// Save incase of system failure.
						// TODO: request asynchronous save
						ContextCorePlugin.getContextManager().saveActivityContext();
						timeTicks = 0;
					}
				}
				return;
			}
		} catch (Throwable t) {
			StatusHandler.fail(t, "Error parsing interaction event", false);
		}
	}

	private void addElapsedTimeForEvent(AbstractTask activatedTask, InteractionEvent event) {
		SortedMap<Calendar, Long> activityMap = taskElapsedTimeMap.get(activatedTask);
		if (activityMap == null) {
			activityMap = Collections.synchronizedSortedMap(new TreeMap<Calendar, Long>());
			taskElapsedTimeMap.put(activatedTask, activityMap);
		}

		long attentionSpan = event.getEndDate().getTime() - event.getDate().getTime();

		// Ignore any potential negative or zero times
		if (attentionSpan <= 0)
			return;

		// granularity to the hour
		Calendar hourOfDay = Calendar.getInstance();
		hourOfDay.setTime(event.getDate());
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
		active.add(activatedTask);
	}

	private void addScheduledTask(AbstractTask task) {
		Calendar time = Calendar.getInstance();
		time.setTime(task.getScheduledForDate());
		snapToStartOfHour(time);
		Set<AbstractTask> tasks = scheduledTasks.get(time);
		if (tasks == null) {
			tasks = new CopyOnWriteArraySet<AbstractTask>();
			scheduledTasks.put(time, tasks);
		}
		tasks.add(task);
	}

	private void removeScheduledTask(AbstractTask task) {
		synchronized (scheduledTasks) {
			for (Set<AbstractTask> setOfTasks : scheduledTasks.values()) {
				setOfTasks.remove(task);
			}
		}
	}

	private void addDueTask(AbstractTask task) {
		Calendar time = Calendar.getInstance();
		time.setTime(task.getDueDate());
		snapToStartOfHour(time);
		Set<AbstractTask> tasks = dueTasks.get(time);
		if (tasks == null) {
			tasks = new CopyOnWriteArraySet<AbstractTask>();
			dueTasks.put(time, tasks);
		}
		tasks.add(task);
	}

	private void removeDueTask(AbstractTask task) {
		synchronized (dueTasks) {
			for (Set<AbstractTask> setOfTasks : dueTasks.values()) {
				setOfTasks.remove(task);
			}
		}
	}

	public Set<AbstractTask> getActiveTasks(Calendar start, Calendar end) {
		Set<AbstractTask> resultingTasks = new HashSet<AbstractTask>();

		SortedMap<Calendar, Set<AbstractTask>> result = activeTasks.subMap(start, end);
		synchronized (activeTasks) {
			for (Set<AbstractTask> set : result.values()) {
				resultingTasks.addAll(set);
			}
		}
		return resultingTasks;
	}

	public Set<AbstractTask> getScheduledTasks(Calendar start, Calendar end) {
		Set<AbstractTask> resultingTasks = new HashSet<AbstractTask>();
		SortedMap<Calendar, Set<AbstractTask>> result = scheduledTasks.subMap(start, end);
		synchronized (scheduledTasks) {
			for (Set<AbstractTask> set : result.values()) {
				resultingTasks.addAll(set);
			}
		}
		return resultingTasks;
	}

	public Set<AbstractTask> getDueTasks(Calendar start, Calendar end) {
		Set<AbstractTask> resultingTasks = new HashSet<AbstractTask>();
		SortedMap<Calendar, Set<AbstractTask>> result = dueTasks.subMap(start, end);
		synchronized (activeTasks) {
			for (Set<AbstractTask> set : result.values()) {
				resultingTasks.addAll(set);
			}
		}
		return resultingTasks;
	}

	/** total elapsed time based on activation history */
	public long getElapsedTime(AbstractTask task) {
		long result = 0;
		SortedMap<Calendar, Long> activityMap = taskElapsedTimeMap.get(task);
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
	public long getElapsedTime(AbstractTask task, Calendar start, Calendar end) {
		long result = 0;

		Calendar startRange = Calendar.getInstance();
		startRange.setTimeInMillis(start.getTimeInMillis());
		snapToStartOfHour(startRange);

		Calendar endRange = Calendar.getInstance();
		endRange.setTimeInMillis(end.getTimeInMillis());
		snapToEndOfHour(endRange);

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

	private final IInteractionContextListener CONTEXT_LISTENER = new IInteractionContextListener() {

		public void contextActivated(IInteractionContext context) {
			// ignore
		}

		public void contextDeactivated(IInteractionContext context) {
			// ignore
		}

		public void contextCleared(IInteractionContext context) {
			// ignore
		}

		public void interestChanged(List<IInteractionElement> elements) {
			List<InteractionEvent> events = ContextCorePlugin.getContextManager()
					.getActivityMetaContext()
					.getInteractionHistory();
			InteractionEvent event = events.get(events.size() - 1);
			parseInteractionEvent(event);

		}

		public void elementDeleted(IInteractionElement element) {
			// ignore
		}

		public void landmarkAdded(IInteractionElement element) {
			// ignore
		}

		public void landmarkRemoved(IInteractionElement element) {
			// ignore
		}

		public void relationsChanged(IInteractionElement element) {
			// ignore
		}
	};

	private boolean isInitialized = false;

	// TODO: remove, copied from TaskListManager
	private void snapToStartOfHour(Calendar cal) {
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.getTime();
	}

	// TODO: remove, copied from TaskListManager
	private void snapToEndOfHour(Calendar cal) {
		cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
		cal.getTime();
	}

	// TODO: copy from TaskListManager
	public void snapToNextDay(Calendar cal) {
		cal.add(Calendar.DAY_OF_MONTH, 1);
		TaskActivityUtil.snapStartOfDay(cal);
	}

	public AbstractTask getActiveTask() {
		return taskList.getActiveTask();
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

	public void setScheduledFor(AbstractTask task, Date reminderDate) {
		// API-3.0: remove check
		if (task == null)
			return;
		
		if (reminderDate != null && !reminderDate.equals(task.getScheduledForDate())) {
			task.setReminded(false);
		}

		task.setScheduledForDate(reminderDate);
		if (reminderDate == null) {
			removeScheduledTask(task);
		} else {		
			removeScheduledTask(task);
			addScheduledTask(task);
		}
		taskList.notifyTaskChanged(task, false);
	}

	public void setDueDate(AbstractTask task, Date dueDate) {
		task.setDueDate(dueDate);
		if (dueDate == null) {
			removeDueTask(task);
		} else {
			removeDueTask(task);
			addDueTask(task);
		}
		taskList.notifyTaskChanged(task, false);
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	/**
	 * @return if a repository task, will only return true if the user is a
	 */
	public boolean isCompletedToday(AbstractTask task) {
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

	public boolean isOverdue(AbstractTask task) {
		return (!task.isCompleted() && task.getDueDate() != null && new Date().after(task.getDueDate()))
				&& repositoryManager.isOwnedByUser(task);
	}

	public boolean isOwnedByUser(AbstractTask task) {
		return repositoryManager.isOwnedByUser(task);
	}

	public boolean isActiveThisWeek(AbstractTask task) {
		Calendar calStart = Calendar.getInstance();
		TaskActivityUtil.snapStartOfWorkWeek(calStart);
		Calendar calEnd = Calendar.getInstance();
		TaskActivityUtil.snapEndOfWeek(calEnd);
		return getElapsedTime(task, calStart, calEnd) > 0;
	}

	public boolean isScheduledForToday(AbstractTask task) {
		if (task != null) {
			Date reminder = task.getScheduledForDate();
			if (reminder != null) {
				Calendar time = TaskActivityUtil.getCalendar();
				time.setTime(reminder);
				return TaskActivityUtil.isToday(time);
			}
		}
		return false;
	}

	public boolean isScheduledAfterThisWeek(AbstractTask task) {
		Calendar cal = TaskActivityUtil.getCalendar();
		if (task.getScheduledForDate() != null) {
			cal.setTime(task.getScheduledForDate());
			return TaskActivityUtil.isAfterCurrentWeek(cal);
		}

		return false;
	}

	public boolean isScheduledForFuture(AbstractTask task) {
		if (task != null) {
			Date reminder = task.getScheduledForDate();
			if (reminder != null) {
				Calendar cal = TaskActivityUtil.getCalendar();
				cal.setTime(reminder);
				return TaskActivityUtil.isFuture(cal);
			}
		}
		return false;
	}

	public boolean isScheduledForThisWeek(AbstractTask task) {
		if (task != null) {
			Date reminder = task.getScheduledForDate();
			if (reminder != null) {
				Calendar time = TaskActivityUtil.getCalendar();
				time.setTime(reminder);
				return TaskActivityUtil.isThisWeek(time);
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

		int startDay = TaskActivityManager.getInstance().getStartDay();
		//int endDay = TaskActivityManager.getInstance().getEndDay();
		// scheduledStartHour =
		// TasksUiPlugin.getDefault().getPreferenceStore().getInt(
		// TaskListPreferenceConstants.PLANNING_STARTHOUR);

//		scheduledEndHour = TasksUiPlugin.getDefault().getPreferenceStore().getInt(
//				TasksUiPreferenceConstants.PLANNING_ENDHOUR);

		Calendar pastStart = GregorianCalendar.getInstance();
		pastStart.setTimeInMillis(0);
//		pastStart.setFirstDayOfWeek(startDay);
//		pastStart.setTime(startTime);
//		pastStart.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_PAST_START);
//		snapToStartOfWeek(pastStart);
		GregorianCalendar pastEnd = new GregorianCalendar();
		pastEnd.setFirstDayOfWeek(startDay);
		pastEnd.setTime(startTime);
		pastEnd.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_PAST_END);
		TaskActivityUtil.snapEndOfWeek(pastEnd);
		scheduledPast = new ScheduledTaskContainer(this, pastStart.getTime(), pastEnd.getTime(), DESCRIPTION_PAST);
		scheduleContainers.add(scheduledPast);

		scheduleWeekDays.clear();
		for (int x = startDay; x < (startDay + 7); x++) {
			GregorianCalendar dayStart = new GregorianCalendar();
			GregorianCalendar dayEnd = new GregorianCalendar();
			dayStart.setFirstDayOfWeek(startDay);
			dayEnd.setFirstDayOfWeek(startDay);
			if (x > 7) {
				dayStart.set(Calendar.DAY_OF_WEEK, x % 7);
				dayEnd.set(Calendar.DAY_OF_WEEK, x % 7);
			} else {
				dayStart.set(Calendar.DAY_OF_WEEK, x);
				dayEnd.set(Calendar.DAY_OF_WEEK, x);
			}

			dayStart.set(Calendar.HOUR_OF_DAY, 0);
			dayStart.set(Calendar.MINUTE, 0);
			dayStart.set(Calendar.SECOND, 0);
			dayStart.set(Calendar.MILLISECOND, 0);
			dayStart.getTime();

			dayEnd.set(Calendar.HOUR_OF_DAY, dayEnd.getMaximum(Calendar.HOUR_OF_DAY));
			dayEnd.set(Calendar.MINUTE, dayEnd.getMaximum(Calendar.MINUTE));
			dayEnd.set(Calendar.SECOND, dayEnd.getMaximum(Calendar.SECOND));
			dayEnd.set(Calendar.MILLISECOND, dayEnd.getMaximum(Calendar.MILLISECOND));
			dayEnd.getTime();

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

		GregorianCalendar currentBegin = new GregorianCalendar();
		currentBegin.setFirstDayOfWeek(startDay);
		currentBegin.setTime(startTime);
		TaskActivityUtil.snapStartOfWorkWeek(currentBegin);
		GregorianCalendar currentEnd = new GregorianCalendar();
		currentEnd.setFirstDayOfWeek(startDay);
		currentEnd.setTime(startTime);
		TaskActivityUtil.snapEndOfWeek(currentEnd);
		scheduledThisWeek = new ScheduledTaskContainer(this, currentBegin, currentEnd, DESCRIPTION_THIS_WEEK);
		// dateRangeContainers.add(activityThisWeek);

		GregorianCalendar nextStart = new GregorianCalendar();
		nextStart.setFirstDayOfWeek(startDay);
		nextStart.setTime(startTime);
		nextStart.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_NEXT);
		TaskActivityUtil.snapStartOfWorkWeek(nextStart);
		GregorianCalendar nextEnd = new GregorianCalendar();
		nextEnd.setFirstDayOfWeek(startDay);
		nextEnd.setTime(startTime);
		nextEnd.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_NEXT);
		TaskActivityUtil.snapEndOfWeek(nextEnd);
		scheduledNextWeek = new ScheduledTaskContainer(this, nextStart.getTime(), nextEnd.getTime(),
				DESCRIPTION_NEXT_WEEK);
		scheduleContainers.add(scheduledNextWeek);

		GregorianCalendar futureStart = new GregorianCalendar();
		futureStart.setFirstDayOfWeek(startDay);
		futureStart.setTime(startTime);
		futureStart.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_FUTURE_START);
		TaskActivityUtil.snapStartOfWorkWeek(futureStart);
		GregorianCalendar futureEnd = new GregorianCalendar();
		futureEnd.setFirstDayOfWeek(startDay);
		futureEnd.setTime(startTime);
		futureEnd.add(Calendar.YEAR, 1);
		TaskActivityUtil.snapEndOfWeek(futureEnd);
		scheduledFuture = new ScheduledTaskContainer(this, futureStart.getTime(), futureEnd.getTime(),
				DESCRIPTION_FUTURE);
		scheduleContainers.add(scheduledFuture);

		GregorianCalendar previousStart = new GregorianCalendar();
		previousStart.setFirstDayOfWeek(startDay);
		previousStart.setTime(startTime);
		previousStart.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_PREVIOUS_START);
		TaskActivityUtil.snapStartOfWorkWeek(previousStart);
		GregorianCalendar previousEnd = new GregorianCalendar();
		previousEnd.setFirstDayOfWeek(startDay);
		previousEnd.setTime(startTime);
		previousEnd.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_PREVIOUS_END);
		TaskActivityUtil.snapEndOfWeek(previousEnd);
		scheduledPrevious = new ScheduledTaskContainer(this, previousStart.getTime(), previousEnd.getTime(),
				DESCRIPTION_PREVIOUS_WEEK);
		scheduleContainers.add(scheduledPrevious);
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
		Calendar newTaskSchedule = Calendar.getInstance();
		// If past scheduledEndHour set for following day
		if (newTaskSchedule.get(Calendar.HOUR_OF_DAY) >= getEndHour()) {
			TaskActivityUtil.snapForwardNumDays(newTaskSchedule, 1);
		} else {
			TaskActivityUtil.snapEndOfWorkDay(newTaskSchedule);
		}
		setScheduledFor(newTask, newTaskSchedule.getTime());
	}

	public boolean isDueThisWeek(AbstractTask task) {
		Date due = task.getDueDate();
		if (due != null && repositoryManager.isOwnedByUser(task)) {
			Calendar cal = TaskActivityUtil.getCalendar();
			cal.setTime(due);
			return TaskActivityUtil.isThisWeek(cal);
		}
		return false;
	}
}
