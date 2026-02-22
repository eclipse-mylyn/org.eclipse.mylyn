/*******************************************************************************
 * Copyright (c) 2012, 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Francois Chouinard - Moved/added fields for Gerrit Dashboard
 *     Marc-Andre Laperle (Ericsson) - Add topic
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core;

import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema;
import org.eclipse.mylyn.tasks.core.data.DefaultTaskSchema;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

import com.google.gerrit.common.data.ChangeInfo;

/**
 * The schema for tasks populated from {@link ChangeInfo} objects.
 *
 * @author Steffen Pingel
 */
public class GerritQueryResultSchema extends AbstractTaskSchema {

	private static final GerritQueryResultSchema instance = new GerritQueryResultSchema();

	public static GerritQueryResultSchema getDefault() {
		return instance;
	}

	private final DefaultTaskSchema parent = DefaultTaskSchema.getInstance();

	public final Field SUMMARY = inheritFrom(parent.SUMMARY).addFlags(Flag.READ_ONLY).create();

	public final Field STATUS = inheritFrom(parent.STATUS).create();

	public final Field COMPLETED = inheritFrom(parent.DATE_COMPLETION).create();

	public final Field UPDATED = inheritFrom(parent.DATE_MODIFICATION).create();

	public final Field PROJECT = createField(TaskAttribute.PRODUCT, Messages.GerritQueryResultSchema_Project,
			TaskAttribute.TYPE_SHORT_TEXT, Flag.READ_ONLY, Flag.ATTRIBUTE);

	public final Field CHANGE_ID = createField("org.eclipse.gerrit.Key", Messages.GerritQueryResultSchema_ChangeId, //$NON-NLS-1$
			TaskAttribute.TYPE_LONG_TEXT, Flag.READ_ONLY, Flag.ATTRIBUTE);

	public final Field KEY = inheritFrom(parent.TASK_KEY).create();

	public final Field URL = inheritFrom(parent.TASK_URL).create();

	// Moved from GerritTaskSchema
	public final Field OWNER = inheritFrom(parent.USER_ASSIGNED).flags(Flag.READ_ONLY, Flag.ATTRIBUTE).create();

	// Moved from GerritTaskSchema
	public final Field BRANCH = createField("org.eclipse.gerrit.Branch", Messages.GerritQueryResultSchema_Branch, //$NON-NLS-1$
			TaskAttribute.TYPE_SHORT_TEXT, Flag.READ_ONLY, Flag.ATTRIBUTE);

	// Indicates that the review is 'starred'
	public final Field IS_STARRED = createField("org.eclipse.gerrit.StarredReview", //$NON-NLS-1$
			Messages.GerritQueryResultSchema_Starred, TaskAttribute.TYPE_BOOLEAN);

	// The review state (typically -2 .. +2)
	public final Field REVIEW_STATE = createField("org.eclipse.gerrit.ReviewState", //$NON-NLS-1$
			Messages.GerritQueryResultSchema_ReviewState, TaskAttribute.TYPE_INTEGER, Flag.READ_ONLY);

	// The verification state (typically -1 .. +1)
	public final Field VERIFY_STATE = createField("org.eclipse.gerrit.VerifyState", //$NON-NLS-1$
			Messages.GerritQueryResultSchema_VerifyState, TaskAttribute.TYPE_INTEGER, Flag.READ_ONLY);

	// The topic string, optionally set by the user. Read only for now.
	public final Field TOPIC = createField("org.eclipse.gerrit.Topic", Messages.GerritQueryResultSchema_Topic, //$NON-NLS-1$
			TaskAttribute.TYPE_SHORT_TEXT, Flag.READ_ONLY, Flag.ATTRIBUTE);
}
