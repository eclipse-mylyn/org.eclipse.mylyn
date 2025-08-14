/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.context.ui.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String AbstractFocusViewAction_Apply_Mylyn;

	public static String AbstractFocusViewAction_Empty_task_context;

	public static String CompoundContextComputationStrategy_Computing_Context_Task_Label;

	public static String ContextPopulationStrategy_Populate_Context_Job_Label;

	public static String FilteredChildrenDecorationDrawer_No_Filtered_Children;

	public static String FilteredChildrenDecorationDrawer_Show_Filtered_Children;

}
