/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.commons.ui.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String ColorSelectionWindow_Close;

	public static String SwtUtil_Fading;

	public static String AbstractNotificationPopup_Close_Notification_Job;

	public static String AbstractNotificationPopup_Notification;

	public static String DateSelectionDialog_Clear;

	public static String ScreenshotCreationPage_After_capturing;

	public static String ScreenshotCreationPage_CAPTURE_SCRRENSHOT;

	public static String ScreenshotCreationPage_NOTE_THAT_YOU_CONTINUTE;

	public static String AbstractColumnViewerSupport_Restore_defaults;

	public static String CollapseAllAction_Label;

	public static String CollapseAllAction_ToolTip;

	public static String ExpandAllAction_Label;

	public static String ExpandAllAction_ToolTip;

	public static String ValidatableWizardDialog_Validate_Button_Label;

}
