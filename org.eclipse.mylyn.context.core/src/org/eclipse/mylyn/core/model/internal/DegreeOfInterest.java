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
 * Created on May 20, 2005
  */
package org.eclipse.mylar.core.model.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylar.core.model.IDegreeOfInterest;
import org.eclipse.mylar.core.model.InteractionEvent;
import org.eclipse.mylar.core.model.TaskscapeManager;
import org.eclipse.mylar.dt.MylarInterest;


/**
 * @author Mik Kersten
 */
class DegreeOfInterest implements IDegreeOfInterest {
    
    private List<InteractionEvent> events = new ArrayList<InteractionEvent>();
    protected transient ScalingFactors scaling;

    private float edits = 0;
    private float selections = 0;
    private float commands = 0;
    private float predictedBias = 0;
    private float propagatedBias = 0;
    private float manipulationBias = 0;
    
    private Taskscape taskscape;
    private int eventCountOnCreation;
    
    public DegreeOfInterest(Taskscape taskscape) {
        this.taskscape = taskscape;
        this.eventCountOnCreation = taskscape.getUserEventCount();
        init();
    }
    
    /**
     * TODO: lose the reference to TaskManager?
     */
    private void init() {
        scaling = TaskscapeManager.getScalingFactors();
    }
    
    void addEvent(InteractionEvent event) {
        events.add(0, event);
        updateEventState(event);
    }

    private void updateEventState(InteractionEvent event) {
        switch(event.getKind()) {
        case EDIT:
            edits += event.getInterestContribution();
            break;
        case SELECTION:
            selections += event.getInterestContribution();
            break;
        case COMMAND:
            commands += event.getInterestContribution();
            break;
        case PREDICTION:
            predictedBias += event.getInterestContribution();
            break;
        case PROPAGATION:
            propagatedBias += event.getInterestContribution();
            break;
        case MANIPULATION:
            manipulationBias += event.getInterestContribution();
            break;
        }
    }
    
    @MylarInterest(level=MylarInterest.Level.LANDMARK)
    public float getValue() {
        float value = getEncodedValue(); 
        value += predictedBias; 
        value += propagatedBias; 
//        value -= getDecayValue();
        return value;
    }

    public float getEncodedValue() {
        float value = 0;
        value += selections * scaling.get(InteractionEvent.Kind.SELECTION).getValue();
        value += edits * scaling.get(InteractionEvent.Kind.EDIT).getValue();
        value += commands * scaling.get(InteractionEvent.Kind.COMMAND).getValue();
        value += manipulationBias; 
        value -= getDecayValue();
//      return Math.max(0, value);   
        return value;
    }
    
    /**
     * @return a scaled decay count based on the number of events since the creation
     * of this interest object
     */
    public float getDecayValue() {
//    	if (isPredicted()) return 0;
        if (taskscape != null) {
            return (taskscape.getUserEventCount() - eventCountOnCreation) * scaling.getDecay().getValue();
        } else {
            return 0;
        }
    }

    /**
     * Sums predicted and propagated values
     */
    public boolean isPredicted() {
//        return getEncodedValue() == 0 && predictedBias + propagatedBias >= 0;
        return getEncodedValue() <= 0 && predictedBias + propagatedBias >= 0;

    }

    public boolean isLandmark() {
        return getValue() >= scaling.getLandmark();
    }

    public boolean isInteresting() {
        return getValue() > scaling.getInteresting();
    }
    
    @Override
    public String toString() {
        return "(" + "selections: " + selections + ", edits: " + edits 
            + ", commands: " + commands + ", predicted: " + predictedBias 
            + ", propagated: " + propagatedBias 
            + ", manipulation: "+ manipulationBias + ")";
    }
    
    /**
     * TODO: make unmodifiable?  Clients should not muck with this list.
     */
    public List<InteractionEvent> getEvents() {
        return events;
    }
    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeObject(events);
    }
    
    @SuppressWarnings(value="unchecked")
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        events = (List<InteractionEvent>)stream.readObject();
        init();
        for (InteractionEvent event : events) {
            updateEventState(event);
        }
    }   
}
