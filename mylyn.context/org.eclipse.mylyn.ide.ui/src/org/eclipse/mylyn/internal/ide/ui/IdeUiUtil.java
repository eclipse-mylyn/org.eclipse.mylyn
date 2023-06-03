/*******************************************************************************
 * Copyright (c) 2004, 2023 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - adapt to SimRel 2023-06
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.resources.ProjectExplorer;

/**
 * @author Mik Kersten
 */
public class IdeUiUtil {

	public static final String ID_VIEW_SYNCHRONIZE = "org.eclipse.team.sync.views.SynchronizeView"; //$NON-NLS-1$

	public static final String ID_NAVIGATOR = IPageLayout.ID_PROJECT_EXPLORER;

	public static IViewPart getView(String id) {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (activePage == null) {
			return null;
		}
		IViewPart view = activePage.findView(id);
		return view;
	}

	public static ProjectExplorer getNavigatorFromActivePage() {
		if (PlatformUI.getWorkbench() == null || PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null) {
			return null;
		}
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (activePage == null) {
			return null;
		}
		IViewPart view = activePage.findView(ID_NAVIGATOR);
		if (view instanceof ProjectExplorer) {
			return (ProjectExplorer) view;
		}
		return null;
	}
}
