/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylyn.tasks.core.ITaskDataHandler;
import org.eclipse.mylyn.tasks.core.ITaskFactory;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Interim factory
 * 
 * @author Rob Elves
 */
public class TaskFactory implements ITaskFactory {

	private AbstractRepositoryConnector connector;

	private RepositorySynchronizationManager synchManager;

	private TaskRepository repository;

	private TaskList taskList;

	//private TaskDataManager dataManager;

	private ITaskDataHandler dataHandler;

	public TaskFactory(TaskRepository repository) {
		this.repository = repository;
		connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getKind());
		synchManager = TasksUiPlugin.getSynchronizationManager();
		taskList = TasksUiPlugin.getTaskListManager().getTaskList();
		//dataManager = TasksUiPlugin.getDefault().getTaskDataManager();
		dataHandler = connector.getTaskDataHandler();

	}

	/**
	 * @param updateTasklist -
	 *            synchronize task with the provided taskData
	 * @param forced -
	 *            user requested synchronization
	 * @throws CoreException
	 */
	public AbstractRepositoryTask createTask(RepositoryTaskData taskData, boolean updateTasklist, boolean forced,
			IProgressMonitor monitor) throws CoreException {
		AbstractRepositoryTask repositoryTask = taskList.getTask(taskData.getRepositoryUrl(), taskData.getId());
		if (repositoryTask == null) {

			repositoryTask = connector.createTaskFromTaskData(repository, taskData, true, monitor);

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
					repositoryTask.clear();
					for (String subId : dataHandler.getSubTaskIds(taskData)) {
						if (subId == null || subId.trim().equals("")) {
							continue;
						}
						AbstractRepositoryTask subTask = connector.createTaskFromExistingId(repository, subId, false,
								new SubProgressMonitor(monitor, 1));
						if (subTask != null) {
							taskList.addTask(subTask, repositoryTask);
						}
					}
				}
			}
		}
		return repositoryTask;
	}

	// TODO: Move all task construction code here
	
//	/**
//	 * Create new repository task, adding result to tasklist
//	 */
//	public AbstractRepositoryTask createTaskFromExistingId(TaskRepository repository, String id,
//			boolean retrieveSubTasks, IProgressMonitor monitor) throws CoreException {
//		ITask task = taskList.getTask(repository.getUrl(), id);
//		AbstractRepositoryTask repositoryTask = null;
//		if (task instanceof AbstractRepositoryTask) {
//			repositoryTask = (AbstractRepositoryTask) task;
//		} else if (task == null && dataHandler != null) {
//			RepositoryTaskData taskData = null;
//			taskData = dataHandler.getTaskData(repository, id, new SubProgressMonitor(monitor, 1));
//			if (taskData != null) {
//				repositoryTask = createTaskFromTaskData(repository, taskData, retrieveSubTasks, new SubProgressMonitor(
//						monitor, 1));
//				if (repositoryTask != null) {
//					taskList.addTask(repositoryTask);
//				}
//			}
//		} // TODO: Handle case similar to web tasks (no taskDataHandler but
//		// have tasks)
//
//		return repositoryTask;
//	}
//
//	/**
//	 * Creates a new task from the given task data. Does NOT add resulting task
//	 * to the tasklist
//	 */
//	public AbstractRepositoryTask createTaskFromTaskData(TaskRepository repository, RepositoryTaskData taskData,
//			boolean retrieveSubTasks, IProgressMonitor monitor) throws CoreException {
//		AbstractRepositoryTask repositoryTask = null;
//		if (monitor == null) {
//			monitor = new NullProgressMonitor();
//		}
//		try {
//			if (taskData != null && dataHandler != null) {
//				// Use connector task factory
//				repositoryTask = connector.createTask(repository.getUrl(), taskData.getId(), taskData.getId() + ": "
//						+ taskData.getDescription());
//				connector.updateTaskFromTaskData(repository, repositoryTask, taskData);
//				dataManager.setNewTaskData(repositoryTask.getHandleIdentifier(), taskData);
//
//				if (retrieveSubTasks) {
//					monitor.beginTask("Creating task", dataHandler.getSubTaskIds(taskData).size());
//					for (String subId : dataHandler.getSubTaskIds(taskData)) {
//						if (subId == null || subId.trim().equals("")) {
//							continue;
//						}
//						AbstractRepositoryTask subTask = createTaskFromExistingId(repository, subId, false,
//								new SubProgressMonitor(monitor, 1));
//						if (subTask != null) {
//							repositoryTask.addSubTask(subTask);
//						}
//					}
//				}
//			}
//		} finally {
//			monitor.done();
//		}
//		return repositoryTask;
//	}

}
