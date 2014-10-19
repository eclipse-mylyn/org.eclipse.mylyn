/*******************************************************************************
 * Copyright (c) 2014 Frank Becker and others.
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

public class Component implements Serializable {
	private static final long serialVersionUID = -3420463254677859338L;

	private int id;

	private String name;

	private String description;

	private String default_assigned_to;

	private String default_qa_contact;

	private int sort_key;

	private boolean is_active;

	private FlagTypes flag_types;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getDefaultAssignedTo() {
		return default_assigned_to;
	}

	public String getDefaultQaContact() {
		return default_qa_contact;
	}

	public int getSortKey() {
		return sort_key;
	}

	public boolean isActive() {
		return is_active;
	}

	public FlagTypes getFlagTypes() {
		return flag_types;
	}

}
