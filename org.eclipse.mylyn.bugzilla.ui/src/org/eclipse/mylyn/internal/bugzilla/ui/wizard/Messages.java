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

package org.eclipse.mylyn.internal.bugzilla.ui.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.bugzilla.ui.wizard.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String BugzillaProductPage_Bugzilla_Search_Page;
	public static String BugzillaProductPage_Error_reported;
	public static String BugzillaProductPage_Error_updating_product_list;
	public static String BugzillaProductPage_New_Bugzilla_Task_Error;
	public static String BugzillaProductPage_PAGE_1;
	public static String BugzillaProductPage_PICK_PRODUCT_TO_OPEN_NEW_BUG_EDITOR;
	public static String BugzillaProductPage_PRESS_UPDATE_BUTTON;
	public static String BugzillaProductPage_Unable_to_get_configuration;
	public static String BugzillaProductPage_Unable_to_get_products;
	public static String BugzillaProductPage_Update_Products_from_Repository;
	public static String BugzillaProductPage_Updating_repository_report_options_;
	public static String BugzillaProductPage_YOU_MUST_SELECT_PRODUCT;
}
