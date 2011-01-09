/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * Enum holding the mapping of gerrit task attributes to mylyn task attributes.
 * 
 * @author Mikael Kober
 * @author Thomas Westling
 */
public enum GerritAttribute {

	ID(GerritConstants.ATTRIBUTE_ID, "ID", TaskAttribute.TASK_KEY, TaskAttribute.TYPE_SHORT_TEXT, true),
	//	BRANCH(GerritConstants.ATTRIBUTE_BRANCH, "Branch", TaskAttribute.USER_ASSIGNED, TaskAttribute.TYPE_PERSON, true),
	OWNER(GerritConstants.ATTRIBUTE_OWNER, "Owner", TaskAttribute.USER_ASSIGNED, TaskAttribute.TYPE_PERSON, true), //
	PROJECT(GerritConstants.ATTRIBUTE_PROJECT, "Project", TaskAttribute.PRODUCT, TaskAttribute.TYPE_SHORT_TEXT, true), //
	STATUS(GerritConstants.ATTRIBUTE_STATUS, "Status", TaskAttribute.STATUS, TaskAttribute.TYPE_SHORT_TEXT, false), //
	SUMMARY(GerritConstants.ATTRIBUTE_SUMMARY, "Title", TaskAttribute.SUMMARY, TaskAttribute.TYPE_SHORT_RICH_TEXT, true), //
	URL(GerritConstants.ATTRIBUTE_URL, "URL", TaskAttribute.TASK_URL, TaskAttribute.TYPE_URL, true), //
	UPLOADED(GerritConstants.ATTRIBUTE_UPLOADED, "Uploaded", TaskAttribute.DATE_CREATION, TaskAttribute.TYPE_DATE, true), //
	UPDATED(GerritConstants.ATTRIBUTE_UPDATED, "Updated", TaskAttribute.DATE_MODIFICATION, TaskAttribute.TYPE_DATE,
			true), //
	DESCRIPTION(GerritConstants.ATTRIBUTE_DESCRIPTION, "Description", TaskAttribute.DESCRIPTION,
			TaskAttribute.TYPE_LONG_RICH_TEXT, true);

	private final String gerritKey;

	private final String prettyName;

	private final String taskKey;

	private final String type;

	private final boolean readOnly;

	/**
	 * Constructor.
	 * 
	 * @param gerritKey
	 * @param prettyName
	 * @param taskKey
	 * @param type
	 * @param readOnly
	 */
	GerritAttribute(String gerritKey, String prettyName, String taskKey, String type, boolean readOnly) {
		this.gerritKey = gerritKey;
		this.taskKey = taskKey;
		this.prettyName = prettyName;
		this.type = type;
		this.readOnly = readOnly;
	}

	/**
	 * Get the gerrit specifiv key.
	 * 
	 * @return the gerrit specific key
	 */
	public String getGerritKey() {
		return gerritKey;
	}

	/**
	 * Get the attribute kind
	 * 
	 * @return attribute kind
	 */
	public String getKind() {
		return (this == DESCRIPTION) ? null : TaskAttribute.KIND_DEFAULT;
	}

	/**
	 * Get the task key.
	 * 
	 * @return the mylyn task key
	 */
	public String getTaskKey() {
		return taskKey;
	}

	/**
	 * Get the attribute type.
	 * 
	 * @return attribute type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Check if this is a read only attribute.
	 * 
	 * @return true if the attribute is read only, false otherwise
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/* (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString() */
	@Override
	public String toString() {
		return prettyName;
	}

}