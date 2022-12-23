/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
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
		attributes = new HashMap<String, String>(4);
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
			attributes.put(key.intern(), value.intern());
		}
	}

}
