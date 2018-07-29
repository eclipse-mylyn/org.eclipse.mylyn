/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
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
	public static String RepositoryImportWizard_CloningRepositories;

	/** */
	public static String RepositoryImportWizard_Cloning;

	/** */
	public static String RepositoryImportWizard_CloningRepository;

	/** */
	public static String RepositoryImportWizard_CreatingOperation;

	/** */
	public static String RepositoryImportWizard_Registering;

	/** */
	public static String RepositorySearchWizardPage_Found;

	/** */
	public static String RepositorySearchWizardPage_Error;

	/** */
	public static String RepositorySearchWizardPage_AnyLanguage;

	/** */
	public static String RepositorySearchWizardPage_Description;

	/** */
	public static String RepositorySearchWizardPage_SearchButton;

	/** */
	public static String RepositorySearchWizardPage_SearchForRepositories;

	/** */
	public static String RepositorySearchWizardPage_Searching;

	/** */
	public static String RepositorySearchWizardPage_Title;

	/** */
	public static String RepositorySearchWizardPage_counters;

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

	/** */
	public static String TaskRepositoryImportWizard_Title;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
