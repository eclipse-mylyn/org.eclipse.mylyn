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
