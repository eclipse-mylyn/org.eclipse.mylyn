/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema;
import org.eclipse.mylyn.tasks.core.data.DefaultTaskSchema;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

public class BugzillaRestTaskSchema extends AbstractTaskSchema {

	private static final BugzillaRestTaskSchema instance = new BugzillaRestTaskSchema();

	public static BugzillaRestTaskSchema getDefault() {
		return instance;
	}

	private final DefaultTaskSchema parent = DefaultTaskSchema.getInstance();

	public final Field DESCRIPTION = inheritFrom(parent.DESCRIPTION).create();

	public final Field SUMMARY = inheritFrom(parent.SUMMARY).create();

	public final Field NEW_COMMENT = inheritFrom(parent.NEW_COMMENT).create();

	public final Field COMPONENT = inheritFrom(parent.COMPONENT).create();

	public final Field DUMMYATTRIBUTE = createField("task.bugzilla.dummy.attribute", "Dummy Attribute", //$NON-NLS-1$ //$NON-NLS-2$
			TaskAttribute.TYPE_SHORT_TEXT, Flag.ATTRIBUTE);

}
