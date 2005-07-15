/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.java.ui.actions;

import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.ui.InterestFilter;
import org.eclipse.mylar.ui.actions.AbstractApplyMylarAction;

/**
 * @author Mik Kersten
 */
public class ApplyMylarToPackageExplorerAction extends AbstractApplyMylarAction {

	public static ApplyMylarToPackageExplorerAction INSTANCE;
	
	public void init(IAction action) {
		super.init(action);

	}
	
	public ApplyMylarToPackageExplorerAction() {
		super(new InterestFilter());
		INSTANCE = this;
	}
	
	@Override
	protected StructuredViewer getViewer() {
		PackageExplorerPart part = PackageExplorerPart.getFromActivePerspective();
		if (part != null) {
			return part.getTreeViewer();
		} else {
			return null;
		}
	}

	@Override
	public void refreshViewer() {
		TreeViewer viewer = (TreeViewer)getViewer();
		viewer.refresh();
	}

	public static ApplyMylarToPackageExplorerAction getDefault() {
		return INSTANCE;
	}
	
}