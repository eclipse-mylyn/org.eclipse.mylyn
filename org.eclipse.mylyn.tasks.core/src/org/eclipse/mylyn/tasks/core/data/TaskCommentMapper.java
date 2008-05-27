/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import java.util.Date;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITaskComment;

/**
 * A comment posted by a user on a task.
 * 
 * @author Rob Elves
 * @author Steffen Pingel
 * @since 3.0
 */
public class TaskCommentMapper {

	private IRepositoryPerson author;

	private String commentId;

	private Date creationDate;

	private Integer number;

	private String text;

	private String url;

	public TaskCommentMapper() {
	}

	public IRepositoryPerson getAuthor() {
		return author;
	}

	public String getCommentId() {
		return commentId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Integer getNumber() {
		return number;
	}

	public String getText() {
		return text;
	}

	public String getUrl() {
		return url;
	}

	public void setAuthor(IRepositoryPerson author) {
		this.author = author;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@SuppressWarnings("deprecation")
	public static TaskCommentMapper createFrom(TaskAttribute taskAttribute) {
		Assert.isNotNull(taskAttribute);
		TaskData taskData = taskAttribute.getTaskData();
		TaskAttributeMapper mapper = taskData.getAttributeMapper();
		TaskCommentMapper comment = new TaskCommentMapper();
		comment.setCommentId(mapper.getValue(taskAttribute));
		TaskAttribute child = taskAttribute.getMappedAttribute(TaskAttribute.COMMENT_AUTHOR);
		if (child != null) {
			IRepositoryPerson person = mapper.getRepositoryPerson(child);
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
		child = taskAttribute.getMappedAttribute(TaskAttribute.COMMENT_NUMBER);
		if (child != null) {
			comment.setNumber(mapper.getIntegerValue(child));
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
		Assert.isNotNull(taskAttribute);
		TaskData taskData = taskAttribute.getTaskData();
		TaskAttributeMapper mapper = taskData.getAttributeMapper();
		taskAttribute.getMetaData().defaults().setType(TaskAttribute.TYPE_COMMENT);
		if (getCommentId() != null) {
			mapper.setValue(taskAttribute, getCommentId());
		}
		if (getAuthor() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute(TaskAttribute.COMMENT_AUTHOR);
			child.getMetaData().defaults().setType(TaskAttribute.TYPE_PERSON);
			mapper.setRepositoryPerson(child, getAuthor());
		}
		if (getCreationDate() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute(TaskAttribute.COMMENT_DATE);
			child.getMetaData().defaults().setType(TaskAttribute.TYPE_DATE);
			mapper.setDateValue(child, getCreationDate());
		}
		if (getNumber() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute(TaskAttribute.COMMENT_NUMBER);
			child.getMetaData().defaults().setType(TaskAttribute.TYPE_INTEGER);
			mapper.setIntegerValue(child, getNumber());
		}
		if (getUrl() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute(TaskAttribute.COMMENT_URL);
			child.getMetaData().defaults().setType(TaskAttribute.TYPE_URL);
			mapper.setValue(child, getUrl());
		}
		if (getText() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute(TaskAttribute.COMMENT_TEXT);
			child.getMetaData().defaults().setType(TaskAttribute.TYPE_LONG_RICH_TEXT);
			mapper.setValue(child, getText());
			taskAttribute.putMetaDatum(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, TaskAttribute.COMMENT_TEXT);
		}
	}

	public void applyTo(ITaskComment taskComment) {
		Assert.isNotNull(taskComment);
		if (getAuthor() != null) {
			taskComment.setAuthor(getAuthor());
		}
		if (getCreationDate() != null) {
			taskComment.setCreationDate(getCreationDate());
		}
		if (getNumber() != null) {
			taskComment.setNumber(getNumber());
		}
		if (getUrl() != null) {
			taskComment.setUrl(getUrl());
		}
		if (getText() != null) {
			taskComment.setText(getText());
		}
	}
}
