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
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;


/**
 * @author Mik Kersten
 */
public class CompositeContextNode implements IMylarElement {
    private Set<MylarContextNode> nodes = null;//new HashSet<ITaskscapeNode>();
    
    private String handle = "<no handle>";
//    private String name = "";
    
    public CompositeContextNode(String handle, Set<MylarContextNode> nodes) {
        assert(handle != null);
        this.nodes = nodes;
        this.handle = handle;
    }

    /**
     * @return the taskscape with the hightest value
     * TODO: is this always best?
     */
    public IMylarContext getContext() {
        IMylarElement highestValueNode = null;
        for (IMylarElement node : nodes) {
            if (highestValueNode == null || node.getDegreeOfInterest().getValue() < highestValueNode.getDegreeOfInterest().getValue()) highestValueNode = node;
        }
        if (highestValueNode != null) {
            return highestValueNode.getContext();
        } else {
            return null;
        }
    }
    
    public IDegreeOfInterest getDegreeOfInterest() {
        CompositeDegreeOfInterest degreeOfInterest = new CompositeDegreeOfInterest();
        for (IMylarElement node : nodes) {
            degreeOfInterest.getInfos().add(node.getDegreeOfInterest());
        }
        return degreeOfInterest;
    }

    public String getHandleIdentifier() {
        return handle;
    }

    public void setHandleIdentifier(String elementHandle) {
    	// can't set a handle on this
    }

    public Set<MylarContextNode> getNodes() {
        return nodes;
    }
    
    /**
     * @return null if all kinds aren't equal
     */
    public String getContentType() {
        Set<String> kinds = new HashSet<String>();
        String lastKind = null;
        for (IMylarElement node : nodes) {
            lastKind = node.getContentType();
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
    public MylarContextEdge getEdge(String targetHandle) {
        Set<MylarContextEdge> edges = new HashSet<MylarContextEdge>();
        for (IMylarElement node : nodes) edges.add(node.getEdge(targetHandle));
        if (edges.size() == 0) {
            return null;
        } else if (edges.size() > 1) {
            MylarPlugin.log("Multiple edges found in composite, not supported", this);
        }
        return edges.iterator().next();
    }
    
    public Collection<MylarContextEdge> getEdges() {
        Set<MylarContextEdge> edges = new HashSet<MylarContextEdge>();
        
        for (MylarContextNode node : nodes) edges.addAll(node.getEdges());
        return edges;
    }
    
    public void clearEdges() {
    	for (MylarContextNode node : nodes) node.clearEdges();
	}
    
    @Override
    public boolean equals(Object obj) { 
        if (obj == null) return false;
        if (obj instanceof CompositeContextNode) {
            CompositeContextNode node = (CompositeContextNode)obj;
            return this.getHandleIdentifier().equals(node.getHandleIdentifier());
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
