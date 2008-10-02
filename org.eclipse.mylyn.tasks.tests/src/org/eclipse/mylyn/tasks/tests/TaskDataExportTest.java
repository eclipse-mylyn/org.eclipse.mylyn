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
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.tests.AbstractContextTest;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.TaskListBackupManager;
import org.eclipse.mylyn.internal.tasks.ui.TaskListManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskDataExportWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskDataExportWizardPage;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
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

	private AbstractTask task1 = null;

	private final TaskListManager manager = TasksUiPlugin.getTaskListManager();

	private InteractionContext mockContext;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		removeFiles(new File(TasksUiPlugin.getDefault().getDataDirectory()));
//		ContextCore.getContextStore().init();

		// Create the export wizard
		wizard = new TaskDataExportWizard();
		wizard.addPages();
		wizard.createPageControls(new Shell());
		wizardPage = (TaskDataExportWizardPage) wizard.getPage(TaskDataExportWizardPage.PAGE_NAME);
		assertNotNull(wizardPage);

		// Create test export destination directory
		destinationDir = new File(TasksUiPlugin.getDefault().getDataDirectory() + File.separator + "TestDir");
		if (destinationDir.exists()) {
			removeFiles(destinationDir);
		} else {
			destinationDir.mkdir();
		}
		assertTrue(destinationDir.exists());

		// Create a task and context with an interaction event to be saved
		task1 = TasksUiInternal.createNewLocalTask("Export Test Task");
		manager.getTaskList().addTask(task1);
		mockContext = (InteractionContext) ContextCorePlugin.getContextStore().loadContext(task1.getHandleIdentifier());
		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.EDIT, "structureKind", "handle", "originId");
		mockContext.parseEvent(event);

		// Save the context file and check that it exists
		ContextCorePlugin.getContextStore().saveContext(mockContext);
		File taskFile = ContextCorePlugin.getContextStore().getFileForContext(task1.getHandleIdentifier());
		assertTrue(ContextCore.getContextManager().hasContext(task1.getHandleIdentifier()));
		assertTrue(taskFile.exists());

		// do this last to make sure tearDown() runs
		ContextCorePlugin.getContextManager().internalActivateContext(mockContext);
	}

	@Override
	protected void tearDown() throws Exception {
		ContextCore.getContextManager().deactivateContext(mockContext.getHandleIdentifier());
		removeFiles(destinationDir);
		destinationDir.delete();
		assertFalse(destinationDir.exists());
		super.tearDown();
	}

	/**
	 * Tests the wizard when it has been asked to export all task data to a zip file
	 */
	public void testExportAllToZip() throws Exception {

		// Set parameters in the wizard to simulate a user setting them and
		// clicking "Finish"
		wizardPage.setParameters(true, true, true, true, true, destinationDir.getPath());
		wizard.performFinish();

		// Check that the task list file was exported
		File destZipFile = new File(destinationDir + File.separator + TaskListBackupManager.getBackupFileName());
		assertTrue(destZipFile.exists());
		ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(destZipFile));
		ArrayList<String> entries = new ArrayList<String>();

		ZipEntry entry = zipInputStream.getNextEntry();
		while (entry != null) {
			entries.add(entry.getName());
			entry = zipInputStream.getNextEntry();
		}
		zipInputStream.close();
		assertEquals(3, entries.size());
		assertTrue(entries.contains("tasklist.xml.zip"));
		assertTrue(entries.contains("contexts/local-1.xml.zip"));
	}

	private void removeFiles(File root) {
		if (root.isDirectory()) {
			for (File file : root.listFiles()) {
				if (file.isDirectory()) {
					removeFiles(file);
				}
				file.delete();
			}
		}
	}
}
