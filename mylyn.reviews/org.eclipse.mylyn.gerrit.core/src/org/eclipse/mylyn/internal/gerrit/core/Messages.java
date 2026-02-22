/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Marc-Andre Laperle (Ericsson) - Add topic
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.gerrit.core.messages"; //$NON-NLS-1$

	public static String GerritConnector_BranchTooltip;

	public static String GerritConnector_CodeReviewTooltip;

	public static String GerritConnector_Executing_query;

	public static String GerritConnector_Label;

	public static String GerritConnector_ProjectTooltip;

	public static String GerritConnector_VerifiedTooltip;

	public static String GerritQueryResultSchema_Branch;

	public static String GerritQueryResultSchema_ChangeId;

	public static String GerritQueryResultSchema_Project;

	public static String GerritQueryResultSchema_ReviewState;

	public static String GerritQueryResultSchema_Starred;

	public static String GerritQueryResultSchema_VerifyState;

	public static String GerritQueryResultSchema_Topic;

	public static String GerritTaskSchema_Publish;

	public static String GerritTaskSchema_Review;

	public static String GerritUtil_Anonymous;

	public static String GerritUtil_X_dot_dot_dot;

	public static String GerritUtil_Unknown;

	public static String GerritReviewBehavior_Internal_Exception;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
