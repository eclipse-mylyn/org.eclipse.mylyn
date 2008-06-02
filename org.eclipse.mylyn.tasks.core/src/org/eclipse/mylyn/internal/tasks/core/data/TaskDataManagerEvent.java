/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.data;

import java.util.EventObject;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 */
public class TaskDataManagerEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private final ITask task;

	private final boolean taskChanged;

	private final TaskData taskData;

	private final boolean taskDataChanged;

	private final Object token;

	public TaskDataManagerEvent(ITaskDataManager source, ITask task, boolean taskChanged, TaskData taskData,
			boolean taskDataChanged, Object token) {
		super(source);
		this.task = task;
		this.taskChanged = taskChanged;
		this.taskData = taskData;
		this.taskDataChanged = taskDataChanged;
		this.token = token;
	}

	public ITask getTask() {
		return task;
	}

	public boolean getTaskChanged() {
		return taskChanged;
	}

	public TaskData getTaskData() {
		return taskData;
	}

	public boolean getTaskDataChanged() {
		return taskDataChanged;
	}

	public Object getToken() {
		return token;
	}

}
