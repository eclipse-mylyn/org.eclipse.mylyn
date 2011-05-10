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
package org.eclipse.mylyn.github.internal;

import org.eclipse.osgi.util.NLS;

/**
 * NLS for Mylyn GitHub Core
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mylyn.github.internal.messages"; //$NON-NLS-1$

	/** */
	public static String FieldError_InvalidField;

	/** */
	public static String FieldError_InvalidFieldWithValue;

	/** */
	public static String FieldError_MissingField;

	/** */
	public static String FieldError_ResourceError;

	/** */
	public static String GitHubRepositoryConnector_LabelConnector;

	/** */
	public static String GitHubRepositoryConnector_TaskQuerying;

	/** */
	public static String GitHubRepositoryConnector_TaskUpdatingLabels;

	/** */
	public static String GitHubRepositoryConnector_TaskUpdatingMilestones;

	/** */
	public static String GitHubTaskAttributes_LabekSummary;

	/** */
	public static String GitHubTaskAttributes_LabelAssignee;

	/** */
	public static String GitHubTaskAttributes_LabelAssigneeGravatar;

	/** */
	public static String GitHubTaskAttributes_LabelClosed;

	/** */
	public static String GitHubTaskAttributes_LabelComment;

	/** */
	public static String GitHubTaskAttributes_LabelCreated;

	/** */
	public static String GitHubTaskAttributes_LabelDescription;

	/** */
	public static String GitHubTaskAttributes_LabelKey;

	/** */
	public static String GitHubTaskAttributes_LabelLabels;

	/** */
	public static String GitHubTaskAttributes_LabelMilestone;

	/** */
	public static String GitHubTaskAttributes_LabelModified;

	/** */
	public static String GitHubTaskAttributes_LabelReporter;

	/** */
	public static String GitHubTaskAttributes_LabelReporterGravatar;

	/** */
	public static String GitHubTaskAttributes_LabelStatus;

	/** */
	public static String GitHubTaskDataHandler_MilestoneNone;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
