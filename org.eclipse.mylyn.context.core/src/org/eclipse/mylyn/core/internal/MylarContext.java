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
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.dt.MylarInterest;


/**
 * @author Mik Kersten
 */
public class MylarContext implements IMylarContext, Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private List<InteractionEvent> interactionHistory = new ArrayList<InteractionEvent>();
 
    protected transient Map<String, MylarContextNode> nodes = new HashMap<String, MylarContextNode>();
    protected transient IMylarContextNode activeNode = null;
    protected transient List tempRaised = new ArrayList();
    protected transient Map<String, IMylarContextNode> landmarks;
    protected transient ScalingFactors scaling;
    private transient InteractionEvent lastEdgeEvent = null;
    private transient MylarContextNode lastEdgeNode = null;
    private transient int numUserEvents = 0;
    
    public MylarContext() { 
    	// only needed for serialization
    }
    
    void parseInteractionHistory() {
        nodes = new HashMap<String, MylarContextNode>();
        landmarks = new HashMap<String, IMylarContextNode>();
        for (InteractionEvent event : interactionHistory) parseInteractionEvent(event);
        updateLandmarks();
        activeNode = lastEdgeNode;
    }

    public MylarContext(String id, ScalingFactors scaling) { 
        this.id = id;
        this.scaling = scaling;
        parseInteractionHistory();
    }

    public IMylarContextNode parseEvent(InteractionEvent event) {
        interactionHistory.add(event);
        return parseInteractionEvent(event);
    }

    @MylarInterest(level=MylarInterest.Level.LANDMARK)
    private IMylarContextNode parseInteractionEvent(InteractionEvent event) {
    	if (event.getKind().isUserEvent()) numUserEvents++;
        MylarContextNode node = nodes.get(event.getStructureHandle());
        if (node == null) {
            node = new MylarContextNode(event.getStructureKind(), event.getStructureHandle(), this);
            nodes.put(event.getStructureHandle(), node);
        }
        if (event.getNavigation() != null && !event.getNavigation().equals("null") && lastEdgeEvent != null && lastEdgeNode != null
            && event.getKind() != InteractionEvent.Kind.PROPAGATION) {
            IMylarContextNode navigationSource = nodes.get(lastEdgeEvent.getStructureHandle());
            if (navigationSource != null) {
               MylarContextEdge edge = lastEdgeNode.getEdge(event.getStructureHandle());
               if (edge == null) {
                    edge = new MylarContextEdge(event.getStructureKind(), event.getNavigation(), lastEdgeNode, node, this);
                    lastEdgeNode.addEdge(edge);
                }
                DegreeOfInterest doi = (DegreeOfInterest)edge.getDegreeOfInterest();
                doi.addEvent(event); 
            }
        } 
        DegreeOfInterest doi = (DegreeOfInterest)node.getDegreeOfInterest();
        doi.addEvent(event); 
        if (doi.isLandmark()) {
        	landmarks.put(node.getElementHandle(), node);
        } else {
            landmarks.remove(node.getElementHandle()); // TODO: redundant
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
        for (MylarContextNode node : nodes.values()) {
            if (node.getDegreeOfInterest().isLandmark()) landmarks.put(node.getElementHandle(), node);
        }
    }
    
    public IMylarContextNode get(String elementHandle) {
        return nodes.get(elementHandle);
    }
    
    public List<IMylarContextNode> getInteresting() {
        List<IMylarContextNode> elements = new ArrayList<IMylarContextNode>();
        for (String key : nodes.keySet()) {
            MylarContextNode info = nodes.get(key);
            if (info.getDegreeOfInterest().isInteresting()) {
                elements.add(info);  
            }
        }
        return elements;        
    }

    public List<IMylarContextNode> getLandmarks() {
        return Collections.unmodifiableList(new ArrayList<IMylarContextNode>(landmarks.values()));
    }

    public IMylarContextNode getActiveNode() {
        return activeNode;
    }

    /**
     * @param handleIdentifier
     */
    public void remove(IMylarContextNode node) {
        landmarks.remove(node.getElementHandle()); 
        nodes.remove(node.getElementHandle());
    }

    public synchronized List<IMylarContextNode> getAllElements() {
        return new ArrayList<IMylarContextNode>(nodes.values());
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
		for (MylarContextNode node : nodes.values()) {
			interactionHistory.add(0, new InteractionEvent(
	                InteractionEvent.Kind.MANIPULATION, 
	                node.getStructureKind(),
	                node.getElementHandle(), 
	                MylarContextManager.SOURCE_ID_DECAY,
	                -node.getDegreeOfInterest().getDecayValue()));
		}
	}
}

//private void writeObject(ObjectOutputStream stream) throws IOException {
//stream.defaultWriteObject();
//stream.writeInt(id);
//stream.writeObject(interactionHistory);
//}
//
//@SuppressWarnings(value="unchecked")
//private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
//stream.defaultReadObject();
//id = stream.readInt();
//interactionHistory = (List<InteractionEvent>)stream.readObject();
//parseInteractionHistory();
//}

//    public List<TaskscapeEdge> getRelatedElements(String handle) {
//        return relationshipMap.get(handle);
//    }
//
//    public void addRelatedElements(TaskscapeEdge relationship) {
//        List<TaskscapeEdge> relationships = relationshipMap.get(relationship.getSource());
//        if (relationships == null) {
//            relationships = new ArrayList<TaskscapeEdge>();
//        }
//        relationships.add(relationship);
//        relationshipMap.put(relationship.getSource(), relationships);
//    } 
//
//    public void removeRelatedElements(String handle) {
//        relationshipMap.remove(handle);
//    }
