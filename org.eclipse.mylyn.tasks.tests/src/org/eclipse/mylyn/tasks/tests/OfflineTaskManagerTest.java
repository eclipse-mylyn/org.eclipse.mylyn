/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.tasks.tests;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.internal.tasks.ui.TaskDataManager;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.tests.connector.MockAttributeFactory;
import org.eclipse.mylar.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

import junit.framework.TestCase;

/**
 * @author Rob Elves
 */
public class OfflineTaskManagerTest extends TestCase {

	TaskDataManager offlineTaskDataManager;

	protected void setUp() throws Exception {
		super.setUp();
		offlineTaskDataManager = TasksUiPlugin.getDefault().getTaskDataManager();
		offlineTaskDataManager.clear();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		if (offlineTaskDataManager != null) {
			offlineTaskDataManager.clear();
			offlineTaskDataManager.save();
		}
	}

	public void testAdd() throws CoreException {
		RepositoryTaskData taskData = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, "1");
		offlineTaskDataManager.put(taskData);
		assertNotNull(offlineTaskDataManager.getTaskData(MockRepositoryConnector.REPOSITORY_URL, "1"));
	}

	public void testSave() throws CoreException, IOException, ClassNotFoundException {
		RepositoryTaskData taskData = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, "1");
		offlineTaskDataManager.put(taskData);
		taskData = new RepositoryTaskData(new MockAttributeFactory(), MockRepositoryConnector.REPOSITORY_KIND,
				MockRepositoryConnector.REPOSITORY_URL, "2");
		offlineTaskDataManager.put(taskData);
		assertNotNull(offlineTaskDataManager.getTaskData(MockRepositoryConnector.REPOSITORY_URL, "1"));
		assertNotNull(offlineTaskDataManager.getTaskData(MockRepositoryConnector.REPOSITORY_URL, "2"));
		offlineTaskDataManager.save();
		offlineTaskDataManager.clear();
		assertNull(offlineTaskDataManager.getTaskData(MockRepositoryConnector.REPOSITORY_URL, "1"));
		assertNull(offlineTaskDataManager.getTaskData(MockRepositoryConnector.REPOSITORY_URL, "2"));
		offlineTaskDataManager.reloadFromFile();
		assertNotNull(offlineTaskDataManager.getTaskData(MockRepositoryConnector.REPOSITORY_URL, "1"));
		assertNotNull(offlineTaskDataManager.getTaskData(MockRepositoryConnector.REPOSITORY_URL, "2"));
	}

	public void testGetNextOfflineBugId() throws IOException, ClassNotFoundException {
		assertEquals("1", offlineTaskDataManager.getNextLocalTaskId());
		assertEquals("2", offlineTaskDataManager.getNextLocalTaskId());
		offlineTaskDataManager.save();
		offlineTaskDataManager.clear();
		offlineTaskDataManager.reloadFromFile();
		assertEquals("3", offlineTaskDataManager.getNextLocalTaskId());
	}

	public void testGetTaskData() throws CoreException, IOException, ClassNotFoundException {
		RepositoryTaskData taskData = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, "1");
		taskData.setNewComment("version 1");
		offlineTaskDataManager.put(taskData);
		assertNull(offlineTaskDataManager.getOldTaskData(MockRepositoryConnector.REPOSITORY_URL, "1"));
		taskData = new RepositoryTaskData(new MockAttributeFactory(), MockRepositoryConnector.REPOSITORY_KIND,
				MockRepositoryConnector.REPOSITORY_URL, "1");
		taskData.setNewComment("version 2");
		offlineTaskDataManager.put(taskData);
		offlineTaskDataManager.save();
		offlineTaskDataManager.clear();
		offlineTaskDataManager.reloadFromFile();
		assertEquals("version 2", offlineTaskDataManager.getTaskData(MockRepositoryConnector.REPOSITORY_URL, "1")
				.getNewComment());
		assertEquals("version 1", offlineTaskDataManager.getOldTaskData(MockRepositoryConnector.REPOSITORY_URL, "1")
				.getNewComment());
	}

	public void testRemoveRepositoryTaskData() throws CoreException, IOException, ClassNotFoundException {
		RepositoryTaskData taskData = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, "1");
		offlineTaskDataManager.put(taskData);
		taskData = new RepositoryTaskData(new MockAttributeFactory(), MockRepositoryConnector.REPOSITORY_KIND,
				MockRepositoryConnector.REPOSITORY_URL, "2");
		offlineTaskDataManager.put(taskData);
		offlineTaskDataManager.save();
		assertNotNull(offlineTaskDataManager.getTaskData(MockRepositoryConnector.REPOSITORY_URL, "1"));
		assertNotNull(offlineTaskDataManager.getTaskData(MockRepositoryConnector.REPOSITORY_URL, "2"));
		offlineTaskDataManager.remove(taskData);
		assertNotNull(offlineTaskDataManager.getTaskData(MockRepositoryConnector.REPOSITORY_URL, "1"));
		assertNull(offlineTaskDataManager.getTaskData(MockRepositoryConnector.REPOSITORY_URL, "2"));
		offlineTaskDataManager.save();
		offlineTaskDataManager.clear();
		offlineTaskDataManager.reloadFromFile();
		assertNotNull(offlineTaskDataManager.getTaskData(MockRepositoryConnector.REPOSITORY_URL, "1"));
		assertNull(offlineTaskDataManager.getTaskData(MockRepositoryConnector.REPOSITORY_URL, "2"));
	}

	public void testRemoveListOfRepositoryTaskData() throws CoreException, IOException, ClassNotFoundException {
		RepositoryTaskData taskData1 = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, "1");
		offlineTaskDataManager.put(taskData1);
		RepositoryTaskData taskData2 = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, "2");
		offlineTaskDataManager.put(taskData2);
		offlineTaskDataManager.save();
		assertNotNull(offlineTaskDataManager.getTaskData(MockRepositoryConnector.REPOSITORY_URL, "1"));
		assertNotNull(offlineTaskDataManager.getTaskData(MockRepositoryConnector.REPOSITORY_URL, "2"));
		ArrayList<RepositoryTaskData> list = new ArrayList<RepositoryTaskData>();
		list.add(taskData1);
		list.add(taskData2);
		offlineTaskDataManager.remove(list);
		assertNull(offlineTaskDataManager.getTaskData(MockRepositoryConnector.REPOSITORY_URL, "1"));
		assertNull(offlineTaskDataManager.getTaskData(MockRepositoryConnector.REPOSITORY_URL, "2"));
		offlineTaskDataManager.save();
		offlineTaskDataManager.clear();
		offlineTaskDataManager.reloadFromFile();
		assertNull(offlineTaskDataManager.getTaskData(MockRepositoryConnector.REPOSITORY_URL, "1"));
		assertNull(offlineTaskDataManager.getTaskData(MockRepositoryConnector.REPOSITORY_URL, "2"));
	}

}
