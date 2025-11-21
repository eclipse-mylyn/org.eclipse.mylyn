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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.context.ui.BrowseFilteredListener;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Mik Kersten
 */
@SuppressWarnings("restriction")
@Deprecated
public class ShowFilteredChildrenAction extends Action implements IObjectActionDelegate, IViewActionDelegate {

	private BrowseFilteredListener browseFilteredListener;

	private TreeViewer treeViewer;

	private IStructuredSelection selection;

	@Deprecated
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		if (targetPart instanceof PackageExplorerPart) {
			treeViewer = ((PackageExplorerPart) targetPart).getTreeViewer();
			browseFilteredListener = new BrowseFilteredListener(treeViewer);
		}
	}

	@Deprecated
	@Override
	public void init(IViewPart targetPart) {
		if (targetPart instanceof PackageExplorerPart) {
			treeViewer = ((PackageExplorerPart) targetPart).getTreeViewer();
			browseFilteredListener = new BrowseFilteredListener(treeViewer);
		}
	}

	@Deprecated
	@Override
	public void run(IAction action) {
		if (selection != null) {
			browseFilteredListener.unfilterSelection(treeViewer, selection);
		}
	}

	@Deprecated
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		}
	}
}
