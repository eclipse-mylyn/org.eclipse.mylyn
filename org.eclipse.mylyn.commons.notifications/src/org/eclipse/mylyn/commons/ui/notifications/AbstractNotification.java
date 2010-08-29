/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui.notifications;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Image;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public abstract class AbstractNotification implements Comparable<AbstractNotification>, IAdaptable {

	private final String eventId;

	public AbstractNotification(String eventId) {
		Assert.isNotNull(eventId);
		this.eventId = eventId;
	}

	public String getEventId() {
		return eventId;
	}

	public abstract void open();

	public abstract String getDescription();

	public abstract String getLabel();

	public abstract Image getNotificationImage();

	public abstract Image getNotificationKindImage();

	public Object getToken() {
		return null;
	}

}
