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

package org.eclipse.mylar.internal.ui;

import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Mik Kersten
 */
public abstract class AbstractPartTracker implements IPartListener {

	public void install(IWorkbench workbench) {
		MylarPlugin.getDefault().addWindowPartListener(this);
//		if (workbench != null) {
//			workbench.addWindowListener(this);
//			IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
//			for (int i = 0; i < windows.length; i++) {
//				windows[i].addPageListener(this);
//				IWorkbenchPage[] pages = windows[i].getPages();
//				for (int j = 0; j < pages.length; j++) {
//					pages[j].addPartListener(this);
//				}
//			}
//		}
	}

	public void dispose(IWorkbench workbench) {
		MylarPlugin.getDefault().removeWindowPartListener(this);
//		if (workbench != null) {
//			workbench.removeWindowListener(this);
//			IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
//			for (int i = 0; i < windows.length; i++) {
//				windows[i].removePageListener(this);
//				IWorkbenchPage[] pages = windows[i].getPages();
//				for (int j = 0; j < pages.length; j++) {
//					pages[j].removePartListener(this);
//				}
//			}
//		}
	}

//	public void windowActivated(IWorkbenchWindow window) {
//	}
//
//	public void windowDeactivated(IWorkbenchWindow window) {
//	}

//	public void windowClosed(IWorkbenchWindow window) {
//		window.removePageListener(this);
//	}
//
//	public void windowOpened(IWorkbenchWindow window) {
//		window.addPageListener(this);
//	}

//	public void pageActivated(IWorkbenchPage page) {
//	}
//
//	public void pageClosed(IWorkbenchPage page) {
//		page.removePartListener(this);
//	}
//
//	public void pageOpened(IWorkbenchPage page) {
//		page.addPartListener(this);
//	}

	public abstract void partActivated(IWorkbenchPart part);

	public abstract void partBroughtToTop(IWorkbenchPart part);

	public abstract void partClosed(IWorkbenchPart part);

	public abstract void partDeactivated(IWorkbenchPart part);

	public abstract void partOpened(IWorkbenchPart part);

}