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
package org.eclipse.mylar.provisional.tasklist;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylar.internal.core.MylarContextManager;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.core.util.TimerThread;
import org.eclipse.mylar.internal.tasklist.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasklist.util.TaskActivityTimer;
import org.eclipse.mylar.internal.tasklist.util.TaskListWriter;
import org.eclipse.mylar.provisional.core.IMylarContext;
import org.eclipse.mylar.provisional.core.IMylarContextListener;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.InteractionEvent;
import org.eclipse.mylar.provisional.core.MylarPlugin;

/**
 * TODO: clean-up
 * 
 * @author Mik Kersten
 */
public class TaskListManager {

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

	private DateRangeContainer activityThisWeek;

	private DateRangeContainer activityNextWeek;

	private DateRangeContainer activityPreviousWeek;

	private DateRangeContainer activityFuture;

	private DateRangeContainer activityPast;

	private boolean isInactive;

	private long startInactive;

	private long totalInactive;

	private ArrayList<DateRangeContainer> dateRangeContainers = new ArrayList<DateRangeContainer>();

	private Set<ITask> tasksWithReminders = new HashSet<ITask>();

	private ITask currentTask = null;

	private String currentHandle = "";

	private Calendar currentTaskStart = null;

	private Calendar currentTaskEnd = null;

	private Map<ITask, TaskActivityTimer> timerMap = new HashMap<ITask, TaskActivityTimer>();

	private List<ITaskChangeListener> changeListeners = new ArrayList<ITaskChangeListener>();

	private List<ITaskActivityListener> activityListeners = new ArrayList<ITaskActivityListener>();

	private TaskListWriter taskListWriter;

	private File taskListFile;

	private TaskList taskList = new TaskList();

	private boolean taskListInitialized = false;

	private int nextTaskId;

	private int timerSleepInterval = TimerThread.DEFAULT_SLEEP_INTERVAL;

	private final IMylarContextListener CONTEXT_LISTENER = new IMylarContextListener() {

		public void contextActivated(IMylarContext context) {
			parseTaskActivityInteractionHistory();
		}

		public void contextDeactivated(IMylarContext context) {

		}

		public void interestChanged(IMylarElement element) {
			// String taskHandle = element.getHandleIdentifier();
			List<InteractionEvent> events = MylarPlugin.getContextManager().getActivityHistoryMetaContext()
					.getInteractionHistory();
			InteractionEvent event = events.get(events.size() - 1);
			parseInteractionEvent(event);
		}

		public void presentationSettingsChanging(UpdateKind kind) {
			// ignore
		}

		public void presentationSettingsChanged(UpdateKind kind) {
			// ignore
		}

		public void interestChanged(List<IMylarElement> elements) {
			// ignore
		}

		public void nodeDeleted(IMylarElement element) {
			// ignore
		}

		public void landmarkAdded(IMylarElement element) {
			// ignore
		}

		public void landmarkRemoved(IMylarElement element) {
			// ignore
		}

		public void edgesChanged(IMylarElement element) {
			// ignore
		}
	};

	public TaskListManager(TaskListWriter taskListWriter, File file, int startId) {
		this.taskListFile = file;
		this.taskListWriter = taskListWriter;
		this.nextTaskId = startId;

		setupCalendarRanges();
		MylarPlugin.getContextManager().addActivityMetaContextListener(CONTEXT_LISTENER);
	}

	public void dispose() {
		MylarPlugin.getContextManager().removeActivityMetaContextListener(CONTEXT_LISTENER);
	}

	public TaskList createNewTaskList() {
		resetActivity();
		taskList = new TaskList();
		taskListInitialized = true;
		return taskList;
	}

	private void resetActivity() {
		dateRangeContainers.clear();
		setupCalendarRanges();
	}

	private void parseTaskActivityInteractionHistory() {
		if (!MylarTaskListPlugin.getTaskListManager().isTaskListInitialized()) {
			return;
		}
		List<InteractionEvent> events = MylarPlugin.getContextManager().getActivityHistoryMetaContext()
				.getInteractionHistory();
		for (InteractionEvent event : events) {
			parseInteractionEvent(event);
		}
	}

	private void parseFutureReminders() {
		activityFuture.clear();
		activityNextWeek.clear();
		GregorianCalendar tempCalendar = new GregorianCalendar();
		for (ITask task : tasksWithReminders) {
			if (task.getReminderDate() != null) {
				tempCalendar.setTime(task.getReminderDate());
				if (activityNextWeek.includes(tempCalendar)) {
					activityNextWeek.addTask(new DateRangeActivityDelegate(activityNextWeek, task, tempCalendar,
							tempCalendar));
				} else if (activityFuture.includes(tempCalendar)) {
					activityFuture.addTask(new DateRangeActivityDelegate(activityFuture, task, tempCalendar,
							tempCalendar));
				}
			}
		}
	}

	/** public for testing **/
	public void parseInteractionEvent(InteractionEvent event) {
		if (event.getDelta().equals(MylarContextManager.ACTIVITY_ACTIVATED)) {
			if (!event.getStructureHandle().equals(MylarContextManager.ACTIVITY_HANDLE)) {
				if (isInactive) {
					isInactive = false;
					totalInactive = 0;
					startInactive = 0;
				}
				currentTask = MylarTaskListPlugin.getTaskListManager().getTaskForHandle(event.getStructureHandle(),
						true);
				if (currentTask != null) {
					GregorianCalendar calendar = new GregorianCalendar();
					calendar.setTime(event.getDate());
					currentTaskStart = calendar;
					currentHandle = event.getStructureHandle();
				}
			} else if (event.getStructureHandle().equals(MylarContextManager.ACTIVITY_HANDLE) && isInactive) {
				isInactive = false;
				totalInactive = event.getDate().getTime() - startInactive;
			}
		} else if (event.getDelta().equals(MylarContextManager.ACTIVITY_DEACTIVATED)) {
			if (!event.getStructureHandle().equals(MylarContextManager.ACTIVITY_HANDLE)
					&& currentHandle.equals(event.getStructureHandle())) {
				GregorianCalendar calendarEnd = new GregorianCalendar();
				calendarEnd.setTime(event.getDate());
				calendarEnd.getTime();
				currentTaskEnd = calendarEnd;
				if (isInactive) {
					isInactive = false;
					totalInactive = event.getDate().getTime() - startInactive;
				}
				for (DateRangeContainer week : dateRangeContainers) {
					if (week.includes(currentTaskStart)) {
						week.addTask(new DateRangeActivityDelegate(week, currentTask, currentTaskStart, currentTaskEnd,
								totalInactive));
						for (ITaskActivityListener listener : activityListeners) {
							listener.activityChanged(week);
						}
					}
				}
				currentTask = null;
				currentHandle = "";
				totalInactive = 0;
				startInactive = 0;
			} else if (event.getStructureHandle().equals(MylarContextManager.ACTIVITY_HANDLE) && !isInactive) {
				isInactive = true;
				startInactive = event.getDate().getTime();
			}
		}
	}
	
	/** public for testing **/
	public DateRangeContainer getActivityThisWeek() {
		return activityThisWeek;
	}

	/** public for testing **/
	public DateRangeContainer getActivityPast() {
		return activityPast;
	}
	/** public for testing **/
	public DateRangeContainer getActivityFuture() {
		return activityFuture;
	}

	/** public for testing **/
	public DateRangeContainer getActivityNextWeek() {
		return activityNextWeek;
	}

	/** public for testing **/
	public DateRangeContainer getActivityPrevious() {
		return activityPreviousWeek;
	}

	private void setupCalendarRanges() {

		GregorianCalendar currentBegin = new GregorianCalendar();
		Date startTime = new Date();
		currentBegin.setTime(startTime);
		snapToStartOfWeek(currentBegin);
		GregorianCalendar currentEnd = new GregorianCalendar();
		snapToEndOfWeek(currentEnd);
		activityThisWeek = new DateRangeContainer(currentBegin, currentEnd, DESCRIPTION_THIS_WEEK);
		dateRangeContainers.add(activityThisWeek);

		GregorianCalendar previousStart = new GregorianCalendar();
		previousStart.setTime(new Date());
		previousStart.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_PREVIOUS);
		snapToStartOfWeek(previousStart);
		GregorianCalendar previousEnd = new GregorianCalendar();
		previousEnd.setTime(new Date());
		previousEnd.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_PREVIOUS);
		snapToEndOfWeek(previousEnd);
		activityPreviousWeek = new DateRangeContainer(previousStart.getTime(), previousEnd.getTime(),
				DESCRIPTION_PREVIOUS_WEEK);
		dateRangeContainers.add(activityPreviousWeek);

		GregorianCalendar nextStart = new GregorianCalendar();
		nextStart.setTime(new Date());
		nextStart.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_NEXT);
		snapToStartOfWeek(nextStart);
		GregorianCalendar nextEnd = new GregorianCalendar();
		nextEnd.setTime(new Date());
		nextEnd.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_NEXT);
		snapToEndOfWeek(nextEnd);
		activityNextWeek = new DateRangeContainer(nextStart.getTime(), nextEnd.getTime(), DESCRIPTION_NEXT_WEEK);
		dateRangeContainers.add(activityNextWeek);

		GregorianCalendar futureStart = new GregorianCalendar();
		futureStart.setTime(new Date());
		futureStart.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_FUTURE_START);
		snapToStartOfWeek(futureStart);
		GregorianCalendar futureEnd = new GregorianCalendar();
		futureEnd.setTime(new Date());
		futureEnd.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_FUTURE_END);
		snapToEndOfWeek(futureEnd);
		activityFuture = new DateRangeContainer(futureStart.getTime(), futureEnd.getTime(), DESCRIPTION_FUTURE);
		dateRangeContainers.add(activityFuture);

		GregorianCalendar pastStart = new GregorianCalendar();
		pastStart.setTime(new Date());
		pastStart.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_PAST_START);
		snapToStartOfWeek(pastStart);
		GregorianCalendar pastEnd = new GregorianCalendar();
		pastEnd.setTime(new Date());
		pastEnd.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_PAST_END);
		snapToEndOfWeek(pastEnd);
		activityPast = new DateRangeContainer(pastStart.getTime(), pastEnd.getTime(), DESCRIPTION_PAST);
		dateRangeContainers.add(activityPast);
	}

	private void snapToStartOfWeek(GregorianCalendar cal) {
		cal.getTime();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.getTime();
	}

	private void snapToEndOfWeek(GregorianCalendar cal) {
		cal.getTime();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
		cal.getTime();
	}

	public Object[] getDateRanges() {
		// parseFutureReminders();
		return dateRangeContainers.toArray();
	}

	/**
	 * Exposed for unit testing
	 * 
	 * @return unmodifiable collection of ITaskActivityListeners
	 */
	public List<ITaskChangeListener> getChangeListeners() {
		return Collections.unmodifiableList(changeListeners);
	}

	public String genUniqueTaskHandle() {
		return TaskRepositoryManager.PREFIX_LOCAL + nextTaskId++;
	}

	public boolean readExistingOrCreateNewList() {
		try {
			if (taskListFile.exists()) {
				taskList = new TaskList();
				taskListWriter.readTaskList(taskList, taskListFile);
				int maxHandle = taskList.findLargestTaskHandle();
				if (maxHandle >= nextTaskId) {
					nextTaskId = maxHandle + 1;
				}
			} else {
				createNewTaskList();
			}

			for (ITask task : taskList.getAllTasks()) {
				if (task.getReminderDate() != null)
					tasksWithReminders.add(task);
			}
			resetActivity();
			parseFutureReminders();
			taskListInitialized = true;
			for (ITaskChangeListener listener : new ArrayList<ITaskChangeListener>(changeListeners)) {
				listener.tasklistRead();
			}

			// only activate the first task to avoid confusion of mutliple
			// active tasks on startup
			List<ITask> activeTasks = taskList.getActiveTasks();
			if (activeTasks.size() > 0) {
				activateTask(activeTasks.get(0));
			}
			parseTaskActivityInteractionHistory();
		} catch (Exception e) {
			MylarStatusHandler.log(e, "Could not read task list");
			return false;
		}
		return true;
	}

	/**
	 * Will not save an empty task list to avoid losing data on bad startup.
	 */
	public void saveTaskList() {
		try {
			if (taskListInitialized) {
				if (!taskList.isEmpty()) {
					taskListWriter.writeTaskList(taskList, taskListFile);
					MylarPlugin.getDefault().getPreferenceStore().setValue(TaskListPreferenceConstants.TASK_ID,
							nextTaskId);
				}
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

	public void setTaskList(TaskList taskList) {
		this.taskList = taskList;
	}

	public void moveToRoot(ITask task) {
		if (task.getCategory() instanceof TaskCategory) {
			((TaskCategory) task.getCategory()).removeTask(task);
		}
		// task.setCategory(null);
		// if (!taskList.getRootTasks().contains(task))
		taskList.internalAddRootTask(task);
		for (ITaskChangeListener listener : changeListeners)
			listener.taskListModified();
	}

	public void moveToCategory(TaskCategory category, ITask task) {
		if (category.equals(taskList.getRootCategory())) {
			moveToRoot(task);
		} else {
			taskList.removeFromRoot(task);
		}
		if (task.getCategory() instanceof TaskCategory) {
			((TaskCategory) task.getCategory()).removeTask(task);
		}
		if (!category.getChildren().contains(task)) {
			category.addTask(task);
		}
		task.setCategory(category);
		for (ITaskChangeListener listener : changeListeners)
			listener.taskListModified();
	}

	public void addCategory(ITaskContainer cat) {
		taskList.addCategory(cat);
		for (ITaskChangeListener listener : changeListeners)
			listener.taskListModified();
	}

	public void removeFromCategory(TaskCategory category, ITask task) {
		if (!category.isArchive()) {
			category.removeTask(task);
			task.setCategory(null);
		}
		for (ITaskChangeListener listener : changeListeners)
			listener.taskListModified();
	}

	public void removeFromRoot(ITask task) {
		taskList.removeFromRoot(task);
		for (ITaskChangeListener listener : changeListeners)
			listener.taskListModified();
	}

	public void addQuery(AbstractRepositoryQuery cat) {
		taskList.addQuery(cat);
		for (ITaskChangeListener listener : changeListeners)
			listener.taskListModified();
	}

	public void deleteTask(ITask task) {
		TaskActivityTimer taskTimer = timerMap.remove(task);
		if (taskTimer != null)
			taskTimer.stopTimer();
		taskList.setActive(task, false);
		taskList.deleteTask(task);
		for (ITaskChangeListener listener : changeListeners)
			listener.taskListModified();
	}

	public void deleteCategory(ITaskContainer cat) {
		taskList.deleteCategory(cat);
		for (ITaskChangeListener listener : changeListeners)
			listener.taskListModified();
	}

	public void deleteQuery(AbstractRepositoryQuery query) {
		taskList.deleteQuery(query);
		for (ITaskChangeListener listener : changeListeners)
			listener.taskListModified();
	}

	public void addChangeListener(ITaskChangeListener listener) {
		changeListeners.add(listener);
	}

	public void removeChangeListener(ITaskChangeListener listener) {
		changeListeners.remove(listener);
	}

	public void addActivityListener(ITaskActivityListener listener) {
		activityListeners.add(listener);
	}

	public void removeActivityListener(ITaskActivityListener listener) {
		activityListeners.remove(listener);
	}

	public void activateTask(ITask task) {
		if (!MylarTaskListPlugin.getDefault().isMultipleActiveTasksMode()) {
			for (ITask activeTask : new ArrayList<ITask>(taskList.getActiveTasks())) {
				deactivateTask(activeTask);
			}
		}

		try {
			int timeout = MylarPlugin.getContextManager().getInactivityTimeout();
			TaskActivityTimer activityTimer = new TaskActivityTimer(task, timeout, timerSleepInterval);
			activityTimer.startTimer();
			timerMap.put(task, activityTimer);
			taskList.setActive(task, true);
			for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(activityListeners)) {
				listener.taskActivated(task);
			}
		} catch (Throwable t) {
			MylarStatusHandler.fail(t, "could not activate task", false);
		}
	}

	public void deactivateTask(ITask task) {
		TaskActivityTimer taskTimer = timerMap.remove(task);
		if (taskTimer != null)
			taskTimer.stopTimer();
		taskList.setActive(task, false);
		for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(activityListeners)) {
			try {
				listener.taskDeactivated(task);
			} catch (Throwable t) {
				MylarStatusHandler.fail(t, "notification failed for: " + listener, false);
			}
		}
	}

	/**
	 * TODO: refactor into task deltas?
	 */
	public void notifyLocalInfoChanged(ITask task) {
		for (ITaskChangeListener listener : new ArrayList<ITaskChangeListener>(changeListeners)) {
			try {
				listener.localInfoChanged(task);
			} catch (Throwable t) {
				MylarStatusHandler.fail(t, "notification failed for: " + listener, false);
			}
		}
	}

	public void notifyRepositoryInfoChanged(ITask task) {
		for (ITaskChangeListener listener : new ArrayList<ITaskChangeListener>(changeListeners)) {
			try {
				listener.repositoryInfoChanged(task);
			} catch (Throwable t) {
				MylarStatusHandler.fail(t, "notification failed for: " + listener, false);
			}
		}
	}

	public void notifyListUpdated() {
		for (ITaskChangeListener listener : new ArrayList<ITaskChangeListener>(changeListeners)) {
			try {
				listener.taskListModified();
			} catch (Throwable t) {
				MylarStatusHandler.fail(t, "notification failed for: " + listener, false);
			}
		}
	}

	public void setTaskListFile(File f) {
		this.taskListFile = f;
	}

	public ITask getTaskForHandle(String handle, boolean lookInArchives) {
		if (handle == null)
			return null;
		return taskList.getTaskForHandle(handle, lookInArchives);
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

	/**
	 * Public for testing
	 */
	public Map<ITask, TaskActivityTimer> getTimerMap() {
		return timerMap;
	}

	public void markComplete(ITask task, boolean complete) {
		task.setCompleted(complete);
		for (ITaskChangeListener listener : new ArrayList<ITaskChangeListener>(changeListeners)) {
			listener.localInfoChanged(task); // to ensure comleted filter
			// notices
		}
	}

	/**
	 * For testing
	 */
	public void setTimerSleepInterval(int timerSleepInterval) {
		this.timerSleepInterval = timerSleepInterval;
	}

	public boolean isActiveThisWeek(ITask task) {
		for (DateRangeContainer container : dateRangeContainers) {
			if (container.isPresent()) {
				if (container.getChildren().contains(task)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * TODO: Need to migrate to use of this method for setting of reminders
	 */
	public void setReminder(ITask task, Date reminderDate) {
		task.setReminderDate(reminderDate);
		task.setReminded(false);
		if (reminderDate == null) {
			tasksWithReminders.remove(task);
		} else {
			tasksWithReminders.add(task);
		}
		parseFutureReminders();
		notifyLocalInfoChanged(task);
	}
}
