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

package org.eclipse.mylyn.context.tests;

import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
public class InteractionEventTest extends AbstractContextTest {

	public void testCopy() throws InterruptedException {
		InteractionEvent original = mockSelection();
		Thread.sleep(1000);
		InteractionEvent copy = InteractionEvent.makeCopy(original, original.getInterestContribution());
		assertEquals(original, copy);
	}
}
