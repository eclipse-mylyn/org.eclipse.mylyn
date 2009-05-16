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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.mylyn.internal.provisional.tasks.bugs.IProduct;
import org.eclipse.mylyn.internal.provisional.tasks.bugs.IProvider;

/**
 * @author Steffen Pingel
 */
public class SupportProduct implements IProduct {

	private String description;

	private String name;

	private String id;

	private IProvider provider;

	private String pluginId;

	TreeMap<String, ProductRepositoryMapping> mappingByNamespace;

	public SupportProduct() {
		mappingByNamespace = new TreeMap<String, ProductRepositoryMapping>();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public IProvider getProvider() {
		return provider;
	}

	public void setProvider(IProvider provider) {
		this.provider = provider;
	}

	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	public void addRepositoryMapping(ProductRepositoryMapping mapping) {
		ProductRepositoryMapping existingMapping = mappingByNamespace.get(mapping.getNamespace());
		if (existingMapping != null) {
			existingMapping.getAttributes().putAll(mapping.getAttributes());
		} else {
			mappingByNamespace.put(mapping.getNamespace(), mapping);
		}
	}

	public ProductRepositoryMapping getMapping(String prefix) {
		return mappingByNamespace.get(prefix);
	}

	public boolean hasMappings() {
		return !mappingByNamespace.isEmpty();
	}

	public Map<String, String> getAllAttributes(String prefix) {
		Map<String, String> attributes = null;
		for (int i = 0; i <= prefix.length(); i++) {
			ProductRepositoryMapping mapping = getMapping(prefix.substring(0, i));
			if (mapping != null) {
				if (attributes == null) {
					attributes = new HashMap<String, String>();
				}
				attributes.putAll(mapping.getAttributes());
			}
		}
		if (attributes != null) {
			return attributes;
		} else {
			return Collections.emptyMap();
		}
	}

	public String getAttribute(String prefix, String key) {
		for (int i = prefix.length() - 1; i >= 0; i--) {
			ProductRepositoryMapping mapping = getMapping(prefix.substring(0, i));
			if (mapping != null) {
				String value = mapping.getAttributes().get(key);
				if (value != null) {
					return value;
				}
			}
		}
		return null;
	}

}
