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

package org.eclipse.mylyn.internal.context.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.context.ui.preferences.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String ContextUiPreferencePage_Auto_expand_tree_views_when_focused;

	public static String ContextUiPreferencePage_Auto_focus_navigator_views_on_task_activation;

	public static String ContextUiPreferencePage_CONFIGURE_TASK_FOCUSED_UI_MANAGEMENT_AND_AUTOMATION;

	public static String ContextUiPreferencePage_Context;

	public static String ContextUiPreferencePage_Editors;

	public static String ContextUiPreferencePage_Manage_open_editors_to_match_task_context;

	public static String ContextUiPreferencePage_Open_last_used_perspective_on_task_activation;

	public static String ContextUiPreferencePage_Perspectives;

	public static String ContextUiPreferencePage_Remove_file_from_context_when_editor_is_closed;

	public static String ContextUiPreferencePage_Views;

	public static String ContextUiPreferencePage_will_be_toggled_with_activation;
}
