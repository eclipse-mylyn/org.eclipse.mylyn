/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Itema AS - bug 330064 notification filtering and model persistence
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui.notifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

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
 * @author Torkild U. Resheim
 */
public class NotificationService implements INotificationService {

	public NotificationService() {
	}

	/**
	 * Notify sinks about the.
	 */
	public void notify(List<? extends AbstractNotification> notifications) {
		// Return if notifications are not globally enabled.
		if (!NotificationsPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(NotificationsPlugin.PREF_NOTICATIONS_ENABLED)) {
			return;
		}
		// For each sink assemble a list of notifications that are not blocked
		// and pass these along.
		HashMap<NotificationSink, ArrayList<AbstractNotification>> filtered = new HashMap<NotificationSink, ArrayList<AbstractNotification>>();
		for (AbstractNotification abstractNotification : notifications) {
			String id = abstractNotification.getEventId();
			NotificationHandler handler = NotificationsPlugin.getDefault().getModel().getNotificationHandler(id);
			if (handler != null) {
				//Assert.isNotNull(handler, NLS.bind("Notification handler for event ''{0}'' has not been initialized", id)); //$NON-NLS-1$
				if (handler.getEvent().isSelected()) {
					List<NotificationAction> actions = handler.getActions();
					for (NotificationAction notificationAction : actions) {
						if (notificationAction.isSelected()) {
							NotificationSink sink = notificationAction.getSinkDescriptor().getSink();
							if (sink != null) {
								ArrayList<AbstractNotification> list = filtered.get(sink);
								if (list == null) {
									list = new ArrayList<AbstractNotification>();
									filtered.put(sink, list);
								}
								list.add(abstractNotification);
							}
						}
					}
				}
			}
		}
		// Go through all the sinks that have notifications to display and let
		// them do their job.
		for (Entry<NotificationSink, ArrayList<AbstractNotification>> entry : filtered.entrySet()) {
			final NotificationSink sink = entry.getKey();
			final NotificationSinkEvent event = new NotificationSinkEvent(new ArrayList<AbstractNotification>(
					entry.getValue()));
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
