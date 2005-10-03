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
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.java.MylarJavaPlugin;
import org.eclipse.mylar.ui.InterestFilter;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.mylar.ui.actions.AbstractApplyMylarAction;

/**
 * @author Mik Kersten
 */
public class ApplyMylarToPackageExplorerAction extends AbstractApplyMylarAction implements IPropertyChangeListener {

	public static ApplyMylarToPackageExplorerAction INSTANCE;
	
	public void init(IAction action) {
		super.init(action);
	}
	
	public ApplyMylarToPackageExplorerAction() {
		super(new InterestFilter());
		INSTANCE = this;
		configureAction();
	}
	
	@Override
	public void update() {
		super.update();
	}
	
	@Override
	public StructuredViewer getViewer() {
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

	public void propertyChange(PropertyChangeEvent event) {
		if (MylarJavaPlugin.PACKAGE_EXPLORER_AUTO_FILTER_ENABLE.equals(event.getProperty())) {
			configureAction();
		}
	}

	private void configureAction() {
		if (MylarJavaPlugin.getDefault().getPreferenceStore().getBoolean(MylarJavaPlugin.PACKAGE_EXPLORER_AUTO_FILTER_ENABLE)) {
			MylarUiPlugin.getDefault().getViewerManager().addManagedAction(this);
		} else {
			MylarUiPlugin.getDefault().getViewerManager().removeManagedAction(this);
		}
	}

	public void propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent event) {
		// ignore
	}
}

