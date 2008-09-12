/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Felix Schwartz - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListContentProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

/**
 * @author Felix Schwarz
 */
public class TaskListContentProviderTest extends TestCase {

	private TaskListContentProvider provider;

	private TaskListView view;

	private ITaskList taskList;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TasksUiUtil.openTasksViewInActivePerspective();
		view = TaskListView.getFromActivePerspective();
		provider = (TaskListContentProvider) view.getViewer().getContentProvider();
		TasksUiPlugin.getDefault().getPreferenceStore().setValue(ITasksUiPreferenceConstants.GROUP_SUBTASKS, true);
		view.clearFilters(true);
		view.addFilter(view.getCompleteFilter());
		taskList = TasksUiPlugin.getTaskList();
	}

	@Override
	protected void tearDown() throws Exception {
		view.clearFilters(true);
		super.tearDown();
	}

	public void testHasChildren() {

		AbstractTask parent = new LocalTask("parent", "parent label");
		AbstractTask completedChild = new LocalTask("completed child", "completed child label");
		completedChild.setCompletionDate(new Date());
		taskList.addTask(parent);
		taskList.addTask(completedChild, parent);
		assertFalse(provider.hasChildren(parent));

		AbstractTask incompleteChild = new LocalTask("incomplete child", "incomplete child label");
		incompleteChild.setCompletionDate(null);
		taskList.addTask(incompleteChild, parent);
		assertTrue(provider.hasChildren(parent));
	}
}
