/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryTask;

public class AbstractTaskContainerTest extends TestCase {

	public void testGetChildren() {
		MockRepositoryTask task1 = new MockRepositoryTask("1");
		MockRepositoryTask task2 = new MockRepositoryTask("2");
		MockRepositoryTask task3 = new MockRepositoryTask("3");

		task1.internalAddChild(task2);
		task2.internalAddChild(task3);
		task3.internalAddChild(task1);

		assertFalse(task1.contains("abc"));
	}

}
