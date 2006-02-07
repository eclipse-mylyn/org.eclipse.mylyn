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
import java.net.URL;
import java.util.Date;
import java.util.Iterator;

import javax.security.auth.login.LoginException;

import junit.framework.TestCase;

import org.eclipse.mylar.bugzilla.core.Attribute;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaException;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportSubmitForm;
import org.eclipse.mylar.internal.bugzilla.core.PossibleBugzillaFailureException;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaRepositoryClient;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryClient;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskRepository;
import org.eclipse.mylar.internal.tasklist.TaskRepositoryManager;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.ui.PartInitException;

public class BugzillaRepositoryClientTest extends TestCase {

	private static final String DEFAULT_KIND = BugzillaPlugin.REPOSITORY_KIND;

	private static final String TEST_REPOSITORY_URL = "https://bugs.eclipse.org/bugs";

	BugzillaRepositoryClient client;

	TaskRepositoryManager manager;

	TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = MylarTaskListPlugin.getRepositoryManager();
		manager.clearRepositories();
		repository = new TaskRepository(DEFAULT_KIND, new URL(TEST_REPOSITORY_URL));
		// repository.setAuthenticationCredentials("userid", "password");
		manager.addRepository(repository);
		assertNotNull(manager);

		AbstractRepositoryClient abstractRepositoryClient = manager.getRepositoryClient(DEFAULT_KIND);

		assertEquals(abstractRepositoryClient.getKind(), DEFAULT_KIND);

		client = (BugzillaRepositoryClient) abstractRepositoryClient;
		client.setForceSyncExec(true);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		client.clearArchive();
		client.clearAllRefreshes();
		manager.clearRepositories();

	}

	public void testCreateTaskFromExistingId() throws MalformedURLException, InterruptedException {
		BugzillaTask badId = (BugzillaTask) client.createTaskFromExistingId(repository, "bad-id");
		assertNull(badId);

		BugzillaTask task = (BugzillaTask) client.createTaskFromExistingId(repository, "1");
		assertNotNull(task);
		assertEquals(task.getSyncState(), RepositoryTaskSyncState.SYNCHRONIZED);

		BugzillaTask retrievedTask = (BugzillaTask) client.getTaskFromArchive(task.getHandleIdentifier());
		assertNotNull(retrievedTask);
		assertEquals(task.getHandleIdentifier(), retrievedTask.getHandleIdentifier());

		assertTrue(task.isBugDownloaded());
		assertEquals(1, task.getBugReport().getId());
	}

	public void testSynchronize() throws InterruptedException, PartInitException, LoginException, BugzillaException,
			PossibleBugzillaFailureException {

		// Get the task
		BugzillaTask task = (BugzillaTask) client.createTaskFromExistingId(repository, "1");
		MylarTaskListPlugin.getTaskListManager().moveToRoot(task);
		assertTrue(task.isBugDownloaded());
		// (The initial local copy from server)
		client.saveBugReport(task.getBugReport());
		assertEquals(task.getSyncState(), RepositoryTaskSyncState.SYNCHRONIZED);

		// Modify it
		String newCommentText = "BugzillaRepositoryClientTest.testSynchronize(): " + (new Date()).toString();
		task.getBugReport().setNewNewComment(newCommentText);
		// overwrites old fields/attributes with new content (ususually done by
		// BugEditor)
		updateBug(task.getBugReport());
		assertEquals(task.getSyncState(), RepositoryTaskSyncState.SYNCHRONIZED);
		client.saveBugReport(task.getBugReport());
		assertEquals(RepositoryTaskSyncState.OUTGOING, task.getSyncState());

		// Submit changes
		MockBugzillaReportSubmitForm form = new MockBugzillaReportSubmitForm();
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
		BugReport bugReport = task.getBugReport();
		task.setBugReport(null);
		client.synchronize(task, false, null);
		assertEquals(RepositoryTaskSyncState.SYNCHRONIZED, task.getSyncState());
		assertNotNull(task.getBugReport());
		assertEquals(task.getBugReport().getId(), bugReport.getId());
	}

	class MockBugzillaReportSubmitForm extends BugzillaReportSubmitForm {

		@Override
		public String submitReportToRepository() throws BugzillaException, LoginException,
				PossibleBugzillaFailureException {
			return "test-submit";
		}

	}

	//
	// /*
	// * Test method for
	// 'org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaRepositoryClient.synchronize()'
	// */
	// public void testSynchronize() {
	//
	// }
	//
	// /*
	// * Test method for
	// 'org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaRepositoryClient.synchronize(ITask,
	// boolean)'
	// */
	// public void testSynchronizeITaskBoolean() {
	//
	// }
	//
	// /*
	// * Test method for
	// 'org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaRepositoryClient.clearAllRefreshes()'
	// */
	// public void testClearAllRefreshes() {
	//
	// }
	//
	// /*
	// * Test method for
	// 'org.eclipse.mylar.internal.tasklist.AbstractRepositoryClient.getTaskFromArchive(String)'
	// */
	// public void testGetTaskFromArchive() {
	//
	// }
	//
	// /*
	// * Test method for
	// 'org.eclipse.mylar.internal.tasklist.AbstractRepositoryClient.getArchiveTasks()'
	// */
	// public void testGetArchiveTasks() {
	//
	// }

	// Utility Methods for testing purposes

	// private void populatePostHandler(BugzillaReportSubmitForm
	// bugReportPostHandler, BugReport bug) {
	//
	// // set the url for the bug to be submitted to
	// AbstractBugEditor.setURL(bugReportPostHandler, repository,
	// "process_bug.cgi");
	//
	// if (bug.getCharset() != null) {
	// bugReportPostHandler.setCharset(bug.getCharset());
	// }
	//
	// // Add the user's address to the CC list if they haven't specified a CC
	// Attribute newCCattr = bug.getAttributeForKnobName("newcc");
	// Attribute owner = bug.getAttribute("Assigned To");
	//
	// // Don't add the cc if the user is the bug owner
	// if (repository.getUserName() != null && !(owner != null &&
	// owner.getValue().indexOf(repository.getUserName()) > -1)) {
	// // Add the user to the cc list
	// if (newCCattr != null) {
	// if (newCCattr.getNewValue().equals("")) {
	// newCCattr.setNewValue(repository.getUserName());
	// }
	// }
	// }
	//
	// // go through all of the attributes and add them to the bug post
	// for (Iterator<Attribute> it = bug.getAttributes().iterator();
	// it.hasNext();) {
	// Attribute a = it.next();
	// if (a != null && a.getParameterName() != null &&
	// a.getParameterName().compareTo("") != 0 && !a.isHidden()) {
	// String value = a.getNewValue();
	// // add the attribute to the bug post
	// bugReportPostHandler.add(a.getParameterName(),
	// AbstractBugEditor.checkText(value));
	// } else if (a != null && a.getParameterName() != null &&
	// a.getParameterName().compareTo("") != 0
	// && a.isHidden()) {
	// // we have a hidden attribute and we should send it back.
	// bugReportPostHandler.add(a.getParameterName(), a.getValue());
	// }
	// }
	//
	// // make sure that the comment is broken up into 80 character lines
	// bug.setNewNewComment(AbstractBugEditor.formatText(bug.getNewNewComment()));
	//
	// // add the summary to the bug post
	// bugReportPostHandler.add(ATTR_SHORT_DESC,
	// bug.getAttribute(BugReport.ATTR_SUMMARY).getNewValue());
	//
	// // if (removeCC != null && removeCC.size() > 0) {
	// // String[] s = new String[removeCC.size()];
	// // bugReportPostHandler.add("cc",
	// // toCommaSeparatedList(removeCC.toArray(s)));
	// // bugReportPostHandler.add("removecc", "true");
	// // }
	//
	// // add the operation to the bug post
	// Operation o = bug.getSelectedOperation();
	// if (o == null)
	// bugReportPostHandler.add(ATTR_KNOB, VAL_NONE);
	// else {
	// bugReportPostHandler.add(ATTR_KNOB, o.getKnobName());
	// if (o.hasOptions()) {
	// String sel = o.getOptionValue(o.getOptionSelection());
	// bugReportPostHandler.add(o.getOptionName(), sel);
	// } else if (o.isInput()) {
	// String sel = o.getInputValue();
	// bugReportPostHandler.add(o.getInputName(), sel);
	// }
	// }
	// bugReportPostHandler.add(ATTR_FORM_NAME, VAL_PROCESS_BUG);
	//
	// // add the new comment to the bug post if there is some text in it
	// if (bug.getNewNewComment().length() != 0) {
	// bugReportPostHandler.add(ATTR_COMMENT, bug.getNewNewComment());
	// }
	//
	// }

	protected void updateBug(BugReport bug) {

		// go through all of the attributes and update the main values to the
		// new ones
		for (Iterator<Attribute> it = bug.getAttributes().iterator(); it.hasNext();) {
			Attribute attribute = it.next();
			if (attribute.getNewValue() != null && attribute.getNewValue().compareTo(attribute.getValue()) != 0) {
				bug.setHasChanged(true);
			}
			attribute.setValue(attribute.getNewValue());

		}
		if (bug.getNewComment().compareTo(bug.getNewNewComment()) != 0) {
			bug.setHasChanged(true);
		}

		// Update some other fields as well.
		bug.setNewComment(bug.getNewNewComment());

	}

}
