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

package org.eclipse.mylyn.commons.notifications.ui;

import org.eclipse.mylyn.commons.notifications.core.INotificationService;
import org.eclipse.mylyn.internal.commons.notifications.ui.NotificationsPlugin;

/**
 * @author Steffen Pingel
 */
public final class NotificationsUi {

	private NotificationsUi() {
		// do not instantiate
	}

	public static INotificationService getService() {
		return NotificationsPlugin.getDefault().getService();
	}

}
