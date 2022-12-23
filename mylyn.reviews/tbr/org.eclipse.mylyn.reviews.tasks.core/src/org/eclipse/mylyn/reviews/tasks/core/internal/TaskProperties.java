/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.tasks.core.internal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.reviews.tasks.core.Attachment;
import org.eclipse.mylyn.reviews.tasks.core.ITaskProperties;
import org.eclipse.mylyn.reviews.tasks.core.TaskComment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
/**
 * 
 * @author mattk
 *
 */
public class TaskProperties implements ITaskProperties {
	private ITaskDataManager manager;
	private TaskData taskData;

	public TaskProperties(ITaskDataManager manager, TaskData taskData) {
		Assert.isNotNull(manager);
		Assert.isNotNull(taskData);
		this.taskData = taskData;
		this.manager = manager;
	}

	public String getDescription() {
		return taskData.getRoot().getMappedAttribute(TaskAttribute.DESCRIPTION)
				.getValue();
	}

	public String getAssignedTo() {
		return taskData.getRoot()
				.getMappedAttribute(TaskAttribute.USER_ASSIGNED).getValue();
	}

	public List<Attachment> getAttachments() {
		List<TaskAttribute> attachments = taskData.getAttributeMapper()
				.getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT);
		ArrayList<Attachment> list = new ArrayList<Attachment>();
		for (TaskAttribute att : attachments) {
			list.add(new Attachment(
					this,
					attributeValue(att, TaskAttribute.ATTACHMENT_FILENAME),
					attributeValue(att, TaskAttribute.ATTACHMENT_AUTHOR),
					attributeValue(att, TaskAttribute.ATTACHMENT_DATE),
					taskData.getAttributeMapper()
							.getBooleanValue(
									att.getMappedAttribute(TaskAttribute.ATTACHMENT_IS_PATCH)),
					attributeValue(att, TaskAttribute.ATTACHMENT_URL)));
		}
		return list;
	}

	private String attributeValue(TaskAttribute parent, String attribute) {
		return parent.getMappedAttribute(attribute).getValue();
	}

	public List<TaskComment> getComments() {
		List<TaskComment> comments = new ArrayList<TaskComment>();
		for (TaskAttribute attr : taskData.getRoot().getAttributes().values()) {
			if (attr.getId().startsWith(TaskAttribute.PREFIX_COMMENT)) {

				TaskComment comment = new TaskComment();
				comment.setAuthor(attr.getMappedAttribute(
						TaskAttribute.COMMENT_AUTHOR).getValue());
				comment.setText(attr.getMappedAttribute(
						TaskAttribute.COMMENT_TEXT).getValue());
				try {
					comment.setDate(parseDate(attr.getMappedAttribute(
							TaskAttribute.COMMENT_DATE).getValue()));
				} catch (ParseException ex) {
					ex.printStackTrace();
				}
				comments.add(comment);
			}
		}
		return comments;
	}

	public String getNewCommentText() {
		TaskAttribute attribute = this.taskData.getRoot().getMappedAttribute(
				TaskAttribute.COMMENT_NEW);
		if (attribute == null)
			return null;

		return attribute.getValue();
	}

	public void setNewCommentText(String value) {
		TaskAttribute attribute = this.taskData.getRoot().getMappedAttribute(
				TaskAttribute.COMMENT_NEW);
		if (attribute == null) {
			attribute = this.taskData.getRoot().createMappedAttribute(
					TaskAttribute.COMMENT_NEW);
		}
		attribute.setValue(value);
	}

	private Date parseDate(String dateString) throws ParseException {
		return new SimpleDateFormat("yyyy-mm-dd hh:MM").parse(dateString);
	}

	public static ITaskProperties fromTaskData(ITaskDataManager manager,
			TaskData taskData) {
		return new TaskProperties(manager, taskData);
	}

	public ITaskProperties loadFor(String taskId) throws CoreException {
		TaskData td = manager.getTaskData(
				new TaskRepository(taskData.getConnectorKind(), taskData
						.getRepositoryUrl()), taskId);
		return new TaskProperties(manager, td);
	}

	public String getTaskId() {
		return taskData.getTaskId();
	}

	public void setDescription(String description) {
		setValue(TaskAttribute.DESCRIPTION, description);
	}

	private void setValue(String mappedAttributeName, String value) {
		TaskAttribute attribute = taskData.getRoot().getMappedAttribute(
				mappedAttributeName);
		if (attribute == null) {
			attribute = taskData.getRoot().createMappedAttribute(
					mappedAttributeName);
		}
		attribute.setValue(value);
	}

	public void setSummary(String summary) {
		taskData.getRoot().getMappedAttribute(TaskAttribute.SUMMARY)
				.setValue(summary);
	}

	public void setAssignedTo(String assignee) {
		taskData.getRoot().getMappedAttribute(TaskAttribute.USER_ASSIGNED)
				.setValue(assignee);
	}

	public String getRepositoryUrl() {
		return taskData.getRepositoryUrl();
	}

	public String getReporter() {
		return attributeValue(taskData.getRoot(),TaskAttribute.USER_REPORTER);
	}
}