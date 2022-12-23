/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ui;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.navigator.ResourceNavigator;

/**
 * @author Mik Kersten
 */
@SuppressWarnings("deprecation")
public class IdeUiUtil {

	public static final String ID_VIEW_SYNCHRONIZE = "org.eclipse.team.sync.views.SynchronizeView"; //$NON-NLS-1$

	public static final String ID_NAVIGATOR = "org.eclipse.ui.views.ResourceNavigator"; //$NON-NLS-1$

	public static IViewPart getView(String id) {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (activePage == null) {
			return null;
		}
		IViewPart view = activePage.findView(id);
		return view;
	}

	public static ResourceNavigator getNavigatorFromActivePage() {
		if (PlatformUI.getWorkbench() == null || PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null) {
			return null;
		}
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (activePage == null) {
			return null;
		}
		IViewPart view = activePage.findView(ID_NAVIGATOR);
		if (view instanceof ResourceNavigator) {
			return (ResourceNavigator) view;
		}
		return null;
	}
}
