/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.context.tests;

import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
public class InteractionContextTest extends AbstractContextTest {

	public void testParseEventWithNullHandle() {
		InteractionEvent event = mockSelection(null);
		InteractionContext context = new InteractionContext("test", new InteractionContextScaling());
		assertNull(context.parseEvent(event));
	}

	public void testSetScalingFactors() {
		InteractionContextScaling oldScalingFactors = new InteractionContextScaling();
//		InteractionContextScaling newScalingFactors = new InteractionContextScaling();
//		newScalingFactors.get(InteractionEvent.Kind.EDIT).setValue(10f);
		InteractionContext globalContext = new InteractionContext("global", oldScalingFactors);
		assertEquals(oldScalingFactors, globalContext.getScaling());
//		globalContext.setScaling(newScalingFactors);
		globalContext.getScaling().set(InteractionEvent.Kind.EDIT, 10f);
//		assertEquals(newScalingFactors, globalContext.getScaling());
		assertEquals(10f, globalContext.getScaling().get(InteractionEvent.Kind.EDIT));
	}

	public void testScalingFactorSet() {
		InteractionContextScaling scalingFactors = new InteractionContextScaling();
		scalingFactors.setDecay(0f);
		InteractionContext context = new InteractionContext("test", scalingFactors);
		assertEquals(0f, context.getScaling().getDecay());
	}
}
