/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.spi.remote.emf;

import org.eclipse.emf.common.notify.Notification;

class RemoteNotification {

	private final boolean modification;

	private final int type;

	/**
	 * An {@link Notification#getEventType event type} indicating that the object has been updated from a remote source.
	 */
	static final int REMOTE_UPDATE = 1;

	/**
	 * An {@link Notification#getEventType event type} indicating that the object is in the process of being updated from a remote source.
	 */
	static final int REMOTE_UPDATING = 2;

	private RemoteNotification(int type, boolean modification) {
		this.type = type;
		this.modification = modification;
	}

	static RemoteNotification createUpdateNotification(boolean modified) {
		return new RemoteNotification(REMOTE_UPDATE, modified);
	}

	static RemoteNotification createUpdatingNotification() {
		return new RemoteNotification(REMOTE_UPDATING, false);
	}

	int getType() {
		return type;
	}

	/**
	 * Returns true if the notification represents an actual change to the model object state.
	 *
	 * @return
	 */
	boolean isModification() {
		return modification;
	}
}