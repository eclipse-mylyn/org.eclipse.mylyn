/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class TaskOpenEvent {

	private final TaskRepository repository;

	private final ITask task;

	private final String taskId;

	public TaskOpenEvent(TaskRepository repository, ITask task, String taskId) {
		this.repository = repository;
		this.task = task;
		this.taskId = taskId;
	}

	public TaskRepository getRepository() {
		return repository;
	}

	public ITask getTask() {
		return task;
	}

	public String getTaskId() {
		return taskId;
	}

}
