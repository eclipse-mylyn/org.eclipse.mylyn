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

package org.eclipse.mylar.ide.tests;

import junit.framework.TestCase;

import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.tests.connector.MockRepositoryTask;
import org.eclipse.mylar.team.MylarTeamPlugin;

/**
 * @author Mik Kersten
 */
public class CommitTemplateTest extends TestCase {

	public void testRepositoryTaskCommentParsing() {
		String template = MylarTeamPlugin.getDefault().getPreferenceStore().getString(
				MylarTeamPlugin.COMMIT_TEMPLATE);
		
		AbstractRepositoryTask task = new MockRepositoryTask("handle-123");
		String comment = MylarTeamPlugin.getDefault().getCommitTemplateManager().generateComment(task, template);

		String taskId = MylarTeamPlugin.getDefault().getCommitTemplateManager().getTaskIdFromCommentOrLabel(comment);
		assertEquals("123", taskId);
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

//	public void testChangeSetLabelParsing() {
//		String label = "1: foo";
//		String id = MylarTeamPlugin.getDefault().getCommitTemplateManager().getTaskIdFromCommentOrLabel(label);
//		assertEquals("1", id);
//	}
	
}
