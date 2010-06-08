/*******************************************************************************
 * Copyright (c) 2006, 2009 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.ui;

import junit.framework.TestCase;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tests.util.TestFixture;
import org.eclipse.mylyn.trac.tests.support.TracFixture;
import org.eclipse.mylyn.trac.tests.support.TracTestUtil;

/**
 * @author Steffen Pingel
 */
public class TracTaskEditorTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		TestFixture.resetTaskList();
	}

	public void testGetSelectedRepository() throws Exception {
		TaskRepository repository = TracFixture.DEFAULT.singleRepository();

		ITask task = TracTestUtil.createTask(repository, "1");
		TasksUiPlugin.getTaskList().addTask(task);
		TasksUiUtil.openTask(task);

		TaskListView taskListView = TaskListView.getFromActivePerspective();
		// force refresh since automatic refresh is delayed  
		taskListView.getViewer().refresh();
		taskListView.getViewer().expandAll();
		taskListView.getViewer().setSelection(new StructuredSelection(task));

		assertFalse(taskListView.getViewer().getSelection().isEmpty());
		assertEquals(repository, TasksUiUtil.getSelectedRepository(taskListView.getViewer()));
	}

}
