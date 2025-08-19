/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core;

import java.util.Date;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * A comment posted by a user on a task.
 *
 * @author Steffen Pingel
 */
public class TaskComment implements ITaskComment {

	private IRepositoryPerson author;

	private Date creationDate;

	private int number;

	private final ITask task;

	private final TaskAttribute taskAttribute;

	private final TaskRepository taskRepository;

	private String text;

	private String url;

	private Boolean isPrivate;

	public TaskComment(TaskRepository taskRepository, ITask task, TaskAttribute taskAttribute) {
		Assert.isNotNull(taskRepository);
		Assert.isNotNull(task);
		Assert.isNotNull(taskAttribute);
		this.taskRepository = taskRepository;
		this.task = task;
		this.taskAttribute = taskAttribute;
	}

	@Override
	public IRepositoryPerson getAuthor() {
		return author;
	}

	@Override
	public String getConnectorKind() {
		return taskRepository.getConnectorKind();
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public int getNumber() {
		return number;
	}

	@Override
	public String getRepositoryUrl() {
		return taskRepository.getRepositoryUrl();
	}

	@Override
	public ITask getTask() {
		return task;
	}

	@Override
	public TaskAttribute getTaskAttribute() {
		return taskAttribute;
	}

	@Override
	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public void setAuthor(IRepositoryPerson author) {
		this.author = author;
	}

	@Override
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public Boolean getIsPrivate() {
		return isPrivate;
	}

	@Override
	public void setIsPrivate(Boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

}
