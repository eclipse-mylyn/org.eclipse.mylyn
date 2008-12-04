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

package org.eclipse.mylyn.internal.tasks.bugs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.bugs.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String DefaultTaskContributor_Bundle_Version;
	public static String DefaultTaskContributor_DATE;
	public static String DefaultTaskContributor_Error;
	public static String DefaultTaskContributor_Error_DETAILS;
	public static String DefaultTaskContributor_EXCEPTION_STACK_TRACE;
	public static String DefaultTaskContributor_Info;
	public static String DefaultTaskContributor_INSTALLED_FEATURES_AND_PLUGINS;
	public static String DefaultTaskContributor_MESSAGE;
	public static String DefaultTaskContributor_OK;
	public static String DefaultTaskContributor_PLUGIN;
	public static String DefaultTaskContributor_SESSION_DATA;
	public static String DefaultTaskContributor_SEVERITY;
	public static String DefaultTaskContributor_Warning;
}
