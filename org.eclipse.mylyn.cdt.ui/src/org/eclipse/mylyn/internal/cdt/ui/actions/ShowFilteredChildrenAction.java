/*******************************************************************************
 * Copyright (c) 2004, 2008 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	   Red Hat Inc. - Modification for CDT usage
 *     Anton Leherbauer (Wind River Systems) - Project Explorer integration
 *******************************************************************************/

package org.eclipse.cdt.mylyn.internal.ui.actions;

import org.eclipse.cdt.internal.ui.cview.CView;
import org.eclipse.cdt.mylyn.internal.ui.CDTUIBridgePlugin;
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
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @author Mik Kersten
 * @author Jeff Johnston
 */
public class ShowFilteredChildrenAction extends Action implements IObjectActionDelegate, IViewActionDelegate {

	private BrowseFilteredListener browseFilteredListener;

	private TreeViewer treeViewer;

	private IStructuredSelection selection;

	public ShowFilteredChildrenAction() {
		super();
		setText(CDTUIBridgePlugin.getResourceString("ShowFilteredChildren.label")); //$NON-NLS-1$
		setToolTipText(CDTUIBridgePlugin.getResourceString("ShowFilteredChildren.tooltip")); //$NON-NLS-1$
		setImageDescriptor(CDTUIBridgePlugin.getImageDescriptor("icons/etool16/browse-filtered.gif")); //$NON-NLS-1$
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		if (targetPart instanceof CView) {
			treeViewer = ((CView) targetPart).getViewer();
			browseFilteredListener = new BrowseFilteredListener(treeViewer);
		} else if (targetPart instanceof CommonNavigator) {
			treeViewer= (TreeViewer) targetPart.getAdapter(CommonViewer.class);
			browseFilteredListener = new BrowseFilteredListener(treeViewer);
		}
	}

	public void init(IViewPart targetPart) {
		if (targetPart instanceof CView) {
			treeViewer = ((CView) targetPart).getViewer();
			browseFilteredListener = new BrowseFilteredListener(treeViewer);
		} else if (targetPart instanceof CommonNavigator) {
			treeViewer= (TreeViewer) targetPart.getAdapter(CommonViewer.class);
			browseFilteredListener = new BrowseFilteredListener(treeViewer);
		}
	}

	@Override
	public void run() {
		run(this);
	}

	public void run(IAction action) {
		if (selection != null) {
			browseFilteredListener.unfilterSelection(treeViewer, selection);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		}
	}
}
