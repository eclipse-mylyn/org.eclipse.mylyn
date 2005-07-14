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
 * Created on Dec 29, 2004
  */
package org.eclipse.mylar.core.internal;

import java.util.*;

import org.eclipse.mylar.core.IDegreeOfInterest;
import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.MylarPlugin;


/**
 * @author Mik Kersten
 */
public class CompositeContextNode implements IMylarContextNode {
    private Set<ContextNode> nodes = null;//new HashSet<ITaskscapeNode>();
    
    private String handle = "<no handle>";
//    private String name = "";
    
    public CompositeContextNode(String handle, Set<ContextNode> nodes) {
        assert(handle != null);
        this.nodes = nodes;
        this.handle = handle;
    }

    /**
     * @return the taskscape with the hightest value
     * TODO: is this always best?
     */
    public IMylarContext getTaskscape() {
        IMylarContextNode highestValueNode = null;
        for (IMylarContextNode node : nodes) {
            if (highestValueNode == null || node.getDegreeOfInterest().getValue() < highestValueNode.getDegreeOfInterest().getValue()) highestValueNode = node;
        }
        if (highestValueNode != null) {
            return highestValueNode.getTaskscape();
        } else {
            return null;
        }
    }
    
    public IDegreeOfInterest getDegreeOfInterest() {
        CompositeDegreeOfInterest degreeOfInterest = new CompositeDegreeOfInterest();
        for (IMylarContextNode node : nodes) {
            degreeOfInterest.getInfos().add(node.getDegreeOfInterest());
        }
        return degreeOfInterest;
    }

    public String getElementHandle() {
        return handle;
    }

    public void setElementHandle(String elementHandle) {
    	// can't set a handle on this
    }

    public Set<ContextNode> getNodes() {
        return nodes;
    }
    
    /**
     * @return null if all kinds aren't equal
     */
    public String getStructureKind() {
        Set<String> kinds = new HashSet<String>();
        String lastKind = null;
        for (IMylarContextNode node : nodes) {
            lastKind = node.getStructureKind();
            kinds.add(lastKind);
        }
        if (kinds.size() == 1) {
            return lastKind;
        } else {
            return null;
        }
    }

    /**
     * TODO: need composite edges here
     */
    public ContextEdge getEdge(String targetHandle) {
        Set<ContextEdge> edges = new HashSet<ContextEdge>();
        for (IMylarContextNode node : nodes) edges.add(node.getEdge(targetHandle));
        if (edges.size() == 0) {
            return null;
        } else if (edges.size() > 1) {
            MylarPlugin.log("Multiple edges found in composite, not supported", this);
        }
        return edges.iterator().next();
    }
    
    public Collection<ContextEdge> getEdges() {
        Set<ContextEdge> edges = new HashSet<ContextEdge>();
        
        for (IMylarContextNode node : nodes) edges.addAll(node.getEdges());
        return edges;
    }
    
    @Override
    public boolean equals(Object obj) { 
        if (obj == null) return false;
        if (obj instanceof CompositeContextNode) {
            CompositeContextNode node = (CompositeContextNode)obj;
            return this.getElementHandle().equals(node.getElementHandle());
        }
        return false;
    }
 
    @Override
    public int hashCode() {
        return handle.hashCode();
    }
 
    @Override
    public String toString() {
        return "composite" + nodes;
    }
}
