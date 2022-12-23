/*******************************************************************************
 * Copyright (c) 2015 Frank Becker and others.
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

public class PutUpdateEntry {
	private String last_change_time;

	private String id;

	private String[] alias;

	public PutUpdateEntry() {
	}

	public String getLast_change_time() {
		return last_change_time;
	}

	public String getId() {
		return id;
	}

	public String[] getAlias() {
		return alias;
	}

}
