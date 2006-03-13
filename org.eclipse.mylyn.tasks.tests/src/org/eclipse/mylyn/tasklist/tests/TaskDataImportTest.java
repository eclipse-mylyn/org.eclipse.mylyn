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
import java.util.Set;

import org.eclipse.mylar.core.tests.AbstractContextTest;
import org.eclipse.mylar.internal.tasklist.ui.wizards.TaskDataImportWizard;
import org.eclipse.mylar.internal.tasklist.ui.wizards.TaskDataImportWizardPage;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskList;
import org.eclipse.mylar.provisional.tasklist.TaskListManager;
import org.eclipse.swt.widgets.Shell;

/**
 * Test case for the Task Import Wizard
 * 
 * @author Rob Elves
 */
public class TaskDataImportTest extends AbstractContextTest {

	private TaskDataImportWizard wizard = null;

	private TaskDataImportWizardPage wizardPage = null;

	private String sourceDir = "testdata/taskdataimporttest";

	private File sourceDirFile = null;

	private String sourceZipPath = "testdata/taskdataimporttest/mylardata-2006-02-16.zip";

	private File sourceZipFile = null;

	private TaskListManager manager = MylarTaskListPlugin.getTaskListManager();

	protected void setUp() throws Exception {
		super.setUp();

		// Create the import wizard
		wizard = new TaskDataImportWizard();
		wizard.addPages();
		wizard.createPageControls(new Shell());
		wizardPage = (TaskDataImportWizardPage) wizard.getPage(TaskDataImportWizardPage.PAGE_NAME);
		assertNotNull(wizardPage);

		manager.resetTaskList();
		assertTrue(manager.getTaskList().getRootElements().size() == 1);

		sourceDirFile = TaskTestUtil.getLocalFile(sourceDir);
		assertTrue(sourceDirFile.exists());
		sourceZipFile = TaskTestUtil.getLocalFile(sourceZipPath);
		assertTrue(sourceZipFile.exists());

		// make sure no tasks and categories exist prior to import tests
		assertEquals(1, manager.getTaskList().getTaskContainers().size()); 
	}

	protected void tearDown() throws Exception {

		super.tearDown();
	}

	/**
	 * Tests the wizard when it has been asked to import all task data from a
	 * zip file
	 */
	public void testImportFromAllFromZip() {
		wizardPage.setParameters(true, true, true, true, true, "", sourceZipFile.getPath());
		wizard.performFinish();

		TaskList taskList = MylarTaskListPlugin.getTaskListManager().getTaskList();
		assertNotNull(taskList);
		Set<ITask> tasks = taskList.getRootTasks();
		assertTrue(tasks.size() > 0);
		for (ITask task : tasks) {
			assertTrue(MylarPlugin.getContextManager().hasContext(task.getHandleIdentifier()));
		}
	}

	/** Tests the wizard when it has been asked to import task data from folder */
	public void testImportFromAllFromFolder() {
		wizardPage.setParameters(true, true, true, true, false, sourceDirFile.getPath(), "");
		wizard.performFinish();

		TaskList taskList = MylarTaskListPlugin.getTaskListManager().getTaskList();
		assertNotNull(taskList);
		Set<ITask> tasks = taskList.getRootTasks();
		assertTrue(tasks.size() > 0);
		for (ITask task : tasks) {
			assertTrue(MylarPlugin.getContextManager().hasContext(task.getHandleIdentifier()));
		}
	}
}
