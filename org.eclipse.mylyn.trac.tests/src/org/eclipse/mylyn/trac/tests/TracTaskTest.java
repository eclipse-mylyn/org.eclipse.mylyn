/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

	public void testGetMylynPriority() {
		assertEquals("P1", TracTask.getMylynPriority("blocker"));
		assertEquals("P2", TracTask.getMylynPriority("critical"));
		assertEquals("P3", TracTask.getMylynPriority("major"));
		assertEquals("P3", TracTask.getMylynPriority(null));
		assertEquals("P3", TracTask.getMylynPriority(""));
		assertEquals("P3", TracTask.getMylynPriority("foo bar"));
		assertEquals("P4", TracTask.getMylynPriority("minor"));
		assertEquals("P5", TracTask.getMylynPriority("trivial"));
	}
}
