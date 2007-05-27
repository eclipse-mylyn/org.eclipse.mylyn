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

import org.eclipse.mylar.internal.context.core.InteractionContext;
import org.eclipse.mylar.internal.context.core.ScalingFactors;
import org.eclipse.mylar.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
public class InteractionContextTest extends AbstractContextTest {

	public void testParseEventWithNullHandle() {
		InteractionEvent event = mockSelection(null);
		InteractionContext context = new InteractionContext("test", new ScalingFactors());
		assertNull(context.parseEvent(event));
	}
	
}
