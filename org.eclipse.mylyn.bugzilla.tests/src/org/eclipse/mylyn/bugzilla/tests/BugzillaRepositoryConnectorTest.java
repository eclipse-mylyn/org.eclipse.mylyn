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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.security.auth.login.LoginException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaAttachmentHandler;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaException;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportSubmitForm;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.PossibleBugzillaFailureException;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaQueryHit;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaRepositoryConnector;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.internal.tasklist.LocalAttachment;
import org.eclipse.mylar.internal.tasklist.RepositoryTaskData;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskList;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.mylar.provisional.tasklist.TaskRepositoryManager;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.ui.PartInitException;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Nathan Hapke
 */
public class BugzillaRepositoryConnectorTest extends TestCase {

	private static final String DEFAULT_KIND = BugzillaPlugin.REPOSITORY_KIND;

	private BugzillaRepositoryConnector client;

	private TaskRepositoryManager manager;

	private TaskRepository repository;

	private TaskList taskList;

	private BugzillaAttachmentHandler attachmentHandler = new BugzillaAttachmentHandler();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = MylarTaskListPlugin.getRepositoryManager();
		manager.clearRepositories();
	}

	protected void init222() {
		init(IBugzillaConstants.TEST_BUGZILLA_222_URL);
	}
	protected void init2201() {
		init(IBugzillaConstants.TEST_BUGZILLA_2201_URL);
	}
	protected void init220() {
		init(IBugzillaConstants.TEST_BUGZILLA_220_URL);
	}
	protected void init218() {
		init(IBugzillaConstants.TEST_BUGZILLA_218_URL);
	}
	
	protected void init (String url) {
		repository = new TaskRepository(DEFAULT_KIND, url);
		// Valid user name and password must be set for tests to pass
		try {
			Properties properties = new Properties();
			URL localURL = FileLocator.toFileURL(BugzillaTestPlugin.getDefault().getBundle().getEntry(
					"credentials.properties"));
			properties.load(new FileInputStream(new File(localURL.getFile())));
			repository.setAuthenticationCredentials(properties.getProperty("username"), properties
					.getProperty("password"));
		} catch (Throwable t) {
			fail("must define credentials in <plug-in dir>/credentials.properties");
		}

		repository.setTimeZoneId("Canada/Eastern");
		manager.addRepository(repository);
		assertNotNull(manager);
		taskList = MylarTaskListPlugin.getTaskListManager().getTaskList();

		AbstractRepositoryConnector abstractRepositoryClient = manager.getRepositoryConnector(DEFAULT_KIND);

		assertEquals(abstractRepositoryClient.getRepositoryType(), DEFAULT_KIND);

		client = (BugzillaRepositoryConnector) abstractRepositoryClient;
		client.setForceSyncExec(true);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		// taskList.clearArchive();
		// client.clearAllRefreshes();
		MylarTaskListPlugin.getTaskListManager().resetTaskList();
		manager.clearRepositories();

	}

	public void testCreateTaskFromExistingId() throws MalformedURLException, InterruptedException {
		init222();
		BugzillaTask badId = (BugzillaTask) client.createTaskFromExistingKey(repository, "bad-id");
		assertNull(badId);

		BugzillaTask task = (BugzillaTask) client.createTaskFromExistingKey(repository, "1");
		assertNotNull(task);
		assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
		client.synchronize(task, true, null);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		
		BugzillaTask retrievedTask = (BugzillaTask) taskList.getTask(task.getHandleIdentifier());
		assertNotNull(retrievedTask);
		assertEquals(task.getHandleIdentifier(), retrievedTask.getHandleIdentifier());

		assertTrue(task.isDownloaded());
		assertEquals(1, task.getTaskData().getId());
	}

	public void testSynchronize() throws InterruptedException, PartInitException, LoginException, BugzillaException,
			PossibleBugzillaFailureException {
		init222();

		// Get the task
		BugzillaTask task = (BugzillaTask) client.createTaskFromExistingKey(repository, "1");
		MylarTaskListPlugin.getTaskListManager().getTaskList().moveToRoot(task);
		assertTrue(task.isDownloaded());
		// (The initial local copy from server)
		assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
		client.synchronize(task, true, null);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		// Modify it
		String newCommentText = "BugzillaRepositoryClientTest.testSynchronize(): " + (new Date()).toString();
		task.getTaskData().setNewComment(newCommentText);
		// overwrites old fields/attributes with new content (ususually done by
		// BugEditor)
		task.getTaskData().setHasChanged(true);
		task.setSyncState(RepositoryTaskSyncState.OUTGOING);
		client.saveOffline(task.getTaskData());
		assertEquals(RepositoryTaskSyncState.OUTGOING, task.getSyncState());

		// Submit changes
		MockBugzillaReportSubmitForm form = new MockBugzillaReportSubmitForm(BugzillaPlugin.ENCODING_UTF_8);
		client.submitBugReport(task.getTaskData(), form, null);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());

		// TODO: Test that comment was appended
		// ArrayList<Comment> comments = task.getTaskData().getComments();
		// assertNotNull(comments);
		// assertTrue(comments.size() > 0);
		// Comment lastComment = comments.get(comments.size() - 1);
		// assertEquals(newCommentText, lastComment.getText());

		client.synchronize(task, true, null);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());

		// OUTGOING with forceddSynch=false
		task.setSyncState(RepositoryTaskSyncState.OUTGOING);
		client.synchronize(task, false, null);
		assertEquals(RepositoryTaskSyncState.OUTGOING, task.getSyncState());

		// OUTGOING with forcedSynch=true --> Update Local Copy dialog
		// Choosing to override local changes results in SYNCHRONIZED
		// Choosing not to override results in CONFLICT

		task.setSyncState(RepositoryTaskSyncState.CONFLICT);
		client.synchronize(task, false, null);
		assertEquals(RepositoryTaskSyncState.CONFLICT, task.getSyncState());

		// CONFLICT with forcedSynch=true --> Update Local Copy dialog

		// Has no outgoing changes or conflicts yet needs synch
		// because task doesn't have bug report (new query hit)
		// Result: retrieved with no incoming status
		task.setSyncState(RepositoryTaskSyncState.SYNCHRONIZED);
		RepositoryTaskData bugReport = task.getTaskData();
		task.setTaskData(null);
		client.synchronize(task, false, null);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		assertNotNull(task.getTaskData());
		assertEquals(task.getTaskData().getId(), bugReport.getId());
	}

	public void testUniqueTaskObjects() {
		init222();
		String repositoryURL = "repositoryURL";
		BugzillaQueryHit hit1 = new BugzillaQueryHit("description", "P1", repositoryURL, 1, null, "status");
		ITask task1 = hit1.getOrCreateCorrespondingTask();
		assertNotNull(task1);
		// taskList.renameTask(task1, "testing");
		// task1.setDescription("testing");

		BugzillaQueryHit hit1Twin = new BugzillaQueryHit("description", "P1", repositoryURL, 1, null, "status");
		ITask task2 = hit1Twin.getOrCreateCorrespondingTask();
		assertEquals(task1.getDescription(), task2.getDescription());
	}

	class MockBugzillaReportSubmitForm extends BugzillaReportSubmitForm {

		public MockBugzillaReportSubmitForm(String encoding_utf_8) {
			super(encoding_utf_8);
		}

		@Override
		public String submitReportToRepository() throws BugzillaException, LoginException,
				PossibleBugzillaFailureException {
			return "test-submit";
		}

	}

	protected void updateBug(RepositoryTaskData bug) {

		// go through all of the attributes and update the main values to the
		// new ones
		// for (Iterator<AbstractRepositoryTaskAttribute> it =
		// bug.getAttributes().iterator(); it.hasNext();) {
		// AbstractRepositoryTaskAttribute attribute = it.next();
		// if (attribute.getValue() != null &&
		// attribute.getValue().compareTo(attribute.getValue()) != 0) {
		// bug.setHasChanged(true);
		// }
		// attribute.setValue(attribute.getNewValue());
		//
		// }
		// if (bug.getNewComment().compareTo(bug.getNewNewComment()) != 0) {
		// bug.setHasChanged(true);
		// }

		// Update some other fields as well.
		// bug.setNewComment(bug.getNewNewComment());

	}

	public void testAttachToExistingReport() throws Exception {
		init222();

		String taskNumber = "6";
		BugzillaTask task = (BugzillaTask) client.createTaskFromExistingKey(repository, taskNumber);
		assertNotNull(task);
		assertEquals(RepositoryTaskSyncState.INCOMING, task.getSyncState());
		assertTrue(task.isDownloaded());
		client.synchronize(task, true, null);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		
		assertEquals(6, task.getTaskData().getId());
		int numAttached = task.getTaskData().getAttachments().size();
		String fileName = "test-attach-" + System.currentTimeMillis() + ".txt";

		// A valid user name and password for the mylar bugzilla test server
		// must
		// be present. See 'setUp()'
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
		task = (BugzillaTask) client.createTaskFromExistingKey(repository, taskNumber);
		assertEquals(numAttached, task.getTaskData().getAttachments().size());

		/* Test attempt to upload an empty file */
		File attachFile = new File(fileName);
		attachment.setFilePath(attachFile.getAbsolutePath());
		BufferedWriter write = new BufferedWriter(new FileWriter(attachFile));
		assertFalse(attachmentHandler.uploadAttachment(attachment, repository.getUserName(), repository.getPassword(),
				Proxy.NO_PROXY));
		task = (BugzillaTask) client.createTaskFromExistingKey(repository, taskNumber);
		assertEquals(numAttached, task.getTaskData().getAttachments().size());

		/* Test uploading a proper file */
		write.write("This is a test text file");
		write.write("elif txet tset a si sihT");
		write.close();
		attachment.setFilePath(attachFile.getAbsolutePath());
		assertTrue(attachmentHandler.uploadAttachment(attachment, repository.getUserName(), repository.getPassword(),
				Proxy.NO_PROXY));
		task = (BugzillaTask) client.createTaskFromExistingKey(repository, taskNumber);
		assertEquals(numAttached + 1, task.getTaskData().getAttachments().size());

		// use assertion to track clean-up
		assertTrue(attachFile.delete());
	}

	public void testSynchChangedReports() throws Exception {

		init222();
		BugzillaTask task4 = generateLocalTaskAndDownload("4");
		assertEquals(4, task4.getTaskData().getId());

		BugzillaTask task5 = generateLocalTaskAndDownload("5");
 		assertEquals(5, task5.getTaskData().getId());
		
		Set<AbstractRepositoryTask> tasks = new HashSet<AbstractRepositoryTask>();
		tasks.add(task4);
		tasks.add(task5);

		synchAndAssertState(tasks, RepositoryTaskSyncState.SYNCHRONIZED);
		
		MylarTaskListPlugin.getRepositoryManager().setSyncTime(repository, null);
		client.synchronizeChanged(repository);
		// synchronizeChanged uses the date stamp from the most recent task returned so
		// this should always result in 1 task being returned (the most recently modified task). 
		Set<AbstractRepositoryTask> changedTasks = client.getChangedSinceLastSync(repository, tasks);
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
		
		for (AbstractRepositoryTask task : tasks) {
			bugzillaReportSubmitForm = makeExistingBugPost(task.getTaskData());
			bugzillaReportSubmitForm.submitReportToRepository();	
		}

		assertEquals("Changed reports expected ", 2, client.getChangedSinceLastSync(repository, tasks).size());
		
		synchAndAssertState(tasks, RepositoryTaskSyncState.INCOMING);
		synchAndAssertState(tasks, RepositoryTaskSyncState.SYNCHRONIZED);
		
		assertEquals(priority4, task4.getPriority());
		assertEquals(priority5, task5.getPriority());
	}
	
	public void testIncomingWhenOfflineDeleted() throws Exception {

		init222();
		BugzillaTask task7 = generateLocalTaskAndDownload("7");
		assertEquals(7, task7.getTaskData().getId());
		
		Set<AbstractRepositoryTask> tasks = new HashSet<AbstractRepositoryTask>();
		tasks.add(task7);
		synchAndAssertState(tasks, RepositoryTaskSyncState.SYNCHRONIZED);
		
		RepositoryTaskData recentTaskData = task7.getTaskData();
		assertNotNull(recentTaskData);
		
		assertFalse(MylarTaskListPlugin.getDefault().getOfflineReportsFile().find(IBugzillaConstants.TEST_BUGZILLA_222_URL, 7) == -1);
		ArrayList<RepositoryTaskData> taskDataList = new ArrayList<RepositoryTaskData>();		
		taskDataList.add(task7.getTaskData());
		MylarTaskListPlugin.getDefault().getOfflineReportsFile().remove(taskDataList);
		assertTrue(MylarTaskListPlugin.getDefault().getOfflineReportsFile().find(IBugzillaConstants.TEST_BUGZILLA_222_URL, 7) == -1);
		
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
		client.synchronizeChanged(repository);
		
		assertEquals(RepositoryTaskSyncState.INCOMING, task7.getSyncState());		
	}
	
	private BugzillaTask generateLocalTaskAndDownload(String taskNumber) {
		BugzillaTask task = (BugzillaTask) client.createTaskFromExistingKey(repository, taskNumber);
		assertNotNull(task);
		MylarTaskListPlugin.getTaskListManager().getTaskList().moveToRoot(task);
		assertTrue(task.isDownloaded());
		
		return task;
	}
	private BugzillaReportSubmitForm makeExistingBugPost(RepositoryTaskData taskData) throws UnsupportedEncodingException {
		return BugzillaReportSubmitForm.makeExistingBugPost(taskData, repository
				.getUrl(), repository.getUserName(), repository.getPassword(), null, null, repository
				.getCharacterEncoding());
	}
	private void synchAndAssertState(Set<AbstractRepositoryTask> tasks, RepositoryTaskSyncState state) {
		for (AbstractRepositoryTask task : tasks) {
			client.synchronize(task, true, null);
			assertEquals(task.getSyncState(), state);
		}
	}

	public void testTimeTracker222() throws Exception {
		init222();
		timeTracker(14, true);
	}
	public void testTimeTracker2201() throws Exception {
		init2201();
		timeTracker(22, true);
	}
	public void testTimeTracker220() throws Exception {
		init220();
		timeTracker(8, true);
	}
	public void testTimeTracker218() throws Exception {
		init218();
		timeTracker(18, false);
	}
	/**
	 * @param enableDeadline bugzilla 218 doesn't support deadlines
	 */
	protected void timeTracker(int taskid, boolean enableDeadline) throws Exception {
		BugzillaTask task14 = generateLocalTaskAndDownload("" + taskid);
		assertEquals(taskid, task14.getTaskData().getId());

		Set<AbstractRepositoryTask> tasks = new HashSet<AbstractRepositoryTask>();
		tasks.add(task14);
		
		synchAndAssertState(tasks, RepositoryTaskSyncState.SYNCHRONIZED);
		
		MylarTaskListPlugin.getRepositoryManager().setSyncTime(repository, null);
		client.synchronizeChanged(repository);
		// if a task or two has changed the last sync date is updated to 1s
		// after most recent change
		// therefore the following call should generally result in 0 changed
		// tasks returned.
		Set<AbstractRepositoryTask> changedTasks = client.getChangedSinceLastSync(repository, tasks);
		assertEquals(1, changedTasks.size());

		assertNotNull(repository.getUserName());
		assertNotNull(repository.getPassword());
		
		float estimatedTime, remainingTime, actualTime, addTime;
		String deadline = null;

		RepositoryTaskData taskdata14 = task14.getTaskData();
		estimatedTime = Float.parseFloat(taskdata14.getAttributeValue(BugzillaReportElement.ESTIMATED_TIME.getKeyString()));
		remainingTime = Float.parseFloat(taskdata14.getAttributeValue(BugzillaReportElement.REMAINING_TIME.getKeyString()));
		actualTime 	  =	Float.parseFloat(taskdata14.getAttributeValue(BugzillaReportElement.ACTUAL_TIME.getKeyString()));
		if (enableDeadline) 
			deadline = taskdata14.getAttributeValue(BugzillaReportElement.DEADLINE.getKeyString());
		
		estimatedTime += 2;
		remainingTime += 1.5;
		addTime = 0.75f;
		if (enableDeadline) 
			deadline = generateNewDay();
				
		taskdata14.setAttributeValue(BugzillaReportElement.ESTIMATED_TIME.getKeyString(), "" + estimatedTime);
		taskdata14.setAttributeValue(BugzillaReportElement.REMAINING_TIME.getKeyString(), "" + remainingTime);
		taskdata14.setAttributeValue(BugzillaReportElement.WORK_TIME.getKeyString(), 	  "" + addTime);
		if (enableDeadline)
			taskdata14.setAttributeValue(BugzillaReportElement.DEADLINE.getKeyString(), deadline);
		
		BugzillaReportSubmitForm bugzillaReportSubmitForm;
		
		for (AbstractRepositoryTask task : tasks) {
			task.getTaskData().setAttributeValue(BugzillaReportElement.ADD_COMMENT.getKeyString(), 
					"New Estimate: " + estimatedTime + "\nNew Remaining: " + remainingTime + "\nAdd: " + addTime );
			bugzillaReportSubmitForm = makeExistingBugPost(task.getTaskData());
			bugzillaReportSubmitForm.submitReportToRepository();	
		}

		assertEquals("Changed reports expected ", 1, client.getChangedSinceLastSync(repository, tasks).size());
		
		synchAndAssertState(tasks, RepositoryTaskSyncState.INCOMING);
		synchAndAssertState(tasks, RepositoryTaskSyncState.SYNCHRONIZED);
		
		taskdata14 = task14.getTaskData();
		
		
		assertEquals(estimatedTime, Float.parseFloat(taskdata14.getAttributeValue(BugzillaReportElement.ESTIMATED_TIME.getKeyString())));
		assertEquals(remainingTime, Float.parseFloat(taskdata14.getAttributeValue(BugzillaReportElement.REMAINING_TIME.getKeyString())));
		assertEquals(actualTime + addTime, Float.parseFloat(taskdata14.getAttributeValue(BugzillaReportElement.ACTUAL_TIME.getKeyString())));
		if (enableDeadline)
			assertEquals(deadline, taskdata14.getAttributeValue(BugzillaReportElement.DEADLINE.getKeyString()));
		
	}

	private String generateNewDay() {
		int year = 2006;
		int month = (int) (Math.random() * 12 + 1);
		int day = (int) (Math.random() * 28 + 1);
		return "" + year + "-" + ((month <= 9) ? "0" : "") + month + "-" + ((day <= 9) ? "0" : "") + day;
	}

}