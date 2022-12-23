/*******************************************************************************
 * Copyright (c) 2004, 2008 Mylyn project committers and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.tasks;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.java.tasks.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String JavaStackTraceFileHyperlink_Failed_to_open_type;

	public static String JavaStackTraceFileHyperlink_Line_not_found_in_type;

	public static String JavaStackTraceFileHyperlink_link_search_complete;

	public static String JavaStackTraceFileHyperlink_Open_Type;

	public static String JavaStackTraceFileHyperlink_Searching_;

	public static String JavaStackTraceFileHyperlink_Type_could_not_be_located;
}
