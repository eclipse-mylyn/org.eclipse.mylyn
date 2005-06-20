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
package org.eclipse.mylar.core.model.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.ITaskscape;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.core.model.InteractionEvent;
import org.eclipse.mylar.dt.MylarInterest;


/**
 * @author Mik Kersten
 */
public class Taskscape implements ITaskscape, Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private List<InteractionEvent> interactionHistory = new ArrayList<InteractionEvent>();
 
    protected transient Map<String, TaskscapeNode> nodes = new HashMap<String, TaskscapeNode>();
    protected transient ITaskscapeNode activeNode = null;
    protected transient List tempRaised = new ArrayList();
    protected transient List<ITaskscapeNode> landmarks;
    protected transient ScalingFactors scaling;
    private transient InteractionEvent lastEdgeEvent = null;
    private transient TaskscapeNode lastEdgeNode = null;
    
    public Taskscape() { 
    	// only needed for serialization
    }
    
    void parseInteractionHistory() {
        nodes = new HashMap<String, TaskscapeNode>();
        landmarks = new ArrayList<ITaskscapeNode>();
        for (InteractionEvent event : interactionHistory) parseInteractionEvent(event);
        updateLandmarks();
        activeNode = lastEdgeNode;
    }

    public Taskscape(String id, ScalingFactors scaling) { 
        this.id = id;
        this.scaling = scaling;
        parseInteractionHistory();
    }

    public ITaskscapeNode parseEvent(InteractionEvent event) {
        interactionHistory.add(event);
        return parseInteractionEvent(event);
    }

    @MylarInterest(level=MylarInterest.Level.LANDMARK)
    private ITaskscapeNode parseInteractionEvent(InteractionEvent event) {
        TaskscapeNode node = nodes.get(event.getStructureHandle());
        if (node == null) {
            node = new TaskscapeNode(event.getStructureKind(), event.getStructureHandle(), this);
            nodes.put(event.getStructureHandle(), node);
        }
        if (event.getNavigation() != null && !event.getNavigation().equals("null") && lastEdgeEvent != null && lastEdgeNode != null
            && event.getKind() != InteractionEvent.Kind.PROPAGATION) {
            ITaskscapeNode navigationSource = nodes.get(lastEdgeEvent.getStructureHandle());
            if (navigationSource != null) {
               TaskscapeEdge edge = lastEdgeNode.getEdge(event.getStructureHandle());
               if (edge == null) {
                    edge = new TaskscapeEdge(event.getStructureKind(), event.getNavigation(), lastEdgeNode, node, this);
                    lastEdgeNode.addEdge(edge);
                }
                DegreeOfInterest doi = (DegreeOfInterest)edge.getDegreeOfInterest();
                doi.addEvent(event); 
            }
        } 
        DegreeOfInterest doi = (DegreeOfInterest)node.getDegreeOfInterest();
        doi.addEvent(event); 
        if (doi.isLandmark()) {
            landmarks.add(node);
        } else {
            landmarks.remove(node);
        }
        if (event.getKind().isUserEvent()) {
            lastEdgeEvent = event;
            lastEdgeNode = node;
        } 
        return node;        
    }

    private void updateLandmarks() {
        landmarks = new ArrayList<ITaskscapeNode>();
        for (TaskscapeNode node : nodes.values()) {
            if (node.getDegreeOfInterest().isLandmark()) landmarks.add(node);
        }
    }
    
    public ITaskscapeNode get(String elementHandle) {
        return nodes.get(elementHandle);
    }
    
    public List<ITaskscapeNode> getInteresting() {
        List<ITaskscapeNode> elements = new ArrayList<ITaskscapeNode>();
        for (String key : nodes.keySet()) {
            TaskscapeNode info = nodes.get(key);
            if (info.getDegreeOfInterest().isInteresting()) {
                elements.add(info);  
            }
        }
        return elements;        
    }

    public List<ITaskscapeNode> getLandmarks() {
        return Collections.unmodifiableList(landmarks);
    }

    /**
     * TODO: should this really call out to the plugin?
     */
    public Set<ITaskscapeNode> getInterestingResources() {
        Set<ITaskscapeNode> interestingFiles = new HashSet<ITaskscapeNode>();
        List<ITaskscapeNode> allIntersting = getInteresting();
        for (ITaskscapeNode node : allIntersting) {
            if (MylarPlugin.getDefault().getStructureBridge(node.getStructureKind()).isDocument(node.getElementHandle())) {       
                interestingFiles.add(node);
            }
        }
        return interestingFiles;
    }

    public void setActiveElement(ITaskscapeNode activeNode) {
        this.activeNode = activeNode;
    }

    public ITaskscapeNode getActiveNode() {
        return activeNode;
    }

    /**
     * @param handleIdentifier
     */
    public void remove(ITaskscapeNode node) {
        landmarks.remove(node); 
        nodes.remove(node.getElementHandle());
    }

    public synchronized List<ITaskscapeNode> getAllElements() {
        return new ArrayList<ITaskscapeNode>(nodes.values());
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
    
    public int getEventCount() {
        return interactionHistory.size();
    }

    /**
     * TODO: make unmodifiable?
     */
    public List<InteractionEvent> getInteractionHistory() {
        return interactionHistory;
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
