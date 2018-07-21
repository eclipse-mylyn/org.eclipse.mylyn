/*******************************************************************************
 * Copyright (c) 2009, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.mylyn.commons.ui.ClipboardCopier;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction.Mode;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;
import org.eclipse.mylyn.tasks.ui.TasksUi;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 * @author Thomas Ehrnhoefer
 */
public class CopyDetailsActionTest extends TestCase {

//	public void testIdLabelIncluded() {
//		MockRepositoryConnector connector = MockRepositoryConnector.getDefault();
//		String oldPrefix = connector.getTaskIdPrefix();
//		try {
//			MockTask task = new MockTask("123");
//			task.setSummary("abc");
//
//			task.setTaskKey("123");
//			connector.setTaskIdPrefix("task");
//			String text = CopyTaskDetailsAction.getTextForTask(task);
//			//assertEquals("task 123: abc", text);
//			assertEquals("123: abc", text);
//
//			connector.setTaskIdPrefix("#");
//			assertEquals("#123: abc", CopyTaskDetailsAction.getTextForTask(task));
//
//			connector.setTaskIdPrefix("");
//			assertEquals("123: abc", CopyTaskDetailsAction.getTextForTask(task));
//
//			task.setTaskKey(null);
//			assertEquals("abc", CopyTaskDetailsAction.getTextForTask(task));
//		} finally {
//			connector.setTaskIdPrefix(oldPrefix);
//		}
//	}

	public void testGetTextForTask() {
		MockTask task = new MockTask("123");
		task.setSummary("abc");

		task.setTaskKey("123");
		String text = CopyTaskDetailsAction.getTextForTask(task);
		assertEquals("123: abc", text);

		task.setTaskKey(null);
		assertEquals("abc", CopyTaskDetailsAction.getTextForTask(task));
	}

	public void testGetSummaryAndUrl() {
		MockTask task = new MockTask("321");
		task.setSummary("s321");
		task.setTaskKey("321");
		MockRepositoryConnector connector = new MockRepositoryConnector() {
			@Override
			public String getTaskUrl(String repositoryUrl, String taskId) {
				return "http://321.com";
			}
		};
		AbstractRepositoryConnector oldConnector = TasksUi.getRepositoryManager()
				.getRepositoryConnector(MockRepositoryConnector.CONNECTOR_KIND);
		try {
			((TaskRepositoryManager) TasksUi.getRepositoryManager()).addRepositoryConnector(connector);
			assertEquals("321: s321" + ClipboardCopier.LINE_SEPARATOR + "http://321.com",
					CopyTaskDetailsAction.getTextForTask(task, Mode.ID_SUMMARY_URL));
		} finally {
			if (oldConnector != null) {
				((TaskRepositoryManager) TasksUi.getRepositoryManager()).addRepositoryConnector(oldConnector);
			}
		}
	}

	public void testGetBrowseableUrl() {
		MockTask task = new MockTask("123");
		task.setSummary("Ticket 123");
		MockRepositoryConnector connector = new MockRepositoryConnector() {

			@Override
			public String getTaskUrl(String repositoryUrl, String taskId) {
				return "URI://mock-repo/id/123";
			}

			@Override
			public URL getBrowserUrl(TaskRepository repository, IRepositoryElement element) {
				try {
					return new URL("http://mock-repo-evolved.com/tickets/123");
				} catch (MalformedURLException e) {
					return null;
				}
			}

		};

		AbstractRepositoryConnector oldConnector = TasksUi.getRepositoryManager()
				.getRepositoryConnector(MockRepositoryConnector.CONNECTOR_KIND);
		try {
			((TaskRepositoryManager) TasksUi.getRepositoryManager()).addRepositoryConnector(connector);
			assertEquals(
					"123: Ticket 123" + ClipboardCopier.LINE_SEPARATOR + "http://mock-repo-evolved.com/tickets/123",
					CopyTaskDetailsAction.getTextForTask(task, Mode.ID_SUMMARY_URL));
			assertEquals("http://mock-repo-evolved.com/tickets/123",
					CopyTaskDetailsAction.getTextForTask(task, Mode.URL));
		} finally {
			if (oldConnector != null) {
				((TaskRepositoryManager) TasksUi.getRepositoryManager()).addRepositoryConnector(oldConnector);
			}
		}
	}

}
