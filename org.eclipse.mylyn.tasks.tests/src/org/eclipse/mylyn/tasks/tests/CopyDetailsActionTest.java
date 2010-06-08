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

import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;

/**
 * @author Steffen Pingel
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

}
