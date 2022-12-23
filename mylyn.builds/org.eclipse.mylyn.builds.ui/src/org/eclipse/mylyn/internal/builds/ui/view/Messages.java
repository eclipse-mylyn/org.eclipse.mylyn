/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.view;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.builds.ui.view.messages"; //$NON-NLS-1$

	public static String BuildsView_Build;

	public static String BuildsView_BuildStatusSummary;

	public static String BuildsView_HideSucceedingPlans;

	public static String BuildsView_LastBuilt;

	public static String BuildsView_LastUpdate;

	public static String BuildsView_LastUpdateFailed;

	public static String BuildsView_NoServersAvailable;

	public static String BuildsView_ShowTextFilter;

	public static String BuildsView_Summary;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
