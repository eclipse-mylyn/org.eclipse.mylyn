/*******************************************************************************
 * Copyright (c) 2004, 2009 Nathan Hapke and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nathan Hapke - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.history.AssignmentEvent;
import org.eclipse.mylyn.internal.bugzilla.core.history.ResolutionEvent;
import org.eclipse.mylyn.internal.bugzilla.core.history.StatusEvent;
import org.eclipse.mylyn.internal.bugzilla.core.history.TaskHistory;

/**
 * @author Nathan Hapke
 * @author Steffen Pingel
 */
public class BugzillaTaskHistoryTest extends TestCase {

	private static final String HISTORY_FILE_NAME = "storedHistory.history";

	private BugzillaClient client;

	@Override
	public void setUp() throws Exception {
		client = BugzillaFixture.current().client();
	}

	public void testGetBugHistory() throws Exception {
		TaskHistory history = client.getHistory("1", null);
		assertNotNull(history);

		assertEquals(1, history.getAssignmentEvents().size());
		assertEquals(2, history.getStatusEvents().size());
		assertEquals(1, history.getResolutionEvents().size());
		assertEquals(12, history.getOtherEvents().size());
	}

	public void testAssignmentEvent() throws Exception {
		TaskHistory history = client.getHistory("1", null);
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
		TaskHistory history = client.getHistory("1", null);
		assertNotNull(history);

		StatusEvent statusChange = history.getStatusEvents().get(0);
		assertEquals("nhapke@cs.ubc.ca", statusChange.getName());
		assertEquals("2006-08-25 19:18:05", statusChange.getDate());
		assertEquals("NEW", statusChange.getRemoved());
		assertEquals("ASSIGNED", statusChange.getAdded());
		assertEquals("Status", statusChange.getWhat());
	}

	public void testResolutionEvent() throws Exception {
		TaskHistory history = client.getHistory("1", null);
		assertNotNull(history);

		ResolutionEvent resolutionChange = history.getResolutionEvents().get(0);
		assertEquals("janvik@cs.ubc.ca", resolutionChange.getName());
		assertEquals("2007-02-15 14:52:51", resolutionChange.getDate());
		assertEquals("", resolutionChange.getRemoved());
		assertEquals("FIXED", resolutionChange.getAdded());
		assertEquals("Resolution", resolutionChange.getWhat());
	}

	public void testPersistHistory() throws Exception {
		TaskHistory history = client.getHistory("1", null);
		assertNotNull(history);
		try {
			storeHistory(history);

			history = getStoredHistory();

			assertEquals(1, history.getAssignmentEvents().size());
			assertEquals(2, history.getStatusEvents().size());
			assertEquals(1, history.getResolutionEvents().size());
			assertEquals(12, history.getOtherEvents().size());
		} finally {
			// clean up
			new File(HISTORY_FILE_NAME).delete();
		}
	}

	private void storeHistory(TaskHistory history) throws FileNotFoundException, IOException {
		File saveFile = new File(HISTORY_FILE_NAME);
		saveFile.deleteOnExit();
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(saveFile));
		out.writeObject(history);
		out.close();
	}

	private TaskHistory getStoredHistory() throws FileNotFoundException, IOException, ClassNotFoundException {
		File file = new File(HISTORY_FILE_NAME);
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		TaskHistory history = (TaskHistory) in.readObject();
		in.close();
		return history;
	}

}
