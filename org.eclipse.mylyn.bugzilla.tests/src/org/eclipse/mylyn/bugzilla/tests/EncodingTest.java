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

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.text.ParseException;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaException;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportSubmitForm;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylar.internal.bugzilla.core.PossibleBugzillaFailureException;

/**
 * @author Mik Kersten
 */
public class EncodingTest extends AbstractBugzillaTest {

	public void testEncodingSetting() throws LoginException, IOException, ParseException {

		String charset = BugzillaClient.getCharsetFromString("text/html; charset=UTF-8");
		assertEquals("UTF-8", charset);

		charset = BugzillaClient.getCharsetFromString("text/html");
		assertEquals(null, charset);

		charset = BugzillaClient
				.getCharsetFromString("<<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-2\">>");
		assertEquals("iso-8859-2", charset);

		charset = BugzillaClient
				.getCharsetFromString("<<meta http-equiv=\"Content-Type\" content=\"text/html\">>");
		assertEquals(null, charset);
	}

	/**
	 * This test just shows that when the encoding is changed on the repository
	 * synchronization does in fact return in a different encoding (though it
	 * may not be legible)
	 */
	public void testDifferentReportEncoding() throws CoreException {
		init222();
		repository.setCharacterEncoding("UTF-8");
		BugzillaTask task = (BugzillaTask) connector.createTaskFromExistingKey(repository, "57", null);
		assertNotNull(task);
		//TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);
		assertTrue(task.getSummary().equals("\u05D0"));
		taskList.deleteTask(task);
		connector.getClientManager().repositoryRemoved(repository);
		repository.setCharacterEncoding("ISO-8859-1");
		task = (BugzillaTask) connector.createTaskFromExistingKey(repository, "57", null);
		assertNotNull(task);
		//TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);		
		// iso-8859-1 'incorrect' interpretation
		assertFalse(task.getSummary().equals("\u05D0"));
	}
	
	public void testProperEncodingUponPost() throws MalformedURLException, IOException, BugzillaException, PossibleBugzillaFailureException, GeneralSecurityException, CoreException {
		init222();
		repository.setCharacterEncoding("UTF-8");
		BugzillaTask task = (BugzillaTask) connector.createTaskFromExistingKey(repository, "57", null);
		assertNotNull(task);
		assertTrue(task.getSummary().equals("\u05D0"));
		String priority = null;
		if (task.getPriority().equals("P1")) {
			priority = "P2";
			task.getTaskData().setAttributeValue(BugzillaReportElement.PRIORITY.getKeyString(), priority);
		} else {
			priority = "P1";
			task.getTaskData().setAttributeValue(BugzillaReportElement.PRIORITY.getKeyString(), priority);
		}

		BugzillaReportSubmitForm bugzillaReportSubmitForm;
		bugzillaReportSubmitForm = makeExistingBugPost(task.getTaskData());
		bugzillaReportSubmitForm.submitReportToRepository(connector.getClientManager().getClient(repository));
		taskList.deleteTask(task);
		task = (BugzillaTask) connector.createTaskFromExistingKey(repository, "57", null);
		assertNotNull(task);
		assertTrue(task.getSummary().equals("\u05D0"));
	}

}
