/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.java.tests;

import junit.framework.TestCase;

import org.eclipse.mylar.java.TaskContextChangeSet;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.Task;

/**
 * @author Mik Kersten
 */
public class TaskContextChangeSetTest extends TestCase {
	
	@SuppressWarnings("serial")
	public void testBugzillaCommentParsing() {
		ITask task = new Task("handle", "111: ", false) {
			public boolean isLocal() {
				return false;
			}
		};
		String comment = TaskContextChangeSet.generateComment(
				task, 
				MylarTasklistPlugin.DEFAULT_PREFIX_COMPLETED, 
				MylarTasklistPlugin.DEFAULT_PREFIX_PROGRESS);
		String bugId = TaskContextChangeSet.getIssueIdFromComment(comment);
		assertEquals("111", bugId);
	}

	@SuppressWarnings("serial")
	public void testLocalTaskCommentParsing() {
		ITask task = new Task("handle", "foo", false);
		task.setIssueReportURL("http://eclipse.org/mylar");
		String comment = TaskContextChangeSet.generateComment(
				task, 
				MylarTasklistPlugin.DEFAULT_PREFIX_COMPLETED, 
				MylarTasklistPlugin.DEFAULT_PREFIX_PROGRESS);
		String bugId = TaskContextChangeSet.getIssueIdFromComment(comment);
		assertEquals(null, bugId);
		String url = TaskContextChangeSet.getUrlFromComment(comment);
		assertEquals("http://eclipse.org/mylar", url);
		
		task.setIssueReportURL("http://eclipse.org/mylar bla \n bla");
		String comment2 = TaskContextChangeSet.generateComment(
				task, 
				MylarTasklistPlugin.DEFAULT_PREFIX_COMPLETED, 
				MylarTasklistPlugin.DEFAULT_PREFIX_PROGRESS);
		String url2 = TaskContextChangeSet.getUrlFromComment(comment2);
		assertEquals("http://eclipse.org/mylar", url2);
	}
	
}
