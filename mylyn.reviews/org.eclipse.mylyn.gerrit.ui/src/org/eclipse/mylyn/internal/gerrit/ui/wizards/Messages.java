/*******************************************************************************
 * Copyright (c) 2014, 2015 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.gerrit.ui.wizards.messages"; //$NON-NLS-1$

	public static String GerritCustomQueryPage_All_open_changes;

	public static String GerritCustomQueryPage_Custom_query;

	public static String GerritCustomQueryPage_Enter_title_and_select_query_type;

	public static String GerritCustomQueryPage_My_changes;

	public static String GerritCustomQueryPage_My_watched_changes;

	public static String GerritCustomQueryPage_Open_changes_by_project;

	public static String GerritCustomQueryPage_Open_Changes_in_X;

	public static String GerritCustomQueryPage_Query_type;

	public static String ProjectNameContentProposalProvider_Repository_configuration_needs_to_be_refreshed;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
