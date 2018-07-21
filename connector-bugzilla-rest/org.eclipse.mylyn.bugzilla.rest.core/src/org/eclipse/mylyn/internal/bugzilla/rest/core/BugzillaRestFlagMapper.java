/*******************************************************************************
 * Copyright (c) 2016 Frank Becker and others.
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

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.data.TaskData;

import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonWriter;

public class BugzillaRestFlagMapper {

	private String requestee;

	private String setter;

	@SerializedName("status")
	private String state;

	private String name;

	@SerializedName("id")
	private int number;

	private String description;

	@SerializedName("type_id")
	private int typeId;

	@SerializedName("creation_date")
	private String creationDate;

	@SerializedName("modification_date")
	private String modificationDate;

	public BugzillaRestFlagMapper() {
	}

	public String getRequestee() {
		return requestee;
	}

	public void setRequestee(String requestee) {
		this.requestee = requestee;
	}

	public String getSetter() {
		return setter;
	}

	public void setSetter(String setter) {
		this.setter = setter;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(String modificationDate) {
		this.modificationDate = modificationDate;
	}

	public void applyTo(TaskAttribute taskAttribute) {
		Assert.isNotNull(taskAttribute);
		TaskData taskData = taskAttribute.getTaskData();
		TaskAttributeMapper mapper = taskData.getAttributeMapper();
		TaskAttributeMetaData meta = taskAttribute.getMetaData().defaults();
		meta.setType(IBugzillaRestConstants.EDITOR_TYPE_FLAG);
		meta.setLabel(getDescription());
		meta.setKind(IBugzillaRestConstants.KIND_FLAG);
		meta.setReadOnly(false);

		if (getNumber() != 0) {
			TaskAttribute child = taskAttribute.createMappedAttribute("number"); //$NON-NLS-1$
			child.getMetaData().defaults().setType(TaskAttribute.TYPE_INTEGER);
			mapper.setIntegerValue(child, getNumber());
		}
		if (getRequestee() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute("requestee"); //$NON-NLS-1$
			child.getMetaData().defaults().setType(TaskAttribute.TYPE_SHORT_TEXT);
			mapper.setValue(child, getRequestee());
		}
		if (getSetter() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute("setter"); //$NON-NLS-1$
			child.getMetaData().defaults().setType(TaskAttribute.TYPE_SHORT_TEXT);
			mapper.setValue(child, getSetter());
		}
		if (getState() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute("state"); //$NON-NLS-1$
			child.getMetaData().defaults().setType(TaskAttribute.TYPE_SINGLE_SELECT);
			child.getMetaData().setLabel(getName());
			child.getMetaData().setReadOnly(false);
			mapper.setValue(child, getState());
			taskAttribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, "state"); //$NON-NLS-1$
		}
		if (getTypeId() != 0) {
			TaskAttribute child = taskAttribute.createMappedAttribute("typeId"); //$NON-NLS-1$
			child.getMetaData().defaults().setType(TaskAttribute.TYPE_INTEGER);
			mapper.setIntegerValue(child, getTypeId());
		}
		if (getCreationDate() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute("creationDate"); //$NON-NLS-1$
			child.getMetaData().defaults().setType(TaskAttribute.TYPE_SHORT_TEXT);
			mapper.setValue(child, getCreationDate());
		}
		if (getModificationDate() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute("modificationDate"); //$NON-NLS-1$
			child.getMetaData().defaults().setType(TaskAttribute.TYPE_SHORT_TEXT);
			mapper.setValue(child, getModificationDate());
		}
	}

	public static BugzillaRestFlagMapper createFrom(TaskAttribute taskAttribute) {
		Assert.isNotNull(taskAttribute);
		TaskAttributeMapper mapper = taskAttribute.getTaskData().getAttributeMapper();
		BugzillaRestFlagMapper flag = new BugzillaRestFlagMapper();
		flag.setDescription(taskAttribute.getMetaData().getLabel());
		TaskAttribute child = taskAttribute.getMappedAttribute("number");
		if (child != null) {
			flag.setNumber(mapper.getIntegerValue(child));
		}
		child = taskAttribute.getMappedAttribute("requestee");
		if (child != null) {
			flag.setRequestee(mapper.getValue(child));
		}
		child = taskAttribute.getMappedAttribute("setter");
		if (child != null) {
			flag.setSetter(mapper.getValue(child));
		}
		child = taskAttribute.getMappedAttribute("state");
		if (child != null) {
			flag.setName(child.getMetaData().getLabel());
			flag.setState(mapper.getValue(child));
		}
		child = taskAttribute.getMappedAttribute("typeId");
		if (child != null) {
			flag.setTypeId(mapper.getIntegerValue(child));
		}
		child = taskAttribute.getMappedAttribute("creationDate");
		if (child != null) {
			flag.setCreationDate(mapper.getValue(child));
		}
		child = taskAttribute.getMappedAttribute("modificationDate");
		if (child != null) {
			flag.setModificationDate(mapper.getValue(child));
		}

		return flag;
	}

	public void applyTo(JsonWriter out) throws IOException {
		out.beginObject();
		if (getNumber() != 0) {
			out.name("id").value(getNumber());
		} else {
			out.name("name").value(getName());
			out.name("new").value(true);
		}
		out.name("status").value(StringUtils.defaultIfBlank(getState(), "X"));
		out.name("requestee").value(getRequestee());
		out.endObject();
	}
}
