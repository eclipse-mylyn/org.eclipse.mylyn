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
package org.eclipse.mylar.tasklist.tests;

import java.io.File;

import org.eclipse.mylar.context.core.InteractionEvent;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.core.tests.AbstractContextTest;
import org.eclipse.mylar.internal.context.core.MylarContext;
import org.eclipse.mylar.internal.context.core.MylarContextManager;
import org.eclipse.mylar.internal.tasks.ui.wizards.TaskDataExportWizard;
import org.eclipse.mylar.internal.tasks.ui.wizards.TaskDataExportWizardPage;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.Task;
import org.eclipse.mylar.tasks.ui.TaskListManager;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.widgets.Shell;

/**
 * Test case for the Task Export Wizard
 * 
 * @author Wesley Coelho
 * @author Mik Kersten (fixes)
 */
public class TaskDataExportTest extends AbstractContextTest {

	private TaskDataExportWizard wizard = null;

	private TaskDataExportWizardPage wizardPage = null;

	private File destinationDir = null;

	private ITask task1 = null;

	private TaskListManager manager = TasksUiPlugin.getTaskListManager();

	private MylarContext mockContext;

	protected void setUp() throws Exception {
		super.setUp();

		// Create the export wizard
		wizard = new TaskDataExportWizard();
		wizard.addPages();
		wizard.createPageControls(new Shell());
		wizardPage = (TaskDataExportWizardPage) wizard.getPage(TaskDataExportWizardPage.PAGE_NAME);
		assertNotNull(wizardPage);

		// Create test export destination directory
		destinationDir = new File(TasksUiPlugin.getDefault().getDataDirectory() + File.separator + "TestDir");
		destinationDir.mkdir();
		assertTrue(destinationDir.exists());

		// Create a task and context with an interaction event to be saved
		task1 = new Task(TasksUiPlugin.getTaskListManager().genUniqueTaskHandle(), "Export Test Task", true);
		manager.getTaskList().moveToRoot(task1);
		mockContext = ContextCorePlugin.getContextManager().loadContext(task1.getHandleIdentifier());
		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.EDIT, "structureKind", "handle", "originId");
		mockContext.parseEvent(event);
		ContextCorePlugin.getContextManager().activateContext(mockContext);

		// Save the context file and check that it exists
		ContextCorePlugin.getContextManager().saveContext(mockContext.getHandleIdentifier());
		File taskFile = ContextCorePlugin.getContextManager().getFileForContext(task1.getHandleIdentifier());
		assertTrue(ContextCorePlugin.getContextManager().hasContext(task1.getHandleIdentifier()));
		assertTrue(taskFile.exists());
	}

	protected void tearDown() throws Exception {
		File[] files = destinationDir.listFiles();
		for (File file : files) {
			file.delete();
		}

		destinationDir.delete();
		assertFalse(destinationDir.exists());
		ContextCorePlugin.getContextManager().deactivateContext(mockContext.getHandleIdentifier());
		super.tearDown();
	}

	/**
	 * Tests the wizard when it has been asked to export all task data to a zip
	 * file
	 */
	public void testExportAllToZip() {

		// Set parameters in the wizard to simulate a user setting them and
		// clicking "Finish"
		wizardPage.setParameters(true, true, true, true, true, destinationDir.getPath());
		wizard.performFinish();

		// Check that the task list file was exported
		File destZipFile = new File(destinationDir + File.separator + TaskDataExportWizard.getZipFileName());
		assertTrue(destZipFile.exists());
	}

	/** Tests the wizard when it has been asked to export all task data */
	public void testExportAll() {

		// Set parameters in the wizard to simulate a user setting them and
		// clicking "Finish"
		wizardPage.setParameters(true, true, true, true, false, destinationDir.getPath());
		wizard.performFinish();

		// Check that the task list file was exported
		File destTaskListFile = new File(destinationDir + File.separator + TasksUiPlugin.DEFAULT_TASK_LIST_FILE);
		assertTrue(destTaskListFile.exists());

		// Check that the activity history file was exported
		File destActivationHistoryFile = new File(destinationDir + File.separator
				+ MylarContextManager.CONTEXT_HISTORY_FILE_NAME + MylarContextManager.CONTEXT_FILE_EXTENSION);
		assertTrue(destActivationHistoryFile.exists());

		// Check that the task context file created in setUp() was exported
		File destTaskContextFile = ContextCorePlugin.getContextManager().getFileForContext(task1.getHandleIdentifier());
		// File destTaskContextFile = new File(destinationDir + File.separator +
		// task1.getContextPath() + MylarContextManager.CONTEXT_FILE_EXTENSION);
		assertTrue(destTaskContextFile.exists());
	}
}
