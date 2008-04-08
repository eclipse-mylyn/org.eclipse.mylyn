/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

	private static final long serialVersionUID = 5703683576871326128L;

	public static final String CUSTOM_FIELD_PREFIX = "cf_";

	private String name;

	private String description;

	private List<String> options = new ArrayList<String>();

	public BugzillaCustomField(String description, String name) {
		this.description = description;
		this.name = name;
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
}
