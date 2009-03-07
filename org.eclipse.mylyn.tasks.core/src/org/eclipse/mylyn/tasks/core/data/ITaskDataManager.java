/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ITaskDataManager {

	/**
	 * @since 3.0
	 */
	public ITaskDataWorkingCopy createWorkingCopy(ITask task, TaskData taskData);

	/**
	 * @since 3.0
	 */
	public abstract ITaskDataWorkingCopy getWorkingCopy(ITask task) throws CoreException;

	/**
	 * @since 3.0
	 */
	public abstract void discardEdits(ITask task) throws CoreException;

	/**
	 * @since 3.0
	 */
	public abstract TaskData getTaskData(ITask task) throws CoreException;

	/**
	 * @since 3.0
	 */
	public abstract TaskData getTaskData(TaskRepository task, String taskId) throws CoreException;

	/**
	 * @since 3.0
	 */
	public abstract boolean hasTaskData(ITask task);

}