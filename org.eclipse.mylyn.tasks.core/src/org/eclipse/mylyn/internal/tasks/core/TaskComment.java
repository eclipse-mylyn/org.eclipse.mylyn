/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.Date;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.core.ITaskRepositoryPerson;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * A comment posted by a user on a task.
 * 
 * @author Steffen Pingel
 */
public class TaskComment implements ITaskComment {

	private ITaskRepositoryPerson author;

	private Date creationDate;

	private int number;

	private final AbstractTask task;

	private final TaskAttribute taskAttribute;

	private final TaskRepository taskRepository;

	private String text;

	private String url;

	public TaskComment(TaskRepository taskRepository, AbstractTask task, TaskAttribute taskAttribute) {
		Assert.isNotNull(taskRepository);
		Assert.isNotNull(task);
		Assert.isNotNull(taskAttribute);
		this.taskRepository = taskRepository;
		this.task = task;
		this.taskAttribute = taskAttribute;
	}

	public ITaskRepositoryPerson getAuthor() {
		return author;
	}

	public String getConnectorKind() {
		return taskRepository.getConnectorKind();
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public int getNumber() {
		return number;
	}

	public String getRepositoryUrl() {
		return taskRepository.getRepositoryUrl();
	}

	public AbstractTask getTask() {
		return task;
	}

	public TaskAttribute getTaskAttribute() {
		return taskAttribute;
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	public String getText() {
		return text;
	}

	public String getUrl() {
		return url;
	}

	public void setAuthor(ITaskRepositoryPerson author) {
		this.author = author;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
