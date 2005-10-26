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
import org.eclipse.mylar.core.IMylarRelation;
import org.eclipse.mylar.core.IMylarElement;


/**
 * Can only have one edge from a node to a particular target.
 * 
 * @author Mik Kersten
 */
public class MylarContextElement implements IMylarElement {
    
    private String handle;
    private String kind;
    private DegreeOfInterest interest;
    private MylarContext context;
    
    private Map<String/*target handle*/, MylarContextRelation> edges = new HashMap<String, MylarContextRelation>();
    
    public MylarContextElement(String kind, String elementHandle, MylarContext context) {
        if (elementHandle == null) throw new RuntimeException("malformed context: null handle");
        interest = new DegreeOfInterest(context);
        this.handle = elementHandle;
        this.kind = kind;
        this.context = context;
    }
       
    public String getHandleIdentifier() {
        return handle;
    }
    
    public void setHandleIdentifier(String handle) {
        this.handle = handle;
    }
    
    public String getContentType() {
        return kind;
    }
    
    public void setKind(String kind) {
        this.kind = kind;
    }

    public Collection<MylarContextRelation> getRelations() {
        return edges.values();
    }

    public MylarContextRelation getEdge(String targetHandle) {
        return edges.get(targetHandle);
    }
    
    /**
     * TODO: reduce visibility
     */
    public void addEdge(MylarContextRelation edge) {
        edges.put(edge.getTarget().getHandleIdentifier(), edge);
    }
    
    public void clearEdges() {
		edges.clear();
	}
    
    void removeEdge(IMylarRelation edge) {
    	edges.remove(edge.getTarget().getHandleIdentifier());
    }
    
    @Override
    public boolean equals(Object obj) { 
        if (obj == null) return false;
        if (this.getHandleIdentifier() == null) return false;
        if (obj instanceof MylarContextElement) {
            MylarContextElement node = (MylarContextElement)obj;
            return this.getHandleIdentifier().equals(node.getHandleIdentifier());
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

    public IDegreeOfInterest getInterest() {
        return interest;
    }

    public MylarContext getContext() {
        return context;
    }
    
    @Override
    public String toString() {
        return handle;
    }

}