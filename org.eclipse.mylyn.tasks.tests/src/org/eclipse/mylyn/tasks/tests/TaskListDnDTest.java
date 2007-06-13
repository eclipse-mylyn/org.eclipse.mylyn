/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListDropAdapter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TaskListManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class TaskListDnDTest extends TestCase {

	private TaskListDropAdapter dropAdapter;

	private TaskListManager manager;

	@Override
	protected void setUp() throws Exception {
		manager = TasksUiPlugin.getTaskListManager();
		manager.resetTaskList();

		TreeViewer viewer = TaskListView.getFromActivePerspective().getViewer();
		assertNotNull(viewer);
		dropAdapter = new TaskListDropAdapter(viewer);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		manager.resetTaskList();
		manager.saveTaskList();
		assertNull(manager.getTaskList().getActiveTask());
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
		assertEquals(0, manager.getTaskList().getRootTasks().size());
		String url = "http://eclipse.org/mylar";
		String title = "Mylar Technology Project";
		String urlData = url + "\n" + title;

		dropAdapter.performDrop(urlData);
		Set<AbstractTask> tasks = manager.getTaskList().getRootTasks();
		assertNotNull(tasks);
		assertEquals(1, tasks.size());
		assertEquals(url, tasks.iterator().next().getTaskUrl());

		// TODO: Failing due to asynchronous retrieval of title from url
		// assertEquals(title, tasks.get(0).getDescription(false));
	}
}
