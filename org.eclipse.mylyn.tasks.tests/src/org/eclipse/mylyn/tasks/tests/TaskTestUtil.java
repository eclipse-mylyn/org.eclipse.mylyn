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
import java.io.IOException;
import java.net.URL;

import junit.framework.Assert;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskAttachment;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.tests.util.TestFixture;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ViewIntroAdapterPart;

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
	 * Clears tasks and repositories. When this method returns only the local task repository will exist and the task
	 * list will only have default categories but no tasks.
	 */
	public static void resetTaskListAndRepositories() throws Exception {
		TestFixture.resetTaskListAndRepositories();
	}

	/**
	 * Clears all tasks.
	 */
	public static void resetTaskList() throws Exception {
		TestFixture.resetTaskList();
	}

	/**
	 * @see #resetTaskList()
	 */
	public static void saveAndReadTasklist() throws Exception {
		TestFixture.saveAndReadTasklist();
	}

	public static void saveNow() throws Exception {
		TestFixture.saveNow();
	}

	public static TaskRepository createMockRepository() {
		return new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, MockRepositoryConnector.REPOSITORY_URL);
	}

	public static TaskTask createMockTask(String taskId) {
		return new TaskTask(MockRepositoryConnector.CONNECTOR_KIND, MockRepositoryConnector.REPOSITORY_URL, taskId);
	}

	public static RepositoryQuery createMockQuery(String queryId) {
		return new RepositoryQuery(MockRepositoryConnector.CONNECTOR_KIND, queryId);
	}

	public static TaskListView openTasksViewInActivePerspective() throws Exception {
		IWorkbenchPart activePart = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.getActivePart();
		if (activePart instanceof ViewIntroAdapterPart) {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView((IViewPart) activePart);
		}
		TaskListView taskListView = (TaskListView) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.showView(ITasksUiConstants.ID_VIEW_TASKS);
		Assert.assertSame("Failed to make task list view active", PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.getActivePart(), taskListView);
		return taskListView;
	}

	public static void addAndSelectTask(ITask task) throws Exception {
		TasksUiPlugin.getTaskList().addTask(task);
		TaskListView taskListView = TaskTestUtil.openTasksViewInActivePerspective();
		taskListView.refresh();
		taskListView.getViewer().expandAll();
		taskListView.getViewer().setSelection(new StructuredSelection(task), true);
		Assert.assertSame("Failed to select task", task, taskListView.getSelectedTask());
	}

	public static TaskData createTaskData(TaskRepository taskRepository, String taskId) {
		return new TaskData(new TaskAttributeMapper(taskRepository), taskRepository.getConnectorKind(),
				taskRepository.getRepositoryUrl(), taskId);
	}

	public static TaskData createMockTaskData(String taskId) {
		return createTaskData(createMockRepository(), taskId);
	}

	public static TaskAttachment createMockTaskAttachment(String taskId) {
		return new TaskAttachment(createMockRepository(), createMockTask(taskId), createMockTaskData(taskId).getRoot());
	}

}
