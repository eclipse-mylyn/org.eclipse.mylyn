/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - bug 206510
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaRepositoryConnectorTest extends AbstractBugzillaTest {
	// TODO: refactor
//
//	public void testSubTaskHasIncoming() throws CoreException {
//		init30();
//		String taskNumber = "6";
//		ITask task = generateLocalTaskAndDownload(taskNumber);
//		assertNotNull(task);
//		assertEquals(2, task.getChildren().size());
//		ITask child = task.getChildren().iterator().next();
//		assertEquals(SynchronizationState.INCOMING, child.getSynchronizationState());
//	}
//
//	public void testFocedQuerySynchronization() throws CoreException {
//		init222();
//		TasksUiPlugin.getTaskDataStorageManager().clear();
//		assertEquals(0, taskList.getAllTasks().size());
//		BugzillaRepositoryQuery bugQuery = new BugzillaRepositoryQuery(
//				IBugzillaConstants.TEST_BUGZILLA_222_URL,
//				"http://mylyn.eclipse.org/bugs222/buglist.cgi?short_desc_type=allwordssubstr&short_desc=&product=Read+Only+Test+Cases&long_desc_type=allwordssubstr&long_desc=&bug_status=NEW&order=Importance",
//				"testFocedQuerySynchronization");
//
//		taskList.addQuery(bugQuery);
//
//		TasksUiInternal.synchronizeQuery(connector, bugQuery, null, false);
//
//		assertEquals(1, bugQuery.getChildren().size());
//		ITask hit = (ITask) bugQuery.getChildren().toArray()[0];
//		assertTrue(TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(hit.getRepositoryUrl(), hit.getTaskId()) != null);
//		TasksUiPlugin.getTaskDataStorageManager().remove(hit.getRepositoryUrl(), hit.getTaskId());
//
//		TasksUiInternal.synchronizeQuery(connector, bugQuery, null, true);
//		assertEquals(1, bugQuery.getChildren().size());
//		hit = (ITask) bugQuery.getChildren().toArray()[0];
//		assertTrue(TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(hit.getRepositoryUrl(), hit.getTaskId()) != null);
//
//	}
//
//	public void testCreateTaskFromExistingId() throws Exception {
//		init222();
//		try {
//			TasksUiInternal.createTask(repository, "9999", new NullProgressMonitor());
//			fail();
//		} catch (CoreException ce) {
//
//		}
//
//		ITask task = generateLocalTaskAndDownload("1");
//		assertNotNull(task);
//		assertNotNull(TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(),
//				task.getTaskId()));
//		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
//
//		ITask retrievedTask = taskList.getTask(task.getHandleIdentifier());
//		assertNotNull(retrievedTask);
//		assertEquals(task.getHandleIdentifier(), retrievedTask.getHandleIdentifier());
//	}
//
//	public void testUpdateWithSubTasks() throws Exception {
//		init222();
//		String taskNumber = "23";
//		TasksUiPlugin.getTaskDataStorageManager().clear();
//		assertEquals(0, TasksUiPlugin.getTaskList().getAllTasks().size());
//		BugzillaTask task = this.generateLocalTaskAndDownload(taskNumber);
//		assertNotNull(task.getChildren());
//		assertEquals(2, task.getChildren().size());
//	}
//
//	public void testContextAttachFailure() throws Exception {
//		init218();
//		BugzillaTask task = this.generateLocalTaskAndDownload("3");
//		assertNotNull(task);
//		assertNotNull(TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(),
//				task.getTaskId()));
//		TasksUiPlugin.getTaskListManager().activateTask(task);
//		File sourceContextFile = ContextCorePlugin.getContextStore().getFileForContext(task.getHandleIdentifier());
//		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
//		sourceContextFile.createNewFile();
//		sourceContextFile.deleteOnExit();
//		repository.setAuthenticationCredentials("wrong", "wrong");
//		try {
//			AttachmentUtil.attachContext(connector.getAttachmentHandler(), repository, task, "",
//					new NullProgressMonitor());
//		} catch (CoreException e) {
//			assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
//			return;
//		}
//		fail("Should have failed due to invalid userid and password.");
//	}
//
//
//	public void testUniqueQueryHitObjects() {
//		init222();
//		BugzillaRepositoryQuery query1 = new BugzillaRepositoryQuery(IBugzillaConstants.TEST_BUGZILLA_222_URL,
//				"queryurl", "description1");
//		BugzillaTask query1Hit = new BugzillaTask(IBugzillaConstants.TEST_BUGZILLA_222_URL, "1", "description1");
//		taskList.addQuery(query1);
//		taskList.addTask(query1Hit, query1);
//
//		BugzillaRepositoryQuery query2 = new BugzillaRepositoryQuery(IBugzillaConstants.TEST_BUGZILLA_222_URL,
//				"queryurl2", "description2");
//		BugzillaTask query2Hit = new BugzillaTask(IBugzillaConstants.TEST_BUGZILLA_222_URL, "1", "description2");
//		taskList.addQuery(query2);
//		taskList.addTask(query2Hit, query1);
//
//		assertEquals(2, taskList.getQueries().size());
//		assertEquals(1, taskList.getAllTasks().size());
//		for (ITask hit : query1.getChildren()) {
//			for (ITask hit2 : query2.getChildren()) {
//				assertTrue(hit.getClass().equals(hit2.getClass()));
//			}
//		}
//
//		taskList.deleteQuery(query1);
//		taskList.deleteQuery(query2);
//		assertEquals(1, taskList.getAllTasks().size());
//	}
//
//
//	public void testSynchChangedReports() throws Exception {
//
//		init222();
//		String taskID = "4";
//		BugzillaTask task4 = generateLocalTaskAndDownload(taskID);
//		RepositoryTaskData taskData4 = TasksUiPlugin.getTaskDataStorageManager().getEditableCopy(
//				task4.getRepositoryUrl(), task4.getTaskId());
//		assertNotNull(task4);
//		assertNotNull(taskData4);
//		assertEquals(SynchronizationState.SYNCHRONIZED, task4.getSynchronizationState());
//		assertEquals(taskID, taskData4.getTaskId());
//
//		BugzillaTask task5 = generateLocalTaskAndDownload("5");
//		RepositoryTaskData taskData5 = TasksUiPlugin.getTaskDataStorageManager().getEditableCopy(
//				task5.getRepositoryUrl(), task5.getTaskId());
//		assertNotNull(task5);
//		assertNotNull(taskData5);
//		assertEquals(SynchronizationState.SYNCHRONIZED, task5.getSynchronizationState());
//		assertEquals("5", taskData5.getTaskId());
//
//		Set<ITask> tasks = new HashSet<ITask>();
//		tasks.add(task4);
//		tasks.add(task5);
//
//		// Precondition for test passing is that task5's modification data is
//		// AFTER
//		// task4's
//
//		TasksUiPlugin.getRepositoryManager().setSynchronizationTime(repository, task5.getLastReadTimeStamp(),
//				TasksUiPlugin.getDefault().getRepositoriesFilePath());
//
//		SynchronizationContext event = new SynchronizationContext();
//		event.setTasks(tasks);
//		event.setNeedsPerformQueries(true);
//		event.setTaskRepository(repository);
//		event.setFullSynchronization(true);
//		connector.preSynchronization(event, null);
//		assertTrue(event.needsPerformQueries());
//		// Always last known changed returned
//		assertFalse(task4.isStale());
//		assertTrue(task5.isStale());
//
//		String priority4 = null;
//		if (task4.getPriority().equals("P1")) {
//			priority4 = "P2";
//			taskData4.setAttributeValue(BugzillaReportElement.PRIORITY.getKeyString(), priority4);
//		} else {
//			priority4 = "P1";
//			taskData4.setAttributeValue(BugzillaReportElement.PRIORITY.getKeyString(), priority4);
//		}
//
//		String priority5 = null;
//		if (task5.getPriority().equals("P1")) {
//			priority5 = "P2";
//			taskData5.setAttributeValue(BugzillaReportElement.PRIORITY.getKeyString(), priority5);
//		} else {
//			priority5 = "P1";
//			taskData5.setAttributeValue(BugzillaReportElement.PRIORITY.getKeyString(), priority5);
//		}
//
//		assertNotNull(repository.getUserName());
//		assertNotNull(repository.getPassword());
//
//		submit(task4, taskData4);
//		submit(task5, taskData5);
//
//		event = new SynchronizationContext();
//		event.setTasks(tasks);
//		event.setNeedsPerformQueries(true);
//		event.setTaskRepository(repository);
//		event.setFullSynchronization(true);
//		connector.preSynchronization(event, null);
//
//		assertTrue(task4.isStale());
//		assertTrue(task5.isStale());
//
//		TasksUiInternal.synchronizeTasks(connector, tasks, true, null);
//
//		for (ITask task : tasks) {
//			if (task.getTaskId() == "4") {
//				assertEquals(priority4, task4.getPriority());
//			}
//			if (task.getTaskId() == "5") {
//				assertEquals(priority5, task5.getPriority());
//			}
//		}
//	}
//
//	public void testIncomingWhenOfflineDeleted() throws Exception {
//
//		init222();
//		BugzillaTask task7 = generateLocalTaskAndDownload("7");
//		RepositoryTaskData recentTaskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(
//				task7.getRepositoryUrl(), task7.getTaskId());
//		assertNotNull(recentTaskData);
//		TasksUiPlugin.getTaskDataManager().setTaskRead(task7, true);
//		assertEquals(SynchronizationState.SYNCHRONIZED, task7.getSynchronizationState());
//		assertEquals("7", recentTaskData.getTaskId());
//
//		Set<AbstractTask> tasks = new HashSet<AbstractTask>();
//		tasks.add(task7);
//
//		TasksUiPlugin.getRepositoryManager().setSynchronizationTime(repository, task7.getLastReadTimeStamp(),
//				TasksUiPlugin.getDefault().getRepositoriesFilePath());
//
//		assertNotNull(TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(
//				IBugzillaConstants.TEST_BUGZILLA_222_URL, "7"));
//		TasksUiPlugin.getTaskDataStorageManager().remove(task7.getRepositoryUrl(), task7.getTaskId());
//
//		assertNull(TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(IBugzillaConstants.TEST_BUGZILLA_222_URL,
//				"7"));
//
//		assertEquals(SynchronizationState.SYNCHRONIZED, task7.getSynchronizationState());
//		assertNotNull(task7.getLastReadTimeStamp());
//		// Task no longer stored offline
//		// make an external change
//		assertNotNull(repository.getUserName());
//		assertNotNull(repository.getPassword());
//
//		String priority = null;
//		if (task7.getPriority().equals("P1")) {
//			priority = "P2";
//			recentTaskData.setAttributeValue(BugzillaReportElement.PRIORITY.getKeyString(), priority);
//		} else {
//			priority = "P1";
//			recentTaskData.setAttributeValue(BugzillaReportElement.PRIORITY.getKeyString(), priority);
//		}
//
//		// disabled due to TasksUi.synchronizeChanged(connector, repository) being removed
//		// REMOVE ALL TASK DATA
////		TasksUiPlugin.getTaskDataManager().clear();
////		connector.getTaskDataHandler().postTaskData(repository, recentTaskData, new NullProgressMonitor());
////		TasksUi.synchronizeChanged(connector, repository);
////		assertEquals(SynchronizationState.INCOMING, task7.getSynchronizationState());
//	}
//
//	public void testTimeTracker222() throws Exception {
//		init222();
//		timeTracker(15, true);
//	}
//
//	// We'll skip these two for now and just test 222 and 218 since
//	// they are the most common. If problems arise we can re-enable.
//	// public void testTimeTracker2201() throws Exception {
//	// init2201();
//	// timeTracker(22, true);
//	// }
//	//
//	// public void testTimeTracker220() throws Exception {
//	// init220();
//	// timeTracker(8, true);
//	// }
//
//	public void testTimeTracker218() throws Exception {
//		init218();
//		timeTracker(20, false);
//	}
//
//	/**
//	 * @param enableDeadline
//	 *            bugzilla 218 doesn't support deadlines
//	 */
//	protected void timeTracker(int taskid, boolean enableDeadline) throws Exception {
//		BugzillaTask bugtask = generateLocalTaskAndDownload("" + taskid);
//		RepositoryTaskData bugtaskdata = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(
//				bugtask.getRepositoryUrl(), bugtask.getTaskId());
//		assertNotNull(bugtaskdata);
//		assertEquals(taskid + "", bugtaskdata.getTaskId());
//		assertEquals(SynchronizationState.SYNCHRONIZED, bugtask.getSynchronizationState());
//
//		Set<AbstractTask> tasks = new HashSet<AbstractTask>();
//		tasks.add(bugtask);
//
//		// synchAndAssertState(tasks, SynchronizationState.SYNCHRONIZED);
//
//		TasksUiPlugin.getRepositoryManager().setSynchronizationTime(repository, bugtask.getLastReadTimeStamp(),
//				TasksUiPlugin.getDefault().getRepositoriesFilePath());
//		// connector.synchronizeChanged(repository);
//
//		// Set<AbstractTask> changedTasks =
//		// connector.getOfflineTaskHandler().getChangedSinceLastSync(repository,
//		// tasks);
//		// assertEquals(1, changedTasks.size());
//
//		assertNotNull(repository.getUserName());
//		assertNotNull(repository.getPassword());
//
//		float estimatedTime, remainingTime, actualTime, addTime;
//		String deadline = null;
//
//		estimatedTime = Float.parseFloat(bugtaskdata.getAttributeValue(BugzillaReportElement.ESTIMATED_TIME.getKeyString()));
//		remainingTime = Float.parseFloat(bugtaskdata.getAttributeValue(BugzillaReportElement.REMAINING_TIME.getKeyString()));
//		actualTime = Float.parseFloat(bugtaskdata.getAttributeValue(BugzillaReportElement.ACTUAL_TIME.getKeyString()));
//		if (enableDeadline) {
//			deadline = bugtaskdata.getAttributeValue(BugzillaReportElement.DEADLINE.getKeyString());
//		}
//
//		estimatedTime += 2;
//		remainingTime += 1.5;
//		addTime = 0.75f;
//		if (enableDeadline) {
//			deadline = generateNewDay();
//		}
//
//		bugtaskdata.setAttributeValue(BugzillaReportElement.ESTIMATED_TIME.getKeyString(), "" + estimatedTime);
//		bugtaskdata.setAttributeValue(BugzillaReportElement.REMAINING_TIME.getKeyString(), "" + remainingTime);
//		bugtaskdata.setAttributeValue(BugzillaReportElement.WORK_TIME.getKeyString(), "" + addTime);
//		if (enableDeadline) {
//			bugtaskdata.setAttributeValue(BugzillaReportElement.DEADLINE.getKeyString(), deadline);
//		}
//
////		for (AbstractTask task : tasks) {
////			RepositoryTaskData taskData = TasksUiPlugin.getTaskDataManager().getNewTaskData(
////					task.getHandleIdentifier());
//		bugtaskdata.setAttributeValue(BugzillaReportElement.ADD_COMMENT.getKeyString(), "New Estimate: "
//				+ estimatedTime + "\nNew Remaining: " + remainingTime + "\nAdd: " + addTime);
//		submit(bugtask, bugtaskdata);
////		}
//
//		synchAndAssertState(tasks, SynchronizationState.SYNCHRONIZED);
//
//		bugtaskdata = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(bugtask.getRepositoryUrl(),
//				bugtask.getTaskId());
//
//		assertEquals(estimatedTime,
//				Float.parseFloat(bugtaskdata.getAttributeValue(BugzillaReportElement.ESTIMATED_TIME.getKeyString())));
//		assertEquals(remainingTime,
//				Float.parseFloat(bugtaskdata.getAttributeValue(BugzillaReportElement.REMAINING_TIME.getKeyString())));
//		assertEquals(actualTime + addTime,
//				Float.parseFloat(bugtaskdata.getAttributeValue(BugzillaReportElement.ACTUAL_TIME.getKeyString())));
//		if (enableDeadline) {
//			assertEquals(deadline, bugtaskdata.getAttributeValue(BugzillaReportElement.DEADLINE.getKeyString()));
//		}
//
//	}
//
//	private String generateNewDay() {
//		int year = 2006;
//		int month = (int) (Math.random() * 12 + 1);
//		int day = (int) (Math.random() * 28 + 1);
//		return "" + year + "-" + ((month <= 9) ? "0" : "") + month + "-" + ((day <= 9) ? "0" : "") + day;
//	}
//
//
//// public void testSimpleLoad() throws Exception {
//// repository = new TaskRepository(DEFAULT_KIND,
//// IBugzillaConstants.ECLIPSE_BUGZILLA_URL);
//// //Credentials credentials = MylynTestUtils.readCredentials();
//// //repository.setAuthenticationCredentials(credentials.username,
//// credentials.password);
////
//// //repository.setTimeZoneId("Canada/Eastern");
//// assertNotNull(manager);
//// manager.addRepository(repository,
//// TasksUiPlugin.getDefault().getRepositoriesFilePath());
////
//// taskList = TasksUiPlugin.getTaskList();
////
//// AbstractRepositoryConnector abstractRepositoryConnector =
//// manager.getRepositoryConnector(DEFAULT_KIND);
////
//// assertEquals(abstractRepositoryConnector.getRepositoryType(), DEFAULT_KIND);
////
//// connector = (BugzillaRepositoryConnector) abstractRepositoryConnector;
////
//// long start = System.currentTimeMillis();
//// BugzillaTask task = null;
//// for(int x = 1; x < 5; x++) {
//// if(task != null)
//// taskList.deleteTask(task);
////
//// task = this.generateLocalTaskAndDownload("154100");
//// assertNotNull(task);
//// }
//// System.err.println("Total: "+((System.currentTimeMillis() - start)/1000));
//// }
//
//	// class MockBugzillaReportSubmitForm extends BugzillaReportSubmitForm {
//	//
//	// public MockBugzillaReportSubmitForm(String encoding_utf_8) {
//	// super(encoding_utf_8);
//	// }
//	//
//	// @Override
//	// public String submitReportToRepository() throws BugzillaException,
//	// LoginException,
//	// PossibleBugzillaFailureException {
//	// return "test-submit";
//	// }
//	//
//	// }

// Bugzilla specific test cases from TaskDataManagerTest	

//	private static final String DATE_STAMP_3 = "2006-06-21 15:29:42";

//	private static final String DATE_STAMP_2 = "2006-06-21 15:29:41";

	private static final String DATE_STAMP_1 = "2006-06-21 15:29:40";

	private static final String MOCCK_ID = "1";

	//private static final String URL1 = "http://www.eclipse.org/mylar";

//	private final TestRepositoryConnector connector = new TestRepositoryConnector();
//
//	private final TestOfflineTaskHandler handler = new TestOfflineTaskHandler();

//	private RepositoryTaskData newData;

//	public void testHasIncoming() {
//		MockTask task = new MockTask(MOCCK_ID);
//		RepositoryTaskData taskData = new RepositoryTaskData(new MockAttributeFactory(), connector.getConnectorKind(),
//				MockRepositoryConnector.REPOSITORY_URL, MOCCK_ID);
//		task.setLastReadTimeStamp("never");
//
//		assertTrue(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));
//		taskData.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, "2006-06-21 15:29:39");
//		assertTrue(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));
//		taskData.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, DATE_STAMP_1);
//		assertTrue(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));
//		task.setLastReadTimeStamp("2006-06-21 15:29:39");
//		assertTrue(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));
//		task.setLastReadTimeStamp(DATE_STAMP_1);
//		assertFalse(TasksUiPlugin.getTaskDataManager().checkHasIncoming(task, taskData));
//	}

	// Invalid state change. Test that this can't happen.
	public void testIncomingToSynchronized() {
//		// When not forced, tasks with incoming state should remain in incoming
//		// state if
//		// if new data has same date stamp as old data.
//		AbstractTask task = primeTaskAndRepository(SynchronizationState.INCOMING, SynchronizationState.SYNCHRONIZED);
//		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
//		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, false);
//		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());
//		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
//
//		task = primeTaskAndRepository(SynchronizationState.INCOMING, SynchronizationState.SYNCHRONIZED);
//		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
//		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, true);
//		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());
//		// assertEquals(SynchronizationState.SYNCHRONIZED,
//		// task.getSyncState());
//		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
//
//		// Test forced with remote incoming
//		// Update: bug#163850 - synchronize gets new data but doesn't mark
//		// synchronized
//		task = primeTaskAndRepository(SynchronizationState.INCOMING, SynchronizationState.INCOMING);
//		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
//		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, true);
//		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());

	}

//
//	public void testSynchronizedToIncoming() {
//		AbstractTask task = primeTaskAndRepository(SynchronizationState.SYNCHRONIZED, SynchronizationState.INCOMING);
//		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
//		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, false);
//		assertEquals(SynchronizationState.INCOMING, task.getSynchronizationState());
//		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(),
//				task.getTaskId());
//		assertEquals(DATE_STAMP_2, taskData.getLastModified());
//		// assertEquals(DATE_STAMP_2, task.getLastModifiedDateStamp());
//	}

	public void testSynchronizedToSynchronized() {
//		AbstractTask task = primeTaskAndRepository(SynchronizationState.SYNCHRONIZED, SynchronizationState.SYNCHRONIZED);
//		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
//		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, false);
//		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
//		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
	}

	/*
	 * public void testSynchronizedToConflict() { // invalid }
	 */

//	public void testSynchronizedToOutgoing() {
//		AbstractTask task = primeTaskAndRepository(SynchronizationState.SYNCHRONIZED, SynchronizationState.SYNCHRONIZED);
//		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
//
//		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getEditableCopy(
//				task.getRepositoryUrl(), task.getTaskId());
//
//		taskData.setNewComment("new comment");
//
//		HashSet<RepositoryTaskAttribute> changed = new HashSet<RepositoryTaskAttribute>();
//		changed.add(taskData.getAttribute(RepositoryTaskAttribute.COMMENT_NEW));
//		TasksUiPlugin.getTaskDataManager().saveOutgoing(task, changed);
//		assertEquals(SynchronizationState.OUTGOING, task.getSynchronizationState());
//		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
//	}
//
//	public void testConflictToConflict() {
//		AbstractTask task = primeTaskAndRepository(SynchronizationState.CONFLICT, SynchronizationState.INCOMING);
//		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
//		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, true);
//		assertEquals(SynchronizationState.CONFLICT, task.getSynchronizationState());
//		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(),
//				task.getTaskId());
//
//		assertEquals(DATE_STAMP_2, taskData.getLastModified());
//		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, true);
//		assertEquals(SynchronizationState.CONFLICT, task.getSynchronizationState());
//		taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
//		assertEquals(DATE_STAMP_2, taskData.getLastModified());
//	}
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
//	public void testOutgoingToConflict() {
//		// Forced
//		AbstractTask task = primeTaskAndRepository(SynchronizationState.OUTGOING, SynchronizationState.INCOMING);
//		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(),
//				task.getTaskId());
//
//		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
//		assertEquals(SynchronizationState.OUTGOING, task.getSynchronizationState());
//		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, true);
//		assertEquals(SynchronizationState.CONFLICT, task.getSynchronizationState());
//		taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
//
//		assertEquals(DATE_STAMP_2, taskData.getLastModified());
//		// assertEquals(DATE_STAMP_2, task.getLastModifiedDateStamp());
//	}
//
//	// Illegal state change, test it doesn't occur
	public void testOutgoingToSynchronized() {
		AbstractTask task = primeTaskAndRepository(SynchronizationState.OUTGOING, SynchronizationState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());

//		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, true);
//		assertEquals(SynchronizationState.OUTGOING, task.getSynchronizationState());
//		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
	}

	public void testOutgoingToOutgoing() {
		AbstractTask task = primeTaskAndRepository(SynchronizationState.OUTGOING, SynchronizationState.SYNCHRONIZED);
		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
//		TasksUiPlugin.getTaskDataManager().saveIncoming(task, newData, false);
//		assertEquals(SynchronizationState.OUTGOING, task.getSynchronizationState());
//		assertEquals(DATE_STAMP_1, task.getLastReadTimeStamp());
	}

	/*
	 * public void testIncomingToConflict() { // invalid }
	 */

	/*
	 * public void testIncomingToOutgoing() { // invalid }
	 */

	private AbstractTask primeTaskAndRepository(SynchronizationState localState, SynchronizationState remoteState) {
//		RepositoryTaskData taskData = null;
		AbstractTask task = new MockTask(MOCCK_ID);

//		taskData = new RepositoryTaskData(new MockAttributeFactory(), connector.getConnectorKind(),
//				MockRepositoryConnector.REPOSITORY_URL, MOCCK_ID);
//		taskData.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, DATE_STAMP_1);
//		task.setLastReadTimeStamp(DATE_STAMP_1);
//		task.setSynchronizationState(localState);
//		TasksUiPlugin.getTaskDataStorageManager().setNewTaskData(taskData);
//		newData = new RepositoryTaskData(new MockAttributeFactory(), connector.getConnectorKind(),
//				MockRepositoryConnector.REPOSITORY_URL, MOCCK_ID);
//
//		switch (remoteState) {
//		case CONFLICT:
//		case INCOMING:
//			newData.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, DATE_STAMP_2);
//			break;
//		case SYNCHRONIZED:
//			newData.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED, DATE_STAMP_1);
//			break;
//		default:
//			fail("Remote repository can only be INCOMING or SYNCHRONIZED wrt the local task.");
//
//		}

		return task;
	}

}
