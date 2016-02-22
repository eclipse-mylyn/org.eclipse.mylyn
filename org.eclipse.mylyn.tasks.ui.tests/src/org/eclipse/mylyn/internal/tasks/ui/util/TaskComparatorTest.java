/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.junit.Test;

public class TaskComparatorTest {

	@Test
	public void compareRank() {
		assertCompare(taskWithRank("5"), taskWithRank("7"));
		assertCompare(taskWithRank("5"), taskWithRank("17"));
		assertCompare(taskWithRank("5"), taskWithRank("17.1"));
		assertCompare(taskWithRank("5.3"), taskWithRank("17.1"));
		assertCompare(taskWithRank("#$d"), taskWithRank("#$e"));
		assertCompare(taskWithRank("gjp"), taskWithRank("gkp"));
		assertCompare(taskWithRank("A"), taskWithRank("a"));
		assertCompare(taskWithRank("dsfhgSd"), taskWithRank("dsfhgsd"));
		assertCompare(taskWithRank("dsfhgS"), taskWithRank("dsfhgsd"));
		assertCompare(taskWithRank("dsfhgs"), taskWithRank("dsfhgsd"));

		assertEquals(0, compare(taskWithRank("5"), taskWithRank("5")));
		assertEquals(0, compare(taskWithRank("17.1"), taskWithRank("17.1")));
		assertEquals(0, compare(taskWithRank("dsfhgs"), taskWithRank("dsfhgs")));
		assertEquals(0, compare(taskWithRank("ds#fHgs"), taskWithRank("ds#fHgs")));
	}

	private void assertCompare(ITask task1, ITask task2) {
		assertTrue(compare(task1, task2) < 0);
		assertTrue(compare(task2, task1) > 0);
	}

	private int compare(ITask task1, ITask task2) {
		return new TaskComparator().compare(task1, task2);
	}

	private ITask taskWithRank(String rank) {
		@SuppressWarnings("restriction")
		ITask task = new TaskTask("kind", "http://mock", "1");
		task.setPriority("");
		task.setAttribute(TaskAttribute.RANK, rank);
		return task;
	}
}
