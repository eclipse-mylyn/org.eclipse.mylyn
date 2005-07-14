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
package org.eclipse.mylar.ui.resources;


import java.util.List;

import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.views.navigator.ResourceNavigator;


/**
 * @author Mik Kersten
 */
public class NavigatorRefreshListener implements IMylarContextListener {

    public static ResourceNavigator getResourceNavigator() {
        if (Workbench.getInstance() == null || Workbench.getInstance().getActiveWorkbenchWindow() == null) return null;
        IWorkbenchPage activePage= Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
        if (activePage == null)
            return null;
        IViewPart view= activePage.findView("org.eclipse.ui.views.ResourceNavigator");
        if (view instanceof ResourceNavigator)
            return (ResourceNavigator)view;
        return null;    
    }
    
    protected void refresh(IMylarContextNode node) {
        ResourceNavigator navigator = getResourceNavigator();
        if (navigator == null || navigator.getTreeViewer() == null) return;
        
        if (node != null) {
            Object object = MylarPlugin.getDefault().getGenericResourceBridge().getObjectForHandle(node.getElementHandle());
            getResourceNavigator().getTreeViewer().refresh(object);
        } else {
            getResourceNavigator().getTreeViewer().refresh();
        }
    }
    
    public void contextActivated(IMylarContext taskscape) {
        refresh(null);
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

    public void interestChanged(IMylarContextNode node) {
        refresh(node);
    }

    public void interestChanged(List<IMylarContextNode> nodes) {
        IMylarContextNode node = nodes.get(nodes.size()-1);
        interestChanged(node);
    }

    public void nodeDeleted(IMylarContextNode node) {
        refresh(node);
    }

    public void landmarkAdded(IMylarContextNode node) {
        refresh(node);
    }

    public void landmarkRemoved(IMylarContextNode node) {
        refresh(node);
    }

    public void relationshipsChanged() {
        refresh(null);
    }
}
