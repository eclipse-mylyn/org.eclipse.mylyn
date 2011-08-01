/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.ui.pr;

import org.eclipse.osgi.util.NLS;

/**
 * NLS
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.github.ui.pr.messages"; //$NON-NLS-1$

	/** */
	public static String CheckoutPullRequestHandler_JobName;
	/** */
	public static String CheckoutPullRequestHandler_TaskAddRemote;
	/** */
	public static String CheckoutPullRequestHandler_TaskCheckoutBranch;
	/** */
	public static String CheckoutPullRequestHandler_TaskCreateBranch;
	/** */
	public static String CheckoutPullRequestHandler_TaskPullChanges;
	/** */
	public static String CommitAttributePart_LabelDestination;
	/** */
	public static String CommitAttributePart_LabelSource;
	/** */
	public static String CommitAttributePart_MessageClickToFetch;
	/** */
	public static String CommitAttributePart_MessageFetch;
	/** */
	public static String CommitAttributePart_PartName;
	/** */
	public static String CommitAttributePart_SectionCommits;
	/** */
	public static String CommitAttributePart_TitleFetch;
	/** */
	public static String FetchPullRequestHandler_JobName;
	/** */
	public static String PullRequestCommitAdapter_AuthorWithDate;
	/** */
	public static String PullRequestConnectorUi_LabelKind;
	/** */
	public static String PullRequestRepositoryQueryPage_Description;
	/** */
	public static String PullRequestRepositoryQueryPage_LabelStatus;
	/** */
	public static String PullRequestRepositoryQueryPage_LabelTitle;
	/** */
	public static String PullRequestRepositoryQueryPage_MessageSelectStatus;
	/** */
	public static String PullRequestRepositoryQueryPage_StatusClosed;
	/** */
	public static String PullRequestRepositoryQueryPage_StatusOpen;
	/** */
	public static String PullRequestRepositorySettingsPage_Description;
	/** */
	public static String PullRequestRepositorySettingsPage_TaskContacting;
	/** */
	public static String PullRequestRepositorySettingsPage_TaskValidating;
	/** */
	public static String PullRequestRepositorySettingsPage_Title;
	/** */
	public static String PullRequestRepositorySettingsPage_ValidateError;
	/** */
	public static String PullRequestRepositorySettingsPage_ValidateSuccess;
	/** */
	public static String PullRequestTaskEditorPageFactory_PageText;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
