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

package org.eclipse.mylyn.ide.tests;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryTask;
import org.eclipse.mylyn.team.FocusedTeamPlugin;

/**
 * @author Mik Kersten
 */
public class CommitTemplateTest extends TestCase {

	public void testRepositoryTaskCommentParsing() {
		String template = FocusedTeamPlugin.getDefault().getPreferenceStore().getString(
				FocusedTeamPlugin.COMMIT_TEMPLATE);
		
		AbstractRepositoryTask task = new MockRepositoryTask("12345");
		String comment = FocusedTeamPlugin.getDefault().getCommitTemplateManager().generateComment(task, template);
		
		String taskId = FocusedTeamPlugin.getDefault().getCommitTemplateManager().getTaskIdFromCommentOrLabel(comment);
		assertEquals("12345", taskId);
	}

	public void testRepositoryTaskCommentParsingMultiline() {
		String template = FocusedTeamPlugin.getDefault().getPreferenceStore().getString(
				FocusedTeamPlugin.COMMIT_TEMPLATE);
		
		AbstractRepositoryTask task = new MockRepositoryTask("12345");
		String comment = FocusedTeamPlugin.getDefault().getCommitTemplateManager().generateComment(task, template) + "\n";
		
		String taskId = FocusedTeamPlugin.getDefault().getCommitTemplateManager().getTaskIdFromCommentOrLabel(comment);
		assertEquals("12345", taskId);
	}
	
	public void testRegex() {
		String comment = "task 123: label for handle-123";
		String regex = ".*\\ (\\d+):\\ .*";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(comment);
		assertTrue(matcher.find());
//		if (matcher.find()) {
//			return matcher.group(1);
//		}
	}
	
//	public void testLocalTaskCommentParsing() {	
//		ITask task = new Task("handle", "foo", false);
//		task.setUrl("http://eclipse.org/mylar");
//		String comment = ContextChangeSet.generateComment(task, MylarTeamPlugin.DEFAULT_PREFIX_COMPLETED,
//				MylarTeamPlugin.DEFAULT_PREFIX_PROGRESS);
//
//		String url = ContextChangeSet.getUrlFromComment(comment);
//		assertEquals("http://eclipse.org/mylar", url);
//
//		task.setUrl("http://eclipse.org/mylar bla \n bla");
//		String comment2 = ContextChangeSet.generateComment(task, MylarTeamPlugin.DEFAULT_PREFIX_COMPLETED,
//				MylarTeamPlugin.DEFAULT_PREFIX_PROGRESS);
//		String url2 = ContextChangeSet.getUrlFromComment(comment2);
//		assertEquals("http://eclipse.org/mylar", url2);
//	}
	
}
