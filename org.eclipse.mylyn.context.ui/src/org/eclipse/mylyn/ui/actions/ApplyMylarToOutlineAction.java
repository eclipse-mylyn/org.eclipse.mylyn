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

package org.eclipse.mylar.ui.actions;


import java.util.List;

import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.ui.InterestFilter;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Shawn Minto
 */
public class ApplyMylarToOutlineAction extends AbstractApplyMylarAction {
	
	public static ApplyMylarToOutlineAction INSTANCE;
	
	public ApplyMylarToOutlineAction() {
		super(new InterestFilter());
		INSTANCE = this;
	}
	
	/**
	 * TODO: a bit wierd how it gets the first viewer
	 */
	@Override
	protected StructuredViewer getViewer() {
		if (Workbench.getInstance() == null || Workbench.getInstance().getActiveWorkbenchWindow() == null) return null;
		IEditorPart activeEditorPart = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        List<TreeViewer> viewers = MylarUiPlugin.getDefault().getUiBridgeForEditor(activeEditorPart).getTreeViewers(activeEditorPart);
        if (viewers.size() > 0) {
        	return viewers.get(0);
        } else {
        	return null;
        }
	}

	/**
	 * TODO: cache viewer?
	 */
	@Override
	public void refreshViewer() {
		StructuredViewer viewer = getViewer();
		if (viewer != null && viewer.getControl().isVisible()) viewer.refresh();
	}

	public static ApplyMylarToOutlineAction getDefault() {
		return INSTANCE;
	}

	public void propertyChange(PropertyChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
}