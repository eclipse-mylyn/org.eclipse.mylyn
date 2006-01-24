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

package org.eclipse.mylar.tests.ide;

import junit.framework.TestCase;

import org.eclipse.mylar.ide.MylarIdePlugin;
import org.eclipse.mylar.ide.team.MylarContextChangeSet;
import org.eclipse.mylar.internal.tasklist.Task;
import org.eclipse.mylar.tasklist.ITask;

/**
 * @author Mik Kersten
 */
public class CommitMessageTest extends TestCase {
	
	@SuppressWarnings("serial")
	public void testBugzillaCommentParsing() {
		ITask task = new Task("handle", "111: ", false) {
			public boolean isLocal() {
				return false;
			}
		};
		String comment = MylarContextChangeSet.generateComment(
				task, 
				MylarIdePlugin.DEFAULT_PREFIX_COMPLETED, 
				MylarIdePlugin.DEFAULT_PREFIX_PROGRESS);
		String bugId = MylarContextChangeSet.getIssueIdFromComment(comment);
		assertEquals("111", bugId);
	}

	@SuppressWarnings("serial")
	public void testLocalTaskCommentParsing() {
		ITask task = new Task("handle", "foo", false);
		task.setUrl("http://eclipse.org/mylar");
		String comment = MylarContextChangeSet.generateComment(
				task, 
				MylarIdePlugin.DEFAULT_PREFIX_COMPLETED, 
				MylarIdePlugin.DEFAULT_PREFIX_PROGRESS);
		String bugId = MylarContextChangeSet.getIssueIdFromComment(comment);
		assertEquals(null, bugId);
		String url = MylarContextChangeSet.getUrlFromComment(comment);
		assertEquals("http://eclipse.org/mylar", url);
		
		task.setUrl("http://eclipse.org/mylar bla \n bla");
		String comment2 = MylarContextChangeSet.generateComment(
				task, 
				MylarIdePlugin.DEFAULT_PREFIX_COMPLETED, 
				MylarIdePlugin.DEFAULT_PREFIX_PROGRESS);
		String url2 = MylarContextChangeSet.getUrlFromComment(comment2);
		assertEquals("http://eclipse.org/mylar", url2);
	}
	
}
