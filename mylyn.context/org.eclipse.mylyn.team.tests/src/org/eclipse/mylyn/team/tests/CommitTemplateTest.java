/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.team.tests;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.team.ui.FocusedTeamUiPlugin;
import org.eclipse.mylyn.internal.team.ui.templates.CommitTemplateManager;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;

import junit.framework.TestCase;

/**
 * @author Mik Kersten
 */
public class CommitTemplateTest extends TestCase {

	public void testCreateTemplate() {
		String template = "${task.status} - ${connector.task.prefix} ${task.key}: ${task.description}";

		String taskId = "12345678";
		TaskTask testTask = new TaskTask("no url", taskId, "summary");
		testTask.setTaskKey(taskId);

		String commitComment = FocusedTeamUiPlugin.getDefault()
				.getCommitTemplateManager()
				.generateComment(testTask, template);
		assertTrue(commitComment.contains(taskId));

		testTask.setTaskKey(null);

		commitComment = FocusedTeamUiPlugin.getDefault().getCommitTemplateManager().generateComment(testTask, template);
		assertFalse(commitComment.contains(taskId));

	}

	public void testTemplateNullKeyTrailingCharacters() {
		String template = "${task.status} - ${connector.task.prefix} ${task.key}: ${task.description}";

		TaskTask testTask = createTask();

		String commitComment = FocusedTeamUiPlugin.getDefault()
				.getCommitTemplateManager()
				.generateComment(testTask, template);
		assertTrue(commitComment.contains(testTask.getSummary()));

		assertTrue(commitComment.contains(":"));
		assertEquals("Incomplete - : TestSummary", commitComment);
	}

	public void testTemplateCollapseWhitespace() {
		String template = "${task.status} - ${connector.task.prefix} ${task.key} ${task.key} : ${task.description}";

		TaskTask testTask = createTask();

		String commitComment = FocusedTeamUiPlugin.getDefault()
				.getCommitTemplateManager()
				.generateComment(testTask, template);
		assertTrue(commitComment.contains(testTask.getSummary()));

		assertFalse(commitComment.contains("  "));
		assertEquals("Incomplete - : TestSummary", commitComment);
	}

	public void testTemplateWithTab() {
		String template = "${task.status} - \t${connector.task.prefix} ${task.key} ${task.key} : ${task.description}";

		TaskTask testTask = createTask();

		String commitComment = FocusedTeamUiPlugin.getDefault()
				.getCommitTemplateManager()
				.generateComment(testTask, template);
		assertTrue(commitComment.contains(testTask.getSummary()));

		assertTrue(commitComment.contains("\t"));
		assertEquals("Incomplete - \t : TestSummary", commitComment);
	}

	public void testTemplateVariableWithParameters() {
		CommitTemplateManager manager = FocusedTeamUiPlugin.getDefault().getCommitTemplateManager();
		TaskTask testTask = createTask();

		assertEquals("ABC", manager.generateComment(testTask, "${TestVar(\"ABC\")}"));
		assertEquals("one ABC two", manager.generateComment(testTask, "one ${TestVar(\"ABC\")} two"));
		assertEquals("oneABCtwo", manager.generateComment(testTask, "one${TestVar(\"ABC\")}two"));
		assertEquals("oneABC-DEFtwo", manager.generateComment(testTask, "one${TestVar(\"ABC\",\"DEF\")}two"));
		assertEquals("oneABC-DEFtwo", manager.generateComment(testTask, "one${TestVar(\"ABC\", \"DEF\")}two"));
		assertEquals("oneABC-DEFtwo", manager.generateComment(testTask, "one${TestVar(\"ABC\" ,\"DEF\")}two"));
		assertEquals("oneABC-DEFtwo", manager.generateComment(testTask, "one${TestVar(\"ABC\" , \"DEF\")}two"));
		assertEquals("oneABC-DEFtwo", manager.generateComment(testTask, "one${TestVar( \"ABC\" , \"DEF\" )}two"));
		assertEquals("oneABC-DEF-GHItwo",
				manager.generateComment(testTask, "one${TestVar(\"ABC\", \"DEF\", \"GHI\")}two"));
		assertEquals("Incomplete x-y TestSummary",
				manager.generateComment(testTask, "${task.status} ${TestVar( \"x\" , \"y\" )} ${task.description}"));
		assertEquals("one ABC DEF two",
				manager.generateComment(testTask, "one ${TestVar(\"ABC\")} ${TestVar(\"DEF\")} two"));
		assertEquals("one two", manager.generateComment(testTask, "one ${TestVar(\"\")} two"));
		assertEquals("Incomplete TestSummary",
				manager.generateComment(testTask, "${task.status} ${TestVar()} ${task.description}"));
	}

	public void testTemplateVariableWithParametersIncorrectSyntax() {
		CommitTemplateManager manager = FocusedTeamUiPlugin.getDefault().getCommitTemplateManager();
		TaskTask testTask = createTask();

		assertEquals("Incomplete x , \"y TestSummary",
				manager.generateComment(testTask, "${task.status} ${TestVar( \"x , \"y\" )} ${task.description}"));
		assertEquals("Incomplete x\" \"y TestSummary",
				manager.generateComment(testTask, "${task.status} ${TestVar( \"x\" \"y\" )} ${task.description}"));
		assertEquals("Incomplete TestSummary",
				manager.generateComment(testTask, "${task.status} ${TestVar( \"x , )} ${task.description}"));
		assertEquals("Incomplete TestSummary",
				manager.generateComment(testTask, "${task.status} ${TestVar( \"x\" , )} ${task.description}"));
		assertEquals("Incomplete TestSummary",
				manager.generateComment(testTask, "${task.status} ${TestVar( x )} ${task.description}"));
		assertEquals("Incomplete TestSummary",
				manager.generateComment(testTask, "${task.status} ${noexist( \"x\" )} ${task.description}"));
	}

	private TaskTask createTask() {
		String taskId = "12345678";
		TaskTask testTask = new TaskTask("no url", taskId, "summary");
		testTask.setTaskKey(null);
		testTask.setSummary("TestSummary");
		return testTask;
	}

	public void testRepositoryTaskCommentParsing() {
		String template = FocusedTeamUiPlugin.getDefault()
				.getPreferenceStore()
				.getString(FocusedTeamUiPlugin.COMMIT_TEMPLATE);

		AbstractTask task = new MockTask("12345");
		String comment = FocusedTeamUiPlugin.getDefault().getCommitTemplateManager().generateComment(task, template);

		String taskId = FocusedTeamUiPlugin.getDefault()
				.getCommitTemplateManager()
				.getTaskIdFromCommentOrLabel(comment);
		assertEquals("12345", taskId);
	}

	public void testRepositoryTaskCommentParsingMultiline() {
		String template = FocusedTeamUiPlugin.getDefault()
				.getPreferenceStore()
				.getString(FocusedTeamUiPlugin.COMMIT_TEMPLATE);

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
