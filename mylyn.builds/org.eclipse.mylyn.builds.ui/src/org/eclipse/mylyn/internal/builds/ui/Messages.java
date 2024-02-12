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

package org.eclipse.mylyn.internal.builds.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$

	public static String BuildNotification_planLabelStatus;

	public static String BuildRefresher_backgroundBuildsRefresh;

	public static String BuildServerValidator_serverValidationFailed;

	public static String BuildsPreferencesPage_automaticallyRefreshBuilds;

	public static String BuildsPreferencesPage_refreshBuildStatusEveryInMinutes;

	public static String BuildsPreferencesPage_refreshWhenBuildsViewIsFocused;

	public static String BuildsStartup_initializingBuildsView;

	public static String BuildsUiInternal_ConnectorKindUnknown;

	public static String BuildsUiInternal_noConnectorKindWasSpecified;

	public static String BuildsUiInternal_UnexpectedErrorLoadingConnector;

	public static String BuildsUiInternal_UNexpectedErrorLoadingConnectorReturnedNUll;

	public static String BuildsUiPlugin_unexpectedErrorWhileSavingBuilds;

	public static String BuildToolTip_abortedBuilds;

	public static String BuildToolTip_build;

	public static String BuildToolTip_disabledBuilds;

	public static String BuildToolTip_failedBuilds;

	public static String BuildToolTip_lastBuilt;

	public static String BuildToolTip_passedBuilds;

	public static String BuildToolTip_refreshed;

	public static String BuildToolTip_statusAborted;

	public static String BuildToolTip_statusDisabled;

	public static String BuildToolTip_statusFailed;

	public static String BuildToolTip_statusQueued;

	public static String BuildToolTip_statusRunning;

	public static String BuildToolTip_statusSuccess;

	public static String BuildToolTip_statusUnstable;

	public static String BuildToolTip_took;

	public static String BuildToolTip_unustableBuilds;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
