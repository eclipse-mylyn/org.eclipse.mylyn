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

package org.eclipse.mylyn.tasks.core;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public abstract class AbstractTaskDataHandler {

	public abstract RepositoryTaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException;

	/**
	 * Override if connector supports retrieval of multiple task data in one query
	 */
	public Set<RepositoryTaskData> getTaskData(TaskRepository repository, Set<String> taskIds, IProgressMonitor monitor)
			throws CoreException {
		// not supported
		return null;
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

	public abstract Set<String> getSubTaskIds(RepositoryTaskData taskData);

}
