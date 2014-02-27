/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.egit;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.gerrit.ui.egit.messages"; //$NON-NLS-1$

	public static String GerritRepositorySearchPage_Add;

	public static String GerritRepositorySearchPage_Git_Repository;

	public static String GerritRepositorySearchPage_No_download_scheme;

	public static String GerritRepositorySearchPage_Refresh;

	public static String GerritRepositorySearchPage_Refreshing_X;

	public static String GerritRepositorySearchPage_Select_Gerrit_project;

	public static String GerritRepositorySearchPage_Source_Git_Repository;

	public static String GerritRepositorySearchPage_Unable_to_compute_clone_URI;

	public static String GerritRepositorySearchPage_URI;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
