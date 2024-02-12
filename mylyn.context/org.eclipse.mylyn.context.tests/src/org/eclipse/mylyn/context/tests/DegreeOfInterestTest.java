/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.context.tests;

import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.core.DegreeOfInterest;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

import junit.framework.TestCase;

/**
 * @author Mik Kersten
 */
@SuppressWarnings("nls")
public class DegreeOfInterestTest extends TestCase {

	private final InteractionContext mockContext = new InteractionContext("doitest", new InteractionContextScaling());

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testPredictedInterest() {
		DegreeOfInterest doi = new DegreeOfInterest(mockContext, ContextCore.getCommonContextScaling());
		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.PREDICTION, "kind", "handle", "source-id",
				"id", null, 1);
		doi.addEvent(event);

		assertTrue(doi.isInteresting());
		assertFalse(doi.isLandmark());
		assertFalse(doi.isPropagated());
		assertTrue(doi.isPredicted());
	}

	public void testPredictedInterestWithPropagated() {
		DegreeOfInterest doi = new DegreeOfInterest(mockContext, ContextCore.getCommonContextScaling());
		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.SELECTION, "kind", "handle", "source-id",
				"id", null, 20);
		doi.addEvent(event);

		InteractionEvent event2 = new InteractionEvent(InteractionEvent.Kind.PREDICTION, "kind", "handle", "source-id",
				"id", null, 2);
		doi.addEvent(event2);

		InteractionEvent event3 = new InteractionEvent(InteractionEvent.Kind.PROPAGATION, "kind", "handle", "source-id",
				"id", null, 750);
		doi.addEvent(event3);

		InteractionEvent event4 = new InteractionEvent(InteractionEvent.Kind.MANIPULATION, "kind", "handle",
				"source-id", "id", null, -684);
		doi.addEvent(event4);

		assertTrue(doi.isInteresting());
		assertTrue(doi.isLandmark());
		assertFalse(doi.isPropagated());
		assertFalse(doi.isPredicted());
	}

	public void testPropagatedInterest() {
		DegreeOfInterest doi = new DegreeOfInterest(mockContext, ContextCore.getCommonContextScaling());
		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.PROPAGATION, "kind", "handle", "source-id",
				"id", null, 1);
		doi.addEvent(event);

		assertTrue(doi.isInteresting());
		assertFalse(doi.isLandmark());
		assertTrue(doi.isPropagated());
		assertFalse(doi.isPredicted());
	}

	public void testCreation() {
		DegreeOfInterest doi = new DegreeOfInterest(mockContext, ContextCore.getCommonContextScaling());
		assertFalse(doi.isInteresting());
		assertFalse(doi.isLandmark());
		assertFalse(doi.isPropagated());
		assertFalse(doi.isPredicted());
	}
}
