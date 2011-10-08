package org.eclipse.mylyn.commons.core;
/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 * @since 3.7
 */
public class ExtensionPointReader<T> {

	private final String extensionId;

	private final String elementId;

	private final Class<T> clazz;

	private String classAttributeId;

	private final String pluginId;

	private final List<T> items;

	public ExtensionPointReader(String pluginId, String extensionId, String elementId, Class<T> clazz) {
		Assert.isNotNull(pluginId);
		Assert.isNotNull(extensionId);
		Assert.isNotNull(elementId);
		Assert.isNotNull(clazz);
		this.pluginId = pluginId;
		this.extensionId = extensionId;
		this.elementId = elementId;
		this.clazz = clazz;
		this.classAttributeId = "class"; //$NON-NLS-1$
		this.items = new ArrayList<T>();
	}

	public final String getPluginId() {
		return pluginId;
	}

	public final String getElementId() {
		return elementId;
	}

	public final void setClassAttributeId(String classAttributeId) {
		this.classAttributeId = classAttributeId;
	}

	public final String getClassAttributeId() {
		return classAttributeId;
	}

	public IStatus read() {
		items.clear();

		MultiStatus result = new MultiStatus(pluginId, 0, "Extensions failed to load", null); //$NON-NLS-1$

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(classAttributeId + "." + extensionId); //$NON-NLS-1$
		if (extensionPoint != null) {
			IExtension[] extensions = extensionPoint.getExtensions();
			for (IExtension extension : extensions) {
				IConfigurationElement[] elements = extension.getConfigurationElements();
				for (IConfigurationElement element : elements) {
					if (element.getName().equals(elementId)) {
						T item = readElement(element, result);
						if (item != null) {
							items.add(item);
						}
					}
				}
			}
		}

		handleResult(result);

		return result;
	}

	protected void handleResult(IStatus result) {
		if (!result.isOK()) {
			StatusHandler.log(result);
		}
	}

	public List<T> getItems() {
		return new ArrayList<T>(items);
	}

	protected T readElement(IConfigurationElement element, MultiStatus result) {
		try {
			Object object = element.createExecutableExtension(getClassAttributeId());
			if (clazz.isInstance(object)) {
				return clazz.cast(object);
			} else {
				result.add(new Status(IStatus.ERROR, pluginId, NLS.bind(
						"Class ''{0}'' does not extend expected class for extension contributed by {1}", //$NON-NLS-1$
						object.getClass().getCanonicalName(), getPluginId())));
			}
		} catch (Throwable e) {
			result.add(new Status(IStatus.ERROR, pluginId, NLS.bind(
					"Failed to load for extension contributed by {0}", getPluginId()), e)); //$NON-NLS-1$
		}
		return null;
	}

}
