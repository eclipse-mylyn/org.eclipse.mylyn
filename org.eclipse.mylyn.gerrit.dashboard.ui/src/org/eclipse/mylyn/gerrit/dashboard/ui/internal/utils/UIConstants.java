// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString, com.instantiations.assist.eclipse.analysis.deserializeabilitySecurity, com.instantiations.assist.eclipse.analysis.enforceCloneableUsageSecurity
/*******************************************************************************
 * Copyright (c) 2013 Ericsson AB and others.
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at>
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Description:
 * 
 * This class defines various constants used in the Gerrit Dashboard UI plugin
 * 
 * Contributors:
 *   Jacques Bouthillier - Add some definitions for Mylyn Review Gerrit Dashboard project
 *   
 ******************************************************************************/
package org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils;

import java.util.TimeZone;

/**
 * @authot Jacques Bouthillier
 * @version $Revision: 1.0 $
 */
public class UIConstants { // $codepro.audit.disable convertClassToInterface

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------

	/**
	 * Field TIME_ZONE_OFFSET. (value is "TimeZone.getDefault().getOffset(System.currentTimeMillis()")
	 */
	public final static long TIME_ZONE_OFFSET = TimeZone.getDefault().getOffset(System.currentTimeMillis());

	//Test Constants
	/**
	 * Field DASHBOARD_UI_JOB_FAMILY.
	 */
	public static final String DASHBOARD_UI_JOB_FAMILY = "DASHBOARD_UI";

	/**
	 * Field DEFAULT_REPOSITORY. (value is ""https://"")
	 */
	public static final String DEFAULT_REPOSITORY = "https://";

	/**
	 * Field ADD_GERRIT_SITE_COMMAND_ID. (value is ""org.eclipse.mylyn.gerrit.dashboard.ui.addGerritSite"")
	 */
	public static final String ADD_GERRIT_SITE_COMMAND_ID = "org.eclipse.mylyn.gerrit.dashboard.ui.addGerritSite";

	/**
	 * Field ADJUST_MY_STARRED_NAME. (value is ""Star Review"")
	 */
	public static final String ADJUST_MY_STARRED_NAME = "Star Review";

	//Test Constants
	/**
	 * Field ADJUST_MY_STARRED_COMMAND_ID. (value is ""org.eclipse.mylyn.gerrit.dashboard.ui.adjustMyStarred"")
	 */
	public static final String ADJUST_MY_STARRED_COMMAND_ID = "org.eclipse.mylyn.gerrit.dashboard.ui.adjustMyStarred";

}
