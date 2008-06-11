/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Rob Elves
 * @since 3.0
 */
public enum BugzillaOperation {

	none("Leave as %s %s"),

	accept("Accept (change status to ASSIGNED)"),

	resolve("Resolve as", "resolutionInput", TaskAttribute.TYPE_SINGLE_SELECT),

	duplicate("Duplicate of", "dup_id", TaskAttribute.TYPE_TASK_DEPENDENCY),

	reassign("Reassign to", "reassignInput", TaskAttribute.TYPE_PERSON),

	reassignbycomponent("Reassign to default assignee"),

	reopen("Reopen bug"),

	verify("Mark as VERIFIED"),

	close("Mark as CLOSED");

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