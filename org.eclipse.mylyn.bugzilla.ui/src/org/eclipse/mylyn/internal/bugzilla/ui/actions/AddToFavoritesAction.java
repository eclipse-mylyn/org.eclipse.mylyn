/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.bugzilla.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.internal.Favorite;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.internal.bugzilla.ui.FavoritesView;
import org.eclipse.mylar.internal.bugzilla.ui.editor.ExistingBugEditorInput;
import org.eclipse.ui.part.EditorPart;

/**
 * Action used to add the supplied editor's bug to the favorites list.
 */
public class AddToFavoritesAction extends Action {
	private EditorPart editorPart;

	/**
	 * Creates a new <code>AddToFavoritesAction</code>.
	 * 
	 * @param editor
	 *            The editor for the bug that is being added to the favorites
	 *            list.
	 */
	public AddToFavoritesAction(EditorPart editor) {
		editorPart = editor;
		setText("&Add to favorites");
		setImageDescriptor(BugzillaImages.IMG_TOOL_ADD_TO_FAVORITES);
	}

	@Override
	public void run() {
		ExistingBugEditorInput input = (ExistingBugEditorInput) editorPart.getEditorInput();
		Favorite entry = new Favorite(input.getBug());
		FavoritesView.checkWindow();
		BugzillaPlugin.getDefault().getFavorites().add(entry);
		FavoritesView.add();
	}
}
