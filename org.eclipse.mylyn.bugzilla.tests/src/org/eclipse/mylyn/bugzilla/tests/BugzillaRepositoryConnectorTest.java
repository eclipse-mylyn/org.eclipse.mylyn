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

import java.net.MalformedURLException;
import java.util.Date;

import javax.security.auth.login.LoginException;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.bugzilla.core.BugzillaException;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportSubmitForm;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.PossibleBugzillaFailureException;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaQueryHit;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaRepositoryConnector;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.provisional.bugzilla.core.BugzillaReport;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
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
 */
public class BugzillaRepositoryConnectorTest extends TestCase {

	private static final String DEFAULT_KIND = BugzillaPlugin.REPOSITORY_KIND;

	//private static final String TEST_REPOSITORY_URL = "https://bugs.eclipse.org/bugs";

	private BugzillaRepositoryConnector client;

	private TaskRepositoryManager manager;

	private TaskRepository repository;

	private TaskList taskList;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = MylarTaskListPlugin.getRepositoryManager();
		manager.clearRepositories();
		repository = new TaskRepository(DEFAULT_KIND, IBugzillaConstants.TEST_BUGZILLA_222_URL);
		// repository.setAuthenticationCredentials("userid", "password");
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
//		taskList.clearArchive();
		client.clearAllRefreshes();
		MylarTaskListPlugin.getTaskListManager().resetTaskList();
		manager.clearRepositories();

	}

	public void testCreateTaskFromExistingId() throws MalformedURLException, InterruptedException {
		BugzillaTask badId = (BugzillaTask) client.createTaskFromExistingKey(repository, "bad-id");
		assertNull(badId);

		BugzillaTask task = (BugzillaTask) client.createTaskFromExistingKey(repository, "1");
		assertNotNull(task);
		assertEquals(task.getSyncState(), RepositoryTaskSyncState.SYNCHRONIZED);

		BugzillaTask retrievedTask = (BugzillaTask) taskList.getTask(task.getHandleIdentifier());
		assertNotNull(retrievedTask);
		assertEquals(task.getHandleIdentifier(), retrievedTask.getHandleIdentifier());

		assertTrue(task.isDownloaded());
		assertEquals(1, task.getBugReport().getId());
	}

	public void testSynchronize() throws InterruptedException, PartInitException, LoginException, BugzillaException,
			PossibleBugzillaFailureException {

		// Get the task
		BugzillaTask task = (BugzillaTask) client.createTaskFromExistingKey(repository, "1");
		MylarTaskListPlugin.getTaskListManager().getTaskList().moveToRoot(task);
		assertTrue(task.isDownloaded());
		// (The initial local copy from server)
		client.saveBugReport(task.getBugReport());
		assertEquals(task.getSyncState(), RepositoryTaskSyncState.SYNCHRONIZED);

		// Modify it
		String newCommentText = "BugzillaRepositoryClientTest.testSynchronize(): " + (new Date()).toString();
		task.getBugReport().setNewComment(newCommentText);
		// overwrites old fields/attributes with new content (ususually done by
		// BugEditor)
		task.getBugReport().setHasChanged(true);
//		updateBug(task.getBugReport());
		assertEquals(task.getSyncState(), RepositoryTaskSyncState.SYNCHRONIZED);
		client.saveBugReport(task.getBugReport());
		assertEquals(RepositoryTaskSyncState.OUTGOING, task.getSyncState());

		// Submit changes
		MockBugzillaReportSubmitForm form = new MockBugzillaReportSubmitForm(BugzillaPlugin.ENCODING_UTF_8);
		client.submitBugReport(task.getBugReport(), form, null);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());

		// TODO: Test that comment was appended
		// ArrayList<Comment> comments = task.getBugReport().getComments();
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
		BugzillaReport bugReport = task.getBugReport();
		task.setBugReport(null);
		client.synchronize(task, false, null);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		assertNotNull(task.getBugReport());
		assertEquals(task.getBugReport().getId(), bugReport.getId());
	}

	public void testUniqueTaskObjects() {
		String repositoryURL = "repositoryURL";
		BugzillaQueryHit hit1 = new BugzillaQueryHit("description", "P1", repositoryURL, 1, null, "status");
		ITask task1 = hit1.getOrCreateCorrespondingTask();
		assertNotNull(task1); 
//		taskList.renameTask(task1, "testing");
//		task1.setDescription("testing"); 

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

	protected void updateBug(BugzillaReport bug) {

		// go through all of the attributes and update the main values to the
		// new ones
//		for (Iterator<AbstractRepositoryReportAttribute> it = bug.getAttributes().iterator(); it.hasNext();) {
//			AbstractRepositoryReportAttribute attribute = it.next();
//			if (attribute.getValue() != null && attribute.getValue().compareTo(attribute.getValue()) != 0) {
//				bug.setHasChanged(true);
//			}
//			attribute.setValue(attribute.getNewValue());
//
//		}
//		if (bug.getNewComment().compareTo(bug.getNewNewComment()) != 0) {
//			bug.setHasChanged(true);
//		}

		// Update some other fields as well.
		//bug.setNewComment(bug.getNewNewComment());

	}

}
