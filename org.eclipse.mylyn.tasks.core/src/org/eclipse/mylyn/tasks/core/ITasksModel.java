/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @since 3.0
 * @author Steffen Pingel
 */
public interface ITasksModel {

	public abstract IRepositoryQuery createQuery(TaskRepository taskRepository);

	public abstract ITask createTask(TaskRepository taskRepository, String taskId);

	public abstract ITaskAttachment createTaskAttachment(TaskAttribute taskAttribute);

	public abstract ITaskComment createTaskComment(TaskAttribute taskAttribute);

	public abstract ITask getTask(TaskRepository taskRepository, String taskId);

	public abstract ITask getTask(String handle);

}
