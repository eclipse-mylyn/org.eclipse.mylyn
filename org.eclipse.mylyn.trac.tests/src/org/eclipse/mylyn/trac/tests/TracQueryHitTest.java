/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.trac.tests;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.trac.ui.TracQueryHit;
import org.eclipse.mylar.internal.trac.ui.TracTask;

/**
 * @author Steffen Pingel
 */
public class TracQueryHitTest extends TestCase {

	public void testAttributes() {
		TracQueryHit hit = new TracQueryHit("url", "description", "123");
		hit.setPriority("P1");
		hit.setCompleted(true);
		
		TracTask task = (TracTask) hit.getOrCreateCorrespondingTask();
		assertEquals("url-123", task.getHandleIdentifier());
		assertEquals("P1", task.getPriority());
		assertEquals(true, task.isCompleted());
	}
	
}
