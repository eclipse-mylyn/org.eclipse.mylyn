/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.externalization.TaskListExternalizationParticipant;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskListElementImporter;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * Provides facilities for using and managing the Task List and task activity information.
 * 
 * @author Mik Kersten
 * @author Rob Elves (task activity)
 * @author Jevgeni Holodkov (insertQueries)
 * @since 3.0
 */
@Deprecated
public class TaskListManager {

	private final TaskListElementImporter importer;

	private final TaskList taskList;

	private final TaskListExternalizationParticipant participant;

	public TaskListManager(TaskList taskList, TaskListExternalizationParticipant participant,
			TaskListElementImporter importer) {
		this.taskList = taskList;
		this.importer = importer;
		this.participant = participant;
	}

	/**
	 * @deprecated moved to TasksUiPlugin
	 */
	@Deprecated
	private void prepareOrphanContainers() {
		for (TaskRepository repository : TasksUi.getRepositoryManager().getAllRepositories()) {
			if (!repository.getConnectorKind().equals(LocalRepositoryConnector.CONNECTOR_KIND)) {
				taskList.addUnmatchedContainer(new UnmatchedTaskContainer(repository.getConnectorKind(),
						repository.getRepositoryUrl()));
			}
		}
	}

	@Deprecated
	public TaskListElementImporter getTaskListWriter() {
		return importer;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public ITaskList resetTaskList() {
		participant.resetTaskList();
		return taskList;
	}

	/**
	 * @deprecated use TasksUiPlugin.reloadDataDirectory()
	 */
	@Deprecated
	public boolean readExistingOrCreateNewList() {
		try {
			TasksUiPlugin.getDefault().reloadDataDirectory();
		} catch (CoreException e) {
			StatusHandler.fail(e.getStatus());
		}
		return true;
	}

	/**
	 * @deprecated use {@link TasksUiPlugin#getTaskList()} instead
	 */
	@Deprecated
	public TaskList getTaskList() {
		return taskList;
	}

	/**
	 * use <code>TasksUi.getTaskActivityManager().getActiveTask()</code>
	 * 
	 * @deprecated
	 */
	@Deprecated
	public ITask getActiveTask() {
		return TasksUi.getTaskActivityManager().getActiveTask();
	}

	/**
	 * Will not save an empty task list to avoid losing data on bad startup.
	 * 
	 * @deprecated use <code>TasksUiPlugin.getExternalizationManager().requestSave()</code>
	 */
	@Deprecated
	public synchronized void saveTaskList() {
		TasksUiPlugin.getExternalizationManager().requestSave();
	}

	/**
	 * use <code>TasksUi.getTaskActivityManager().activateTask(task)</code>
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void activateTask(AbstractTask task) {
		TasksUi.getTaskActivityManager().activateTask(task);
	}

	/**
	 * use <code>TasksUi.getTaskActivityManager().activateTask(task)</code>
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void activateTask(AbstractTask task, boolean addToHistory) {
		TasksUi.getTaskActivityManager().activateTask(task);
	}

	/**
	 * use <code>TasksUi.getTaskActivityManager().deactivateAllTasks()</code>
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void deactivateAllTasks() {
		TasksUi.getTaskActivityManager().deactivateActiveTask();
	}

	/**
	 * use <code>TasksUi.getTaskActivityManager().deactivateTask(task)</code>
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void deactivateTask(ITask task) {
		TasksUi.getTaskActivityManager().deactivateTask(task);
	}
}
