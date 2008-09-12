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

package org.eclipse.mylyn.internal.tasks.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

/**
 * @author Steffen Pingel
 */
public class AttributeMap {

	private final Map<String, String> attributes;

	public AttributeMap() {
		attributes = new HashMap<String, String>();
	}

	public String getAttribute(String key) {
		return attributes.get(key);
	}

	public Map<String, String> getAttributes() {
		return new HashMap<String, String>(attributes);
	}

	public void setAttribute(String key, String value) {
		Assert.isNotNull(key);
		if (value == null) {
			attributes.remove(key);
		} else {
			attributes.put(key, value);
		}
	}

}
