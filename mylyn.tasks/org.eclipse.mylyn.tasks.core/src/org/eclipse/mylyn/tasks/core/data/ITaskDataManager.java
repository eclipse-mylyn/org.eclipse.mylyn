/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
	ITaskDataWorkingCopy createWorkingCopy(ITask task, TaskData taskData);

	/**
	 * @since 3.0
	 */
	ITaskDataWorkingCopy getWorkingCopy(ITask task) throws CoreException;

	/**
	 * @since 3.0
	 */
	void discardEdits(ITask task) throws CoreException;

	/**
	 * @since 3.0
	 */
	TaskData getTaskData(ITask task) throws CoreException;

	/**
	 * @since 3.0
	 */
	TaskData getTaskData(TaskRepository task, String taskId) throws CoreException;

	/**
	 * @since 3.0
	 */
	boolean hasTaskData(ITask task);

}