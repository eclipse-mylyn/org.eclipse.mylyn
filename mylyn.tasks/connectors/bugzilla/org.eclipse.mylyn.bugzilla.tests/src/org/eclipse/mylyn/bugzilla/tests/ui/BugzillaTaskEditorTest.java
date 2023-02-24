/*******************************************************************************
 * Copyright (c) 2004, 2013 Jeff Pound and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeff Pound - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.ui;

import junit.framework.TestCase;

import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.sdk.util.UiTestUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tests.util.TestFixture;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff Pound
 * @author Steffen Pingel
 */
public class BugzillaTaskEditorTest extends TestCase {

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		// ensure that the local repository is present
		TestFixture.resetTaskListAndRepositories();
		repository = BugzillaFixture.current().repository();
		TasksUi.getRepositoryManager().addRepository(repository);
	}

	@Override
	protected void tearDown() throws Exception {
		UiTestUtil.closeAllEditors();
		TestFixture.resetTaskListAndRepositories();
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

		TaskData taskData = TasksUiInternal.createTaskData(repository, taskMappingInit, taskMappingSelect, null);
		TasksUiInternal.createAndOpenNewTask(taskData);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		TaskEditor taskEditor = (TaskEditor) page.getActiveEditor();
		assertEquals("New Task", taskEditor.getTitle());
	}

}
