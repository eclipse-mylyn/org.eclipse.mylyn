/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui.actions;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.java.ui.JavaUiBridgePlugin;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Mik Kersten
 */
public class ToggleActiveFoldingAction extends Action implements IWorkbenchWindowActionDelegate, IActionDelegate2 {

	private static ToggleActiveFoldingAction INSTANCE;

	private IAction parentAction = null;

	public ToggleActiveFoldingAction() {
		INSTANCE = this;
		setText(Messages.ToggleActiveFoldingAction_Active_folding);
	}

	public static void toggleFolding(boolean on) {
		if (INSTANCE.parentAction != null) {
			INSTANCE.valueChanged(INSTANCE.parentAction, on);
		}
	}

	@Override
	public void run(IAction action) {
		valueChanged(action, action.isChecked());
	}

	private void valueChanged(IAction action, final boolean on) {
		if (on) {
			JavaPlugin.getDefault().getPreferenceStore().setValue(PreferenceConstants.EDITOR_FOLDING_ENABLED, true);
		}
		action.setChecked(on);
		JavaUiBridgePlugin.getDefault().getPreferenceStore().setValue(JavaUiBridgePlugin.AUTO_FOLDING_ENABLED, on);
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		// don't care when the active editor changes
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// don't care when the selection changes
	}

	@Override
	public void init(IAction action) {
		parentAction = action;
		valueChanged(action,
				JavaUiBridgePlugin.getDefault()
						.getPreferenceStore()
						.getBoolean(JavaUiBridgePlugin.AUTO_FOLDING_ENABLED));
	}

	@Override
	public void dispose() {
		// don't need to do anything

	}

	@Override
	public void runWithEvent(IAction action, Event event) {
		run(action);
	}

	@Override
	public void init(IWorkbenchWindow window) {
	}
}
