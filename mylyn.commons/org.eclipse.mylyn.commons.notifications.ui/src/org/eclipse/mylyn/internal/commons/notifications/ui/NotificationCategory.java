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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Steffen Pingel
 */
public class NotificationCategory extends NotificationElement {

	private final List<NotificationEvent> events;

	public NotificationCategory(IConfigurationElement element) {
		super(element);
		events = new ArrayList<>();
	}

	public void addEvent(NotificationEvent event) {
		event.setCategory(this);
		events.add(event);
	}

	public List<NotificationEvent> getEvents() {
		return events;
	}

	public void removeEvent(NotificationEvent event) {
		event.setCategory(null);
		events.remove(event);
	}

}
