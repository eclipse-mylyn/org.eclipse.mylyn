/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.notifications.ui;

import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.swt.graphics.Image;

/**
 * A notification with UI specific extensions.
 * 
 * @author Steffen Pingel
 */
public abstract class AbstractUiNotification extends AbstractNotification {

	public AbstractUiNotification(String eventId) {
		super(eventId);
	}

	public abstract Image getNotificationImage();

	public abstract Image getNotificationKindImage();

	/**
	 * Executes the default action for opening the notification.
	 */
	public abstract void open();

}
