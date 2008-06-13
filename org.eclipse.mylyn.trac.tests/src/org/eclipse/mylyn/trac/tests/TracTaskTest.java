/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.model.TracPriority;

/**
 * @author Steffen Pingel
 */
public class TracTaskTest extends TestCase {

	public void testIsCompleted() {
		assertTrue(TracRepositoryConnector.isCompleted("closed"));
		assertFalse(TracRepositoryConnector.isCompleted("Closed"));
		assertFalse(TracRepositoryConnector.isCompleted("new"));
		assertFalse(TracRepositoryConnector.isCompleted("assigned"));
		assertFalse(TracRepositoryConnector.isCompleted("reopened"));
		assertFalse(TracRepositoryConnector.isCompleted("foobar"));
		assertFalse(TracRepositoryConnector.isCompleted(""));
		assertFalse(TracRepositoryConnector.isCompleted(null));
	}

	public void testGetTaskPriority() {
		assertEquals("P1", TracRepositoryConnector.getTaskPriority("blocker").toString());
		assertEquals("P2", TracRepositoryConnector.getTaskPriority("critical").toString());
		assertEquals("P3", TracRepositoryConnector.getTaskPriority("major").toString());
		assertEquals("P3", TracRepositoryConnector.getTaskPriority(null).toString());
		assertEquals("P3", TracRepositoryConnector.getTaskPriority("").toString());
		assertEquals("P3", TracRepositoryConnector.getTaskPriority("foo bar").toString());
		assertEquals("P4", TracRepositoryConnector.getTaskPriority("minor").toString());
		assertEquals("P5", TracRepositoryConnector.getTaskPriority("trivial").toString());
	}

	public void testGetTaskPriorityFromTracPriorities() {
		TracPriority p1 = new TracPriority("a", 1);
		TracPriority p2 = new TracPriority("b", 2);
		TracPriority p3 = new TracPriority("c", 3);
		TracPriority[] priorities = new TracPriority[] { p1, p2, p3 };
		assertEquals("P1", TracRepositoryConnector.getTaskPriority("a", priorities).toString());
		assertEquals("P3", TracRepositoryConnector.getTaskPriority("b", priorities).toString());
		assertEquals("P5", TracRepositoryConnector.getTaskPriority("c", priorities).toString());
		assertEquals("P3", TracRepositoryConnector.getTaskPriority("foo", priorities).toString());
		assertEquals("P3", TracRepositoryConnector.getTaskPriority(null, priorities).toString());

		p1 = new TracPriority("a", 10);
		priorities = new TracPriority[] { p1 };
		assertEquals("P1", TracRepositoryConnector.getTaskPriority("a", priorities).toString());
		assertEquals("P3", TracRepositoryConnector.getTaskPriority("b", priorities).toString());
		assertEquals("P3", TracRepositoryConnector.getTaskPriority(null, priorities).toString());

		p1 = new TracPriority("1", 10);
		p2 = new TracPriority("2", 20);
		p3 = new TracPriority("3", 30);
		TracPriority p4 = new TracPriority("4", 40);
		TracPriority p5 = new TracPriority("5", 70);
		TracPriority p6 = new TracPriority("6", 100);
		priorities = new TracPriority[] { p1, p2, p3, p4, p5, p6 };
		assertEquals("P1", TracRepositoryConnector.getTaskPriority("1", priorities).toString());
		assertEquals("P1", TracRepositoryConnector.getTaskPriority("2", priorities).toString());
		assertEquals("P2", TracRepositoryConnector.getTaskPriority("3", priorities).toString());
		assertEquals("P2", TracRepositoryConnector.getTaskPriority("4", priorities).toString());
		assertEquals("P4", TracRepositoryConnector.getTaskPriority("5", priorities).toString());
		assertEquals("P5", TracRepositoryConnector.getTaskPriority("6", priorities).toString());
	}

}
