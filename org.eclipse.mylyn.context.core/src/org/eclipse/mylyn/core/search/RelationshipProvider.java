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
 * Created on Jan 31, 2005
  */
package org.eclipse.mylar.core.search;

import java.util.List;

import org.eclipse.mylar.core.ITaskscapeListener;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.ITaskscape;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.core.model.InteractionEvent;
import org.eclipse.mylar.core.model.TaskscapeManager;


/**
 * @author Mik Kersten
 */
public abstract class RelationshipProvider implements ITaskscapeListener {

    private boolean enabled = false;
    private String id;
    private String structureKind;
    
    public void nodeDeleted(ITaskscapeNode node) {
    	// we don't care when this happens
    }

    public String getId() {
        return id;
    }

    
    public RelationshipProvider(String structureKind, String id) {
        this.id = id;
        this.structureKind = structureKind;
    }
   
    protected abstract void findRelated(final ITaskscapeNode node, int degreeOfSeparation);
    
    /**
     * @param limitTo Only used in thye AbstractJavaRelationshipProvider for the search type
     */
    public abstract IMylarSearchOperation getSearchOperation(ITaskscapeNode node, int limitTo, int degreeOfSeparation);

    public abstract String getName();
    
    public boolean acceptResultElement(Object element){
    	return true;
    }
    
    public void landmarkAdded(ITaskscapeNode node) { 
        if (enabled) findRelated(node, TaskscapeManager.getScalingFactors().getDegreeOfSeparation());
  } 
    
    public void landmarkRemoved(ITaskscapeNode node) {
//        MylarPlugin.getTaskscapeManager().removeEdge(element, id);
    }
     
    protected void incrementInterest(int degreeOfSeparation, String elementKind, String elementHandle) {
        int predictedInterest = 1;//(7-degreeOfSeparation) * TaskscapeManager.getScalingFactors().getDegreeOfSeparationScale();
        InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.PREDICTION, elementKind, elementHandle, getSourceId(), getId(), null, predictedInterest);
        MylarPlugin.getTaskscapeManager().handleInteractionEvent(event);
    }
    
    protected abstract String getSourceId();

//    protected void addResultsAsRelationships(ITaskscapeNode node, String edgeKind, List<ITaskscapeNode> nodes) {
//        MylarPlugin.getTaskscapeManager().addEdge(node, edgeKind, nodes);
//    } 
    
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
   
    public void presentationSettingsChanging(UpdateKind kind) { 
    	// we don't care about this event
    }
    
    public void presentationSettingsChanged(UpdateKind kind) { 
    	// we don't care about this event
    }
    
    public void taskscapeActivated(ITaskscape taskscape) { 
    	// we don't care about this event
    }

    public void taskscapeDeactivated(ITaskscape taskscape) { 
    	// we don't care about this event
    }

    public void interestChanged(ITaskscapeNode info) { 
    	// we don't care about this event
    }
    
    public void interestChanged(List<ITaskscapeNode> nodes) { 
    	// we don't care about this event
    }

    public void relationshipsChanged() { 
    	// we don't care about this event
    }
    
    @Override
    public String toString() {
        return "(provider for: " + id.toString() + ")";
    }

    public String getStructureKind() {
        return structureKind;
    }
}
