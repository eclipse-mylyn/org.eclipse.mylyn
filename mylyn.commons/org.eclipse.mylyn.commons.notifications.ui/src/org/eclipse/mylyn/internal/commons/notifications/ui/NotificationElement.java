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
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.notifications.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Steffen Pingel
 */
public class NotificationElement {

	protected final IConfigurationElement element;

	private ImageDescriptor iconDescriptor;

	private final String id;

	private final String label;

	public NotificationElement(IConfigurationElement element) {
		Assert.isNotNull(element);
		this.element = element;
		this.id = element.getAttribute("id"); //$NON-NLS-1$
		this.label = element.getAttribute("label"); //$NON-NLS-1$
	}

	public String getId() {
		return id;
	}

	public ImageDescriptor getImageDescriptor() {
		if (iconDescriptor == null) {
			if (element != null) {
				String iconPath = element.getAttribute("icon"); //$NON-NLS-1$
				if (iconPath != null) {
					iconDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(element.getContributor().getName(),
							iconPath);
				}
			}
		}
		return iconDescriptor;
	}

	public String getLabel() {
		return label;
	}

	public String getPluginId() {
		return element.getContributor().getName();
	}

	public IStatus validate() {
		if (id == null) {
			return new Status(IStatus.ERROR, NotificationsPlugin.ID_PLUGIN,
					NLS.bind("Extension {0} contributed by {1} does not specify id attribute", //$NON-NLS-1$
							element.getNamespaceIdentifier(), getPluginId())); //NON-NLS-1$
		} else if (label == null) {
			return new Status(IStatus.ERROR, NotificationsPlugin.ID_PLUGIN,
					NLS.bind("Extension {0} contributed by {1} does not specify label attribute", //$NON-NLS-1$
							element.getNamespaceIdentifier(), getPluginId())); //NON-NLS-1$
		}
		return Status.OK_STATUS;
	}

}
