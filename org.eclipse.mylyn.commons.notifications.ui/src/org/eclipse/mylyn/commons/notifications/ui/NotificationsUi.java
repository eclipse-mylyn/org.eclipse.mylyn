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
