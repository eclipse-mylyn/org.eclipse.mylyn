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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
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
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class TaskDataExportTest extends TestCase {

	private File destinationDir;

	private File mylynFolder;

	private final List<File> tempFiles = new ArrayList<File>();

	private TaskDataExportWizard wizard;

	private TaskDataExportWizardPage wizardPage;

	private File createDirectory(File parent, String folderName) {
		File file = new File(parent, folderName);
		if (!file.exists()) {
			assertTrue(file.mkdir());
			tempFiles.add(file);
		}
		return file;
	}

	private File createFile(File directory, String fileName) throws IOException {
		File file = new File(directory, fileName);
		if (!file.exists()) {
			assertTrue(file.createNewFile());
			tempFiles.add(file);
		}
		return file;
	}

	private List<String> getEntries(File file) throws IOException {
		ArrayList<String> entries = new ArrayList<String>();
		ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
		try {
			ZipEntry entry = zipInputStream.getNextEntry();
			while (entry != null) {
				entries.add(entry.getName());
				entry = zipInputStream.getNextEntry();
			}
		} finally {
			zipInputStream.close();
		}
		Collections.sort(entries);
		return entries;
	}

	@Override
	protected void setUp() throws Exception {
		// Create the export wizard
		wizard = new TaskDataExportWizard();
		wizard.addPages();
		wizard.createPageControls(new Shell());
		wizardPage = (TaskDataExportWizardPage) wizard.getPage("org.eclipse.mylyn.tasklist.exportPage");
		assertNotNull(wizardPage);

		mylynFolder = new File(TasksUiPlugin.getDefault().getDataDirectory());

		// Clear context and tasks directory
		File contextDirectory = new File(mylynFolder, "contexts");
		CommonTestUtil.deleteFolder(contextDirectory);
		contextDirectory.mkdir();

		File tasksDirectory = new File(mylynFolder, "tasks");
		CommonTestUtil.deleteFolderRecursively(tasksDirectory);
		tasksDirectory.mkdir();

		// Create test export destination directory
		destinationDir = new File(mylynFolder.getParent(), "TestDir");
		CommonTestUtil.deleteFolder(destinationDir);
		createDirectory(destinationDir.getParentFile(), destinationDir.getName());

		// Create folder/file structure
		createFile(mylynFolder, "monitor-log.xml");
		createFile(mylynFolder, "my-tasklist.xml.zip");
		createFile(mylynFolder, "tasks.xml.zip");
		createFile(mylynFolder, "tasklist.xml.zip");
		createFile(mylynFolder, ".hidden");

		createDirectory(mylynFolder, "my-attachments");
		createDirectory(mylynFolder, "attachments");
		createDirectory(mylynFolder, "backup");

		File tasksandstuff = createDirectory(mylynFolder, "tasksandstuff");
		createFile(tasksandstuff, "file1.xml.zip");
		File taskSubDir = createDirectory(tasksandstuff, "sub");
		createFile(taskSubDir, "file2.xml.zip");
	}

	@Override
	protected void tearDown() throws Exception {
		wizard.dispose();
		wizardPage.dispose();

		Collections.reverse(tempFiles);
		for (File file : tempFiles) {
			file.delete();
		}
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

		List<String> entries = getEntries(files[0]);
		assertEquals(Arrays.asList("my-tasklist.xml.zip", "repositories.xml.zip", "tasks.xml.zip",
				"tasksandstuff/file1.xml.zip", "tasksandstuff/sub/file2.xml.zip"), entries);
	}

	public void testSnapshotWithContext() throws Exception {
		File activityFile = new File(mylynFolder, "contexts/activity.xml.zip");
		if (!activityFile.exists()) {
			assertTrue(activityFile.createNewFile());
		}

		final TaskDataExportOperation backupJob = new TaskDataSnapshotOperation(destinationDir.getPath(),
				"testBackup.zip");
		backupJob.run(new NullProgressMonitor());

		// check that the task list file was exported
		File[] files = destinationDir.listFiles();
		assertEquals(1, files.length);
		List<String> entries = getEntries(files[0]);
		assertEquals(Arrays.asList("contexts/activity.xml.zip", "repositories.xml.zip", "tasks.xml.zip"), entries);
	}

	public void testSnapshotWithoutContext() throws Exception {
		File activityFile = new File(mylynFolder, "contexts/activity.xml.zip");
		if (activityFile.exists()) {
			assertTrue(activityFile.delete());
		}

		final TaskDataExportOperation backupJob = new TaskDataSnapshotOperation(destinationDir.getPath(),
				"testBackup.zip");
		backupJob.run(new NullProgressMonitor());

		// check that the task list file was exported
		File[] files = destinationDir.listFiles();
		assertEquals(1, files.length);
		List<String> entries = getEntries(files[0]);
		assertEquals(Arrays.asList("repositories.xml.zip", "tasks.xml.zip"), entries);
	}

}
