/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
