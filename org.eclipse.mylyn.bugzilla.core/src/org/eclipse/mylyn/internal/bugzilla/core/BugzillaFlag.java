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

/**
 * @author Frank Becker
 */
public class BugzillaFlag implements Serializable {

	private static final long serialVersionUID = 4920551884607344418L;

	private final String name;

	private final String description;

	private final String type;

	private boolean requestable;

	private boolean specifically_requestable;

	private boolean multiplicable;

	public BugzillaFlag(String name, String description, String type, String requestable,
			String specifically_requestable, String multiplicable) {
		this.description = description;
		this.name = name;
		this.type = type;

		if (multiplicable != null && !multiplicable.equals("")) {
			this.multiplicable = multiplicable.equals("1");
		}

		if (requestable != null && !requestable.equals("")) {
			this.requestable = requestable.equals("1");
		}

		if (specifically_requestable != null && !specifically_requestable.equals("")) {
			this.specifically_requestable = specifically_requestable.equals("1");
		}
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getType() {
		return type;
	}

	public boolean isRequestable() {
		return requestable;
	}

	public boolean isSpecifically_requestable() {
		return specifically_requestable;
	}

	public boolean isMultiplicable() {
		return multiplicable;
	}
}
