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

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Steffen Pingel
 */
public class NotificationCategory extends NotificationElement {

	private final List<NotificationEvent> events;

	public NotificationCategory(IConfigurationElement element) {
		super(element);
		this.events = new ArrayList<NotificationEvent>();
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
