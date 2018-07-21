/*******************************************************************************
 * Copyright (c) 2009, 2012 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.util.Date;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Frank Becker
 */
public class BugzillaAttachmentMapper extends TaskAttachmentMapper {
	private String token;

	private Date deltaDate;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public void applyTo(TaskAttribute taskAttribute) {
		Assert.isNotNull(taskAttribute);
		TaskData taskData = taskAttribute.getTaskData();
		TaskAttributeMapper mapper = taskData.getAttributeMapper();
		taskAttribute.getMetaData().defaults().setType(TaskAttribute.TYPE_ATTACHMENT);
		if (getAttachmentId() != null) {
			mapper.setValue(taskAttribute, getAttachmentId());
		}
		if (getAuthor() != null && getAuthor().getPersonId() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute(TaskAttribute.ATTACHMENT_AUTHOR);
			TaskAttributeMetaData defaults = child.getMetaData().defaults();
			defaults.setType(TaskAttribute.TYPE_PERSON);
			defaults.setLabel(Messages.BugzillaAttachmentMapper_Author);
			mapper.setRepositoryPerson(child, getAuthor());
		}
		if (getContentType() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute(TaskAttribute.ATTACHMENT_CONTENT_TYPE);
			TaskAttributeMetaData defaults = child.getMetaData().defaults();
			defaults.setType(TaskAttribute.TYPE_SHORT_TEXT);
			defaults.setLabel(Messages.BugzillaAttachmentMapper_Content_Type);
			mapper.setValue(child, getContentType());
		}
		if (getCreationDate() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute(TaskAttribute.ATTACHMENT_DATE);
			TaskAttributeMetaData defaults = child.getMetaData().defaults();
			defaults.setType(TaskAttribute.TYPE_DATE);
			defaults.setLabel(Messages.BugzillaAttachmentMapper_Creation_Date);
			mapper.setDateValue(child, getCreationDate());
		}
		if (getDescription() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute(TaskAttribute.ATTACHMENT_DESCRIPTION);
			TaskAttributeMetaData defaults = child.getMetaData().defaults();
			defaults.setType(TaskAttribute.TYPE_SHORT_TEXT);
			defaults.setLabel(Messages.BugzillaAttachmentMapper_Description);
			mapper.setValue(child, getDescription());
		}
		if (getFileName() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute(TaskAttribute.ATTACHMENT_FILENAME);
			TaskAttributeMetaData defaults = child.getMetaData().defaults();
			defaults.setType(TaskAttribute.TYPE_SHORT_TEXT);
			defaults.setLabel(Messages.BugzillaAttachmentMapper_Filename);
			mapper.setValue(child, getFileName());
		}
		if (isDeprecated() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute(TaskAttribute.ATTACHMENT_IS_DEPRECATED);
			TaskAttributeMetaData defaults = child.getMetaData().defaults();
			defaults.setType(TaskAttribute.TYPE_BOOLEAN);
			defaults.setLabel(Messages.BugzillaAttachmentMapper_obsolete);
			mapper.setBooleanValue(child, isDeprecated());
		}
		if (isPatch() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute(TaskAttribute.ATTACHMENT_IS_PATCH);
			TaskAttributeMetaData defaults = child.getMetaData().defaults();
			defaults.setType(TaskAttribute.TYPE_BOOLEAN);
			defaults.setLabel(Messages.BugzillaAttachmentMapper_patch);
			mapper.setBooleanValue(child, isPatch());
		}
		if (getLength() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute(TaskAttribute.ATTACHMENT_SIZE);
			TaskAttributeMetaData defaults = child.getMetaData().defaults();
			defaults.setType(TaskAttribute.TYPE_SHORT_TEXT);
			defaults.setLabel(Messages.BugzillaAttachmentMapper_Size);
			mapper.setLongValue(child, getLength());
		}
		if (getUrl() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute(TaskAttribute.ATTACHMENT_URL);
			TaskAttributeMetaData defaults = child.getMetaData().defaults();
			defaults.setType(TaskAttribute.TYPE_URL);
			defaults.setLabel(Messages.BugzillaAttachmentMapper_URL);
			mapper.setValue(child, getUrl());
		}
		if (getToken() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute(BugzillaAttribute.TOKEN.getKey());
			TaskAttributeMetaData defaults = child.getMetaData().defaults();
			defaults.setType(TaskAttribute.TYPE_SHORT_TEXT);
			defaults.setLabel(Messages.BugzillaAttachmentMapper_Token);
			mapper.setValue(child, getToken());
		}
		if (getDeltaDate() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute(BugzillaAttribute.DELTA_TS.getKey());
			TaskAttributeMetaData defaults = child.getMetaData().defaults();
			defaults.setType(TaskAttribute.TYPE_DATE);
			defaults.setLabel(Messages.BugzillaAttachmentMapper_DELTA_TS);
			mapper.setDateValue(child, getDeltaDate());
		}
	}

	public static BugzillaAttachmentMapper createFrom(TaskAttribute taskAttribute) {
		Assert.isNotNull(taskAttribute);
		TaskAttributeMapper mapper = taskAttribute.getTaskData().getAttributeMapper();
		BugzillaAttachmentMapper attachment = new BugzillaAttachmentMapper();
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
		child = taskAttribute.getMappedAttribute(BugzillaAttribute.TOKEN.getKey());
		if (child != null) {
			attachment.setToken(mapper.getValue(child));
		}
		child = taskAttribute.getMappedAttribute(BugzillaAttribute.DELTA_TS.getKey());
		if (child != null) {
			attachment.setDeltaDate(mapper.getDateValue(child));
		}
		return attachment;
	}

	public Date getDeltaDate() {
		return deltaDate;
	}

	public void setDeltaDate(Date deltaDate) {
		this.deltaDate = deltaDate;
	}
}
