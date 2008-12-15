/*******************************************************************************
 * Copyright (c) 2004, 2008 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class describing a custom Fields for a given Bugzilla installation.
 * 
 * @author Frank Becker
 * @since 2.3
 */
public class BugzillaCustomField implements Serializable {

	// old version	private static final long serialVersionUID = 5703683576871326128L;
	private static final long serialVersionUID = 7273310489883205486L;

	public static final String CUSTOM_FIELD_PREFIX = "cf_"; //$NON-NLS-1$

	private final String name;

	private final String description;

	private List<String> options = new ArrayList<String>();

	final private int type;

	final private String typeDesc;

	final private boolean enterBug;

	public BugzillaCustomField(String description, String name, String type, String typeDesc, String enterBug) {
		this.description = description;
		this.name = name;

		this.type = parseInt(type);
		this.typeDesc = typeDesc;
		this.enterBug = "1".equals(enterBug); //$NON-NLS-1$
	}

	private int parseInt(String type) {
		try {
			return Integer.parseInt(type);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public void addOption(String option) {
		this.options.add(option);
	}

	/*
	* @since 3.0.2
	*/
	public int getType() {
		return type;
	}

	/*
	* @since 3.0.2
	*/
	public String getTypeDesc() {
		return typeDesc;
	}

	/*
	* @since 3.0.2
	*/
	public boolean isEnterBug() {
		return enterBug;
	}

}
