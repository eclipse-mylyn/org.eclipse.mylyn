/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;

/**
 * @author Steffen Pingel
 */
public class PluginRepositoryMappingManager {

	private static final String EXTENSION_ID_PLUGIN_REPOSITORY_MAPPING = "org.eclipse.mylyn.tasks.bugs.pluginRepositoryMappings";

	private static final String ELEMENT_MAPPING = "mapping";

	private static final String ELEMENT_MAPPING_ATTRIBUTES = "attributes";

	private static final String ATTRIBUTE_PLUGID_ID_PREFIX = "pluginIdPrefix";

	private TreeMap<String, PluginRepositoryMapping> mappingByPrefix;

	public PluginRepositoryMappingManager() {
		readExtensions();
	}

	private void readMapping(IConfigurationElement element) {
		String pluginIdPrefix = element.getAttribute(ATTRIBUTE_PLUGID_ID_PREFIX);

		Map<String, String> attributes = null;
		IConfigurationElement[] attributeElements = element.getChildren(ELEMENT_MAPPING_ATTRIBUTES);
		for (IConfigurationElement attributeElement : attributeElements) {
			String repositoryAttributes = attributeElement.getValue();
			try {
				KeyValueParser parser = new KeyValueParser(repositoryAttributes);
				attributes = parser.parse();
			} catch (ParseException e) {
				StatusHandler.log(new Status(IStatus.WARNING, TasksBugsPlugin.ID_PLUGIN,
						"Invalid attributes in extension.", e));
				return;
			}
		}
		
		if (attributes != null) {
			PluginRepositoryMapping pluginRepositoryMapping = new PluginRepositoryMapping();
			pluginRepositoryMapping.addAttributes(attributes);
			pluginRepositoryMapping.addPrefix(pluginIdPrefix);
			addPluginRepositoryMapping(pluginRepositoryMapping);
		} else {
			StatusHandler.log(new Status(IStatus.ERROR, TasksBugsPlugin.ID_PLUGIN,
					"Missing attributes in extension."));			
		}
	}

	private void addPluginRepositoryMapping(PluginRepositoryMapping pluginRepositoryMapping) {
		List<String> prefixs = pluginRepositoryMapping.getPrefixes();
		for (String prefix : prefixs) {
			mappingByPrefix.put(prefix, pluginRepositoryMapping);
		}
	}

	private synchronized void readExtensions() {
		mappingByPrefix = new TreeMap<String, PluginRepositoryMapping>();

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENSION_ID_PLUGIN_REPOSITORY_MAPPING);
		IExtension[] extensions = extensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getName().equals(ELEMENT_MAPPING)) {
					readMapping(element);
				}
			}
		}
	}

	public PluginRepositoryMapping getMapping(String prefix) {
		return mappingByPrefix.get(prefix);
	}

	public String getAttribute(String prefix, String key) {
		for (int i = prefix.length() - 1; i >= 0; i--) {
			PluginRepositoryMapping mapping = getMapping(prefix.substring(0, i));
			if (mapping != null) {
				String value = mapping.getAttributes().get(key);
				if (value != null) {
					return value;
				}
			}
		}
		return null;
	}

	public Map<String, String> getAllAttributes(String prefix) {
		Map<String, String> attributes = new HashMap<String, String>();
		for (int i = 0; i <= prefix.length(); i++) {
			PluginRepositoryMapping mapping = getMapping(prefix.substring(0, i));
			if (mapping != null) {
				attributes.putAll(mapping.getAttributes());
			}
		}
		return attributes;
	}

	public boolean hasMappings() {
		return !mappingByPrefix.isEmpty();
	}
	
}
