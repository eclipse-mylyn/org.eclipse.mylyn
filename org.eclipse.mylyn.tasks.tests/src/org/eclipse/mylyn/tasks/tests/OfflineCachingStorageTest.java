/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.TaskDataState;
import org.eclipse.mylyn.internal.tasks.ui.OfflineCachingStorage;
import org.eclipse.mylyn.internal.tasks.ui.OfflineFileStorage;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.tests.connector.MockAttributeFactory;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Rob Elves
 */
public class OfflineCachingStorageTest extends TestCase {

	private static final String MOCK_ID = "1";

	private File dataDir;

	private OfflineCachingStorage storage;

	protected void setUp() throws Exception {
		//dataDir = new File("c:/offline");
		dataDir = new File(TasksUiPlugin.getDefault().getDataDirectory() + '/' + "offline");
		storage = new OfflineCachingStorage(new OfflineFileStorage(dataDir));
		storage.start();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		storage.stop();
		storage.clear();
	}

	public void testPutAndGet() throws Exception {

		TaskDataState state = new TaskDataState(MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);

		RepositoryTaskData newData = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);

		newData.setAttributeValue("attributeKey1", "attValue!");
		newData.setDescription("description");
		newData.setNew(true);
		newData.setNewComment("new comment");
		newData.setSummary("summary");
		newData.setTaskKey("task key");

		RepositoryTaskData oldData = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);

		Set<RepositoryTaskAttribute> edits = new HashSet<RepositoryTaskAttribute>();

		state.setNewTaskData(newData);
		state.setOldTaskData(oldData);
		state.setEdits(edits);

		storage.put(state);

		TaskDataState retrieved = storage.get(MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);
		assertNotNull(retrieved);
		RepositoryTaskData newTaskData = retrieved.getNewTaskData();
		assertNotNull(newTaskData);
		assertEquals(MockRepositoryConnector.REPOSITORY_KIND, newTaskData.getRepositoryKind());
		assertEquals("description", newTaskData.getDescription());
		assertEquals("new comment", newTaskData.getNewComment());
		assertEquals("task key", newTaskData.getTaskKey());
	}

	public void testRemove() throws Exception {
		assertNull(storage.get(MockRepositoryConnector.REPOSITORY_URL, MOCK_ID));

		TaskDataState state = new TaskDataState(MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);

		RepositoryTaskData newData = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);

		newData.setAttributeValue("attributeKey1", "attValue!");
		newData.setDescription("description");
		newData.setNew(true);
		newData.setNewComment("new comment");
		newData.setSummary("summary");
		newData.setTaskKey("task key");

		RepositoryTaskData oldData = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);

		Set<RepositoryTaskAttribute> edits = new HashSet<RepositoryTaskAttribute>();

		state.setNewTaskData(newData);
		state.setOldTaskData(oldData);
		state.setEdits(edits);

		storage.put(state);
		assertEquals(1, storage.getReadQueue().size());
		assertEquals("1", storage.getReadQueue().iterator().next().getId());
		assertNotNull(storage.get(MockRepositoryConnector.REPOSITORY_URL, MOCK_ID));
		storage.remove(MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);
		assertEquals(0, storage.getReadQueue().size());
		assertNull(storage.get(MockRepositoryConnector.REPOSITORY_URL, MOCK_ID));

	}

	public void testLRUQueue() {
		assertNull(storage.get(MockRepositoryConnector.REPOSITORY_URL, MOCK_ID));
		assertEquals(0, storage.getReadQueue().size());
		TaskDataState state1 = new TaskDataState(MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);
		TaskDataState state2 = new TaskDataState(MockRepositoryConnector.REPOSITORY_URL, "2");
		TaskDataState state3 = new TaskDataState(MockRepositoryConnector.REPOSITORY_URL, "3");

		storage.put(state1);
		storage.put(state2);
		storage.put(state3);

		assertEquals(3, storage.getReadQueue().size());

		// should return least recently used
		assertEquals("1", storage.getReadQueue().iterator().next().getId());

		assertNotNull(storage.get(MockRepositoryConnector.REPOSITORY_URL, "3"));
		assertNotNull(storage.get(MockRepositoryConnector.REPOSITORY_URL, "2"));
		assertNotNull(storage.get(MockRepositoryConnector.REPOSITORY_URL, MOCK_ID));

		assertEquals(3, storage.getReadQueue().size());

		// should return least recently used
		assertEquals("3", storage.getReadQueue().iterator().next().getId());
		storage.put(state1);
		storage.put(state3);
		// should return least recently used
		assertEquals("2", storage.getReadQueue().iterator().next().getId());
		assertEquals(3, storage.getReadQueue().size());
	}
}
