/*******************************************************************************
 * Copyright (c) 2004, 2011 Eugene Kuleshov and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataDiff;
import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskDiffUtil;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Eugene Kuleshov
 * @author Steffen Pingel
 * @author Robert Elves
 */
public class TaskDiffUtilTest extends TestCase {

	public void testFoldSpaces() {
		assertEquals("a b", TaskDiffUtil.foldSpaces("a   b"));
		assertEquals("", TaskDiffUtil.foldSpaces("  "));
		assertEquals("a b c d", TaskDiffUtil.cleanCommentText("a   b c   d"));
		assertEquals("b", TaskDiffUtil.cleanCommentText("   b   "));
		assertEquals("b", TaskDiffUtil.cleanCommentText("   b"));
	}

	public void testCleanComment() {
		assertEquals("attachment: some attachment. attachment description",
				TaskDiffUtil.cleanCommentText(("Created an attachment (id=111)\n" //
						+ "some attachment\n" //
						+ "\n" //
						+ "attachment description")));
		assertEquals("attachment: some attachment", TaskDiffUtil.cleanCommentText(("Created an attachment (id=111)\n" //
				+ "some attachment\n" //
				+ "\n")));
		assertEquals("some comment", TaskDiffUtil.cleanCommentText(("(In reply to comment #11)\n" //
				+ "some comment\n")));
		assertEquals("some comment. other comment", TaskDiffUtil.cleanCommentText((" (In reply to comment #11)\n" //
				+ "some comment\n" //
				+ "\n" //
				+ " (In reply to comment #12)\n" //
				+ "other comment\n")));
		assertEquals("some comment. other comment", TaskDiffUtil.cleanCommentText((" (In reply to comment #11)\n" //
				+ "some comment.  \n" //
				+ "\n" //
				+ " (In reply to comment #12)\n" //
				+ "> loren ipsum\n" + "> loren ipsum\n" + "other comment\n")));
	}

	public void testDateDiff() {
		TaskData dataA = new TaskData(new MyTaskAttributeMapper(new TaskRepository("mock", "url")), "mock", "url",
				"1123");
		TaskAttribute attributeA = new TaskAttribute(dataA.getRoot(), "attributeA");
		attributeA.getMetaData()
				.setKind(TaskAttribute.KIND_DEFAULT)
				.setType(TaskAttribute.TYPE_DATE)
				.setLabel("someDate:");
		attributeA.setValue("1275068800000");

		TaskData dataB = new TaskData(new MyTaskAttributeMapper(new TaskRepository("mock", "url")), "mock", "url",
				"1123");
		TaskAttribute attributeB = new TaskAttribute(dataB.getRoot(), "attributeA");
		attributeB.getMetaData()
				.setKind(TaskAttribute.KIND_DEFAULT)
				.setType(TaskAttribute.TYPE_DATE)
				.setLabel("mydate");
		attributeB.setValue("1265068800000");

		TaskDataDiff diff = new TaskDataDiff(new RepositoryModel(new TaskList(), new TaskRepositoryManager()), dataA,
				dataB);
		assertEquals(" someDate: 2010/02/01 -> 2010/05/28", TaskDiffUtil.toString(diff));
	}

	static class MyTaskAttributeMapper extends TaskAttributeMapper {

		public MyTaskAttributeMapper(TaskRepository taskRepository) {
			super(taskRepository);
		}

		@Override
		public String getValueLabel(TaskAttribute taskAttribute) {
			if (taskAttribute.getMetaData().getType() == TaskAttribute.TYPE_DATE) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(Long.parseLong(taskAttribute.getValue()));
				return new SimpleDateFormat("yyyy/MM/dd").format(cal.getTime());
			}
			return super.getValueLabel(taskAttribute);
		}

		@Override
		public List<String> getValueLabels(TaskAttribute taskAttribute) {
			if (taskAttribute.getMetaData().getType() == TaskAttribute.TYPE_DATE) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(Long.parseLong(taskAttribute.getValue()));
				return Collections.singletonList(new SimpleDateFormat("yyyy/MM/dd").format(cal.getTime()));
			}
			return super.getValueLabels(taskAttribute);
		}

	}

}
