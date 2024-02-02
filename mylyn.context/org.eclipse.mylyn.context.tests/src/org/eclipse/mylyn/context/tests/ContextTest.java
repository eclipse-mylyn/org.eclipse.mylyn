/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.tests;

import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionRelation;
import org.eclipse.mylyn.context.sdk.util.AbstractContextTest;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;

/**
 * @author Mik Kersten
 */
public class ContextTest extends AbstractContextTest {

	private InteractionContext context;

	private InteractionContextScaling scaling;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		scaling = new InteractionContextScaling();
		context = new InteractionContext("0", scaling);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEquality() {
		InteractionContext context1 = new InteractionContext("1", scaling);
		context1.parseEvent(mockSelection("1"));
		InteractionContext context2 = new InteractionContext("2", scaling);
		context2.parseEvent(mockSelection("2"));
		assertFalse(context1.equals(context2));
	}

	public void testReset() {
		context.parseEvent(mockSelection());
		context.reset();

		assertNull(context.getActiveNode());

	}

	public void testManipulation() {
		IInteractionElement node = context.parseEvent(mockSelection("1"));
		context.parseEvent(mockSelection("1"));
		context.parseEvent(mockInterestContribution("1", 40));
		assertEquals(42 - scaling.getDecay() * 1, node.getInterest().getValue());

		context.parseEvent(mockInterestContribution("1", -20));
		assertEquals(22 - scaling.getDecay() * 1, node.getInterest().getValue());
	}

	public void testPropagatedInterest() {
		IInteractionElement node = context.parseEvent(mockPropagation("1"));
		assertTrue(node.getInterest().isPropagated());
		context.parseEvent(mockSelection("1"));
		context.parseEvent(mockInterestContribution("1", -10));
		assertFalse(node.getInterest().isPropagated());
		// context.parseEvent(mockInterestContribution("1", 40));
		// assertEquals(42-(scaling.getDecay().getValue()*1),
		// node.getDegreeOfInterest().getValue());
	}

	public void testEdges() {
		IInteractionElement node = context.parseEvent(mockSelection("1"));
		context.parseEvent(mockNavigation("2"));
		IInteractionRelation edge = node.getRelation("2");
		assertNotNull(edge);
		assertEquals(edge.getTarget().getHandleIdentifier(), "2");
	}

	public void testDecay() {
		float decay = scaling.getDecay();
		IInteractionElement node1 = context.parseEvent(mockSelection("1"));

		context.parseEvent(mockSelection("2"));
		for (int i = 0; i < 98; i++) {
			context.parseEvent(mockSelection("1"));
		}
		assertEquals(99 - decay * 99, node1.getInterest().getValue());
	}

	public void testLandmarkScaling() {
		IInteractionElement node1 = context.parseEvent(mockSelection("1"));
		for (int i = 0; i < scaling.getLandmark() - 2 + scaling.getLandmark() * scaling.getDecay(); i++) {
			context.parseEvent(mockSelection("1"));
		}
		assertTrue(node1.getInterest().isInteresting());
		assertFalse(node1.getInterest().isLandmark());
		context.parseEvent(mockSelection("1"));
		context.parseEvent(mockSelection("1"));
		assertTrue(node1.getInterest().isLandmark());
	}

	public void testSelections() {
		IInteractionElement missing = context.get("0");
		assertNull(missing);

		IInteractionElement node = context.parseEvent(mockSelection());
		assertTrue(node.getInterest().isInteresting());
		context.parseEvent(mockSelection());
		assertTrue(node.getInterest().isInteresting());
		context.parseEvent(mockSelection());

		float doi = node.getInterest().getEncodedValue();
		assertEquals(3.0f - 2 * scaling.getDecay(), doi);
	}
}
