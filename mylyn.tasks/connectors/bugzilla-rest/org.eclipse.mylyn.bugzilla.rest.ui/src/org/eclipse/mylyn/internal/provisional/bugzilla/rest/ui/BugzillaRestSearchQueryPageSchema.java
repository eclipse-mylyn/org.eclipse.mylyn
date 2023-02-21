/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.bugzilla.rest.ui;

import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestTaskSchema;
import org.eclipse.mylyn.internal.provisional.tasks.ui.wizards.AbstractQueryPageSchema;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

public class BugzillaRestSearchQueryPageSchema extends AbstractQueryPageSchema {

	private final BugzillaRestTaskSchema parent = BugzillaRestTaskSchema.getDefault();

	private static final BugzillaRestSearchQueryPageSchema instance = new BugzillaRestSearchQueryPageSchema();

	public BugzillaRestSearchQueryPageSchema() {
	}

	public static BugzillaRestSearchQueryPageSchema getInstance() {
		return instance;
	}

	public final Field summary = copyFrom(parent.SUMMARY).create();

	public final Field product = copyFrom(parent.PRODUCT).type(TaskAttribute.TYPE_MULTI_SELECT)
			.layoutPriority(11)
			.create();

	public final Field component = copyFrom(parent.COMPONENT).type(TaskAttribute.TYPE_MULTI_SELECT)
			.layoutPriority(11)
			.create();

	public final Field version = copyFrom(parent.VERSION).type(TaskAttribute.TYPE_MULTI_SELECT)
			.layoutPriority(11)
			.create();

	public final Field target_milestone = copyFrom(parent.TARGET_MILESTONE).type(TaskAttribute.TYPE_MULTI_SELECT)
			.layoutPriority(11)
			.create();
}
