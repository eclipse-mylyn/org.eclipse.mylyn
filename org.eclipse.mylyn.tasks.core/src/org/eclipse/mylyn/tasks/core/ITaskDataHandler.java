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

package org.eclipse.mylar.tasks.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public interface ITaskDataHandler {

	public RepositoryTaskData getTaskData(TaskRepository repository, String taskId) throws CoreException;

	/**
	 * Return a reference to the newly created report in the case of new task submission,
	 * null otherwise
	 */
	public String postTaskData(TaskRepository repository, RepositoryTaskData taskData) throws CoreException;

	public AbstractAttributeFactory getAttributeFactory(String repositoryUrl, String repositoryKind, String taskKind);
	
	/**
	 * Initialize a new task data object with default attributes and values
	 */
	public boolean initializeTaskData(TaskRepository repository, RepositoryTaskData data, IProgressMonitor monitor) throws CoreException;

	public AbstractAttributeFactory getAttributeFactory(RepositoryTaskData taskData);

}
