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

package org.eclipse.mylar.internal.ide;

import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.internal.ide.ui.views.ActiveSearchView;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * TODO: support multiple workbench windows properly
 * 
 * @author Mik Kersten
 */
public class ActiveSearchViewTracker implements IPartListener2, IWindowListener, IPageListener {

	public void partClosed(IWorkbenchPartReference partRef) {
		if (partRef.getId().equals(ActiveSearchView.ID)) {
			MylarPlugin.getContextManager().setActiveSearchEnabled(false);
		}
	}

	public void partOpened(IWorkbenchPartReference partRef) {
		if (partRef.getId().equals(ActiveSearchView.ID)) {
			MylarPlugin.getContextManager().setActiveSearchEnabled(true);
		}
	}

	public void partDeactivated(IWorkbenchPartReference partRef) {

	}

	public void windowActivated(IWorkbenchWindow window) {
	}

	public void windowDeactivated(IWorkbenchWindow window) {
	}

	public void windowClosed(IWorkbenchWindow window) {
		window.removePageListener(this);
	}

	public void windowOpened(IWorkbenchWindow window) {
		window.addPageListener(this);
	}

	public void partActivated(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub

	}

	public void partBroughtToTop(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub

	}

	public void pageActivated(IWorkbenchPage page) {
	}

	public void pageClosed(IWorkbenchPage page) {
		page.removePartListener(this);
	}

	public void pageOpened(IWorkbenchPage page) {
		page.addPartListener(this);
	}

	public void partHidden(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub

	}

	public void partVisible(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub

	}

	public void partInputChanged(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub

	}
}