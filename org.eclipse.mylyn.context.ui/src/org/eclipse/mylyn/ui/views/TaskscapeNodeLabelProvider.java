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
 * Created on Apr 18, 2005
  */
package org.eclipse.mylar.ui.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.mylar.core.model.ITaskscapeEdge;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.core.model.internal.TaskscapeEdge;
import org.eclipse.mylar.ui.IMylarUiBridge;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.swt.graphics.Image;


/**
 * TODO: refactor edge stuff
 * 
 * @author Mik Kersten
 */
public class TaskscapeNodeLabelProvider implements ILabelProvider {

    public Image getImage(Object element) {
        if (element instanceof ITaskscapeNode) {
            ITaskscapeNode node = (ITaskscapeNode)element;
            IMylarUiBridge adapter = MylarUiPlugin.getDefault().getUiBridge(node.getStructureKind());
            if (adapter != null) {
                return adapter.getLabelProvider().getImage(element);
            } else {
                return null;
            }
        } else if (element instanceof TaskscapeEdge) {
            ITaskscapeEdge edge = (ITaskscapeEdge)element;
            IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridge(edge.getStructureKind());
            ImageDescriptor descriptor = bridge.getIconForRelationship(edge.getRelationshipHandle());
            if (descriptor != null) return MylarImages.getImage(descriptor);
        }
        return null;
    }

    public String getText(Object object) {
        assert(object != null);
        if (object instanceof ITaskscapeNode) {
            ITaskscapeNode node = (ITaskscapeNode)object;
            IMylarUiBridge adapter = MylarUiPlugin.getDefault().getUiBridge(node.getStructureKind());
            if (MylarUiPlugin.getDefault().isDecorateInterestMode()) {
                return adapter.getLabelProvider().getText(object) + " [" + node.getDegreeOfInterest().getValue() + "]";     
            } else {
                return adapter.getLabelProvider().getText(object);
            }
        } else if (object instanceof ITaskscapeEdge) {
            ITaskscapeEdge edge = (ITaskscapeEdge)object;
            String interestDecoration = "";
            if (MylarUiPlugin.getDefault().isDecorateInterestMode()) interestDecoration = " [" + edge.getDegreeOfInterest().getValue() + "]";     
            
            IMylarUiBridge adapter = MylarUiPlugin.getDefault().getUiBridge(edge.getStructureKind());
            return adapter.getNameForRelationship(edge.getRelationshipHandle()) + interestDecoration; 
        }
        return "" + object;
    }

    public boolean isLabelProperty(Object element, String property) {
        return true;
        // TODO: implement?
    }

    public void addListener(ILabelProviderListener listener) {
        // TODO: implement?
    }

    public void dispose() {
        // TODO: implement?        
    }
    
    public void removeListener(ILabelProviderListener listener) {
        // TODO: implement?
    }
}
