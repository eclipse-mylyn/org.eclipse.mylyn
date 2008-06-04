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
 * @since 3.0
 */
public interface ITaskActivityManager {

	/**
	 * activate the given <code>task</code>
	 */
	public abstract void activateTask(ITask task);

	/**
	 * deactivate the currently active task (if any). There are no negative side effects if this method is called when
	 * no task is active
	 */
	public abstract void deactivateActiveTask();

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
	 * @return the currently active task if any
	 */
	public abstract ITask getActiveTask();

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

	/**
	 * return the total elapsed time based on activation history between <code>start</code> and <code>end</code> If task
	 * is null, the elapsed time for the range with no task active is returned
	 */
	public abstract long getElapsedTime(ITask task, Calendar start, Calendar end);

	public abstract void addActivityListener(ITaskActivityListener listener);

	public abstract void removeActivityListener(ITaskActivityListener listener);

	public abstract void addActivationListener(ITaskActivationListener listener);

	public abstract void removeActivationListener(ITaskActivationListener listener);

	/**
	 * @param task
	 *            cannot be null
	 * @return whether the task is the single currently active task
	 */
	public abstract boolean isActive(ITask task);
}