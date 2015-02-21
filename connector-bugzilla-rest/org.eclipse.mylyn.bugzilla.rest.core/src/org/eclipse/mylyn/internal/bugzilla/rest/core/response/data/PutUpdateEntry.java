/*******************************************************************************
 * Copyright (c) 2015 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
