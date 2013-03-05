/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.spi.remote.emf;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;

/**
 * Supports specialized remote EMF notifications. Of course, consumers can still listen for {@link Notification#ADD}
 * {@link Notification#CREATE} and other notifications, but there notification concerns unique to a remote /
 * asynchronous context. These notifications also provide for more coarse grained notifications so that consumers can
 * respond to changes at the object rather than field level.
 * 
 * @author Miles Parker
 */
public interface RemoteNotification extends Notification {

	/**
	 * An {@link Notification#getEventType event type} indicating that a member has been created representing a remote
	 * object.
	 */
	int REMOTE_MEMBER_CREATE = 1001;

	/**
	 * An {@link Notification#getEventType event type} indicating that a member has been updated from a remote object.
	 * (The member may or may not have been actually modified; see {@link #isModification()}.)
	 */
	int REMOTE_MEMBER_UPDATE = 1002;

	/**
	 * An {@link Notification#getEventType event type} indicating that a member object is in the process of being
	 * updated.
	 */
	int REMOTE_MEMBER_UPDATING = 1003;

	/**
	 * An {@link Notification#getEventType event type} indicating that a failure has occurred when attempting to create
	 * or update a member object.
	 */
	int REMOTE_MEMBER_FAILURE = 1004;

	/**
	 * (There is no REMOTE_CREATE type, because there would be no object from which to listen to it!)
	 */

	/**
	 * An {@link Notification#getEventType event type} indicating that the object has been updated from a remote source.
	 */
	int REMOTE_UPDATE = 1012;

	/**
	 * An {@link Notification#getEventType event type} indicating that the object is in the process of being updated
	 * from a remote source.
	 */
	int REMOTE_UPDATING = 1013;

	/**
	 * An {@link Notification#getEventType event type} indicating that a failure has occurred when attempting to update
	 * the object.
	 */
	int REMOTE_FAILURE = 1014;

	/**
	 * Returns true if a remote operation has just completed. This is true for REMOTE_MEMBER_UPDATE,
	 * REMOTE_MEMBER_FAILURE, REMOTE_UPDATE and REMOTE_FAILURE.
	 * 
	 * @return
	 */
	boolean isDone();

	boolean isMember();

	/**
	 * Returns true if the notification represents an actual change to the model object state.
	 * 
	 * @return
	 */
	boolean isModification();

	IStatus getStatus();
}
