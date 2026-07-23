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
package org.eclipse.mylyn.internal.github.core.gist;

import org.eclipse.mylyn.internal.github.core.GitHubAttributeMetadata;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * Gist task attribute enumeration.
 */
public enum GistAttribute {

	/**
	 * Gist key
	 */
	KEY(TaskAttribute.TASK_KEY, Messages.GistAttribute_LabelKey,
			TaskAttribute.TYPE_SHORT_TEXT, true),

	/**
	 * Gist author
	 */
	AUTHOR(TaskAttribute.USER_REPORTER, Messages.GistAttribute_LabelAuthor,
			TaskAttribute.TYPE_PERSON, true),

	/**
	 * Gist author gravatar url
	 */
	AUTHOR_GRAVATAR(
			"github.gist.reporter.avatar", //$NON-NLS-1$
			Messages.GistAttribute_LabelAuthorGravatar, TaskAttribute.TYPE_URL,
			true),

	/**
	 * Gist created date
	 */
	CREATED(TaskAttribute.DATE_CREATION, Messages.GistAttribute_LabelCreated,
			TaskAttribute.TYPE_DATETIME, true),

	/**
	 * Gist updated date
	 */
	UPDATED(TaskAttribute.DATE_MODIFICATION,
			Messages.GistAttribute_LabelModified, TaskAttribute.TYPE_DATETIME,
			true),

	/**
	 * Comment being added to gist
	 */
	COMMENT_NEW(TaskAttribute.COMMENT_NEW,
			Messages.GistAttribute_LabelNewComment,
			TaskAttribute.TYPE_LONG_RICH_TEXT, false),

	/**
	 * URL
	 */
	URL(TaskAttribute.TASK_URL, Messages.GistAttribute_LabelUrl,
			TaskAttribute.TYPE_URL, true),

	/**
	 * RAW_FILE_URL
	 */
	RAW_FILE_URL(
			"github.gist.file.url", Messages.GistAttribute_LabelFileUrl, TaskAttribute.TYPE_URL, //$NON-NLS-1$
			true),

	/**
	 * CLONE_URL
	 */
	CLONE_URL(
			"github.gist.cloneUrl", Messages.GistAttribute_LabelCloneUrl, TaskAttribute.TYPE_URL, //$NON-NLS-1$
			true),

	/**
	 * SUMMARY
	 */
	SUMMARY(TaskAttribute.SUMMARY, Messages.GistAttribute_LabelSummary,
			TaskAttribute.TYPE_SHORT_RICH_TEXT, true),

	/**
	 * Gist description
	 */
	DESCRIPTION(TaskAttribute.DESCRIPTION,
			Messages.GistAttribute_LabelDescription,
			TaskAttribute.TYPE_LONG_RICH_TEXT, false);

	private final GitHubAttributeMetadata attribute;

	private GistAttribute(String id, String label, String type, boolean readOnly) {
		attribute = new GitHubAttributeMetadata(id, label, type, readOnly);
	}

	private GistAttribute(String id, String label, String kind, String type,
			boolean readOnly) {
		attribute = new GitHubAttributeMetadata(id, label, kind, type,
				readOnly, false);
	}

	/**
	 * Get task attribute metadata
	 * 
	 * @return metadata
	 */
	public GitHubAttributeMetadata getMetadata() {
		return attribute;
	}
}
