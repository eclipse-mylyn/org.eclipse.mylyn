/*******************************************************************************
 * Copyright (c) 2014 Frank Becker and others.
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

public class FlagType implements Serializable {
	private static final long serialVersionUID = 4749994430690706562L;

	private int id;

	private String name;

	private String description;

	private String cc_list;

	private int sort_key;

	private boolean is_active;

	private boolean is_requestable;

	private boolean is_requesteeble;

	private boolean is_multiplicable;

	private int grant_group;

	private int request_group;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getCcList() {
		return cc_list;
	}

	public int getSortKey() {
		return sort_key;
	}

	public boolean isIs_active() {
		return is_active;
	}

	public boolean isRequestable() {
		return is_requestable;
	}

	public boolean isRequesteeble() {
		return is_requesteeble;
	}

	public boolean isMultiplicable() {
		return is_multiplicable;
	}

	public int getGrantGroup() {
		return grant_group;
	}

	public int getRequestGroup() {
		return request_group;
	}

}
