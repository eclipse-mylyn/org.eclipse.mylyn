/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import java.util.Date;

/**
 * A comment posted by a user on a task.
 * 
 * @author Rob Elves
 * @author Steffen Pingel
 * @since 3.0
 */
public class TaskComment {

	private RepositoryPerson author;

	private String authorName;

	private final String commentId;

	private final String connectorKind;

	private Date creationDate;

	private final String repositoryUrl;

	private final String taskId;

	private String text;

	private int number;

	public TaskComment(String connectorKind, String repositoryUrl, String taskId, String commentId) {
		this.connectorKind = connectorKind;
		this.repositoryUrl = repositoryUrl;
		this.taskId = taskId;
		this.commentId = commentId;
	}

	public RepositoryPerson getAuthor() {
		return author;
	}

	public String getCommentId() {
		return commentId;
	}

	public String getConnectorKind() {
		return connectorKind;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public String getTaskId() {
		return taskId;
	}

	public String getText() {
		return text;
	}

	public void setAuthor(RepositoryPerson author) {
		this.author = author;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

}
