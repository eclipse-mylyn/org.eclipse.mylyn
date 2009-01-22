/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.tasks.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.wikitext.tasks.ui.WikiTextTasksUiPlugin;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

/**
 * @author David Green
 */
public class ToggleActiveFoldingEditorActionDelegate implements IEditorActionDelegate {

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		action.setChecked(isActiveFoldingEnabled());
	}

	private boolean isActiveFoldingEnabled() {
		return WikiTextTasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
				WikiTextTasksUiPlugin.PREF_ACTIVE_FOLDING_ENABLED);
	}

	public void run(IAction action) {
		WikiTextTasksUiPlugin.getDefault().getPreferenceStore().setValue(
				WikiTextTasksUiPlugin.PREF_ACTIVE_FOLDING_ENABLED, action.isChecked());
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore

	}

}
