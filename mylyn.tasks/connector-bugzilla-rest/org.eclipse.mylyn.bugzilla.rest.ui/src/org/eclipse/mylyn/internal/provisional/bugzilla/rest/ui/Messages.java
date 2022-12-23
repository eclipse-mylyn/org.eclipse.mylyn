/*******************************************************************************
 * Copyright (c) 2016 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.bugzilla.rest.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.provisional.bugzilla.rest.ui.messages"; //$NON-NLS-1$

	public static String BugzillaRestQueryTypeWizardPage_ChooseQueryType;

	public static String BugzillaRestQueryTypeWizardPage_CreateQueryFromExistingURL;

	public static String BugzillaRestQueryTypeWizardPage_CreateQueryUsingForm;

	public static String BugzillaRestQueryTypeWizardPage_Query;

	public static String BugzillaRestQueryTypeWizardPage_SelectAvailableQueryTypes;

	public static String BugzillaRestUiUtil_CreateQueryFromForm;

	public static String BugzillaRestUiUtil_CreateQueryFromURL;

	public static String BugzillaRestUiUtil_EnterQueryParameter;

	public static String BugzillaRestUiUtil_EnterQueryParameters;

	public static String BugzillaRestUiUtil_enterTitleAndFillForm;

	public static String BugzillaRestUiUtil_EnterTitleAndFillForm;

	public static String BugzillaRestUiUtil_EnterTitleAndURL;

	public static String BugzillaRestUiUtil_EnterTitleAndURL1;

	public static String BugzillaRestUiUtil_fillForm;

	public static String BugzillaRestUiUtil_FillForm;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
