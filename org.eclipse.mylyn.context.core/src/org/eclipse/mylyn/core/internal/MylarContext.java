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
 * Created on Jul 22, 2004
  */
package org.eclipse.mylar.core.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.dt.MylarInterest;

/**
 * @author Mik Kersten
 */
public class MylarContext implements IMylarContext, Serializable {

    private static final long serialVersionUID = 1L;
    private String id;

    private List<InteractionEvent> interactionHistory = new ArrayList<InteractionEvent>();
    protected transient Map<String, MylarContextElement> nodes = new HashMap<String, MylarContextElement>();
    
    protected transient MylarContextElement activeNode = null;
    protected transient List tempRaised = new ArrayList();
    protected transient Map<String, IMylarElement> landmarks;
    protected transient ScalingFactors scaling;
    private transient InteractionEvent lastEdgeEvent = null;
    private transient MylarContextElement lastEdgeNode = null;
    private transient int numUserEvents = 0;
    
    public MylarContext() { 
    	// only needed for serialization
    }
    
    void parseInteractionHistory() {
        nodes = new HashMap<String, MylarContextElement>();
        landmarks = new HashMap<String, IMylarElement>();
        for (InteractionEvent event : interactionHistory) parseInteractionEvent(event);
        updateLandmarks();
        activeNode = lastEdgeNode;
    }

    public MylarContext(String id, ScalingFactors scaling) { 
        this.id = id;
        this.scaling = scaling;
        parseInteractionHistory();
    }

    public IMylarElement parseEvent(InteractionEvent event) {
    	interactionHistory.add(event);
        return parseInteractionEvent(event);
    }

    /**
     * Propagations and predictions are not addes as edges
     */
    @MylarInterest(level=MylarInterest.Level.LANDMARK)
    private IMylarElement parseInteractionEvent(InteractionEvent event) {
    	if (event.getKind().isUserEvent()) numUserEvents++;
        MylarContextElement node = nodes.get(event.getStructureHandle());
        if (node == null) {
            node = new MylarContextElement(event.getContentType(), event.getStructureHandle(), this);
            nodes.put(event.getStructureHandle(), node);
        }

        if (event.getNavigation() != null && !event.getNavigation().equals("null") && lastEdgeEvent != null && lastEdgeNode != null
            && event.getKind() != InteractionEvent.Kind.PROPAGATION 
            && event.getKind() != InteractionEvent.Kind.PREDICTION) {
            IMylarElement navigationSource = nodes.get(lastEdgeEvent.getStructureHandle());
            if (navigationSource != null) {
               MylarContextRelation edge = lastEdgeNode.getEdge(event.getStructureHandle());
               if (edge == null) {
                    edge = new MylarContextRelation(event.getContentType(), event.getNavigation(), lastEdgeNode, node, this);
                    lastEdgeNode.addEdge(edge);
                }
                DegreeOfInterest doi = (DegreeOfInterest)edge.getDegreeOfInterest();
                doi.addEvent(event); 
            }
        } 
        DegreeOfInterest doi = (DegreeOfInterest)node.getDegreeOfInterest();
        
        doi.addEvent(event); 
        if (doi.isLandmark()) {
        	landmarks.put(node.getHandleIdentifier(), node);
        } else {
            landmarks.remove(node.getHandleIdentifier()); // TODO: redundant
        }
        if (event.getKind().isUserEvent()) {
            lastEdgeEvent = event;
            lastEdgeNode = node;
            activeNode = node;
        } 
        return node;        
    }

    private void updateLandmarks() {
//        landmarks = new HashMap<String, ITaskscapeNode>();
        for (MylarContextElement node : nodes.values()) {
            if (node.getDegreeOfInterest().isLandmark()) landmarks.put(node.getHandleIdentifier(), node);
        }
    }
    
    public IMylarElement get(String elementHandle) {
        return nodes.get(elementHandle);
    }
    
    public List<IMylarElement> getInteresting() {
        List<IMylarElement> elements = new ArrayList<IMylarElement>();
        for (String key : nodes.keySet()) {
            MylarContextElement info = nodes.get(key);
            if (info.getDegreeOfInterest().isInteresting()) {
                elements.add(info);  
            }
        }
        return elements;        
    }

    public List<IMylarElement> getLandmarks() {
        return Collections.unmodifiableList(new ArrayList<IMylarElement>(landmarks.values()));
    }

    public IMylarElement getActiveNode() {
        return activeNode;
    }

    /**
     * @param handleIdentifier
     */
    public void remove(IMylarElement node) {
        landmarks.remove(node.getHandleIdentifier()); 
        nodes.remove(node.getHandleIdentifier());
    }

    public synchronized List<IMylarElement> getAllElements() {
        return new ArrayList<IMylarElement>(nodes.values());
    }
    
    public String getId() {
        return id;
    }
    
    @Override
    public String toString() {
        return id;
    }
    
    public void reset() {
        interactionHistory.clear();
        nodes.clear(); 
        landmarks.clear(); 
    }
    
    public int getUserEventCount() {
        return numUserEvents;
    }

    /**
     * TODO: make unmodifiable?
     */
    public List<InteractionEvent> getInteractionHistory() {
        return interactionHistory;
    }

	public void collapse() {
		List<InteractionEvent> collapsedHistory = new ArrayList<InteractionEvent>();
		for (MylarContextElement node : nodes.values()) {
			if (!node.equals(activeNode)) {
				collapseNode(collapsedHistory, node);
			}
		}
		collapseNode(collapsedHistory, activeNode);
		interactionHistory.clear(); 
		interactionHistory.addAll(collapsedHistory);
	}

	private void collapseNode(List<InteractionEvent> collapsedHistory, MylarContextElement node) {
		if (node != null) {
			collapsedHistory.addAll(((DegreeOfInterest)node.getDegreeOfInterest()).getCollapsedEvents());
		}
	}
}
