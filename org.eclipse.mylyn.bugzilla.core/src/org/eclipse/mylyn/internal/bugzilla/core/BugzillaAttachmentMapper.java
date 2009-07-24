/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Frank Becker
 */
public class BugzillaAttachmentMapper extends TaskAttachmentMapper {
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public void applyTo(TaskAttribute taskAttribute) {
		// ignore
		super.applyTo(taskAttribute);

		Assert.isNotNull(taskAttribute);
		TaskData taskData = taskAttribute.getTaskData();
		TaskAttributeMapper mapper = taskData.getAttributeMapper();

		if (getToken() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute(BugzillaAttribute.TOKEN.getKey());
			child.getMetaData().defaults().setType(TaskAttribute.TYPE_SHORT_TEXT);
			mapper.setValue(child, getToken());
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
		return attachment;
	}

}
