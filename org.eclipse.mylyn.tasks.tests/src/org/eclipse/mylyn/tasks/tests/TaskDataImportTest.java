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

import java.io.File;

import org.eclipse.mylyn.context.tests.AbstractContextTest;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskDataImportWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskDataImportWizardPage;
import org.eclipse.swt.widgets.Shell;

/**
 * Test case for the Task Import Wizard
 * 
 * @author Rob Elves
 */
public class TaskDataImportTest extends AbstractContextTest {

	private TaskDataImportWizard wizard = null;

	private TaskDataImportWizardPage wizardPage = null;

	private final String sourceZipPath = "testdata/taskdataimporttest/mylardata-2007-01-19.zip";

	private File sourceZipFile = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// Create the import wizard
		wizard = new TaskDataImportWizard();
		wizard.addPages();
		wizard.createPageControls(new Shell());
		wizardPage = (TaskDataImportWizardPage) wizard.getPage(TaskDataImportWizardPage.PAGE_NAME);
		assertNotNull(wizardPage);

		TaskTestUtil.resetTaskListAndRepositories();

		sourceZipFile = TaskTestUtil.getLocalFile(sourceZipPath);
		assertTrue(sourceZipFile.exists());

		ContextCorePlugin.getContextManager().getActivityMetaContext().reset();
	}

	@Override
	protected void tearDown() throws Exception {
		ContextCorePlugin.getContextManager().resetActivityMetaContext();
		TasksUiPlugin.getRepositoryManager().clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		TasksUiPlugin.getTaskListManager().resetTaskList();
		super.tearDown();
	}

	/**
	 * Tests the wizard when it has been asked to import all task data from a zip file
	 */
	// XXX: Put Back
//	public void testImportRepositoriesZip() {
//		TaskList taskList = TasksUiPlugin.getTaskList();
//		InteractionContext historyContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
//		assertNotNull(taskList);
//		assertNotNull(historyContext);
//		assertTrue(taskList.getAllTasks().size() == 0);
//		assertTrue(historyContext.getInteractionHistory().size() == 0);
//		
//		wizardPage.setParameters(true, true, true, true, true, "", sourceZipFile.getPath());
//		wizard.performFinish();
//
//		Collection<AbstractTask> tasks = taskList.getAllTasks();
//		assertEquals(2, tasks.size());
//		for (AbstractTask task : tasks) {
//			assertTrue(ContextCorePlugin.getContextManager().hasContext(task.getHandleIdentifier()));
//		}
//		historyContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
//		assertNotNull(historyContext);
//		assertTrue(historyContext.getInteractionHistory().size() > 0);
//		assertTrue(TasksUiPlugin.getRepositoryManager().getAllRepositories().size() >  2);
//	}
	// XXX: Put Back
//	public void testImportOverwritesAllTasks() {
//		TaskList taskList = TasksUiPlugin.getTaskList();
//		InteractionContext historyContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
//		assertNotNull(taskList);
//		assertNotNull(historyContext);
//		assertTrue(taskList.getAllTasks().size() == 0);
//		assertTrue(historyContext.getInteractionHistory().size() == 0);
//		//assertEquals(2, TasksUiPlugin.getRepositoryManager().getAllRepositories().size());
//
//		AbstractTask task1 = new LocalTask("999", "label");
//		taskList.addTask(task1);
//		Collection<AbstractTask> tasks = taskList.getAllTasks();
//		assertEquals(1, tasks.size());
//
//		wizardPage.setParameters(true, true, true, true, true, "", sourceZipFile.getPath());
//		wizard.performFinish();
//
//		tasks = taskList.getAllTasks();
//		assertEquals(2, tasks.size());
//		assertTrue(!taskList.getAllTasks().contains(task1));
//		for (AbstractTask task : tasks) {
//			assertTrue(ContextCorePlugin.getContextManager().hasContext(task.getHandleIdentifier()));
//		}
//		historyContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
//		assertNotNull(historyContext);
//		assertTrue(historyContext.getInteractionHistory().size() > 0);
//		assertTrue(TasksUiPlugin.getRepositoryManager().getAllRepositories().size() > 2);
//	}
}
