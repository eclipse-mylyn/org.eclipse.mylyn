/*******************************************************************************
 * Copyright (c) 2009, 2013 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.context.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.wikitext.context.ui.WikiTextContextUiPlugin;
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
		return WikiTextContextUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(WikiTextContextUiPlugin.PREF_ACTIVE_FOLDING_ENABLED);
	}

	public void run(IAction action) {
		WikiTextContextUiPlugin.getDefault()
				.getPreferenceStore()
				.setValue(WikiTextContextUiPlugin.PREF_ACTIVE_FOLDING_ENABLED, action.isChecked());
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore

	}

}
