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
/*
 * Created on Dec 26, 2004
 */
package org.eclipse.mylyn.tasks.ui;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextListener;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.core.MylarStatusHandler;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.core.WebTask;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.WorkspaceAwareContextStore;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskListSaveManager;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskListWriter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskActivationHistory;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.DateRangeActivityDelegate;
import org.eclipse.mylyn.tasks.core.DateRangeContainer;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.tasks.core.ITaskListElement;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskCategory;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryTask.PriorityLevel;
import org.eclipse.swt.widgets.Display;

/**
 * TODO: pull task activity management out into new TaskActivityManager
 * 
 * @author Mik Kersten
 * @author Rob Elves (task activity)
 */
public class TaskListManager implements IPropertyChangeListener {

	// Did have preference for this so should re-enable
	private static final int START_HOUR = 8;

	private static final long SECOND = 1000;

	private static final long MINUTE = 60 * SECOND;

	private static final long ROLLOVER_DELAY = 30 * MINUTE;

	private static final int NUM_WEEKS_PREVIOUS = -1;

	private static final int NUM_WEEKS_NEXT = 1;

	private static final int NUM_WEEKS_FUTURE_START = 2;

	private static final int NUM_WEEKS_FUTURE_END = 8;

	private static final int NUM_WEEKS_PAST_START = -8;

	private static final int NUM_WEEKS_PAST_END = -2;

	public static final String ARCHIVE_CATEGORY_DESCRIPTION = "Archive";

	private static final String DESCRIPTION_THIS_WEEK = "This Week";

	private static final String DESCRIPTION_PREVIOUS_WEEK = "Previous Week";

	private static final String DESCRIPTION_NEXT_WEEK = "Next Week";

	private static final String DESCRIPTION_FUTURE = "Future";

	private static final String DESCRIPTION_PAST = "Past";

	public static final String[] ESTIMATE_TIMES = new String[] { "0 Hours", "1 Hours", "2 Hours", "3 Hours", "4 Hours",
			"5 Hours", "6 Hours", "7 Hours", "8 Hours", "9 Hours", "10 Hours" };

	private DateRangeContainer activityPreviousWeek;

	private DateRangeContainer activityThisWeek;

	private List<DateRangeContainer> activityWeekDays = new ArrayList<DateRangeContainer>();

	private DateRangeContainer activityNextWeek;

	private DateRangeContainer activityFuture;

	private DateRangeContainer activityPast;

	private ArrayList<DateRangeContainer> dateRangeContainers = new ArrayList<DateRangeContainer>();

	private Set<ITask> tasksWithReminders = new HashSet<ITask>();

	private Set<ITask> tasksWithDueDates = new HashSet<ITask>();

	private ITask currentTask = null;

	private String currentHandle = "";

	private Calendar currentTaskStart = null;

	private Calendar currentTaskEnd = null;

	private Map<ITask, Long> taskElapsedTimeMap = new ConcurrentHashMap<ITask, Long>();

	private List<ITaskActivityListener> activityListeners = new ArrayList<ITaskActivityListener>();

	private TaskListWriter taskListWriter;

	private File taskListFile;

	private TaskListSaveManager taskListSaveManager;

	// TODO: guard against overwriting the single instance?
	private TaskList taskList = new TaskList();

	private TaskActivationHistory taskActivityHistory = new TaskActivationHistory();

	private boolean taskListInitialized = false;

	private boolean taskActivityHistoryInitialized = false;

	private int startDay;

	private int endDay;

	private int scheduledEndHour;

	private Timer timer;

	/** public for testing */
	public Date startTime = new Date();

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

	private final ITaskListChangeListener CHANGE_LISTENER = new ITaskListChangeListener() {

		public void containerAdded(AbstractTaskContainer container) {
		}

		public void containerDeleted(AbstractTaskContainer container) {
		}

		public void containerInfoChanged(AbstractTaskContainer container) {
		}

		public void localInfoChanged(ITask task) {
		}

		public void repositoryInfoChanged(ITask task) {
		}

		public void taskAdded(ITask task) {
		}

		public void taskDeleted(ITask task) {
			TaskListManager.this.resetAndRollOver();
		}

		public void taskMoved(ITask task, AbstractTaskContainer fromContainer, AbstractTaskContainer toContainer) {
		}

	};

	private int timeTicks = 0;

	public TaskListManager(TaskListWriter taskListWriter, File file) {
		this.taskListFile = file;
		this.taskListWriter = taskListWriter;

		timer = new Timer();
		timer.schedule(new RolloverCheck(), ROLLOVER_DELAY, ROLLOVER_DELAY);
		taskList.addChangeListener(CHANGE_LISTENER);
	}

	public void init() {
		ContextCorePlugin.getContextManager().addActivityMetaContextListener(CONTEXT_LISTENER);
	}

	public void dispose() {
		ContextCorePlugin.getContextManager().removeActivityMetaContextListener(CONTEXT_LISTENER);
	}

	public TaskList resetTaskList() {
		resetActivity();
		taskList.reset();
		taskListInitialized = true;
		return taskList;
	}

	private void resetActivity() {
		taskElapsedTimeMap.clear();
		dateRangeContainers.clear();
		setupCalendarRanges();
	}

	/**
	 * Warning: if called twice task times will be wrong
	 */
	private void parseTaskActivityInteractionHistory() {
		if (!TasksUiPlugin.getTaskListManager().isTaskListInitialized()) {
			return;
		}
		List<InteractionEvent> events = ContextCorePlugin.getContextManager()
				.getActivityMetaContext()
				.getInteractionHistory();
		for (InteractionEvent event : events) {
			parseInteractionEvent(event);
		}
		taskActivityHistoryInitialized = true;
		parseFutureReminders();
	}

	public void parseFutureReminders() {
		activityFuture.clear();
		activityNextWeek.clear();

		for (DateRangeContainer day : activityWeekDays) {
			day.clear();
		}
		HashSet<ITask> toRemove = new HashSet<ITask>();
		toRemove.addAll(activityThisWeek.getDateRangeDelegates());
		for (ITask activity : toRemove) {
			DateRangeActivityDelegate delegate = (DateRangeActivityDelegate) activity;
			Calendar calendar = GregorianCalendar.getInstance();

			Date schedDate = delegate.getScheduledForDate();
			if (schedDate == null) {
				schedDate = delegate.getDueDate();
			}

			if (schedDate != null) {
				calendar.setTime(schedDate);
				if (!activityThisWeek.includes(calendar) && activityThisWeek.getElapsed(delegate) == 0) {
					activityThisWeek.remove(delegate);
				}
			} else {
				if (activityThisWeek.getElapsed(delegate) == 0) {
					activityThisWeek.remove(delegate);
				}
			}
		}
		GregorianCalendar tempCalendar = new GregorianCalendar();
		tempCalendar.setFirstDayOfWeek(startDay);

		Set<ITask> allScheduledTasks = new HashSet<ITask>();
		allScheduledTasks.addAll(tasksWithReminders);
		allScheduledTasks.addAll(tasksWithDueDates);

		for (ITask task : allScheduledTasks) {
			if (task instanceof DateRangeActivityDelegate) {
				task = ((DateRangeActivityDelegate) task).getCorrespondingTask();
			}

			Date schedDate = task.getScheduledForDate();
			if (schedDate == null || isOverdue(task)) {
				schedDate = task.getDueDate();
			}

			if (schedDate != null) {
				tempCalendar.setTime(schedDate);
				if (activityNextWeek.includes(tempCalendar)) {
					activityNextWeek.addTask(new DateRangeActivityDelegate(activityNextWeek, task, tempCalendar,
							tempCalendar));
				} else if (activityFuture.includes(tempCalendar)) {
					activityFuture.addTask(new DateRangeActivityDelegate(activityFuture, task, tempCalendar,
							tempCalendar));
				} else if (activityThisWeek.includes(tempCalendar) && !activityThisWeek.getChildren().contains(task)) {
					activityThisWeek.addTask(new DateRangeActivityDelegate(activityThisWeek, task, tempCalendar,
							tempCalendar));
				} else if (activityPreviousWeek.includes(tempCalendar)
						&& !activityPreviousWeek.getChildren().contains(task)) {
					activityPreviousWeek.addTask(new DateRangeActivityDelegate(activityPreviousWeek, task,
							tempCalendar, tempCalendar));
				} else if (activityPast.includes(tempCalendar) && !activityPast.getChildren().contains(task)) {
					activityPast.addTask(new DateRangeActivityDelegate(activityPast, task, tempCalendar, tempCalendar));
				}

				for (DateRangeContainer day : activityWeekDays) {
					if (day.includes(tempCalendar) && !day.getChildren().contains(task)) {
						day.addTask(new DateRangeActivityDelegate(day, task, tempCalendar, tempCalendar,
								this.getElapsedTime(task)));
					}
				}
			}
		}
	}

	/** public for testing * */
	public void parseInteractionEvent(InteractionEvent event) {
		try {
			if (event.getDelta().equals(InteractionContextManager.ACTIVITY_DELTA_ACTIVATED)) {
				if (!event.getStructureHandle().equals(InteractionContextManager.ACTIVITY_HANDLE_ATTENTION)) {

					ITask activatedTask = TasksUiPlugin.getTaskListManager().getTaskList().getTask(
							event.getStructureHandle());

					if (currentTask != null && activatedTask != null) {
						if (!currentTask.equals(activatedTask)) {

							GregorianCalendar calendarEnd = new GregorianCalendar();
							calendarEnd.setFirstDayOfWeek(startDay);
							calendarEnd.setTime(event.getDate());
							calendarEnd.getTime();
							// Activation of different task before deactivation
							// of
							// previous, log was inconsistent,
							// finish what we started
							taskDeactivated(calendarEnd);
						} else {
							// skip re-activations of same task
							return;
						}
					}

					currentTask = activatedTask;
					if (currentTask != null) {
						GregorianCalendar calendar = new GregorianCalendar();
						calendar.setFirstDayOfWeek(startDay);
						calendar.setTime(event.getDate());
						currentTaskStart = calendar;
						currentHandle = event.getStructureHandle();
					}
				} else if (event.getStructureHandle().equals(InteractionContextManager.ACTIVITY_HANDLE_ATTENTION)) {
					if (currentTask != null && !currentHandle.equals("")) {
						long active = event.getEndDate().getTime() - event.getDate().getTime();

						// add to running total
						if (taskElapsedTimeMap.containsKey(currentTask)) {
							long pastTime = taskElapsedTimeMap.get(currentTask);
							taskElapsedTimeMap.put(currentTask, pastTime + active);
							timeTicks++;
							if (taskActivityHistoryInitialized && timeTicks > 3) {
								// Save incase of system failure.
								// TODO: request asynchronous save
								ContextCorePlugin.getContextManager().saveActivityContext();
								timeTicks = 0;
							}

						} else {
							taskElapsedTimeMap.put(currentTask, active);
						}
					}
				}
			} else if (event.getDelta().equals(InteractionContextManager.ACTIVITY_DELTA_DEACTIVATED)) {
				if (!event.getStructureHandle().equals(InteractionContextManager.ACTIVITY_HANDLE_ATTENTION)
						&& currentHandle.equals(event.getStructureHandle())) {
					GregorianCalendar calendarEnd = new GregorianCalendar();
					calendarEnd.setFirstDayOfWeek(startDay);
					calendarEnd.setTime(event.getDate());
					calendarEnd.getTime();
					taskDeactivated(calendarEnd);
				} else if (event.getStructureHandle().equals(InteractionContextManager.ACTIVITY_HANDLE_ATTENTION)) {
					// Deactivated attention events not currently used (ignored)
				}
			}
		} catch (Throwable t) {
			MylarStatusHandler.fail(t, "Error parsing interaction event", false);
		}
	}

	private void taskDeactivated(GregorianCalendar calendarEnd) {
		currentTaskEnd = calendarEnd;

		Set<DateRangeContainer> rangeSet = new HashSet<DateRangeContainer>();
		rangeSet.addAll(dateRangeContainers);
		rangeSet.add(activityThisWeek);

		for (DateRangeContainer week : rangeSet) {
			if (week.includes(currentTaskStart) && (!isWeekDay(week) && !week.isFuture())) {
				if (currentTask != null) {
					// add to date range 'bin'
					Long activeTime = taskElapsedTimeMap.get(currentTask);
					if (activeTime == null) {
						activeTime = new Long(0);
					}
					DateRangeActivityDelegate delegate = new DateRangeActivityDelegate(week, currentTask,
							currentTaskStart, currentTaskEnd, activeTime);
					week.addTask(delegate);
					if (taskActivityHistoryInitialized) {
						for (ITaskActivityListener listener : activityListeners) {
							listener.activityChanged(week);
						}
					}
				}
			}
		}

		currentTask = null;
		currentHandle = "";
	}

	/** public for testing * */
	public DateRangeContainer getActivityThisWeek() {
		return activityThisWeek;
	}

	/** public for testing * */
	public DateRangeContainer getActivityPast() {
		return activityPast;
	}

	/** public for testing * */
	public DateRangeContainer getActivityFuture() {
		return activityFuture;
	}

	/** public for testing * */
	public DateRangeContainer getActivityNextWeek() {
		return activityNextWeek;
	}

	/** public for testing * */
	public DateRangeContainer getActivityPrevious() {
		return activityPreviousWeek;
	}

	/** total elapsed time based on activation history */
	public long getElapsedTime(ITask task) {
		if (taskElapsedTimeMap.containsKey(task)) {
			return taskElapsedTimeMap.get(task);
		} else {
			return 0;
		}
	}

	private void setupCalendarRanges() {
		// MylarTaskListPlugin.getMylarCorePrefs().getInt(TaskListPreferenceConstants.PLANNING_STARTDAY);
		startDay = Calendar.MONDAY;
		// MylarTaskListPlugin.getMylarCorePrefs().getInt(TaskListPreferenceConstants.PLANNING_ENDDAY);
		endDay = Calendar.SUNDAY;
		// scheduledStartHour =
		// TasksUiPlugin.getDefault().getPreferenceStore().getInt(
		// TaskListPreferenceConstants.PLANNING_STARTHOUR);
		scheduledEndHour = TasksUiPlugin.getDefault().getPreferenceStore().getInt(
				TasksUiPreferenceConstants.PLANNING_ENDHOUR);

		activityWeekDays.clear();
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
			DateRangeContainer day = new DateRangeContainer(dayStart, dayEnd, summary);
			activityWeekDays.add(day);
			dateRangeContainers.add(day);
		}

		GregorianCalendar currentBegin = new GregorianCalendar();
		currentBegin.setFirstDayOfWeek(startDay);
		currentBegin.setTime(startTime);
		snapToStartOfWeek(currentBegin);
		GregorianCalendar currentEnd = new GregorianCalendar();
		currentEnd.setFirstDayOfWeek(startDay);
		currentEnd.setTime(startTime);
		snapToEndOfWeek(currentEnd);
		activityThisWeek = new DateRangeContainer(currentBegin, currentEnd, DESCRIPTION_THIS_WEEK);
		// dateRangeContainers.add(activityThisWeek);

		GregorianCalendar previousStart = new GregorianCalendar();
		previousStart.setFirstDayOfWeek(startDay);
		previousStart.setTime(startTime);
		previousStart.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_PREVIOUS);
		snapToStartOfWeek(previousStart);
		GregorianCalendar previousEnd = new GregorianCalendar();
		previousEnd.setFirstDayOfWeek(startDay);
		previousEnd.setTime(startTime);
		previousEnd.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_PREVIOUS);
		snapToEndOfWeek(previousEnd);
		activityPreviousWeek = new DateRangeContainer(previousStart.getTime(), previousEnd.getTime(),
				DESCRIPTION_PREVIOUS_WEEK);
		dateRangeContainers.add(activityPreviousWeek);

		GregorianCalendar nextStart = new GregorianCalendar();
		nextStart.setFirstDayOfWeek(startDay);
		nextStart.setTime(startTime);
		nextStart.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_NEXT);
		snapToStartOfWeek(nextStart);
		GregorianCalendar nextEnd = new GregorianCalendar();
		nextEnd.setFirstDayOfWeek(startDay);
		nextEnd.setTime(startTime);
		nextEnd.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_NEXT);
		snapToEndOfWeek(nextEnd);
		activityNextWeek = new DateRangeContainer(nextStart.getTime(), nextEnd.getTime(), DESCRIPTION_NEXT_WEEK);
		dateRangeContainers.add(activityNextWeek);

		GregorianCalendar futureStart = new GregorianCalendar();
		futureStart.setFirstDayOfWeek(startDay);
		futureStart.setTime(startTime);
		futureStart.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_FUTURE_START);
		snapToStartOfWeek(futureStart);
		GregorianCalendar futureEnd = new GregorianCalendar();
		futureEnd.setFirstDayOfWeek(startDay);
		futureEnd.setTime(startTime);
		futureEnd.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_FUTURE_END);
		snapToEndOfWeek(futureEnd);
		activityFuture = new DateRangeContainer(futureStart.getTime(), futureEnd.getTime(), DESCRIPTION_FUTURE);
		dateRangeContainers.add(activityFuture);

		GregorianCalendar pastStart = new GregorianCalendar();
		pastStart.setFirstDayOfWeek(startDay);
		pastStart.setTime(startTime);
		pastStart.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_PAST_START);
		snapToStartOfWeek(pastStart);
		GregorianCalendar pastEnd = new GregorianCalendar();
		pastEnd.setFirstDayOfWeek(startDay);
		pastEnd.setTime(startTime);
		pastEnd.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_PAST_END);
		snapToEndOfWeek(pastEnd);
		activityPast = new DateRangeContainer(pastStart.getTime(), pastEnd.getTime(), DESCRIPTION_PAST);
		dateRangeContainers.add(activityPast);
	}

	public void snapToNextDay(Calendar cal) {
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.getTime();
	}

	private void snapToStartOfWeek(Calendar cal) {
		cal.getTime();
		cal.set(Calendar.DAY_OF_WEEK, startDay);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.getTime();
	}

	private void snapToEndOfWeek(Calendar cal) {
		cal.getTime();
		cal.set(Calendar.DAY_OF_WEEK, endDay);
		cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
		cal.getTime();
	}

	public Calendar setSecheduledIn(Calendar calendar, int days) {
		calendar.add(Calendar.DAY_OF_MONTH, days);
		calendar.set(Calendar.HOUR_OF_DAY, scheduledEndHour);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	/**
	 * Will schedule for today if past work-day's end.
	 */
	public Calendar setScheduledEndOfDay(Calendar calendar) {
		// Calendar now = Calendar.getInstance();
		// if (now.get(Calendar.HOUR_OF_DAY) >= scheduledEndHour) {
		// setSecheduledIn(calendar, 1);
		// }
		calendar.set(Calendar.HOUR_OF_DAY, scheduledEndHour);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	public void setScheduledNextWeek(Calendar calendar) {
		calendar.setTimeInMillis(TasksUiPlugin.getTaskListManager().getActivityNextWeek().getStart().getTimeInMillis());
		setScheduledEndOfDay(calendar);
	}

	public List<DateRangeContainer> getDateRanges() {
		// parseFutureReminders();
		// return dateRangeContainers.toArray(new
		// ITaskListElement[dateRangeContainers.size()]);
		return dateRangeContainers;
	}

	/**
	 * Every call to this method generates a unique handle, subsequent calls will have incremented task numbers
	 */
//	public String genUniqueTaskHandle() {
//		return TaskRepositoryManager.PREFIX_LOCAL + taskList.getNextTaskNum();
//	}
	public void refactorRepositoryUrl(String oldUrl, String newUrl) {
		if (oldUrl == null || newUrl == null || oldUrl.equals(newUrl)) {
			return;
		}
		List<ITask> activeTasks = taskList.getActiveTasks();
		for (ITask task : new ArrayList<ITask>(activeTasks)) {
			deactivateTask(task);
		}
		refactorOfflineHandles(oldUrl, newUrl);
		taskList.refactorRepositoryUrl(oldUrl, newUrl);

		File dataDir = new File(TasksUiPlugin.getDefault().getDataDirectory(),
				WorkspaceAwareContextStore.CONTEXTS_DIRECTORY);
		if (dataDir.exists() && dataDir.isDirectory()) {
			for (File file : dataDir.listFiles()) {
				int dotIndex = file.getName().lastIndexOf(".xml");
				if (dotIndex != -1) {
					String storedHandle;
					try {
						storedHandle = URLDecoder.decode(file.getName().substring(0, dotIndex),
								InteractionContextManager.CONTEXT_FILENAME_ENCODING);
						int delimIndex = storedHandle.lastIndexOf(RepositoryTaskHandleUtil.HANDLE_DELIM);
						if (delimIndex != -1) {
							String storedUrl = storedHandle.substring(0, delimIndex);
							if (oldUrl.equals(storedUrl)) {
								String id = RepositoryTaskHandleUtil.getTaskId(storedHandle);
								String newHandle = RepositoryTaskHandleUtil.getHandle(newUrl, id);
								File newFile = ContextCorePlugin.getContextManager().getFileForContext(newHandle);
								file.renameTo(newFile);
							}
						}
					} catch (Exception e) {
						MylarStatusHandler.fail(e, "Could not move context file: " + file.getName(), false);
					}
				}
			}
		}

		saveTaskList();
	}

	private void refactorOfflineHandles(String oldRepositoryUrl, String newRepositoryUrl) {
		TaskDataManager taskDataManager = TasksUiPlugin.getDefault().getTaskDataManager();
		for (ITask task : taskList.getAllTasks()) {
			if (task instanceof AbstractRepositoryTask) {
				AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) task;
				if (repositoryTask.getRepositoryUrl().equals(oldRepositoryUrl)) {
					RepositoryTaskData newTaskData = taskDataManager.getNewTaskData(repositoryTask.getHandleIdentifier());
					RepositoryTaskData oldTaskData = taskDataManager.getOldTaskData(repositoryTask.getHandleIdentifier());
					Set<RepositoryTaskAttribute> edits = taskDataManager.getEdits(repositoryTask.getHandleIdentifier());
					taskDataManager.remove(repositoryTask.getHandleIdentifier());

					String newHandle = RepositoryTaskHandleUtil.getHandle(newRepositoryUrl, repositoryTask.getTaskId());

					if (newTaskData != null) {
						newTaskData.setRepositoryURL(newRepositoryUrl);
						taskDataManager.setNewTaskData(newHandle, newTaskData);
					}
					if (oldTaskData != null) {
						oldTaskData.setRepositoryURL(newRepositoryUrl);
						taskDataManager.setOldTaskData(newHandle, oldTaskData);
					}
					if (!edits.isEmpty()) {
						taskDataManager.saveEdits(newHandle, edits);
					}
				}
			}
		}
		TasksUiPlugin.getDefault().getTaskDataManager().saveNow();
	}

	public boolean readExistingOrCreateNewList() {
		try {
			if (taskListFile.exists()) {
				taskListWriter.readTaskList(taskList, taskListFile, TasksUiPlugin.getDefault().getTaskDataManager());
			} else {
				resetTaskList();
			}

			for (ITask task : taskList.getAllTasks()) {
				if (task.getScheduledForDate() != null) {
					tasksWithReminders.add(task);
				}
				if (task.getDueDate() != null) {
					tasksWithDueDates.add(task);
				}
			}

			resetActivity();
			// parseFutureReminders();
			taskListInitialized = true;
			for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(activityListeners)) {
				listener.taskListRead();
			}
		} catch (Throwable t) {
			MylarStatusHandler.fail(t, "Could not read task list, consider restoring via view menu", true);
			return false;
		}
		return true;
	}

	/**
	 * Only to be called upon initial startup by plugin.
	 */
	public void initActivityHistory() {
		resetAndRollOver();
		taskActivityHistory.loadPersistentHistory();
	}

	/**
	 * Will not save an empty task list to avoid losing data on bad startup.
	 */
	public synchronized void saveTaskList() {
		try {
			if (taskListInitialized) {
				taskListSaveManager.saveTaskList(true, false);
				// TasksUiPlugin.getDefault().getPreferenceStore().setValue(TaskListPreferenceConstants.TASK_ID,
				// nextLocalTaskId);
			} else {
				MylarStatusHandler.log("task list save attempted before initialization", this);
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Could not save task list", true);
		}
	}

	public TaskList getTaskList() {
		return taskList;
	}

	public void addActivityListener(ITaskActivityListener listener) {
		activityListeners.add(listener);
	}

	public void removeActivityListener(ITaskActivityListener listener) {
		activityListeners.remove(listener);
	}

	public void activateTask(ITask task) {
		if (!TasksUiPlugin.getDefault().isMultipleActiveTasksMode()) {
			deactivateAllTasks();
		}

		try {
			taskList.setActive(task, true);
			for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(activityListeners)) {
				try {
					listener.taskActivated(task);
				} catch (Throwable t) {
					MylarStatusHandler.fail(t, "task activity listener failed: " + listener, false);
				}
			}
		} catch (Throwable t) {
			MylarStatusHandler.fail(t, "could not activate task", false);
		}
	}

	public void deactivateAllTasks() {
		// Make a copy to avoid modification on list being traversed; can result
		// in a ConcurrentModificationException
		List<ITask> activeTasks = taskList.getActiveTasks();
		for (ITask task : activeTasks) {
			deactivateTask(task);
		}
	}

	public void deactivateTask(ITask task) {
		if (task == null) {
			return;
		}

		if (task.isActive()) {
			taskList.setActive(task, false);
			for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(activityListeners)) {
				try {
					listener.taskDeactivated(task);
				} catch (Throwable t) {
					MylarStatusHandler.fail(t, "notification failed for: " + listener, false);
				}
			}
		}
	}

	public void setTaskListFile(File file) {
		this.taskListFile = file;
	}

	public void copyDataDirContentsTo(String newDataDir) {
		taskListSaveManager.copyDataDirContentsTo(newDataDir);
	}

	public boolean isTaskListInitialized() {
		return taskListInitialized;
	}

	public TaskListWriter getTaskListWriter() {
		return taskListWriter;
	}

	public File getTaskListFile() {
		return taskListFile;
	}

	public boolean isActiveThisWeek(ITask task) {
		for (ITask activityDelegateTask : activityThisWeek.getChildren()) {
			if (activityDelegateTask.getHandleIdentifier().equals(task.getHandleIdentifier())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return if a repository task, will only return true if the user is a
	 */
	public boolean isCompletedToday(ITask task) {
		if (task != null) {
			boolean isOwnedByUser = isOwnedByUser(task);
			if (!isOwnedByUser) {
				return false;
			} else {
				Date completionDate = task.getCompletionDate();
				if (completionDate != null) {
					Calendar tomorrow = Calendar.getInstance();
					snapToNextDay(tomorrow);
					Calendar yesterday = Calendar.getInstance();
					yesterday.set(Calendar.HOUR_OF_DAY, 0);
					yesterday.set(Calendar.MINUTE, 0);
					yesterday.set(Calendar.SECOND, 0);
					yesterday.set(Calendar.MILLISECOND, 0);

					return completionDate.compareTo(yesterday.getTime()) == 1
							&& completionDate.compareTo(tomorrow.getTime()) == -1;
				}
			}
		}
		return false;
	}

	public boolean isOwnedByUser(ITask task) {
		if (task instanceof WebTask || (task instanceof AbstractRepositoryTask && ((AbstractRepositoryTask) task).isLocal())) {
			return true;
		}

		if (task instanceof AbstractRepositoryTask) {
			AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) task;
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
					repositoryTask.getRepositoryKind(), repositoryTask.getRepositoryUrl());
			if (repository != null && repositoryTask.getOwner() != null) {
				return repositoryTask.getOwner().equals(repository.getUserName());
			}
		}

		return false;

		// if (task instanceof AbstractRepositoryTask && !(task instanceof
		// WebTask)) {
		// AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask)
		// task;
		// TaskRepository repository =
		// TasksUiPlugin.getRepositoryManager().getRepository(
		// repositoryTask.getRepositoryKind(),
		// repositoryTask.getRepositoryUrl());
		// if (repository != null && repositoryTask.getOwner() != null
		// && !repositoryTask.getOwner().equals(repository.getUserName())) {
		// return false;
		// }
		// }
		// return true;
	}

	public boolean isScheduledAfterThisWeek(ITask task) {
		if (task != null) {
			Date reminder = task.getScheduledForDate();
			if (reminder != null) {
				return reminder.compareTo(activityNextWeek.getStart().getTime()) > -1;
			}
		}
		return false;
	}

	public boolean isScheduledForLater(ITask task) {
		if (task != null) {
			Date reminder = task.getScheduledForDate();
			if (reminder != null) {
				return reminder.compareTo(activityFuture.getStart().getTime()) > -1;
			}
		}
		return false;
	}

	public boolean isScheduledForThisWeek(ITask task) {
		if (task != null) {
			Date reminder = task.getScheduledForDate();
			if (reminder != null) {
				Calendar weekStart = Calendar.getInstance();
				snapToStartOfWeek(weekStart);
				return (reminder.compareTo(weekStart.getTime()) >= 0 && reminder.compareTo(activityThisWeek.getEnd()
						.getTime()) <= 0);
			}
		}
		return false;
	}

	public boolean isScheduledForToday(ITask task) {
		if (task != null) {
			Date reminder = task.getScheduledForDate();
			if (reminder != null) {
				Calendar dayStart = Calendar.getInstance();
				dayStart.set(Calendar.HOUR_OF_DAY, 0);
				dayStart.set(Calendar.MINUTE, 0);
				dayStart.set(Calendar.SECOND, 0);
				dayStart.set(Calendar.MILLISECOND, 0);
				Calendar midnight = GregorianCalendar.getInstance();
				snapToNextDay(midnight);
				return (reminder.compareTo(dayStart.getTime()) >= 0 && reminder.compareTo(midnight.getTime()) == -1);
			}
		}
		return false;
	}

	/**
	 * TODO: Need to migrate to use of this method for setting of reminders
	 */
	public void setScheduledFor(ITask task, Date reminderDate) {
		if (task == null)
			return;
		task.setScheduledForDate(reminderDate);
		task.setReminded(false);
		if (reminderDate == null) {
			tasksWithReminders.remove(task);
		} else {
			tasksWithReminders.remove(task);
			tasksWithReminders.add(task);
		}
		parseFutureReminders();
		taskList.notifyLocalInfoChanged(task);
	}

	public void setDueDate(ITask task, Date dueDate) {
		task.setDueDate(dueDate);
		if (dueDate == null) {
			tasksWithDueDates.remove(task);
		} else {
			tasksWithDueDates.remove(task);
			tasksWithDueDates.add(task);
		}
		parseFutureReminders();
		taskList.notifyLocalInfoChanged(task);
	}

	/**
	 * @return true if task due date != null and has past
	 */
	public boolean isOverdue(ITask task) {
		return (!task.isCompleted() && task.getDueDate() != null && new Date().after(task.getDueDate()))
				&& isOwnedByUser(task);
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(TasksUiPreferenceConstants.PLANNING_STARTHOUR)
				|| event.getProperty().equals(TasksUiPreferenceConstants.PLANNING_ENDHOUR)) {
			// event.getProperty().equals(TaskListPreferenceConstants.PLANNING_STARTDAY)
			// scheduledStartHour =
			// TasksUiPlugin.getDefault().getPreferenceStore().getInt(
			// TaskListPreferenceConstants.PLANNING_STARTHOUR);
			scheduledEndHour = TasksUiPlugin.getDefault().getPreferenceStore().getInt(
					TasksUiPreferenceConstants.PLANNING_ENDHOUR);
		}
	}

	/** public for testing */
	public void resetAndRollOver() {
		taskActivityHistoryInitialized = false;
		tasksWithReminders.clear();
		tasksWithDueDates.clear();
		for (ITask task : taskList.getAllTasks()) {
			if (task.getScheduledForDate() != null) {
				tasksWithReminders.add(task);
			}
			if (task.getDueDate() != null) {
				tasksWithDueDates.add(task);
			}
		}
		resetActivity();
		parseTaskActivityInteractionHistory();
		for (ITaskActivityListener listener : activityListeners) {
			listener.calendarChanged();
		}
	}

	private class RolloverCheck extends TimerTask {

		@Override
		public void run() {
			if (!Platform.isRunning() || ContextCorePlugin.getDefault() == null) {
				return;
			} else {
				Calendar now = GregorianCalendar.getInstance();
				DateRangeContainer thisWeek = getActivityThisWeek();
				if (!thisWeek.includes(now)) {
					startTime = new Date();
					resetAndRollOver();
				}
			}
		}
	}

	public TaskActivationHistory getTaskActivationHistory() {
		return taskActivityHistory;
	}

	public Set<ITask> getScheduledForThisWeek() {
		Set<ITask> tasksScheduled = new HashSet<ITask>();
		for (ITask task : getActivityThisWeek().getChildren()) {
			if (isScheduledForThisWeek(task)) {
				tasksScheduled.add(task);
			}
		}
		return tasksScheduled;
	}

	/**
	 * @param element
	 *            tasklist element to retrieve a task for currently will work for (ITask, AbstractQueryHit)
	 * @param force -
	 *            if a query hit is passed you can either force construction of the task or not (if not and no task,
	 *            null is returned) TODO: Move into TaskList?
	 */
	public ITask getTaskForElement(ITaskListElement element, boolean force) {
		ITask task = null;
		if (element instanceof ITask) {
			task = (ITask) element;
		}
		return task;
	}

	protected void setTaskListSaveManager(TaskListSaveManager taskListSaveManager) {
		this.taskListSaveManager = taskListSaveManager;
		this.taskList.addChangeListener(taskListSaveManager);
	}

	public List<DateRangeContainer> getActivityWeekDays() {
		return activityWeekDays;
	}

	public boolean isWeekDay(DateRangeContainer dateRangeTaskContainer) {
		return activityWeekDays.contains(dateRangeTaskContainer);
	}

	public int getStartHour() {
		return START_HOUR;
	}

	public static void scheduleNewTask(ITask newTask) {
		newTask.setCreationDate(new Date());

		Calendar newTaskSchedule = Calendar.getInstance();
		int scheduledEndHour = TasksUiPlugin.getDefault().getPreferenceStore().getInt(
				TasksUiPreferenceConstants.PLANNING_ENDHOUR);
		// If past scheduledEndHour set for following day
		if (newTaskSchedule.get(Calendar.HOUR_OF_DAY) >= scheduledEndHour) {
			TasksUiPlugin.getTaskListManager().setSecheduledIn(newTaskSchedule, 1);
		} else {
			TasksUiPlugin.getTaskListManager().setScheduledEndOfDay(newTaskSchedule);
		}
		TasksUiPlugin.getTaskListManager().setScheduledFor(newTask, newTaskSchedule.getTime());
	}

	/**
	 * Creates a new local task and schedules for today
	 * @param summary if null DEFAULT_SUMMARY (New Task) used.
	 */
	public LocalTask createNewLocalTask(String summary) {
		if (summary == null) {
			summary = LocalRepositoryConnector.DEFAULT_SUMMARY;
		}
		LocalTask newTask = new LocalTask(""+taskList.getNextTaskNum(), summary);
		newTask.setPriority(PriorityLevel.P3.toString());

		scheduleNewTask(newTask);

		Object selectedObject = null;
		TaskListView view = TaskListView.getFromActivePerspective();
		if (view != null) {
			selectedObject = ((IStructuredSelection) view.getViewer().getSelection()).getFirstElement();
		}
		if (selectedObject instanceof TaskCategory) {
			taskList.addTask(newTask, (TaskCategory) selectedObject);
		} else if (selectedObject instanceof ITask) {
			ITask task = (ITask) selectedObject;
			if (task.getContainer() instanceof TaskCategory) {
				taskList.addTask(newTask, task.getContainer());
			} else if (view != null && view.getDrilledIntoCategory() instanceof TaskCategory) {
				taskList.addTask(newTask, view.getDrilledIntoCategory());
			} else {
				taskList.addTask(newTask, TasksUiPlugin.getTaskListManager().getTaskList().getUncategorizedCategory());
			}
		} else if (view != null && view.getDrilledIntoCategory() instanceof TaskCategory) {
			taskList.addTask(newTask, view.getDrilledIntoCategory());
		} else {
			if (view != null && view.getDrilledIntoCategory() != null) {
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), ITasksUiConstants.TITLE_DIALOG,
						"The new task has been added to the root of the list, since tasks can not be added to a query.");
			}
			taskList.addTask(newTask, taskList.getUncategorizedCategory());
		}
		return newTask;
	}

}
