/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.context.ui.ContextUiImages;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * @author Mik Kersten
 */
public class EditHighlightersAction extends Action implements IViewActionDelegate {

	private static final String ID_PREFS_HIGHLIGHTERS = "org.eclipse.mylyn.sandbox.ui.preferences";

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.context.highlighters.edit";

	public EditHighlightersAction() {
		setText("Edit Highlighters...");
		setToolTipText("Edit Highlighters...");
		setId(ID);
		setImageDescriptor(ContextUiImages.COLOR_PALETTE);
	}

	@Override
	public void run() {
		PreferenceDialog dlg = PreferencesUtil.createPreferenceDialogOn(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getShell(), ID_PREFS_HIGHLIGHTERS, new String[] { ID_PREFS_HIGHLIGHTERS }, null);
		dlg.open();
	}

	public void init(IViewPart view) {

	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}
}
