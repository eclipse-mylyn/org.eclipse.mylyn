/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryQuery;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalAttachment;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryAttachment;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryOperation;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.ui.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.search.RepositorySearchResult;
import org.eclipse.mylyn.internal.tasks.ui.search.SearchHitCollector;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationContext;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Nathan Hapke
 * @author Frank Becker (bug 206510)
 */
public class BugzillaRepositoryConnectorTest extends AbstractBugzillaTest {

	int counter = 0;

//	public void testMissingHits() throws Exception {
//		// query for all mylyn bugzilla tasks.
//		// reset sync date
//		// mark stale tasks should equal number of tasks
//		TasksUiPlugin.getSynchronizationManager().setForceSyncExec(true);
//		init(IBugzillaConstants.ECLIPSE_BUGZILLA_URL);
//		repository.setAuthenticationCredentials("username", "password");
//		String queryString = "https://bugs.eclipse.org/bugs/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=&classification=Tools&product=Mylyn&component=Bugzilla&long_desc_type=allwordssubstr&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&status_whiteboard_type=allwordssubstr&status_whiteboard=&keywords_type=allwords&keywords=&bug_status=NEW&priority=P1&priority=P2&emailtype1=substring&email1=&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=";
//		//String queryString = "https://bugs.eclipse.org/bugs/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=&classification=Tools&product=Mylyn&component=Bugzilla&long_desc_type=allwordssubstr&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&status_whiteboard_type=allwordssubstr&status_whiteboard=&keywords_type=allwords&keywords=&priority=P1&emailtype1=substring&email1=&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=";
//		//String queryString = "https://bugs.eclipse.org/bugs/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=&classification=Tools&product=Mylyn&component=Bugzilla&component=Tasks&long_desc_type=allwordssubstr&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&status_whiteboard_type=allwordssubstr&status_whiteboard=&keywords_type=allwords&keywords=&emailtype1=substring&email1=&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=";
//		BugzillaRepositoryQuery query = new BugzillaRepositoryQuery(IBugzillaConstants.ECLIPSE_BUGZILLA_URL, queryString, "test" );
//		//TasksUiPlugin.getTaskList().addQuery(query);
//		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(BugzillaCorePlugin.REPOSITORY_KIND);
////		QueryHitCollector collector = new QueryHitCollector(new TaskFactory(repository, false, false));
////		connector.performQuery(query, repository, new NullProgressMonitor(), collector);
////		System.err.println(">>> Collector: "+collector.getTasks().size());
////		for (AbstractTask task : collector.getTasks()) {
////			TasksUiPlugin.getTaskList().addTask(task);
////		}
//
//		TasksUiPlugin.getSynchronizationManager().synchronize(connector, query, null, true);
//		//System.err.println(">>> initial size: "+query.getChildren().size());
//		for (AbstractTask task : query.getChildren()) {
//			assertTrue(task.getSynchronizationState() == SynchronizationState.INCOMING);
//			TasksUiPlugin.getSynchronizationManager().setTaskRead(task, true);
//			task.setLastReadTimeStamp("1970-01-01");
//			assertTrue(task.getSynchronizationState() == SynchronizationState.SYNCHRONIZED);
//		}
//
//		for (AbstractTask task : query.getChildren()) {
//			assertTrue(task.getSynchronizationState() == SynchronizationState.SYNCHRONIZED);
//		}
//
//		repository.setSynchronizationTimeStamp("1970-01-01");//getSynchronizationTimeStamp();
//		//connector.markStaleTasks(repository, query.getChildren(), new NullProgressMonitor());
//		TasksUiPlugin.getSynchronizationManager().synchronize(connector, query, null, true);
//		for (AbstractTask task : query.getChildren()) {
//			assertTrue(task.getSynchronizationState() == SynchronizationState.INCOMING);
//		}
//	}

	BugzillaTask fruitTask;
	RepositoryTaskData fruitTaskData;
	private void setFruitValueTo(String newValue) throws CoreException {
		Set<RepositoryTaskAttribute> changed = new HashSet<RepositoryTaskAttribute>();
		fruitTaskData.setAttributeValue("cf_fruit", newValue);
		assertEquals(newValue, fruitTaskData.getAttributeValue("cf_fruit"));
		changed.add(fruitTaskData.getAttribute("cf_fruit"));
		submit(fruitTask, fruitTaskData);
		TasksUiInternal.synchronizeTask(connector, fruitTask, true, null);
		fruitTaskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(fruitTask.getRepositoryUrl(), fruitTask.getTaskId());
		assertEquals(newValue, fruitTaskData.getAttributeValue("cf_fruit"));

	}

	public void testCustomFields() throws Exception {
		init(IBugzillaConstants.TEST_BUGZILLA_303_URL);

		String taskNumber = "1";
		TasksUiPlugin.getTaskDataStorageManager().clear();

		// Get the task
		fruitTask = generateLocalTaskAndDownload(taskNumber);

		fruitTaskData = TasksUiPlugin.getTaskDataStorageManager().getEditableCopy(fruitTask.getRepositoryUrl(),
				fruitTask.getTaskId());
		assertNotNull(fruitTaskData);

		if (fruitTaskData.getAttributeValue("cf_fruit").equals("---")) {
			setFruitValueTo("apple");
			setFruitValueTo("orange");
			setFruitValueTo("---");
		} else if (fruitTaskData.getAttributeValue("cf_fruit").equals("apple")) {
			setFruitValueTo("orange");
			setFruitValueTo("apple");
			setFruitValueTo("---");
		} else if (fruitTaskData.getAttributeValue("cf_fruit").equals("orange")) {
			setFruitValueTo("apple");
			setFruitValueTo("orange");
			setFruitValueTo("---");
		}
		if (fruitTask!=null) {
			fruitTask = null;
		}
		if (fruitTaskData!=null) {
			fruitTaskData = null;
		}
	}

	public void testMidAirCollision() throws Exception {
		init30();
		String taskNumber = "5";

		TasksUiPlugin.getTaskDataStorageManager().clear();

		// Get the task
		BugzillaTask task = generateLocalTaskAndDownload(taskNumber);

		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getEditableCopy(task.getRepositoryUrl(),
				task.getTaskId());
		assertNotNull(taskData);

		TasksUiPlugin.getTaskList().addTask(task);

		String newCommentText = "BugzillaRepositoryClientTest.testMidAirCollision(): test " + (new Date()).toString();
		taskData.setNewComment(newCommentText);
		Set<RepositoryTaskAttribute> changed = new HashSet<RepositoryTaskAttribute>();
		changed.add(taskData.getAttribute(RepositoryTaskAttribute.COMMENT_NEW));
		taskData.setAttributeValue("delta_ts", "2007-01-01 00:00:00");
		changed.add(taskData.getAttribute("delta_ts"));

		TasksUiPlugin.getTaskDataStorageManager().saveEdits(task.getRepositoryUrl(), task.getTaskId(), changed);

		try {
			// Submit changes
			submit(task, taskData);
			fail("Mid-air collision expected");
		} catch (CoreException e) {
			assertTrue(e.getStatus().getMessage().indexOf("Mid-air collision occurred while submitting") != -1);
		}
	}

	public void testAuthenticationCredentials() throws Exception {
		init218();
		BugzillaTask task = this.generateLocalTaskAndDownload("3");
		assertNotNull(task);
		assertNotNull(TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId()));
		TasksUiPlugin.getTaskListManager().activateTask(task);
		File sourceContextFile = ContextCore.getContextManager().getFileForContext(task.getHandleIdentifier());
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
		sourceContextFile.createNewFile();
		sourceContextFile.deleteOnExit();

		repository.setAuthenticationCredentials("wrong", "wrong");
		TasksUiPlugin.getRepositoryManager().notifyRepositorySettingsChanged(repository);
		try {
			AttachmentUtil.attachContext(connector.getAttachmentHandler(), repository, task, "", new NullProgressMonitor());
		} catch (CoreException e) {
			assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
			assertTrue(e.getStatus().getMessage().indexOf("Invalid repository credentials.") != -1);
			return;
		}
		fail("Should have failed due to invalid userid and password.");
	}

//  testReassign Bugs
//	Version	BugNr	assigned				reporter
//	2.22	92		user@mylar.eclipse.org	tests@mylar.eclipse.org
//	3.0		 5		tests@mylar.eclipse.org	tests2@mylar.eclipse.org
//	3.1		 1		rob.elves@eclipse.org	tests@mylar.eclipse.org

	public void testReassign222() throws CoreException {
		init222();
		String taskNumber = "92";
		doReassignOld(taskNumber, "user@mylar.eclipse.org");
	}

	public void testReassign30() throws CoreException {
		init30();
		String taskNumber = "5";
		doReassignOld(taskNumber, "tests@mylyn.eclipse.org");
	}

	public void testReassign31() throws CoreException {
		init31();
		String taskNumber = "1";

		TasksUiPlugin.getTaskDataStorageManager().clear();

		// Get the task
		BugzillaTask task = generateLocalTaskAndDownload(taskNumber);

		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getEditableCopy(task.getRepositoryUrl(),
				task.getTaskId());
		assertNotNull(taskData);

		TasksUiPlugin.getTaskList().addTask(task);
		if (taskData.getAssignedTo().equals("rob.elves@eclipse.org")) {
			assertEquals("rob.elves@eclipse.org", taskData.getAssignedTo());
			reassingToUser31(task, taskData);
			TasksUiInternal.synchronizeTask(connector, task, true, null);
			taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
			assertEquals("tests2@mylyn.eclipse.org", taskData.getAssignedTo());

			reassignToDefault31(task, taskData);
			TasksUiInternal.synchronizeTask(connector, task, true, null);
			taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
			assertEquals("rob.elves@eclipse.org", taskData.getAssignedTo());
		} else if (taskData.getAssignedTo().equals("tests2@mylyn.eclipse.org")) {
			assertEquals("tests2@mylyn.eclipse.org", taskData.getAssignedTo());
			reassignToDefault31(task, taskData);
			TasksUiInternal.synchronizeTask(connector, task, true, null);
			taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
			assertEquals("rob.elves@eclipse.org", taskData.getAssignedTo());

			reassingToUser31(task, taskData);
			TasksUiInternal.synchronizeTask(connector, task, true, null);
			taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
			assertEquals("tests2@mylyn.eclipse.org", taskData.getAssignedTo());
		} else {
			fail("Bug with unexpected user assigned");
		}

	}

	private void reassignToDefault31(BugzillaTask task, RepositoryTaskData taskData) throws CoreException {
		// Modify it (reassignbycomponent)
		String newCommentText = "BugzillaRepositoryClientTest.testReassign31(): reassignbycomponent "
			+ (new Date()).toString();
		taskData.setNewComment(newCommentText);
		Set<RepositoryTaskAttribute> changed = new HashSet<RepositoryTaskAttribute>();
		changed.add(taskData.getAttribute(RepositoryTaskAttribute.COMMENT_NEW));

		taskData.setAttributeValue(BugzillaReportElement.SET_DEFAULT_ASSIGNEE.getKeyString(), "1");
		changed.add(taskData.getAttribute(BugzillaReportElement.SET_DEFAULT_ASSIGNEE.getKeyString()));

		TasksUiPlugin.getTaskDataStorageManager().saveEdits(task.getRepositoryUrl(), task.getTaskId(), changed);

		// Submit changes
		submit(task, taskData);
	}

	private void reassingToUser31(BugzillaTask task, RepositoryTaskData taskData) throws CoreException {
		// Modify it (reassign to tests2@mylyn.eclipse.org)
		String newCommentText = "BugzillaRepositoryClientTest.testReassign31(): reassign " + (new Date()).toString();
		taskData.setNewComment(newCommentText);
		Set<RepositoryTaskAttribute> changed = new HashSet<RepositoryTaskAttribute>();
		changed.add(taskData.getAttribute(RepositoryTaskAttribute.COMMENT_NEW));

		taskData.setAttributeValue(RepositoryTaskAttribute.USER_ASSIGNED, "tests2@mylyn.eclipse.org");
		changed.add(taskData.getAttribute(RepositoryTaskAttribute.USER_ASSIGNED));
		TasksUiPlugin.getTaskDataStorageManager().saveEdits(task.getRepositoryUrl(), task.getTaskId(), changed);

		// Submit changes
		submit(task, taskData);
	}

	private void doReassignOld(String taskNumber, String defaultAssignee) throws CoreException {
		TasksUiPlugin.getTaskDataStorageManager().clear();

		// Get the task
		BugzillaTask task = generateLocalTaskAndDownload(taskNumber);

		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getEditableCopy(task.getRepositoryUrl(),
				task.getTaskId());
		assertNotNull(taskData);

		TasksUiPlugin.getTaskList().addTask(task,
				TasksUiPlugin.getTaskList().getDefaultCategory());

		if (taskData.getAssignedTo().equals(defaultAssignee)) {
			assertEquals(defaultAssignee, taskData.getAssignedTo());
			reassingToUserOld(task, taskData);
			TasksUiInternal.synchronizeTask(connector, task, true, null);
			taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
			assertEquals("tests2@mylyn.eclipse.org", taskData.getAssignedTo());

			reassignToDefaultOld(task, taskData);
			TasksUiInternal.synchronizeTask(connector, task, true, null);
			taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
			assertEquals(defaultAssignee, taskData.getAssignedTo());
		} else if (taskData.getAssignedTo().equals("tests2@mylyn.eclipse.org")) {
			assertEquals("tests2@mylyn.eclipse.org", taskData.getAssignedTo());
			reassignToDefaultOld(task, taskData);
			TasksUiInternal.synchronizeTask(connector, task, true, null);
			taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
			assertEquals(defaultAssignee, taskData.getAssignedTo());

			reassingToUserOld(task, taskData);
			TasksUiInternal.synchronizeTask(connector, task, true, null);
			taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
			assertEquals("tests2@mylyn.eclipse.org", taskData.getAssignedTo());
		} else {
			fail("Bug with unexpected user assigned");
		}
	}

	private void reassignToDefaultOld(BugzillaTask task, RepositoryTaskData taskData) throws CoreException {
		// Modify it (reassignbycomponent)
		String newCommentText = "BugzillaRepositoryClientTest.testReassignOld(): reassignbycomponent "
			+ (new Date()).toString();
		taskData.setNewComment(newCommentText);
		Set<RepositoryTaskAttribute> changed = new HashSet<RepositoryTaskAttribute>();
		changed.add(taskData.getAttribute(RepositoryTaskAttribute.COMMENT_NEW));
		for (RepositoryOperation o : taskData.getOperations()) {
			if (o.isChecked()) {
				o.setChecked(false);
			}
			if (o.getKnobName().compareTo("reassignbycomponent") == 0) {
				o.setChecked(true);
				taskData.setSelectedOperation(o);
			}
		}
		TasksUiPlugin.getTaskDataStorageManager().saveEdits(task.getRepositoryUrl(), task.getTaskId(), changed);

		// Submit changes
		submit(task, taskData);
	}

	private void reassingToUserOld(BugzillaTask task, RepositoryTaskData taskData) throws CoreException {
		// Modify it (reassign to tests2@mylyn.eclipse.org)
		String newCommentText = "BugzillaRepositoryClientTest.testReassignOld(): reassign " + (new Date()).toString();
		taskData.setNewComment(newCommentText);
		Set<RepositoryTaskAttribute> changed = new HashSet<RepositoryTaskAttribute>();
		changed.add(taskData.getAttribute(RepositoryTaskAttribute.COMMENT_NEW));
		for (RepositoryOperation o : taskData.getOperations()) {
			if (o.isChecked()) {
				o.setChecked(false);
			}
			if (o.getKnobName().compareTo("reassign") == 0) {
				o.setInputValue("tests2@mylyn.eclipse.org");
				o.setChecked(true);
				taskData.setSelectedOperation(o);
			}
		}
		TasksUiPlugin.getTaskDataStorageManager().saveEdits(task.getRepositoryUrl(), task.getTaskId(), changed);

		// Submit changes
		submit(task, taskData);

	}

	public void testSubTaskHasIncoming() throws CoreException {
		init30();
		String taskNumber = "6";
		BugzillaTask task = generateLocalTaskAndDownload(taskNumber);
		assertNotNull(task);
		assertEquals(2, task.getChildren().size());
		ITask child = task.getChildren().iterator().next();
		assertEquals(SynchronizationState.INCOMING, child.getSynchronizationState());
	}

	public void testFocedQuerySynchronization() throws CoreException {
		init222();
		TasksUiPlugin.getTaskDataStorageManager().clear();
		assertEquals(0, taskList.getAllTasks().size());
		BugzillaRepositoryQuery bugQuery = new BugzillaRepositoryQuery(
				IBugzillaConstants.TEST_BUGZILLA_222_URL,
				"http://mylyn.eclipse.org/bugs222/buglist.cgi?short_desc_type=allwordssubstr&short_desc=&product=Read+Only+Test+Cases&long_desc_type=allwordssubstr&long_desc=&bug_status=NEW&order=Importance",
		"testFocedQuerySynchronization");

		taskList.addQuery(bugQuery);

		TasksUiInternal.synchronizeQuery(connector, bugQuery, null, false);

		assertEquals(1, bugQuery.getChildren().size());
		ITask hit = (ITask) bugQuery.getChildren().toArray()[0];
		assertTrue(TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(hit.getRepositoryUrl(), hit.getTaskId()) != null);
		TasksUiPlugin.getTaskDataStorageManager().remove(hit.getRepositoryUrl(), hit.getTaskId());

		TasksUiInternal.synchronizeQuery(connector, bugQuery, null, true);
		assertEquals(1, bugQuery.getChildren().size());
		hit = (ITask) bugQuery.getChildren().toArray()[0];
		assertTrue(TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(hit.getRepositoryUrl(), hit.getTaskId()) != null);

	}

	public void testCreateTaskFromExistingId() throws Exception {
		init222();
		try {
			TasksUiUtil.createTask(repository, "9999", new NullProgressMonitor());
			fail();
		} catch (CoreException ce) {

		}

		BugzillaTask task = generateLocalTaskAndDownload("1");
		assertNotNull(task);
		assertNotNull(TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId()));
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());

		BugzillaTask retrievedTask = (BugzillaTask) taskList.getTask(task.getHandleIdentifier());
		assertNotNull(retrievedTask);
		assertEquals(task.getHandleIdentifier(), retrievedTask.getHandleIdentifier());
	}

	public void testAnonymousRepositoryAccess() throws Exception {
		init218();
		assertNotNull(repository);
		repository.setAuthenticationCredentials("", "");
		// test anonymous task retrieval
		BugzillaTask task = this.generateLocalTaskAndDownload("2");
		assertNotNull(task);

		// // test anonymous query (note that this demonstrates query via
		// eclipse search (ui)
		// SearchHitCollector collector = new SearchHitCollector(taskList);
		// collector.setProgressMonitor(new NullProgressMonitor());
		// BugzillaSearchOperation operation = new BugzillaSearchOperation(
		// repository,
		// "http://mylyn.eclipse.org/bugs218/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=search-match-test&product=TestProduct&long_desc_type=substring&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&deadlinefrom=&deadlineto=&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&emailassigned_to1=1&emailtype1=substring&email1=&emailassigned_to2=1&emailreporter2=1&emailcc2=1&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=",
		// null, collector, "-1");
		//
		String queryUrl = "http://mylyn.eclipse.org/bugs218/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=search-match-test&product=TestProduct&long_desc_type=substring&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&deadlinefrom=&deadlineto=&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&emailassigned_to1=1&emailtype1=substring&email1=&emailassigned_to2=1&emailreporter2=1&emailcc2=1&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=";
		BugzillaRepositoryQuery bugzillaQuery = new BugzillaRepositoryQuery(repository.getRepositoryUrl(), queryUrl, "search");

		SearchHitCollector collector = new SearchHitCollector(taskList, repository, bugzillaQuery);
		RepositorySearchResult result = (RepositorySearchResult) collector.getSearchResult();

		// operation.run(new NullProgressMonitor());
		// BugzillaSearchQuery searchQuery = new BugzillaSearchQuery(collector);
		collector.run(new NullProgressMonitor());
		assertEquals(2, result.getElements().length);

		for (ITask hit : collector.getTasks()) {
			assertTrue(hit.getSummary().contains("search-match-test"));
		}

		// test anonymous update of configuration
		RepositoryConfiguration config = BugzillaCorePlugin.getRepositoryConfiguration(repository, false, null);
		assertNotNull(config);
		assertTrue(config.getComponents().size() > 0);
	}

	public void testUpdate() throws Exception {
		init222();
		String taskNumber = "3";
		TasksUiPlugin.getTaskDataStorageManager().clear();
		assertEquals(0, TasksUiPlugin.getTaskList().getAllTasks().size());
		BugzillaTask task = this.generateLocalTaskAndDownload(taskNumber);
		assertEquals("search-match-test 2", task.getSummary());
		assertEquals("TestProduct", task.getProduct());
		assertEquals("P1", task.getPriority());
		assertEquals("blocker", task.getSeverity());
		assertEquals("nhapke@cs.ubc.ca", task.getOwner());
		// assertEquals("2007-04-18 14:21:40",
		// task.getCompletionDate().toString());
		assertFalse(task.isCompleted());
		assertEquals("http://mylyn.eclipse.org/bugs222/show_bug.cgi?id=3", task.getUrl());
	}

	public void testUpdateWithSubTasks() throws Exception {
		init222();
		String taskNumber = "23";
		TasksUiPlugin.getTaskDataStorageManager().clear();
		assertEquals(0, TasksUiPlugin.getTaskList().getAllTasks().size());
		BugzillaTask task = this.generateLocalTaskAndDownload(taskNumber);
		assertNotNull(task.getChildren());
		assertEquals(2, task.getChildren().size());
	}

	public void testContextAttachFailure() throws Exception {
		init218();
		BugzillaTask task = this.generateLocalTaskAndDownload("3");
		assertNotNull(task);
		assertNotNull(TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId()));
		TasksUiPlugin.getTaskListManager().activateTask(task);
		File sourceContextFile = ContextCore.getContextManager().getFileForContext(task.getHandleIdentifier());
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
		sourceContextFile.createNewFile();
		sourceContextFile.deleteOnExit();
		repository.setAuthenticationCredentials("wrong", "wrong");
		try {
			AttachmentUtil.attachContext(connector.getAttachmentHandler(), repository, task, "", new NullProgressMonitor());
		} catch (CoreException e) {
			assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
			return;
		}
		fail("Should have failed due to invalid userid and password.");
	}

	public void testSynchronize() throws CoreException {
		init222();

		TasksUiPlugin.getTaskDataStorageManager().clear();

		// Get the task
		BugzillaTask task = generateLocalTaskAndDownload("3");

		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getEditableCopy(task.getRepositoryUrl(),
				task.getTaskId());
		assertNotNull(taskData);

		TasksUiPlugin.getTaskList().addTask(task);
		int numComments = taskData.getComments().size();

		// Modify it
		String newCommentText = "BugzillaRepositoryClientTest.testSynchronize(): " + (new Date()).toString();
		taskData.setNewComment(newCommentText);
		Set<RepositoryTaskAttribute> changed = new HashSet<RepositoryTaskAttribute>();
		changed.add(taskData.getAttribute(RepositoryTaskAttribute.COMMENT_NEW));
		TasksUiPlugin.getTaskDataStorageManager().saveEdits(task.getRepositoryUrl(), task.getTaskId(), changed);

		// Submit changes
		submit(task, taskData);

		TasksUiInternal.synchronizeTask(connector, task, true, null);
		// After submit task should be in SYNCHRONIZED state
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
		RepositoryTaskData taskData2 = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(),
				task.getTaskId());
		assertFalse(taskData2.getLastModified().equals(taskData.getLastModified()));
		// Still not read
		assertFalse(taskData2.getLastModified().equals(task.getLastReadTimeStamp()));
		TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
		assertEquals(taskData2.getLastModified(), task.getLastReadTimeStamp());
		assertTrue(taskData2.getComments().size() > numComments);

		// Has no outgoing changes or conflicts yet needs synch
		// because task doesn't have bug report (new query hit)
		// Result: retrieved with no incoming status
		// task.setSyncState(SynchronizationState.SYNCHRONIZED);
		TasksUiPlugin.getTaskDataStorageManager().remove(task.getRepositoryUrl(), task.getTaskId());
		TasksUiInternal.synchronizeTask(connector, task, false, null);
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
		RepositoryTaskData bugReport2 = null;
		bugReport2 = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
		assertNotNull(bugReport2);
		assertEquals(task.getTaskId(), bugReport2.getTaskId());

		assertEquals(newCommentText, bugReport2.getComments().get(numComments).getText());
		// TODO: Test that comment was appended
		// ArrayList<Comment> comments = task.getTaskData().getComments();
		// assertNotNull(comments);
		// assertTrue(comments.size() > 0);
		// Comment lastComment = comments.get(comments.size() - 1);
		// assertEquals(newCommentText, lastComment.getText());

	}

	public void testUniqueQueryHitObjects() {
		init222();
		BugzillaRepositoryQuery query1 = new BugzillaRepositoryQuery(IBugzillaConstants.TEST_BUGZILLA_222_URL,
				"queryurl", "description1");
		BugzillaTask query1Hit = new BugzillaTask(IBugzillaConstants.TEST_BUGZILLA_222_URL, "1", "description1");
		taskList.addQuery(query1);
		taskList.addTask(query1Hit, query1);

		BugzillaRepositoryQuery query2 = new BugzillaRepositoryQuery(IBugzillaConstants.TEST_BUGZILLA_222_URL,
				"queryurl2", "description2");
		BugzillaTask query2Hit = new BugzillaTask(IBugzillaConstants.TEST_BUGZILLA_222_URL, "1", "description2");
		taskList.addQuery(query2);
		taskList.addTask(query2Hit, query1);

		assertEquals(2, taskList.getQueries().size());
		assertEquals(1, taskList.getAllTasks().size());
		for (ITask hit : query1.getChildren()) {
			for (ITask hit2 : query2.getChildren()) {
				assertTrue(hit.getClass().equals(hit2.getClass()));
			}
		}

		taskList.deleteQuery(query1);
		taskList.deleteQuery(query2);
		assertEquals(1, taskList.getAllTasks().size());
	}

	public void testAttachToExistingReport() throws Exception {
		init222();
		String taskNumber = "33";
		BugzillaTask task = generateLocalTaskAndDownload(taskNumber);
		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(),
				task.getTaskId());

		assertNotNull(task);
		assertNotNull(taskData);
		TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
		assertEquals(taskNumber, taskData.getTaskId());
		int numAttached = taskData.getAttachments().size();
		String fileName = "test-attach-" + System.currentTimeMillis() + ".txt";

		assertNotNull(repository.getUserName());
		assertNotNull(repository.getPassword());

		/* Initialize a local attachment */
		LocalAttachment attachment = new LocalAttachment();
		attachment.setDescription("Test attachment " + new Date());
		attachment.setContentType("text/plain");
		attachment.setPatch(false);
		attachment.setReport(taskData);
		attachment.setComment("Automated JUnit attachment test"); // optional

		/* Test attempt to upload a non-existent file */
		attachment.setFilePath("/this/is/not/a/real-file");
		attachment.setFile(new File(attachment.getFilePath()));
		attachment.setFilename("real-file");
		// IAttachmentHandler attachmentHandler =
		// connector.getAttachmentHandler();
		BugzillaClient client = connector.getClientManager().getClient(repository, new NullProgressMonitor());
		try {
			client.postAttachment(attachment.getReport().getTaskId(), attachment.getComment(), attachment, null);
			fail();
		} catch (Exception e) {
		}
		// attachmentHandler.uploadAttachment(repository, task, comment,
		// summary, file, contentType, isPatch, proxySettings)
		// assertFalse(attachmentHandler.uploadAttachment(attachment,
		// repository.getUserName(), repository.getPassword(),
		// Proxy.NO_PROXY));
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
		task = (BugzillaTask) TasksUiUtil.createTask(repository, taskNumber, new NullProgressMonitor());
		TasksUiInternal.synchronizeTask(connector, task, true, null);

		assertEquals(numAttached, taskData.getAttachments().size());

		/* Test attempt to upload an empty file */
		File attachFile = new File(fileName);
		attachment.setFilePath(attachFile.getAbsolutePath());
		BufferedWriter write = new BufferedWriter(new FileWriter(attachFile));
		attachFile = new File(attachment.getFilePath());
		attachment.setFile(attachFile);
		attachment.setFilename(attachFile.getName());
		// assertFalse(attachmentHandler.uploadAttachment(attachment,
		// repository.getUserName(), repository.getPassword(),
		// Proxy.NO_PROXY));
		try {
			client.postAttachment(attachment.getReport().getTaskId(), attachment.getComment(), attachment, null);
			fail();
		} catch (Exception e) {
		}
		task = (BugzillaTask) TasksUiUtil.createTask(repository, taskNumber, new NullProgressMonitor());
		TasksUiInternal.synchronizeTask(connector, task, true, null);
		taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
		assertEquals(numAttached, taskData.getAttachments().size());

		/* Test uploading a proper file */
		write.write("test file");
		write.close();
		attachment.setFilePath(attachFile.getAbsolutePath());
		// assertTrue(attachmentHandler.uploadAttachment(attachment,
		// repository.getUserName(), repository.getPassword(),
		// Proxy.NO_PROXY));
		File fileToAttach = new File(attachment.getFilePath());
		assertTrue(fileToAttach.exists());
		attachment.setFile(fileToAttach);
		attachment.setFilename(fileToAttach.getName());
		client.postAttachment(attachment.getReport().getTaskId(), attachment.getComment(), attachment, null);

		task = (BugzillaTask) TasksUiUtil.createTask(repository, taskNumber, new NullProgressMonitor());
		TasksUiInternal.synchronizeTask(connector, task, true, null);
		taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
		assertEquals(numAttached + 1, taskData.getAttachments().size());

		// use assertion to track clean-up
		assertTrue(attachFile.delete());
	}

	public void testSynchChangedReports() throws Exception {

		init222();
		String taskID = "4";
		BugzillaTask task4 = generateLocalTaskAndDownload(taskID);
		RepositoryTaskData taskData4 = TasksUiPlugin.getTaskDataStorageManager().getEditableCopy(task4.getRepositoryUrl(),
				task4.getTaskId());
		assertNotNull(task4);
		assertNotNull(taskData4);
		assertEquals(SynchronizationState.SYNCHRONIZED, task4.getSynchronizationState());
		assertEquals(taskID, taskData4.getTaskId());

		BugzillaTask task5 = generateLocalTaskAndDownload("5");
		RepositoryTaskData taskData5 = TasksUiPlugin.getTaskDataStorageManager().getEditableCopy(task5.getRepositoryUrl(),
				task5.getTaskId());
		assertNotNull(task5);
		assertNotNull(taskData5);
		assertEquals(SynchronizationState.SYNCHRONIZED, task5.getSynchronizationState());
		assertEquals("5", taskData5.getTaskId());

		Set<ITask> tasks = new HashSet<ITask>();
		tasks.add(task4);
		tasks.add(task5);

		// Precondition for test passing is that task5's modification data is
		// AFTER
		// task4's

		TasksUiPlugin.getRepositoryManager().setSynchronizationTime(repository, task5.getLastReadTimeStamp(),
				TasksUiPlugin.getDefault().getRepositoriesFilePath());

		SynchronizationContext event = new SynchronizationContext();
		event.tasks = tasks;
		event.performQueries = true;
		event.taskRepository = repository;
		event.fullSynchronization = true;
		connector.preSynchronization(event, null);
		assertTrue(event.performQueries);
		// Always last known changed returned
		assertFalse(task4.isStale());
		assertTrue(task5.isStale());

		String priority4 = null;
		if (task4.getPriority().equals("P1")) {
			priority4 = "P2";
			taskData4.setAttributeValue(BugzillaReportElement.PRIORITY.getKeyString(), priority4);
		} else {
			priority4 = "P1";
			taskData4.setAttributeValue(BugzillaReportElement.PRIORITY.getKeyString(), priority4);
		}

		String priority5 = null;
		if (task5.getPriority().equals("P1")) {
			priority5 = "P2";
			taskData5.setAttributeValue(BugzillaReportElement.PRIORITY.getKeyString(), priority5);
		} else {
			priority5 = "P1";
			taskData5.setAttributeValue(BugzillaReportElement.PRIORITY.getKeyString(), priority5);
		}

		assertNotNull(repository.getUserName());
		assertNotNull(repository.getPassword());

		submit(task4, taskData4);
		submit(task5, taskData5);

		event = new SynchronizationContext();
		event.tasks = tasks;
		event.performQueries = true;
		event.taskRepository = repository;
		event.fullSynchronization = true;
		connector.preSynchronization(event, null);

		assertTrue(task4.isStale());
		assertTrue(task5.isStale());

		TasksUiInternal.synchronizeTasks(connector, tasks, true, null);

		for (ITask task : tasks) {
			if (task.getTaskId() == "4") {
				assertEquals(priority4, task4.getPriority());
			}
			if (task.getTaskId() == "5") {
				assertEquals(priority5, task5.getPriority());
			}
		}
	}

	public void testIncomingWhenOfflineDeleted() throws Exception {

		init222();
		BugzillaTask task7 = generateLocalTaskAndDownload("7");
		RepositoryTaskData recentTaskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task7.getRepositoryUrl(),
				task7.getTaskId());
		assertNotNull(recentTaskData);
		TasksUiPlugin.getTaskDataManager().setTaskRead(task7, true);
		assertEquals(SynchronizationState.SYNCHRONIZED, task7.getSynchronizationState());
		assertEquals("7", recentTaskData.getTaskId());

		Set<AbstractTask> tasks = new HashSet<AbstractTask>();
		tasks.add(task7);

		TasksUiPlugin.getRepositoryManager().setSynchronizationTime(repository, task7.getLastReadTimeStamp(),
				TasksUiPlugin.getDefault().getRepositoriesFilePath());

		assertNotNull(TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(IBugzillaConstants.TEST_BUGZILLA_222_URL, "7"));
		TasksUiPlugin.getTaskDataStorageManager().remove(task7.getRepositoryUrl(), task7.getTaskId());

		assertNull(TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(IBugzillaConstants.TEST_BUGZILLA_222_URL, "7"));

		assertEquals(SynchronizationState.SYNCHRONIZED, task7.getSynchronizationState());
		assertNotNull(task7.getLastReadTimeStamp());
		// Task no longer stored offline
		// make an external change
		assertNotNull(repository.getUserName());
		assertNotNull(repository.getPassword());

		String priority = null;
		if (task7.getPriority().equals("P1")) {
			priority = "P2";
			recentTaskData.setAttributeValue(BugzillaReportElement.PRIORITY.getKeyString(), priority);
		} else {
			priority = "P1";
			recentTaskData.setAttributeValue(BugzillaReportElement.PRIORITY.getKeyString(), priority);
		}

		// disabled due to TasksUi.synchronizeChanged(connector, repository) being removed
		// REMOVE ALL TASK DATA
//		TasksUiPlugin.getTaskDataManager().clear();
//		connector.getTaskDataHandler().postTaskData(repository, recentTaskData, new NullProgressMonitor());
//		TasksUi.synchronizeChanged(connector, repository);
//		assertEquals(SynchronizationState.INCOMING, task7.getSynchronizationState());
	}

	public void testTimeTracker222() throws Exception {
		init222();
		timeTracker(15, true);
	}

	// We'll skip these two for now and just test 222 and 218 since
	// they are the most common. If problems arise we can re-enable.
	// public void testTimeTracker2201() throws Exception {
	// init2201();
	// timeTracker(22, true);
	// }
	//
	// public void testTimeTracker220() throws Exception {
	// init220();
	// timeTracker(8, true);
	// }

	public void testTimeTracker218() throws Exception {
		init218();
		timeTracker(20, false);
	}

	/**
	 * @param enableDeadline
	 *            bugzilla 218 doesn't support deadlines
	 */
	protected void timeTracker(int taskid, boolean enableDeadline) throws Exception {
		BugzillaTask bugtask = generateLocalTaskAndDownload("" + taskid);
		RepositoryTaskData bugtaskdata = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(bugtask.getRepositoryUrl(),
				bugtask.getTaskId());
		assertNotNull(bugtaskdata);
		assertEquals(taskid + "", bugtaskdata.getTaskId());
		assertEquals(SynchronizationState.SYNCHRONIZED, bugtask.getSynchronizationState());

		Set<AbstractTask> tasks = new HashSet<AbstractTask>();
		tasks.add(bugtask);

		// synchAndAssertState(tasks, SynchronizationState.SYNCHRONIZED);

		TasksUiPlugin.getRepositoryManager().setSynchronizationTime(repository, bugtask.getLastReadTimeStamp(),
				TasksUiPlugin.getDefault().getRepositoriesFilePath());
		// connector.synchronizeChanged(repository);

		// Set<AbstractTask> changedTasks =
		// connector.getOfflineTaskHandler().getChangedSinceLastSync(repository,
		// tasks);
		// assertEquals(1, changedTasks.size());

		assertNotNull(repository.getUserName());
		assertNotNull(repository.getPassword());

		float estimatedTime, remainingTime, actualTime, addTime;
		String deadline = null;

		estimatedTime = Float.parseFloat(bugtaskdata.getAttributeValue(BugzillaReportElement.ESTIMATED_TIME.getKeyString()));
		remainingTime = Float.parseFloat(bugtaskdata.getAttributeValue(BugzillaReportElement.REMAINING_TIME.getKeyString()));
		actualTime = Float.parseFloat(bugtaskdata.getAttributeValue(BugzillaReportElement.ACTUAL_TIME.getKeyString()));
		if (enableDeadline) {
			deadline = bugtaskdata.getAttributeValue(BugzillaReportElement.DEADLINE.getKeyString());
		}

		estimatedTime += 2;
		remainingTime += 1.5;
		addTime = 0.75f;
		if (enableDeadline) {
			deadline = generateNewDay();
		}

		bugtaskdata.setAttributeValue(BugzillaReportElement.ESTIMATED_TIME.getKeyString(), "" + estimatedTime);
		bugtaskdata.setAttributeValue(BugzillaReportElement.REMAINING_TIME.getKeyString(), "" + remainingTime);
		bugtaskdata.setAttributeValue(BugzillaReportElement.WORK_TIME.getKeyString(), "" + addTime);
		if (enableDeadline) {
			bugtaskdata.setAttributeValue(BugzillaReportElement.DEADLINE.getKeyString(), deadline);
		}

//		for (AbstractTask task : tasks) {
//			RepositoryTaskData taskData = TasksUiPlugin.getTaskDataManager().getNewTaskData(
//					task.getHandleIdentifier());
		bugtaskdata.setAttributeValue(BugzillaReportElement.ADD_COMMENT.getKeyString(), "New Estimate: "
				+ estimatedTime + "\nNew Remaining: " + remainingTime + "\nAdd: " + addTime);
		submit(bugtask, bugtaskdata);
//		}

		synchAndAssertState(tasks, SynchronizationState.SYNCHRONIZED);

		bugtaskdata = TasksUiPlugin.getTaskDataStorageManager()
		.getNewTaskData(bugtask.getRepositoryUrl(), bugtask.getTaskId());

		assertEquals(estimatedTime,
				Float.parseFloat(bugtaskdata.getAttributeValue(BugzillaReportElement.ESTIMATED_TIME.getKeyString())));
		assertEquals(remainingTime,
				Float.parseFloat(bugtaskdata.getAttributeValue(BugzillaReportElement.REMAINING_TIME.getKeyString())));
		assertEquals(actualTime + addTime,
				Float.parseFloat(bugtaskdata.getAttributeValue(BugzillaReportElement.ACTUAL_TIME.getKeyString())));
		if (enableDeadline) {
			assertEquals(deadline, bugtaskdata.getAttributeValue(BugzillaReportElement.DEADLINE.getKeyString()));
		}

	}

	private String generateNewDay() {
		int year = 2006;
		int month = (int) (Math.random() * 12 + 1);
		int day = (int) (Math.random() * 28 + 1);
		return "" + year + "-" + ((month <= 9) ? "0" : "") + month + "-" + ((day <= 9) ? "0" : "") + day;
	}

	/**
	 * Ensure obsoletes and patches are marked as such by the parser.
	 */
	public void testAttachmentAttributes() throws Exception {
		init222();
		int bugId = 19;
		String taskNumber = "" + bugId;
		BugzillaTask task = generateLocalTaskAndDownload(taskNumber);

		// TasksUiPlugin.getSynchronizationManager().synchronize(connector,
		// task, true, null);

		assertNotNull(task);

		boolean isPatch[] = { false, true, false, false, false, false, false, true, false, false };
		boolean isObsolete[] = { false, true, false, true, false, false, false, false, false, false };

		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(),
				task.getTaskId());

		Iterator<RepositoryAttachment> iter = taskData.getAttachments().iterator();

		int index = 0;
		while (iter.hasNext()) {
			assertTrue(validateAttachmentAttributes(iter.next(), isPatch[index], isObsolete[index]));
			index++;
		}
	}

	private boolean validateAttachmentAttributes(RepositoryAttachment att, boolean isPatch, boolean isObsolete) {
		return (att.isPatch() == isPatch) && (att.isObsolete() == isObsolete);
	}

// public void testSimpleLoad() throws Exception {
// repository = new TaskRepository(DEFAULT_KIND,
// IBugzillaConstants.ECLIPSE_BUGZILLA_URL);
// //Credentials credentials = MylynTestUtils.readCredentials();
// //repository.setAuthenticationCredentials(credentials.username,
// credentials.password);
//
// //repository.setTimeZoneId("Canada/Eastern");
// assertNotNull(manager);
// manager.addRepository(repository,
// TasksUiPlugin.getDefault().getRepositoriesFilePath());
//
// taskList = TasksUiPlugin.getTaskList();
//
// AbstractRepositoryConnector abstractRepositoryConnector =
// manager.getRepositoryConnector(DEFAULT_KIND);
//
// assertEquals(abstractRepositoryConnector.getRepositoryType(), DEFAULT_KIND);
//
// connector = (BugzillaRepositoryConnector) abstractRepositoryConnector;
//
// long start = System.currentTimeMillis();
// BugzillaTask task = null;
// for(int x = 1; x < 5; x++) {
// if(task != null)
// taskList.deleteTask(task);
//
// task = this.generateLocalTaskAndDownload("154100");
// assertNotNull(task);
// }
// System.err.println("Total: "+((System.currentTimeMillis() - start)/1000));
// }

	// class MockBugzillaReportSubmitForm extends BugzillaReportSubmitForm {
	//
	// public MockBugzillaReportSubmitForm(String encoding_utf_8) {
	// super(encoding_utf_8);
	// }
	//
	// @Override
	// public String submitReportToRepository() throws BugzillaException,
	// LoginException,
	// PossibleBugzillaFailureException {
	// return "test-submit";
	// }
	//
	// }
}
