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
import java.util.Map;

import org.eclipse.ui.IMemento;

/**
 * @author Steffen Pingel
 */
public class NotificationModel {

	private boolean dirty;

	private final Map<String, NotificationHandler> handlerByEventId;

	public NotificationModel(IMemento memento) {
		this.handlerByEventId = new HashMap<String, NotificationHandler>();
	}

	public Collection<NotificationCategory> getCategories() {
		return NotificationsExtensionReader.getCategories();
	}

	public NotificationHandler getNotificationHandler(String eventId) {
		return handlerByEventId.get(eventId);
	}

	public NotificationHandler getOrCreateNotificationHandler(NotificationEvent event) {
		NotificationHandler handler = getNotificationHandler(event.getId());
		if (handler == null) {
			handler = new NotificationHandler(event, getActions(event));
			handlerByEventId.put(event.getId(), handler);
		}
		return handler;
	}

	private List<NotificationAction> getActions(NotificationEvent event) {
		List<NotificationSinkDescriptor> descriptors = NotificationsExtensionReader.getSinks();
		List<NotificationAction> actions = new ArrayList<NotificationAction>(descriptors.size());
		for (NotificationSinkDescriptor descriptor : descriptors) {
			NotificationAction action = new NotificationAction(descriptor);
			action.setSelected(event.isSelected());
			actions.add(action);
		}
		return actions;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void save(IMemento memento) {
		setDirty(false);
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public void setNotificationHandler(String eventId, NotificationHandler handler) {
		handlerByEventId.put(eventId, handler);
		setDirty(true);
	}

}
