/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Steffen Pingel
 */
public class PluginRepositoryMapping {

	private final List<String> prefixes = new ArrayList<String>();

	private final Map<String, String> attributes = new HashMap<String, String>();

	public void addPrefix(String prefix) {
		prefixes.add(prefix);
	}

	public void addAttributes(Map<String, String> attributes) {
		this.attributes.putAll(attributes);
	}

	public void removePrefix(String prefix) {
		prefixes.remove(prefix);
	}

	public Map<String, String> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}

	public List<String> getPrefixes() {
		return Collections.unmodifiableList(prefixes);
	}

}
