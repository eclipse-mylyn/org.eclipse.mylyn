/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
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

import java.util.Date;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.commons.core.CoreUtil;

/**
 * A notification. Each notification has an associated <code>eventId</code> that identifies the type of the notification.
 * 
 * @author Rob Elves
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public abstract class AbstractNotification implements Comparable<AbstractNotification>, IAdaptable {

	private final String eventId;

	public AbstractNotification(String eventId) {
		Assert.isNotNull(eventId);
		this.eventId = eventId;
	}

	@Override
	public int compareTo(AbstractNotification o) {
		if (o == null) {
			return 1;
		}
		return CoreUtil.compare(getDate(), o.getDate());
	}

	public String getEventId() {
		return eventId;
	}

	public abstract Date getDate();

	public abstract String getDescription();

	public abstract String getLabel();

	/**
	 * Returns a token that identifies correlated notifications, e.g. all notifications resulting from a refresh operation. Returns
	 * <code>null</code> by default.
	 * 
	 * @return any object; null, if no token is specified
	 */
	public Object getToken() {
		return null;
	}

}
