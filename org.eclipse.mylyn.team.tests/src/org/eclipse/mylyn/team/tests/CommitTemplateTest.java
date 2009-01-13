/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.team.tests;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.team.ui.FocusedTeamUiPlugin;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;

/**
 * @author Mik Kersten
 */
public class CommitTemplateTest extends TestCase {

	public void testCreateTemplate() {
		String template = "${task.status} - ${connector.task.prefix} ${task.key}: ${task.description}";

		String taskId = "12345678";
		TaskTask testTask = new TaskTask("no url", taskId, "summary");
		testTask.setTaskKey(taskId);

		String commitComment = FocusedTeamUiPlugin.getDefault().getCommitTemplateManager().generateComment(testTask,
				template);
		assertTrue(commitComment.contains(taskId));

		testTask.setTaskKey(null);

		commitComment = FocusedTeamUiPlugin.getDefault().getCommitTemplateManager().generateComment(testTask, template);
		assertFalse(commitComment.contains(taskId));

	}

	public void testRepositoryTaskCommentParsing() {
		String template = FocusedTeamUiPlugin.getDefault().getPreferenceStore().getString(
				FocusedTeamUiPlugin.COMMIT_TEMPLATE);

		AbstractTask task = new MockTask("12345");
		String comment = FocusedTeamUiPlugin.getDefault().getCommitTemplateManager().generateComment(task, template);

		String taskId = FocusedTeamUiPlugin.getDefault()
				.getCommitTemplateManager()
				.getTaskIdFromCommentOrLabel(comment);
		assertEquals("12345", taskId);
	}

	public void testRepositoryTaskCommentParsingMultiline() {
		String template = FocusedTeamUiPlugin.getDefault().getPreferenceStore().getString(
				FocusedTeamUiPlugin.COMMIT_TEMPLATE);

		AbstractTask task = new MockTask("12345");
		String comment = FocusedTeamUiPlugin.getDefault().getCommitTemplateManager().generateComment(task, template)
				+ "\n";

		String taskId = FocusedTeamUiPlugin.getDefault()
				.getCommitTemplateManager()
				.getTaskIdFromCommentOrLabel(comment);
		assertEquals("12345", taskId);
	}

	public void testRegex() {
		String comment = "task 123: label for handle-123";
		String regex = ".*\\ (\\d+):\\ .*";

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(comment);
		assertTrue(matcher.find());
	}
}
