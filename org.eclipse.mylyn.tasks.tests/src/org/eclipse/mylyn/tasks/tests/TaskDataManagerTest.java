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
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylar.internal.tasks.core.TaskDataManager;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.tests.connector.MockAttributeFactory;
import org.eclipse.mylar.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Rob Elves
 */
public class TaskDataManagerTest extends TestCase {

	TaskDataManager offlineTaskDataManager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		offlineTaskDataManager = TasksUiPlugin.getDefault().getTaskDataManager();
		offlineTaskDataManager.clear();
	}

	@Override
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
		offlineTaskDataManager.push(RepositoryTaskHandleUtil.getHandle(MockRepositoryConnector.REPOSITORY_URL, "1"),
				taskData);
		assertNotNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "1")));
	}

	public void testSave() throws CoreException, IOException, ClassNotFoundException {
		RepositoryTaskData taskData = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, "1");
		offlineTaskDataManager.push(RepositoryTaskHandleUtil.getHandle(MockRepositoryConnector.REPOSITORY_URL, "1"),
				taskData);
		taskData = new RepositoryTaskData(new MockAttributeFactory(), MockRepositoryConnector.REPOSITORY_KIND,
				MockRepositoryConnector.REPOSITORY_URL, "2");
		offlineTaskDataManager.push(RepositoryTaskHandleUtil.getHandle(MockRepositoryConnector.REPOSITORY_URL, "2"),
				taskData);
		assertNotNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "1")));
		assertNotNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "2")));
		offlineTaskDataManager.save();
		offlineTaskDataManager.clear();
		assertNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "1")));
		assertNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "2")));
		offlineTaskDataManager.readOfflineData();
		assertNotNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "1")));
		assertNotNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "2")));
	}

	public void testGetNextOfflineBugId() throws IOException, ClassNotFoundException {
		assertEquals("1", offlineTaskDataManager.getNewRepositoryTaskId());
		assertEquals("2", offlineTaskDataManager.getNewRepositoryTaskId());
		offlineTaskDataManager.save();
		offlineTaskDataManager.clear();
		offlineTaskDataManager.readOfflineData();
		assertEquals("3", offlineTaskDataManager.getNewRepositoryTaskId());
	}

	public void testGetTaskData() throws CoreException, IOException, ClassNotFoundException {
		RepositoryTaskData taskData = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, "1");
		taskData.setNewComment("version 1");
		offlineTaskDataManager.push(RepositoryTaskHandleUtil.getHandle(MockRepositoryConnector.REPOSITORY_URL, "1"),
				taskData);
		assertNotNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "1")));
		taskData = new RepositoryTaskData(new MockAttributeFactory(), MockRepositoryConnector.REPOSITORY_KIND,
				MockRepositoryConnector.REPOSITORY_URL, "1");
		taskData.setNewComment("version 2");
		offlineTaskDataManager.push(RepositoryTaskHandleUtil.getHandle(MockRepositoryConnector.REPOSITORY_URL, "1"),
				taskData);
		offlineTaskDataManager.save();
		offlineTaskDataManager.clear();
		offlineTaskDataManager.readOfflineData();
		assertEquals("version 2", offlineTaskDataManager.getRepositoryTaskData(
				RepositoryTaskHandleUtil.getHandle(MockRepositoryConnector.REPOSITORY_URL, "1")).getNewComment());
		assertEquals("version 1", offlineTaskDataManager.getOldRepositoryTaskData(
				RepositoryTaskHandleUtil.getHandle(MockRepositoryConnector.REPOSITORY_URL, "1")).getNewComment());
	}

	public void testUniqueCopy() throws Exception {
		RepositoryTaskData taskData = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, "1");
		offlineTaskDataManager.push(RepositoryTaskHandleUtil.getHandle(MockRepositoryConnector.REPOSITORY_URL, "1"),
				taskData);
		RepositoryTaskData taskData2 = offlineTaskDataManager.getEditableCopy(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "1"));
		assertNotNull(taskData2);
		taskData2.setNewComment("test");
		taskData = null;
		taskData = offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "1"));
		assertFalse(taskData.getNewComment().equals("test"));
		taskData = null;
		taskData = offlineTaskDataManager.getOldRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "1"));
		assertFalse(taskData.getNewComment().equals("test"));
	}

	public void testRemoveRepositoryTaskData() throws CoreException, IOException, ClassNotFoundException {
		RepositoryTaskData taskData = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, "1");
		offlineTaskDataManager.push(RepositoryTaskHandleUtil.getHandle(MockRepositoryConnector.REPOSITORY_URL, "1"),
				taskData);
		taskData = new RepositoryTaskData(new MockAttributeFactory(), MockRepositoryConnector.REPOSITORY_KIND,
				MockRepositoryConnector.REPOSITORY_URL, "2");
		offlineTaskDataManager.push(RepositoryTaskHandleUtil.getHandle(MockRepositoryConnector.REPOSITORY_URL, "2"),
				taskData);
		offlineTaskDataManager.save();
		assertNotNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "1")));
		assertNotNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "2")));
		offlineTaskDataManager.remove(RepositoryTaskHandleUtil.getHandle(MockRepositoryConnector.REPOSITORY_URL, "2"));
		assertNotNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "1")));
		assertNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "2")));
		offlineTaskDataManager.save();
		offlineTaskDataManager.clear();
		offlineTaskDataManager.readOfflineData();
		assertNotNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "1")));
		assertNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "2")));
	}

	public void testRemoveListOfRepositoryTaskData() throws CoreException, IOException, ClassNotFoundException {
		RepositoryTaskData taskData1 = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, "1");
		offlineTaskDataManager.push(RepositoryTaskHandleUtil.getHandle(MockRepositoryConnector.REPOSITORY_URL, "1"),
				taskData1);
		RepositoryTaskData taskData2 = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, "2");
		offlineTaskDataManager.push(RepositoryTaskHandleUtil.getHandle(MockRepositoryConnector.REPOSITORY_URL, "2"),
				taskData2);
		offlineTaskDataManager.save();
		assertNotNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "1")));
		assertNotNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "2")));
		ArrayList<String> list = new ArrayList<String>();
		list.add(RepositoryTaskHandleUtil.getHandle(MockRepositoryConnector.REPOSITORY_URL, "1"));
		list.add(RepositoryTaskHandleUtil.getHandle(MockRepositoryConnector.REPOSITORY_URL, "2"));
		offlineTaskDataManager.remove(list);
		assertNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "1")));
		assertNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "2")));
		offlineTaskDataManager.save();
		offlineTaskDataManager.clear();
		offlineTaskDataManager.readOfflineData();
		assertNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "1")));
		assertNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "2")));
	}

	public void testEditing() {
		RepositoryTaskData taskData = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, "1");
		offlineTaskDataManager.push(RepositoryTaskHandleUtil.getHandle(MockRepositoryConnector.REPOSITORY_URL, "1"),
				taskData);

		assertNotNull(offlineTaskDataManager.getRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "1")));
		assertNotNull(offlineTaskDataManager.getOldRepositoryTaskData(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "1")));

		RepositoryTaskData editData = offlineTaskDataManager.getEditableCopy(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "1"));
		assertNotNull(editData);
		editData.setAttributeFactory(new MockAttributeFactory());
		editData.setAttributeValue(RepositoryTaskAttribute.COMMENT_NEW, "new comment");
		// for (RepositoryTaskAttribute attribute: editData.getAttributes()) {
		// assertTrue(taskData.getAttribute(attribute.getID()).equals(attribute));
		// }

		Set<RepositoryTaskAttribute> attSave = new HashSet<RepositoryTaskAttribute>();
		attSave.add(editData.getAttribute(RepositoryTaskAttribute.COMMENT_NEW));
		offlineTaskDataManager.saveEdits(RepositoryTaskHandleUtil.getHandle(MockRepositoryConnector.REPOSITORY_URL, "1"),
				attSave);

		editData = null;
		editData = offlineTaskDataManager.getEditableCopy(RepositoryTaskHandleUtil.getHandle(
				MockRepositoryConnector.REPOSITORY_URL, "1"));
		assertNotNull(editData);
		assertEquals("new comment", editData.getAttributeValue(RepositoryTaskAttribute.COMMENT_NEW));

	}

	// /** DND
	// * As is will write 81481326 bytes.
	// *
	// * @throws Exception
	// */
	// public void testLargeDataSet() throws Exception {
	// RepositoryTaskData taskData;
	// for (int x = 1; x < 500; x++) {
	// taskData = new RepositoryTaskData(new MockAttributeFactory(),
	// MockRepositoryConnector.REPOSITORY_KIND,
	// MockRepositoryConnector.REPOSITORY_URL, "" + x);
	//
	// for (int y = 1; y < 60; y++) {
	// RepositoryTaskAttribute attribute = new RepositoryTaskAttribute("" + y,
	// "" + y, false);
	// for (int z = 1; z < 10; z++) {
	// attribute.addOption("" + z, "" + z);
	// attribute.addValue("" + z);
	// }
	// taskData.addAttribute("" + y, attribute);
	// }
	//
	// for (int y = 1; y < 5; y++) {
	// RepositoryOperation op = new RepositoryOperation("" + y, "" + y);
	// taskData.addOperation(op);
	// }
	//
	// try {
	// for (int y = 1; y < 1000; y++) {
	// TaskComment comment = new TaskComment(new MockAttributeFactory(), y);
	// comment.setAttributeValue(RepositoryTaskAttribute.COMMENT_TEXT, "Testing
	// \u05D0");
	// taskData.addComment(comment);
	// }
	// } catch (StackOverflowError e) {
	// e.printStackTrace();
	// }
	//
	// // for(int y = 1; y < 1000; y++) {
	// // RepositoryAttachment attachment = new
	// // RepositoryAttachment(repository, new MockAttributeFactory());
	// // taskData.addAttachment(attachment);
	// // }
	//
	// offlineTaskDataManager.put(taskData);
	// offlineTaskDataManager.put(taskData);
	// }
	// System.err.println("done Building");
	// offlineTaskDataManager.save();
	// System.err.println("Saved");
	// File file =
	// TasksUiPlugin.getDefault().getOfflineReportsFilePath().toFile();
	// System.err.println(file.length());
	// offlineTaskDataManager.clear();
	// offlineTaskDataManager.readOfflineData();
	// assertNotNull(offlineTaskDataManager.getOldTaskData(AbstractRepositoryTask.getHandle(
	// MockRepositoryConnector.REPOSITORY_URL, 400)));
	// }
}
