/*******************************************************************************
 * Copyright (c) 2023 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.gitlab.ui;

import org.eclipse.mylyn.internal.gitlab.core.GitlabNewTaskSchema;
import org.eclipse.mylyn.internal.provisional.tasks.ui.wizards.AbstractQueryPageSchema;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

public class GitlabSearchQueryPageSchema extends AbstractQueryPageSchema {

	private static final GitlabSearchQueryPageSchema instance = new GitlabSearchQueryPageSchema();

	private final GitlabNewTaskSchema parent = GitlabNewTaskSchema.getDefault();

	public static GitlabSearchQueryPageSchema getInstance() {
		return instance;
	}

	public GitlabSearchQueryPageSchema() {
	}

	public final Field product = copyFrom(parent.PRODUCT).type(TaskAttribute.TYPE_MULTI_SELECT)
			.layoutPriority(1)
			.create();

	public final Field group = createField("GROUP", "Group", TaskAttribute.TYPE_MULTI_SELECT, null, 1);

	public final Field search = createField("SEARCH", "search", TaskAttribute.TYPE_SHORT_TEXT, null, 2);

	public final Field search_in = createField("SEARCH_IN", "in", TaskAttribute.TYPE_SINGLE_SELECT, null, 2);

	public final Field state = createField("STATE", "State", TaskAttribute.TYPE_SINGLE_SELECT, null, 3);

	public final Field confidential = createField("CONFIDENTIAL", "confidential", TaskAttribute.TYPE_BOOLEAN, null, 4);

	public final Field assigned_to_me = createField("ASSIGNED_TO_ME", "assigned to me", TaskAttribute.TYPE_BOOLEAN,
			null, 4);
}
