/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.tasklist.tests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.internal.tasklist.AbstractAttributeFactory;
import org.eclipse.mylar.internal.tasklist.RepositoryTaskAttribute;
import org.eclipse.mylar.internal.tasklist.RepositoryTaskData;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.IOfflineTaskHandler;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.mylar.tasklist.tests.mockconnector.MockAttributeFactory;
import org.eclipse.mylar.tasklist.tests.mockconnector.MockRepositoryConnector;
import org.eclipse.mylar.tasklist.tests.mockconnector.MockRepositoryTask;

/**
 * @author Rob Elves
 */
public class RepositoryTaskSynchronizationTest extends TestCase {

	private static final String DATE_STAMP_2 = "2006-06-21 15:29:41";

	private static final String DATE_STAMP_1 = "2006-06-21 15:29:40";

	private static final String HANDLE1 = "handle1";

	private static final String URL1 = "http://www.eclipse.org/mylar";

	private TestRepositoryConnector connector = new TestRepositoryConnector();

	private TestOfflineTaskHandler handler = new TestOfflineTaskHandler();

	private RepositoryTaskData newData;

	protected void setUp() throws Exception {
		super.setUp();
		connector.setForceSyncExec(true);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testHasIncoming() {
		AbstractRepositoryTask task = new MockRepositoryTask(HANDLE1);
		RepositoryTaskData taskData = new RepositoryTaskData(new MockAttributeFactory(), connector.getRepositoryType(),
				URL1, "1");
		assertTrue(connector.checkHasIncoming(task, taskData));
		task.setModifiedDateStamp("never");
		assertTrue(connector.checkHasIncoming(task, taskData));
		taskData.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, "2006-06-21 15:29:39");
		assertTrue(connector.checkHasIncoming(task, taskData));
		taskData.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, DATE_STAMP_1);
		assertTrue(connector.checkHasIncoming(task, taskData));
		task.setModifiedDateStamp("2006-06-21 15:29:39");
		assertTrue(connector.checkHasIncoming(task, taskData));
		task.setModifiedDateStamp(DATE_STAMP_2);
		assertFalse(connector.checkHasIncoming(task, taskData));
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
		assertEquals(DATE_STAMP_1, task.getLastModifiedDateStamp());
		connector.updateOfflineState(task, newData, false);
		assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
		assertEquals(DATE_STAMP_2, task.getLastModifiedDateStamp());

	}

	public void testIncomingToSynchronized() {
		AbstractRepositoryTask task = primeTaskAndRepository(RepositoryTaskSyncState.INCOMING,
				RepositoryTaskSyncState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastModifiedDateStamp());
		connector.updateOfflineState(task, newData, false);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		assertEquals(DATE_STAMP_1, task.getLastModifiedDateStamp());

		// Test forced
		task = primeTaskAndRepository(RepositoryTaskSyncState.INCOMING, RepositoryTaskSyncState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastModifiedDateStamp());
		connector.updateOfflineState(task, newData, true);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		assertEquals(DATE_STAMP_1, task.getLastModifiedDateStamp());

		// Test forced with remote incoming
		task = primeTaskAndRepository(RepositoryTaskSyncState.INCOMING, RepositoryTaskSyncState.INCOMING);
		assertEquals(DATE_STAMP_1, task.getLastModifiedDateStamp());
		connector.updateOfflineState(task, newData, true);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		assertEquals(DATE_STAMP_2, task.getLastModifiedDateStamp());

	}

	/*
	 * public void testIncomingToConflict() { // invalid }
	 */

	/*
	 * public void testIncomingToOutgoing() { // invalid }
	 */

	public void testSynchronizedToIncoming() {
		AbstractRepositoryTask task = primeTaskAndRepository(RepositoryTaskSyncState.SYNCHRONIZED,
				RepositoryTaskSyncState.INCOMING);
		assertEquals(DATE_STAMP_1, task.getLastModifiedDateStamp());
		connector.updateOfflineState(task, newData, false);
		assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
		assertEquals(DATE_STAMP_2, task.getLastModifiedDateStamp());
	}

	public void testSynchronizedToSynchronized() {
		AbstractRepositoryTask task = primeTaskAndRepository(RepositoryTaskSyncState.SYNCHRONIZED,
				RepositoryTaskSyncState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastModifiedDateStamp());
		connector.updateOfflineState(task, newData, false);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		assertEquals(DATE_STAMP_1, task.getLastModifiedDateStamp());
	}

	/*
	 * public void testSynchronizedToConflict() { // invalid }
	 */

	public void testSynchronizedToOutgoing() {
		AbstractRepositoryTask task = primeTaskAndRepository(RepositoryTaskSyncState.SYNCHRONIZED,
				RepositoryTaskSyncState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastModifiedDateStamp());

		task.getTaskData().setNewComment("new comment");
		task.getTaskData().setHasLocalChanges(true);

		connector.updateOfflineState(task, task.getTaskData(), false);
		assertEquals(RepositoryTaskSyncState.OUTGOING, task.getSyncState());
		assertEquals(DATE_STAMP_1, task.getLastModifiedDateStamp());
	}

	public void testConflictToIncoming() {
		// Forced
		AbstractRepositoryTask task = primeTaskAndRepository(RepositoryTaskSyncState.CONFLICT,
				RepositoryTaskSyncState.INCOMING);
		assertEquals(DATE_STAMP_1, task.getLastModifiedDateStamp());
		connector.updateOfflineState(task, newData, true);
		assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
		assertEquals(DATE_STAMP_2, task.getLastModifiedDateStamp());
	}

	public void testConflictToSynchonized() {
		// Forced
		AbstractRepositoryTask task = primeTaskAndRepository(RepositoryTaskSyncState.CONFLICT,
				RepositoryTaskSyncState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastModifiedDateStamp());
		connector.updateOfflineState(task, newData, true);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		assertEquals(DATE_STAMP_1, task.getLastModifiedDateStamp());
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
		assertEquals(DATE_STAMP_1, task.getLastModifiedDateStamp());
		connector.updateOfflineState(task, newData, true);
		assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
		assertEquals(DATE_STAMP_2, task.getLastModifiedDateStamp());
	}

	public void testOutgoingToSynchronized() {
		// Forced
		AbstractRepositoryTask task = primeTaskAndRepository(RepositoryTaskSyncState.OUTGOING,
				RepositoryTaskSyncState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastModifiedDateStamp());
		connector.updateOfflineState(task, newData, true);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		assertEquals(DATE_STAMP_1, task.getLastModifiedDateStamp());
		
		
		// Forced with Incoming (user submits a report)
		task = primeTaskAndRepository(RepositoryTaskSyncState.OUTGOING,
				RepositoryTaskSyncState.INCOMING);
		task.setTaskData(null);
		assertEquals(DATE_STAMP_1, task.getLastModifiedDateStamp());
		connector.updateOfflineState(task, newData, true);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		assertEquals(DATE_STAMP_2, task.getLastModifiedDateStamp());
	}

	/*
	 * public void testOutgoingToConflict() { // ui required }
	 */

	public void testOutgoingToOutgoing() {
		AbstractRepositoryTask task = primeTaskAndRepository(RepositoryTaskSyncState.OUTGOING,
				RepositoryTaskSyncState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastModifiedDateStamp());
		connector.updateOfflineState(task, newData, false);
		assertEquals(RepositoryTaskSyncState.OUTGOING, task.getSyncState());
		assertEquals(DATE_STAMP_1, task.getLastModifiedDateStamp());
	}

	private class TestOfflineTaskHandler implements IOfflineTaskHandler {

		private final String DATE_FORMAT_2 = "yyyy-MM-dd HH:mm:ss";

		private final SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_2);

		public RepositoryTaskData downloadTaskData(AbstractRepositoryTask repositoryTask) throws CoreException {
			return null;
		}

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

	}

	private class TestRepositoryConnector extends MockRepositoryConnector {

		@Override
		public IOfflineTaskHandler getOfflineTaskHandler() {
			return handler;
		}

		@Override
		protected void removeOfflineTaskData(RepositoryTaskData bug) {
			// ignore
		}

		@Override
		public void saveOffline(RepositoryTaskData taskData) {
			// ignore
		}

		// @Override
		// protected RepositoryTaskData
		// loadOfflineTaskData(AbstractRepositoryTask repositoryTask) {
		// return repositoryTask.getTaskData();
		//		}
		
		

	}

	private AbstractRepositoryTask primeTaskAndRepository(RepositoryTaskSyncState localState,
			RepositoryTaskSyncState remoteState) {
		RepositoryTaskData taskData = null;
		AbstractRepositoryTask task = new MockRepositoryTask(HANDLE1);

		taskData = new RepositoryTaskData(new MockAttributeFactory(), connector.getRepositoryType(), URL1, "1");
		taskData.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, DATE_STAMP_1);
		task.setModifiedDateStamp(DATE_STAMP_1);
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
