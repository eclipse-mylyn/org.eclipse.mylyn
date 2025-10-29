/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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

import java.util.Collection;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListDropAdapter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;

import junit.framework.TestCase;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
@SuppressWarnings("nls")
public class TaskListDropAdapterTest extends TestCase {

	private TaskListDropAdapter dropAdapter;

	private TaskList taskList;

	@Override
	protected void setUp() throws Exception {
		TaskTestUtil.resetTaskListAndRepositories();

		TreeViewer viewer = TaskListView.getFromActivePerspective().getViewer();
		assertNotNull(viewer);
		dropAdapter = new TaskListDropAdapter(viewer);
		taskList = TasksUiPlugin.getTaskList();
	}

	@Override
	protected void tearDown() throws Exception {
		TaskTestUtil.resetTaskList();
	}

	public void testUrlDrop() {
		assertEquals(0, taskList.getDefaultCategory().getChildren().size());
		String url = "http://eclipse.org/mylyn";
		String title = "Mylyn Project";
		String urlData = url + "\n" + title;

		dropAdapter.performDrop(urlData);
		Collection<ITask> tasks = taskList.getDefaultCategory().getChildren();
		assertNotNull(tasks);
		assertEquals(1, tasks.size());
		assertEquals(url, tasks.iterator().next().getUrl());

		// TODO: Failing due to asynchronous retrieval of title from url
		// assertEquals(title, tasks.get(0).getDescription(false));
	}

}
