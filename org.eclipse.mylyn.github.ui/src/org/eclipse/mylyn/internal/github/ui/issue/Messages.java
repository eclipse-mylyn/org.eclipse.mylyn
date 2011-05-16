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
package org.eclipse.mylyn.internal.github.ui.issue;

import org.eclipse.osgi.util.NLS;

/**
 * NLS
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.github.ui.issue.messages"; //$NON-NLS-1$

	/** */
	public static String IssueRepositoryQueryPage_ErrorLoading;

	/** */
	public static String IssueRepositoryQueryPage_AssigneeLabel;

	/** */
	public static String IssueRepositoryQueryPage_Description;

	/** */
	public static String IssueRepositoryQueryPage_ErrorStatus;

	/** */
	public static String IssueRepositoryQueryPage_LabelsLabel;

	/** */
	public static String IssueRepositoryQueryPage_MentionsLabel;

	/** */
	public static String IssueRepositoryQueryPage_MilestoneLabel;

	/** */
	public static String IssueRepositoryQueryPage_MilestoneNone;

	/** */
	public static String IssueRepositoryQueryPage_StatusClosed;

	/** */
	public static String IssueRepositoryQueryPage_StatusLabel;

	/** */
	public static String IssueRepositoryQueryPage_StatusOpen;

	/** */
	public static String IssueRepositoryQueryPage_TaskLoadingLabels;

	/** */
	public static String IssueRepositoryQueryPage_TaskLoadingMilestones;

	/** */
	public static String IssueRepositoryQueryPage_TitleLabel;

	/** */
	public static String IssueRepositoryQueryPage_TooltipUpdateRepository;

	/** */
	public static String IssueRepositorySettingsPage_Description;

	/** */
	public static String IssueRepositorySettingsPage_StatusError;

	/** */
	public static String IssueRepositorySettingsPage_StatusSuccess;

	/** */
	public static String IssueRepositorySettingsPage_TaskContactingServer;

	/** */
	public static String IssueRepositorySettingsPage_TaskValidating;

	/** */
	public static String IssueRepositorySettingsPage_Title;

	/** */
	public static String IssueTaskEditorPageFactory_PageText;

	/** */
	public static String IssueLabelAttributeEditor_ActionNewLabel;

	/** */
	public static String IssueLabelAttributeEditor_ActionRemoveLabel;

	/** */
	public static String IssueLabelAttributeEditor_DescriptionNewLabel;

	/** */
	public static String IssueLabelAttributeEditor_MessageEnterName;

	/** */
	public static String IssueLabelAttributeEditor_TitleNewLabel;

	/** */
	public static String IssueLabelAttributeEditor_TooltipAddLabel;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
