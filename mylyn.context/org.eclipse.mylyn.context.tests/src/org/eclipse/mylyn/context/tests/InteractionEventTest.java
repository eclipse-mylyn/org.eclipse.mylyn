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

import org.eclipse.mylyn.context.sdk.util.AbstractContextTest;
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
