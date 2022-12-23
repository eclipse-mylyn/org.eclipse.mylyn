/*******************************************************************************
 * Copyright (c) 2004, 2014 Frank Becker and others.
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
	//              private static final long serialVersionUID = 7273310489883205486L;

	private static final long serialVersionUID = 8268371206426652131L;

	public static final String CUSTOM_FIELD_PREFIX = "cf_"; //$NON-NLS-1$

	public static enum FieldType {
		UNKNOWN, FreeText, DropDown, MultipleSelection, LargeText, DateTime, BugId;

		private static int parseInt(String type) {
			try {
				return Integer.parseInt(type);
			} catch (NumberFormatException e) {
				return -1;
			}
		}

		@Override
		public String toString() {
			switch (this.ordinal()) {
			case 1:
				return "Free Text"; //$NON-NLS-1$
			case 2:
				return "Drop Down"; //$NON-NLS-1$
			case 3:
				return "Multiple-Selection Box"; //$NON-NLS-1$
			case 4:
				return "Large Text Box"; //$NON-NLS-1$
			case 5:
				return "Date/Time"; //$NON-NLS-1$
			case 6:
				return "Bug ID"; //$NON-NLS-1$
			default:
				return super.toString();
			}
		}

		public static FieldType convert(String change) {
			switch (parseInt(change)) {
			case 1:
				return FreeText;
			case 2:
				return DropDown;
			case 3:
				return MultipleSelection;
			case 4:
				return LargeText;
			case 5:
				return DateTime;
			case 6:
				return BugId;
			default:
				return UNKNOWN;
			}
		}
	}

	private final String name;

	private final String description;

	private List<String> options = new ArrayList<String>();

	final private int type;

	final private FieldType fieldType;

	final private boolean enterBug;

	public BugzillaCustomField(String description, String name, String type, String enterBug) {
		this.description = description;
		this.name = name;
		this.type = parseInt(type);
		this.fieldType = FieldType.convert(type);
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

	/**
	 * @since 3.0.2
	 * @deprecated use {@link #getFieldType()} instead
	 */
	@Deprecated
	public int getType() {
		return type;
	}

	/**
	 * @since 3.0.2
	 * @deprecated use {@link #getFieldType().toString()} instead
	 */
	@Deprecated
	public String getTypeDesc() {
		return getFieldType().toString();
	}

	/**
	 * @since 3.0.2
	 */
	public boolean isEnterBug() {
		return enterBug;
	}

	public FieldType getFieldType() {
		return fieldType;
	}

}
