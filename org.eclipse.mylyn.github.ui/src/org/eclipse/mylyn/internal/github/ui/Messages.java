/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui;

import org.eclipse.osgi.util.NLS;

/**
 * NLS
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.github.ui.messages"; //$NON-NLS-1$

	/** */
	public static String CredentialsWizardPage_Description;

	/** */
	public static String CredentialsWizardPage_ErrorPassword;

	/** */
	public static String CredentialsWizardPage_ErrorUser;

	/** */
	public static String CredentialsWizardPage_LabelPassword;

	/** */
	public static String CredentialsWizardPage_LabelUser;

	/** */
	public static String CredentialsWizardPage_Title;

	/** */
	public static String RepositorySearchWizardPage_SearchButton;

	/** */
	public static String RepositorySearchWizardPage_SearchForRepositories;

	/** */
	public static String RepositorySelectionWizardPage_Description;

	/** */
	public static String RepositorySelectionWizardPage_ErrorLoading;

	/** */
	public static String RepositorySelectionWizardPage_LabelAddGist;

	/** */
	public static String RepositorySelectionWizardPage_LabelRepos;

	/** */
	public static String RepositorySelectionWizardPage_LabelSelectionCount;

	/** */
	public static String RepositorySelectionWizardPage_TaskFetchingOrganizationRepositories;

	/** */
	public static String RepositorySelectionWizardPage_TaskFetchingRepositories;

	/** */
	public static String RepositorySelectionWizardPage_Title;

	/** */
	public static String RepositorySelectionWizardPage_TooltipCheckAll;

	/** */
	public static String RepositorySelectionWizardPage_TooltipUncheckAll;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
