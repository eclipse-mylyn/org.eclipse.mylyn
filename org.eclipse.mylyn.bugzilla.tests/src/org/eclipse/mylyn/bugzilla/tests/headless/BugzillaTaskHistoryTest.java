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
package org.eclipse.mylar.bugzilla.tests.headless;

import org.eclipse.mylar.bugzilla.tests.AbstractBugzillaTest;
import org.eclipse.mylar.context.tests.support.MylarTestUtils;
import org.eclipse.mylar.context.tests.support.MylarTestUtils.Credentials;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.history.AssignmentEvent;
import org.eclipse.mylar.internal.bugzilla.core.history.TaskHistory;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.core.TaskRepository;

public class BugzillaTaskHistoryTest extends AbstractBugzillaTest {

	private TaskRepository repository;

	private BugzillaRepositoryConnector connector;

	public void setUp() throws Exception {
		super.setUp();
		connector = new BugzillaRepositoryConnector();
		connector.init(new TaskList());
		repository = new TaskRepository(BugzillaCorePlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_222_URL);
		Credentials credentials = MylarTestUtils.readCredentials();
		repository.setAuthenticationCredentials(credentials.username, credentials.password);
	}

	public void testGetBugHistory() throws Exception {

		BugzillaClient client = connector.getClientManager().getClient(repository);
		assertNotNull(client);
		TaskHistory history = client.getHistory("1");
		assertNotNull(history);

		assertEquals(1, history.getAssignmentEvents().size());
		assertEquals(1, history.getStatusEvents().size());
		assertEquals(12, history.getOtherEvents().size());
		AssignmentEvent assignment = history.getAssignmentEvents().get(0);
		assertEquals("nhapke@cs.ubc.ca", assignment.getName());
		assertEquals("user@mylar.eclipse.org", assignment.getAssigned());
		assertEquals("2006-08-25 17:48:02", assignment.getDate());
		assertEquals("nhapke@cs.ubc.ca", assignment.getRemoved());
		assertEquals("user@mylar.eclipse.org", assignment.getAdded());
		assertEquals("AssignedTo", assignment.getWhat());
	}

}
