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

    private static final String MOCK_PROVIDER = "<mock-provider>";
	private static final String MOCK_ORIGIN = "<mock-origin>";
	private static final String MOCK_KIND = "java";

	protected InteractionEvent mockSelection(String handle) {
        return new InteractionEvent(InteractionEvent.Kind.SELECTION, MOCK_KIND, handle, MOCK_ORIGIN);
    }
    
    protected InteractionEvent mockSelection() {
        return mockSelection("<mock-handle>");
    }
    
    protected InteractionEvent mockNavigation(String toHandle) {
        return new InteractionEvent(InteractionEvent.Kind.SELECTION, "java", toHandle, MOCK_ORIGIN, MOCK_PROVIDER);
    }

    protected InteractionEvent mockInterestContribution(String handle, String kind, float value) {
        InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.MANIPULATION, kind, handle, MOCK_ORIGIN, value);
        return event;
    }
    
    protected InteractionEvent mockInterestContribution(String handle, float value) {
    	return mockInterestContribution(handle, MOCK_KIND, value);
    }

    protected InteractionEvent mockPreferenceChange(String handle) {
        return new InteractionEvent(InteractionEvent.Kind.PREFERENCE, MOCK_KIND, handle, MOCK_ORIGIN);
    }
    
    protected boolean compareTaskscapeEquality(IMylarContext t1, IMylarContext t2) {
        return false;
    }
    
}
