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
package org.eclipse.mylyn.internal.github.core.gist;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

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
	RAW_FILE_URL("github.gist.file.url", Messages.GistAttribute_LabelFileUrl, TaskAttribute.TYPE_URL, //$NON-NLS-1$
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

	private String id;
	private String label;
	private String type;
	private String kind;
	private boolean readOnly;

	private GistAttribute(String id, String label, String type, boolean readOnly) {
		this(id, label, TaskAttribute.KIND_DEFAULT, type, readOnly);
	}

	private GistAttribute(String id, String label, String kind, String type,
			boolean readOnly) {
		this.id = id;
		this.label = label;
		this.kind = kind;
		this.type = type;
		this.readOnly = readOnly;
	}

	/**
	 * @return id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return label
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * @return type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @return kind
	 */
	public String getKind() {
		return this.kind;
	}

	/**
	 * @return readOnly
	 */
	public boolean isReadOnly() {
		return this.readOnly;
	}

	/**
	 * Create task attribute under root of task data
	 * 
	 * @param data
	 * @return created attribute
	 */
	public TaskAttribute create(TaskData data) {
		return create(data.getRoot());
	}

	/**
	 * Create task attribute under parent
	 * 
	 * @param parent
	 * @return created attribute
	 */
	public TaskAttribute create(TaskAttribute parent) {
		TaskAttribute attribute = new TaskAttribute(parent, this.id);
		attribute.getMetaData().defaults().setLabel(this.label)
				.setType(this.type).setKind(this.kind)
				.setReadOnly(this.readOnly);
		return attribute;
	}
}
