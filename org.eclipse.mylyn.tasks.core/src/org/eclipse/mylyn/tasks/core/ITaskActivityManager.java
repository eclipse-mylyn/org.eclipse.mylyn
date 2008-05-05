/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Calendar;
import java.util.Set;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;

/**
 * @author Rob Elves
 */
public interface ITaskActivityManager {

	public abstract void addActivityListener(ITaskActivityListener listener);

	public void removeActivityListener(ITaskActivityListener listener);

	/**
	 * activate the given <code>task</code>
	 */
	public abstract void activateTask(ITask task);

	public abstract void deactivateAllTasks();

	/**
	 * deactivate the given task
	 */
	public abstract void deactivateTask(ITask task);

	/**
	 * returns all tasks that where active between <code>start</code> and <code>end</end> (exclusive)
	 * both ranges are floored to the hour
	 */
	public abstract Set<AbstractTask> getActiveTasks(Calendar start, Calendar end);

	/**
	 * @return all tasks currently scheduled in the task list
	 */
	public abstract Set<ITask> getAllScheduledTasks();

	/**
	 * returns all tasks scheduled between <code>start</code> and <code>end</code>
	 */
	public abstract Set<ITask> getScheduledTasks(Calendar start, Calendar end);

	/**
	 * returns all tasks with a due date set
	 */
	public abstract Set<ITask> getAllDueTasks();

	/**
	 * returns all tasks due between the given dates
	 */
	public abstract Set<ITask> getDueTasks(Calendar start, Calendar end);

	/** total elapsed time based on activation history */
	public abstract long getElapsedTime(ITask task);

	/** total elapsed time based on activation history between <code>start</code> and <code>end</code> */
	public abstract long getElapsedTime(ITask task, Calendar start, Calendar end);

	public abstract ITask getActiveTask();

}