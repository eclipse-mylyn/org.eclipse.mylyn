/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.WorkspaceAwareContextStore;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskListSaveManager;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskListWriter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskActivationHistory;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;
import org.eclipse.swt.widgets.Display;

/**
 * Provides facilities for using and managing the Task List and task activity information.
 * 
 * TODO: pull task activity management out into new TaskActivityManager NOTE: likely to change for 3.0
 * 
 * @author Mik Kersten
 * @author Rob Elves (task activity)
 * @author Jevgeni Holodkov (insertQueries)
 * @since 2.0
 */
public class TaskListManager implements IPropertyChangeListener {

	private static final long SECOND = 1000;

	private static final long MINUTE = 60 * SECOND;

	private static final long ROLLOVER_DELAY = 30 * MINUTE;

	private static final int NUM_WEEKS_PREVIOUS_START = -1;

	private static final int NUM_WEEKS_PREVIOUS_END = -1;

	private static final int NUM_WEEKS_NEXT = 1;

	private static final int NUM_WEEKS_FUTURE_START = 2;

	private static final int NUM_WEEKS_PAST_END = -2;

	// TODO: Remove
	public static final String ARCHIVE_CATEGORY_DESCRIPTION = "Archive";

	private static final String DESCRIPTION_THIS_WEEK = "This Week";

	private static final String DESCRIPTION_PREVIOUS_WEEK = "Previous Week";

	private static final String DESCRIPTION_NEXT_WEEK = "Next Week";

	private static final String DESCRIPTION_FUTURE = "Future";

	private static final String DESCRIPTION_PAST = "Past";

	public static final String[] ESTIMATE_TIMES = new String[] { "0 Hours", "1 Hours", "2 Hours", "3 Hours", "4 Hours",
			"5 Hours", "6 Hours", "7 Hours", "8 Hours", "9 Hours", "10 Hours" };

	private ScheduledTaskContainer scheduledThisWeek;

	private List<ScheduledTaskContainer> scheduleWeekDays = new ArrayList<ScheduledTaskContainer>();

	private ScheduledTaskContainer scheduledNextWeek;

	private ScheduledTaskContainer scheduledFuture;

	private ScheduledTaskContainer scheduledPast;

	private ScheduledTaskContainer scheduledPrevious;

	private ArrayList<ScheduledTaskContainer> scheduleContainers = new ArrayList<ScheduledTaskContainer>();

	private List<ITaskActivityListener> activityListeners = new ArrayList<ITaskActivityListener>();

	private TaskListWriter taskListWriter;

	private File taskListFile;

	private TaskListSaveManager taskListSaveManager;

	// TODO: guard against overwriting the single instance?
	private TaskList taskList = new TaskList();

	private TaskActivationHistory taskActivityHistory = new TaskActivationHistory();

	private boolean taskListInitialized = false;

	private Timer timer;

	/** public for testing */
	public Date startTime = new Date();

	private final ITaskListChangeListener CHANGE_LISTENER = new ITaskListChangeListener() {

		public void containersChanged(Set<TaskContainerDelta> containers) {
			for (TaskContainerDelta taskContainerDelta : containers) {
				if (taskContainerDelta.getContainer() instanceof AbstractTask) {
					switch (taskContainerDelta.getKind()) {
					case REMOVED:
						TaskListManager.this.resetAndRollOver();
						return;
					}
				}
			}
		}
	};

	public TaskListManager(TaskListWriter taskListWriter, File file) {
		this.taskListFile = file;
		this.taskListWriter = taskListWriter;
		timer = new Timer();
		timer.schedule(new RolloverCheck(), ROLLOVER_DELAY, ROLLOVER_DELAY);
		taskList.addChangeListener(CHANGE_LISTENER);
	}

	public void init() {
	}

	public void dispose() {
	}

	public TaskList resetTaskList() {
		resetAndRollOver();
		taskList.reset();
		taskListInitialized = true;
		return taskList;
	}

	public void refactorRepositoryUrl(String oldUrl, String newUrl) {
		if (oldUrl == null || newUrl == null || oldUrl.equals(newUrl)) {
			return;
		}
		List<AbstractTask> activeTasks = taskList.getActiveTasks();
		for (AbstractTask task : new ArrayList<AbstractTask>(activeTasks)) {
			deactivateTask(task);
		}
		refactorOfflineHandles(oldUrl, newUrl);
		taskList.refactorRepositoryUrl(oldUrl, newUrl);
		refactorMetaContextHandles(oldUrl, newUrl);

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
						StatusHandler.fail(e, "Could not move context file: " + file.getName(), false);
					}
				}
			}
		}

		saveTaskList();
	}

	private void refactorMetaContextHandles(String oldRepositoryUrl, String newRepositoryUrl) {
		InteractionContext metaContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		ContextCorePlugin.getContextManager().resetActivityHistory();
		InteractionContext newMetaContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		for (InteractionEvent event : metaContext.getInteractionHistory()) {
			if (event.getStructureHandle() != null) {
				String storedUrl = RepositoryTaskHandleUtil.getRepositoryUrl(event.getStructureHandle());
				if (storedUrl != null) {
					if (oldRepositoryUrl.equals(storedUrl)) {
						String taskId = RepositoryTaskHandleUtil.getTaskId(event.getStructureHandle());
						if (taskId != null) {
							String newHandle = RepositoryTaskHandleUtil.getHandle(newRepositoryUrl, taskId);
							event = new InteractionEvent(event.getKind(), event.getStructureKind(), newHandle,
									event.getOriginId(), event.getNavigation(), event.getDelta(),
									event.getInterestContribution(), event.getDate(), event.getEndDate());
						}
					}
				}
			}
			newMetaContext.parseEvent(event);
		}
		ContextCorePlugin.getContextManager().saveActivityContext();
		initActivityHistory();
	}

	private void refactorOfflineHandles(String oldRepositoryUrl, String newRepositoryUrl) {
		TaskDataManager taskDataManager = TasksUiPlugin.getTaskDataManager();
		for (AbstractTask task : taskList.getAllTasks()) {
			if (task != null) {
				AbstractTask repositoryTask = task;
				if (repositoryTask.getRepositoryUrl().equals(oldRepositoryUrl)) {
					RepositoryTaskData newTaskData = taskDataManager.getNewTaskData(repositoryTask.getRepositoryUrl(),
							repositoryTask.getTaskId());
					RepositoryTaskData oldTaskData = taskDataManager.getOldTaskData(repositoryTask.getRepositoryUrl(),
							repositoryTask.getTaskId());
					Set<RepositoryTaskAttribute> edits = taskDataManager.getEdits(repositoryTask.getRepositoryUrl(),
							repositoryTask.getTaskId());
					taskDataManager.remove(repositoryTask.getRepositoryUrl(), repositoryTask.getTaskId());

					if (newTaskData != null) {
						newTaskData.setRepositoryURL(newRepositoryUrl);
						taskDataManager.setNewTaskData(newTaskData);
					}
					if (oldTaskData != null) {
						oldTaskData.setRepositoryURL(newRepositoryUrl);
						taskDataManager.setOldTaskData(oldTaskData);
					}
					if (!edits.isEmpty()) {
						taskDataManager.saveEdits(newRepositoryUrl, repositoryTask.getTaskId(), edits);
					}
				}
			}
		}
		TasksUiPlugin.getTaskDataManager().saveNow();
	}

	public boolean readExistingOrCreateNewList() {
		try {
			if (taskListFile.exists()) {
				taskListWriter.readTaskList(taskList, taskListFile, TasksUiPlugin.getTaskDataManager());
			} else {
				resetTaskList();
			}
			taskListInitialized = true;
			resetAndRollOver();
			for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(activityListeners)) {
				listener.taskListRead();
			}
		} catch (Throwable t) {
			StatusHandler.fail(t, "Could not read task list, consider restoring via view menu", true);
			return false;
		}
		return true;
	}

	/**
	 * TODO: Move activation history to activity manager
	 * 
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
			if (taskListInitialized && taskListSaveManager != null) {
				taskListSaveManager.saveTaskList(true, false);
			} else {
				StatusHandler.log("task list save attempted before initialization", this);
			}
		} catch (Exception e) {
			StatusHandler.fail(e, "Could not save task list", true);
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

	public void activateTask(AbstractTask task) {
		activateTask(task, true);
	}

	public void activateTask(AbstractTask task, boolean addToHistory) {
		deactivateAllTasks();
		try {
			taskList.setActive(task, true);
			if (addToHistory) {
				taskActivityHistory.addTask(task);
			}
			for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(activityListeners)) {
				try {
					listener.taskActivated(task);
				} catch (Throwable t) {
					StatusHandler.fail(t, "task activity listener failed: " + listener, false);
				}
			}
		} catch (Throwable t) {
			StatusHandler.fail(t, "could not activate task", false);
		}
	}

	public void deactivateAllTasks() {
		List<AbstractTask> activeTasks = taskList.getActiveTasks();
		for (AbstractTask task : activeTasks) {
			deactivateTask(task);
		}
	}

	public void deactivateTask(AbstractTask task) {
		if (task == null) {
			return;
		}

		if (task.isActive()) {
			taskList.setActive(task, false);
			for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(activityListeners)) {
				try {
					listener.taskDeactivated(task);
				} catch (Throwable t) {
					StatusHandler.fail(t, "notification failed for: " + listener, false);
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

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(TasksUiPreferenceConstants.PLANNING_STARTHOUR)
				|| event.getProperty().equals(TasksUiPreferenceConstants.PLANNING_ENDHOUR)) {
			// event.getProperty().equals(TaskListPreferenceConstants.PLANNING_STARTDAY)
			// scheduledStartHour =
			// TasksUiPlugin.getDefault().getPreferenceStore().getInt(
			// TaskListPreferenceConstants.PLANNING_STARTHOUR);
			TaskActivityManager.getInstance()
					.setEndHour(
							TasksUiPlugin.getDefault().getPreferenceStore().getInt(
									TasksUiPreferenceConstants.PLANNING_ENDHOUR));
		}
	}

	/** public for testing */
	public void resetAndRollOver() {
		resetAndRollOver(new Date());
	}

	/** public for testing */
	public void resetAndRollOver(Date startDate) {
		if (!TaskActivityManager.getInstance().isInitialized()) {
			TaskActivityManager.getInstance().init(TasksUiPlugin.getRepositoryManager(), taskList);
			TaskActivityManager.getInstance()
					.setEndHour(
							TasksUiPlugin.getDefault().getPreferenceStore().getInt(
									TasksUiPreferenceConstants.PLANNING_ENDHOUR));
		}
		startTime = startDate;
		setupCalendarRanges();
		if (isTaskListInitialized()) {
			TaskActivityManager.getInstance().reloadTimingData();
		}
		for (ITaskActivityListener listener : activityListeners) {
			listener.activityChanged(null);
		}
	}

	private class RolloverCheck extends TimerTask {

		@Override
		public void run() {
			if (!Platform.isRunning() || ContextCorePlugin.getDefault() == null) {
				return;
			} else {
				Calendar now = GregorianCalendar.getInstance();
				ScheduledTaskContainer thisWeek = getActivityThisWeek();
				if (!thisWeek.includes(now)) {
					resetAndRollOver();
				}
			}
		}
	}

	public TaskActivationHistory getTaskActivationHistory() {
		return taskActivityHistory;
	}

	/**
	 * @param element
	 *            tasklist element to retrieve a task for currently will work for (ITask, AbstractQueryHit)
	 * @param force -
	 *            if a query hit is passed you can either force construction of the task or not (if not and no task,
	 *            null is returned) TODO: Move into TaskList?
	 */
	public AbstractTask getTaskForElement(AbstractTaskContainer element, boolean force) {
		AbstractTask task = null;
		if (element instanceof AbstractTask) {
			task = (AbstractTask) element;
		}
		return task;
	}

	protected void setTaskListSaveManager(TaskListSaveManager taskListSaveManager) {
		this.taskListSaveManager = taskListSaveManager;
		this.taskList.addChangeListener(taskListSaveManager);
	}

	public List<ScheduledTaskContainer> getActivityWeekDays() {
		return scheduleWeekDays;
	}

	public boolean isWeekDay(ScheduledTaskContainer dateRangeTaskContainer) {
		return scheduleWeekDays.contains(dateRangeTaskContainer);
	}

	public int getStartHour() {
		return TasksUiPlugin.getDefault().getPreferenceStore().getInt(TasksUiPreferenceConstants.PLANNING_ENDHOUR);
	}

	public static void scheduleNewTask(AbstractTask newTask) {
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
	 * 
	 * @param summary
	 *            if null DEFAULT_SUMMARY (New Task) used.
	 */
	public LocalTask createNewLocalTask(String summary) {
		if (summary == null) {
			summary = LocalRepositoryConnector.DEFAULT_SUMMARY;
		}
		LocalTask newTask = new LocalTask("" + taskList.getNextLocalTaskId(), summary);
		newTask.setPriority(PriorityLevel.P3.toString());

		scheduleNewTask(newTask);

		Object selectedObject = null;
		TaskListView view = TaskListView.getFromActivePerspective();
		if (view != null) {
			selectedObject = ((IStructuredSelection) view.getViewer().getSelection()).getFirstElement();
		}
		if (selectedObject instanceof TaskCategory) {
			taskList.addTask(newTask, (TaskCategory) selectedObject);
		} else if (selectedObject instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) selectedObject;

			AbstractTaskContainer container = null;
			if (task.getChildren().size() > 0 && task.getParentContainers().iterator().hasNext()) {
				container = task.getParentContainers().iterator().next();
			}
			if (container instanceof TaskCategory) {
				taskList.addTask(newTask, container);
			} else if (view != null && view.getDrilledIntoCategory() instanceof TaskCategory) {
				taskList.addTask(newTask, view.getDrilledIntoCategory());
			} else {
				taskList.addTask(newTask, TasksUiPlugin.getTaskListManager().getTaskList().getDefaultCategory());
			}
		} else if (view != null && view.getDrilledIntoCategory() instanceof TaskCategory) {
			taskList.addTask(newTask, view.getDrilledIntoCategory());
		} else {
			if (view != null && view.getDrilledIntoCategory() != null) {
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), ITasksUiConstants.TITLE_DIALOG,
						"The new task has been added to the root of the list, since tasks can not be added to a query.");
			}
			taskList.addTask(newTask, taskList.getDefaultCategory());
		}
		return newTask;
	}

	/**
	 * Imports Queries to the TaskList and synchronize them with the repository. If the imported query have the name
	 * that overlaps with the existing one, the the suffix [x] is added, where x is a number starting from 1.
	 * 
	 * @param queries
	 *            to insert
	 * @return the list queries, which were not inserted since because the related repository was not found.
	 */
	public List<AbstractRepositoryQuery> insertQueries(List<AbstractRepositoryQuery> queries) {
		List<AbstractRepositoryQuery> badQueries = new ArrayList<AbstractRepositoryQuery>();

		for (AbstractRepositoryQuery query : queries) {

			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(query.getRepositoryKind(),
					query.getRepositoryUrl());
			if (repository == null) {
				badQueries.add(query);
				continue;
			}

			String handle = resolveIdentifiersConflict(query);
			query.setHandleIdentifier(handle);

			// add query
			TasksUiPlugin.getTaskListManager().getTaskList().addQuery(query);

			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
					repository.getConnectorKind());
			if (connector != null) {
				TasksUiPlugin.getSynchronizationManager().synchronize(connector, query, null, true);
			}

		}

		return badQueries;
	}

	/**
	 * Utility method that checks, if there is already a query with the same identifier.
	 * 
	 * @param query
	 * @return a handle, that is not in conflict with any existed one in the system. If there were no conflict in the
	 *         beginning, then the query's own identifier is returned. If there were, then the suffix [x] is applied the
	 *         query's identifier, where x is a number.
	 * @since 2.1
	 */
	public String resolveIdentifiersConflict(AbstractRepositoryQuery query) {
		String patternStr = "\\[(\\d+)\\]$"; // all string that end with [x], where x is a number
		Pattern pattern = Pattern.compile(patternStr);

		// resolve name conflict
		Set<AbstractRepositoryQuery> existingQueries = getTaskList().getQueries();
		Map<String, AbstractRepositoryQuery> queryMap = new HashMap<String, AbstractRepositoryQuery>();
		for (AbstractRepositoryQuery existingQuery : existingQueries) {
			queryMap.put(existingQuery.getHandleIdentifier(), existingQuery);
		}

		// suggest a new handle if needed
		String handle = query.getHandleIdentifier();

		while (queryMap.get(handle) != null) {
			Matcher matcher = pattern.matcher(handle);
			boolean matchFound = matcher.find();
			if (matchFound) {
				// increment index
				int index = Integer.parseInt(matcher.group(1));
				index++;
				handle = matcher.replaceAll("[" + index + "]");
			} else {
				handle += "[1]";
			}
		}

		return handle;
	}

	/** deprecated post 2.0 *********************************************** */

	/**
	 * @deprecated use TaskActivityManager.getScheduledTasks
	 */
	public Set<AbstractTask> getScheduledForThisWeek() {
		Calendar startWeek = TaskActivityUtil.getStartOfCurrentWeek();
		Calendar endWeek = TaskActivityUtil.getEndOfCurrentWeek();
		return TaskActivityManager.getInstance().getScheduledTasks(startWeek, endWeek);
	}

	/**
	 * @deprecated use TaskActivityUtil.isScheduledAfterThisWeek
	 */
	public boolean isScheduledAfterThisWeek(AbstractTask task) {
		return TaskActivityManager.getInstance().isScheduledAfterThisWeek(task);
	}

	/**
	 * @deprecated use TaskActivityUtil.isScheduledForFuture
	 */
	public boolean isScheduledForLater(AbstractTask task) {
		return TaskActivityManager.getInstance().isScheduledForFuture(task);
	}

	/**
	 * @deprecated use TaskActivityUtil.isScheduledForThisWeek
	 */
	public boolean isScheduledForThisWeek(AbstractTask task) {
		return TaskActivityManager.getInstance().isScheduledForThisWeek(task);
	}

	/**
	 * @deprecated use TaskActivityManager.isScheduledForToday
	 */
	public boolean isScheduledForToday(AbstractTask task) {
		return TaskActivityManager.getInstance().isScheduledForToday(task);
	}

	/**
	 * @deprecated use TaskRepositoryManager.isOwnedBuyUser
	 */
	public boolean isOwnedByUser(AbstractTask task) {
		return TasksUiPlugin.getRepositoryManager().isOwnedByUser(task);
	}

	/**
	 * @deprecated use ActivityManager
	 */
	public void setScheduledFor(AbstractTask task, Date reminderDate) {
		TaskActivityManager.getInstance().setScheduledFor(task, reminderDate);
	}

	/**
	 * @deprecated use TaskActivityManager.setDueDate
	 */
	public void setDueDate(AbstractTask task, Date dueDate) {
		TaskActivityManager.getInstance().setDueDate(task, dueDate);
	}

	/**
	 * @deprecated use TaskActivityManager
	 * @return true if task due date != null and has past
	 */
	public boolean isOverdue(AbstractTask task) {
		return TaskActivityManager.getInstance().isOverdue(task);
	}

	/**
	 * @deprecated use TaskActivityManager.isActiveThisWeek
	 */
	public boolean isActiveThisWeek(AbstractTask task) {
		return TaskActivityManager.getInstance().isActiveThisWeek(task);
	}

	/**
	 * @deprecated use TaskActivityManager.isCompletedToday
	 * @return if a repository task, will only return true if the user is a
	 */
	public boolean isCompletedToday(AbstractTask task) {
		return TaskActivityManager.getInstance().isCompletedToday(task);
	}

	@Deprecated
	public void parseFutureReminders() {
		// no lonhger required
	}

	/** public for testing * */
	public ScheduledTaskContainer getActivityThisWeek() {
		return scheduledThisWeek;
	}

	/** public for testing * */
	public ScheduledTaskContainer getActivityPast() {
		return scheduledPast;
	}

	/** public for testing * */
	public ScheduledTaskContainer getActivityFuture() {
		return scheduledFuture;
	}

	/** public for testing * */
	public ScheduledTaskContainer getActivityNextWeek() {
		return scheduledNextWeek;
	}

	/** public for testing * */
	public ScheduledTaskContainer getActivityPrevious() {
		return scheduledPrevious;
	}

	/**
	 * @deprecated use same method on TaskActivityManager
	 */
	public long getElapsedTime(AbstractTask task) {
		return TasksUiPlugin.getTaskActivityManager().getElapsedTime(task);
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
		snapToEndOfWeek(pastEnd);
		scheduledPast = new ScheduledTaskContainer(TasksUiPlugin.getTaskActivityManager(), pastStart.getTime(),
				pastEnd.getTime(), DESCRIPTION_PAST);
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
			ScheduledTaskContainer day = new ScheduledTaskContainer(TasksUiPlugin.getTaskActivityManager(), dayStart,
					dayEnd, summary);
			scheduleWeekDays.add(day);
			scheduleContainers.add(day);
		}

		GregorianCalendar currentBegin = new GregorianCalendar();
		currentBegin.setFirstDayOfWeek(startDay);
		currentBegin.setTime(startTime);
		snapToStartOfWeek(currentBegin);
		GregorianCalendar currentEnd = new GregorianCalendar();
		currentEnd.setFirstDayOfWeek(startDay);
		currentEnd.setTime(startTime);
		snapToEndOfWeek(currentEnd);
		scheduledThisWeek = new ScheduledTaskContainer(TasksUiPlugin.getTaskActivityManager(), currentBegin,
				currentEnd, DESCRIPTION_THIS_WEEK);
		// dateRangeContainers.add(activityThisWeek);

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
		scheduledNextWeek = new ScheduledTaskContainer(TasksUiPlugin.getTaskActivityManager(), nextStart.getTime(),
				nextEnd.getTime(), DESCRIPTION_NEXT_WEEK);
		scheduleContainers.add(scheduledNextWeek);

		GregorianCalendar futureStart = new GregorianCalendar();
		futureStart.setFirstDayOfWeek(startDay);
		futureStart.setTime(startTime);
		futureStart.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_FUTURE_START);
		snapToStartOfWeek(futureStart);
		GregorianCalendar futureEnd = new GregorianCalendar();
		futureEnd.setFirstDayOfWeek(startDay);
		futureEnd.setTime(startTime);
		futureEnd.add(Calendar.YEAR, 1);
		snapToEndOfWeek(futureEnd);
		scheduledFuture = new ScheduledTaskContainer(TasksUiPlugin.getTaskActivityManager(), futureStart.getTime(),
				futureEnd.getTime(), DESCRIPTION_FUTURE);
		scheduleContainers.add(scheduledFuture);

		GregorianCalendar previousStart = new GregorianCalendar();
		previousStart.setFirstDayOfWeek(startDay);
		previousStart.setTime(startTime);
		previousStart.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_PREVIOUS_START);
		snapToStartOfWeek(previousStart);
		GregorianCalendar previousEnd = new GregorianCalendar();
		previousEnd.setFirstDayOfWeek(startDay);
		previousEnd.setTime(startTime);
		previousEnd.add(Calendar.WEEK_OF_YEAR, NUM_WEEKS_PREVIOUS_END);
		snapToEndOfWeek(previousEnd);
		scheduledPrevious = new ScheduledTaskContainer(TasksUiPlugin.getTaskActivityManager(), previousStart.getTime(),
				previousEnd.getTime(), DESCRIPTION_PREVIOUS_WEEK);
		scheduleContainers.add(scheduledPrevious);

	}

	/**
	 * @deprecated use TaskActivityUtil.snapStartOfDay
	 */
	public void snapToStartOfDay(Calendar cal) {
		TaskActivityUtil.snapStartOfDay(cal);
	}

	/**
	 * @deprecated use TaskActivityUtil.snapStartOfHour
	 */
	public void snapToStartOfHour(Calendar cal) {
		TaskActivityUtil.snapStartOfHour(cal);
	}

	/**
	 * @deprecated use TaskActivityUtil.snapEndOfHour
	 */
	public void snapToEndOfHour(Calendar cal) {
		TaskActivityUtil.snapEndOfHour(cal);
	}

	/**
	 * @deprecated use TaskActivityUtil.snapEndOfDay
	 */
	public void snapToEndOfDay(Calendar cal) {
		TaskActivityUtil.snapEndOfDay(cal);
	}

	/**
	 * @deprecated use TaskActivityUtil.snapNextDay
	 */
	public void snapToNextDay(Calendar cal) {
		TaskActivityUtil.snapNextDay(cal);
	}

	/**
	 * @deprecated use TaskActivityUtil.snapStartOfWorkWeek
	 */
	public void snapToStartOfWeek(Calendar cal) {
		TaskActivityUtil.snapStartOfWorkWeek(cal);
	}

	/**
	 * @deprecated use TaskActivityUtil.snapEndOfWeek
	 */
	public void snapToEndOfWeek(Calendar cal) {
		TaskActivityUtil.snapEndOfWeek(cal);
	}

	/**
	 * @deprecated use TaskActivityUtil.snapForwardNumDays
	 */
	public Calendar setSecheduledIn(Calendar calendar, int days) {
		return TaskActivityUtil.snapForwardNumDays(calendar, days);
	}

	/**
	 * @deprecated use TaskActivityUtil.snapEndOfWorkDay
	 */
	public Calendar setScheduledEndOfDay(Calendar calendar) {
		return TaskActivityUtil.snapEndOfWorkDay(calendar);
	}

	/**
	 * @deprecated use TaskActivityUtil.snapNextWorkWeek
	 */
	public void setScheduledNextWeek(Calendar calendar) {
		TaskActivityUtil.snapNextWorkWeek(calendar);
	}

	public List<ScheduledTaskContainer> getDateRanges() {
		return scheduleContainers;
	}

}
