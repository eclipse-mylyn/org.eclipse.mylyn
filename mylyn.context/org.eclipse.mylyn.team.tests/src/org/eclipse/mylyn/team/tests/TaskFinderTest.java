/*******************************************************************************
 * Copyright (c) 2004, 2016 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.team.tests;

import org.eclipse.mylyn.internal.team.ui.actions.TaskFinder;

import junit.framework.TestCase;

/**
 * @author Mik Kersten
 */
@SuppressWarnings("nls")
public class TaskFinderTest extends TestCase {

	public void test07LegacyMatching() {
		String label = "Progress on: 123: foo \nhttps://bugs.eclipse.org";
		String id = TaskFinder.getTaskIdFromLegacy07Label(label);
		assertEquals("123", id);
	}

	public void testUrlMatching() {
		String label = "bla bla\nhttp://foo.bar-123 bla bla";
		String id = TaskFinder.getUrlFromComment(label);
		assertEquals("http://foo.bar-123", id);
		label = "bla bla\nhttp://foo.bar-1234\n- bla bla";
		id = TaskFinder.getUrlFromComment(label);
		assertEquals("http://foo.bar-1234", id);
	}

	public void testExtractTaskId() throws Exception {
		assertNull(TaskFinder.extractTaskId("http://example.com/tasks/123", "", ""));
		assertNull(TaskFinder.extractTaskId("http://example.com/tasks/123", "prefix", "postfix"));
		assertNull(TaskFinder.extractTaskId("http://example.com/tasks/123", "http://example.com/tasks/", "postfix"));
		assertEquals("123", TaskFinder.extractTaskId("http://example.com/tasks/123", "http://example.com/tasks/", ""));
		assertEquals("123", TaskFinder.extractTaskId("http://example.com/tasks/123/viewtask.asp",
				"http://example.com/tasks/", "/viewtask.asp"));
		assertNull(TaskFinder.extractTaskId("http://example.com/tasks/123/viewtask.asp", "http://example.com/tasks/",
				"/viewtask.cgi"));
	}

	public void testGuessTaskKey() throws Exception {
		assertNull(TaskFinder.guessTaskKey("http://example.com/tasks/123", "", ""));
		assertNull(TaskFinder.guessTaskKey("http://example.com/tasks/123", "http://example.com/tasks/45", "123"));
		assertNull(TaskFinder.guessTaskKey("http://example.com/tasks/123", "http://example.com/tasks/45", "450"));
		assertNull(TaskFinder.guessTaskKey("http://example.com/tasks/123", "http://example.com/tasks/45", "045"));
		assertEquals("123",
				TaskFinder.guessTaskKey("http://example.com/tasks/123", "http://example.com/tasks/45", "45"));
		assertEquals("123", TaskFinder.guessTaskKey("http://example.com/tasks/123/viewtask.asp",
				"http://example.com/tasks/45/viewtask.asp", "45"));
		assertNull(TaskFinder.guessTaskKey("http://example.com/tasks/123/viewtask.asp", "http://example.com/tasks/45",
				"/viewtask.cgi"));
	}
}
