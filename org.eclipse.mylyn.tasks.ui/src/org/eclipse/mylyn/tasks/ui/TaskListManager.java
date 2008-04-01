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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
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
import org.eclipse.mylyn.tasks.core.ITaskActivityListener2;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.tasks.core.ITaskTimingListener;
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

	// TODO: Remove
	public static final String ARCHIVE_CATEGORY_DESCRIPTION = "Archive";

	@Deprecated
	public static final String[] ESTIMATE_TIMES = new String[] { "0 Hours", "1 Hours", "2 Hours", "3 Hours", "4 Hours",
			"5 Hours", "6 Hours", "7 Hours", "8 Hours", "9 Hours", "10 Hours" };

	//private ScheduledTaskContainer scheduledThisWeek;

	//private List<ScheduledTaskContainer> scheduleWeekDays = new ArrayList<ScheduledTaskContainer>();
//
//	private ScheduledTaskContainer scheduledNextWeek;
//
//	private ScheduledTaskContainer scheduledFuture;
//
//	private ScheduledTaskContainer scheduledPast;
//
//	private ScheduledTaskContainer scheduledPrevious;

	//private ArrayList<ScheduledTaskContainer> scheduleContainers = new ArrayList<ScheduledTaskContainer>();

	private final List<ITaskActivityListener> activityListeners = new ArrayList<ITaskActivityListener>();

	private final TaskListWriter taskListWriter;

	private File taskListFile;

	private TaskListSaveManager taskListSaveManager;

	private final TaskList taskList = new TaskList();

	private final TaskActivationHistory taskActivityHistory = new TaskActivationHistory();

	private boolean taskListInitialized = false;

	private final Timer timer;

	/**
	 * public for testing
	 * 
	 * @deprecated use TaskActivityManager.getStartTime()
	 */
	@Deprecated
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
		prepareOrphanContainers();

		taskListInitialized = true;
		return taskList;
	}

	private void prepareOrphanContainers() {
		for (TaskRepository repository : TasksUiPlugin.getRepositoryManager().getAllRepositories()) {
			if (!repository.getConnectorKind().equals(LocalRepositoryConnector.CONNECTOR_KIND)) {
				taskList.addOrphanContainer(new UnmatchedTaskContainer(repository.getConnectorKind(),
						repository.getUrl()));
			}
		}

//		taskList.addOrphanContainer(new OrphanedTasksContainer(LocalRepositoryConnector.CONNECTOR_KIND,
//				LocalRepositoryConnector.REPOSITORY_URL));
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
						StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
								"Could not move context file: " + file.getName(), e));
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
				prepareOrphanContainers();
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
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Could not read task list, consider restoring via view menu", t));
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
				StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
						"Task list save attempted before initialization"));
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not save task list", e));
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

	/**
	 * @API-3.0 this should be moved to TaskActivityManager
	 */
	public void addTimingListener(ITaskTimingListener listener) {
		TasksUiPlugin.getTaskActivityManager().addTimingListener(listener);
	}

	/**
	 * @API-3.0 this should be moved to TaskActivityManager
	 */
	public void removeTimingListener(ITaskTimingListener listener) {
		TasksUiPlugin.getTaskActivityManager().removeTimingListener(listener);
	}

	public void activateTask(AbstractTask task) {
		activateTask(task, true);
	}

	public void activateTask(AbstractTask task, boolean addToHistory) {
		deactivateAllTasks();

		// notify that a task is about to be activated
		for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(activityListeners)) {
			try {
				if (listener instanceof ITaskActivityListener2) {
					((ITaskActivityListener2) listener).preTaskActivated(task);
				}
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Task activity listener failed: "
						+ listener, t));
			}
		}

		try {
			taskList.setActive(task, true);
			if (addToHistory) {
				taskActivityHistory.addTask(task);
			}
			for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(activityListeners)) {
				try {
					listener.taskActivated(task);
				} catch (Throwable t) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
							"Task activity listener failed: " + listener, t));
				}
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not activate task", t));
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
			// notify that a task is about to be deactivated
			for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(activityListeners)) {
				try {
					if (listener instanceof ITaskActivityListener2) {
						((ITaskActivityListener2) listener).preTaskDeactivated(task);
					}
				} catch (Throwable t) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Notification failed for: "
							+ listener, t));
				}
			}

			taskList.setActive(task, false);
			for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(activityListeners)) {
				try {
					listener.taskDeactivated(task);
				} catch (Throwable t) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Notification failed for: "
							+ listener, t));
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

	/**
	 * public for testing TODO: Move to TaskActivityManager
	 */
	public void resetAndRollOver() {
		resetAndRollOver(new Date());
	}

	/**
	 * public for testing TODO: Move to TaskActivityManager
	 */
	public void resetAndRollOver(Date startDate) {
		TasksUiPlugin.getDefault().initTaskActivityManager();

		startTime = startDate;
		if (isTaskListInitialized()) {
			TaskActivityManager.getInstance().reloadTimingData(startDate);
			List<InteractionEvent> events = ContextCorePlugin.getContextManager()
					.getActivityMetaContext()
					.getInteractionHistory();
			for (InteractionEvent event : events) {
				TaskActivityManager.getInstance().parseInteractionEvent(event);
			}
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
				Calendar now = Calendar.getInstance();
				ScheduledTaskContainer thisWeek = TasksUiPlugin.getTaskActivityManager().getActivityThisWeek();
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

		TasksUiPlugin.getTaskActivityManager().scheduleNewTask(newTask);

		Object selectedObject = null;
		TaskListView view = TaskListView.getFromActivePerspective();
		if (view != null) {
			selectedObject = ((IStructuredSelection) view.getViewer().getSelection()).getFirstElement();
		}
		if (selectedObject instanceof TaskCategory) {
			taskList.addTask(newTask, (TaskCategory) selectedObject);
		} else if (selectedObject instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) selectedObject;

			AbstractTaskContainer container = TaskCategory.getParentTaskCategory(task);

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
			taskList.addTask(newTask, TasksUiPlugin.getTaskListManager().getTaskList().getDefaultCategory());
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
	@Deprecated
	public Set<AbstractTask> getScheduledForThisWeek() {
		Calendar startWeek = TaskActivityUtil.getStartOfCurrentWeek();
		Calendar endWeek = TaskActivityUtil.getEndOfCurrentWeek();
		return TaskActivityManager.getInstance().getScheduledTasks(startWeek, endWeek);
	}

	/**
	 * @deprecated use TaskActivityUtil.isScheduledAfterThisWeek
	 */
	@Deprecated
	public boolean isScheduledAfterThisWeek(AbstractTask task) {
		return TaskActivityManager.getInstance().isScheduledAfterThisWeek(task);
	}

	/**
	 * @deprecated use TaskActivityUtil.isScheduledForFuture
	 */
	@Deprecated
	public boolean isScheduledForLater(AbstractTask task) {
		return TaskActivityManager.getInstance().isScheduledForFuture(task);
	}

	/**
	 * @deprecated use TaskActivityUtil.isScheduledForThisWeek
	 */
	@Deprecated
	public boolean isScheduledForThisWeek(AbstractTask task) {
		return TaskActivityManager.getInstance().isScheduledForThisWeek(task);
	}

	/**
	 * @deprecated use TaskActivityManager.isScheduledForToday
	 */
	@Deprecated
	public boolean isScheduledForToday(AbstractTask task) {
		return TaskActivityManager.getInstance().isScheduledForToday(task);
	}

	/**
	 * @deprecated use TaskRepositoryManager.isOwnedBuyUser
	 */
	@Deprecated
	public boolean isOwnedByUser(AbstractTask task) {
		return TasksUiPlugin.getRepositoryManager().isOwnedByUser(task);
	}

	/**
	 * @deprecated use ActivityManager
	 */
	@Deprecated
	public void setScheduledFor(AbstractTask task, Date reminderDate) {
		TaskActivityManager.getInstance().setScheduledFor(task, reminderDate);
	}

	/**
	 * @deprecated use TaskActivityManager.setDueDate
	 */
	@Deprecated
	public void setDueDate(AbstractTask task, Date dueDate) {
		TaskActivityManager.getInstance().setDueDate(task, dueDate);
	}

	/**
	 * @deprecated use TaskActivityManager
	 * @return true if task due date != null and has past
	 */
	@Deprecated
	public boolean isOverdue(AbstractTask task) {
		return TaskActivityManager.getInstance().isOverdue(task);
	}

	/**
	 * @deprecated use TaskActivityManager.isActiveThisWeek
	 */
	@Deprecated
	public boolean isActiveThisWeek(AbstractTask task) {
		return TaskActivityManager.getInstance().isActiveThisWeek(task);
	}

	/**
	 * @deprecated use TaskActivityManager.isCompletedToday
	 * @return if a repository task, will only return true if the user is a
	 */
	@Deprecated
	public boolean isCompletedToday(AbstractTask task) {
		return TaskActivityManager.getInstance().isCompletedToday(task);
	}

	@Deprecated
	public void parseFutureReminders() {
		// no longer required
	}

	/**
	 * @deprecated use same method on TaskActivityManager
	 */
	@Deprecated
	public long getElapsedTime(AbstractTask task) {
		return TasksUiPlugin.getTaskActivityManager().getElapsedTime(task);
	}

	/**
	 * @deprecated use TaskActivityUtil.snapStartOfDay
	 */
	@Deprecated
	public void snapToStartOfDay(Calendar cal) {
		TaskActivityUtil.snapStartOfDay(cal);
	}

	/**
	 * @deprecated use TaskActivityUtil.snapStartOfHour
	 */
	@Deprecated
	public void snapToStartOfHour(Calendar cal) {
		TaskActivityUtil.snapStartOfHour(cal);
	}

	/**
	 * @deprecated use TaskActivityUtil.snapEndOfHour
	 */
	@Deprecated
	public void snapToEndOfHour(Calendar cal) {
		TaskActivityUtil.snapEndOfHour(cal);
	}

	/**
	 * @deprecated use TaskActivityUtil.snapEndOfDay
	 */
	@Deprecated
	public void snapToEndOfDay(Calendar cal) {
		TaskActivityUtil.snapEndOfDay(cal);
	}

	/**
	 * @deprecated use TaskActivityUtil.snapNextDay
	 */
	@Deprecated
	public void snapToNextDay(Calendar cal) {
		TaskActivityUtil.snapNextDay(cal);
	}

	/**
	 * @deprecated use TaskActivityUtil.snapStartOfWorkWeek
	 */
	@Deprecated
	public void snapToStartOfWeek(Calendar cal) {
		TaskActivityUtil.snapStartOfWorkWeek(cal);
	}

	/**
	 * @deprecated use TaskActivityUtil.snapEndOfWeek
	 */
	@Deprecated
	public void snapToEndOfWeek(Calendar cal) {
		TaskActivityUtil.snapEndOfWeek(cal);
	}

	/**
	 * @deprecated use TaskActivityUtil.snapForwardNumDays
	 */
	@Deprecated
	public Calendar setSecheduledIn(Calendar calendar, int days) {
		return TaskActivityUtil.snapForwardNumDays(calendar, days);
	}

	/**
	 * @deprecated use TaskActivityUtil.snapEndOfWorkDay
	 */
	@Deprecated
	public Calendar setScheduledEndOfDay(Calendar calendar) {
		return TaskActivityUtil.snapEndOfWorkDay(calendar);
	}

	/**
	 * @deprecated use TaskActivityUtil.snapNextWorkWeek
	 */
	@Deprecated
	public void setScheduledNextWeek(Calendar calendar) {
		TaskActivityUtil.snapNextWorkWeek(calendar);
	}

	/**
	 * @deprecated use TaskActivityManager.getDateRanges()
	 */
	@Deprecated
	public List<ScheduledTaskContainer> getDateRanges() {
		return TasksUiPlugin.getTaskActivityManager().getDateRanges();
	}

	/**
	 * @deprecated use TaskActivityManager.getActivityWeekDays()
	 */
	@Deprecated
	public List<ScheduledTaskContainer> getActivityWeekDays() {
		return TasksUiPlugin.getTaskActivityManager().getActivityWeekDays();
	}

	/**
	 * @deprecated use TaskActivityManager.isWeekDay()
	 */
	@Deprecated
	public boolean isWeekDay(ScheduledTaskContainer dateRangeTaskContainer) {
		return TasksUiPlugin.getTaskActivityManager().isWeekDay(dateRangeTaskContainer);
	}

	/**
	 * public for testing
	 * 
	 * @deprecated
	 */
	@Deprecated
	public ScheduledTaskContainer getActivityThisWeek() {
		return TasksUiPlugin.getTaskActivityManager().getActivityThisWeek();
	}

	/**
	 * public for testing
	 * 
	 * @deprecated
	 */
	@Deprecated
	public ScheduledTaskContainer getActivityPast() {
		return TasksUiPlugin.getTaskActivityManager().getActivityPast();
	}

	/**
	 * public for testing
	 * 
	 * @deprecated
	 */
	@Deprecated
	public ScheduledTaskContainer getActivityFuture() {
		return TasksUiPlugin.getTaskActivityManager().getActivityFuture();
	}

	/**
	 * public for testing
	 * 
	 * @deprecated
	 */
	@Deprecated
	public ScheduledTaskContainer getActivityNextWeek() {
		return TasksUiPlugin.getTaskActivityManager().getActivityNextWeek();
	}

	/**
	 * public for testing
	 * 
	 * @deprecated
	 */
	@Deprecated
	public ScheduledTaskContainer getActivityPrevious() {
		return TasksUiPlugin.getTaskActivityManager().getActivityPrevious();
	}

	/**
	 * Use TaskActivityManager.getStartHour()
	 * 
	 * @deprecated
	 */
	@Deprecated
	public int getStartHour() {
		return TasksUiPlugin.getDefault().getPreferenceStore().getInt(TasksUiPreferenceConstants.PLANNING_ENDHOUR);
	}

	/**
	 * Use TaskActivityManager.scheduleNewTask()
	 * 
	 * @deprecated
	 */
	@Deprecated
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
		TasksUiPlugin.getTaskActivityManager().setScheduledFor(newTask, newTaskSchedule.getTime());
	}
}
