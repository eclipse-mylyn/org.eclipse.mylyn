/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Responsible for retrieving and posting task data to a repository.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 * @author Frank Becker
 * @since 3.0
 */
public abstract class AbstractTaskDataHandler {

	/**
	 * Download task data for each id provided
	 * 
	 * Override getMultiTaskData() to return true and implement this method if connector supports download of multiple
	 * task data in one request.
	 * 
	 * @since 3.0
	 */
	public void getMultiTaskData(TaskRepository repository, Set<String> taskIds, TaskDataCollector collector,
			IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Return a reference to the newly created report in the case of new task submission, null otherwise
	 */
	public abstract RepositoryResponse postTaskData(TaskRepository repository, TaskData taskData,
			Set<TaskAttribute> oldAttributes, IProgressMonitor monitor) throws CoreException;

	/**
	 * Initialize a new task data object with default attributes and values
	 */
	public abstract boolean initializeTaskData(TaskRepository repository, TaskData data,
			ITaskMapping initializationData, IProgressMonitor monitor) throws CoreException;

	/**
	 * @since 2.2
	 * @return false if this operation is not supported by the connector, true if initialized
	 */
	public boolean initializeSubTaskData(TaskRepository repository, TaskData taskData, TaskData parentTaskData,
			IProgressMonitor monitor) throws CoreException {
		return false;
	}

	/**
	 * @param taskRepository
	 *            TODO
	 * @param task
	 *            the parent task, may be null
	 * @param task
	 *            the parent task data, may be null
	 * @since 2.2
	 */
	public boolean canInitializeSubTaskData(TaskRepository taskRepository, ITask task) {
		return false;
	}

	public abstract TaskAttributeMapper getAttributeMapper(TaskRepository taskRepository);

	/**
	 * @param taskRepository
	 *            TODO
	 * @return true if connector support downloading multiple task data in single request, false otherwise. If true,
	 *         override and implement getMultiTaskData
	 */
	public boolean canGetMultiTaskData(TaskRepository taskRepository) {
		return false;
	}

	public void migrateTaskData(TaskRepository taskRepository, TaskData taskData) {
	}

}