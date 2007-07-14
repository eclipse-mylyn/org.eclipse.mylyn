/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.help.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.help.ui.dialogs.UiLegendDialog;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;

/**
 * @author Mik Kersten
 * @author Leo Dos Santos
 */
public class ShowMylynLegendAction implements IWorkbenchWindowActionDelegate, IViewActionDelegate {
	
	private IWorkbenchWindow wbWindow;

	public void dispose() {
		// ignore
	}

	public void init(IWorkbenchWindow window) {
		wbWindow = window;
	}

	public void run(IAction action) {
		IIntroManager introMgr = wbWindow.getWorkbench().getIntroManager();
		IIntroPart intro = introMgr.getIntro();
		if (intro != null) {
			introMgr.setIntroStandby(intro, true);
		}
		
		TaskListView.openInActivePerspective();
		Shell parentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		UiLegendDialog uiLegendDialog = new UiLegendDialog(parentShell);
		uiLegendDialog.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

	public void init(IViewPart view) {
		wbWindow = view.getViewSite().getWorkbenchWindow();
	}
}
