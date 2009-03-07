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
	public abstract Set<ITask> getChangedTasks();

	/**
	 * @since 3.0
	 */
	public abstract Object getData();

	/**
	 * @since 3.0
	 */
	public abstract IStatus getStatus();

	/**
	 * @since 3.0
	 */
	public abstract ITaskDataManager getTaskDataManager();

	/**
	 * @since 3.0
	 */
	public abstract TaskRepository getTaskRepository();

	/**
	 * @since 3.0
	 */
	public abstract Set<ITask> getTasks();

	/**
	 * @since 3.0
	 */
	public abstract boolean isFullSynchronization();

	/**
	 * @since 3.0
	 */
	public abstract boolean isUser();

	/**
	 * @since 3.0
	 */
	public abstract boolean needsPerformQueries();

	/**
	 * @since 3.0
	 */
	public abstract void setData(Object data);

	/**
	 * @since 3.0
	 */
	public abstract void setNeedsPerformQueries(boolean performQueries);

	/**
	 * @since 3.0
	 */
	public abstract void markStale(ITask task);

	/**
	 * @since 3.0
	 */
	// TODO m4.0 pass TaskDataCollector to preSynchronization() instead
	public abstract void putTaskData(ITask task, TaskData taskData) throws CoreException;

}