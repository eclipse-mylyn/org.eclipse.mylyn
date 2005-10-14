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

import org.eclipse.mylar.core.internal.MylarContextRelation;

/**
 * @author Mik Kersten
 */
public interface IMylarElement extends IMylarObject {
       
    public abstract String getHandleIdentifier();
    
    public abstract void setHandleIdentifier(String elementHandle);
   
    public abstract IMylarContext getContext();
    
    public abstract Collection<MylarContextRelation> getRelations();
        
    public abstract MylarContextRelation getEdge(String targetHandle);

    public abstract void clearEdges();
}