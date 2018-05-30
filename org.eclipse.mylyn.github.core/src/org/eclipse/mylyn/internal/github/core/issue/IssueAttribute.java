/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green <david.green@tasktop.com> - initial contribution
 *     Christian Trutz <christian.trutz@gmail.com> - initial contribution
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.core.issue;

import org.eclipse.mylyn.internal.github.core.GitHubAttributeMetadata;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * GitHub issue task attributes
 */
public enum IssueAttribute {

	/**
	 * Issue key
	 */
	KEY(Messages.IssueAttribute_LabelKey, TaskAttribute.TASK_KEY,
			TaskAttribute.TYPE_SHORT_TEXT, true, true),

	/**
	 * Issue title
	 */
	TITLE(Messages.IssueAttribute_LabekSummary, TaskAttribute.SUMMARY,
			TaskAttribute.TYPE_SHORT_RICH_TEXT, false, true),

	/**
	 * Issue description
	 */
	BODY(Messages.IssueAttribute_LabelDescription, TaskAttribute.DESCRIPTION,
			TaskAttribute.TYPE_LONG_RICH_TEXT, false, true),

	/**
	 * Issue creation date
	 */
	CREATION_DATE(Messages.IssueAttribute_LabelCreated,
			TaskAttribute.DATE_CREATION, TaskAttribute.TYPE_DATETIME, true,
			false),

	/**
	 * Issue modification date
	 */
	MODIFICATION_DATE(Messages.IssueAttribute_LabelModified,
			TaskAttribute.DATE_MODIFICATION, TaskAttribute.TYPE_DATETIME, true,
			false),

	/**
	 * Issue closed date
	 */
	CLOSED_DATE(Messages.IssueAttribute_LabelClosed,
			TaskAttribute.DATE_COMPLETION, TaskAttribute.TYPE_DATETIME, true,
			false),

	/**
	 * Issue status
	 */
	STATUS(Messages.IssueAttribute_LabelStatus, TaskAttribute.STATUS,
			TaskAttribute.TYPE_SHORT_TEXT, false, true),

	/**
	 * Issue reporter
	 */
	REPORTER(Messages.IssueAttribute_LabelReporter,
			TaskAttribute.USER_REPORTER, TaskAttribute.TYPE_PERSON, true, false),

	/**
	 * Comment being added to issue
	 */
	COMMENT_NEW(Messages.IssueAttribute_LabelComment,
			TaskAttribute.COMMENT_NEW, TaskAttribute.TYPE_LONG_RICH_TEXT,
			false, false),

	/**
	 * Labels applied to issue
	 */
	LABELS(Messages.IssueAttribute_LabelLabels, "github.issue.labels", //$NON-NLS-1$
			TaskAttribute.TYPE_MULTI_SELECT, false, true),

	/**
	 * Issue assignee
	 */
	ASSIGNEE(Messages.IssueAttribute_LabelAssignee,
			TaskAttribute.USER_ASSIGNED, TaskAttribute.TYPE_PERSON, false, true),

	/**
	 * Issue milestone
	 */
	MILESTONE(Messages.IssueAttribute_LabelMilestone,
			"github.issue.milestone", TaskAttribute.TYPE_SINGLE_SELECT, //$NON-NLS-1$
			false, true),

	/**
	 * Issue assignee gravatar
	 */
	ASSIGNEE_GRAVATAR(Messages.IssueAttribute_LabelAssigneeGravatar,
			"github.issue.assignee.gravatar", TaskAttribute.TYPE_URL, null, //$NON-NLS-1$
			false, true),

	/**
	 * Issue reporter gravatar
	 */
	REPORTER_GRAVATAR(Messages.IssueAttribute_LabelReporterGravatar,
			"github.issue.reporter.gravatar", TaskAttribute.TYPE_URL, null, //$NON-NLS-1$
			true, false);

	private final GitHubAttributeMetadata metadata;

	private IssueAttribute(String label, String id, String type,
			boolean readOnly, boolean initTask) {
		metadata = new GitHubAttributeMetadata(id, label, type, readOnly,
				initTask);
	}

	private IssueAttribute(String label, String id, String type, String kind,
			boolean readOnly, boolean initTask) {
		metadata = new GitHubAttributeMetadata(id, label, kind, type, readOnly,
				initTask);
	}

	/**
	 * Get task attribute metadata
	 * 
	 * @return metadata
	 */
	public GitHubAttributeMetadata getMetadata() {
		return metadata;
	}
}
