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

	public static String AbstractFilteredTree_Clear;

	public static String AbstractFilteredTree_Find;

	public static String AbstractNotificationPopup_Close_Notification_Job;

	public static String AbstractNotificationPopup_Notification;

	public static String AbstractRetrieveTitleFromUrlJob_Retrieving_summary_from_URL;

	public static String DatePicker_Choose_Date;

	public static String DateSelectionDialog_Clear;

	public static String DateSelectionDialog_Date_Selection;

	public static String ScreenshotCreationPage_After_capturing;

	public static String ScreenshotCreationPage_Annotate;

	public static String ScreenshotCreationPage_Capture_Desktop;

	public static String ScreenshotCreationPage_Capture_Desktop_C;

	public static String ScreenshotCreationPage_CAPTURE_SCRRENSHOT;

	public static String ScreenshotCreationPage_Change_pen_color;

	public static String ScreenshotCreationPage_Clear_all_annotations_made_on_screenshot_image;

	public static String ScreenshotCreationPage_Clear_Annotations;

	public static String ScreenshotCreationPage_Crop;

	public static String ScreenshotCreationPage_Crop_R;

	public static String ScreenshotCreationPage_DRAW_ANNOTATION_ON_SCREENSHOT_IMAGE;

	public static String ScreenshotCreationPage_Fit_Image;

	public static String ScreenshotCreationPage_Fit_Image_F;

	public static String ScreenshotCreationPage_NOTE_THAT_YOU_CONTINUTE;
}
