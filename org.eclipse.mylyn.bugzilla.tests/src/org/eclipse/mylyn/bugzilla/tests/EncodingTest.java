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
import java.text.ParseException;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaServerFacade;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 */
public class EncodingTest extends AbstractBugzillaTest {

	public void testEncodingSetting() throws LoginException, IOException, ParseException {

		String charset = BugzillaServerFacade.getCharsetFromString("text/html; charset=UTF-8");
		assertEquals("UTF-8", charset);

		charset = BugzillaServerFacade.getCharsetFromString("text/html");
		assertEquals(null, charset);

		charset = BugzillaServerFacade
				.getCharsetFromString("<<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-2\">>");
		assertEquals("iso-8859-2", charset);

		charset = BugzillaServerFacade
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
		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);
		assertTrue(task.getDescription().equals("\u05D0"));
		taskList.deleteTask(task);
		repository.setCharacterEncoding("ISO-8859-1");
		task = (BugzillaTask) connector.createTaskFromExistingKey(repository, "57", null);
		assertNotNull(task);
		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);		
		// iso-8859-1 'incorrect' interpretation
		assertFalse(task.getDescription().equals("\u05D0"));
	}

}
