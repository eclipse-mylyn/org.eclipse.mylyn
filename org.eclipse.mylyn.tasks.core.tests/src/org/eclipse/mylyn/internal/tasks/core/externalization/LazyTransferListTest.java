/*******************************************************************************
 * Copyright (c) 2018 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.externalization;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITransferList;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.junit.Before;
import org.junit.Test;

public class LazyTransferListTest {

	private ITransferList taskList;

	private LazyTransferList lazyList;

	@Before
	public void setUp() throws Exception {
		taskList = mock(TaskList.class);
		lazyList = new LazyTransferList(taskList);
	}

	@Test
	public void addUnmatchedTask() {
		AbstractTask task = spy(new TaskTask("kind", "repoUrl", "id"));

		// add to unmatched
		lazyList.addTask(task);

		// should not add task to task list
		verify(taskList, never()).addTask(task);
		verify(taskList, never()).addTask(eq(task), any());

		// commit should add the task to task list
		lazyList.commit();
		verify(taskList).addTask(task);

		// subsequent commit should not add task again
		lazyList.commit();
		verifyNoMoreInteractions(taskList);
	}

	@Test
	public void addSubTask() {
		AbstractTask task = spy(new TaskTask("kind", "repoUrl", "parent"));
		AbstractTask subTask = spy(new TaskTask("kind", "repoUrl", "child"));

		// add subtask to a task container
		lazyList.addTask(subTask);
		lazyList.addTask(task);
		lazyList.addTask(subTask, task);

		// should add task and subtask to task list
		verify(taskList).addTask(task);
		verify(taskList).addTask(subTask, task);
		verifyNoMoreInteractions(taskList);

		// commit should not add anything to task list
		lazyList.commit();
		verifyNoMoreInteractions(taskList);
	}

}
