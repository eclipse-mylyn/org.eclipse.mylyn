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

import junit.framework.TestCase;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class TaskDataManagerTest extends TestCase {

//	public void testHasIncomingDateComparison() {
//		final Stack<Date> dates = new Stack<Date>();
//		MockTask task = new MockTask(MOCCK_ID);
//		RepositoryTaskData taskData = new RepositoryTaskData(new MockAttributeFactory() {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public Date getDateForAttributeType(String attributeKey, String dateString) {
//				return dates.pop();
//			}
//		}, connector.getConnectorKind(), MockRepositoryConnector.REPOSITORY_URL, MOCCK_ID);
//		task.setLastReadTimeStamp("never");
//
//		assertTrue(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));
//
//		// strings and dates mismatch
//		dates.push(new Date(1));
//		dates.push(new Date(2));
//		taskData.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, "2006-06-21 15:29:39");
//		assertTrue(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));
//
//		dates.push(null);
//		dates.push(new Date(2));
//		assertTrue(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));
//
//		dates.push(new Date());
//		dates.push(null);
//		assertTrue(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));
//
//		// strings mismatch but dates match
//		dates.push(new Date(1));
//		dates.push(new Date(1));
//		assertFalse(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));
//
//		// strings match, dates should not be checked
//		task.setLastReadTimeStamp("2006-06-21 15:29:39");
//		assertFalse(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));
//	}

//	public void testIncomingToIncoming() {
	/*
	 * - Synchronize task with incoming changes - Make another change using
	 * browser - Open report in tasklist (editor opens with old 'new' data
	 * and background sync occurs) - Incoming status set again on task due
	 * to 2nd occurrence of new incoming data
	 */

//		// Test unforced
//		AbstractTask task = primeTaskAndRepository(SynchronizationState.INCOMING, SynchronizationState.INCOMING);
//		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(),
//				task.getTaskId());
//		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
//		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());
//		assertEquals(DATE_STAMP_2, newData.getLastModified());
//		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, false);
//		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
//		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());
//		// TasksUiPlugin.getSynchronizationManager().setTaskRead(task, true);
//		// assertEquals(DATE_STAMP_2, task.getLastSyncDateStamp());
//		// and again...
//
//		RepositoryTaskData taskData3 = new RepositoryTaskData(new MockAttributeFactory(), connector.getConnectorKind(),
//				MockRepositoryConnector.REPOSITORY_URL, "1");
//		taskData3.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, DATE_STAMP_3);
//		TasksUiPlugin.getTaskDataManager().saveIncoming(task, taskData3, false);
//		// last modified stamp not updated until user synchronizes (newdata ==
//		// olddata)
//		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());
//		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
//		taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
//		assertEquals(DATE_STAMP_3, taskData.getLastModified());
//
//		// Should keep INCOMING state state since new data has same date samp
//		// and sych is not forced.
//		TasksUiPlugin.getTaskDataManager().saveIncoming(task, taskData3, false);
//		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());
//		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
//		taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
//		assertEquals(DATE_STAMP_3, taskData.getLastModified());
//	}
	// public void testIncomingToSynchronizedWithVoidSyncTime() {
	// // IF the last sync time (modified timestamp on task) is null, this can
	// result
	// // in the editor refresh/repoen going into an infinite loops since the
	// task never
	// // gets to a synchronized state if the last mod time isn't set. It is now
	// being set
	// // if found to be null.
	// AbstractTask task =
	// primeTaskAndRepository(SynchronizationState.INCOMING,
	// SynchronizationState.SYNCHRONIZED);
	// assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
	// task.setLastSyncDateStamp(null);
	// TasksUiPlugin.getSynchronizationManager().updateOfflineState(task,
	// newData, false);
	// assertEquals(SynchronizationState.INCOMING, task.getSyncState());
	// assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
	//		
	// TasksUiPlugin.getSynchronizationManager().updateOfflineState(task,
	// newData, false);
	// assertEquals(SynchronizationState.SYNCHRONIZED, task.getSyncState());
	// assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
	// }
//	public void testMarkRead() {
//		AbstractTask task = primeTaskAndRepository(SynchronizationState.INCOMING, SynchronizationState.SYNCHRONIZED);
//		task.setLastReadTimeStamp(null);
//		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());
//		TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
//		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
//		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
//	}
//
//	public void testMarkUnread() {
//		AbstractTask task = primeTaskAndRepository(SynchronizationState.SYNCHRONIZED, SynchronizationState.SYNCHRONIZED);
//		task.setLastReadTimeStamp(null);
//		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
//		TasksUiPlugin.getTaskDataManager().setTaskRead(task, false);
//		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());
//	}
//	public void testClearOutgoing() {
//		AbstractTask task = primeTaskAndRepository(SynchronizationState.SYNCHRONIZED, SynchronizationState.SYNCHRONIZED);
//		RepositoryTaskData taskData1 = new RepositoryTaskData(new MockAttributeFactory(),
//				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, "1");
//		TasksUiPlugin.getTaskDataStorageManager().setNewTaskData(taskData1);
//		taskData1 = TasksUiPlugin.getTaskDataStorageManager()
//				.getEditableCopy(task.getRepositoryUrl(), task.getTaskId());
//
//		taskData1.setNewComment("Testing");
//		Set<RepositoryTaskAttribute> edits = new HashSet<RepositoryTaskAttribute>();
//		edits.add(taskData1.getAttribute(RepositoryTaskAttribute.COMMENT_NEW));
//		TasksUiPlugin.getTaskDataStorageManager().saveEdits(task.getRepositoryUrl(), task.getTaskId(), edits);
//
//		RepositoryTaskData editedData = TasksUiPlugin.getTaskDataStorageManager().getEditableCopy(
//				task.getRepositoryUrl(), task.getTaskId());
//		assertEquals("Testing", editedData.getNewComment());
//
//		TasksUiPlugin.getTaskDataManager().discardOutgoing(task);
//
//		assertTrue(task.getSynchronizationState().equals(SynchronizationState.SYNCHRONIZED));
//		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getEditableCopy(
//				task.getRepositoryUrl(), task.getTaskId());
//		assertEquals("", taskData.getNewComment());
//
//	}
//
//	public void testMarkTaskRead() {
//		// TODO reimplement
//		String repositoryUrl = "http://mylyn.eclipse.org/bugs222";
//		MockTask task1 = new MockTask(repositoryUrl, "1");
//		MockTask task2 = new MockTask(repositoryUrl, "2");
//		task1.setSynchronizationState(SynchronizationState.INCOMING);
//		task2.setSynchronizationState(SynchronizationState.INCOMING);
//		List<ITaskElement> elements = new ArrayList<ITaskElement>();
//		elements.add(task1);
//		elements.add(task2);
//		MarkTaskReadAction readAction = new MarkTaskReadAction(elements);
//		readAction.run();
//		assertEquals(SynchronizationState.SYNCHRONIZED, task1.getSynchronizationState());
//		assertEquals(SynchronizationState.SYNCHRONIZED, task2.getSynchronizationState());
//
//		taskList.reset();
//		MockTask hit1 = new MockTask("1");
//		MockTask hit2 = new MockTask("2");
//		MockRepositoryQuery query = new MockRepositoryQuery("summary");
//		taskList.addQuery(query);
//		taskList.addTask(hit1, query);
//		taskList.addTask(hit2, query);
//
//		elements.clear();
//		elements.add(query);
//		readAction = new MarkTaskReadAction(elements);
//		readAction.run();
//		assertEquals(2, query.getChildren().size());
//		for (ITaskElement element : query.getChildren()) {
//			if (element instanceof MockTask) {
//				MockTask mockTask = (MockTask) element;
//				assertEquals(SynchronizationState.SYNCHRONIZED, mockTask.getSynchronizationState());
//			}
//		}
//	}
//	public void testMarkUnRead() {
	// TODO reimplement
//		String repositoryUrl = "http://mylyn.eclipse.org/bugs222";
//		MockTask task1 = new MockTask(repositoryUrl, "1");
//		MockTask task2 = new MockTask(repositoryUrl, "2");
//		assertEquals(SynchronizationState.SYNCHRONIZED, task1.getSynchronizationState());
//		assertEquals(SynchronizationState.SYNCHRONIZED, task2.getSynchronizationState());
//		List<ITaskElement> elements = new ArrayList<ITaskElement>();
//		elements.add(task1);
//		elements.add(task2);
//		MarkTaskUnreadAction unreadAction = new MarkTaskUnreadAction(elements);
//		unreadAction.run();
//		assertEquals(SynchronizationState.INCOMING, task1.getSynchronizationState());
//		assertEquals(SynchronizationState.INCOMING, task2.getSynchronizationState());
//
//		taskList.reset();
//		MockTask hit1 = new MockTask("1");
//		MockTask hit2 = new MockTask("2");
//		MockRepositoryQuery query = new MockRepositoryQuery("summary");
//		taskList.addQuery(query);
//		taskList.addTask(hit1, query);
//		taskList.addTask(hit2, query);
//
//		elements.clear();
//		elements.add(query);
//		MarkTaskReadAction readAction = new MarkTaskReadAction(elements);
//		readAction.run();
//		assertEquals(2, query.getChildren().size());
//		for (ITaskElement element : query.getChildren()) {
//			if (element instanceof MockTask) {
//				MockTask mockTask = (MockTask) element;
//				assertEquals(SynchronizationState.SYNCHRONIZED, mockTask.getSynchronizationState());
//			} else {
//				fail();
//			}
//		}
//
//		unreadAction = new MarkTaskUnreadAction(elements);
//		unreadAction.run();
//		assertEquals(2, query.getChildren().size());
//		for (ITaskElement element : query.getChildren()) {
//			if (element instanceof MockTask) {
//				MockTask mockTask = (MockTask) element;
//				assertEquals(SynchronizationState.INCOMING, mockTask.getSynchronizationState());
//			} else {
//				fail();
//			}
//		}
//	}
//	public void testQueryHitsNotDropped() {
//		MockTask task1 = new MockTask("1");
//		MockTask task2 = new MockTask("2");
//		task1.setLastReadTimeStamp("today");
//		task2.setLastReadTimeStamp("today");
//		MockRepositoryQuery query = new MockRepositoryQuery("summary");
//		taskList.addQuery(query);
//		taskList.addTask(task1, query);
//		taskList.addTask(task2, query);
//		//assertEquals(0, taskList.getArchiveContainer().getChildren().size());
//		assertEquals(2, query.getChildren().size());
//		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
//				MockRepositoryConnector.REPOSITORY_URL);
//		Set<RepositoryQuery> queries = new HashSet<RepositoryQuery>();
//		queries.add(query);
//		TasksUiInternal.synchronizeQueries(new MockRepositoryConnector(), repository, queries, null, true);
//		//assertEquals(2, taskList.getArchiveContainer().getChildren().size());
//		assertEquals(0, query.getChildren().size());
//	}
}
