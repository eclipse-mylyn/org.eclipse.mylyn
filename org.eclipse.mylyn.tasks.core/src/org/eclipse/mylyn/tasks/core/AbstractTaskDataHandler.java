/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Responsible for retrieving and posting task data to a repository.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @since 2.0
 */
public abstract class AbstractTaskDataHandler {

	/**
	 * Download copy of task data from repository
	 * 
	 * @throws CoreException
	 */
	public abstract RepositoryTaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException;

	/**
	 * Download task data for each id provided
	 * 
	 * Override getMultiTaskData() to return true and implement this method if connector supports download of multiple
	 * task data in one request.
	 */
	public Set<RepositoryTaskData> getMultiTaskData(TaskRepository repository, Set<String> taskIds,
			IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Return a reference to the newly created report in the case of new task submission, null otherwise
	 */
	public abstract String postTaskData(TaskRepository repository, RepositoryTaskData taskData, IProgressMonitor monitor)
			throws CoreException;

	/**
	 * @param repositoryUrl
	 * @param repositoryKind
	 * @param taskKind
	 *            AbstractTask.DEFAULT_KIND or connector specific task kind string
	 * @return
	 */
	public abstract AbstractAttributeFactory getAttributeFactory(String repositoryUrl, String repositoryKind,
			String taskKind);

	/**
	 * Initialize a new task data object with default attributes and values
	 */
	public abstract boolean initializeTaskData(TaskRepository repository, RepositoryTaskData data,
			IProgressMonitor monitor) throws CoreException;

	public abstract AbstractAttributeFactory getAttributeFactory(RepositoryTaskData taskData);

	/**
	 * @return Task id for any sub tasks referenced by the provided task data
	 */
	public abstract Set<String> getSubTaskIds(RepositoryTaskData taskData);

	/**
	 * @return true if connector support downloading multiple task data in single request, false otherwise. If true,
	 *         override and implement getMultiTaskData
	 */
	public boolean canGetMultiTaskData() {
		return false;
	}

}
