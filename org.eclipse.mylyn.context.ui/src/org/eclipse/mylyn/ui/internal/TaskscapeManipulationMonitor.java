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
 * Created on May 22, 2005
  */
package org.eclipse.mylar.ui.internal;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.mylar.core.*;
import org.eclipse.mylar.core.model.*;
import org.eclipse.mylar.ui.actions.InterestDecrementAction;
import org.eclipse.mylar.ui.actions.InterestIncrementAction;


/**
 * @author Mik Kersten
 */
public class TaskscapeManipulationMonitor extends AbstractCommandMonitor {

    public static final String SOURCE_ID = "org.eclipse.mylar.ui.interest.user";
       
    /**
     * HACK: uses numbers
     */
    @Override
    protected void handleCommandExecution(String commandId, ExecutionEvent event) {
        
        ITaskscapeNode node = MylarPlugin.getTaskscapeManager().getActiveNode();
        if (node == null) return;
        float originalValue = node.getDegreeOfInterest().getEncodedValue();
        float changeValue = 0;
        if (commandId.equals(InterestDecrementAction.COMMAND_ID)) {
            if (node.getDegreeOfInterest().isLandmark()) { // keep it interesting
                changeValue = (-1 * originalValue) + 1; 
            } else { // XXX could be < 0
                changeValue = (-1 * originalValue) - TaskscapeManager.getScalingFactors().getDecay().getValue() -1;
            }
        } else if (commandId.equals(InterestIncrementAction.COMMAND_ID)) {
            if (originalValue >  TaskscapeManager.getScalingFactors().getLandmark()) {
                changeValue = 0;
            } else {
                changeValue = TaskscapeManager.getScalingFactors().getLandmark() - originalValue + 1;
            } 
        }
        if (changeValue != 0) {
            InteractionEvent interactionEvent = new InteractionEvent(
                    InteractionEvent.Kind.MANIPULATION, 
                    node.getStructureKind(), 
                    node.getElementHandle(), 
                    SOURCE_ID,
                    changeValue);
//            interactionEvent.setInterestContribution(changeValue);
            MylarPlugin.getTaskscapeManager().handleInteractionEvent(interactionEvent);
        }
    }
}

//CompositeTaskscapeNode activeNode = (CompositeTaskscapeNode)getActiveNode();
//if (activeNode == null || activeNode.getNodes().size() == 0) {
//  activeNode = (CompositeTaskscapeNode) composite.create(activeNode.getElementHandle(), activeNode.getName(), activeNode.getKind());
//}

//if (MylarPlugin.getTaskscapeManager().getActiveNode() != null) {
//    MylarPlugin.getTaskscapeManager().lowerDoiByUser(
//        MylarPlugin.getTaskscapeManager().getActiveNode().getElementHandle()
//    ); 
//}

//node.getDegreeOfInterest().getDegreeOfInterest().reset();
//composite.remove(node);
//for (ITaskscapeListener listener : listeners) listener.nodeDeleted(node);

//MylarPlugin.getTaskscapeManager().getScalingFactors().getLandmark() - originalValue + 2;
//composite.removeLandmark(node);
//node.getDegreeOfInterest().getDegreeOfInterest().reset();
//node.getDegreeOfInterest().getDegreeOfInterest().increment(IDegreeOfInterest.Value.Selections);
//for (ITaskscapeListener listener : listeners) listener.landmarkRemoved(node);
//for (ITaskscapeListener provider : relationshipProviders) provider.landmarkRemoved(node);