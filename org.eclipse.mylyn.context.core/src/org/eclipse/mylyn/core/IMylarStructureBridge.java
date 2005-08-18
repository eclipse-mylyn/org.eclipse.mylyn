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
 * Created on Apr 7, 2005
  */
package org.eclipse.mylar.core;

import java.util.List;

import org.eclipse.core.resources.IProject;

/**
 * @author Mik Kersten
 */
public interface IMylarStructureBridge {

    public abstract String getResourceExtension();
    
    public abstract String getHandleIdentifier(Object object);
    
    public abstract Object getObjectForHandle(String handle);
    
    public abstract String getParentHandle(String handle);
    
    /**
     * @return 	The name or a null String("").  Can't be null since the views
     * 			displaying the context can't handle null names
     */
    public abstract String getName(Object object);
    
    public abstract boolean canBeLandmark(Object element);

    public abstract boolean acceptsObject(Object object);

    /**
     * @return false for objects that can not be filtered
     */
    public abstract boolean canFilter(Object element);

    /**
     * @return  true if this is a resource that can be opened by an editor (i.e. false
     * for a directory, or a Java method)
     */
    public abstract boolean isDocument(String handle);
    
    /**
     * @param resource	can be anything that has an element accessible
     * 					via an offset, e.g. a file with a character offset
     */
    public abstract String getHandleForOffsetInObject(Object resource, int offset);
    
    /**
     * TODO: get rid of this method.
     */
//    public abstract String getHandleForMarker(ProblemMarker marker);
    
    /**
     * TODO Should this really be here??
     * 
     * @return The IProject that contains the object, or null if there is no project
     */
    public abstract IProject getProjectForObject(Object object);

    /**
     * Used for switching kinds based on parent handles
     */
    public abstract String getResourceExtension(String elementHandle);
 
    public abstract List<AbstractRelationshipProvider> getProviders();
    
    public abstract List<IDegreeOfSeparation> getDegreesOfSeparation();
}
