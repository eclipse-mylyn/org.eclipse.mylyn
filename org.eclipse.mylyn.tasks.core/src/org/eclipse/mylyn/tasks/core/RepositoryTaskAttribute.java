/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasks.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing a report attribute
 * 
 * @author Rob Elves
 * 
 */
public class RepositoryTaskAttribute implements Serializable {

	private static final long serialVersionUID = 5548885751645139746L;

	/**
	 * Key for the author of a comment.
	 * 
	 * TODO remove this key: Trac uses owner to denote the assignee of a ticket
	 * and AbstractRepository has a getOwner() method which is confusing
	 */
	public static final String USER_OWNER = "task.common.user.owner";

	public static final String USER_CC = "task.common.user.cc";

	public static final String COMMENT_NEW = "task.common.comment.new";

	public static final String COMMENT_TEXT = "task.common.comment.text";

	public static final String COMMENT_DATE = "task.common.comment.date";

	// THIS IS NOT BEING USED BUT RATHER "USER_OWNER" for comments
	// TODO: use this taskId instead
	public static final String COMMENT_AUTHOR = "task.common.comment.author";

	public static final String DESCRIPTION = "task.common.description";

	public static final String ATTACHMENT_ID = "task.common.attachment.id";

	public static final String ATTACHMENT_TYPE = "task.common.attachment.type";

	public static final String ATTACHMENT_CTYPE = "task.common.attachment.ctype";

	public static final String ATTACHMENT_DATE = "task.common.attachment.date";

	public static final String ATTACHMENT_URL = "task.common.attachment.url";

	public static final String ATTACHMENT_FILENAME = "filename";

	public static final String USER_ASSIGNED = "task.common.user.assigned";

	public static final String RESOLUTION = "task.common.resolution";

	public static final String STATUS = "task.common.status";

	public static final String PRIORITY = "task.common.priority";

	public static final String DATE_MODIFIED = "task.common.date.modified";

	public static final String USER_REPORTER = "task.common.user.reporter";

	public static final String SUMMARY = "task.common.summary";

	public static final String PRODUCT = "task.common.product";

	public static final String DATE_CREATION = "task.common.date.created";

	public static final String KEYWORDS = "task.common.keywords";

	/**
	 * Boolean attribute. If true, repository user needs to be added to the cc
	 * list.
	 */
	public static final String ADD_SELF_CC = "task.common.addselfcc";

	public static final String NEW_CC = "task.common.newcc";

	public static final String REMOVE_CC = "task.common.removecc";
	
	public static final String TASK_KEY = "task.common.key";

	/**
	 * String constant used to represent true for boolean attributes.
	 */
	public static final String TRUE = "1";

	/**
	 * String constant used to represent false for boolean attributes.
	 */
	public static final String FALSE = "0";

	private boolean hidden = false;

	private boolean isReadOnly = false;

	/** Attribute pretty printing name */
	private String name;

	/** Name of the option used when updating the attribute on the server */
	private String key;

	/** Option parameters */
	private Map<String, String> optionParameters;

	/** Ordered list of legal attribute values */
	private List<String> options;

	/**
	 * Attribute's values (selected or added)
	 */
	private List<String> values = new ArrayList<String>();

	private Map<String, String> metaData = new HashMap<String, String>();

	public RepositoryTaskAttribute(String key, String name, boolean hidden) {
		this.key = key;
		this.name = name;
		this.hidden = hidden;
		this.options = new ArrayList<String>();
		optionParameters = new HashMap<String, String>();
	}

	public String getName() {
		return name;
	}

	public String getID() {
		return key;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	public void setReadOnly(boolean readonly) {
		this.isReadOnly = readonly;
	}

	public String getOptionParameter(String option) {
		return optionParameters.get(option);
	}

	public List<String> getOptions() {
		return options;
	}

	public String getValue() {
		if (values.size() > 0) {
			return values.get(0);
		} else {
			return "";
		}
	}

	public List<String> getValues() {
		return values;
	}

	public void setValue(String value) {
		if (values.size() > 0) {
			values.set(0, value);
		} else {
			values.add(value);
		}
	}

	public void setValues(List<String> values) {
		this.values.clear();
		this.values.addAll(values);
	}

	public void addValue(String value) {
		values.add(value);
	}

	public void removeValue(String value) {
		if (values.contains(value)) {
			values.remove(values.indexOf(value));
		}
	}

	public void clearValues() {
		values.clear();
	}

	/**
	 * Adds an attribute option value
	 * 
	 * @param readableValue
	 *            The value displayed on the screen
	 * @param parameterValue
	 *            The option value used when sending the form to the server
	 */
	public void addOption(String readableValue, String parameterValue) {
		options.add(readableValue);
		optionParameters.put(readableValue, parameterValue);
	}

	public boolean hasOptions() {
		return options.size() > 0;
	}

	public void clearOptions() {
		options.clear();
		optionParameters.clear();
	}

	/**
	 * Determine if the field was hidden or not
	 * 
	 * @return True if the field was hidden
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * Set whether the field was hidden in the bug
	 * 
	 * @param b
	 *            Whether the field was hidden or not
	 */
	public void setHidden(boolean b) {
		hidden = b;
	}

	@Override
	public String toString() {
		return getID() + ":" + values;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final RepositoryTaskAttribute other = (RepositoryTaskAttribute) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	public void putMetaDataValue(String key, String value) {
		metaData.put(key, value);
	}

	public String getMetaDataValue(String key) {
		return metaData.get(key);
	}

	public void removeMetaDataValue(String key) {
		metaData.remove(key);
	}

}
