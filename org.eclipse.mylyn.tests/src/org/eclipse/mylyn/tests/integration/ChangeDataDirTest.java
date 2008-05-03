/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.tests.integration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.ui.TaskListManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractTask;

/**
 * Tests changes to the main data directory location.
 * 
 * @author Wesley Coelho
 * @author Mik Kersten (rewrites)
 */
public class ChangeDataDirTest extends TestCase {

	private String newDataDir = null;

	private final String defaultDir = TasksUiPlugin.getDefault().getDefaultDataDirectory();

	private final TaskListManager manager = TasksUiPlugin.getTaskListManager();

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		newDataDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + '/'
				+ ChangeDataDirTest.class.getSimpleName();
		File dir = new File(newDataDir);

		dir.mkdir();
		dir.deleteOnExit();
		manager.resetTaskList();
		TasksUiPlugin.getExternalizationManager().requestSave();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		manager.resetTaskList();
		TasksUiPlugin.getDefault().setDataDirectory(defaultDir, new NullProgressMonitor());
	}

	public void testDefaultDataDirectoryMove() throws CoreException {
		String workspaceRelativeDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + '/'
				+ ".metadata" + '/' + ".mylyn";
		assertEquals(defaultDir, workspaceRelativeDir);

		TasksUiPlugin.getDefault().setDataDirectory(newDataDir, new NullProgressMonitor());
		assertEquals(TasksUiPlugin.getDefault().getDataDirectory(), newDataDir);
	}

	public void testTaskMove() throws CoreException {
		AbstractTask task = TasksUiInternal.createNewLocalTask("label");
		String handle = task.getHandleIdentifier();
		manager.getTaskList().addTask(task,
				manager.getTaskList().getUnmatchedContainer(LocalRepositoryConnector.REPOSITORY_URL));

		AbstractTask readTaskBeforeMove = manager.getTaskList().getTask(handle);
		assertNotNull(readTaskBeforeMove);
		assertTrue(manager.getTaskList().getAllTasks().size() > 0);
		copyDataDirContentsTo(newDataDir);
		TasksUiPlugin.getDefault().setDataDirectory(newDataDir, new NullProgressMonitor());
		assertTrue(manager.getTaskList().getAllTasks().size() > 0);
		AbstractTask readTaskAfterMove = manager.getTaskList().getTask(handle);

		assertNotNull(readTaskAfterMove);
		assertEquals(readTaskBeforeMove.getCreationDate(), readTaskAfterMove.getCreationDate());
	}

	/**
	 * Copies all files in the current data directory to the specified folder. Will overwrite.
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void copyDataDirContentsTo(String targetFolderPath) {

		File mainDataDir = new File(TasksUiPlugin.getDefault().getDataDirectory());

		for (File currFile : mainDataDir.listFiles()) {
			if (currFile.isFile()) {
				File destFile = new File(targetFolderPath + File.separator + currFile.getName());
				copy(currFile, destFile);
			} else if (currFile.isDirectory()) {
				File destDir = new File(targetFolderPath + File.separator + currFile.getName());
				if (!destDir.exists()) {
					if (!destDir.mkdir()) {
						StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
								"Unable to create destination context folder: " + destDir.getAbsolutePath()));
						continue;
					}
				}
				for (File file : currFile.listFiles()) {
					File destFile = new File(destDir, file.getName());
					if (destFile.exists()) {
						destFile.delete();
					}
					copy(file, destFile);
				}
			}
		}
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	private boolean copy(File src, File dst) {
		try {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			return true;
		} catch (IOException ioe) {
			return false;
		}
	}

}
