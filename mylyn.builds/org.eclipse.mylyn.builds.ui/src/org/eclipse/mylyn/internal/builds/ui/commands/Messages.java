/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.commands;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.builds.ui.commands.messages"; //$NON-NLS-1$

	public static String BuildUrlCommandHandler_Downloading_Build_X;

	public static String BuildUrlCommandHandler_Open_with_Browser;

	public static String CopyDetailsHandler_buildLabel;

	public static String CopyDetailsHandler_id;

	public static String CopyDetailsHandler_invalidKindSpecified;

	public static String CopyDetailsHandler_summary;

	public static String CopyDetailsHandler_summaryAndUrl;

	public static String CopyDetailsHandler_url;

	public static String DeleteBuildElementHandler_areYouSure;

	public static String DeleteBuildElementHandler_deleteBuildServer;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
