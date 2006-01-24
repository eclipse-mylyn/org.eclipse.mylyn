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

import org.eclipse.jface.action.Action;

/**
 * Action used to copy selected text from a bug editor to the clipboard.
 */
public class BugzillaEditorCopyAction extends Action {
	/** The editor to copy text selections from. */
	private AbstractBugEditor bugEditor;

	/**
	 * Creates a new <code>BugzillaEditorCopyAction</code>.
	 * 
	 * @param editor
	 *            The editor that this action is copying text selections from.
	 */
	public BugzillaEditorCopyAction(AbstractBugEditor editor) {
		bugEditor = editor;
		setText("AbstractBugEditor.copy.text");
	}

	@Override
	public void run() {
		bugEditor.getCurrentText().copy();
	}

}
