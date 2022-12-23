/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataFileManager;
import org.eclipse.mylyn.tasks.core.ITask;

import junit.framework.TestCase;

/**
 * @author Alexei Trebounskikh
 */
public class TaskDataFileManagerTest extends TestCase {

	private class TestTaskDataFileManager extends TaskDataFileManager {
		public String getFileName(ITask task, boolean fileExists) {
			return super.getFileName(task, name -> fileExists);
		}

	}

	private final TestTaskDataFileManager fileManager = new TestTaskDataFileManager();

	public void testShortFileName() {
		// <max, exists, not requires encoding == encoded anyway for backwards compatibility
		assertEquals("11111%2520", fileManager.getFileName(TaskTestUtil.createMockTask("11111%20"), true));
		// <max, does not exist, not requires encoding == not encoded
		assertEquals("11111%20", fileManager.getFileName(TaskTestUtil.createMockTask("11111%20"), false));
		// <max, does not exist, requires encoding == encoded
		assertEquals("11111%2520%2B", fileManager.getFileName(TaskTestUtil.createMockTask("11111%20+"), false));
		// <max, exists, requires encoding == encoded
		assertEquals("11111%2520%2B", fileManager.getFileName(TaskTestUtil.createMockTask("11111%20+"), true));
	}

	public void testLongFileNameThatDoesNotRequireEncoding() {
		// >max, does not exist, not requires encoding == not encoded + trimmed
		String str = StringUtils.repeat("1", 256);
		assertEquals(StringUtils.repeat("1", 242) + ".71634944",
				fileManager.getFileName(TaskTestUtil.createMockTask(str), false));

		// >max, exists, not requires encoding == use as is
		assertEquals(str, fileManager.getFileName(TaskTestUtil.createMockTask(str), true));
	}

	public void testLongFileNameThatRequiresEncoding() {
		// >max, does not exist, requires encoding == encoded + trimmed
		String str = "+" + StringUtils.repeat("1", 255);
		String result = fileManager.getFileName(TaskTestUtil.createMockTask(str), false);
		assertEquals("%2B" + StringUtils.repeat("1", 237) + ".3664039548", result);

		// >max, exists, requires encoding == encoded + NOT trimmed
		result = fileManager.getFileName(TaskTestUtil.createMockTask(str), true);
		assertEquals(str.replaceAll("\\+", "%2B"), result);
	}

	public void testGetSetDataPath() {
		final String path = "path";
		fileManager.setDataPath(path);
		assertEquals(path, fileManager.getDataPath());
	}

	public void testGetFile() {
		final String path = "path";
		fileManager.setDataPath(path);

		final String taskId = "taskId";
		final File result = fileManager.getFile("url", TaskTestUtil.createMockTask(taskId), "kind");
		assertTrue(result.getPath().matches("^" + path + "\\S+" + taskId + "\\.\\S+$"));
	}
}
