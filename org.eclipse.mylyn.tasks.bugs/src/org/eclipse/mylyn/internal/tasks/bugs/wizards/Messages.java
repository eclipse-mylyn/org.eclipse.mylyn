/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.bugs.wizards.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String ReportBugOrEnhancementWizard_Report_Bug_or_Enhancement;

	public static String ReportErrorPage_Details;

	public static String ReportErrorPage_Report_as_Bug;

	public static String ReportErrorPage_Report_to_;

	public static String ReportErrorPage_Select_repository;

	public static String ReportErrorPage_AN_UNEXPETED_ERROR_HAS_OCCURED_IN_PLUGIN;

	public static String ReportErrorWizard_Report_as_Bug;

	public static String SelectFeaturePage_SELECT_FEATURE;

	public static String SelectProductPage_Other;

	public static String SelectProductPage_SELECT_PRODUCT;
}
