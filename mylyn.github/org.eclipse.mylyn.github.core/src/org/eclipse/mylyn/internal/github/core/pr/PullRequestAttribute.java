/******************************************************************************
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
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.core.pr;

import org.eclipse.mylyn.internal.github.core.GitHubAttributeMetadata;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * GitHub pull request attributes
 */
public enum PullRequestAttribute {

	/**
	 * Pull request key
	 */
	KEY(Messages.PullRequestAttribute_LabelKey, TaskAttribute.TASK_KEY,
			TaskAttribute.TYPE_SHORT_TEXT, true, true),

	/**
	 * Pull request title
	 */
	TITLE(Messages.PullRequestAttribute_LabelSummary, TaskAttribute.SUMMARY,
			TaskAttribute.TYPE_SHORT_RICH_TEXT, false, true),

	/**
	 * Pull request description
	 */
	BODY(Messages.PullRequestAttribute_LabelDescription,
			TaskAttribute.DESCRIPTION, TaskAttribute.TYPE_LONG_RICH_TEXT,
			false, true),

	/**
	 * Pull request creation date
	 */
	CREATION_DATE(Messages.PullRequestAttribute_LabelCreatedAt,
			TaskAttribute.DATE_CREATION, TaskAttribute.TYPE_DATETIME, true,
			false),

	/**
	 * Pull request modification date
	 */
	MODIFICATION_DATE(Messages.PullRequestAttribute_LabelModifiedAt,
			TaskAttribute.DATE_MODIFICATION, TaskAttribute.TYPE_DATETIME, true,
			false),

	/**
	 * Pull request closed date
	 */
	CLOSED_DATE(Messages.PullRequestAttribute_LabelClosedAt,
			TaskAttribute.DATE_COMPLETION, TaskAttribute.TYPE_DATETIME, true,
			false),

	/**
	 * Pull request merged date
	 */
	MERGED_DATE(Messages.PullRequestAttribute_LabelMergedAt,
			"github.pr.merged.at", TaskAttribute.TYPE_DATETIME, true, false), //$NON-NLS-1$

	/**
	 * Pull request status
	 */
	STATUS(Messages.PullRequestAttribute_LabelStatus, TaskAttribute.STATUS,
			TaskAttribute.TYPE_SHORT_TEXT, false, true),

	/**
	 * Pull request reporter
	 */
	REPORTER(Messages.PullRequestAttribute_LabelReporter,
			TaskAttribute.USER_REPORTER, TaskAttribute.TYPE_PERSON, true, false),

	/**
	 * Comment being added to pull request
	 */
	COMMENT_NEW(Messages.PullRequestAttribute_LabelComment,
			TaskAttribute.COMMENT_NEW, TaskAttribute.TYPE_LONG_RICH_TEXT,
			false, false),

	/**
	 * Pull request reporter gravatar
	 */
	REPORTER_GRAVATAR(Messages.PullRequestAttribute_LabelReporter,
			"github.pr.reporter.gravatar", TaskAttribute.TYPE_URL, null, true, //$NON-NLS-1$
			false),

	/**
	 * Full pull request model stored as JSON
	 */
	MODEL(Messages.PullRequestAttribute_LabelModel, "github.pr.model", //$NON-NLS-1$
			TaskAttribute.TYPE_LONG_TEXT, null, true, false);

	private final GitHubAttributeMetadata metadata;

	private PullRequestAttribute(String label, String id, String type,
			boolean readOnly, boolean initTask) {
		metadata = new GitHubAttributeMetadata(id, label, type, readOnly,
				initTask);
	}

	private PullRequestAttribute(String label, String id, String type,
			String kind, boolean readOnly, boolean initTask) {
		metadata = new GitHubAttributeMetadata(id, label, kind, type, readOnly,
				initTask);
	}

	/**
	 * Get attribute metadata
	 * 
	 * @return metadata
	 */
	public GitHubAttributeMetadata getMetadata() {
		return metadata;
	}
}
