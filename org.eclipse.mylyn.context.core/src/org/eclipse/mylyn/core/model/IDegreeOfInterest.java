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
 * Created on Feb 2, 2005
 */
package org.eclipse.mylar.core.model;

import java.util.List;


/**
 * @author Mik Kersten
 */
public interface IDegreeOfInterest {

//    public abstract void addEvent(InteractionEvent event);
        
    public abstract boolean isPredicted();
    
    public abstract boolean isLandmark();
    
    public abstract boolean isInteresting();

    public abstract float getEncodedValue();
    
    public float getDecayValue();

    public abstract float getValue();

    public abstract List<InteractionEvent> getEvents();
}

