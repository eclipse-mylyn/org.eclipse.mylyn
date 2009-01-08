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

package org.eclipse.mylyn.bugzilla.tests;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Rob Elves
 */
public class BugzillaTaskCompletionTest extends TestCase {

	private TaskRepository repository;

	private BugzillaRepositoryConnector connector;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.connector = (BugzillaRepositoryConnector) TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				BugzillaCorePlugin.CONNECTOR_KIND);
		this.repository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND, IBugzillaConstants.ECLIPSE_BUGZILLA_URL);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCompletionDate() throws Exception {
		TaskTask task = new TaskTask(BugzillaCorePlugin.CONNECTOR_KIND, IBugzillaConstants.ECLIPSE_BUGZILLA_URL, "1");
		TaskAttributeMapper mapper = connector.getTaskDataHandler().getAttributeMapper(repository);
		TaskData taskData = new TaskData(mapper, BugzillaCorePlugin.CONNECTOR_KIND,
				IBugzillaConstants.ECLIPSE_BUGZILLA_URL, "1");
		taskData.getRoot().createAttribute(BugzillaAttribute.BUG_STATUS.getKey()).setValue(
				IBugzillaConstants.VALUE_STATUS_RESOLVED);
		TaskAttribute attrComment = taskData.getRoot().createAttribute("commentId");
		attrComment.getMetaData().setType(TaskAttribute.TYPE_COMMENT);
		TaskAttribute attrCreationDate = attrComment.createAttribute(BugzillaAttribute.BUG_WHEN.getKey());
		attrCreationDate.setValue("2009-12-11 12:00");

		assertFalse(task.isCompleted());
		connector.updateTaskFromTaskData(repository, task, taskData);
		assertTrue(task.isCompleted());
		Date completionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2009-12-11 12:00");
		assertTrue(completionDate.equals(task.getCompletionDate()));

	}

	public void testCompletionDateForStates() throws Exception {
		TaskTask task = new TaskTask(BugzillaCorePlugin.CONNECTOR_KIND, IBugzillaConstants.ECLIPSE_BUGZILLA_URL, "1");
		TaskAttributeMapper mapper = connector.getTaskDataHandler().getAttributeMapper(repository);
		TaskData taskData = new TaskData(mapper, BugzillaCorePlugin.CONNECTOR_KIND,
				IBugzillaConstants.ECLIPSE_BUGZILLA_URL, "1");
		TaskAttribute status = taskData.getRoot().createAttribute(BugzillaAttribute.BUG_STATUS.getKey());
		status.setValue("REOPENED");
		TaskAttribute attrComment = taskData.getRoot().createAttribute("commentId");
		attrComment.getMetaData().setType(TaskAttribute.TYPE_COMMENT);
		TaskAttribute attrCreationDate = attrComment.createAttribute(BugzillaAttribute.BUG_WHEN.getKey());
		attrCreationDate.setValue("2008-12-11 12:00");

		assertFalse(task.isCompleted());
		taskData.setPartial(true);
		Date completionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2009-01-06 06:06");
		task.setCompletionDate(completionDate);
		assertTrue(completionDate.equals(task.getCompletionDate()));
		connector.updateTaskFromTaskData(repository, task, taskData);
		assertTrue(!task.isCompleted());
		assertNull(task.getCompletionDate());

		status.setValue(IBugzillaConstants.VALUE_STATUS_NEW);
		task.setCompletionDate(completionDate);
		assertTrue(completionDate.equals(task.getCompletionDate()));
		connector.updateTaskFromTaskData(repository, task, taskData);
		assertTrue(!task.isCompleted());
		assertNull(task.getCompletionDate());

		status.setValue(IBugzillaConstants.VALUE_STATUS_VERIFIED);
		connector.updateTaskFromTaskData(repository, task, taskData);
		assertTrue(task.isCompleted());
		Date nullDate = new Date(0);
		assertNotNull(task.getCompletionDate());
		assertTrue(nullDate.equals(task.getCompletionDate()));
	}
}
