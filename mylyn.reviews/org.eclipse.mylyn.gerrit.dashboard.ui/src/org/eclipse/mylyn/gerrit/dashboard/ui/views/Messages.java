/*******************************************************************************
 * Copyright (c) 2014 Ericsson AB and others.
 *
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Jacques Bouthillier - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.dashboard.ui.views;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.gerrit.dashboard.ui.views.messages"; //$NON-NLS-1$

	public static String GerritTableView_commandMessage;

	public static String GerritTableView_defineRepository;

	public static String GerritTableView_gerritLabel;

	public static String GerritTableView_missingGitConnector;

	public static String GerritTableView_noGerritRepository;

	public static String GerritTableView_popupMenu;

	public static String GerritTableView_refreshTable;

	public static String GerritTableView_search;

	public static String GerritTableView_serverNotRead;

	public static String GerritTableView_tooltipSearch;

	public static String GerritTableView_totalReview;

	public static String GerritTableView_warning;

	public static String GerritTableView_warningAnonymous;

	public static String GerritTableView_warningEmptyValue;

	public static String GerritTableView_warningSearchAnonymous;

	public static String GerritTableView_starredName;

	public static String GerritTableView_dashboardUiJob;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
