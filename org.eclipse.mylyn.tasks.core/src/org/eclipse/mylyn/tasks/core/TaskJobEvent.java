/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

/**
 * @author Sam Davis
 * @since 3.7
 */
public class TaskJobEvent {

	private final ITask task;

	private final ITask originalTask;

	public TaskJobEvent(ITask originalTask, ITask task) {
		this.originalTask = originalTask;
		this.task = task;
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

}
