/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.bugzilla.tests.headless;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.mylyn.bugzilla.tests.AbstractBugzillaTest;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.core.history.AssignmentEvent;
import org.eclipse.mylyn.internal.bugzilla.core.history.ResolutionEvent;
import org.eclipse.mylyn.internal.bugzilla.core.history.StatusEvent;
import org.eclipse.mylyn.internal.bugzilla.core.history.TaskHistory;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public class BugzillaTaskHistoryTest extends AbstractBugzillaTest {

	private TaskRepository repository;

	private BugzillaRepositoryConnector connector;

	private static final String HISTORY_FILE_NAME = "storedHistory.history";

	private static final String REPORT_ID = "1";

	public void setUp() throws Exception {
		super.setUp();
		connector = new BugzillaRepositoryConnector();
		connector.init(new TaskList());
		repository = new TaskRepository(BugzillaCorePlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_222_URL);

		Credentials credentials = TestUtil.readCredentials();
		repository.setAuthenticationCredentials(credentials.username, credentials.password);
	}

	public void testGetBugHistory() throws Exception {

		BugzillaClient client = connector.getClientManager().getClient(repository);
		assertNotNull(client);
		TaskHistory history = client.getHistory("1");
		assertNotNull(history);

		assertEquals(1, history.getAssignmentEvents().size());
		assertEquals(2, history.getStatusEvents().size());
		assertEquals(1, history.getResolutionEvents().size());
		assertEquals(12, history.getOtherEvents().size());
	}

	public void testAssignmentEvent() throws Exception {
		BugzillaClient client = connector.getClientManager().getClient(repository);
		assertNotNull(client);
		TaskHistory history = client.getHistory("1");
		assertNotNull(history);

		AssignmentEvent assignment = history.getAssignmentEvents().get(0);
		assertEquals("nhapke@cs.ubc.ca", assignment.getName());
		assertEquals("user@mylar.eclipse.org", assignment.getAssigned());
		assertEquals("2006-08-25 17:48:02", assignment.getDate());
		assertEquals("nhapke@cs.ubc.ca", assignment.getRemoved());
		assertEquals("user@mylar.eclipse.org", assignment.getAdded());
		assertEquals("AssignedTo", assignment.getWhat());
	}

	public void testStatusEvent() throws Exception {

		BugzillaClient client = connector.getClientManager().getClient(repository);
		assertNotNull(client);
		TaskHistory history = client.getHistory("1");
		assertNotNull(history);

		StatusEvent statusChange = history.getStatusEvents().get(0);
		assertEquals("nhapke@cs.ubc.ca", statusChange.getName());
		assertEquals("2006-08-25 19:18:05", statusChange.getDate());
		assertEquals("NEW", statusChange.getRemoved());
		assertEquals("ASSIGNED", statusChange.getAdded());
		assertEquals("Status", statusChange.getWhat());
	}

	public void testResolutionEvent() throws Exception {
		BugzillaClient client = connector.getClientManager().getClient(repository);
		assertNotNull(client);
		TaskHistory history = client.getHistory(REPORT_ID);
		assertNotNull(history);

		ResolutionEvent resolutionChange = history.getResolutionEvents().get(0);
		assertEquals("janvik@cs.ubc.ca", resolutionChange.getName());
		assertEquals("2007-02-15 14:52:51", resolutionChange.getDate());
		assertEquals("", resolutionChange.getRemoved());
		assertEquals("FIXED", resolutionChange.getAdded());
		assertEquals("Resolution", resolutionChange.getWhat());
	}

	public void testStoredHistory() throws Exception {
		BugzillaClient client = connector.getClientManager().getClient(repository);
		assertNotNull(client);
		TaskHistory history = client.getHistory(REPORT_ID);
		assertNotNull(history);
		storeHistory(history);

		history = getStoredHistory();

		assertEquals(1, history.getAssignmentEvents().size());
		assertEquals(2, history.getStatusEvents().size());
		assertEquals(1, history.getResolutionEvents().size());
		assertEquals(12, history.getOtherEvents().size());

		// Remove file
		File storedHistoryFile = new File(HISTORY_FILE_NAME);
		assertTrue(storedHistoryFile.delete());
	}

	private void storeHistory(TaskHistory history) {
		File saveFile = new File(HISTORY_FILE_NAME);
		saveFile.deleteOnExit();
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(saveFile));
			out.writeObject(history);
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println("Can't write to: " + saveFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private TaskHistory getStoredHistory() {
		File file = new File(HISTORY_FILE_NAME);
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			TaskHistory history = (TaskHistory) in.readObject();
			in.close();
			return history;
		} catch (FileNotFoundException e) {
			System.err.println("Can't find: " + file);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// Should never happen
		return null;
	}
}
