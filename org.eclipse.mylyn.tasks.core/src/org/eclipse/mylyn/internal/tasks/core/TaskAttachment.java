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
import org.eclipse.mylyn.tasks.core.ITaskAttachment2;
import org.eclipse.mylyn.tasks.core.ITaskRepositoryPerson;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Steffen Pingel
 */
public class TaskAttachment implements ITaskAttachment2 {

	private ITaskRepositoryPerson author;

	private String comment;

	private String contentType;

	private Date creationDate;

	private boolean deprecated;

	private String description;

	private String fileName;

	private long length;

	private boolean patch;

	private final AbstractTask task;

	private final TaskAttribute taskAttribute;

	private final TaskRepository taskRepository;

	private String url;

	public TaskAttachment(TaskRepository taskRepository, AbstractTask task, TaskAttribute taskAttribute) {
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

	public String getComment() {
		return comment;
	}

	public String getConnectorKind() {
		return taskRepository.getConnectorKind();
	}

	public String getContentType() {
		return contentType;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String getDescription() {
		return description;
	}

	public String getFileName() {
		return fileName;
	}

	public long getLength() {
		return length;
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

	public String getUrl() {
		return url;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	public boolean isPatch() {
		return patch;
	}

	public void setAuthor(ITaskRepositoryPerson author) {
		this.author = author;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public void setPatch(boolean patch) {
		this.patch = patch;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
