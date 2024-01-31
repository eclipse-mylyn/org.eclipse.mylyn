/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Steffen Pingel
 */
public class TaskAttachment implements ITaskAttachment {

	private IRepositoryPerson author;

	private String comment;

	private String contentType;

	private Date creationDate;

	private boolean deprecated;

	private String description;

	private String fileName;

	private long length;

	private boolean patch;

	private final ITask task;

	private final TaskAttribute taskAttribute;

	private final TaskRepository taskRepository;

	private String url;

	public TaskAttachment(TaskRepository taskRepository, ITask task, TaskAttribute taskAttribute) {
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
	public String getComment() {
		return comment;
	}

	@Override
	public String getConnectorKind() {
		return taskRepository.getConnectorKind();
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public long getLength() {
		return length;
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
	public String getUrl() {
		return url;
	}

	@Override
	public boolean isDeprecated() {
		return deprecated;
	}

	@Override
	public boolean isPatch() {
		return patch;
	}

	@Override
	public void setAuthor(IRepositoryPerson author) {
		this.author = author;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void setLength(long length) {
		this.length = length;
	}

	@Override
	public void setPatch(boolean patch) {
		this.patch = patch;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

}
