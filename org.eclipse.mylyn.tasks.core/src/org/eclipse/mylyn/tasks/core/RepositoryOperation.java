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

package org.eclipse.mylyn.tasks.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A representation of an operation that can be done to the bug when it is
 * submitted
 * 
 * NOTE: likely to change for 3.0
 * 
 * @author Shawn Minto
 * @since 2.0
 */
public class RepositoryOperation implements Serializable {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3256442508174045236L;

	/** The name of the value for the knob attribute */
	private String knob_name;

	/** The name of the option that can be chosen */
	private String optionName;

	/** List of option names */
	private List<String> optionNames;

	/** Map of options and their names */
	private Map<String, String> options;

	/** Flag for if we have any options or not */
	private boolean hasOptions = false;

	/** The name of the operation (text that we display) */
	private String operation_name;

	/** The option that is selected */
	private String op_sel;

	/** Whether this is to be checked or not */
	private boolean isChecked = false;

	private boolean isInput = false;

	private String inputName = null;

	private String inputValue = "";

	/**
	 * Constructor
	 * 
	 * @param knobName
	 *            The name of the value for the knob attribute
	 * @param operationName
	 *            The display text for the operation
	 */
	public RepositoryOperation(String knobName, String operationName) {
		knob_name = knobName;
		operation_name = operationName;
	}

	/**
	 * Get the knob name
	 * 
	 * @return The knob name
	 */
	public String getKnobName() {
		return knob_name;
	}

	/**
	 * Get the display name
	 * 
	 * @return The display name
	 */
	public String getOperationName() {
		return operation_name;
	}

	/**
	 * Check if this has any options
	 * 
	 * @return True if there are option values
	 */
	public boolean hasOptions() {
		return hasOptions;
	}

	/**
	 * Set up this operation to have options
	 * 
	 * @param optionName
	 *            The name for the option attribute
	 */
	public void setUpOptions(String optionName) {
		hasOptions = true;
		this.optionName = optionName;
		options = new HashMap<String, String>();
		optionNames = new ArrayList<String>();
	}

	/**
	 * Add an option value to the operation
	 * 
	 * @param name
	 *            The name of the option
	 * @param value
	 *            The value of the option
	 */
	public void addOption(String name, String value) {
		options.put(name, value);
		if (options.size() == 1)
			op_sel = name;
		optionNames.add(name);
	}

	/**
	 * Get the list of option names for this operation
	 * 
	 * @return The list of option names
	 */
	public List<String> getOptionNames() {
		return optionNames;
	}

	/**
	 * Get the selected option
	 * 
	 * @return The selected option name
	 */
	public String getOptionSelection() {
		return op_sel;
	}

	/**
	 * Set the selected option
	 * 
	 * @param string
	 *            The name of the selected option
	 */
	public void setOptionSelection(String string) {
		op_sel = string;
	}

	/**
	 * Check if this is to be checked or not
	 * 
	 * @return True if this is to be checked at the start
	 */
	public boolean isChecked() {
		return isChecked;
	}

	/**
	 * Set whether this option is to be checked or not
	 * 
	 * @param b
	 *            True if it is to be checked
	 */
	public void setChecked(boolean b) {
		isChecked = b;
	}

	/**
	 * Get the name for the option attribute
	 * 
	 * @return The option name
	 */
	public String getOptionName() {
		return optionName;
	}

	/**
	 * Get the value for an option from its name
	 * 
	 * @param option
	 *            The name of the option
	 * @return The value of the option
	 */
	public String getOptionValue(String option) {
		return options.get(option);
	}

	public boolean isInput() {
		return isInput;
	}

	public String getInputName() {
		return inputName;
	}

	public void setInputName(String inputName) {
		isInput = true;
		this.inputName = inputName;
	}

	public String getInputValue() {
		return inputValue == null ? "" : inputValue;
	}

	public void setInputValue(String inputValue) {
		this.inputValue = inputValue;
	}
}
