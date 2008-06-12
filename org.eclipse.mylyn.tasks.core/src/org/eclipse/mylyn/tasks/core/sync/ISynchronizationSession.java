/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 */
public interface ISynchronizationSession {

	public abstract Set<ITask> getChangedTasks();

	public abstract Object getData();

	public abstract IStatus getStatus();

	public abstract ITaskDataManager getTaskDataManager();

	public abstract TaskRepository getTaskRepository();

	public abstract Set<ITask> getTasks();

	public abstract boolean isFullSynchronization();

	public abstract boolean isUser();

	public abstract boolean needsPerformQueries();

	public abstract void setData(Object data);

	public abstract void setNeedsPerformQueries(boolean performQueries);

	public abstract void markStale(ITask task);

	public abstract void putTaskData(ITask task, TaskData taskData) throws CoreException;

}