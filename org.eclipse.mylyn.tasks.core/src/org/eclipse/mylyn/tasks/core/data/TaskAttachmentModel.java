/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public class TaskAttachmentModel {

	private final TaskAttribute attribute;

	private AbstractTaskAttachmentSource source;

	private final TaskRepository taskRepository;

	private final AbstractTask task;

	public TaskAttachmentModel(TaskRepository taskRepository, AbstractTask task, TaskAttribute attribute) {
		this.taskRepository = taskRepository;
		this.task = task;
		this.attribute = attribute;
	}

	public TaskAttribute getAttribute() {
		return attribute;
	}

	public AbstractTaskAttachmentSource getSource() {
		return source;
	}

	public void setSource(AbstractTaskAttachmentSource source) {
		this.source = source;
	}

	public AbstractTask getTask() {
		return task;
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}
}