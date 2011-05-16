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
package org.eclipse.mylyn.internal.github.core.issue;

import org.eclipse.osgi.util.NLS;

/**
 * NLS for Mylyn GitHub Core
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.github.core.issue.messages"; //$NON-NLS-1$

	/** */
	public static String IssueConnector_LabelConnector;

	/** */
	public static String IssueConector_TaskQuerying;

	/** */
	public static String IssueConnector_TaskUpdatingLabels;

	/** */
	public static String IssueConnector_TaskUpdatingMilestones;

	/** */
	public static String IssueAttribute_LabekSummary;

	/** */
	public static String IssueAttribute_LabelAssignee;

	/** */
	public static String IssueAttribute_LabelAssigneeGravatar;

	/** */
	public static String IssueAttribute_LabelClosed;

	/** */
	public static String IssueAttribute_LabelComment;

	/** */
	public static String IssueAttribute_LabelCreated;

	/** */
	public static String IssueAttribute_LabelDescription;

	/** */
	public static String IssueAttribute_LabelKey;

	/** */
	public static String IssueAttribute_LabelLabels;

	/** */
	public static String IssueAttribute_LabelMilestone;

	/** */
	public static String IssueAttribute_LabelModified;

	/** */
	public static String IssueAttribute_LabelReporter;

	/** */
	public static String IssueAttribute_LabelReporterGravatar;

	/** */
	public static String IssueAttribute_LabelStatus;

	/** */
	public static String IssueAttribute_MilestoneNone;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
