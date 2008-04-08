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
import org.eclipse.mylyn.internal.trac.core.model.TracPriority;

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

	public void testGetTaskPriority() {
		assertEquals("P1", TracTask.getTaskPriority("blocker").toString());
		assertEquals("P2", TracTask.getTaskPriority("critical").toString());
		assertEquals("P3", TracTask.getTaskPriority("major").toString());
		assertEquals("P3", TracTask.getTaskPriority(null).toString());
		assertEquals("P3", TracTask.getTaskPriority("").toString());
		assertEquals("P3", TracTask.getTaskPriority("foo bar").toString());
		assertEquals("P4", TracTask.getTaskPriority("minor").toString());
		assertEquals("P5", TracTask.getTaskPriority("trivial").toString());
	}

	public void testGetTaskPriorityFromTracPriorities() {
		TracPriority p1 = new TracPriority("a", 1);
		TracPriority p2 = new TracPriority("b", 2);
		TracPriority p3 = new TracPriority("c", 3);
		TracPriority[] priorities = new TracPriority[] { p1, p2, p3 };
		assertEquals("P1", TracTask.getTaskPriority("a", priorities).toString());
		assertEquals("P3", TracTask.getTaskPriority("b", priorities).toString());
		assertEquals("P5", TracTask.getTaskPriority("c", priorities).toString());
		assertEquals("P3", TracTask.getTaskPriority("foo", priorities).toString());
		assertEquals("P3", TracTask.getTaskPriority(null, priorities).toString());

		p1 = new TracPriority("a", 10);
		priorities = new TracPriority[] { p1 };
		assertEquals("P1", TracTask.getTaskPriority("a", priorities).toString());
		assertEquals("P3", TracTask.getTaskPriority("b", priorities).toString());
		assertEquals("P3", TracTask.getTaskPriority(null, priorities).toString());

		p1 = new TracPriority("1", 10);
		p2 = new TracPriority("2", 20);
		p3 = new TracPriority("3", 30);
		TracPriority p4 = new TracPriority("4", 40);
		TracPriority p5 = new TracPriority("5", 70);
		TracPriority p6 = new TracPriority("6", 100);
		priorities = new TracPriority[] { p1, p2, p3, p4, p5, p6 };
		assertEquals("P1", TracTask.getTaskPriority("1", priorities).toString());
		assertEquals("P1", TracTask.getTaskPriority("2", priorities).toString());
		assertEquals("P2", TracTask.getTaskPriority("3", priorities).toString());
		assertEquals("P2", TracTask.getTaskPriority("4", priorities).toString());
		assertEquals("P4", TracTask.getTaskPriority("5", priorities).toString());
		assertEquals("P5", TracTask.getTaskPriority("6", priorities).toString());
	}

}
