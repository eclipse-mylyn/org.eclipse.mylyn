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

import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.context.core.IMylarRelation;
import org.eclipse.mylar.internal.context.core.MylarContext;
import org.eclipse.mylar.internal.context.core.ScalingFactors;

/**
 * @author Mik Kersten
 */
public class ContextTest extends AbstractContextTest {

	private MylarContext context;

	private ScalingFactors scaling;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		scaling = new ScalingFactors();
		context = new MylarContext("0", scaling);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEquality() {
		MylarContext context1 = new MylarContext("1", scaling);
		context1.parseEvent(mockSelection("1"));
		MylarContext context2 = new MylarContext("2", scaling);
		context2.parseEvent(mockSelection("2"));
		assertFalse(context1.equals(context2));
	}

	public void testReset() {
		context.parseEvent(mockSelection());
		context.reset();

		assertNull(context.getActiveNode());

	}

	public void testManipulation() {
		IMylarElement node = context.parseEvent(mockSelection("1"));
		context.parseEvent(mockSelection("1"));
		context.parseEvent(mockInterestContribution("1", 40));
		assertEquals(42 - (scaling.getDecay().getValue() * 1), node.getInterest().getValue());

		context.parseEvent(mockInterestContribution("1", -20));
		assertEquals(22 - (scaling.getDecay().getValue() * 1), node.getInterest().getValue());
	}

	public void testPropagatedInterest() {
		IMylarElement node = context.parseEvent(mockPropagation("1"));
		assertTrue(node.getInterest().isPropagated());
		context.parseEvent(mockSelection("1"));
		context.parseEvent(mockInterestContribution("1", -10));
		assertFalse(node.getInterest().isPropagated());
		// context.parseEvent(mockInterestContribution("1", 40));
		// assertEquals(42-(scaling.getDecay().getValue()*1),
		// node.getDegreeOfInterest().getValue());
	}

	public void testEdges() {
		IMylarElement node = context.parseEvent(mockSelection("1"));
		context.parseEvent(mockNavigation("2"));
		IMylarRelation edge = node.getRelation("2");
		assertNotNull(edge);
		assertEquals(edge.getTarget().getHandleIdentifier(), "2");
	}

	public void testDecay() {
		float decay = scaling.getDecay().getValue();
		IMylarElement node1 = context.parseEvent(mockSelection("1"));

		context.parseEvent(mockSelection("2"));
		for (int i = 0; i < 98; i++)
			context.parseEvent(mockSelection("1"));
		assertEquals(99 - (decay * 99), node1.getInterest().getValue());
	}

	public void testLandmarkScaling() {
		IMylarElement node1 = context.parseEvent(mockSelection("1"));
		for (int i = 0; i < scaling.getLandmark() - 2 + (scaling.getLandmark() * scaling.getDecay().getValue()); i++) {
			context.parseEvent(mockSelection("1"));
		}
		assertTrue(node1.getInterest().isInteresting());
		assertFalse(node1.getInterest().isLandmark());
		context.parseEvent(mockSelection("1"));
		context.parseEvent(mockSelection("1"));
		assertTrue(node1.getInterest().isLandmark());
	}

	public void testSelections() {
		IMylarElement missing = context.get("0");
		assertNull(missing);

		IMylarElement node = context.parseEvent(mockSelection());
		assertTrue(node.getInterest().isInteresting());
		context.parseEvent(mockSelection());
		assertTrue(node.getInterest().isInteresting());
		context.parseEvent(mockSelection());

		float doi = node.getInterest().getEncodedValue();
		assertEquals(3.0f - (2 * scaling.getDecay().getValue()), doi);
	}
}
