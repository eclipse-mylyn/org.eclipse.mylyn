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

import org.eclipse.mylar.core.IDegreeOfSeparation;
import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContextManager;


/**
 * @author Mik Kersten
 */
public abstract class RelationshipProvider implements IMylarContextListener {

    private boolean enabled = false;
    private String id;
    private String structureKind;
    
    public void nodeDeleted(IMylarContextNode node) {
    	// we don't care when this happens
    }

    public String getId() {
        return id;
    }
    
    public RelationshipProvider(String structureKind, String id) {
        this.id = id;
        this.structureKind = structureKind;
    }
   
    protected abstract void findRelated(final IMylarContextNode node, int degreeOfSeparation);
    
    /**
     * @param limitTo Only used in thye AbstractJavaRelationshipProvider for the search type
     */
    public abstract IMylarSearchOperation getSearchOperation(IMylarContextNode node, int limitTo, int degreeOfSeparation);

    public abstract String getName();
    
    public boolean acceptResultElement(Object element){
    	return true;
    }
    
    public void landmarkAdded(IMylarContextNode node) { 
        if (enabled) findRelated(node, MylarContextManager.getScalingFactors().getDegreeOfSeparation());
  } 
    
    public void landmarkRemoved(IMylarContextNode node) {
//        MylarPlugin.getTaskscapeManager().removeEdge(element, id);
    }
     
    protected void incrementInterest(int degreeOfSeparation, String elementKind, String elementHandle) {
        int predictedInterest = 1;//(7-degreeOfSeparation) * TaskscapeManager.getScalingFactors().getDegreeOfSeparationScale();
        InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.PREDICTION, elementKind, elementHandle, getSourceId(), getId(), null, predictedInterest);
        MylarPlugin.getContextManager().handleInteractionEvent(event);
    }
    
    protected abstract String getSourceId();

    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
   
    public abstract List<IDegreeOfSeparation> getDegreesOfSeparation();
    
    public void presentationSettingsChanging(UpdateKind kind) { 
    	// we don't care about this event
    }
    
    public void presentationSettingsChanged(UpdateKind kind) { 
    	// we don't care about this event
    }
    
    public void contextActivated(IMylarContext taskscape) { 
    	// we don't care about this event
    }

    public void contextDeactivated(IMylarContext taskscape) { 
    	// we don't care about this event
    }

    public void interestChanged(IMylarContextNode info) { 
    	// we don't care about this event
    }
    
    public void interestChanged(List<IMylarContextNode> nodes) { 
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
