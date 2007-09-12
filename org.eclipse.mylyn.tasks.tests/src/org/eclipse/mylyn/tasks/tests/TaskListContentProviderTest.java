/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListContentProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Felix Schwarz
 */
public class TaskListContentProviderTest extends TestCase {

	private TaskListContentProvider provider;

	private TaskListView view;

	private TaskList taskList;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TaskListView.openInActivePerspective();
		view = TaskListView.getFromActivePerspective();
		provider = (TaskListContentProvider) view.getViewer().getContentProvider();
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(TasksUiPreferenceConstants.GROUP_SUBTASKS, false);
		view.clearFilters(true);
		view.addFilter(view.getCompleteFilter());
		taskList = TasksUiPlugin.getTaskListManager().getTaskList();
	}

	@Override
	protected void tearDown() throws Exception {
		view.clearFilters(true);
		super.tearDown();
	}

	public void testHasChildren() {

		AbstractTask parent = new LocalTask("parent", "parent label");
		AbstractTask completedChild = new LocalTask("completed child", "completed child label");
		completedChild.setCompleted(true);
		taskList.addTask(completedChild, parent);
		assertFalse(provider.hasChildren(parent));

		AbstractTask incompleteChild = new LocalTask("incomplete child", "incomplete child label");
		incompleteChild.setCompleted(false);
		taskList.addTask(incompleteChild, parent);
		assertTrue(provider.hasChildren(parent));
	}
}
