/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core.response.data;

import java.io.Serializable;

public class FieldValues implements Serializable {

	private static final long serialVersionUID = -7596294082190197659L;

	private String name;

	private int sort_key;

	private String[] visibility_values;

	private boolean is_active;

	private String description;

	private boolean is_open;

	private StatusTransition[] can_change_to;

	public String getName() {
		return name;
	}

	public int getSortKey() {
		return sort_key;
	}

	public String[] getVisibilityValues() {
		return visibility_values;
	}

	public boolean isActive() {
		return is_active;
	}

	public String getDescription() {
		return description;
	}

	public boolean isOpen() {
		return is_open;
	}

	public StatusTransition[] getCanChangeTo() {
		return can_change_to;
	}

}