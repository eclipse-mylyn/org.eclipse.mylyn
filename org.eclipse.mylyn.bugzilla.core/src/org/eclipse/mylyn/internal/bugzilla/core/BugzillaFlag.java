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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Frank Becker
 */
public class BugzillaFlag implements Serializable {

// old	private static final long serialVersionUID = 4920551884607344418L;
	private static final long serialVersionUID = -3149026741475639885L;

	private final String name;

	private final String description;

	private final String type;

	private final boolean requestable;

	private final boolean specifically_requestable;

	private final boolean multiplicable;

	private final int flagId;

	private final Map<String, List<String>> used = new HashMap<String, List<String>>();

	public BugzillaFlag(String name, String description, String type, String requestable,
			String specifically_requestable, String multiplicable, int flagId) {
		this.description = description;
		this.name = name;
		this.type = type;
		this.flagId = flagId;

		if (multiplicable != null && !multiplicable.equals("")) { //$NON-NLS-1$
			this.multiplicable = multiplicable.equals("1"); //$NON-NLS-1$
		} else {
			this.multiplicable = false;
		}

		if (requestable != null && !requestable.equals("")) { //$NON-NLS-1$
			this.requestable = requestable.equals("1"); //$NON-NLS-1$
		} else {
			this.requestable = false;
		}

		if (specifically_requestable != null && !specifically_requestable.equals("")) { //$NON-NLS-1$
			this.specifically_requestable = specifically_requestable.equals("1"); //$NON-NLS-1$
		} else {
			this.specifically_requestable = false;
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

	public int getFlagId() {
		return flagId;
	}

	public void addUsed(String product, String component) {
		List<String> componentList = used.get(product);
		if (componentList == null) {
			componentList = new ArrayList<String>();
			used.put(product, componentList);
		}
		if (!componentList.contains(component)) {
			componentList.add(component);
		}
	}

	public boolean isUsedIn(String product, String component) {
		List<String> componentList = used.get(product);
		if (componentList != null && componentList.contains(component)) {
			return true;
		}
		return false;
	}
}
