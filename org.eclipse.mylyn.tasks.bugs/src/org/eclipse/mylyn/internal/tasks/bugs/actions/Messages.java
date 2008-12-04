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

package org.eclipse.mylyn.internal.tasks.bugs.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.bugs.actions.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String NewTaskFromErrorAction_ERROR_LOG_DATE;

	public static String NewTaskFromErrorAction_MESSGAE;

	public static String NewTaskFromErrorAction_no_stack_trace_available;

	public static String NewTaskFromErrorAction_PLUGIN_ID;

	public static String NewTaskFromErrorAction_SEVERITY;

	public static String NewTaskFromErrorAction_STACK_TRACE;

	public static String NewTaskFromMarkerHandler_LOCATION_LINE;

	public static String NewTaskFromMarkerHandler_New_Task_from_Marker;

	public static String NewTaskFromMarkerHandler_No_marker_selected;

	public static String NewTaskFromMarkerHandler_Resource_;
}
