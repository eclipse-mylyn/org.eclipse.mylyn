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
 * Created on Jan 26, 2005
  */
package org.eclipse.mylar.core.internal;

import org.eclipse.mylar.core.IDegreeOfInterest;
import org.eclipse.mylar.core.IMylarContextEdge;
import org.eclipse.mylar.core.IMylarContextNode;

/**
 * TODO: make immutable?
 * 
 * @author Mik Kersten
 */
public class MylarContextEdge implements IMylarContextEdge {

    private DegreeOfInterest interest;
    
    private String structureKind;
    private String relationshipHandle;
    private IMylarContextNode source;
    private IMylarContextNode target;
    
    public MylarContextEdge(String kind, String edgeKind, IMylarContextNode source, IMylarContextNode target, MylarContext context) {
        interest = new DegreeOfInterest(context);
        this.structureKind = kind;
        this.relationshipHandle = edgeKind; 
        this.target = target;
        this.source = source;
    }
    
    public IMylarContextNode getTarget() {
        return target;
    }

    public IDegreeOfInterest getDegreeOfInterest() {
        return interest;
    }

    @Override
    public String toString() {
        return "(rel: " + relationshipHandle 
            + ", source: " + source.getElementHandle() 
            + ", target: " + target.getElementHandle() + ")";
    }

    public String getLabel() {
        return toString();
    }

    public String getRelationshipHandle() {
        return relationshipHandle;
    }

    public String getContentType() {
        return structureKind;
    }

    public IMylarContextNode getSource() {
        return source;
    }
}
