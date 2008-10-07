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
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;

/**
 * Tests changes to the main data directory location.
 * 
 * @author Wesley Coelho
 * @author Mik Kersten (rewrites)
 */
public class ChangeDataDirTest extends TestCase {

	private String newDataDir;

	private String defaultDir;

	private TaskList taskList;

	@Override
	protected void setUp() throws Exception {
		defaultDir = TasksUiPlugin.getDefault().getDefaultDataDirectory();

		newDataDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + '/'
				+ ChangeDataDirTest.class.getSimpleName();
		File dir = new File(newDataDir);

		dir.mkdir();
		dir.deleteOnExit();

		taskList = TasksUiPlugin.getTaskList();

		TaskTestUtil.resetTaskList();
	}

	@Override
	protected void tearDown() throws Exception {
		TaskTestUtil.resetTaskList();
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
		taskList.addTask(task, taskList.getUnmatchedContainer(LocalRepositoryConnector.REPOSITORY_URL));

		ITask readTaskBeforeMove = taskList.getTask(handle);
		assertNotNull(readTaskBeforeMove);
		assertTrue(taskList.getAllTasks().size() > 0);
		copyDataDirContentsTo(newDataDir);
		TasksUiPlugin.getDefault().setDataDirectory(newDataDir, new NullProgressMonitor());
		assertTrue(taskList.getAllTasks().size() > 0);
		ITask readTaskAfterMove = taskList.getTask(handle);

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
