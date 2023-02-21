/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
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

package org.eclipse.mylyn.internal.bugzilla.rest.core.response.data;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Field implements Named, Serializable {

	private static final long serialVersionUID = -5578410875657273067L;

	private int id;

	private int type;

	@SerializedName("is_custom")
	private boolean custom;

	private String name;

	@SerializedName("display_name")
	private String displayName;

	@SerializedName("is_mandatory")
	private boolean mandatory;

	@SerializedName("is_on_bug_entry")
	private boolean onBugEntry;

	@SerializedName("visibility_field")
	private String visibilityField;

	@SerializedName("visibility_values")
	private String[] visibilityValues;

	@SerializedName("value_field")
	private String valueField;

	private FieldValues[] values;

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public boolean isCustom() {
		return custom;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public boolean isOnBugEntry() {
		return onBugEntry;
	}

	public String getVisibilityField() {
		return visibilityField;
	}

	public String[] getVisibilityValues() {
		return visibilityValues;
	}

	public String getValueField() {
		return valueField;
	}

	public FieldValues[] getValues() {
		return values;
	}

}