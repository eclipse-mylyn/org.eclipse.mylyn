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
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.mylyn.commons.notifications.core.INotificationService;
import org.eclipse.mylyn.commons.notifications.core.NotificationSink;
import org.eclipse.mylyn.commons.notifications.core.NotificationSinkEvent;

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
	@Override
	public void notify(List<? extends AbstractNotification> notifications) {
		// Return if notifications are not globally enabled.
		if (!NotificationsPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(NotificationsPlugin.PREF_NOTICATIONS_ENABLED)) {
			return;
		}
		// For each sink assemble a list of notifications that are not blocked
		// and pass these along.
		HashMap<NotificationSink, ArrayList<AbstractNotification>> filtered = new HashMap<>();
		for (AbstractNotification notification : notifications) {
			String id = notification.getEventId();
			NotificationHandler handler = NotificationsPlugin.getDefault().getModel().getNotificationHandler(id);
			if (handler != null) {
				List<NotificationAction> actions = handler.getActions();
				for (NotificationAction action : actions) {
					if (action.isSelected()) {
						NotificationSink sink = action.getSinkDescriptor().getSink();
						if (sink != null) {
							ArrayList<AbstractNotification> list = filtered.get(sink);
							if (list == null) {
								list = new ArrayList<>();
								filtered.put(sink, list);
							}
							list.add(notification);
						}
					}
				}
			}
		}
		// Go through all the sinks that have notifications to display and let
		// them do their job.
		for (Entry<NotificationSink, ArrayList<AbstractNotification>> entry : filtered.entrySet()) {
			final NotificationSink sink = entry.getKey();
			final NotificationSinkEvent event = new NotificationSinkEvent(
					new ArrayList<>(entry.getValue()));
			SafeRunner.run(new ISafeRunnable() {
				@Override
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.WARNING, NotificationsPlugin.ID_PLUGIN, "Sink failed: " //$NON-NLS-1$
							+ sink.getClass(), e));
				}

				@Override
				public void run() throws Exception {
					sink.notify(event);
				}
			});
		}
	}

}
