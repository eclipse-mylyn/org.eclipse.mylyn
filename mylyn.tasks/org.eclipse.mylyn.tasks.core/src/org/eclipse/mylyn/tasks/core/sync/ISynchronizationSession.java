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

package org.eclipse.mylyn.tasks.core.sync;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @since 3.0
 * @author Steffen Pingel
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ISynchronizationSession {

	/**
	 * @since 3.0
	 */
	Set<ITask> getChangedTasks();

	/**
	 * @since 3.0
	 */
	Object getData();

	/**
	 * @since 3.0
	 */
	IStatus getStatus();

	/**
	 * @since 3.0
	 */
	ITaskDataManager getTaskDataManager();

	/**
	 * @since 3.0
	 */
	TaskRepository getTaskRepository();

	/**
	 * @since 3.0
	 */
	Set<ITask> getTasks();

	/**
	 * @since 3.0
	 */
	boolean isFullSynchronization();

	/**
	 * @since 3.0
	 */
	boolean isUser();

	/**
	 * @since 3.0
	 */
	boolean needsPerformQueries();

	/**
	 * @since 3.0
	 */
	void setData(Object data);

	/**
	 * @since 3.0
	 */
	void setNeedsPerformQueries(boolean performQueries);

	/**
	 * @since 3.0
	 */
	void markStale(ITask task);

	/**
	 * @since 3.0
	 */
	// TODO m4.0 pass TaskDataCollector to preSynchronization() instead
	void putTaskData(ITask task, TaskData taskData) throws CoreException;

}