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

package org.eclipse.mylyn.trac.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.trac.core.TracTask;

/**
 * @author Steffen Pingel
 */
public class TracTaskTest extends TestCase {

	public void testIsCompleted() {
		assertTrue(TracTask.isCompleted("closed"));
		assertFalse(TracTask.isCompleted("Closed"));
		assertFalse(TracTask.isCompleted("new"));
		assertFalse(TracTask.isCompleted("assigned"));
		assertFalse(TracTask.isCompleted("reopened"));
		assertFalse(TracTask.isCompleted("foobar"));
		assertFalse(TracTask.isCompleted(""));
		assertFalse(TracTask.isCompleted(null));
	}

	public void testGetMylarPriority() {
		assertEquals("P1", TracTask.getMylarPriority("blocker"));
		assertEquals("P2", TracTask.getMylarPriority("critical"));
		assertEquals("P3", TracTask.getMylarPriority("major"));
		assertEquals("P3", TracTask.getMylarPriority(null));
		assertEquals("P3", TracTask.getMylarPriority(""));
		assertEquals("P3", TracTask.getMylarPriority("foo bar"));
		assertEquals("P4", TracTask.getMylarPriority("minor"));
		assertEquals("P5", TracTask.getMylarPriority("trivial"));
	}
}
