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
 * Created on Apr 12, 2005
  */
package org.eclipse.mylar.ui;

import org.eclipse.jface.viewers.*;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.core.model.internal.TaskscapeEdge;
import org.eclipse.mylar.ui.internal.UiUtil;


/**
 * @author Mik Kersten
 */
public class InterestDecoratorLightweight implements ILightweightLabelDecorator {

    public InterestDecoratorLightweight() {
        super();
    }

    public void decorate(Object element, IDecoration decoration) {
        try {
            ITaskscapeNode node = null;
            if (element instanceof TaskscapeEdge) {
                decoration.setForegroundColor(MylarUiPlugin.getDefault().getColorMap().RELATIONSHIP);
            } else  if (element instanceof ITaskscapeNode) {
                node = (ITaskscapeNode)element;
            } else {
                IMylarStructureBridge adapter = MylarPlugin.getDefault().getStructureBridge(element);
                if (adapter != null && adapter.getResourceExtension() != null) {
                    node = MylarPlugin.getTaskscapeManager().getNode(adapter.getHandleIdentifier(element));
                }
            }
            if (node != null) {
                decoration.setBackgroundColor(UiUtil.getBackgroundForElement(node));
                decoration.setForegroundColor(UiUtil.getForegroundForElement(node));      
                if (node.getDegreeOfInterest().isLandmark() && !node.getDegreeOfInterest().isPredicted()) {
                    decoration.setFont(UiUtil.BOLD);
                } 
            } 
        } catch (Exception e) {
        	MylarPlugin.log(this.getClass().toString(), e);
        }
    } 
  
    public void addListener(ILabelProviderListener listener) {
    	// don't care about listeners
    }

    public void dispose() {
    	// don't care when we are disposed
    }

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {
    	// don't care about listeners
    }

}
