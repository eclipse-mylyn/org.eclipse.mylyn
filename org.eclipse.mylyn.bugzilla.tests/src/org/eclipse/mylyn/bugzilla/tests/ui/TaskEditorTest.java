/*******************************************************************************
 * Copyright (c) 2004, 2009 Jeff Pound and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeff Pound - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.ui;

import junit.framework.TestCase;

import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff Pound
 * @author Steffen Pingel
 */
public class TaskEditorTest extends TestCase {

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		BugzillaFixture.current();
		repository = BugzillaFixture.DEFAULT.singleRepository();
	}

	@Override
	protected void tearDown() throws Exception {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}

	/**
	 * Tests that a task editor opens when creating new Bugzilla tasks.
	 */
	public void testOpenNewEditor() throws Exception {
		final TaskMapping taskMappingInit = new TaskMapping() {
			@Override
			public String getSummary() {
				return "The Summary";
			}

			@Override
			public String getDescription() {
				return "The Description";
			}
		};
		final TaskMapping taskMappingSelect = new TaskMapping() {
			@Override
			public String getProduct() {
				return "TestProduct";
			}
		};

		final TaskData[] taskData = new TaskData[1];

		taskData[0] = TasksUiInternal.createTaskData(repository, taskMappingInit, taskMappingSelect, null);
		TasksUiInternal.createAndOpenNewTask(taskData[0]);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		TaskEditor taskEditor = (TaskEditor) page.getActiveEditor();
		assertEquals("New Task", taskEditor.getTitle());
	}

}
