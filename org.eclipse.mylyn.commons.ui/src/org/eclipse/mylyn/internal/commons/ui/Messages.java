/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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

	public static String WorkbenchUtil_Browser_Initialization_Failed;

	public static String WorkbenchUtil_Invalid_URL_Error;

	public static String WorkbenchUtil_No_URL_Error;

	public static String WorkbenchUtil_Open_Location_Title;

	public static String ColorSelectionWindow_Close;

	public static String SwtUtil_Fading;

	public static String AbstractFilteredTree_Find;

	public static String AbstractNotificationPopup_Close_Notification_Job;

	public static String AbstractNotificationPopup_Notification;

	public static String DatePicker_Choose_Date;

	public static String DateSelectionDialog_Clear;

	public static String DateSelectionDialog_Date_Selection;

	public static String InPlaceDateSelectionDialog_Today;

	public static String ScreenshotCreationPage_After_capturing;

	public static String ScreenshotCreationPage_Annotate;

	public static String ScreenshotCreationPage_Capture_Desktop;

	public static String ScreenshotCreationPage_Capture_Desktop_C;

	public static String ScreenshotCreationPage_CAPTURE_SCRRENSHOT;

	public static String ScreenshotCreationPage_Clear_all_annotations_made_on_screenshot_image;

	public static String ScreenshotCreationPage_Clear;

	public static String ScreenshotCreationPage_Undo;

	public static String ScreenshotCreationPage_Undo_annotation;

	public static String ScreenshotCreationPage_Redo;

	public static String ScreenshotCreationPage_Redo_annotation;

	public static String ScreenshotCreationPage_Crop;

	public static String ScreenshotCreationPage_Crop_R;

	public static String ScreenshotCreationPage_DRAW_ANNOTATION_ON_SCREENSHOT_IMAGE;

	public static String ScreenshotCreationPage_Fit_Image;

	public static String ScreenshotCreationPage_Fit_Image_F;

	public static String ScreenshotCreationPage_NOTE_THAT_YOU_CONTINUTE;

	public static String ScreenshotCreationPage_Show_Line_Type_Selector;

	public static String ScreenshotCreationPage_Show_Line_Bold_Selector;

	public static String SelectToolAction_Font_Bold;

	public static String SelectToolAction_Font_Italic;

	public static String SelectToolAction_Font_Name_Size;

	public static String SelectToolAction_1dot;

	public static String SelectToolAction_2dots;

	public static String SelectToolAction_4dots;

	public static String SelectToolAction_8dots;

	public static String SelectToolAction_Clipboard;

	public static String SelectToolAction_Desktop;

	public static String SelectToolAction_File;

	public static String SelectToolAction_Rectangle;

	public static String SelectToolAction_Round_Rectangle;

	public static String SelectToolAction_Oval;

	public static String SelectToolAction_Fill_Rectangle;

	public static String SelectToolAction_Fill_Round_Rectangle;

	public static String SelectToolAction_Fill_Oval;

	public static String SelectToolAction_Free;

	public static String SelectToolAction_Line;

	public static String SelectToolAction_Single_Side_Arrow;

	public static String SelectToolAction_Both_Side_Arrow;

	public static String SelectToolAction_Dashed_Line;

	public static String SelectToolAction_Dashed_Line_1_dot;

	public static String SelectToolAction_Dashed_Line_2_dots;

	public static String SelectToolAction_Dotted_Line;

	public static String SelectToolAction_Solid_Line;

	public static String SelectToolAction_Fit;

	public static String SelectToolAction_ZoomHalf;

	public static String SelectToolAction_Zoom1X;

	public static String SelectToolAction_Zoom2X;

	public static String SelectToolAction_Zoom4X;

	public static String SelectToolAction_Zoom8X;

	public static String SelectToolAction_Selected_Rectangle;

	public static String SelectToolAction_Font_;

	public static String SelectToolAction_Color_;
}
