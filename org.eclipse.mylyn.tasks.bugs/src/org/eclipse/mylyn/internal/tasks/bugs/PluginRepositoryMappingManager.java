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

	private static final String EXTENSION_ID_PLUGIN_REPOSITORY_MAPPING = "org.eclipse.mylyn.tasks.bugs.pluginRepositoryMappings"; //$NON-NLS-1$

	private static final String ELEMENT_MAPPING = "mapping"; //$NON-NLS-1$

	private static final String ELEMENT_BRANDING = "branding"; //$NON-NLS-1$

	private static final String ELEMENT_REPOSITORY = "repository"; //$NON-NLS-1$

	private static final String ELEMENT_PROPERTY = "property"; //$NON-NLS-1$

	private static final String ATTRIBUTE_PLUGIN_ID_PREFIX = "pluginIdPrefix"; //$NON-NLS-1$

	private static final String ATTRIBUTE_REPOSITORY_URL = "url"; //$NON-NLS-1$

	private static final String ATTRIBUTE_REPOSITORY_KIND = "kind"; //$NON-NLS-1$

	private static final String ATTRIBUTE_BRANDING_NAME = "name"; //$NON-NLS-1$

	private static final String ATTRIBUTE_BRANDING_DESCRIPTION = "description"; //$NON-NLS-1$

	private static final String ATTRIBUTE_BRANDING_CATEGORY = "category"; //$NON-NLS-1$

	private static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$

	private static final String ATTRIBUTE_VALUE = "value"; //$NON-NLS-1$

	private TreeMap<String, PluginRepositoryMapping> mappingByPrefix;

	public PluginRepositoryMappingManager() {
		readExtensions();
	}

	private void readMapping(IConfigurationElement element) {
		String pluginIdPrefix = element.getAttribute(ATTRIBUTE_PLUGIN_ID_PREFIX);
		Map<String, String> attributes = new HashMap<String, String>();
		// repository
		for (IConfigurationElement attributeElement : element.getChildren(ELEMENT_REPOSITORY)) {
			String repositoryUrl = attributeElement.getAttribute(ATTRIBUTE_REPOSITORY_URL);
			attributes.put(IRepositoryConstants.REPOSITORY_URL, repositoryUrl);
			String connectorKind = attributeElement.getAttribute(ATTRIBUTE_REPOSITORY_KIND);
			attributes.put(IRepositoryConstants.CONNECTOR_KIND, connectorKind);
		}
		// attributes
		for (IConfigurationElement attributeElement : element.getChildren(ELEMENT_PROPERTY)) {
			String name = attributeElement.getAttribute(ATTRIBUTE_NAME);
			String value = attributeElement.getAttribute(ATTRIBUTE_VALUE);
			attributes.put(name, value);
		}
		// branding
		for (IConfigurationElement attributeElement : element.getChildren(ELEMENT_BRANDING)) {
			attributes.put("brandingName", attributeElement.getAttribute(ATTRIBUTE_BRANDING_NAME)); //$NON-NLS-1$
			String description = attributeElement.getAttribute(ATTRIBUTE_BRANDING_DESCRIPTION);
			if (description != null) {
				attributes.put("brandingDescription", description); //$NON-NLS-1$
			}
			String category = attributeElement.getAttribute(ATTRIBUTE_BRANDING_CATEGORY);
			if (category != null) {
				attributes.put("brandingCategory", category); //$NON-NLS-1$
			}
		}

		if (!attributes.isEmpty()) {
			PluginRepositoryMapping pluginRepositoryMapping = new PluginRepositoryMapping();
			pluginRepositoryMapping.addAttributes(attributes);
			pluginRepositoryMapping.addPrefix(pluginIdPrefix);
			addPluginRepositoryMapping(pluginRepositoryMapping);
		} else {
			StatusHandler.log(new Status(IStatus.ERROR, TasksBugsPlugin.ID_PLUGIN, "Missing attributes in " //$NON-NLS-1$
					+ EXTENSION_ID_PLUGIN_REPOSITORY_MAPPING + " extension for id \"" + ATTRIBUTE_PLUGIN_ID_PREFIX //$NON-NLS-1$
					+ "\"")); //$NON-NLS-1$
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
