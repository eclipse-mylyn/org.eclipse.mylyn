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

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.ui.InterestFilter;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Shawn Minto
 */
public class FilterOutlineAction extends AbstractInterestFilterAction {
	
	public static FilterOutlineAction INSTANCE;
	
	public FilterOutlineAction() {
		super(new InterestFilter());
		INSTANCE = this;
	}
	
	/**
	 * TODO: a bit wierd how it gets the first viewer
	 */
	@Override
	protected StructuredViewer getViewer() {
		IEditorPart activeEditorPart = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        List<TreeViewer> viewers = MylarUiPlugin.getDefault().getUiBridgeForEditor(activeEditorPart).getTreeViewers(activeEditorPart);
        if (viewers.size() > 0) {
        	return viewers.get(0);
        } else {
        	return null;
        }
        //        for (TreeViewer viewer : viewers) { 
//            if (viewer != null) {
//                boolean found = false;
//                for (int i = 0; i < viewer.getFilters().length; i++) {
//                    ViewerFilter filter = viewer.getFilters()[i];
//                    if (filter instanceof InterestFilter) found = true;
//                }
//		return null;
	}

	/**
	 * TODO: cache viewer?
	 */
	@Override
	protected void refreshViewer() {
		StructuredViewer viewer = getViewer();
		if (viewer != null) viewer.refresh();
	}

	public static FilterOutlineAction getDefault() {
		return INSTANCE;
	}
}