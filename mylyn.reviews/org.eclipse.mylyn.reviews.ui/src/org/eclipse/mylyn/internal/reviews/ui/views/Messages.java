/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.views;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.reviews.ui.views.messages"; //$NON-NLS-1$

	public static String ReviewExplorer_Change_X_colon_Y;

	public static String ReviewExplorer_Filter_for_Comments;

	public static String ReviewExplorer_Filter_for_Comments_description;

	public static String ReviewExplorer_Filter_for_Comments_tooltip;

	public static String ReviewExplorer_No_Selection;

	public static String ReviewExplorer_Refresh;

	public static String ReviewExplorer_Refresh_description;

	public static String ReviewExplorer_Refresh_tooltip;

	public static String ReviewExplorer_Show_List;

	public static String ReviewExplorer_Show_List_description;

	public static String ReviewExplorer_Show_List_tooltip;

	public static String ReviewExplorer_Show_Tree;

	public static String ReviewExplorer_Show_Tree_description;

	public static String ReviewExplorer_Show_Tree_tooltip;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
