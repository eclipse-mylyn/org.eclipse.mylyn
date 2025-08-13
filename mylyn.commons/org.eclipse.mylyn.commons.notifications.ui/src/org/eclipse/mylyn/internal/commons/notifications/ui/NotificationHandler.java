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

import java.util.List;

/**
 * Manages actions that are triggered when a {@link NotificationEvent} occurs.
 *
 * @author Steffen Pingel
 */
public class NotificationHandler {

	private final List<NotificationAction> actions;

	private final NotificationEvent event;

	public NotificationHandler(NotificationEvent event, List<NotificationAction> actions) {
		this.event = event;
		this.actions = actions;
	}

	public List<NotificationAction> getActions() {
		return actions;
	}

	public NotificationEvent getEvent() {
		return event;
	}

}
