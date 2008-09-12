/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public class TaskAttachmentModel {

	private boolean attachContext;

	private final TaskAttribute attribute;

	private String comment;

	private AbstractTaskAttachmentSource source;

	private final ITask task;

	private final TaskRepository taskRepository;

	private String contentType;

	public TaskAttachmentModel(TaskRepository taskRepository, ITask task, TaskAttribute attribute) {
		this.taskRepository = taskRepository;
		this.task = task;
		this.attribute = attribute;
	}

	public boolean getAttachContext() {
		return attachContext;
	}

	public TaskAttribute getAttribute() {
		return attribute;
	}

	public String getComment() {
		return comment;
	}

	public AbstractTaskAttachmentSource getSource() {
		return source;
	}

	public ITask getTask() {
		return task;
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	public void setAttachContext(boolean attachContext) {
		this.attachContext = attachContext;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setSource(AbstractTaskAttachmentSource source) {
		this.source = source;
	}

	public String getContentType() {
		if (contentType == null) {
			return getSource().getContentType();
		}
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}