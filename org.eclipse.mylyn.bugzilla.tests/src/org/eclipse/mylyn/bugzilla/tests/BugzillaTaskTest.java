/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTaskDataHandler;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractLegacyRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.core.deprecated.TaskComment;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;

import com.eclipse.mylyn.bugzilla.deprecated.BugzillaAttributeFactory;

/**
 * @author Mik Kersten
 */
public class BugzillaTaskTest extends TestCase {

	private final BugzillaAttributeFactory attributeFactory = new BugzillaAttributeFactory();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		new BugzillaTaskDataHandler((BugzillaRepositoryConnector) TasksUiPlugin.getRepositoryManager()
				.getRepositoryConnector(BugzillaCorePlugin.CONNECTOR_KIND));
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCompletionDate() throws Exception {
		BugzillaTask task = new BugzillaTask("repo", "1", "summary");
		RepositoryTaskData taskData = new RepositoryTaskData(new BugzillaAttributeFactory(),
				BugzillaCorePlugin.CONNECTOR_KIND, IBugzillaConstants.ECLIPSE_BUGZILLA_URL, "1");

		//XXX rewrite test

		assertNull(task.getCompletionDate());

		Date now = new Date();
		String nowTimeStamp = new SimpleDateFormat(BugzillaAttributeFactory.comment_creation_ts_format).format(now);

		TaskComment taskComment = new TaskComment(new BugzillaAttributeFactory(), 1);
		RepositoryTaskAttribute attribute = attributeFactory.createAttribute(BugzillaAttribute.BUG_WHEN.getKey());
		attribute.setValue(nowTimeStamp);
		taskComment.addAttribute(BugzillaAttribute.BUG_WHEN.getKey(), attribute);
		taskData.addComment(taskComment);
		assertNull(task.getCompletionDate());

		RepositoryTaskAttribute resolvedAttribute = attributeFactory.createAttribute(BugzillaAttribute.BUG_STATUS.getKey());
		resolvedAttribute.setValue(IBugzillaConstants.VALUE_STATUS_RESOLVED);
		taskData.addAttribute(BugzillaAttribute.BUG_STATUS.getKey(), resolvedAttribute);
		AbstractLegacyRepositoryConnector connector = (AbstractLegacyRepositoryConnector) TasksUiPlugin.getRepositoryManager()
				.getRepositoryConnector(BugzillaCorePlugin.CONNECTOR_KIND);
		connector.updateTaskFromTaskData(new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND, "http://eclipse.org"),
				task, taskData);
		assertNotNull(task.getCompletionDate());
		assertEquals(taskData.getAttributeFactory().getDateForAttributeType(
				BugzillaAttribute.BUG_WHEN.getKey(), nowTimeStamp), task.getCompletionDate());

	}

}
