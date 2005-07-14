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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.ContextManager;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Mik Kersten
 */
public abstract class AbstractInterestManipulationAction implements IViewActionDelegate, IWorkbenchWindowActionDelegate {

    public static final String SOURCE_ID = "org.eclipse.mylar.ui.interest.user";
	
    public void init(IViewPart view) {
    	// don't need to do anything
    }

    protected void changeInterestForSelected(boolean increment) {
        IMylarContextNode node = MylarPlugin.getContextManager().getActiveNode();
        if (node == null) return;
        float originalValue = node.getDegreeOfInterest().getValue();
        float changeValue = 0;
        if (!increment) {
            if (node.getDegreeOfInterest().isLandmark()) { // keep it interesting
                changeValue = (-1 * originalValue) + 1; 
            } else { 
            	if (originalValue >=0) changeValue = (-1 * originalValue)-1;
            }
        } else {
            if (originalValue >  ContextManager.getScalingFactors().getLandmark()) {
                changeValue = 0;
            } else {
                changeValue = ContextManager.getScalingFactors().getLandmark() - originalValue + 1;
            } 
        }
        if (changeValue != 0) {
            InteractionEvent interactionEvent = new InteractionEvent(
                    InteractionEvent.Kind.MANIPULATION, 
                    node.getStructureKind(), 
                    node.getElementHandle(), 
                    SOURCE_ID,
                    changeValue);
            MylarPlugin.getContextManager().handleInteractionEvent(interactionEvent);
        }
    }
    
    public void dispose() { 
    	// don't care when we are disposed
    }
    
    public void selectionChanged(IAction action, ISelection selection) { 
    	// don't care about selection changes
    }

    public void init(IWorkbenchWindow window) {
    	// don't have anything to initialize
    }
}
