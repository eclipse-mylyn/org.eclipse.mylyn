/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskDataImportWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskDataImportWizardPage;
import org.eclipse.mylyn.internal.tasks.ui.workingsets.TaskWorkingSetUpdater;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.internal.Workbench;

/**
 * Test case for the Task Import Wizard.
 * 
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class TaskDataImportTest extends TestCase {

	private TaskDataImportWizard wizard = null;

	private TaskDataImportWizardPage wizardPage = null;

	private final String BACKUP_v1 = "testdata/taskdataimporttest/mylardata-2007-01-19.zip";

	private final String BACKUP_v3 = "testdata/taskdataimporttest/mylyn-v3-data-2009-12-09-171942.zip";

	private final String BACKUP_IMPORT_TEST = "testdata/taskdataimporttest/mylyn-v3-data-2010-02-28.zip";

	private final String BACKUP_OLD_v3 = "testdata/taskdataimporttest/mylyn-v3-data-2009-12-09-old-tasklist.zip";

	private TaskList taskList;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// Create the import wizard
		wizard = new TaskDataImportWizard();
		wizard.addPages();
		wizard.createPageControls(new Shell());
		wizardPage = (TaskDataImportWizardPage) wizard.getPage("org.eclipse.mylyn.tasklist.importPage");
		assertNotNull(wizardPage);

		TaskTestUtil.resetTaskListAndRepositories();
		taskList = TasksUiPlugin.getTaskList();

//		ContextCorePlugin.getContextManager().getActivityMetaContext().reset();
	}

	@Override
	protected void tearDown() throws Exception {
		wizard.dispose();
		wizardPage.dispose();
//		ContextCorePlugin.getContextManager().resetActivityMetaContext();
		TaskTestUtil.resetTaskListAndRepositories();
		super.tearDown();
	}

	/**
	 * Tests import of task data with working set active doesn't result in queries and categories being erroneously
	 * added to the active working set.
	 */
	public void testDisableWorkingSets() throws Exception {
		IWorkingSetManager workingSetManager = Workbench.getInstance().getWorkingSetManager();
		IWorkingSet workingSet = workingSetManager.createWorkingSet("Task Working Set", new IAdaptable[] {});
		workingSet.setId(TaskWorkingSetUpdater.ID_TASK_WORKING_SET);
		assertEquals(0, workingSet.getElements().length);
		workingSetManager.addWorkingSet(workingSet);
		Set<IWorkingSet> workingSets = new HashSet<IWorkingSet>();
		workingSets.add(workingSet);
		TaskWorkingSetUpdater.applyWorkingSetsToAllWindows(workingSets);

		TaskDataImportWizard.performFinish(TaskTestUtil.getLocalFile(BACKUP_IMPORT_TEST), null);

		assertEquals(2, TasksUiPlugin.getTaskList().getCategories().size());

		// Active working set should not be populated with 
		// imported "Test" category
		assertEquals(0, workingSet.getElements().length);
		workingSetManager.removeWorkingSet(workingSet);

	}

	/**
	 * Tests the wizard when it has been asked to import all task data from a zip file
	 */
	public void testImportRepositoriesZip() {
//		InteractionContext historyContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		assertNotNull(taskList);
//		assertNotNull(historyContext);
		assertTrue(taskList.getAllTasks().size() == 0);
//		assertTrue(historyContext.getInteractionHistory().size() == 0);

		wizardPage.setSource(true, TaskTestUtil.getLocalFile(BACKUP_v1).getAbsolutePath());
		wizard.performFinish();

		Collection<AbstractTask> tasks = taskList.getAllTasks();
		assertEquals(2, tasks.size());
//		for (AbstractTask task : tasks) {
//			assertTrue(ContextCorePlugin.getContextManager().hasContext(task.getHandleIdentifier()));
//		}
//		historyContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
//		assertNotNull(historyContext);
//		assertTrue(historyContext.getInteractionHistory().size() > 0);
		if (TasksUi.getRepositoryConnector("bugzilla") != null) {
			assertEquals(3, TasksUiPlugin.getRepositoryManager().getAllRepositories().size());
		} else {
			// Bugzilla repositories are not imported if connector is not available
			assertEquals(1, TasksUiPlugin.getRepositoryManager().getAllRepositories().size());
		}
	}

	public void testImportOverwritesAllTasks() {
//		InteractionContext historyContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		assertNotNull(taskList);
//		assertNotNull(historyContext);
		assertTrue(taskList.getAllTasks().size() == 0);
//		assertTrue(historyContext.getInteractionHistory().size() == 0);

		AbstractTask task1 = new LocalTask("999", "label");
		taskList.addTask(task1);
		Collection<AbstractTask> tasks = taskList.getAllTasks();
		assertEquals(1, tasks.size());

		wizardPage.setSource(true, TaskTestUtil.getLocalFile(BACKUP_v1).getAbsolutePath());
		wizard.performFinish();

		tasks = taskList.getAllTasks();
		assertEquals(2, tasks.size());
		assertTrue(!taskList.getAllTasks().contains(task1));
//		for (AbstractTask task : tasks) {
//			assertTrue(ContextCorePlugin.getContextManager().hasContext(task.getHandleIdentifier()));
//		}
//		historyContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
//		assertNotNull(historyContext);
//		assertTrue(historyContext.getInteractionHistory().size() > 0);
		if (TasksUi.getRepositoryConnector("bugzilla") != null) {
			assertEquals(3, TasksUiPlugin.getRepositoryManager().getAllRepositories().size());
		} else {
			// Bugzilla repositories are not imported if connector is not available
			assertEquals(1, TasksUiPlugin.getRepositoryManager().getAllRepositories().size());
		}
	}

	public void testImportBackupWithOldTaskList() {
		wizardPage.setSource(true, TaskTestUtil.getLocalFile(BACKUP_OLD_v3).getAbsolutePath());
		wizard.performFinish();

		Collection<AbstractTask> tasks = taskList.getAllTasks();
		assertEquals(1, tasks.size());
		assertEquals("Task 3", tasks.iterator().next().getSummary());
	}

	public void testImportBackup() {
		wizardPage.setSource(true, TaskTestUtil.getLocalFile(BACKUP_v3).getAbsolutePath());
		wizard.performFinish();

		Collection<AbstractTask> tasks = taskList.getAllTasks();
		assertEquals(1, tasks.size());
		assertEquals("Task 3", tasks.iterator().next().getSummary());
	}

}
