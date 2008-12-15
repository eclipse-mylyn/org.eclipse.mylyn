/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Rob Elves
 * @since 3.0
 */
public enum BugzillaOperation {

	none(Messages.BugzillaOperation_Leave_as_X_X),

	accept(Messages.BugzillaOperation_Accept_to_ASSIGNED),

	resolve(Messages.BugzillaOperation_Resolve_as, "resolutionInput", TaskAttribute.TYPE_SINGLE_SELECT), //$NON-NLS-1$

	duplicate(Messages.BugzillaOperation_Duplicate_of, "dup_id", TaskAttribute.TYPE_TASK_DEPENDENCY), //$NON-NLS-1$

	reassign(Messages.BugzillaOperation_Reassign_to, "reassignInput", TaskAttribute.TYPE_PERSON), //$NON-NLS-1$

	reassignbycomponent(Messages.BugzillaOperation_Reassign_to_default_assignee),

	reopen(Messages.BugzillaOperation_Reopen_bug),

	verify(Messages.BugzillaOperation_Mark_as_VERIFIED),

	close(Messages.BugzillaOperation_Mark_as_CLOSED);

	private final String label;

	private final String inputId;

	private final String inputType;

	BugzillaOperation(String label) {
		this(label, null, TaskAttribute.TYPE_SHORT_TEXT);
	}

	BugzillaOperation(String label, String inputId, String type) {
		this.label = label;
		this.inputId = inputId;
		this.inputType = type;
	}

	public String getLabel() {
		return label;
	}

	public String getInputId() {
		return inputId;
	}

	public String getInputType() {
		return inputType;
	}
}
