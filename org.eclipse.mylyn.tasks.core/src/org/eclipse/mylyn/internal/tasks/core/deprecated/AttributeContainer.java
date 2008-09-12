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

package org.eclipse.mylyn.internal.tasks.core.deprecated;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;

/**
 * @deprecated Do not use. This class is pending for removal: see bug 237552.
 */
@Deprecated
public class AttributeContainer implements Serializable {

	public static final String ERROR_NO_ATTRIBUTE_FACTORY = "Attribute factory not available.";

	private static final long serialVersionUID = 3538078709450471836L;

	/** The keys for the report attributes */
	private final ArrayList<String> attributeKeys;

	/** report attributes (status, resolution, etc.) */
	private final HashMap<String, RepositoryTaskAttribute> attributes;

	private transient AbstractAttributeFactory attributeFactory;

	private transient RepositoryTaskData taskData;

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
			StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN, ERROR_NO_ATTRIBUTE_FACTORY));
			return;
		}
		String mapped = attributeFactory.mapCommonAttributeKey(key);
		if (mapped == null) {
			StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN, "Mapped value for " + key
					+ " returned null."));
			return;
		}
		if (!attributes.containsKey(mapped)) {
			attributeKeys.add(mapped);
		}
		attributes.put(mapped, attribute);
	}

	public RepositoryTaskAttribute getAttribute(String key) {
		if (attributeFactory == null) {
			StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN, ERROR_NO_ATTRIBUTE_FACTORY));
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
		for (String key : attributeKeys) {
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
			StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN, ERROR_NO_ATTRIBUTE_FACTORY));
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
			StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN, ERROR_NO_ATTRIBUTE_FACTORY));
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
			StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN, ERROR_NO_ATTRIBUTE_FACTORY));
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
			StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN, ERROR_NO_ATTRIBUTE_FACTORY));
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

	/**
	 * @since 2.3
	 */
	void setTaskData(RepositoryTaskData taskData) {
		this.taskData = taskData;
		for (RepositoryTaskAttribute attribute : attributes.values()) {
			attribute.setTaskData(taskData);
		}
	}

	/**
	 * @since 2.3
	 */
	public RepositoryTaskData getTaskData() {
		return taskData;
	}

}
