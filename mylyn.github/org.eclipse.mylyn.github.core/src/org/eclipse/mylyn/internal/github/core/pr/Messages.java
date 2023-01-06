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
package org.eclipse.mylyn.internal.github.core.pr;

import org.eclipse.osgi.util.NLS;

/**
 * NLS for Mylyn GitHub Core
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.github.core.pr.messages"; //$NON-NLS-1$

	/** */
	public static String PullRequestAttribute_LabelClosedAt;

	/** */
	public static String PullRequestAttribute_LabelComment;

	/** */
	public static String PullRequestAttribute_LabelCreatedAt;

	/** */
	public static String PullRequestAttribute_LabelDescription;

	/** */
	public static String PullRequestAttribute_LabelKey;

	/** */
	public static String PullRequestAttribute_LabelMergedAt;

	/** */
	public static String PullRequestAttribute_LabelModel;

	/** */
	public static String PullRequestAttribute_LabelModifiedAt;

	/** */
	public static String PullRequestAttribute_LabelReporter;

	/** */
	public static String PullRequestAttribute_LabelStatus;

	/** */
	public static String PullRequestAttribute_LabelSummary;

	/** */
	public static String PullRequestConnector_Label;

	/** */
	public static String PullRequestConnector_LabelPullRequests;

	/** */
	public static String PullRequestConnector_TaskFetching;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
