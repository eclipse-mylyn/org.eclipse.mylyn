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

package org.eclipse.mylar.bugzilla.core;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class representing a Bugzilla report attribute that can be changed on the
 * server.
 */
public class Attribute implements Serializable {
	/** Automatically generated serialVersionUID */
	private static final long serialVersionUID = 3257009873370757424L;

	private boolean hidden = false;

	/** Attribute name */
	private String name;

	/** Name of the option used when updating the attribute on the server */
	private String parameterName;

	/** Legal values of the attribute */
	private LinkedHashMap<String, String> optionValues;

	/**
	 * Attribute's value (input field or selected option; value that is saved or
	 * from the server)
	 */
	private String value;

	/** Attributes new Value (value chosen in submit editor) */
	private String newValue;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            The name of the attribute
	 */
	public Attribute(String name) {
		// initialize the name and its legal values
		this.name = name;
		optionValues = new LinkedHashMap<String, String>();
	}

	/**
	 * Get the attribute's name
	 * 
	 * @return The name of the attribute
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get name of the option used when updating the attribute on the server
	 * 
	 * @return The name of the option for server updates
	 */
	public String getParameterName() {
		return parameterName;
	}

	/**
	 * Get whether the attribute can be edited by the used
	 * 
	 * @return <code>true</code> if the attribute can be edited by the user
	 */
	public boolean isEditable() {
		return optionValues.size() > 0;
	}

	/**
	 * Get the legal values for the option
	 * 
	 * @return The <code>Map</code> of legal values for the option.
	 */
	public Map<String, String> getOptionValues() {
		return optionValues;
	}

	/**
	 * Get the value of the attribute
	 * 
	 * @return A <code>String</code> of the attributes value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the value of the attribute
	 * 
	 * @param value
	 *            The new value of the attribute
	 */
	public void setValue(String value) {
		this.value = value;
		newValue = value;
	}

	/**
	 * Set the new value of the attribute
	 * 
	 * @param newVal
	 *            The new value of the attribute
	 */
	public void setNewValue(String newVal) {
		newValue = newVal;
	}

	/**
	 * Get the new value for the attribute
	 * 
	 * @return The new value
	 */
	public String getNewValue() {
		return newValue;
	}

	/**
	 * Sets the name of the option used when updating the attribute on the
	 * server
	 * 
	 * @param parameterName
	 *            The name of the option when updating from the server
	 */
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

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
		return "(" + getName() + " : " + getValue() + ")";
	}
}
