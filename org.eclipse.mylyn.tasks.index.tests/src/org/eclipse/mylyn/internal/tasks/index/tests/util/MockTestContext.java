/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.index.tests.util;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.core.DelegatingProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.data.SynchronizationManger;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataStore;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;

/**
 * @author David Green
 */
public class MockTestContext {

	private final TaskList taskList;

	private final TaskRepositoryManager repositoryManager;

	private final TaskDataManager dataManager;

	private final TaskDataStore dataStore;

	private final TaskActivityManager activityManager;

	private final SynchronizationManger synchronizationManger;

	private final RepositoryModel repositoryModel;

	private final TaskRepository mockRepository;

	private final TaskRepository localRepository;

	private final AtomicInteger idSeed = new AtomicInteger(1003);

	private final FullMockRepositoryConnector mockRepositoryConnector;

	public MockTestContext() {
		taskList = new TaskList();
		repositoryManager = new TaskRepositoryManager();

		mockRepositoryConnector = new FullMockRepositoryConnector();
		repositoryManager.addRepositoryConnector(mockRepositoryConnector);
		mockRepository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND,
				MockRepositoryConnector.REPOSITORY_URL);
		repositoryManager.addRepository(mockRepository);

		repositoryManager.addRepositoryConnector(new LocalRepositoryConnector());
		localRepository = new TaskRepository(LocalRepositoryConnector.CONNECTOR_KIND,
				LocalRepositoryConnector.REPOSITORY_URL);
		repositoryManager.addRepository(localRepository);

		dataStore = new TaskDataStore(repositoryManager);
		activityManager = new TaskActivityManager(repositoryManager, taskList);
		repositoryModel = new RepositoryModel(taskList, repositoryManager);
		synchronizationManger = new SynchronizationManger(repositoryModel);
		dataManager = new TaskDataManager(dataStore, repositoryManager, taskList, activityManager,
				synchronizationManger);

	}

	public TaskList getTaskList() {
		return taskList;
	}

	public TaskRepositoryManager getRepositoryManager() {
		return repositoryManager;
	}

	public TaskDataManager getDataManager() {
		return dataManager;
	}

	public TaskDataStore getDataStore() {
		return dataStore;
	}

	public TaskActivityManager getActivityManager() {
		return activityManager;
	}

	public SynchronizationManger getSynchronizationManger() {
		return synchronizationManger;
	}

	public RepositoryModel getRepositoryModel() {
		return repositoryModel;
	}

	public ITask createLocalTask() {
		LocalTask task = new LocalTask(Integer.toString(idSeed.incrementAndGet()), "summary");
		task.setNotes("description " + task.getTaskKey());

		taskList.addTask(task);

		return task;
	}

	public ITask createRepositoryTask() throws CoreException {
		MockTask task = new MockTask(Integer.toString(idSeed.incrementAndGet()));

		TaskData taskData = new TaskData(new TaskAttributeMapper(mockRepository), task.getConnectorKind(),
				task.getRepositoryUrl(), task.getTaskId());

		mockRepositoryConnector.getTaskDataHandler().initializeTaskData(mockRepository, taskData, new TaskMapping(),
				new NullProgressMonitor());

		taskData.getRoot().getMappedAttribute(TaskAttribute.SUMMARY).setValue("summary");
		taskData.getRoot()
				.getMappedAttribute(TaskAttribute.DATE_CREATION)
				.setValue(Long.toString(new Date().getTime()));
		taskData.getRoot().getMappedAttribute(TaskAttribute.USER_REPORTER).setValue("reporter@example.com");
		taskData.getRoot().getMappedAttribute(TaskAttribute.USER_ASSIGNED).setValue("assignee@example.com");
		taskData.getRoot()
				.getMappedAttribute(TaskAttribute.DESCRIPTION)
				.setValue("task description " + task.getTaskKey());

		mockRepositoryConnector.getTaskMapping(taskData).applyTo(task);

		dataManager.putSubmittedTaskData(task, taskData, new DelegatingProgressMonitor());
		taskList.addTask(task);

		return task;
	}

	public TaskRepository getMockRepository() {
		return mockRepository;
	}

	public FullMockRepositoryConnector getMockRepositoryConnector() {
		return mockRepositoryConnector;
	}

	public void refactorMockRepositoryUrl(String newUrl) throws CoreException {
		String oldUrl = getMockRepository().getRepositoryUrl();

		for (ITask task : getTaskList().getAllTasks()) {
			if (oldUrl.equals(task.getAttribute(ITasksCoreConstants.ATTRIBUTE_OUTGOING_NEW_REPOSITORY_URL))) {
				getDataManager().refactorRepositoryUrl(task, task.getRepositoryUrl(), newUrl);
			}
			if (task.getRepositoryUrl().equals(oldUrl)) {
				getDataManager().refactorRepositoryUrl(task, newUrl, newUrl);
			}
		}
		getTaskList().refactorRepositoryUrl(oldUrl, newUrl);
		getMockRepository().setRepositoryUrl(newUrl);
		getRepositoryManager().notifyRepositoryUrlChanged(getMockRepository(), oldUrl);
	}
}
