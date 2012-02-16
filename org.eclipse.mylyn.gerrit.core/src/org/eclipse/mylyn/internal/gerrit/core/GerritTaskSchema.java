/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     GitHub Inc. - fix for bug 355557
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core;

import org.eclipse.mylyn.tasks.core.data.DefaultTaskSchema;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Steffen Pingel
 */
public class GerritTaskSchema extends GerritQueryResultSchema {

	private static final GerritTaskSchema instance = new GerritTaskSchema();

	public static GerritTaskSchema getDefault() {
		return instance;
	}

	private final DefaultTaskSchema parent = DefaultTaskSchema.getInstance();

	public final Field UPLOADED = inheritFrom(parent.DATE_CREATION).create();

	public final Field OWNER = inheritFrom(parent.USER_ASSIGNED).flags(Flag.READ_ONLY, Flag.ATTRIBUTE).create();

	public final Field BRANCH = createField("org.eclipse.gerrit.Branch", "Branch", TaskAttribute.TYPE_SHORT_TEXT,
			Flag.READ_ONLY, Flag.ATTRIBUTE);

	public final Field DESCRIPTION = inheritFrom(parent.DESCRIPTION).addFlags(Flag.READ_ONLY).create();

	public final Field OBJ_REVIEW = createField("org.eclipse.gerrit.Review", "Review", TaskAttribute.TYPE_LONG_TEXT);

	public final Field CAN_PUBLISH = createField("org.eclipse.gerrit.CanPublish", "Publish", TaskAttribute.TYPE_BOOLEAN);

}
