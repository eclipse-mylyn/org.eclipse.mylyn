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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.ui.notifications.NotificationSink;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author Steffen Pingel
 */
public class NotificationSinkDescriptor extends NotificationElement {

	private NotificationSink sink;

	private Status status;

	public NotificationSinkDescriptor(IConfigurationElement element) {
		super(element);
	}

	public NotificationSink getSink() {
		if (sink != null || status != null) {
			return sink;
		}

		try {
			Object object = element.createExecutableExtension("class");
			if (object instanceof NotificationSink) {
				sink = (NotificationSink) object;
				return sink;
			} else {
				status = new Status(IStatus.ERROR, NotificationsPlugin.ID_PLUGIN, NLS.bind(
						"Sink ''{0}'' does not extend expected class for extension contributed by {1}", //$NON-NLS-1$
						object.getClass().getCanonicalName(), getPluginId()));
			}
		} catch (Throwable e) {
			status = new Status(IStatus.ERROR, NotificationsPlugin.ID_PLUGIN, NLS.bind(
					"Sink failed to load for extension contributed by {0}", getPluginId()), e); //$NON-NLS-1$
		}

		StatusManager.getManager().handle(status);
		return null;
	}

}
