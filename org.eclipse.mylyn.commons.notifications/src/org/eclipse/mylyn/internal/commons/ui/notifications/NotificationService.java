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
import java.util.List;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.ui.notifications.AbstractNotification;
import org.eclipse.mylyn.commons.ui.notifications.INotificationService;
import org.eclipse.mylyn.commons.ui.notifications.NotificationSink;
import org.eclipse.mylyn.commons.ui.notifications.NotificationSinkEvent;

/**
 * @author Steffen Pingel
 */
public class NotificationService implements INotificationService {

	public NotificationService() {
	}

	public void notify(List<AbstractNotification> notifications) {
		if (!NotificationsPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(NotificationsPlugin.PREF_NOTICATIONS_ENABLED)) {
			return;
		}

		List<NotificationSinkDescriptor> descriptors = NotificationsExtensionReader.getSinks();
		for (NotificationSinkDescriptor descriptor : descriptors) {
			final NotificationSink sink = descriptor.getSink();
			if (sink == null) {
				continue;
			}

			final NotificationSinkEvent event = new NotificationSinkEvent(new ArrayList<AbstractNotification>(
					notifications));
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.WARNING, NotificationsPlugin.ID_PLUGIN, "Sink failed: " //$NON-NLS-1$
							+ sink.getClass(), e));
				}

				public void run() throws Exception {
					sink.notify(event);
				}
			});
		}

	}

}
