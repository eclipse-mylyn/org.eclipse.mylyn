/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import java.util.Date;

import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;

/**
 * @since 3.0
 * @author Steffen Pingel
 */
public class TaskAttachmentMapper {

	private IRepositoryPerson author;

	private String comment;

	private String contentType;

	private Date creationDate;

	private boolean deprecated;

	private String description;

	private String fileName;

	private long length;

	private boolean patch;

	private String url;

	private final String attachmentId;

	public TaskAttachmentMapper(String attachmentId) {
		this.attachmentId = attachmentId;
	}

	public String getAttachmentId() {
		return attachmentId;
	}

	public IRepositoryPerson getAuthor() {
		return author;
	}

	public String getComment() {
		return comment;
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

	public String getUrl() {
		return url;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	public boolean isPatch() {
		return patch;
	}

	public void setAuthor(IRepositoryPerson author) {
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

	public static TaskAttachmentMapper createFrom(TaskAttribute taskAttribute) {
		TaskAttributeMapper mapper = taskAttribute.getTaskData().getAttributeMapper();
		String attachmentId = mapper.getValue(taskAttribute);
		TaskAttachmentMapper attachment = new TaskAttachmentMapper(attachmentId);
		TaskAttribute child = taskAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_AUTHOR);
		if (child != null) {
			attachment.setAuthor(mapper.getRepositoryPerson(child));
		}
		child = taskAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_CONTENT_TYPE);
		if (child != null) {
			attachment.setContentType(mapper.getValue(child));
		}
		child = taskAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_DATE);
		if (child != null) {
			attachment.setCreationDate(mapper.getDateValue(child));
		}
		child = taskAttribute.getMappedAttribute(TaskAttribute.DESCRIPTION);
		if (child != null) {
			attachment.setDescription(mapper.getValue(child));
		}
		child = taskAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_FILENAME);
		if (child != null) {
			attachment.setFileName(mapper.getValue(child));
		}
		child = taskAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_IS_DEPRECATED);
		if (child != null) {
			attachment.setDeprecated(mapper.getBooleanValue(child));
		}
		child = taskAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_IS_PATCH);
		if (child != null) {
			attachment.setPatch(mapper.getBooleanValue(child));
		}
		child = taskAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_SIZE);
		if (child != null) {
			Long value = mapper.getLongValue(child);
			if (value != null) {
				attachment.setLength(value);
			}
		}
		child = taskAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_URL);
		if (child != null) {
			attachment.setUrl(mapper.getValue(child));
		}
		return attachment;
	}

	public void applyTo(TaskAttribute taskAttribute) {
		TaskData taskData = taskAttribute.getTaskData();
		TaskAttributeMapper mapper = taskData.getAttributeMapper();

		mapper.setValue(taskAttribute, getAttachmentId());
		TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_ATTACHMENT).applyTo(taskAttribute);

		TaskAttribute child;
		if (getAuthor() != null) {
			child = taskAttribute.createAttribute(TaskAttribute.ATTACHMENT_AUTHOR);
			TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_PERSON).applyTo(child);
			mapper.setRepositoryPerson(child, getAuthor());
		}
		if (getContentType() != null) {
			child = taskAttribute.createAttribute(TaskAttribute.ATTACHMENT_CONTENT_TYPE);
			TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_SHORT_TEXT).applyTo(child);
			mapper.setValue(child, getContentType());
		}
		if (getCreationDate() != null) {
			child = taskAttribute.createAttribute(TaskAttribute.ATTACHMENT_DATE);
			TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_DATE).applyTo(child);
			mapper.setDateValue(child, getCreationDate());
		}
		if (getDescription() != null) {
			child = taskAttribute.createAttribute(TaskAttribute.DESCRIPTION);
			TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_SHORT_TEXT).applyTo(child);
			mapper.setValue(child, getDescription());
		}
		if (getFileName() != null) {
			child = taskAttribute.createAttribute(TaskAttribute.ATTACHMENT_FILENAME);
			TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_SHORT_TEXT).applyTo(child);
			mapper.setValue(child, getFileName());
		}
		child = taskAttribute.createAttribute(TaskAttribute.ATTACHMENT_IS_DEPRECATED);
		TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_BOOLEAN).applyTo(child);
		mapper.setBooleanValue(child, isDeprecated());
		child = taskAttribute.createAttribute(TaskAttribute.ATTACHMENT_IS_PATCH);
		TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_BOOLEAN).applyTo(child);
		mapper.setBooleanValue(child, isPatch());
		child = taskAttribute.createAttribute(TaskAttribute.ATTACHMENT_SIZE);
		mapper.setLongValue(child, getLength());
		if (getUrl() != null) {
			child = taskAttribute.createAttribute(TaskAttribute.ATTACHMENT_URL);
			TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_URL).applyTo(child);
			mapper.setValue(child, getUrl());
		}
	}

	public void applyTo(ITaskAttachment taskAttachment) {
		if (getAuthor() != null) {
			taskAttachment.setAuthor(getAuthor());
		}
		if (getContentType() != null) {
			taskAttachment.setContentType(getContentType());
		}
		if (getCreationDate() != null) {
			taskAttachment.setCreationDate(getCreationDate());
		}
		if (getDescription() != null) {
			taskAttachment.setDescription(getDescription());
		}
		if (getFileName() != null) {
			taskAttachment.setFileName(getFileName());
		}
		taskAttachment.setDeprecated(isDeprecated());
		taskAttachment.setPatch(isPatch());
		taskAttachment.setLength(getLength());
		if (url != null) {
			taskAttachment.setUrl(getUrl());
		}
	}

}
