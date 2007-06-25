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

package org.eclipse.mylyn.tasks.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.mylyn.monitor.core.StatusHandler;

/**
 * Encapsulates attributes for task data.
 * 
 * @author Rob Elves
 * @since 2.0
 */
public class AttributeContainer implements Serializable {

	public static final String ERROR_NO_ATTRIBUTE_FACTORY = "Attribute factory not available.";

	private static final long serialVersionUID = 3538078709450471836L;

	/** The keys for the report attributes */
	private ArrayList<String> attributeKeys;

	/** report attributes (status, resolution, etc.) */
	private HashMap<String, RepositoryTaskAttribute> attributes;

	private transient AbstractAttributeFactory attributeFactory;

	public AttributeContainer(AbstractAttributeFactory attributeFactory) {
		this.attributeFactory = attributeFactory;
		attributeKeys = new ArrayList<String>();
		attributes = new HashMap<String, RepositoryTaskAttribute>();
	}

	public void setAttributeFactory(AbstractAttributeFactory factory) {
		this.attributeFactory = factory;
	}

	public void addAttribute(String key, RepositoryTaskAttribute attribute) {
		if (attributeFactory == null) {
			StatusHandler.log(ERROR_NO_ATTRIBUTE_FACTORY, this);
			return;
		}
		String mapped = attributeFactory.mapCommonAttributeKey(key);
		if (mapped == null) {
			StatusHandler.log("Mylar Error: mapped value for " + key + " returned null.", this);
			return;
		}
		if (!attributes.containsKey(mapped)) {
			attributeKeys.add(mapped);
		}
		attributes.put(mapped, attribute);
	}

	public RepositoryTaskAttribute getAttribute(String key) {
		if (attributeFactory == null) {
			StatusHandler.log(ERROR_NO_ATTRIBUTE_FACTORY, this);
			return null;
		}
		String mapped = attributeFactory.mapCommonAttributeKey(key);
		return attributes.get(mapped);
	}

	public void removeAttribute(Object key) {
		attributeKeys.remove(key);
		attributes.remove(key);
	}

	public List<RepositoryTaskAttribute> getAttributes() {
		ArrayList<RepositoryTaskAttribute> attributeEntries = new ArrayList<RepositoryTaskAttribute>(
				attributeKeys.size());
		for (Iterator<String> it = attributeKeys.iterator(); it.hasNext();) {
			String key = it.next();
			RepositoryTaskAttribute attribute = attributes.get(key);
			attributeEntries.add(attribute);
		}
		return attributeEntries;
	}

	public void removeAllAttributes() {
		attributeKeys.clear();
		attributes.clear();
	}

	public void addAttributeValue(String key, String value) {
		if (attributeFactory == null) {
			StatusHandler.log(ERROR_NO_ATTRIBUTE_FACTORY, this);
			return;
		}
		RepositoryTaskAttribute attrib = getAttribute(key);
		if (attrib != null) {
			attrib.addValue(value);
		} else {
			attrib = attributeFactory.createAttribute(key);
			attrib.addValue(value);
			addAttribute(key, attrib);
		}
	}

	/**
	 * sets a value on an attribute, if attribute doesn't exist, appropriate attribute is created
	 */
	public void setAttributeValue(String key, String value) {
		if (attributeFactory == null) {
			StatusHandler.log(ERROR_NO_ATTRIBUTE_FACTORY, this);
			return;
		}
		RepositoryTaskAttribute attrib = getAttribute(key);
		if (attrib == null) {
			attrib = attributeFactory.createAttribute(key);
			addAttribute(key, attrib);
		}
		attrib.setValue(value);
	}

	public String getAttributeValue(String key) {
		if (attributeFactory == null) {
			StatusHandler.log(ERROR_NO_ATTRIBUTE_FACTORY, this);
			return "";
		}
		String returnValue = "";
		RepositoryTaskAttribute attrib = getAttribute(key);
		if (attrib != null) {
			returnValue = attrib.getValue();
		}
		return returnValue;
	}

	public List<String> getAttributeValues(String key) {
		List<String> returnValue = new ArrayList<String>();
		if (attributeFactory == null) {
			StatusHandler.log(ERROR_NO_ATTRIBUTE_FACTORY, this);
			return returnValue;
		}
		RepositoryTaskAttribute attrib = getAttribute(key);
		if (attrib != null) {
			returnValue = attrib.getValues();
		}
		return returnValue;
	}

	public AbstractAttributeFactory getAttributeFactory() {
		return attributeFactory;
	}

}
