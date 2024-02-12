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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.view;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.builds.ui.view.messages"; //$NON-NLS-1$

	public static String BuildContentProvider_plans;

	public static String BuildContentProvider_servers;

	public static String BuildElementPropertiesAction_properties;

	public static String BuildElementPropertiesAction_propertiesToolTip;

	public static String BuildsView_Build;

	public static String BuildsView_BuildStatusSummary;

	public static String BuildsView_HideSucceedingPlans;

	public static String BuildsView_LastBuilt;

	public static String BuildsView_LastUpdate;

	public static String BuildsView_LastUpdateFailed;

	public static String BuildsView_NoServersAvailable;

	public static String BuildsView_ShowTextFilter;

	public static String BuildsView_Summary;

	public static String FilterByStatusAction_hideDisabledPlans;

	public static String NewBuildServerAction_addBuildServer;

	public static String NewBuildServerAction_addBuildServerLocation;

	public static String NewBuildServerMenuAction_newBuildServerLocation;

	public static String NewBuildServerMenuAction_propertiesFor;

	public static String NewBuildServerMenuAction_showRepositoriesView;

	public static String OpenBuildsPreferencesAction_buildPreferences;

	public static String OpenBuildsPreferencesAction_preferences;

	public static String OpenWithBrowserAction_openWithBrowser;

	public static String OpenWithBrowserAction_openWithBrowserToolTip;

	public static String ParametersDialog_buildPlan;

	public static String ParametersDialog_provideBuildParameters;

	public static String ParametersDialog_runBuild;

	public static String ParametersDialog_unexpectedControlType;

	public static String ParametersDialog_unexpectedDefinitionType;

	public static String PresentationMenuAction_topLevelElements;

	public static String RefreshAction_refreshTooltip;

	public static String RefreshAutomaticallyAction_refreshAutomatically;

	public static String RelativeBuildTimeLabelProvider_lessThanAMinuteAgo;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
