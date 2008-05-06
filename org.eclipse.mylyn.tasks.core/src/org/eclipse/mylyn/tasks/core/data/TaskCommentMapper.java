/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import java.util.Date;

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

	private final String commentId;

	private Date creationDate;

	private int number;

	private String text;

	private String url;

	public TaskCommentMapper(String commentId) {
		this.commentId = commentId;
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

	public int getNumber() {
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

	public static TaskCommentMapper createFrom(TaskAttribute taskAttribute) {
		TaskData taskData = taskAttribute.getTaskData();
		TaskAttributeMapper mapper = taskData.getAttributeMapper();
		String commentId = mapper.getValue(taskAttribute);
		TaskCommentMapper comment = new TaskCommentMapper(commentId);
		try {
			comment.setNumber(Integer.parseInt(taskAttribute.getId()));
		} catch (NumberFormatException e) {
			// ignore
		}
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

	public void applyTo(ITaskComment taskComment) {
		if (getAuthor() != null) {
			taskComment.setAuthor(getAuthor());
		}
		if (getCreationDate() != null) {
			taskComment.setCreationDate(getCreationDate());
		}
		if (getUrl() != null) {
			taskComment.setUrl(getUrl());
		}
		if (getText() != null) {
			taskComment.setText(getText());
		}
	}
}
