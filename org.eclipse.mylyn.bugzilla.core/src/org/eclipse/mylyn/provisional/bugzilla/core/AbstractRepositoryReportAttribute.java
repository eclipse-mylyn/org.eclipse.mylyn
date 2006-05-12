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

package org.eclipse.mylar.provisional.bugzilla.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing a report attribute may contain child attributes
 * 
 * @author Rob Elves
 */
public abstract class AbstractRepositoryReportAttribute implements Serializable {

	private boolean hidden = false;
	private boolean isReadOnly = false;;
	
	/** Attribute pretty printing name */
	private String name;

	/** Name of the option used when updating the attribute on the server */
	private String id;

	/** Legal values of the attribute */
	private LinkedHashMap<String, String> optionValues;

	/**
	 * Attribute's values (selected or added) 
	 */
	private List<String> values = new ArrayList<String>();

	public AbstractRepositoryReportAttribute(String name, boolean hidden) {
		// initialize the name and its legal values
		this.name = name;
		this.hidden = hidden;
		optionValues = new LinkedHashMap<String, String>();
	}

	public String getName() {
		return name;
	}

	public String getID() {
		return id;
	}

	public boolean isReadOnly() {
		return isReadOnly ;//optionValues.size() > 0;
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
		// newValues.add(value);
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
	


	// /**
	// * Set the new value of the attribute
	// *
	// * @param newVal
	// * The new value of the attribute
	// */
	// public void setNewValue(String newVal) {
	// newValues.add(newVal);
	// }
	//
	// public void setNewValues(List<String> newVals) {
	// newValues.clear();
	// newValues.addAll(newVals);
	// }

	// /**
	// * Get the new value for the attribute
	// *
	// * @return The new value
	// */
	// public String getNewValue() {
	// if(newValues.size() > 0) {
	// return values.get(0);
	// } else {
	// return "";
	// }
	// }

	// public List<String> getNewValues() {
	// return newValues;
	// }

	// public boolean isMultiValued() {
	// return newValues.size() > 1;
	// }

	/**
	 * Sets the name of the option used when updating the attribute on the
	 * server
	 * 
	 * @param parameterName
	 *            The name of the option when updating from the server
	 */
	public void setID(String parameterName) {
		this.id = parameterName;
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
		return getValue();
	}
	
	public boolean hasOptions() {
		return optionValues.size() > 0;
	}

	public void clearOptions() {
		optionValues.clear();
	}
}
