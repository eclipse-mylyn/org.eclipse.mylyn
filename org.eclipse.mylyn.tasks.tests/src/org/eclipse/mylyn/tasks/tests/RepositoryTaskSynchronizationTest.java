/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.tasks.tests;

import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.mylar.tasks.tests.connector.MockAttributeFactory;
import org.eclipse.mylar.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylar.tasks.tests.connector.MockRepositoryTask;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Rob Elves
 */
public class RepositoryTaskSynchronizationTest extends TestCase {

	private static final String DATE_STAMP_3 = "2006-06-21 15:29:42";

	private static final String DATE_STAMP_2 = "2006-06-21 15:29:41";

	private static final String DATE_STAMP_1 = "2006-06-21 15:29:40";

	private static final String HANDLE1 = "handle1";

	private static final String URL1 = "http://www.eclipse.org/mylar";

	private TestRepositoryConnector connector = new TestRepositoryConnector();

	private TestOfflineTaskHandler handler = new TestOfflineTaskHandler();

	private RepositoryTaskData newData;

	protected void setUp() throws Exception {
		super.setUp();
		TasksUiPlugin.getSynchronizationManager().setForceSyncExec(true);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testHasIncoming() {
		AbstractRepositoryTask task = new MockRepositoryTask(HANDLE1);
		RepositoryTaskData taskData = new RepositoryTaskData(new MockAttributeFactory(), connector.getRepositoryType(),
				URL1, "1");
		// assertTrue(TasksUiPlugin.getSynchronizationManager().checkHasIncoming(task,
		// taskData));
		task.setLastSyncDateStamp("never");
		assertTrue(TasksUiPlugin.getSynchronizationManager().checkHasIncoming(task, taskData));
		taskData.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, "2006-06-21 15:29:39");
		assertTrue(TasksUiPlugin.getSynchronizationManager().checkHasIncoming(task, taskData));
		taskData.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, DATE_STAMP_1);
		assertTrue(TasksUiPlugin.getSynchronizationManager().checkHasIncoming(task, taskData));
		task.setLastSyncDateStamp("2006-06-21 15:29:39");
		assertTrue(TasksUiPlugin.getSynchronizationManager().checkHasIncoming(task, taskData));
		task.setLastSyncDateStamp(DATE_STAMP_1);
		assertFalse(TasksUiPlugin.getSynchronizationManager().checkHasIncoming(task, taskData));
	}

	public void testIncomingToIncoming() {
		/*
		 * - Synchronize task with incoming changes - Make another change using
		 * browser - Open report in tasklist (editor opens with old 'new' data
		 * and background sync occurs) - Incoming status set again on task due
		 * to 2nd occurrence of new incoming data
		 */

		// Test unforced
		AbstractRepositoryTask task = primeTaskAndRepository(RepositoryTaskSyncState.INCOMING,
				RepositoryTaskSyncState.INCOMING);
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
		assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
		TasksUiPlugin.getSynchronizationManager().updateOfflineState(task, newData, false);
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
		assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
		assertEquals(DATE_STAMP_2, task.getTaskData().getLastModified());
		// and again...

		RepositoryTaskData taskData3 = new RepositoryTaskData(new MockAttributeFactory(),
				connector.getRepositoryType(), URL1, "1");
		taskData3.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, DATE_STAMP_3);
		TasksUiPlugin.getSynchronizationManager().updateOfflineState(task, taskData3, false);
		// last modified stamp not updated until user synchronizes (newdata ==
		// olddata)
		assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
		assertEquals(DATE_STAMP_3, task.getTaskData().getLastModified());

		// Should keep INCOMING state state since new data has same date samp
		// and sych is not forced.
		TasksUiPlugin.getSynchronizationManager().updateOfflineState(task, taskData3, false);
		assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
		assertEquals(DATE_STAMP_3, task.getTaskData().getLastModified());
	}

	public void testIncomingToSynchronized() {
		// When not forced, tasks with incoming state should remain in incoming
		// state if
		// if new data has same date stamp as old data.
		AbstractRepositoryTask task = primeTaskAndRepository(RepositoryTaskSyncState.INCOMING,
				RepositoryTaskSyncState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
		TasksUiPlugin.getSynchronizationManager().updateOfflineState(task, newData, false);
		assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());

		// Test forced (should move to synced state if new data has same date
		// stamp)
		// UPDATE: as of bug#163850 synchronizing doesn't mark read
		// (synchronized)
		task = primeTaskAndRepository(RepositoryTaskSyncState.INCOMING, RepositoryTaskSyncState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
		TasksUiPlugin.getSynchronizationManager().updateOfflineState(task, newData, true);
		assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
		// assertEquals(RepositoryTaskSyncState.SYNCHRONIZED,
		// task.getSyncState());
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());

		// Test forced with remote incoming
		// Update: bug#163850 - synchronize gets new data but doesn't mark as read (synchronized)
		task = primeTaskAndRepository(RepositoryTaskSyncState.INCOMING, RepositoryTaskSyncState.INCOMING);
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
		TasksUiPlugin.getSynchronizationManager().updateOfflineState(task, newData, true);
		assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
		//assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		//assertEquals(DATE_STAMP_2, task.getLastSyncDateStamp());

	}

	// public void testIncomingToSynchronizedWithVoidSyncTime() {
	// // IF the last sync time (modified timestamp on task) is null, this can
	// result
	// // in the editor refresh/repoen going into an infinite loops since the
	// task never
	// // gets to a synchronized state if the last mod time isn't set. It is now
	// being set
	// // if found to be null.
	// AbstractRepositoryTask task =
	// primeTaskAndRepository(RepositoryTaskSyncState.INCOMING,
	// RepositoryTaskSyncState.SYNCHRONIZED);
	// assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
	// task.setLastSyncDateStamp(null);
	// TasksUiPlugin.getSynchronizationManager().updateOfflineState(task,
	// newData, false);
	// assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
	// assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
	//		
	// TasksUiPlugin.getSynchronizationManager().updateOfflineState(task,
	// newData, false);
	// assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
	// assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
	// }

	/*
	 * public void testIncomingToConflict() { // invalid }
	 */

	/*
	 * public void testIncomingToOutgoing() { // invalid }
	 */

	public void testSynchronizedToIncoming() {
		AbstractRepositoryTask task = primeTaskAndRepository(RepositoryTaskSyncState.SYNCHRONIZED,
				RepositoryTaskSyncState.INCOMING);
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
		TasksUiPlugin.getSynchronizationManager().updateOfflineState(task, newData, false);
		assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
		assertEquals(DATE_STAMP_2, task.getTaskData().getLastModified());
		// assertEquals(DATE_STAMP_2, task.getLastModifiedDateStamp());
	}

	public void testSynchronizedToSynchronized() {
		AbstractRepositoryTask task = primeTaskAndRepository(RepositoryTaskSyncState.SYNCHRONIZED,
				RepositoryTaskSyncState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
		TasksUiPlugin.getSynchronizationManager().updateOfflineState(task, newData, false);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
	}

	/*
	 * public void testSynchronizedToConflict() { // invalid }
	 */

	public void testSynchronizedToOutgoing() {
		AbstractRepositoryTask task = primeTaskAndRepository(RepositoryTaskSyncState.SYNCHRONIZED,
				RepositoryTaskSyncState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());

		task.getTaskData().setNewComment("new comment");
		task.getTaskData().setHasLocalChanges(true);

		TasksUiPlugin.getSynchronizationManager().updateOfflineState(task, task.getTaskData(), false);
		assertEquals(RepositoryTaskSyncState.OUTGOING, task.getSyncState());
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
	}

	public void testConflictToIncoming() {
		// Forced
		AbstractRepositoryTask task = primeTaskAndRepository(RepositoryTaskSyncState.CONFLICT,
				RepositoryTaskSyncState.INCOMING);
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
		TasksUiPlugin.getSynchronizationManager().updateOfflineState(task, newData, true);
		assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
		assertEquals(DATE_STAMP_2, task.getTaskData().getLastModified());
		// assertEquals(DATE_STAMP_2, task.getLastModifiedDateStamp());
	}

	public void testConflictToSynchonized() {
		// Forced
		AbstractRepositoryTask task = primeTaskAndRepository(RepositoryTaskSyncState.CONFLICT,
				RepositoryTaskSyncState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
		TasksUiPlugin.getSynchronizationManager().updateOfflineState(task, newData, true);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
	}

	/*
	 * public void testConflictToConflict() { // ui involved }
	 */

	/*
	 * public void testConflictToOutgoing() { // invalid? }
	 */

	public void testOutgoingToIncoming() {
		// Forced
		AbstractRepositoryTask task = primeTaskAndRepository(RepositoryTaskSyncState.OUTGOING,
				RepositoryTaskSyncState.INCOMING);
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
		TasksUiPlugin.getSynchronizationManager().updateOfflineState(task, newData, true);
		assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
		assertEquals(DATE_STAMP_2, task.getTaskData().getLastModified());
		// assertEquals(DATE_STAMP_2, task.getLastModifiedDateStamp());
	}

	public void testOutgoingToSynchronized() {
		// Forced
		AbstractRepositoryTask task = primeTaskAndRepository(RepositoryTaskSyncState.OUTGOING,
				RepositoryTaskSyncState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
		TasksUiPlugin.getSynchronizationManager().updateOfflineState(task, newData, true);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());

		// Forced with Incoming (user submits a report)
		task = primeTaskAndRepository(RepositoryTaskSyncState.OUTGOING, RepositoryTaskSyncState.INCOMING);
		task.setTaskData(null);
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
		TasksUiPlugin.getSynchronizationManager().updateOfflineState(task, newData, true);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		assertEquals(DATE_STAMP_2, task.getLastSyncDateStamp());
	}

	/*
	 * public void testOutgoingToConflict() { // ui required }
	 */

	public void testOutgoingToOutgoing() {
		AbstractRepositoryTask task = primeTaskAndRepository(RepositoryTaskSyncState.OUTGOING,
				RepositoryTaskSyncState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
		TasksUiPlugin.getSynchronizationManager().updateOfflineState(task, newData, false);
		assertEquals(RepositoryTaskSyncState.OUTGOING, task.getSyncState());
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
	}

	public void testMarkRead() {
		AbstractRepositoryTask task = primeTaskAndRepository(RepositoryTaskSyncState.INCOMING,
				RepositoryTaskSyncState.SYNCHRONIZED);
		task.setLastSyncDateStamp(null);
		assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
		TasksUiPlugin.getSynchronizationManager().setTaskRead(task, true);
		assertEquals(DATE_STAMP_1, task.getLastSyncDateStamp());
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
	}

	public void testMarkUnread() {
		AbstractRepositoryTask task = primeTaskAndRepository(RepositoryTaskSyncState.SYNCHRONIZED,
				RepositoryTaskSyncState.SYNCHRONIZED);
		task.setLastSyncDateStamp(null);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		TasksUiPlugin.getSynchronizationManager().setTaskRead(task, false);
		assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
	}

	private class TestOfflineTaskHandler implements IOfflineTaskHandler {

		private final String DATE_FORMAT_2 = "yyyy-MM-dd HH:mm:ss";

		private final SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_2);

		public AbstractAttributeFactory getAttributeFactory() {
			// ignore
			return null;
		}

		public Date getDateForAttributeType(String attributeKey, String dateString) {

			try {
				return format.parse(dateString);
			} catch (ParseException e) {
				return null;
			}
		}

		public Set<AbstractRepositoryTask> getChangedSinceLastSync(TaskRepository repository,
				Set<AbstractRepositoryTask> tasks, Proxy proxySettings) throws CoreException,
				UnsupportedEncodingException {
			return null;
		}

		public RepositoryTaskData downloadTaskData(TaskRepository repository, String taskId, Proxy proxySettings)
				throws CoreException {
			return null;
		}

	}

	private class TestRepositoryConnector extends MockRepositoryConnector {

		@Override
		public IOfflineTaskHandler getOfflineTaskHandler() {
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
		// loadOfflineTaskData(AbstractRepositoryTask repositoryTask) {
		// return repositoryTask.getTaskData();
		// }

	}

	private AbstractRepositoryTask primeTaskAndRepository(RepositoryTaskSyncState localState,
			RepositoryTaskSyncState remoteState) {
		RepositoryTaskData taskData = null;
		AbstractRepositoryTask task = new MockRepositoryTask(HANDLE1);

		taskData = new RepositoryTaskData(new MockAttributeFactory(), connector.getRepositoryType(), URL1, "1");
		taskData.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, DATE_STAMP_1);
		task.setLastSyncDateStamp(DATE_STAMP_1);
		task.setTaskData(taskData);
		task.setSyncState(localState);

		newData = new RepositoryTaskData(new MockAttributeFactory(), connector.getRepositoryType(), URL1, "1");

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
