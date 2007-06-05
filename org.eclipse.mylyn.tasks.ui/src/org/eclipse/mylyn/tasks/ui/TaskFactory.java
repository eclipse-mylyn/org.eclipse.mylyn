/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.tasks.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITaskFactory;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.core.TaskRepository;

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

	public TaskFactory(TaskRepository repository) {
		this.repository = repository;
		connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getKind());
		synchManager = TasksUiPlugin.getSynchronizationManager();
		taskList = TasksUiPlugin.getTaskListManager().getTaskList();
	}

	/**
	 * @param synchData -
	 *            synchronize task with the provided taskData
	 * @param forced -
	 *            user requested synchronization
	 * @throws CoreException
	 */
	public AbstractRepositoryTask createTask(RepositoryTaskData taskData, boolean synchData, boolean forced,
			IProgressMonitor monitor) throws CoreException {
		AbstractRepositoryTask repositoryTask = taskList.getTask(taskData.getRepositoryUrl(), taskData.getId());
		if (repositoryTask == null) {

			repositoryTask = connector.createTaskFromTaskData(repository, taskData, true, monitor);

			if (synchData) {
				taskList.addTask(repositoryTask);
				synchManager.saveIncoming(repositoryTask, taskData, forced);
			} else {
				synchManager.saveOffline(repositoryTask, taskData);
			}

		} else {
			if (synchData) {
				synchManager.saveIncoming(repositoryTask, taskData, forced);
				connector.updateTaskFromTaskData(repository, repositoryTask, taskData);
			}
		}
		return repositoryTask;
	}

}
