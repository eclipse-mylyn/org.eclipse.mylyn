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

package org.eclipse.mylar.internal.tasklist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing a report attribute 
 * 
 * @author Rob Elves
 */
public class RepositoryTaskAttribute implements Serializable {
	
	private static final long serialVersionUID = 6959987055086133307L;

	public static final String USER_OWNER = "task.common.user.owner";

	public static final String USER_CC = "task.common.user.cc";
	
	public static final String COMMENT_TEXT = "task.common.comment.text";
	
	public static final String COMMENT_DATE = "task.common.comment.date";

	public static final String DESCRIPTION = "task.common.description";

	public static final String ATTACHMENT_ID = "task.common.attachment.id";

	public static final String ATTACHMENT_TYPE = "task.common.attachment.type";

	public static final String ATTACHMENT_CTYPE = "task.common.attachment.ctype";
	
	public static final String ATTACHMENT_DATE = "task.common.attachment.date";

	public static final String USER_ASSIGNED = "task.common.user.assigned";

	public static final String RESOLUTION = "task.common.resolution";

	public static final String STATUS = "task.common.status";

	public static final String DATE_MODIFIED = "task.common.date.modified";

	public static final String USER_REPORTER = "task.common.user.reporter";

	public static final String SUMMARY = "task.common.summary";

	public static final String PRODUCT = "task.common.product";

	public static final String DATE_CREATION = "task.common.date.created";

	public static final String KEYWORDS = "task.common.keywords";
	
	private boolean hidden = false;
	
	private boolean isReadOnly = false;
	
	/** Attribute pretty printing name */
	private String name;

	/** Name of the option used when updating the attribute on the server */
	private String key;

	/** Legal values of the attribute */
	private LinkedHashMap<String, String> optionValues;

	/**
	 * Attribute's values (selected or added) 
	 */
	private List<String> values = new ArrayList<String>();

//	public RepositoryTaskAttribute(String key, ) {
//		this(element.toString(), element.isHidden());
//		setID(element.getKeyString());
//		setReadOnly(element.isReadOnly());
//	}
	
	public RepositoryTaskAttribute(String key, String name, boolean hidden) {
		this.key = key;
		this.name = name;
		this.hidden = hidden;
		optionValues = new LinkedHashMap<String, String>();
	}

	public String getName() {
		return name;
	}

	public String getID() {
		return key;
	}

	public boolean isReadOnly() {
		return isReadOnly ;
	}
	
	public void setReadOnly(boolean readonly) {
		this.isReadOnly = readonly;
	}

	public Map<String, String> getOptionValues() {
		return optionValues;
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
		if(values.size() > 0) {
			values.set(0, value);
		} else {
			values.add(value);
		}
	}

	public void setValues(List<String> values) {
		values.clear();
		values.addAll(values);
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

//	/**
//	 * Sets the name of the option used when updating the attribute on the
//	 * server
//	 * 
//	 * @param parameterName
//	 *            The name of the option when updating from the server
//	 */
//	public void setID(String parameterName) {
//		this.id = parameterName;
//	}

	/**
	 * Adds an attribute option value
	 * 
	 * @param readableValue
	 *            The value displayed on the screen
	 * @param parameterValue
	 *            The option value used when sending the form to the server
	 */
	public void addOptionValue(String readableValue, String parameterValue) {
		optionValues.put(readableValue, parameterValue);
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

	public String toString() {
		return getValue();
	}
	
	public boolean hasOptions() {
		return optionValues.size() > 0;
	}

	public void clearOptions() {
		optionValues.clear();
	}
}
