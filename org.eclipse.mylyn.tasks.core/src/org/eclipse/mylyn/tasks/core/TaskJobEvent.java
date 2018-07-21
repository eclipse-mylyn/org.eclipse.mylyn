/*******************************************************************************
 * Copyright (c) 2012, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Endre Zoltan Kovacs - adding {@link TaskData} to the API.
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Sam Davis
 * @since 3.7
 */
public class TaskJobEvent {

	private final ITask task;

	private final ITask originalTask;

	private final TaskData taskData;

	public TaskJobEvent(ITask originalTask, ITask task) {
		this(originalTask, task, null);
	}

	/**
	 * @since 3.11
	 */
	public TaskJobEvent(final ITask originalTask, final ITask task, @Nullable final TaskData taskData) {
		this.originalTask = originalTask;
		this.task = task;
		this.taskData = taskData;
	}

	/**
	 * @return the original task that was submitted
	 */
	public ITask getOriginalTask() {
		return originalTask;
	}

	/**
	 * @return the task that exists after submission has completed; will be the same as originalTask unless originalTask
	 *         was a new (previously unsubmitted) task and this is a submission complete event
	 */
	public ITask getTask() {
		return task;
	}

	/**
	 * @return The taskData of the task being submitted. On 'aboutTosubmit', it holds the unsubmitted taskData, on
	 *         'taskSubmitted' it is the updated taskData. May be <code>null</code>.
	 * @since 3.11
	 */
	public TaskData getTaskData() {
		return taskData;
	}

}
