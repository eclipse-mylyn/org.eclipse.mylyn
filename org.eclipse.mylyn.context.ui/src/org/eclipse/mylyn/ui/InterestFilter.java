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
 * Created on Apr 7, 2005
  */
package org.eclipse.mylar.ui;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContextManager;


/**
 * @author Mik Kersten
 */
public class InterestFilter extends ViewerFilter {

	@Override
    public boolean select(Viewer viewer, Object parent, Object element) {
        try {
        	if (!(viewer instanceof StructuredViewer)) return true;
        	if (!containsMylarInterestFilter((StructuredViewer)viewer)) return true;
            IMylarContextNode node = null;
            if (element instanceof IMylarContextNode) {
                node = (IMylarContextNode)element;
            } else { 
                IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(element);
                if (bridge == null) return false;
                if (!bridge.canFilter(element)) return true;                
                String handle = bridge.getHandleIdentifier(element);
                node = MylarPlugin.getContextManager().getNode(handle);
                String parentHandle = bridge.getParentHandle(handle);
                if (MylarPlugin.getContextManager().isTempRaised(parentHandle)) return true;
            }
            if (node != null) {
                return node.getDegreeOfInterest().getValue() > MylarContextManager.getScalingFactors().getInteresting();
            } else {
                return false;
            }
        } catch (Exception e) {
        	MylarPlugin.log(e, "filter failed");
        }
        return false;
    }   
	
	private boolean containsMylarInterestFilter(StructuredViewer viewer) {
		boolean found = false;
		for (int i = 0; i < viewer.getFilters().length; i++) {
			ViewerFilter filter = viewer.getFilters()[i];
			if (filter instanceof InterestFilter) found = true;
		}
		return found;
	}
}
