/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     John Anvik - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core.history;

/**
 * @author John Anvik
 */
public class AssignmentEvent extends TaskRevision {

	private static final long serialVersionUID = 3258693199936631348L;

	private final String assigned;

	public AssignmentEvent(String change) {
		this.what = TaskRevision.ASSIGNMENT;
		this.assigned = change;
		this.added = change;
	}

	public String getAssigned() {
		return this.assigned;
	}

	@Override
	public String toString() {
		return this.getName() + " | " + this.getDate() + " | " + this.getWhat() + " | " + this.getRemoved() + " | " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ this.getAssigned();
	}
}
