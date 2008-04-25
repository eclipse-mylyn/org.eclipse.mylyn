/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.TaskDataStorageManager;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.ITaskFactory;
import org.eclipse.mylyn.tasks.core.ITaskList;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;

/**
 * Used for creating tasks from repository task data.
 * 
 * NOTE: likely to change for 3.0.
 * 
 * @author Rob Elves
 * @deprecated
 */
@Deprecated
public class TaskFactory implements ITaskFactory {

	private final AbstractRepositoryConnector connector;

	private final TaskDataManager synchManager;

	private final TaskRepository repository;

	private final ITaskList taskList;

	private final AbstractTaskDataHandler dataHandler;

	private final boolean updateTasklist;

	private final boolean forced;

	public TaskFactory(TaskRepository repository, boolean updateTasklist, boolean forced) {
		this.repository = repository;
		this.updateTasklist = updateTasklist;
		this.forced = forced;
		connector = TasksUi.getRepositoryManager().getRepositoryConnector(repository.getConnectorKind());
		synchManager = TasksUiPlugin.getTaskDataManager();
		taskList = TasksUi.getTaskListManager().getTaskList();
		//dataManager = TasksUiPlugin.getTaskDataManager();
		dataHandler = connector.getTaskDataHandler();
	}

	@Deprecated
	public TaskFactory(TaskRepository repository) {
		this(repository, true, false);
	}

	/**
	 * @param updateTasklist -
	 *            synchronize task with the provided taskData
	 * @param forced -
	 *            user requested synchronization
	 * @throws CoreException
	 */
	public AbstractTask createTask(RepositoryTaskData taskData, IProgressMonitor monitor) throws CoreException {
		AbstractTask repositoryTask = taskList.getTask(taskData.getRepositoryUrl(), taskData.getTaskId());
		if (repositoryTask == null) {
			repositoryTask = createTaskFromTaskData(connector, repository, taskData, updateTasklist, monitor);
			repositoryTask.setSynchronizationState(RepositoryTaskSyncState.INCOMING);
			if (updateTasklist) {
				taskList.addTask(repositoryTask);
				synchManager.saveIncoming(repositoryTask, taskData, forced);
			} else {
				synchManager.saveOffline(repositoryTask, taskData);
			}

		} else {
			if (updateTasklist) {
				synchManager.saveIncoming(repositoryTask, taskData, forced);
				connector.updateTaskFromTaskData(repository, repositoryTask, taskData);
				if (dataHandler != null) {
					for (AbstractTask child : repositoryTask.getChildren()) {
						taskList.removeFromContainer(repositoryTask, child);
					}
					Set<String> subTaskIds = dataHandler.getSubTaskIds(taskData);
					if (subTaskIds != null) {
						for (String subId : subTaskIds) {
							if (subId == null || subId.trim().equals("")) {
								continue;
							}
							AbstractTask subTask = createTaskFromExistingId(connector, repository, subId, false,
									new SubProgressMonitor(monitor, 1));
							if (subTask != null) {
								taskList.addTask(subTask, repositoryTask);
							}
						}
					}
				}
			}
		}
		return repositoryTask;
	}

	/**
	 * Creates a new task from the given task data. Does NOT add resulting task to the tasklist
	 */
	private AbstractTask createTaskFromTaskData(AbstractRepositoryConnector connector, TaskRepository repository,
			RepositoryTaskData taskData, boolean retrieveSubTasks, IProgressMonitor monitor) throws CoreException {
		AbstractTask repositoryTask = null;
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		try {
			TaskDataStorageManager taskDataManager = TasksUiPlugin.getTaskDataStorageManager();
			if (taskData != null) {
				// Use connector task factory
				repositoryTask = connector.createTask(repository.getRepositoryUrl(), taskData.getTaskId(),
						taskData.getTaskId() + ": " + taskData.getDescription());
				connector.updateTaskFromTaskData(repository, repositoryTask, taskData);
				taskDataManager.setNewTaskData(taskData);

				if (retrieveSubTasks) {
					monitor.beginTask("Creating task", connector.getTaskDataHandler().getSubTaskIds(taskData).size());
					for (String subId : connector.getTaskDataHandler().getSubTaskIds(taskData)) {
						if (subId == null || subId.trim().equals("")) {
							continue;
						}
						AbstractTask subTask = createTaskFromExistingId(connector, repository, subId, false,
								new SubProgressMonitor(monitor, 1));
						if (subTask != null) {
							taskList.addTask(subTask, repositoryTask);
						}
					}
				}
			}
		} finally {
			monitor.done();
		}
		return repositoryTask;
	}

	/**
	 * Create new repository task, adding result to tasklist
	 */
	private AbstractTask createTaskFromExistingId(AbstractRepositoryConnector connector, TaskRepository repository,
			String id, boolean retrieveSubTasks, IProgressMonitor monitor) throws CoreException {
		AbstractTask repositoryTask = taskList.getTask(repository.getRepositoryUrl(), id);
		if (repositoryTask == null && connector.getTaskDataHandler() != null) {
			RepositoryTaskData taskData = null;
			taskData = connector.getTaskDataHandler().getTaskData(repository, id, new SubProgressMonitor(monitor, 1));
			if (taskData != null) {
				repositoryTask = createTaskFromTaskData(connector, repository, taskData, retrieveSubTasks,
						new SubProgressMonitor(monitor, 1));
				if (repositoryTask != null) {
					repositoryTask.setSynchronizationState(RepositoryTaskSyncState.INCOMING);
					taskList.addTask(repositoryTask);
				}
			}
		} // TODO: Handle case similar to web tasks (no taskDataHandler but
		// have tasks)

		return repositoryTask;
	}

}
