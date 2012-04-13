/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui.notifications;

import org.eclipse.osgi.util.NLS;

/**
 * @deprecated use classes in the <code>org.eclipse.mylyn.commons.notifications.core</code> bundle instead
 */
@Deprecated
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.commons.ui.notifications.messages"; //$NON-NLS-1$

	public static String NotificationsPreferencesPage_Descriptions_Label;

	public static String NotificationsPreferencesPage_Enable_Notifications_Text;

	public static String NotificationsPreferencesPage_Events_Label;

	public static String NotificationsPreferencesPage_Notifiers_Label;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
