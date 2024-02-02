/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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

import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.internal.java.ui.JavaDeclarationsFilter;
import org.eclipse.mylyn.internal.java.ui.JavaUiBridgePlugin;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 */
public class FilterMembersAction extends Action implements IViewActionDelegate {

	public static final String PREF_ID = "org.eclipse.mylyn.java.ui.explorer.filter.members"; //$NON-NLS-1$

	public FilterMembersAction() {
		// setChecked(true);
		// try {
		// boolean checked=
		// ContextCore.getPreferenceStore().getBoolean(PREF_ID);
		// valueChanged(true, true);
		// } catch (Exception e) {
		//
		// }
	}

	@Override
	public void run(IAction action) {
		valueChanged(isChecked(), true);

	}

	private void valueChanged(final boolean on, boolean store) {
		if (store) {
			JavaUiBridgePlugin.getDefault().getPreferenceStore().setValue(PREF_ID, on);
		}

		setChecked(true);
		PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
		ViewerFilter existingFilter = null;
		for (int i = 0; i < packageExplorer.getTreeViewer().getFilters().length; i++) {
			ViewerFilter filter = packageExplorer.getTreeViewer().getFilters()[i];
			if (filter instanceof JavaDeclarationsFilter) {
				existingFilter = filter;
			}
		}
		if (existingFilter != null) {
			packageExplorer.getTreeViewer().removeFilter(existingFilter);
		} else {
			packageExplorer.getTreeViewer().addFilter(new JavaDeclarationsFilter());
		}
	}

	@Override
	public void init(IViewPart view) {
		// don't need to do anything on init
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// don't care when the selection changes
	}

}
