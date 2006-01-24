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

package org.eclipse.mylar.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.mylar.ui.preferences.MylarPreferencePage;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class EditHighlightersAction extends Action implements IViewActionDelegate {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.context.highlighters.edit";

	public EditHighlightersAction() {
		setText("Edit Highlighters...");
		setToolTipText("Edit Highlighters...");
		setId(ID);
		setImageDescriptor(MylarImages.COLOR_PALETTE);
	}

	@Override
	public void run() {
		IPreferencePage page = new MylarPreferencePage();
		showPreferencePage("org.eclipse.mylar.ui.preferences", page);
	}

	protected void showPreferencePage(String id, IPreferencePage page) {
		final IPreferenceNode targetNode = new PreferenceNode(id, page);

		PreferenceManager manager = new PreferenceManager();
		manager.addToRoot(targetNode);
		final PreferenceDialog dialog = new PreferenceDialog(Workbench.getInstance().getActiveWorkbenchWindow()
				.getShell(), manager);
		final boolean[] result = new boolean[] { false };
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				dialog.create();
				dialog.setMessage(targetNode.getLabelText());
				result[0] = (dialog.open() == Window.OK);
			}
		});
	}

	public void init(IViewPart view) {

	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}
}