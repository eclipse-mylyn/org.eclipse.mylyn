/*******************************************************************************
 * Copyright (c) 2006, 2009 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.trac.tests.support.XmlRpcServer.Ticket;

/**
 * @author Steffen Pingel
 */
public class TracTestUtil {

	public static List<ITaskAttachment> getTaskAttachments(ITask task) throws CoreException {
		TaskData taskData = TasksUi.getTaskDataManager().getTaskData(task);
		List<ITaskAttachment> attachments = new ArrayList<ITaskAttachment>();
		List<TaskAttribute> attributes = taskData.getAttributeMapper().getAttributesByType(taskData,
				TaskAttribute.TYPE_ATTACHMENT);
		if (attributes != null) {
			for (TaskAttribute taskAttribute : attributes) {
				ITaskAttachment taskAttachment = TasksUiPlugin.getRepositoryModel().createTaskAttachment(taskAttribute);
				taskData.getAttributeMapper().updateTaskAttachment(taskAttachment, taskAttribute);
				attachments.add(taskAttachment);
			}
		}
		return attachments;
	}

	public static void assertTicketEquals(Ticket expectedTicket, TracTicket actualTicket) throws Exception {
		assertTrue(actualTicket.isValid());

		Map<?, ?> expectedValues = expectedTicket.getValues();
		Map<String, String> values = actualTicket.getValues();
		for (String key : values.keySet()) {
			Object expected = expectedValues.get(key);
			String actual = values.get(key);
			if (key.equals("reporter")) {
				// Trac 0.11 obfuscates email addresses by replacing the domain with the Ellipses character, 
				// mangle expected value accordingly
				if (actual != null && actual.endsWith("\u2026") && expected instanceof String) {
					String expectedString = (String) expected;
					int i = expectedString.indexOf("@");
					if (i != -1) {
						expected = expectedString.substring(0, i + 1) + "\u2026";
					}
				}
			} else if (key.startsWith("_")) {
				// ignore internal values
				continue;
			}
			assertEquals("Values for key '" + key + "' did not match", expected, actual);
		}
	}

	public static void assertTicketEquals(Version accessMode, TracTicket expectedTicket, TracTicket actualTicket)
			throws Exception {
		assertTrue(actualTicket.isValid());

		Map<?, ?> expectedValues = expectedTicket.getValues();
		Map<String, String> actualValues = actualTicket.getValues();
		for (String key : actualValues.keySet()) {
			Object expected = expectedValues.get(key);
			String actual = actualValues.get(key);
			if (key.equals("reporter")) {
				// Trac 0.11 obfuscates email addresses by replacing the domain with the Ellipses character, 
				// mangle expected value accordingly
				if (actual != null && actual.endsWith("\u2026") && expected instanceof String) {
					String expectedString = (String) expected;
					int i = expectedString.indexOf("@");
					if (i != -1) {
						expected = expectedString.substring(0, i + 1) + "\u2026";
					}
				}
			} else if (key.startsWith("_")) {
				// ignore internal values
				continue;
			}
			if (accessMode == Version.TRAC_0_9 && expected == null && "".equals(actual)) {
				// the web-client handles some values as the empty string that are represented as null in XML-RPC
				continue;
			}
			assertEquals("Values for key '" + key + "' did not match", expected, actual);
		}
	}

	public static void assertTicketEquals(TracTicket expectedTicket, TracTicket actualTicket) throws Exception {
		assertTicketEquals(null, expectedTicket, actualTicket);
	}

}
