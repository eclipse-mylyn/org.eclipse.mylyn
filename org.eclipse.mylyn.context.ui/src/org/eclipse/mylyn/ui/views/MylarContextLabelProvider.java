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

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.mylar.core.IMylarContextElement;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.swt.graphics.Image;


/**
 * TODO: refactor edge stuff
 * 
 * @author Mik Kersten
 */
public class MylarContextLabelProvider implements ILabelProvider {

    public Image getImage(Object element) {
    	
    	if (element instanceof IMylarContextElement) {
    		ILabelProvider provider = MylarUiPlugin.getDefault().getContextLabelProvider(
    				((IMylarContextElement)element).getContentKind());
            return provider.getImage(element);
        }  else {
        	return null;
        }
//    	else if (element instanceof MylarContextEdge) {
//            IMylarContextEdge edge = (IMylarContextEdge)element;
//            return provider.getImage(edge);
//            IMylarUiBridge bridge = MylarUiPlugin.getDefault().getUiBridge(edge.getStructureKind());
//            ImageDescriptor descriptor = bridge.getIconForRelationship(edge.getRelationshipHandle());
//            if (descriptor != null) return MylarImages.getImage(descriptor);
//        }
    }

    public String getText(Object object) {
    	if (object instanceof IMylarContextElement) {
    		IMylarContextElement element = (IMylarContextElement)object;
    		ILabelProvider provider = MylarUiPlugin.getDefault().getContextLabelProvider(
    				element.getContentKind());
            if (MylarUiPlugin.getDefault().isDecorateInterestMode()) {
                return provider.getText(element) + " [" + element.getDegreeOfInterest().getValue() + "]";     
            } else {
                return provider.getText(element);
            }
        }  else {
        	return "" + object;
        }
//    	if (object instanceof IMylarContextNode) {
//            IMylarContextNode node = (IMylarContextNode)object;
//            ILabelProvider provider = MylarUiPlugin.getDefault().getContextLabelProvider(node.getContentKind());
//
//        } else if (object instanceof IMylarContextEdge) {
//            IMylarContextEdge edge = (IMylarContextEdge)object;
//            String interestDecoration = "";
//            if (MylarUiPlugin.getDefault().isDecorateInterestMode()) interestDecoration = " [" + edge.getDegreeOfInterest().getValue() + "]";     
//            
//            IMylarUiBridge adapter = MylarUiPlugin.getDefault().getUiBridge(edge.getContentKind());
//            return adapter.getNameForRelationship(edge.getRelationshipHandle()) + interestDecoration; 
//        }
//        return "" + object;
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
