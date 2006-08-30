/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.bugzilla.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaAttachmentHandler;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaException;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportSubmitForm;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.PossibleBugzillaFailureException;
import org.eclipse.mylar.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylar.internal.bugzilla.ui.editor.BugSubmissionHandler;
import org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaResultCollector;
import org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaSearchOperation;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaQueryHit;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.internal.tasks.core.SslProtocolSocketFactory;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.LocalAttachment;
import org.eclipse.mylar.tasks.core.RepositoryAttachment;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.PartInitException;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Nathan Hapke
 */
public class BugzillaRepositoryConnectorTest extends AbstractBugzillaTest {

	private BugzillaAttachmentHandler attachmentHandler = new BugzillaAttachmentHandler();

	public void testCreateTaskFromExistingId() throws Exception {
		init222();
		BugzillaTask badId = (BugzillaTask) connector.createTaskFromExistingKey(repository, "bad-id");
		assertNull(badId);

		BugzillaTask task = (BugzillaTask) connector.createTaskFromExistingKey(repository, "1");
		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);
		assertNotNull(task);
		// assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
		// connector.synchronize(task, true, null);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());

		BugzillaTask retrievedTask = (BugzillaTask) taskList.getTask(task.getHandleIdentifier());
		assertNotNull(retrievedTask);
		assertEquals(task.getHandleIdentifier(), retrievedTask.getHandleIdentifier());

		assertTrue(task.isDownloaded());
		assertEquals(1, Integer.parseInt(task.getTaskData().getId()));
	}
	
	public void testAnonymousRepositoryAccess() throws Exception {
		init218();
		assertNotNull(repository);
		repository.setAuthenticationCredentials("", "");
		// test anonymous task retrieval
		BugzillaTask task = this.generateLocalTaskAndDownload("2");
		assertNotNull(task);
		
		// test anonymous query
		BugzillaResultCollector collector = new BugzillaResultCollector();
		collector.setProgressMonitor(new NullProgressMonitor());
		BugzillaSearchOperation operation = new BugzillaSearchOperation(
				repository,
				"http://mylar.eclipse.org/bugs218/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=search-match-test&product=TestProduct&long_desc_type=substring&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&deadlinefrom=&deadlineto=&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&emailassigned_to1=1&emailtype1=substring&email1=&emailassigned_to2=1&emailreporter2=1&emailcc2=1&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=",
				null, collector, "-1");
		operation.run(new NullProgressMonitor());

		assertEquals(2, collector.getResults().size());		
		
		//test anonymous update of configuration
		RepositoryConfiguration config = BugzillaCorePlugin.getRepositoryConfiguration(true, repository.getUrl(), TasksUiPlugin.getDefault().getProxySettings(), repository.getUserName(), repository.getPassword(), repository.getCharacterEncoding());
		assertNotNull(config);
		assertTrue(config.getComponents().size() > 0);		
	}
	
	public void testContextAttachFailure() throws Exception {
		init218();
		BugzillaTask task = (BugzillaTask) connector.createTaskFromExistingKey(repository, "3");
		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);
				
		assertNotNull(task.getTaskData());
		TasksUiPlugin.getTaskListManager().activateTask(task);
		File sourceContextFile = ContextCorePlugin.getContextManager().getFileForContext(task.getHandleIdentifier());
		sourceContextFile.createNewFile();
		sourceContextFile.deleteOnExit();
		repository.setAuthenticationCredentials("wrong", "wrong");
		try {
			connector.attachContext(repository, task, "", TasksUiPlugin.getDefault().getProxySettings());
		} catch (CoreException e) {
			assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
			assertNotNull(task.getTaskData());
			return;
		}
		fail("Should have failed due to invalid userid and password.");
	}

	public void testSynchronize() throws InterruptedException, PartInitException, LoginException, BugzillaException,
			PossibleBugzillaFailureException {
		init222();

		// Get the task
		BugzillaTask task = (BugzillaTask) connector.createTaskFromExistingKey(repository, "1");
		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);
				
		TasksUiPlugin.getTaskListManager().getTaskList().moveToRoot(task);
		assertTrue(task.isDownloaded());
		// (The initial local copy from server)
		// assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
		// connector.synchronize(task, true, null);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		// Modify it
		String newCommentText = "BugzillaRepositoryClientTest.testSynchronize(): " + (new Date()).toString();
		task.getTaskData().setNewComment(newCommentText);
		// overwrites old fields/attributes with new content (ususually done by
		// BugEditor)
		task.getTaskData().setHasLocalChanges(true);
		task.setSyncState(RepositoryTaskSyncState.OUTGOING);
		TasksUiPlugin.getSynchronizationManager().saveOffline(task.getTaskData());
		assertEquals(RepositoryTaskSyncState.OUTGOING, task.getSyncState());

		// Submit changes
		MockBugzillaReportSubmitForm form = new MockBugzillaReportSubmitForm(BugzillaCorePlugin.ENCODING_UTF_8);
		form.setTaskData(task.getTaskData());
		new BugSubmissionHandler(connector).submitBugReport(form, null, true);
		// submit casuses a synch which should result in
		// a synchronized state (not incoming)
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());

		// TODO: Test that comment was appended
		// ArrayList<Comment> comments = task.getTaskData().getComments();
		// assertNotNull(comments);
		// assertTrue(comments.size() > 0);
		// Comment lastComment = comments.get(comments.size() - 1);
		// assertEquals(newCommentText, lastComment.getText());

		// OUTGOING with forceddSynch=false
		task.setSyncState(RepositoryTaskSyncState.OUTGOING);
		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, false, null);
		assertEquals(RepositoryTaskSyncState.OUTGOING, task.getSyncState());

		// OUTGOING with forcedSynch=true --> Update Local Copy dialog
		// Choosing to override local changes results in SYNCHRONIZED
		// Choosing not to override results in CONFLICT

		task.setSyncState(RepositoryTaskSyncState.CONFLICT);
		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, false, null);
		assertEquals(RepositoryTaskSyncState.CONFLICT, task.getSyncState());

		// CONFLICT with forcedSynch=true --> Update Local Copy dialog

		// Has no outgoing changes or conflicts yet needs synch
		// because task doesn't have bug report (new query hit)
		// Result: retrieved with no incoming status
		task.setSyncState(RepositoryTaskSyncState.SYNCHRONIZED);
		RepositoryTaskData bugReport = task.getTaskData();
		task.setTaskData(null);
		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, false, null);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		assertNotNull(task.getTaskData());
		assertEquals(task.getTaskData().getId(), bugReport.getId());
	}

	public void testUniqueTaskObjects() {
		init222();
		String repositoryURL = "repositoryURL";
		BugzillaQueryHit hit1 = new BugzillaQueryHit("description", "P1", repositoryURL, "1", null, "status");
		ITask task1 = hit1.getOrCreateCorrespondingTask();
		assertNotNull(task1);
		// taskList.renameTask(task1, "testing");
		// task1.setDescription("testing");

		BugzillaQueryHit hit1Twin = new BugzillaQueryHit("description", "P1", repositoryURL, "1", null, "status");
		ITask task2 = hit1Twin.getOrCreateCorrespondingTask();
		assertEquals(task1.getDescription(), task2.getDescription());
	}

	public void testAttachToExistingReport() throws Exception {
		init222();
		int bugId = 31;
		String taskNumber = "" + bugId;
		BugzillaTask task = (BugzillaTask) connector.createTaskFromExistingKey(repository, taskNumber);
		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);
				
		assertNotNull(task);
		assertTrue(task.isDownloaded());
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		assertEquals(bugId, Integer.parseInt(task.getTaskData().getId()));
		int numAttached = task.getTaskData().getAttachments().size();
		String fileName = "test-attach-" + System.currentTimeMillis() + ".txt";

		assertNotNull(repository.getUserName());
		assertNotNull(repository.getPassword());

		/* Initialize a local attachment */
		LocalAttachment attachment = new LocalAttachment();
		attachment.setDescription("Test attachment " + new Date());
		attachment.setContentType("text/plain");
		attachment.setPatch(false);
		attachment.setReport(task.getTaskData());
		attachment.setComment("Automated JUnit attachment test"); // optional

		/* Test attempt to upload a non-existent file */
		attachment.setFilePath("/this/is/not/a/real-file");
		assertFalse(attachmentHandler.uploadAttachment(attachment, repository.getUserName(), repository.getPassword(),
				Proxy.NO_PROXY));
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		task = (BugzillaTask) connector.createTaskFromExistingKey(repository, taskNumber);
		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);
				
		assertEquals(numAttached, task.getTaskData().getAttachments().size());

		/* Test attempt to upload an empty file */
		File attachFile = new File(fileName);
		attachment.setFilePath(attachFile.getAbsolutePath());
		BufferedWriter write = new BufferedWriter(new FileWriter(attachFile));
		assertFalse(attachmentHandler.uploadAttachment(attachment, repository.getUserName(), repository.getPassword(),
				Proxy.NO_PROXY));
		task = (BugzillaTask) connector.createTaskFromExistingKey(repository, taskNumber);
		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);
		
		assertEquals(numAttached, task.getTaskData().getAttachments().size());

		/* Test uploading a proper file */
		write.write("test file");
		write.close();
		attachment.setFilePath(attachFile.getAbsolutePath());
		assertTrue(attachmentHandler.uploadAttachment(attachment, repository.getUserName(), repository.getPassword(),
				Proxy.NO_PROXY));
		task = (BugzillaTask) connector.createTaskFromExistingKey(repository, taskNumber);
		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);
		assertEquals(numAttached + 1, task.getTaskData().getAttachments().size());

		// use assertion to track clean-up
		assertTrue(attachFile.delete());
	}

	public void testSynchChangedReports() throws Exception {

		init222();
		BugzillaTask task4 = generateLocalTaskAndDownload("4");
		assertNotNull(task4.getTaskData());
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task4.getSyncState());
		assertEquals(4, Integer.parseInt(task4.getTaskData().getId()));

		BugzillaTask task5 = generateLocalTaskAndDownload("5");
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task5.getSyncState());
		assertEquals(5, Integer.parseInt(task5.getTaskData().getId()));

		Set<AbstractRepositoryTask> tasks = new HashSet<AbstractRepositoryTask>();
		tasks.add(task4);
		tasks.add(task5);

		TasksUiPlugin.getRepositoryManager().setSyncTime(repository, task5.getLastModifiedDateStamp(), TasksUiPlugin.getDefault().getRepositoriesFilePath());

		Set<AbstractRepositoryTask> changedTasks = connector.getOfflineTaskHandler().getChangedSinceLastSync(repository,
				tasks);
		assertEquals(1, changedTasks.size());

		String priority4 = null;
		if (task4.getPriority().equals("P1")) {
			priority4 = "P2";
			task4.getTaskData().setAttributeValue(BugzillaReportElement.PRIORITY.getKeyString(), priority4);
		} else {
			priority4 = "P1";
			task4.getTaskData().setAttributeValue(BugzillaReportElement.PRIORITY.getKeyString(), priority4);
		}

		String priority5 = null;
		if (task5.getPriority().equals("P1")) {
			priority5 = "P2";
			task5.getTaskData().setAttributeValue(BugzillaReportElement.PRIORITY.getKeyString(), priority5);
		} else {
			priority5 = "P1";
			task5.getTaskData().setAttributeValue(BugzillaReportElement.PRIORITY.getKeyString(), priority5);
		}

		assertNotNull(repository.getUserName());
		assertNotNull(repository.getPassword());

		BugzillaReportSubmitForm bugzillaReportSubmitForm;

		
		bugzillaReportSubmitForm = makeExistingBugPost(task4.getTaskData());
		bugzillaReportSubmitForm.submitReportToRepository();
		bugzillaReportSubmitForm = makeExistingBugPost(task5.getTaskData());
		bugzillaReportSubmitForm.submitReportToRepository();
		

		changedTasks = connector.getOfflineTaskHandler().getChangedSinceLastSync(repository, tasks);
		assertEquals("Changed reports expected ", 2, changedTasks.size());
		assertTrue(tasks.containsAll(changedTasks));
		for (AbstractRepositoryTask task : changedTasks) {
			if (task.getTaskData().getId() == "4") {
				assertEquals(priority4, task4.getPriority());
			}
			if (task.getTaskData().getId() == "5") {
				assertEquals(priority5, task5.getPriority());
			}
		}
		// synchAndAssertState(tasks, RepositoryTaskSyncState.INCOMING);
		// synchAndAssertState(tasks, RepositoryTaskSyncState.SYNCHRONIZED);
	}

	public void testIncomingWhenOfflineDeleted() throws Exception {

		init222();
		BugzillaTask task7 = generateLocalTaskAndDownload("7");
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task7.getSyncState());
		assertEquals(7, Integer.parseInt(task7.getTaskData().getId()));

		Set<AbstractRepositoryTask> tasks = new HashSet<AbstractRepositoryTask>();
		tasks.add(task7);

		RepositoryTaskData recentTaskData = task7.getTaskData();
		assertNotNull(recentTaskData);

		TasksUiPlugin.getRepositoryManager().setSyncTime(repository, task7.getLastModifiedDateStamp(), TasksUiPlugin.getDefault().getRepositoriesFilePath());

		assertFalse(TasksUiPlugin.getDefault().getOfflineReportsFile().find(IBugzillaConstants.TEST_BUGZILLA_222_URL,
				"7") == -1);
		ArrayList<RepositoryTaskData> taskDataList = new ArrayList<RepositoryTaskData>();
		taskDataList.add(task7.getTaskData());
		TasksUiPlugin.getDefault().getOfflineReportsFile().remove(taskDataList);
		assertTrue(TasksUiPlugin.getDefault().getOfflineReportsFile().find(IBugzillaConstants.TEST_BUGZILLA_222_URL,
				"7") == -1);

		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task7.getSyncState());
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

		BugzillaReportSubmitForm bugzillaReportSubmitForm;
		bugzillaReportSubmitForm = makeExistingBugPost(recentTaskData);
		bugzillaReportSubmitForm.submitReportToRepository();
		TasksUiPlugin.getSynchronizationManager().synchronizeChanged(connector, repository);
		assertEquals(RepositoryTaskSyncState.INCOMING, task7.getSyncState());
	}

	public void testTimeTracker222() throws Exception {
		init222();
		timeTracker(14, true);
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
		timeTracker(18, false);
	}

	/**
	 * @param enableDeadline
	 *            bugzilla 218 doesn't support deadlines
	 */
	protected void timeTracker(int taskid, boolean enableDeadline) throws Exception {
		BugzillaTask bugtask = generateLocalTaskAndDownload("" + taskid);
		assertEquals(taskid, Integer.parseInt(bugtask.getTaskData().getId()));
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, bugtask.getSyncState());

		Set<AbstractRepositoryTask> tasks = new HashSet<AbstractRepositoryTask>();
		tasks.add(bugtask);

		// synchAndAssertState(tasks, RepositoryTaskSyncState.SYNCHRONIZED);

		TasksUiPlugin.getRepositoryManager().setSyncTime(repository, bugtask.getLastModifiedDateStamp(), TasksUiPlugin.getDefault().getRepositoriesFilePath());
		// connector.synchronizeChanged(repository);

		// Set<AbstractRepositoryTask> changedTasks =
		// connector.getOfflineTaskHandler().getChangedSinceLastSync(repository,
		// tasks);
		// assertEquals(1, changedTasks.size());

		assertNotNull(repository.getUserName());
		assertNotNull(repository.getPassword());

		float estimatedTime, remainingTime, actualTime, addTime;
		String deadline = null;

		RepositoryTaskData bugtaskdata = bugtask.getTaskData();
		estimatedTime = Float.parseFloat(bugtaskdata.getAttributeValue(BugzillaReportElement.ESTIMATED_TIME
				.getKeyString()));
		remainingTime = Float.parseFloat(bugtaskdata.getAttributeValue(BugzillaReportElement.REMAINING_TIME
				.getKeyString()));
		actualTime = Float.parseFloat(bugtaskdata.getAttributeValue(BugzillaReportElement.ACTUAL_TIME.getKeyString()));
		if (enableDeadline)
			deadline = bugtaskdata.getAttributeValue(BugzillaReportElement.DEADLINE.getKeyString());

		estimatedTime += 2;
		remainingTime += 1.5;
		addTime = 0.75f;
		if (enableDeadline)
			deadline = generateNewDay();

		bugtaskdata.setAttributeValue(BugzillaReportElement.ESTIMATED_TIME.getKeyString(), "" + estimatedTime);
		bugtaskdata.setAttributeValue(BugzillaReportElement.REMAINING_TIME.getKeyString(), "" + remainingTime);
		bugtaskdata.setAttributeValue(BugzillaReportElement.WORK_TIME.getKeyString(), "" + addTime);
		if (enableDeadline)
			bugtaskdata.setAttributeValue(BugzillaReportElement.DEADLINE.getKeyString(), deadline);

		BugzillaReportSubmitForm bugzillaReportSubmitForm;

		for (AbstractRepositoryTask task : tasks) {
			task.getTaskData().setAttributeValue(BugzillaReportElement.ADD_COMMENT.getKeyString(),
					"New Estimate: " + estimatedTime + "\nNew Remaining: " + remainingTime + "\nAdd: " + addTime);
			bugzillaReportSubmitForm = makeExistingBugPost(task.getTaskData());
			bugzillaReportSubmitForm.submitReportToRepository();
		}

		// assertEquals("Changed reports expected ", 1,
		// connector.getOfflineTaskHandler().getChangedSinceLastSync(repository,
		// tasks).size());

		synchAndAssertState(tasks, RepositoryTaskSyncState.INCOMING);
		// synchAndAssertState(tasks, RepositoryTaskSyncState.SYNCHRONIZED);

		bugtaskdata = bugtask.getTaskData();

		assertEquals(estimatedTime, Float.parseFloat(bugtaskdata.getAttributeValue(BugzillaReportElement.ESTIMATED_TIME
				.getKeyString())));
		assertEquals(remainingTime, Float.parseFloat(bugtaskdata.getAttributeValue(BugzillaReportElement.REMAINING_TIME
				.getKeyString())));
		assertEquals(actualTime + addTime, Float.parseFloat(bugtaskdata
				.getAttributeValue(BugzillaReportElement.ACTUAL_TIME.getKeyString())));
		if (enableDeadline)
			assertEquals(deadline, bugtaskdata.getAttributeValue(BugzillaReportElement.DEADLINE.getKeyString()));

	}

	public void testTrustAllSslProtocolSocketFactory() throws Exception {
		SslProtocolSocketFactory factory = new SslProtocolSocketFactory();
		Socket s;

		s = factory.createSocket("mylar.eclipse.org", 80);
		assertNotNull(s);
		assertTrue(s.isConnected());
		s.close();

		InetAddress anyHost = new Socket().getLocalAddress();

		s = factory.createSocket("mylar.eclipse.org", 80, anyHost, 0);
		assertNotNull(s);
		assertTrue(s.isConnected());
		s.close();

		HttpConnectionParams params = new HttpConnectionParams();
		s = factory.createSocket("mylar.eclipse.org", 80, anyHost, 0, null);
		assertNotNull(s);
		assertTrue(s.isConnected());
		s.close();

		params.setConnectionTimeout(1000);
		s = factory.createSocket("mylar.eclipse.org", 80, anyHost, 0, params);
		assertNotNull(s);
		assertTrue(s.isConnected());
		s.close();
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
		BugzillaTask task = (BugzillaTask) connector.createTaskFromExistingKey(repository, taskNumber);
		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);
				
		assertNotNull(task);

		boolean isPatch[] = { false, true, false, false, false, false, false, true, false, false };
		boolean isObsolete[] = { false, true, false, true, false, false, false, false, false, false };

		Iterator<RepositoryAttachment> iter = task.getTaskData().getAttachments().iterator();
		int index = 0;
		while (iter.hasNext()) {
			assertTrue(validateAttachmentAttributes(iter.next(), isPatch[index], isObsolete[index]));
			index++;
		}
	}
	
	private boolean validateAttachmentAttributes(RepositoryAttachment att, boolean isPatch, boolean isObsolete) {
		return (att.isPatch() == isPatch) && (att.isObsolete() == isObsolete);
	}

//	class MockBugzillaReportSubmitForm extends BugzillaReportSubmitForm {
//
//		public MockBugzillaReportSubmitForm(String encoding_utf_8) {
//			super(encoding_utf_8);
//		}
//
//		@Override
//		public String submitReportToRepository() throws BugzillaException, LoginException,
//				PossibleBugzillaFailureException {
//			return "test-submit";
//		}
//
//	}
}