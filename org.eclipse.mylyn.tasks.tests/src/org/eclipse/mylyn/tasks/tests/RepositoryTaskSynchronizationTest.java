/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.SynchronizationState;
import org.eclipse.mylyn.tasks.tests.connector.MockAttributeFactory;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;

/**
 * @author Rob Elves
 */
public class RepositoryTaskSynchronizationTest extends TestCase {

	private static final String DATE_STAMP_3 = "2006-06-21 15:29:42";

	private static final String DATE_STAMP_2 = "2006-06-21 15:29:41";

	private static final String DATE_STAMP_1 = "2006-06-21 15:29:40";

	private static final String MOCCK_ID = "1";

	//private static final String URL1 = "http://www.eclipse.org/mylar";

	private final TestRepositoryConnector connector = new TestRepositoryConnector();

	private final TestOfflineTaskHandler handler = new TestOfflineTaskHandler();

	private RepositoryTaskData newData;

	public void testHasIncoming() {
		AbstractTask task = new MockTask(MOCCK_ID);
		RepositoryTaskData taskData = new RepositoryTaskData(new MockAttributeFactory(), connector.getConnectorKind(),
				MockRepositoryConnector.REPOSITORY_URL, MOCCK_ID);
		task.setLastReadTimeStamp("never");

		assertTrue(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));
		taskData.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, "2006-06-21 15:29:39");
		assertTrue(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));
		taskData.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, DATE_STAMP_1);
		assertTrue(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));
		task.setLastReadTimeStamp("2006-06-21 15:29:39");
		assertTrue(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));
		task.setLastReadTimeStamp(DATE_STAMP_1);
		assertFalse(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));
	}

	public void testHasIncomingDateComparison() {
		final Stack<Date> dates = new Stack<Date>();
		AbstractTask task = new MockTask(MOCCK_ID);
		RepositoryTaskData taskData = new RepositoryTaskData(new MockAttributeFactory() {
			private static final long serialVersionUID = 1L;

			@Override
			public Date getDateForAttributeType(String attributeKey, String dateString) {
				return dates.pop();
			}
		}, connector.getConnectorKind(), MockRepositoryConnector.REPOSITORY_URL, MOCCK_ID);
		task.setLastReadTimeStamp("never");

		assertTrue(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));

		// strings and dates mismatch
		dates.push(new Date(1));
		dates.push(new Date(2));
		taskData.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, "2006-06-21 15:29:39");
		assertTrue(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));

		dates.push(null);
		dates.push(new Date(2));
		assertTrue(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));

		dates.push(new Date());
		dates.push(null);
		assertTrue(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));

		// strings mismatch but dates match
		dates.push(new Date(1));
		dates.push(new Date(1));
		assertFalse(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));

		// strings match, dates should not be checked
		task.setLastReadTimeStamp("2006-06-21 15:29:39");
		assertFalse(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));
	}

	public void testIncomingToIncoming() {
		/*
		 * - Synchronize task with incoming changes - Make another change using
		 * browser - Open report in tasklist (editor opens with old 'new' data
		 * and background sync occurs) - Incoming status set again on task due
		 * to 2nd occurrence of new incoming data
		 */

		// Test unforced
		AbstractTask task = primeTaskAndRepository(SynchronizationState.INCOMING, SynchronizationState.INCOMING);
		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(),
				task.getTaskId());
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());
		assertEquals(DATE_STAMP_2, newData.getLastModified());
		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, false);
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());
		// TasksUiPlugin.getSynchronizationManager().setTaskRead(task, true);
		// assertEquals(DATE_STAMP_2, task.getLastSyncDateStamp());
		// and again...

		RepositoryTaskData taskData3 = new RepositoryTaskData(new MockAttributeFactory(), connector.getConnectorKind(),
				MockRepositoryConnector.REPOSITORY_URL, "1");
		taskData3.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, DATE_STAMP_3);
		TasksUiPlugin.getTaskDataManager().saveIncoming(task, taskData3, false);
		// last modified stamp not updated until user synchronizes (newdata ==
		// olddata)
		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
		taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
		assertEquals(DATE_STAMP_3, taskData.getLastModified());

		// Should keep INCOMING state state since new data has same date samp
		// and sych is not forced.
		TasksUiPlugin.getTaskDataManager().saveIncoming(task, taskData3, false);
		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
		taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
		assertEquals(DATE_STAMP_3, taskData.getLastModified());
	}

	// Invalid state change. Test that this can't happen.
	public void testIncomingToSynchronized() {
		// When not forced, tasks with incoming state should remain in incoming
		// state if
		// if new data has same date stamp as old data.
		AbstractTask task = primeTaskAndRepository(SynchronizationState.INCOMING,
				SynchronizationState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, false);
		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());

		task = primeTaskAndRepository(SynchronizationState.INCOMING, SynchronizationState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, true);
		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());
		// assertEquals(SynchronizationState.SYNCHRONIZED,
		// task.getSyncState());
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());

		// Test forced with remote incoming
		// Update: bug#163850 - synchronize gets new data but doesn't mark
		// synchronized
		task = primeTaskAndRepository(SynchronizationState.INCOMING, SynchronizationState.INCOMING);
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, true);
		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());

	}

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

	/*
	 * public void testIncomingToConflict() { // invalid }
	 */

	/*
	 * public void testIncomingToOutgoing() { // invalid }
	 */

	public void testSynchronizedToIncoming() {
		AbstractTask task = primeTaskAndRepository(SynchronizationState.SYNCHRONIZED,
				SynchronizationState.INCOMING);
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, false);
		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());
		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(),
				task.getTaskId());
		assertEquals(DATE_STAMP_2, taskData.getLastModified());
		// assertEquals(DATE_STAMP_2, task.getLastModifiedDateStamp());
	}

	public void testSynchronizedToSynchronized() {
		AbstractTask task = primeTaskAndRepository(SynchronizationState.SYNCHRONIZED,
				SynchronizationState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, false);
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
	}

	/*
	 * public void testSynchronizedToConflict() { // invalid }
	 */

	public void testSynchronizedToOutgoing() {
		AbstractTask task = primeTaskAndRepository(SynchronizationState.SYNCHRONIZED,
				SynchronizationState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());

		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getEditableCopy(task.getRepositoryUrl(),
				task.getTaskId());

		taskData.setNewComment("new comment");

		HashSet<RepositoryTaskAttribute> changed = new HashSet<RepositoryTaskAttribute>();
		changed.add(taskData.getAttribute(RepositoryTaskAttribute.COMMENT_NEW));
		TasksUiPlugin.getTaskDataManager().saveOutgoing(task, changed);
		assertEquals(SynchronizationState.OUTGOING, task.getSynchronizationState());
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
	}

	public void testConflictToConflict() {
		AbstractTask task = primeTaskAndRepository(SynchronizationState.CONFLICT, SynchronizationState.INCOMING);
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, true);
		assertEquals(SynchronizationState.CONFLICT, task.getSynchronizationState());
		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(),
				task.getTaskId());

		assertEquals(DATE_STAMP_2, taskData.getLastModified());
		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, true);
		assertEquals(SynchronizationState.CONFLICT, task.getSynchronizationState());
		taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
		assertEquals(DATE_STAMP_2, taskData.getLastModified());
	}

	/*
	 * public void testConflictToSynchonized() { // invalid, requires markRead }
	 */

	/*
	 * public void testConflictToConflict() { // ui involved }
	 */

	/*
	 * public void testConflictToOutgoing() { // invalid? }
	 */

	// TODO: Test merging new incoming with outgoing
	// TODO: Test discard outgoing
	public void testOutgoingToConflict() {
		// Forced
		AbstractTask task = primeTaskAndRepository(SynchronizationState.OUTGOING, SynchronizationState.INCOMING);
		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(),
				task.getTaskId());

		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
		assertEquals(SynchronizationState.OUTGOING, task.getSynchronizationState());
		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, true);
		assertEquals(SynchronizationState.CONFLICT, task.getSynchronizationState());
		taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());

		assertEquals(DATE_STAMP_2, taskData.getLastModified());
		// assertEquals(DATE_STAMP_2, task.getLastModifiedDateStamp());
	}

	// Illegal state change, test it doesn't occur
	public void testOutgoingToSynchronized() {

		AbstractTask task = primeTaskAndRepository(SynchronizationState.OUTGOING,
				SynchronizationState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());

		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, true);
		assertEquals(SynchronizationState.OUTGOING, task.getSynchronizationState());
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
	}

	public void testOutgoingToOutgoing() {
		AbstractTask task = primeTaskAndRepository(SynchronizationState.OUTGOING,
				SynchronizationState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, false);
		assertEquals(SynchronizationState.OUTGOING, task.getSynchronizationState());
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
	}

	public void testMarkRead() {
		AbstractTask task = primeTaskAndRepository(SynchronizationState.INCOMING,
				SynchronizationState.SYNCHRONIZED);
		task.setLastReadTimeStamp(null);
		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());
		TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
	}

	public void testMarkUnread() {
		AbstractTask task = primeTaskAndRepository(SynchronizationState.SYNCHRONIZED,
				SynchronizationState.SYNCHRONIZED);
		task.setLastReadTimeStamp(null);
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
		TasksUiPlugin.getTaskDataManager().setTaskRead(task, false);
		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());
	}

	public void testClearOutgoing() {
		AbstractTask task = primeTaskAndRepository(SynchronizationState.SYNCHRONIZED,
				SynchronizationState.SYNCHRONIZED);
		RepositoryTaskData taskData1 = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, "1");
		TasksUiPlugin.getTaskDataStorageManager().setNewTaskData(taskData1);
		taskData1 = TasksUiPlugin.getTaskDataStorageManager().getEditableCopy(task.getRepositoryUrl(), task.getTaskId());

		taskData1.setNewComment("Testing");
		Set<RepositoryTaskAttribute> edits = new HashSet<RepositoryTaskAttribute>();
		edits.add(taskData1.getAttribute(RepositoryTaskAttribute.COMMENT_NEW));
		TasksUiPlugin.getTaskDataStorageManager().saveEdits(task.getRepositoryUrl(), task.getTaskId(), edits);

		RepositoryTaskData editedData = TasksUiPlugin.getTaskDataStorageManager().getEditableCopy(task.getRepositoryUrl(),
				task.getTaskId());
		assertEquals("Testing", editedData.getNewComment());

		TasksUiPlugin.getTaskDataManager().discardOutgoing(task);

		assertTrue(task.getSynchronizationState().equals(SynchronizationState.SYNCHRONIZED));
		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getEditableCopy(task.getRepositoryUrl(),
				task.getTaskId());
		assertEquals("", taskData.getNewComment());

	}

	private class TestOfflineTaskHandler extends AbstractTaskDataHandler {

		@Override
		public AbstractAttributeFactory getAttributeFactory(String repositoryUrl, String repositoryKind, String taskKind) {
			// ignore
			return null;
		}

		public Set<AbstractTask> getChangedSinceLastSync(TaskRepository repository, Set<AbstractTask> tasks)
				throws CoreException {
			return null;
		}

		@Override
		public RepositoryTaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
				throws CoreException {
			return null;
		}

		@Override
		public String postTaskData(TaskRepository repository, RepositoryTaskData taskData, IProgressMonitor monitor)
				throws CoreException {
			// ignore
			return null;
		}

		@Override
		public boolean initializeTaskData(TaskRepository repository, RepositoryTaskData data, IProgressMonitor monitor)
				throws CoreException {
			// ignore
			return false;
		}

		@Override
		public AbstractAttributeFactory getAttributeFactory(RepositoryTaskData taskData) {
			// ignore
			return null;
		}

		@Override
		public Set<String> getSubTaskIds(RepositoryTaskData taskData) {
			return Collections.emptySet();
		}

		// private final String DATE_FORMAT_2 = "yyyy-MM-dd HH:mm:ss";
		//
		// private final SimpleDateFormat format = new
		// SimpleDateFormat(DATE_FORMAT_2);
		//
		//		
		// public Date getDateForAttributeType(String attributeKey, String
		// dateString) {
		//
		// try {
		// return format.parse(dateString);
		// } catch (ParseException e) {
		// return null;
		// }
		// }

	}

	private class TestRepositoryConnector extends MockRepositoryConnector {

		@Override
		public AbstractTaskDataHandler getTaskDataHandler() {
			return handler;
		}

		// @Override
		// protected void removeOfflineTaskData(RepositoryTaskData bug) {
		// // ignore
		// }
		//
		// @Override
		// public void saveOffline(RepositoryTaskData taskData) {
		// // ignore
		// }

		// @Override
		// protected RepositoryTaskData
		// loadOfflineTaskData(AbstractTask repositoryTask) {
		// return repositoryTask.getTaskData();
		// }

	}

	private AbstractTask primeTaskAndRepository(SynchronizationState localState, SynchronizationState remoteState) {
		RepositoryTaskData taskData = null;
		AbstractTask task = new MockTask(MOCCK_ID);

		taskData = new RepositoryTaskData(new MockAttributeFactory(), connector.getConnectorKind(),
				MockRepositoryConnector.REPOSITORY_URL, MOCCK_ID);
		taskData.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, DATE_STAMP_1);
		task.setLastReadTimeStamp(DATE_STAMP_1);
		task.setSynchronizationState(localState);
		TasksUiPlugin.getTaskDataStorageManager().setNewTaskData(taskData);
		newData = new RepositoryTaskData(new MockAttributeFactory(), connector.getConnectorKind(),
				MockRepositoryConnector.REPOSITORY_URL, MOCCK_ID);

		switch (remoteState) {
		case CONFLICT:
		case INCOMING:
			newData.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, DATE_STAMP_2);
			break;
		case SYNCHRONIZED:
			newData.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, DATE_STAMP_1);
			break;
		default:
			fail("Remote repository can only be INCOMING or SYNCHRONIZED wrt the local task.");

		}

		return task;

	}

}
