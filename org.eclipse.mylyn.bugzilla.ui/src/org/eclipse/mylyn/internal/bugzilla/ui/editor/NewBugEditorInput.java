/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.bugzilla.ui.editor;

import org.eclipse.mylar.internal.bugzilla.core.NewBugzillaReport;

/**
 * The <code>IEditorInput</code> implementation for <code>NewBugEditor</code>.
 */
public class NewBugEditorInput extends AbstractBugEditorInput {

	protected NewBugzillaReport bug;

	/**
	 * Creates a new <code>NewBugEditorInput</code>.
	 * 
	 * @param bug
	 *            The bug for this editor input.
	 */
	public NewBugEditorInput(NewBugzillaReport bug) {
		this.bug = bug;
	}

	public String getName() {
		return bug.getLabel();
	}

	@Override
	public NewBugzillaReport getBug() {
		return bug;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof NewBugEditorInput) {
			NewBugEditorInput input = (NewBugEditorInput) o;
			return input.getBug().equals(bug);
		}
		return false;
	}

}
