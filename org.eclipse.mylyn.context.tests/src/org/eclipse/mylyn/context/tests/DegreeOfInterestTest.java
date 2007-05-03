/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.context.tests;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.context.core.DegreeOfInterest;
import org.eclipse.mylar.internal.context.core.MylarContext;
import org.eclipse.mylar.internal.context.core.ContextManager;
import org.eclipse.mylar.internal.context.core.ScalingFactors;
import org.eclipse.mylar.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
public class DegreeOfInterestTest extends TestCase {

	private MylarContext mockContext = new MylarContext("doitest", new ScalingFactors());

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testPredictedInterest() {
		DegreeOfInterest doi = new DegreeOfInterest(mockContext, ContextManager.getScalingFactors());
		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.PREDICTION, "kind", "handle", "source-id",
				"id", null, 1);
		doi.addEvent(event);

		assertTrue(doi.isInteresting());
		assertFalse(doi.isLandmark());
		assertFalse(doi.isPropagated());
		assertTrue(doi.isPredicted());
	}

	public void testPropagatedInterest() {
		DegreeOfInterest doi = new DegreeOfInterest(mockContext, ContextManager.getScalingFactors());
		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.PROPAGATION, "kind", "handle", "source-id",
				"id", null, 1);
		doi.addEvent(event);

		assertTrue(doi.isInteresting());
		assertFalse(doi.isLandmark());
		assertTrue(doi.isPropagated());
		assertFalse(doi.isPredicted());
	}

	public void testCreation() {
		DegreeOfInterest doi = new DegreeOfInterest(mockContext, ContextManager.getScalingFactors());
		assertFalse(doi.isInteresting());
		assertFalse(doi.isLandmark());
		assertFalse(doi.isPropagated());
		assertFalse(doi.isPredicted());
	}
}
