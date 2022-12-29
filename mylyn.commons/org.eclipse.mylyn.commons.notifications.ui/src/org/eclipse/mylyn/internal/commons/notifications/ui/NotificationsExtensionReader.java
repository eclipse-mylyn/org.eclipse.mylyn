/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Itema AS - bug 330064 notification filtering and model persistence
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.notifications.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author Steffen Pingel
 * @author Torkild U. Resheim
 */
public class NotificationsExtensionReader {

	private static boolean errorLogged = false;

	static List<NotificationSinkDescriptor> sinks;

	private static Collection<NotificationCategory> categories;

	/**
	 * Returns a list of notification categories, each containing their belonging notification events. Once initialised
	 * the same list will be returned upon subsequent calls of this method.
	 * 
	 * @return a list of notification categories.
	 * @see NotificationModel#save(org.eclipse.ui.IMemento)
	 * @see NotificationModel#load(org.eclipse.ui.IMemento)
	 */
	public static Collection<NotificationCategory> getCategories() {
		if (categories != null) {
			return categories;
		}
		HashMap<String, NotificationCategory> categoryById = new HashMap<String, NotificationCategory>();

		MultiStatus result = new MultiStatus(NotificationsPlugin.ID_PLUGIN, 0, "Notifcation extensions failed to load", //$NON-NLS-1$
				null);

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint repositoriesExtensionPoint = registry
				.getExtensionPoint(NotificationsPlugin.ID_PLUGIN + ".notifications"); //$NON-NLS-1$
		IExtension[] extensions = repositoriesExtensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if ("category".equals(element.getName())) { //$NON-NLS-1$
					NotificationCategory category = new NotificationCategory(element);
					IStatus status = category.validate();
					if (status.isOK()) {
						categoryById.put(category.getId(), category);
					} else {
						result.add(status);
					}
				}
			}
			for (IConfigurationElement element : elements) {
				if ("event".equals(element.getName())) { //$NON-NLS-1$
					NotificationEvent event = new NotificationEvent(element);
					IStatus status = event.validate();
					if (status.isOK()) {
						NotificationCategory category = categoryById.get(event.getCategoryId());
						if (category != null) {
							category.addEvent(event);
						} else {
							result.add(new Status(IStatus.ERROR, NotificationsPlugin.ID_PLUGIN,
									NLS.bind("Extension {0} contributed by {1} specify unknown category ''{2}''", //$NON-NLS-1$
											new String[] { element.getNamespaceIdentifier(),
													element.getContributor().getName(), event.getCategoryId() }))); //NON-NLS-1$ 
						}
					} else {
						result.add(status);
					}
				}
			}
		}

		if (!result.isOK() && !errorLogged) {
			StatusManager.getManager().handle(result);
			errorLogged = true;
		}

		categories = categoryById.values();
		return categories;
	}

	public static List<NotificationSinkDescriptor> getSinks() {
		if (sinks != null) {
			return sinks;
		}

		sinks = new ArrayList<NotificationSinkDescriptor>();

		MultiStatus result = new MultiStatus(NotificationsPlugin.ID_PLUGIN, 0, "Notifcation extensions failed to load", //$NON-NLS-1$
				null);

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint repositoriesExtensionPoint = registry
				.getExtensionPoint(NotificationsPlugin.ID_PLUGIN + ".notifications"); //$NON-NLS-1$
		IExtension[] extensions = repositoriesExtensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if ("sink".equals(element.getName())) { //$NON-NLS-1$
					NotificationSinkDescriptor descriptor = new NotificationSinkDescriptor(element);
					IStatus status = descriptor.validate();
					if (status.isOK()) {
						sinks.add(descriptor);
					} else {
						result.add(status);
					}
				}
			}
		}

		if (!result.isOK()) {
			StatusManager.getManager().handle(result);
		}

		return sinks;
	}
}
