/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tests.util.TestUtil;
import org.eclipse.mylyn.tests.util.TestUtil.Credentials;
import org.eclipse.mylyn.tests.util.TestUtil.PrivilegeLevel;

/**
 * @author Mik Kersten
 */
public class EncodingTest extends AbstractBugzillaTest {

	public void testEncodingSetting() {

		String charset = BugzillaClient.getCharsetFromString("text/html; charset=UTF-8");
		assertEquals("UTF-8", charset);

		charset = BugzillaClient.getCharsetFromString("text/html");
		assertEquals(null, charset);

		charset = BugzillaClient.getCharsetFromString("<<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-2\">>");
		assertEquals("iso-8859-2", charset);

		charset = BugzillaClient.getCharsetFromString("<<meta http-equiv=\"Content-Type\" content=\"text/html\">>");
		assertEquals(null, charset);
	}

	/**
	 * This test just shows that when the encoding is changed on the repository, synchronization does in fact return in
	 * a different encoding (though it may not be legible)
	 */
	public void testDifferentReportEncoding() throws Exception {
		TaskData data = BugzillaFixture.current().createTask(PrivilegeLevel.USER, "\u00E6", null);
		assertNotNull(data);
		assertTrue(data.getRoot().getMappedAttribute(TaskAttribute.SUMMARY).getValue().equals("\u00E6"));//"\u05D0"));

		WebLocation location = new WebLocation(repository.getRepositoryUrl());
		Credentials credentials = TestUtil.readCredentials(PrivilegeLevel.USER);
		location.setCredentials(AuthenticationType.REPOSITORY, credentials.username, credentials.password);
		BugzillaClient client2 = BugzillaFixture.current().client(location, "ISO-8859-1");
		data = BugzillaFixture.current().getTask(data.getTaskId(), client2);
		assertNotNull(data);
		// iso-8859-1 'incorrect' interpretation
		assertFalse(data.getRoot().getMappedAttribute(TaskAttribute.SUMMARY).getValue().equals("\u00E6"));//"\u05D0"));
	}

	public void testProperEncodingUponPost() throws Exception {
		TaskData data = BugzillaFixture.current().createTask(PrivilegeLevel.USER, "\u00E6", null);
		assertNotNull(data);
		ITask task = TasksUi.getRepositoryModel().createTask(repository, data.getTaskId());
		TasksUiPlugin.getTaskList().addTask(task);
		Set<ITask> tasks = new HashSet<ITask>();
		tasks.add(task);
		TasksUiInternal.synchronizeTasks(connector, tasks, true, null);
		String priority = null;
		TaskDataModel model = createModel(task);
		if (task.getPriority().equals("P1")) {
			priority = "P2";
			TaskAttribute attrPriority = model.getTaskData()
					.getRoot()
					.getAttribute(BugzillaAttribute.PRIORITY.getKey());
			if (attrPriority != null) {
				attrPriority.setValue(priority);
				model.attributeChanged(attrPriority);
			} else {
				fail();
			}
		} else {
			priority = "P1";
			TaskAttribute attrPriority = model.getTaskData()
					.getRoot()
					.getAttribute(BugzillaAttribute.PRIORITY.getKey());
			if (attrPriority != null) {
				attrPriority.setValue(priority);
				model.attributeChanged(attrPriority);
			} else {
				fail();
			}
		}
		model.save(new NullProgressMonitor());

		submit(model);
		data = BugzillaFixture.current().getTask(data.getTaskId(), client);
		assertNotNull(data);
		assertTrue(data.getRoot().getMappedAttribute(TaskAttribute.SUMMARY).getValue().equals("\u00E6"));//"\u05D0"));
	}
}
