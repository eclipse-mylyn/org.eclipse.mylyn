/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.commons.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
 * @author Sam Davis
 * @since 3.7
 */
public class ExtensionPointReader<T> {
	private final class PriorityComparator implements Comparator<IConfigurationElement> {
		@Override
		public int compare(IConfigurationElement arg0, IConfigurationElement arg1) {
			double p0 = 0;
			double p1 = 0;
			try {
				String priorityAttribute = arg0.getAttribute(getPriorityAttributeId());
				if (priorityAttribute != null) {
					p0 = Double.parseDouble(priorityAttribute);
				}
			} catch (NumberFormatException e) {
			}
			try {
				String priorityAttribute = arg1.getAttribute(getPriorityAttributeId());
				if (priorityAttribute != null) {
					p1 = Double.parseDouble(priorityAttribute);
				}
			} catch (NumberFormatException e) {
			}
			if (p1 > p0) {
				return 1;
			} else if (p1 < p0) {
				return -1;
			}
			return 0;
		}

	}

	private static final String DEFAULT_ATTRIBUTE_ID_CLASS = "class"; //$NON-NLS-1$

	private static final String DEFAULT_ATTRIBUTE_ID_PRIORITY = "priority"; //$NON-NLS-1$

	private final PriorityComparator priorityComparator = new PriorityComparator();

	private String classAttributeId;

	private final Class<T> clazz;

	private final String elementId;

	private final String extensionId;

	private String filterAttributeId;

	private String filterAttributeValue;

	private final List<T> items;

	private final String pluginId;

	private String priorityAttributeId;

	public ExtensionPointReader(String pluginId, String extensionId, String elementId, Class<T> clazz) {
		this(pluginId, extensionId, elementId, clazz, null, null);
	}

	public ExtensionPointReader(String pluginId, String extensionId, String elementId, Class<T> clazz,
			String filterAttributeId, String filterAttributeValue) {
		Assert.isNotNull(pluginId);
		Assert.isNotNull(extensionId);
		Assert.isNotNull(elementId);
		Assert.isNotNull(clazz);
		this.pluginId = pluginId;
		this.extensionId = extensionId;
		this.elementId = elementId;
		this.clazz = clazz;
		this.filterAttributeId = filterAttributeId;
		this.filterAttributeValue = filterAttributeValue;
		classAttributeId = DEFAULT_ATTRIBUTE_ID_CLASS;
		priorityAttributeId = DEFAULT_ATTRIBUTE_ID_PRIORITY;
		items = new ArrayList<>();
	}

	public final String getClassAttributeId() {
		return classAttributeId;
	}

	public final String getElementId() {
		return elementId;
	}

	public T getItem() {
		return items.isEmpty() ? null : items.get(0);
	}

	public List<T> getItems() {
		return new ArrayList<>(items);
	}

	public final String getPluginId() {
		return pluginId;
	}

	public String getPriorityAttributeId() {
		return priorityAttributeId;
	}

	public IStatus read() {
		items.clear();

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		if (registry == null) {
			return Status.CANCEL_STATUS;
		}

		MultiStatus result = new MultiStatus(pluginId, 0,
				NLS.bind("Extensions for {0}/{1} failed to load", pluginId, elementId), null); //$NON-NLS-1$

		IExtensionPoint extensionPoint = registry.getExtensionPoint(pluginId + "." + extensionId); //$NON-NLS-1$
		if (extensionPoint != null) {
			IExtension[] extensions = extensionPoint.getExtensions();
			for (IExtension extension : extensions) {
				IConfigurationElement[] elements = extension.getConfigurationElements();
				Arrays.sort(elements, priorityComparator);
				for (IConfigurationElement element : elements) {
					if (element.getName().equals(elementId) && shouldRead(element)) {
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

	public final void setClassAttributeId(String classAttributeId) {
		this.classAttributeId = classAttributeId;
	}

	public void setFilterAttributeId(String filterAttributeId) {
		this.filterAttributeId = filterAttributeId;
	}

	public void setFilterAttributeValue(String filterAttributeValue) {
		this.filterAttributeValue = filterAttributeValue;
	}

	public void setPriorityAttributeId(String priorityAttributeId) {
		this.priorityAttributeId = priorityAttributeId;
	}

	protected void handleResult(IStatus result) {
		if (!result.isOK()) {
			StatusHandler.log(result);
		}
	}

	protected T readElement(IConfigurationElement element, MultiStatus result) {
		try {
			Object object = element.createExecutableExtension(getClassAttributeId());
			if (clazz.isInstance(object)) {
				return clazz.cast(object);
			} else {
				result.add(new Status(IStatus.ERROR, pluginId,
						NLS.bind("Class ''{0}'' does not extend expected class for extension contributed by {1}", //$NON-NLS-1$
								object.getClass().getCanonicalName(), getPluginId())));
			}
		} catch (Throwable e) {
			result.add(new Status(IStatus.ERROR, pluginId,
					NLS.bind("Failed to load for extension contributed by {0}", getPluginId()), e)); //$NON-NLS-1$
		}
		return null;
	}

	/**
	 * Determines whether the element should be instantiated by this ExtensionPointReader. This implementation checks whether the element
	 * defines an attribute with id and value matching filterAttributeId and filterAttributeValue. If filterAttributeValue is the empty
	 * string, an element is also considered to match if it does not define the attribute.
	 * <p>
	 * Subclasses may override.
	 */
	protected boolean shouldRead(IConfigurationElement element) {
		return filterAttributeId == null || filterAttributeValue == null
				|| filterAttributeValue.equals(element.getAttribute(filterAttributeId))
				|| filterAttributeValue.length() == 0 && element.getAttribute(filterAttributeId) == null;
	}

}
