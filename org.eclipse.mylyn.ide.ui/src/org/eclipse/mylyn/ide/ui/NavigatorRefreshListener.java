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
/*
 * Created on May 27, 2005
 */
package org.eclipse.mylar.ide.ui;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.mylar.ide.ResourceStructureBridge;
import org.eclipse.mylar.ide.ui.actions.ApplyMylarToNavigatorAction;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.views.navigator.ResourceNavigator;

/**
 * @author Mik Kersten
 */
public class NavigatorRefreshListener implements IMylarContextListener {

	public static final String ID_NAVIGATOR = "org.eclipse.ui.views.ResourceNavigator";

	public static ResourceNavigator getResourceNavigator() {
		if (Workbench.getInstance() == null || Workbench.getInstance().getActiveWorkbenchWindow() == null)
			return null;
		IWorkbenchPage activePage = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
		if (activePage == null)
			return null;
		IViewPart view = activePage.findView(ID_NAVIGATOR);
		if (view instanceof ResourceNavigator)
			return (ResourceNavigator) view;
		return null;
	}

	protected void refresh(IMylarElement node) {
		ResourceNavigator navigator = getResourceNavigator();
		if (navigator == null || navigator.getTreeViewer() == null
				|| navigator.getTreeViewer().getControl().isDisposed()) {
			return;
		}

		if (node != null) {
			IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(ResourceStructureBridge.CONTENT_TYPE);
			Object object = bridge.getObjectForHandle(node.getHandleIdentifier());
			if (object != null) {
				getResourceNavigator().getTreeViewer().update(object, null);
//					new String[]{IBasicPropertyConstants.P_TEXT});
			}
		} else {
			getResourceNavigator().getTreeViewer().refresh();
		}
	}

	public void contextActivated(IMylarContext taskscape) {
		refresh(null);
    	try {
	    	if (MylarPlugin.getContextManager().hasActiveContext()
	    		&& ApplyMylarToNavigatorAction.getDefault() != null
	        	&& ApplyMylarToNavigatorAction.getDefault().isChecked()) {
	    		
				TreeViewer viewer = getResourceNavigator().getTreeViewer();
				if (viewer != null) { 
					viewer.expandAll();
				}
	    	}	
    	} catch (Throwable t) {
    		MylarStatusHandler.log(t, "Could not update package explorer");
    	}
	}

	public void contextDeactivated(IMylarContext taskscape) {
		refresh(null);
	}

	public void presentationSettingsChanging(UpdateKind kind) {
		refresh(null);
	}

	public void presentationSettingsChanged(UpdateKind kind) {
		refresh(null);
	}

	public void interestChanged(IMylarElement node) {
		refresh(node);
	}

	public void interestChanged(List<IMylarElement> nodes) {
		IMylarElement node = nodes.get(nodes.size() - 1);
		interestChanged(node);
	}

	public void nodeDeleted(IMylarElement node) {
		refresh(node);
	}

	public void landmarkAdded(IMylarElement node) {
		refresh(node);
	}

	public void landmarkRemoved(IMylarElement node) {
		refresh(node);
	}

	public void edgesChanged(IMylarElement node) {
//		refresh(null);
	}
}
