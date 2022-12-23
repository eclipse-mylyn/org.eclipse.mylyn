/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;

/**
 * @author Mik Kersten
 * @author Leo Dos Santos
 */
public class WelcomeIntroAction implements IWorkbenchWindowActionDelegate, IViewActionDelegate {

	private IWorkbenchWindow wbWindow;

	public void dispose() {
		// ignore
	}

	public void init(IViewPart view) {
		wbWindow = view.getViewSite().getWorkbenchWindow();
	}

	public void init(IWorkbenchWindow window) {
		wbWindow = window;
	}

	public void run(IAction action) {
		IIntroManager introMgr = wbWindow.getWorkbench().getIntroManager();
		IIntroPart intro = introMgr.getIntro();
		if (intro != null) {
			try {
				introMgr.setIntroStandby(intro, true);
			} catch (NullPointerException e) {
				// bug 270351: ignore exception
			}
		}

		TasksUiUtil.openTasksViewInActivePerspective();

		new AddRepositoryAction().showWizard(getShell(), null);
	}

	private Shell getShell() {
		return (wbWindow != null) ? wbWindow.getShell() : WorkbenchUtil.getShell();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

}
