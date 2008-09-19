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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;

/**
 * @author Mik Kersten
 */
public class TaskTestUtil {

	public static File getLocalFile(String path) {
		try {
			URL installURL = TasksTestsPlugin.getDefault().getBundle().getEntry(path);
			URL localURL = FileLocator.toFileURL(installURL);
			return new File(localURL.getFile());
		} catch (IOException e) {
			return null;
		}
	}

	public static File getFile(String path) throws IOException {
		if (TasksTestsPlugin.getDefault() != null) {
			URL installURL = TasksTestsPlugin.getDefault().getBundle().getEntry(path);
			URL localURL = FileLocator.toFileURL(installURL);
			return new File(localURL.getFile());
		} else {
			URL localURL = TaskTestUtil.class.getResource("");
			return new File(localURL.getFile() + "../../../../../../" + path);
		}
	}

	/**
	 * Adaptred from Java Developers' almanac
	 */
	public static void copy(File source, File dest) throws IOException {
		InputStream in = new FileInputStream(source);
		OutputStream out = new FileOutputStream(dest);
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	/**
	 * Clears tasks and repositories. When this method returns only the local task repository will exist and the task
	 * list will only have default categories but no tasks.
	 */
	@SuppressWarnings("deprecation")
	public static void resetTaskListAndRepositories() throws Exception {
		TasksUiPlugin.getRepositoryManager().clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		TasksUiPlugin.getDefault().getLocalTaskRepository();
		TasksUiPlugin.getTaskListManager().resetTaskList();
	}

	/**
	 * Clears all tasks.
	 */
	@SuppressWarnings("deprecation")
	public static void resetTaskList() throws Exception {
		TasksUiPlugin.getTaskListManager().resetTaskList();
	}

	/**
	 * @see #resetTaskList()
	 */
	@SuppressWarnings("deprecation")
	public static void saveAndReadTasklist() throws Exception {
		TasksUiPlugin.getTaskList().notifyElementsChanged(null);
		TasksUiPlugin.getTaskListManager().saveTaskList();
		TasksUiPlugin.getTaskListManager().resetTaskList();
		TasksUiPlugin.getDefault().reloadDataDirectory();
	}

	public static TaskTask createMockTask(String taskId) {
		return new TaskTask(MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, taskId);
	}

	public static RepositoryQuery createMockQuery(String queryId) {
		return new RepositoryQuery(MockRepositoryConnector.REPOSITORY_KIND, queryId);
	}

}
