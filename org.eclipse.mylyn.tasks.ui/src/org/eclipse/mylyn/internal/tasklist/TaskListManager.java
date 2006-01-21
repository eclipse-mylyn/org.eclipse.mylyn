/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
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
package org.eclipse.mylar.internal.tasklist;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.mylar.core.util.TimerThread;
import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskActivityListener;
import org.eclipse.mylar.tasklist.ITaskCategory;
import org.eclipse.mylar.tasklist.ITaskQuery;

/**
 * @author Mik Kersten
 */
public class TaskListManager {
	
	public static final String ARCHIVE_CATEGORY_DESCRIPTION = "Archived Reports";
	
	private Map<ITask, TaskActivityTimer> timerMap = new HashMap<ITask, TaskActivityTimer>();

	private List<ITaskActivityListener> listeners = new ArrayList<ITaskActivityListener>();
	
	private TaskListWriter taskListWriter;
	
	private File taskListFile;

	private TaskList taskList = new TaskList();

	private boolean taskListInitialized = false;
	
	private int nextTaskId;

	private int timerSleepInterval = TimerThread.DEFAULT_SLEEP_INTERVAL;
	
	public TaskListManager(TaskListWriter taskListWriter, File file, int startId) { 
		this.taskListFile = file;
		this.taskListWriter = taskListWriter;
		this.nextTaskId = startId;
	}
	
	public TaskList createNewTaskList() {
		taskList = new TaskList();
		taskListInitialized = true;
		return taskList;
	}

	/**
	 * Exposed for unit testing
	 * @return unmodifiable collection of ITaskActivityListeners
	 */
	public List<ITaskActivityListener> getListeners() {
		return Collections.unmodifiableList(listeners);
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
			taskListInitialized = true;
			for (ITaskActivityListener listener : listeners) listener.tasklistRead();
			// only activate the first task to avoid confusion of mutliple active tasks on startup
			List<ITask> activeTasks = taskList.getActiveTasks();
			if (activeTasks.size() > 0) {
				activateTask(activeTasks.get(0));
			}
		} catch (Exception e) {
			MylarStatusHandler.log(e, "Could not read task list");
			return false;
		}
		return true;
	}

	public void saveTaskList() {
		try {
			if (taskListInitialized) {
				taskListWriter.writeTaskList(taskList, taskListFile);
				MylarPlugin.getDefault().getPreferenceStore().setValue(MylarTaskListPrefConstants.TASK_ID, nextTaskId);
			} else {
				MylarStatusHandler.log("task list save attempted before initialization", this);
			}
		} catch (Exception e) {
			Thread.dumpStack();
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
			((TaskCategory)task.getCategory()).removeTask(task);
		}
		task.setCategory(null);
		if (!taskList.getRootTasks().contains(task)) taskList.addRootTask(task);
		for (ITaskActivityListener listener : listeners) listener.taskListModified();
	}

	public void moveToCategory(TaskCategory category, ITask task) {
		taskList.removeFromRoot(task);
		if (task.getCategory() instanceof TaskCategory) {
			((TaskCategory)task.getCategory()).removeTask(task);
		}
		if (!category.getChildren().contains(task)) {
			category.addTask(task);
		}
		task.setCategory(category);
		for (ITaskActivityListener listener : listeners) listener.taskListModified();
	}

	public void addCategory(ITaskCategory cat) {
		taskList.addCategory(cat);
		for (ITaskActivityListener listener : listeners) listener.taskListModified();
	}
	
	public void removeFromCategory(TaskCategory category, ITask task) {
		if (!category.isArchive()) {
			category.removeTask(task);
			task.setCategory(null);
		}
		for (ITaskActivityListener listener : listeners) listener.taskListModified();
	}
	
	public void removeFromRoot(ITask task) {
		taskList.removeFromRoot(task);
		for (ITaskActivityListener listener : listeners) listener.taskListModified();
	}
	
	public void addQuery(ITaskQuery cat) {
		taskList.addQuery(cat);
		for (ITaskActivityListener listener : listeners) listener.taskListModified();
	}

	public void deleteTask(ITask task) {
		TaskActivityTimer taskTimer = timerMap.remove(task);
		if (taskTimer != null) taskTimer.stopTimer();
		taskList.setActive(task, false);
		taskList.deleteTask(task);
		for (ITaskActivityListener listener : listeners) listener.taskListModified();
	}

	public void deleteCategory(ITaskCategory cat) {
		taskList.deleteCategory(cat);
		for (ITaskActivityListener listener : listeners) listener.taskListModified();
	}

	public void deleteQuery(ITaskQuery query) {
		taskList.deleteQuery(query);
		for (ITaskActivityListener listener : listeners) listener.taskListModified();
	}

	public void addListener(ITaskActivityListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ITaskActivityListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Deactivates previously active tasks if not in multiple active mode.
	 * @param task
	 */
	public void activateTask(ITask task) {
		if (!MylarTaskListPlugin.getDefault().isMultipleActiveTasksMode()) {
			for (ITask activeTask : new ArrayList<ITask>(taskList.getActiveTasks())) {
				deactivateTask(activeTask);
			}
		}
		taskList.setActive(task, true);
		int timeout = MylarPlugin.getContextManager().getInactivityTimeout();
		TaskActivityTimer activityTimer = new TaskActivityTimer(task, timeout, timerSleepInterval);
		activityTimer.startTimer();
		timerMap.put(task, activityTimer);
		for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(listeners)) {
			listener.taskActivated(task);
		}
	}

	public void deactivateTask(ITask task) {
		TaskActivityTimer taskTimer = timerMap.remove(task);
		if (taskTimer != null) taskTimer.stopTimer();
		taskList.setActive(task, false); 
		for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(listeners)) {
			listener.taskDeactivated(task);
		}
	}

	/**
	 * TODO: refactor into task deltas?
	 */
	public void notifyTaskChanged(ITask task) {
		for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(listeners)) {
			listener.taskChanged(task);
		}
	}
	
	public void notifyListUpdated() {
		for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(listeners)) {
			listener.taskListModified();
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

	/**
	 * Use to obtain the QueryHit object associated with a particular
	 * task handle  if it exists. 
	 * @param handle handle of task 
	 * @return IQueryHit corresponding to the first hit found in all queries
	 */
	public IQueryHit getQueryHitForHandle(String handle) {
		if (handle == null)
			return null;
		return taskList.getQueryHitForHandle(handle);
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
		for (ITaskActivityListener listener : new ArrayList<ITaskActivityListener>(listeners)) {			
			listener.taskChanged(task); // to ensure comleted filter notices
		}
	}

	/**
	 * For testing
	 */
	public void setTimerSleepInterval(int timerSleepInterval) {
		this.timerSleepInterval = timerSleepInterval;
	}
}
