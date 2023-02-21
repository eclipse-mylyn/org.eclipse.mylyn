/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.model;

import java.io.Serializable;

/**
 * @author Steffen Pingel
 */
public class TracTicketField implements Serializable {

	private static final long serialVersionUID = -640983268404073300L;

	public enum Type {
		TEXT, CHECKBOX, SELECT, RADIO, TEXTAREA;

		public static Type fromString(String value) {
			value = value.toLowerCase();
			if ("text".equals(value)) { //$NON-NLS-1$
				return TEXT;
			} else if ("checkbox".equals(value)) { //$NON-NLS-1$
				return CHECKBOX;
			} else if ("select".equals(value)) { //$NON-NLS-1$
				return SELECT;
			} else if ("radio".equals(value)) { //$NON-NLS-1$
				return RADIO;
			} else if ("textarea".equals(value)) { //$NON-NLS-1$
				return TEXTAREA;
			}
			return TEXT;
		}

	}

	public static final int DEFAULT_SIZE = -1;

	private String name;

	private Type type;

	private String label;

	private String[] options;

	private String defaultValue;

	private boolean custom;

	private int order;

	private boolean optional;

	private int width = DEFAULT_SIZE;

	private int height = DEFAULT_SIZE;

	public TracTicketField(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String[] getOptions() {
		return options;
	}

	public void setOptions(String[] options) {
		this.options = options;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isCustom() {
		return custom;
	}

	public void setCustom(boolean custom) {
		this.custom = custom;
	}

	@Override
	public String toString() {
		return name;
	}

}
