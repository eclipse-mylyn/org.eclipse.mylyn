/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import org.eclipse.mylyn.tasks.tests.connector.MockTask;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class TaskContainerTest extends TestCase {

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
