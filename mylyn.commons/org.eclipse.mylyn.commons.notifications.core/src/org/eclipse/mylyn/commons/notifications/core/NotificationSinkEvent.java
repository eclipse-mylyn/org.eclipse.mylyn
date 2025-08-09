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

package org.eclipse.mylyn.commons.notifications.core;

import java.util.List;

/**
 * @author Steffen Pingel
 */
public class NotificationSinkEvent {

	private final List<AbstractNotification> notifications;

	public NotificationSinkEvent(List<AbstractNotification> notifications) {
		this.notifications = notifications;
	}

	public List<AbstractNotification> getNotifications() {
		return notifications;
	}

}
