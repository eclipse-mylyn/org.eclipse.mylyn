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
 *     Itema AS - bug 331424 handle default event-sink action associations
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.notifications.ui;

import java.util.ArrayList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.commons.notifications.core.NotificationSink;

/**
 * Describes an event that is handled through a notification. The handling of event is stored in
 * {@link NotificationAction} objects that delegate to {@link NotificationSink} objects for the handling of actual
 * events.
 * 
 * @author Steffen Pingel
 * @author Torkild U. Resheim
 */
public class NotificationEvent extends NotificationElement {

	private static final String EXTENSION_POINT_ID = "org.eclipse.mylyn.commons.notifications.notifications"; //$NON-NLS-1$

	private NotificationCategory category;

	private boolean selected;

	private final ArrayList<String> defaultSinks;

	/**
	 * Tests whether or not the event should per default be handled by the sink with the specified identifier.
	 * 
	 * @param sinkId
	 *            the sink identifier
	 * @return <code>true</code> if the
	 */
	public boolean defaultHandledBySink(String sinkId) {
		if (defaultSinks.isEmpty() || defaultSinks.contains(sinkId)) {
			return true;
		}
		return false;
	}

	public NotificationEvent(IConfigurationElement element) {
		super(element);
		defaultSinks = new ArrayList<String>();
		IConfigurationElement[] children = element.getChildren("defaultHandler"); //$NON-NLS-1$
		for (IConfigurationElement child : children) {
			defaultSinks.add(child.getAttribute("sinkId")); //$NON-NLS-1$
		}
		doEventMappings();
	}

	private void doEventMappings() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(EXTENSION_POINT_ID);
		if (point != null) {
			IConfigurationElement[] elements = point.getConfigurationElements();
			for (IConfigurationElement mapping : elements) {
				if (mapping.getName().equals("eventMapping")) { //$NON-NLS-1$
					String eventIds = mapping.getAttribute("eventIds"); //$NON-NLS-1$
					String[] list = eventIds.split(","); //$NON-NLS-1$
					for (String item : list) {
						if (wildCardMatch(getId(), item)) {
							defaultSinks.add(mapping.getAttribute("sinkId")); //$NON-NLS-1$
						}
					}
				}
			}
		}
	}

	private boolean wildCardMatch(String text, String pattern) {
		String[] cards = pattern.split("\\*"); //$NON-NLS-1$
		for (String card : cards) {
			int idx = text.indexOf(card);
			if (idx == -1) {
				return false;
			}
			text = text.substring(idx + card.length());
		}

		return true;
	}

	public NotificationCategory getCategory() {
		return category;
	}

	public String getCategoryId() {
		return element.getAttribute("categoryId"); //$NON-NLS-1$
	}

	public String getDescription() {
		IConfigurationElement[] children = element.getChildren("description"); //$NON-NLS-1$
		if (children.length > 0) {
			return children[0].getValue();
		}
		return ""; //$NON-NLS-1$
	}

	public void setCategory(NotificationCategory category) {
		this.category = category;
	}

	@Deprecated
	public boolean isSelected() {
		return selected;
	}

	@Deprecated
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
