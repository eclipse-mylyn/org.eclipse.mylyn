/*******************************************************************************
 * Copyright (c) 2009, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.ui.ClipboardCopier;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction.Mode;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;
import org.eclipse.mylyn.tasks.ui.TasksUi;

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
		AbstractRepositoryConnector oldConnector = TasksUi.getRepositoryManager().getRepositoryConnector(
				MockRepositoryConnector.CONNECTOR_KIND);
		try {
			((TaskRepositoryManager) TasksUi.getRepositoryManager()).addRepositoryConnector(connector);
			assertEquals("321: s321" + ClipboardCopier.LINE_SEPARATOR + "http://321.com",
					CopyTaskDetailsAction.getTextForTask(task, Mode.SUMMARY_URL));
		} finally {
			if (oldConnector != null) {
				((TaskRepositoryManager) TasksUi.getRepositoryManager()).addRepositoryConnector(oldConnector);
			}
		}
	}

}
