/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff Pound
 */
public class TaskEditorTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		TasksUiPlugin.getRepositoryManager().clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		TasksUiPlugin.getTaskListManager().resetTaskList();
		TasksUiPlugin.getExternalizationManager().requestSave();
		super.tearDown();
	}

	/**
	 * Automated task creation needs to access newly created task editors. This test tests that the access is available.
	 * 
	 * @throws Exception
	 */
	public void testAccessNewEditor() throws Exception {
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

		TaskRepository taskRepository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND,
				IBugzillaConstants.TEST_BUGZILLA_222_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(taskRepository);
		final TaskData[] taskData = new TaskData[1];

		taskData[0] = TasksUiInternal.createTaskData(taskRepository, taskMappingInit, taskMappingSelect, null);
		TasksUiInternal.createAndOpenNewTask(taskData[0]);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		TaskEditor taskEditor = (TaskEditor) page.getActiveEditor();
		assertEquals("New Task", taskEditor.getTitle());
	}

	public void testinitializeTaskData() throws Exception {
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

		TaskRepository taskRepository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND,
				IBugzillaConstants.TEST_BUGZILLA_222_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(taskRepository);

		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				taskRepository.getConnectorKind());
		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(taskRepository);
		TaskData taskData = new TaskData(mapper, taskRepository.getConnectorKind(), taskRepository.getRepositoryUrl(),
				"");
		assertFalse(taskDataHandler.initializeTaskData(taskRepository, taskData, null, null));
		assertFalse(taskDataHandler.initializeTaskData(taskRepository, taskData, taskMappingInit, null));
		assertTrue(taskDataHandler.initializeTaskData(taskRepository, taskData, taskMappingSelect, null));
	}
}
