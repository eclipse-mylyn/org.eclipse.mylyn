/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

public class TaskListTest {

	private TaskList taskList;

	@Before
	public void setUp() throws Exception {
		taskList = new TaskList();
	}

	@Test
	public void removeMatchedTaskFromUnmatched() {
		UnmatchedTaskContainer unmatched = spy(new UnmatchedTaskContainer("kind", "repoUrl"));
		TaskCategory category1 = new TaskCategory("cat1");
		TaskCategory category2 = new TaskCategory("cat2");
		AbstractTask task = spy(new TaskTask("kind", "repoUrl", "id"));
		taskList.addCategory(category1);
		taskList.addCategory(category2);
		taskList.addUnmatchedContainer(unmatched);

		// add to unmatched
		taskList.addTask(task);
		// should remove from unmatched
		taskList.addTask(task, category1);
		verify(unmatched, times(1)).internalRemoveChild(task);
		// should not remove from unmatched again
		taskList.addTask(task, category2);
		verify(unmatched, times(1)).internalRemoveChild(task);
	}

}
