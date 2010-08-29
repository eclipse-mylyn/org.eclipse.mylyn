/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui.notifications;

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
 */
public class NotificationsExtensionReader {

	private static boolean errorLogged = false;

	static List<NotificationSinkDescriptor> sinks;

	public static Collection<NotificationCategory> getCategories() {
		HashMap<String, NotificationCategory> categoryById = new HashMap<String, NotificationCategory>();

		MultiStatus result = new MultiStatus(NotificationsPlugin.ID_PLUGIN, 0,
				"Notifcation extensions failed to load", null); //$NON-NLS-1$

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint repositoriesExtensionPoint = registry.getExtensionPoint(NotificationsPlugin.ID_PLUGIN
				+ ".notifications");
		IExtension[] extensions = repositoriesExtensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if ("category".equals(element.getName())) {
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
				if ("event".equals(element.getName())) {
					NotificationEvent event = new NotificationEvent(element);
					IStatus status = event.validate();
					if (status.isOK()) {
						NotificationCategory category = categoryById.get(event.getCategoryId());
						if (category != null) {
							category.addEvent(event);
						} else {
							result.add(new Status(
									IStatus.ERROR,
									NotificationsPlugin.ID_PLUGIN,
									NLS.bind(
											"Extension {0} contributed by {1} specify unknown category ''{2}''", new String[] { element.getNamespaceIdentifier(), element.getContributor().getName(), event.getCategoryId() }))); //$NON-NLS-1
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

		return categoryById.values();
	}

	public static List<NotificationSinkDescriptor> getSinks() {
		if (sinks != null) {
			return sinks;
		}

		sinks = new ArrayList<NotificationSinkDescriptor>();

		MultiStatus result = new MultiStatus(NotificationsPlugin.ID_PLUGIN, 0,
				"Notifcation extensions failed to load", null); //$NON-NLS-1$

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint repositoriesExtensionPoint = registry.getExtensionPoint(NotificationsPlugin.ID_PLUGIN
				+ ".notifications");
		IExtension[] extensions = repositoriesExtensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if ("sink".equals(element.getName())) {
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
