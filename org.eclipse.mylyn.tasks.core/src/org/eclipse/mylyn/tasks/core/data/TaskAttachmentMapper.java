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
import org.eclipse.mylyn.tasks.core.ITaskAttachment;

/**
 * @since 3.0
 * @author Steffen Pingel
 */
public class TaskAttachmentMapper {

	private IRepositoryPerson author;

	private String comment;

	private String contentType;

	private Date creationDate;

	private Boolean deprecated;

	private String description;

	private String fileName;

	private Long length;

	private Boolean patch;

	private String url;

	private String attachmentId;

	public TaskAttachmentMapper() {
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

	public Long getLength() {
		return length;
	}

	public String getUrl() {
		return url;
	}

	public Boolean isDeprecated() {
		return deprecated;
	}

	public Boolean isPatch() {
		return patch;
	}

	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
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

	public void setDeprecated(Boolean deprecated) {
		this.deprecated = deprecated;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public void setPatch(Boolean patch) {
		this.patch = patch;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public static TaskAttachmentMapper createFrom(TaskAttribute taskAttribute) {
		Assert.isNotNull(taskAttribute);
		TaskAttributeMapper mapper = taskAttribute.getTaskData().getAttributeMapper();
		TaskAttachmentMapper attachment = new TaskAttachmentMapper();
		attachment.setAttachmentId(mapper.getValue(taskAttribute));
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
		child = taskAttribute.getMappedAttribute(TaskAttribute.ATTACHMENT_DESCRIPTION);
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
		Assert.isNotNull(taskAttribute);
		TaskData taskData = taskAttribute.getTaskData();
		TaskAttributeMapper mapper = taskData.getAttributeMapper();
		TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_ATTACHMENT).applyTo(taskAttribute);
		if (getAttachmentId() != null) {
			mapper.setValue(taskAttribute, getAttachmentId());
		}
		if (getAuthor() != null) {
			TaskAttribute child = taskAttribute.createAttribute(TaskAttribute.ATTACHMENT_AUTHOR);
			TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_PERSON).applyTo(child);
			mapper.setRepositoryPerson(child, getAuthor());
		}
		if (getContentType() != null) {
			TaskAttribute child = taskAttribute.createAttribute(TaskAttribute.ATTACHMENT_CONTENT_TYPE);
			TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_SHORT_TEXT).applyTo(child);
			mapper.setValue(child, getContentType());
		}
		if (getCreationDate() != null) {
			TaskAttribute child = taskAttribute.createAttribute(TaskAttribute.ATTACHMENT_DATE);
			TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_DATE).applyTo(child);
			mapper.setDateValue(child, getCreationDate());
		}
		if (getDescription() != null) {
			TaskAttribute child = taskAttribute.createAttribute(TaskAttribute.ATTACHMENT_DESCRIPTION);
			TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_SHORT_TEXT).applyTo(child);
			mapper.setValue(child, getDescription());
		}
		if (getFileName() != null) {
			TaskAttribute child = taskAttribute.createAttribute(TaskAttribute.ATTACHMENT_FILENAME);
			TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_SHORT_TEXT).applyTo(child);
			mapper.setValue(child, getFileName());
		}
		if (isDeprecated() != null) {
			TaskAttribute child = taskAttribute.createAttribute(TaskAttribute.ATTACHMENT_IS_DEPRECATED);
			TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_BOOLEAN).applyTo(child);
			mapper.setBooleanValue(child, isDeprecated());
		}
		if (isPatch() != null) {
			TaskAttribute child = taskAttribute.createAttribute(TaskAttribute.ATTACHMENT_IS_PATCH);
			TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_BOOLEAN).applyTo(child);
			mapper.setBooleanValue(child, isPatch());
		}
		if (getLength() != null) {
			TaskAttribute child = taskAttribute.createAttribute(TaskAttribute.ATTACHMENT_SIZE);
			mapper.setLongValue(child, getLength());
		}
		if (getUrl() != null) {
			TaskAttribute child = taskAttribute.createAttribute(TaskAttribute.ATTACHMENT_URL);
			TaskAttributeProperties.defaults().setType(TaskAttribute.TYPE_URL).applyTo(child);
			mapper.setValue(child, getUrl());
		}
	}

	public void applyTo(ITaskAttachment taskAttachment) {
		Assert.isNotNull(taskAttachment);
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
		if (isDeprecated() != null) {
			taskAttachment.setDeprecated(isDeprecated());
		}
		if (isPatch() != null) {
			taskAttachment.setPatch(isPatch());
		}
		if (getLength() != null) {
			taskAttachment.setLength(getLength());
		}
		if (url != null) {
			taskAttachment.setUrl(getUrl());
		}
	}

}
