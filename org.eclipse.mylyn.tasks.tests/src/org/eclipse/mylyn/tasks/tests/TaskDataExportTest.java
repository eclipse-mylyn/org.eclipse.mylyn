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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.tests.support.CommonsTestUtil;
import org.eclipse.mylyn.context.tests.AbstractContextTest;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskDataExportOperation;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskDataSnapshotOperation;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskDataExportWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskDataExportWizardPage;
import org.eclipse.swt.widgets.Shell;

/**
 * Test case for the Task Export Wizard
 * 
 * @author Wesley Coelho
 * @author Mik Kersten (fixes)
 */
public class TaskDataExportTest extends AbstractContextTest {

	private TaskDataExportWizard wizard;

	private TaskDataExportWizardPage wizardPage;

	private File destinationDir;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// Create the export wizard
		wizard = new TaskDataExportWizard();
		wizard.addPages();
		wizard.createPageControls(new Shell());
		wizardPage = (TaskDataExportWizardPage) wizard.getPage("org.eclipse.mylyn.tasklist.exportPage");
		assertNotNull(wizardPage);

		// Create test export destination directory
		File mylynFolder = new File(TasksUiPlugin.getDefault().getDataDirectory());
		destinationDir = new File(mylynFolder.getParent(), "TestDir");
		CommonsTestUtil.deleteFolder(destinationDir);
		destinationDir.mkdir();

		// Create folder/file structure
		File tasklist = new File(mylynFolder, "tasks.xml.zip");
		assertTrue(tasklist.createNewFile());
		File hidden = new File(mylynFolder, ".hidden");
		hidden.createNewFile();
		assertTrue(hidden.exists());
		File tasks = new File(mylynFolder, "tasksandstuff");
		assertTrue(tasks.mkdir());
		File backup = new File(mylynFolder, "backup");
		if (!backup.exists()) {
			assertTrue(backup.mkdir());
		}
		File tasksFile = new File(tasks, "file1.xml.zip");
		assertTrue(tasksFile.createNewFile());
		File tasksSubDir = new File(tasks, "sub");
		assertTrue(tasksSubDir.mkdir());
		assertTrue(new File(tasksSubDir, "file2.xml.zip").createNewFile());

	}

	@Override
	protected void tearDown() throws Exception {
		wizard.dispose();
		wizardPage.dispose();
		CommonsTestUtil.deleteFolder(destinationDir);
		File mylynFolder = new File(TasksUiPlugin.getDefault().getDataDirectory());
		// Create folder/file structure
		File tasklist = new File(mylynFolder, "tasks.xml.zip");
		tasklist.delete();
		File hidden = new File(mylynFolder, ".hidden");
		hidden.delete();
		File tasks = new File(mylynFolder, "tasksandstuff");
		File tasksSubDir = new File(tasks, "sub");
		File backup = new File(mylynFolder, "backup");
		CommonsTestUtil.deleteFolder(backup);
		CommonsTestUtil.deleteFolder(tasksSubDir);
		CommonsTestUtil.deleteFolder(tasks);
		super.tearDown();
	}

	/**
	 * Tests the wizard when it has been asked to export all task data to a zip file.
	 */
	public void testExportAllToZip() throws Exception {
		// set parameters in the wizard to simulate a user setting them and clicking "Finish"
		wizardPage.setDestinationDirectory(destinationDir.getPath());
		wizard.performFinish();

		// check that the task list file was exported
		File[] files = destinationDir.listFiles();
		assertEquals(1, files.length);
		ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(files[0]));
		try {
			ArrayList<String> entries = new ArrayList<String>();
			ZipEntry entry = zipInputStream.getNextEntry();
			while (entry != null) {
				entries.add(entry.getName());
				entry = zipInputStream.getNextEntry();
			}
			assertFalse(entries.contains(".hidden"));
			assertTrue(entries.contains("tasks.xml.zip"));
			assertTrue(entries.contains("tasksandstuff/file1.xml.zip"));
			assertTrue(entries.contains("tasksandstuff/sub/file2.xml.zip"));
			assertFalse(entries.contains("backup"));
		} finally {
			zipInputStream.close();
		}
	}

	public void testSnapshot() throws Exception {
		final TaskDataExportOperation backupJob = new TaskDataSnapshotOperation(destinationDir.getPath(),
				"testBackup.zip");
		backupJob.run(new NullProgressMonitor());
		// check that the task list file was exported
		File[] files = destinationDir.listFiles();
		assertEquals(1, files.length);
		ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(files[0]));
		try {
			ArrayList<String> entries = new ArrayList<String>();
			ZipEntry entry = zipInputStream.getNextEntry();
			while (entry != null) {
				entries.add(entry.getName());
				entry = zipInputStream.getNextEntry();
			}
			assertFalse(entries.contains(".hidden"));
			assertTrue(entries.contains("tasks.xml.zip"));
			assertTrue(entries.contains("repositories.xml.zip"));
			assertTrue(entries.contains("contexts/activity.xml.zip"));
			assertFalse(entries.contains("tasks"));
			assertEquals(3, entries.size());
		} finally {
			zipInputStream.close();
		}
	}
}
