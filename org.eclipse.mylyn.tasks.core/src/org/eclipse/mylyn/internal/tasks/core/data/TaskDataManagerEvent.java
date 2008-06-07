/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.data;

import java.util.EventObject;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 */
public class TaskDataManagerEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private final ITask task;

	private boolean taskChanged;

	private final TaskData taskData;

	private boolean taskDataChanged;

	private boolean taskDataUpdated;

	private final Object token;

	public TaskDataManagerEvent(ITaskDataManager source, ITask task, TaskData taskData, Object token) {
		super(source);
		Assert.isNotNull(task);
		Assert.isNotNull(taskData);
		this.task = task;
		this.taskData = taskData;
		this.token = token;
	}

	public TaskDataManagerEvent(ITaskDataManager source, ITask task) {
		super(source);
		Assert.isNotNull(task);
		this.task = task;
		this.taskData = null;
		this.token = null;
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

	public boolean getTaskDataUpdated() {
		return taskDataUpdated;
	}

	public Object getToken() {
		return token;
	}

	public void setTaskChanged(boolean taskChanged) {
		this.taskChanged = taskChanged;
	}

	public void setTaskDataChanged(boolean taskDataChanged) {
		this.taskDataChanged = taskDataChanged;
	}

	public void setTaskDataUpdated(boolean taskDataUpdated) {
		this.taskDataUpdated = taskDataUpdated;
	}

}
