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
 * Created on May 26, 2005
  */
package org.eclipse.mylar.core.tests;

import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.InteractionEvent;

import junit.framework.TestCase;

/**
 * @author Mik Kersten
 */
public class AbstractTaskscapeTest extends TestCase {

    protected InteractionEvent mockSelection(String handle) {
        return new InteractionEvent(InteractionEvent.Kind.SELECTION, "<mock-kind>", handle, "<mock-origin>");
    }
    
    protected InteractionEvent mockSelection() {
        return mockSelection("<mock-handle>");
    }
    
    protected InteractionEvent mockNavigation(String toHandle) {
        return new InteractionEvent(InteractionEvent.Kind.SELECTION, "<mock-kind>", toHandle, "<mock-origin>", "<mock-provider>");
    }
        
    protected InteractionEvent mockInterestContribution(String handle, float value) {
        InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.MANIPULATION, "<mock-kind>", handle, "<mock-origin>", value);
        return event;
    }

    protected InteractionEvent mockPreferenceChange(String handle) {
        return new InteractionEvent(InteractionEvent.Kind.PREFERENCE, "<mock-kind>", handle, "<mock-origin>");
    }
    
    protected boolean compareTaskscapeEquality(IMylarContext t1, IMylarContext t2) {
//        List<ITaskscapeNode> nodes1 = t1.get
        return false;
    }
    
}
