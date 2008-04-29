/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public class TaskAttributeProperties {

	//public boolean showInAttributesSection;

	public static TaskAttributeProperties from(TaskAttribute taskAttribute) {
		TaskAttributeProperties properties = new TaskAttributeProperties();
		properties.setKind(taskAttribute.getMetaData(TaskAttribute.META_ATTRIBUTE_KIND));
		properties.setLabel(taskAttribute.getMetaData(TaskAttribute.META_LABEL));
		properties.setReadOnly(Boolean.parseBoolean(taskAttribute.getMetaData(TaskAttribute.META_READ_ONLY)));
		properties.setShowInToolTip(Boolean.parseBoolean(taskAttribute.getMetaData(TaskAttribute.META_SHOW_IN_TOOL_TIP)));
		properties.setType(taskAttribute.getMetaData(TaskAttribute.META_ATTRIBUTE_TYPE));
		return properties;
	}

	public static TaskAttributeProperties defaults() {
		TaskAttributeProperties properties = new TaskAttributeProperties();
		properties.setLabel(null);
		properties.setKind(null);
		properties.setReadOnly(true);
		properties.setShowInToolTip(false);
		properties.setType(TaskAttribute.TYPE_SHORT_TEXT);
		return properties;
	}

	private String kind;

	private String label;

	private boolean readOnly;

	private boolean showInToolTip;

	private String type;

	public void applyTo(TaskAttribute attribute) {
		if (getKind() != null) {
			attribute.putMetaDataValue(TaskAttribute.META_ATTRIBUTE_KIND, getKind());
		} else {
			attribute.removeMetaDataValue(TaskAttribute.META_ATTRIBUTE_KIND);
		}
		attribute.putMetaDataValue(TaskAttribute.META_READ_ONLY, Boolean.toString(isReadOnly()));
		attribute.putMetaDataValue(TaskAttribute.META_SHOW_IN_TOOL_TIP, Boolean.toString(isShowInToolTip()));
		if (getType() != null) {
			attribute.putMetaDataValue(TaskAttribute.META_ATTRIBUTE_TYPE, getType());
		} else {
			attribute.removeMetaDataValue(TaskAttribute.META_ATTRIBUTE_TYPE);
		}
		if (getLabel() != null) {
			attribute.putMetaDataValue(TaskAttribute.META_LABEL, getLabel());
		} else {
			attribute.removeMetaDataValue(TaskAttribute.META_LABEL);
		}
	}

	public String getKind() {
		return kind;
	}

	public String getLabel() {
		return label;
	}

	public String getType() {
		return type;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public boolean isShowInToolTip() {
		return showInToolTip;
	}

	public TaskAttributeProperties setKind(String value) {
		this.kind = value;
		return this;
	}

	public TaskAttributeProperties setLabel(String value) {
		this.label = value;
		return this;
	}

	public TaskAttributeProperties setReadOnly(boolean value) {
		this.readOnly = value;
		return this;
	}

	public void setShowInToolTip(boolean showInToolTip) {
		this.showInToolTip = showInToolTip;
	}

	public TaskAttributeProperties setType(String value) {
		this.type = value;
		return this;
	}

}
