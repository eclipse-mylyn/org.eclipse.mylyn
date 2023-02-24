/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Red Hat Inc. - fixes for bug 259291
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Rob Elves
 * @author Charley Wang
 * @since 3.0
 */
public class BugzillaOperation extends AbstractBugzillaOperation {

//	old: private static final long serialVersionUID = 1920016855179988829L;

	private static final long serialVersionUID = 5940437048263423786L;

	public static final BugzillaOperation none = new BugzillaOperation(Messages.BugzillaOperation_Leave_as_X_X, "none"); //$NON-NLS-1$

	public static final BugzillaOperation reassign = new BugzillaOperation(Messages.BugzillaOperation_Reassign_to,
			"reassignInput", TaskAttribute.TYPE_PERSON, "reassign"); //$NON-NLS-1$ //$NON-NLS-2$

	public static final BugzillaOperation accept = new BugzillaOperation(Messages.BugzillaOperation_Accept_to_ASSIGNED,
			"accept"); //$NON-NLS-1$

	public static final BugzillaOperation resolve = new BugzillaOperation(Messages.BugzillaOperation_Resolve_as,
			"resolutionInput", TaskAttribute.TYPE_SINGLE_SELECT, "resolve"); //$NON-NLS-1$ //$NON-NLS-2$

	public static final BugzillaOperation duplicate = new BugzillaOperation(Messages.BugzillaOperation_Duplicate_of,
			"dup_id", TaskAttribute.TYPE_TASK_DEPENDENCY, "duplicate"); //$NON-NLS-1$ //$NON-NLS-2$

	public static final BugzillaOperation reopen = new BugzillaOperation(Messages.BugzillaOperation_Reopen_bug,
			"reopen"); //$NON-NLS-1$

	public static final BugzillaOperation verify = new BugzillaOperation(Messages.BugzillaOperation_Mark_as_VERIFIED,
			"verify"); //$NON-NLS-1$

	public static final BugzillaOperation close = new BugzillaOperation(Messages.BugzillaOperation_Mark_as_CLOSED,
			"close"); //$NON-NLS-1$

	public static final BugzillaOperation close_with_resolution = new BugzillaOperation(
			Messages.BugzillaOperation_Mark_as_CLOSED, "resolutionInput", TaskAttribute.TYPE_SINGLE_SELECT, "close"); //$NON-NLS-1$ //$NON-NLS-2$

	public static final BugzillaOperation markNew = new BugzillaOperation(Messages.BugzillaOperation_Mark_as_NEW,
			"markNew"); //$NON-NLS-1$

	public static final BugzillaOperation reassignbycomponent = new BugzillaOperation(
			Messages.BugzillaOperation_Reassign_to_default_assignee, "reassignbycomponent"); //$NON-NLS-1$

	// Bugzilla 4.0
	public static final BugzillaOperation unconfirmed = new BugzillaOperation(Messages.BugzillaOperation_unconfirmed,
			"unconfirmed"); //$NON-NLS-1$

	public static final BugzillaOperation confirmed = new BugzillaOperation(Messages.BugzillaOperation_confirmed,
			"confirmed"); //$NON-NLS-1$

	public static final BugzillaOperation in_progress = new BugzillaOperation(Messages.BugzillaOperation_in_progress,
			"in_progress"); //$NON-NLS-1$

	public static final BugzillaOperation verify_with_resolution = new BugzillaOperation(
			Messages.BugzillaOperation_verified, "resolutionInput", TaskAttribute.TYPE_SINGLE_SELECT, "verify"); //$NON-NLS-1$//$NON-NLS-2$ 

	public static final BugzillaOperation new_default = new BugzillaOperation(Messages.BugzillaOperation_default,
			"{Start}"); //$NON-NLS-1$

	private final String value;

	BugzillaOperation(String label) {
		super(label);
		this.value = label.replaceAll(DEFAULT_LABEL_PREFIX, ""); //$NON-NLS-1$
	}

	BugzillaOperation(String label, String inputId, String type, String value) {
		super(label, inputId, type);
		this.value = value;
	}

	public BugzillaOperation(String label, String value) {
		super(label);
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}
}
