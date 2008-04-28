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

	private final String commentId;

	private final String connectorKind;

	private Date creationDate;

	private int number;

	private final String repositoryUrl;

	private final String taskId;

	private String text;

	private String url;

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

	public int getNumber() {
		return number;
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

	public String getUrl() {
		return url;
	}

	public void setAuthor(RepositoryPerson author) {
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

	public static TaskComment createFrom(TaskAttribute taskAttribute) {
		TaskData taskData = taskAttribute.getTaskData();
		TaskAttributeMapper mapper = taskData.getAttributeMapper();
		String commentId = mapper.getValue(taskAttribute);
		TaskComment comment = new TaskComment(taskData.getRepositoryUrl(), taskData.getConnectorKind(),
				taskData.getTaskId(), commentId);
		try {
			comment.setNumber(Integer.parseInt(taskAttribute.getId()));
		} catch (NumberFormatException e) {
			// ignore
		}
		TaskAttribute child = taskAttribute.getMappedAttribute(TaskAttribute.COMMENT_AUTHOR);
		if (child != null) {
			RepositoryPerson person = mapper.getRepositoryPerson(child);
			if (person.getName() == null) {
				child = taskAttribute.getMappedAttribute(TaskAttribute.COMMENT_AUTHOR_NAME);
				if (child != null) {
					person.setName(child.getValue());
				}
			}
			comment.setAuthor(person);
		}
		child = taskAttribute.getMappedAttribute(TaskAttribute.COMMENT_DATE);
		if (child != null) {
			comment.setCreationDate(mapper.getDateValue(child));
		}
		child = taskAttribute.getMappedAttribute(TaskAttribute.COMMENT_URL);
		if (child != null) {
			comment.setUrl(mapper.getValue(child));
		}
		child = taskAttribute.getMappedAttribute(TaskAttribute.COMMENT_TEXT);
		if (child != null) {
			comment.setText(mapper.getValue(child));
		}
		return comment;
	}

	public void applyTo(TaskAttribute taskAttribute) {
		TaskData taskData = taskAttribute.getTaskData();
		TaskAttributeMapper mapper = taskData.getAttributeMapper();

		mapper.setValue(taskAttribute, getCommentId());
		TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_COMMENT).applyTo(taskAttribute);
		taskAttribute.putMetaDataValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, TaskAttribute.COMMENT_TEXT);

		if (getAuthor() != null) {
			TaskAttribute child = taskAttribute.createAttribute(TaskAttribute.COMMENT_AUTHOR);
			TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_PERSON).applyTo(child);
			mapper.setRepositoryPerson(child, getAuthor());
		}
		if (getCreationDate() != null) {
			TaskAttribute child = taskAttribute.createAttribute(TaskAttribute.COMMENT_DATE);
			TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_DATE).applyTo(child);
			mapper.setDateValue(child, getCreationDate());
		}
		if (getUrl() != null) {
			TaskAttribute child = taskAttribute.createAttribute(TaskAttribute.COMMENT_URL);
			TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_URL).applyTo(child);
			mapper.setValue(child, getUrl());
		}
		if (getText() != null) {
			TaskAttribute child = taskAttribute.createAttribute(TaskAttribute.COMMENT_TEXT);
			TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_LONG_RICH_TEXT).applyTo(child);
			mapper.setValue(child, getText());
		}
	}

}
