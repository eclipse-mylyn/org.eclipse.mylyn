/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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
	void activateTask(ITask task);

	/**
	 * deactivate the currently active task (if any). There are no negative side effects if this method is called when no task is active
	 */
	void deactivateActiveTask();

	/**
	 * deactivate the given task
	 */
	void deactivateTask(ITask task);

	/**
	 * returns all tasks that where active between <code>start</code> and <code>end</end> (exclusive) both ranges are floored to the hour
	 */
	Set<AbstractTask> getActiveTasks(Calendar start, Calendar end);

	/**
	 * @return the currently active task if any
	 */
	ITask getActiveTask();

	/**
	 * returns all tasks with a due date set
	 */
	Set<ITask> getAllDueTasks();

	/**
	 * returns all tasks due between the given dates
	 */
	Set<ITask> getDueTasks(Calendar start, Calendar end);

	/** total elapsed time based on activation history */
	long getElapsedTime(ITask task);

	/**
	 * return the total elapsed time based on activation history between <code>start</code> and <code>end</code> If task is null, the
	 * elapsed time for the range with no task active is returned
	 */
	long getElapsedTime(ITask task, Calendar start, Calendar end);

	void addActivityListener(ITaskActivityListener listener);

	void removeActivityListener(ITaskActivityListener listener);

	void addActivationListener(ITaskActivationListener listener);

	void removeActivationListener(ITaskActivationListener listener);

	/**
	 * @param task
	 *            cannot be null
	 * @return whether the task is the single currently active task
	 */
	boolean isActive(ITask task);
}