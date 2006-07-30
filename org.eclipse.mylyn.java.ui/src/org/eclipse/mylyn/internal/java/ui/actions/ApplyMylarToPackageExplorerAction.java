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

package org.eclipse.mylar.internal.java.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.ui.filters.ImportDeclarationFilter;
import org.eclipse.jdt.internal.ui.filters.PackageDeclarationFilter;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylar.context.ui.InterestFilter;
import org.eclipse.mylar.internal.context.ui.actions.AbstractAutoApplyMylarAction;
import org.eclipse.mylar.internal.java.ui.JavaDeclarationsFilter;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 */
public class ApplyMylarToPackageExplorerAction extends AbstractAutoApplyMylarAction {

	public ApplyMylarToPackageExplorerAction() {
		super(new InterestFilter());
	}

	@Override
	protected void valueChanged(IAction action, boolean on, boolean store) {
		super.valueChanged(action, on, store);
	}

	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		// TODO: get from super
		IViewPart part = super.getPartForAction();
		if (part instanceof PackageExplorerPart) {
			viewers.add(((PackageExplorerPart) part).getTreeViewer());
		}
		return viewers;
	}

	public void propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent event) {
		// ignore
	}

	@Override
	public List<Class> getPreservedFilters() {
		List<Class> preserved = new ArrayList<Class>();
		preserved.add(ImportDeclarationFilter.class);
		preserved.add(PackageDeclarationFilter.class);
		preserved.add(JavaDeclarationsFilter.class);
		return preserved;
	}
}
