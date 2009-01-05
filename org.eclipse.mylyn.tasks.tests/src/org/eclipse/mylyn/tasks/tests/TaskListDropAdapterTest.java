/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Collection;

import junit.framework.TestCase;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListDropAdapter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
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

	public void testisUrl() {
		String url = "http://eclipse.org";
		String title = "Title";
		String urlData = url + "\n" + title;
		assertFalse(dropAdapter.isUrl(title));
		assertTrue(dropAdapter.isUrl(url));
		assertTrue(dropAdapter.isUrl(urlData));
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
