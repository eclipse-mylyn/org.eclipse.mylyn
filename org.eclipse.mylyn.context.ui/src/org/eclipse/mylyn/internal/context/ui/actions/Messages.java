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

package org.eclipse.mylyn.internal.context.ui.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.context.ui.actions.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String AbstractInterestManipulationAction_Interest_Manipulation;

	public static String AbstractInterestManipulationAction_No_task_context_is_active;

	public static String AbstractInterestManipulationAction_Not_a_valid_landmark;

	public static String FocusTaskListAction_No_tasks_scheduled_for_this_week;

	public static String ToggleDecorateInterestLevelAction_Decorate_Interest;

	public static String ToggleDecorateInterestLevelAction_Toggle_Interest_Level_Decorator;
}
