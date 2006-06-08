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

package org.eclipse.mylar.internal.tasklist.ui.editors;

import org.eclipse.jface.action.Action;

/**
 * Action used to copy selected text from a bug editor to the clipboard.
 */
public class RepositoryTaskEditorCopyAction extends Action {
	/** The editor to copy text selections from. */
	private AbstractRepositoryTaskEditor bugEditor;

	/**
	 * Creates a new <code>RepositoryTaskEditorCopyAction</code>.
	 * 
	 * @param editor
	 *            The editor that this action is copying text selections from.
	 */
	public RepositoryTaskEditorCopyAction(AbstractRepositoryTaskEditor editor) {
		bugEditor = editor;
		setText("AbstractRepositoryTaskEditor.copy.text");
	}

	@Override
	public void run() {
		bugEditor.getCurrentText().copy();
	}

}
