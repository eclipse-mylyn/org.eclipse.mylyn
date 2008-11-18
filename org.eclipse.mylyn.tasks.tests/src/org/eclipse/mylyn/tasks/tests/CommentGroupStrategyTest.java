/*******************************************************************************
 * Copyright (c) 2004, 2008 Jingwen Ou and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jingwen Ou - initial implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.RepositoryPerson;
import org.eclipse.mylyn.internal.tasks.core.TaskComment;
import org.eclipse.mylyn.internal.tasks.ui.editors.CommentGroupStrategy;
import org.eclipse.mylyn.internal.tasks.ui.editors.CommentGroupStrategy.CommentGroup;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Jingwen Ou
 */
public class CommentGroupStrategyTest extends TestCase {

	private static final String MOCK_CURRENT_PERSON_ID = "mockCurrentPersonId";

	private static final String MOCK_TASK_ATTRIBUTE = "mockTaskAttribute";

	private static final String MOCK_TEXT = "mockText";

	private final List<ITaskComment> comments;

	private final TaskRepository repository;

	private final CommentGroupStrategy strategy;

	private final ITask task;

	private final TaskData taskData;

	public CommentGroupStrategyTest() {
		repository = TaskTestUtil.createMockRepository();
		task = TaskTestUtil.createMockTask("1");
		taskData = TaskTestUtil.createMockTaskData("1");
		comments = new ArrayList<ITaskComment>();
		strategy = new CommentGroupStrategy();
	}

	private TaskComment mockComment(int number, String personId, Date date) {
		return mockComment(number, personId, date, MOCK_TEXT);
	}

	private TaskComment mockComment(int number, String personId, Date date, String text) {
		TaskAttribute taskAttribute = taskData.getRoot().createAttribute(MOCK_TASK_ATTRIBUTE + comments.size());
		TaskComment comment = new TaskComment(repository, task, taskAttribute);
		comment.setNumber(number);
		comment.setAuthor(new RepositoryPerson(repository, personId));
		comment.setCreationDate(date);
		comment.setText(text);
		return comment;
	}

	@Override
	protected void setUp() throws Exception {
		comments.clear();
	}

	public void testGroupCommentsAuthoredLatestComment() {
		int recentComments = CommentGroupStrategy.MAX_CURRENT;

		// didn't author previous comments
		for (int i = 0; i < recentComments; i++) {
			comments.add(mockComment(i + 1, "OtherPerson", new Date(i + 1)));
		}
		// authored the latest comment
		comments.add(mockComment(recentComments + 1, MOCK_CURRENT_PERSON_ID, new Date(recentComments + 1)));

		List<CommentGroup> group = strategy.groupComments(comments, MOCK_CURRENT_PERSON_ID);

		assertEquals(2, group.size());

		CommentGroup recentGroup = group.get(0);
		assertEquals(CommentGroup.RECENT, recentGroup.getGroupName());
		assertEquals(recentComments, recentGroup.getComments().size());

		CommentGroup currentGroup = group.get(1);
		assertEquals(CommentGroup.CURRENT, currentGroup.getGroupName());
		assertEquals(1, currentGroup.getComments().size());
	}

	public void testGroupCommentsMaxCurrent() {
		// one less than max current
		int oneLessThanMaxCurrent = CommentGroupStrategy.MAX_CURRENT - 1;

		for (int i = 0; i < oneLessThanMaxCurrent; i++) {
			comments.add(mockComment(i + 1, MOCK_CURRENT_PERSON_ID, new Date(i + 1)));
		}

		List<CommentGroup> group = strategy.groupComments(comments, MOCK_CURRENT_PERSON_ID);

		assertEquals(1, group.size());

		CommentGroup currentGroup = group.get(0);
		assertEquals(CommentGroup.CURRENT, currentGroup.getGroupName());
		assertEquals(oneLessThanMaxCurrent, currentGroup.getComments().size());
	}

	public void testGroupCommentsNotAuthoredLastestComment() {
		// didn't author previous comments
		for (int i = 0; i < CommentGroupStrategy.MAX_CURRENT; i++) {
			comments.add(mockComment(i + 1, "OtherPerson", new Date(i + 1)));
		}
		// not even the latest comment
		comments.add(mockComment(CommentGroupStrategy.MAX_CURRENT + 1, "OtherPerson", new Date(
				CommentGroupStrategy.MAX_CURRENT + 1)));

		List<CommentGroup> group = strategy.groupComments(comments, MOCK_CURRENT_PERSON_ID);

		assertEquals(2, group.size());

		CommentGroup recentGroup = group.get(0);
		assertEquals(CommentGroup.RECENT, recentGroup.getGroupName());
		assertEquals(1, recentGroup.getComments().size());

		CommentGroup currentGroup = group.get(1);
		assertEquals(CommentGroup.CURRENT, currentGroup.getGroupName());
		assertEquals(CommentGroupStrategy.MAX_CURRENT, currentGroup.getComments().size());
	}

	public void testGroupComments_RecentAndOlder() {
		int total = CommentGroupStrategy.MAX_CURRENT + CommentGroupStrategy.MAX_RECENT;
		// didn't author previous comments
		for (int i = 0; i < total; i++) {
			comments.add(mockComment(i + 1, "OtherPerson", new Date(i + 1)));
		}
		// not even the latest comment
		comments.add(mockComment(total + 1, "OtherPerson", new Date(total + 1)));

		List<CommentGroup> group = strategy.groupComments(comments, MOCK_CURRENT_PERSON_ID);

		assertEquals(3, group.size());

		CommentGroup olderGroup = group.get(0);
		assertEquals(CommentGroup.OLDER, olderGroup.getGroupName());
		assertEquals(1, olderGroup.getComments().size());

		CommentGroup recentGroup = group.get(1);
		assertEquals(CommentGroup.RECENT, recentGroup.getGroupName());
		assertEquals(CommentGroupStrategy.MAX_RECENT, recentGroup.getComments().size());

		CommentGroup currentGroup = group.get(2);
		assertEquals(CommentGroup.CURRENT, currentGroup.getGroupName());
		assertEquals(CommentGroupStrategy.MAX_CURRENT, currentGroup.getComments().size());
	}

	public void testGroupCommentsRecentAndOlder2() {
		int older = 10;
		int recent = CommentGroupStrategy.MAX_RECENT;
		int total = older + recent;
		// author previous comments
		for (int i = 0; i < total; i++) {
			comments.add(mockComment(i + 1, MOCK_CURRENT_PERSON_ID, new Date(i + 1)));
		}
		// even the latest comment
		comments.add(mockComment(total + 1, MOCK_CURRENT_PERSON_ID, new Date(total + 1)));

		List<CommentGroup> group = strategy.groupComments(comments, MOCK_CURRENT_PERSON_ID);

		assertEquals(3, group.size());

		CommentGroup olderGroup = group.get(0);
		assertEquals(CommentGroup.OLDER, olderGroup.getGroupName());
		assertEquals(older, olderGroup.getComments().size());

		CommentGroup recentGroup = group.get(1);
		assertEquals(CommentGroup.RECENT, recentGroup.getGroupName());
		assertEquals(recent, recentGroup.getComments().size());

		CommentGroup currentGroup = group.get(2);
		assertEquals(CommentGroup.CURRENT, currentGroup.getGroupName());
		assertEquals(1, currentGroup.getComments().size());
	}

	// 2: current person
	// 1: current person
	public void testIsCurrentAuthoredPreviousComment() {
		comments.add(mockComment(2, MOCK_CURRENT_PERSON_ID, new Date(2)));
		boolean isCurrent = strategy.isCurrent(comments, mockComment(1, MOCK_CURRENT_PERSON_ID, new Date(1)),
				MOCK_CURRENT_PERSON_ID);
		assertEquals(false, isCurrent);
	}

	// 2: current person - system generated, e.g. mylyn/context/zip
	// 1: current person 
	public void testIsCurrentAuthoredPreviousCommentButSystemGenerated() {
		comments.add(mockComment(2, MOCK_CURRENT_PERSON_ID, new Date(2), AttachmentUtil.CONTEXT_DESCRIPTION));
		boolean isCurrent = strategy.isCurrent(comments, mockComment(1, MOCK_CURRENT_PERSON_ID, new Date(1)),
				MOCK_CURRENT_PERSON_ID);
		assertEquals(true, isCurrent);
	}

	// test max current
	public void testIsCurrentMaxCurrent() {
		for (int i = 0; i < CommentGroupStrategy.MAX_CURRENT; i++) {
			comments.add(mockComment(i + 1, MOCK_CURRENT_PERSON_ID, new Date(i + 1)));
		}

		boolean isCurrent = strategy.isCurrent(comments, mockComment(CommentGroupStrategy.MAX_CURRENT + 1,
				MOCK_CURRENT_PERSON_ID, new Date(CommentGroupStrategy.MAX_CURRENT + 1)), MOCK_CURRENT_PERSON_ID);
		assertEquals(false, isCurrent);
	}

	// no comment
	public void testIsCurrentNoComment() {
		boolean isCurrent = strategy.isCurrent(comments, mockComment(1, MOCK_CURRENT_PERSON_ID, new Date(1)),
				MOCK_CURRENT_PERSON_ID);
		assertEquals(true, isCurrent);
	}

	// 2: another person
	// 1: current person
	public void testIsCurrentNotAuthoredPreviousComment() {
		comments.add(mockComment(2, "AnotherPerson", new Date(2), MOCK_TEXT));
		boolean isCurrent = strategy.isCurrent(comments, mockComment(1, MOCK_CURRENT_PERSON_ID, new Date(1)),
				MOCK_CURRENT_PERSON_ID);
		assertEquals(true, isCurrent);
	}

}
