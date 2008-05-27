/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;

public class BugzillaRepositoryConnectorTest2 extends AbstractBugzillaTest {

	public void testDataRetrieval() throws CoreException, ParseException {
		init(IBugzillaConstants.TEST_BUGZILLA_30_URL);
		TaskData data = connector.getTaskData(repository, "2", new NullProgressMonitor());
		assertNotNull(data);
		TaskMapper mapper = new TaskMapper(data);
		assertEquals("2", data.getTaskId());
		assertEquals("New bug submit", mapper.getSummary());
		assertEquals("Test new bug submission", mapper.getDescription());
		assertEquals(PriorityLevel.P2, mapper.getPriority());
		assertEquals("TestComponent", mapper.getComponent());
		assertEquals("nhapke@cs.ubc.ca", mapper.getOwner());
		assertEquals("TestProduct", mapper.getProduct());
		assertEquals("PC", mapper.getValue(BugzillaReportElement.REP_PLATFORM.getKey()));
		assertEquals("Windows", mapper.getValue(BugzillaReportElement.OP_SYS.getKey()));
		assertEquals("ASSIGNED", mapper.getValue(BugzillaReportElement.BUG_STATUS.getKey()));
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		assertEquals(format1.parse("2007-03-20 16:37"), mapper.getCreationDate());
		assertEquals(format2.parse("2007-12-06 20:50:00"), mapper.getModificationDate());

		//assertEquals("", mapper.getTaskUrl());
		//assertEquals("bugzilla", mapper.getTaskKind());
		//assertEquals("", mapper.getTaskKey());

		// test comments
		TaskAttribute comments = data.getMappedAttribute(TaskAttribute.CONTAINER_COMMENTS);
		assertEquals(12, comments.getAttributes().size());
		TaskCommentMapper commentMap = TaskCommentMapper.createFrom(comments.getAttribute("0"));
		assertEquals("Rob Elves", commentMap.getAuthor().getName());
		assertEquals("Test new bug submission", commentMap.getText());
		commentMap = TaskCommentMapper.createFrom(comments.getAttribute("1"));
		assertEquals("Rob Elves", commentMap.getAuthor().getName());
		assertEquals("Created an attachment (id=1)\ntest\n\ntest attachments", commentMap.getText());
		commentMap = TaskCommentMapper.createFrom(comments.getAttribute("11"));
		assertEquals("Tests", commentMap.getAuthor().getName());
		assertEquals("test", commentMap.getText());
	}
}
