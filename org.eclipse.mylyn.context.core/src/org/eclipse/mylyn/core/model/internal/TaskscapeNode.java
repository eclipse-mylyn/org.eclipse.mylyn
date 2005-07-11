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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylar.core.model.IDegreeOfInterest;
import org.eclipse.mylar.core.model.ITaskscapeNode;


/**
 * Can only have one edge from a node to a particular target.
 * 
 * @author Mik Kersten
 */
public class TaskscapeNode implements ITaskscapeNode {
    
    private String handle;
    private String kind;
    private DegreeOfInterest interest;
    private Taskscape taskscape;
    
    private Map<String/*target handle*/, TaskscapeEdge> edges = new HashMap<String, TaskscapeEdge>();
    
    public TaskscapeNode(String kind, String elementHandle, Taskscape taskscape) {
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

    public Collection<TaskscapeEdge> getEdges() {
        return edges.values();
    }

    public TaskscapeEdge getEdge(String targetHandle) {
        return edges.get(targetHandle);
    }
    
    public void addEdge(TaskscapeEdge edge) {
        edges.put(edge.getTarget().getElementHandle(), edge);
    }
    
    @Override
    public boolean equals(Object obj) { 
        if (obj == null) return false;
        if (this.getElementHandle() == null) return false;
        if (obj instanceof TaskscapeNode) {
            TaskscapeNode node = (TaskscapeNode)obj;
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

    public Taskscape getTaskscape() {
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