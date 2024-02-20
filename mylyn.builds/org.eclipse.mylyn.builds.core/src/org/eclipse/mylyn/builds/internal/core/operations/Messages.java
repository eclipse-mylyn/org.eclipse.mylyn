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

package org.eclipse.mylyn.builds.internal.core.operations;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$

	public static String AbortBuildOperation_abortingBuild;

	public static String AbortBuildOperation_abortingBuildFailed;

	public static String AbortBuildOperation_buildAbortFailed;

	public static String AbstractElementOperation_failed;

	public static String AbstractElementOperation_result;

	public static String GetBuildOutputOperation_failedRetrievingOutput;

	public static String GetBuildOutputOperation_retrievingOutputForBuild;

	public static String GetBuildOutputOperation_unexpectedError;

	public static String GetBuildOutputOperation_unknown;

	public static String GetBuildsOperation_buildFor;

	public static String GetBuildsOperation_buildRetrieveFailed;

	public static String GetBuildsOperation_retrieveFailed;

	public static String RefreshConfigurationOperation_configurationRefreshFailed;

	public static String RefreshConfigurationOperation_invalidServerConfiguration;

	public static String RefreshConfigurationOperation_refreshConfiguration;

	public static String RefreshConfigurationOperation_refreshingConfiguration;

	public static String RefreshConfigurationOperation_refreshingServerConfiguration;

	public static String RefreshConfigurationOperation_subTaskLabel;

	public static String RefreshConfigurationOperation_serverRefreshFailed;

	public static String RefreshOperation_refreshBuildFailed;

	public static String RefreshOperation_refreshingBuild;

	public static String RefreshSession_serverDidNotProvidePlans;

	public static String RefreshSession_planDoesNotExist;

	public static String RunBuildOperation_buildFailed;

	public static String RunBuildOperation_runningBuild;

	public static String RunBuildOperation_specificBuildFailed;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
