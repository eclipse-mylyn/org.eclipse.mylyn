/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.tasks.tests.connector.MockTask;

public class AbstractTaskContainerTest extends TestCase {

	public void testGetChildren() {
		MockTask task1 = new MockTask("1");
		MockTask task2 = new MockTask("2");
		MockTask task3 = new MockTask("3");
		MockTask task4 = new MockTask("4");

		task1.internalAddChild(task2);
		task2.internalAddChild(task3);
		task3.internalAddChild(task1);
		task3.internalAddChild(task4);

		assertTrue(task1.contains(task4.getHandleIdentifier()));
		assertTrue(task3.contains(task4.getHandleIdentifier()));
		assertFalse(task3.contains("abc"));
	}

}
