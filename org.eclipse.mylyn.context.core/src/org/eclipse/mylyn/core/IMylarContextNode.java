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

package org.eclipse.mylar.core;

import java.util.Collection;

import org.eclipse.mylar.core.internal.MylarContextEdge;


/**
 * @author Mik Kersten
 */
public interface IMylarContextNode {
   
    public abstract IDegreeOfInterest getDegreeOfInterest();
    
    public abstract String getElementHandle();
    
    public abstract void setElementHandle(String elementHandle);
    
    public abstract String getStructureKind();
   
    public abstract IMylarContext getContext();
    
    public abstract Collection<MylarContextEdge> getEdges();
        
    public abstract MylarContextEdge getEdge(String targetHandle);

    public abstract void clearEdges();
}