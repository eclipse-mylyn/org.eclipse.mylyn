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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylar.core.IDegreeOfInterest;
import org.eclipse.mylar.core.IMylarContextNode;


/**
 * Can only have one edge from a node to a particular target.
 * 
 * @author Mik Kersten
 */
public class ContextNode implements IMylarContextNode {
    
    private String handle;
    private String kind;
    private DegreeOfInterest interest;
    private Context taskscape;
    
    private Map<String/*target handle*/, ContextEdge> edges = new HashMap<String, ContextEdge>();
    
    public ContextNode(String kind, String elementHandle, Context taskscape) {
        if (elementHandle == null) throw new RuntimeException("malformed taskscape: null handle");
        interest = new DegreeOfInterest(taskscape);
        this.handle = elementHandle;
        this.kind = kind;
        this.taskscape = taskscape;
    }
       
    public String getElementHandle() {
        return handle;
    }
    public void setElementHandle(String elementHandle) {
        this.handle = elementHandle;
    }
    public String getStructureKind() {
        return kind;
    }
    public void setKind(String kind) {
        this.kind = kind;
    }

    public Collection<ContextEdge> getEdges() {
        return edges.values();
    }

    public ContextEdge getEdge(String targetHandle) {
        return edges.get(targetHandle);
    }
    
    public void addEdge(ContextEdge edge) {
        edges.put(edge.getTarget().getElementHandle(), edge);
    }
    
    @Override
    public boolean equals(Object obj) { 
        if (obj == null) return false;
        if (this.getElementHandle() == null) return false;
        if (obj instanceof ContextNode) {
            ContextNode node = (ContextNode)obj;
            return this.getElementHandle().equals(node.getElementHandle());
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (handle != null) { 
            return handle.hashCode();
        } else {
            return super.hashCode();
        }
    }

    public IDegreeOfInterest getDegreeOfInterest() {
        return interest;
    }

    public Context getTaskscape() {
        return taskscape;
    }
    
    @Override
    public String toString() {
        return handle;
    }

    public void removeEdge(String kindToRemove) {
        throw new RuntimeException("unimplemented");
    }
}