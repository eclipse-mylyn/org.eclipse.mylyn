/*******************************************************************************
 * Copyright (c) 2024 George Lindholm
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *      See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.history;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$

	public static String BuildHistoryPage_buildHistoryFor;

	public static String BuildHistoryPage_buildPlan;

	public static String BuildHistoryPage_duration;

	public static String BuildHistoryPage_id;

	public static String BuildHistoryPage_summary;

	public static String BuildHistoryPage_time;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
