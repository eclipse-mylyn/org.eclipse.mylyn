/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.provisional.bugzilla.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author Rob Elves
 */
public class AttributeContainer implements Serializable {
	
	private static final long serialVersionUID = -3990742719133977940L;

	/** The keys for the report attributes */
	private ArrayList<Object> attributeKeys;

	/** report attributes (status, resolution, etc.) */
	private HashMap<Object, AbstractRepositoryReportAttribute> attributes;
	
	public AttributeContainer() {
		attributeKeys = new ArrayList<Object>();
		attributes = new HashMap<Object, AbstractRepositoryReportAttribute>();
	}
	
	public void addAttribute(Object key, AbstractRepositoryReportAttribute attribute) {
		if (!attributes.containsKey(attribute.getName())) {
			attributeKeys.add(key);
		}

		// TODO: deal with character sets
		//attribute.setValue(decodeStringFromCharset(attribute.getValue()));

		attributes.put(key, attribute);
	}
	
	public AbstractRepositoryReportAttribute getAttribute(Object key) {
		return attributes.get(key);
	}

	public void removeAttribute(Object key) {
		attributeKeys.remove(key);
		attributes.remove(key);
	}
	
	public List<AbstractRepositoryReportAttribute> getAttributes() {
		ArrayList<AbstractRepositoryReportAttribute> attributeEntries = new ArrayList<AbstractRepositoryReportAttribute>(
				attributeKeys.size());
		for (Iterator<Object> it = attributeKeys.iterator(); it.hasNext();) {
			Object key = it.next();
			AbstractRepositoryReportAttribute attribute = attributes.get(key);
			attributeEntries.add(attribute);
		}
		return attributeEntries;
	}

	public String getAttributeValue(Object key) {
		AbstractRepositoryReportAttribute attribute = getAttribute(key);
		if(attribute != null) {
			return attribute.getValue();
		}
		return "";
	}

	public void removeAllAttributes() {
		attributeKeys.clear();
		attributes.clear();
	}
}
