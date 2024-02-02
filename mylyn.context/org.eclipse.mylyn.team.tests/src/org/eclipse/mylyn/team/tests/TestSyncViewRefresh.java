/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.team.tests;

import org.eclipse.team.internal.ui.synchronize.SynchronizeView;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;

import junit.framework.TestCase;

/**
 * @author Mik Kersten
 */
public class TestSyncViewRefresh extends TestCase {

	public void testInitialPage() throws PartInitException {
		String ID = "org.eclipse.team.sync.views.SynchronizeView";
		IViewPart view = openInActivePerspective(ID);
		assertTrue(view instanceof SynchronizeView);
		SynchronizeView syncView = (SynchronizeView) view;
		IPage page = syncView.getCurrentPage();
		assertTrue(page instanceof MessagePage);

		// TODO: get the AbstractSynchronizePage and call getViewer() for contents
	}

	private static IViewPart openInActivePerspective(String viewId) throws PartInitException {
		if (PlatformUI.isWorkbenchRunning() && PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (activePage != null) {
				return activePage.showView(viewId);
			}
		}
		return null;
	}

}
