/*******************************************************************************
 * Copyright (c) 2006, 2010 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *     Benjamin Muskalla (Tasktop Technologies) - support for deleting tasks     
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.TracTaskDataHandler;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.model.TracPriority;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylyn.internal.trac.core.model.TracVersion;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.trac.tests.support.TracFixture;
import org.eclipse.mylyn.trac.tests.support.TracHarness;
import org.eclipse.mylyn.trac.tests.support.TracTestUtil;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class TracRepositoryConnectorTest extends TestCase {

	private TaskRepository repository;

	private TracRepositoryConnector connector;

	private TracHarness harness;

	@Override
	protected void setUp() throws Exception {
		harness = TracFixture.current().createHarness();
		connector = harness.connector();
		repository = harness.repository();
	}

	@Override
	protected void tearDown() throws Exception {
		harness.dispose();
	}

	public void testGetRepositoryUrlFromTaskUrl() {
		assertEquals("http://host/repo", connector.getRepositoryUrlFromTaskUrl("http://host/repo/ticket/1"));
		assertEquals("http://host", connector.getRepositoryUrlFromTaskUrl("http://host/ticket/2342"));
		assertEquals(null, connector.getRepositoryUrlFromTaskUrl("http://host/repo/2342"));
		assertEquals(null, connector.getRepositoryUrlFromTaskUrl("http://host/repo/ticket-2342"));
	}

	public void testGetTaskData() throws Exception {
		TracTicket ticket = harness.createTicket("createTaskFromExistingKeyXml");
		String taskId = Integer.toString(ticket.getId());
		TaskData taskData = connector.getTaskData(repository, taskId, null);
		ITask task = TasksUi.getRepositoryModel().createTask(repository, taskData.getTaskId());
		assertNotNull(task);
		connector.updateTaskFromTaskData(repository, task, taskData);
		assertEquals(TaskTask.class, task.getClass());
		assertEquals("createTaskFromExistingKeyXml", task.getSummary());
		assertEquals(repository.getRepositoryUrl() + ITracClient.TICKET_URL + taskId, task.getUrl());
	}

	public void testPerformQuery() {
		TracSearch search = new TracSearch();
		search.addFilter("milestone", "milestone1");
		search.addFilter("milestone", "milestone2");
		search.setOrderBy("id");
		IRepositoryQuery query = TasksUi.getRepositoryModel().createRepositoryQuery(repository);
		query.setUrl(repository.getUrl() + ITracClient.QUERY_URL + search.toUrl());

		final List<TaskData> result = new ArrayList<TaskData>();
		TaskDataCollector hitCollector = new TaskDataCollector() {
			@Override
			public void accept(TaskData hit) {
				result.add(hit);
			}
		};
		IStatus queryStatus = connector.performQuery(repository, query, hitCollector, null, new NullProgressMonitor());
		assertEquals(Status.OK_STATUS, queryStatus);
//		assertEquals(3, result.size());
//		assertEquals(data.tickets.get(0).getId() + "", result.get(0).getTaskId());
//		assertEquals(data.tickets.get(1).getId() + "", result.get(1).getTaskId());
//		assertEquals(data.tickets.get(2).getId() + "", result.get(2).getTaskId());
	}

	public void testUpdateAttributes() throws Exception {
		connector.updateRepositoryConfiguration(repository, new NullProgressMonitor());

		ITracClient server = connector.getClientManager().getTracClient(repository);
		TracVersion[] versions = server.getVersions();
		assertEquals(2, versions.length);
		Arrays.sort(versions, new Comparator<TracVersion>() {
			public int compare(TracVersion o1, TracVersion o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		assertEquals("1.0", versions[0].getName());
		assertEquals("2.0", versions[1].getName());
	}

	public void testContext() throws Exception {
		ITask task = harness.createTask("context");
		File sourceContextFile = TasksUiPlugin.getContextStore().getFileForContext(task);
		sourceContextFile.createNewFile();
		sourceContextFile.deleteOnExit();

		boolean result;
		try {
			result = AttachmentUtil.postContext(connector, repository, task, "", null, null);
			if (repository.getVersion().equals(Version.TRAC_0_9.name())) {
				fail("expected CoreException"); // operation should not be supported
			}
		} catch (CoreException e) {
			if (repository.getVersion().equals(Version.TRAC_0_9.name())) {
				// done
				return;
			}
			throw e;
		}

		assertTrue(result);
		task = harness.getTask(task.getTaskId());
		List<ITaskAttachment> attachments = TracTestUtil.getTaskAttachments(task);
		// TODO attachment may have been overridden therefore size may not have changed
		//assertEquals(size + 1, task.getTaskData().getAttachments().size());
		ITaskAttachment attachment = attachments.get(attachments.size() - 1);
		result = AttachmentUtil.downloadContext(task, attachment, PlatformUI.getWorkbench().getProgressService());
		assertTrue(result);
		assertTrue(task.isActive());
	}

	public void testIsCompleted() {
		assertTrue(TracRepositoryConnector.isCompleted("closed"));
		assertFalse(TracRepositoryConnector.isCompleted("Closed"));
		assertFalse(TracRepositoryConnector.isCompleted("new"));
		assertFalse(TracRepositoryConnector.isCompleted("assigned"));
		assertFalse(TracRepositoryConnector.isCompleted("reopened"));
		assertFalse(TracRepositoryConnector.isCompleted("foobar"));
		assertFalse(TracRepositoryConnector.isCompleted(""));
		assertFalse(TracRepositoryConnector.isCompleted(null));
	}

	public void testGetTaskPriority() {
		assertEquals("P1", TracRepositoryConnector.getTaskPriority("blocker").toString());
		assertEquals("P2", TracRepositoryConnector.getTaskPriority("critical").toString());
		assertEquals("P3", TracRepositoryConnector.getTaskPriority("major").toString());
		assertEquals("P3", TracRepositoryConnector.getTaskPriority(null).toString());
		assertEquals("P3", TracRepositoryConnector.getTaskPriority("").toString());
		assertEquals("P3", TracRepositoryConnector.getTaskPriority("foo bar").toString());
		assertEquals("P4", TracRepositoryConnector.getTaskPriority("minor").toString());
		assertEquals("P5", TracRepositoryConnector.getTaskPriority("trivial").toString());
	}

	public void testGetTaskPriorityFromTracPriorities() {
		TracPriority p1 = new TracPriority("a", 1);
		TracPriority p2 = new TracPriority("b", 2);
		TracPriority p3 = new TracPriority("c", 3);
		TracPriority[] priorities = new TracPriority[] { p1, p2, p3 };
		assertEquals("P1", TracRepositoryConnector.getTaskPriority("a", priorities).toString());
		assertEquals("P3", TracRepositoryConnector.getTaskPriority("b", priorities).toString());
		assertEquals("P5", TracRepositoryConnector.getTaskPriority("c", priorities).toString());
		assertEquals("P3", TracRepositoryConnector.getTaskPriority("foo", priorities).toString());
		assertEquals("P3", TracRepositoryConnector.getTaskPriority(null, priorities).toString());

		p1 = new TracPriority("a", 10);
		priorities = new TracPriority[] { p1 };
		assertEquals("P1", TracRepositoryConnector.getTaskPriority("a", priorities).toString());
		assertEquals("P3", TracRepositoryConnector.getTaskPriority("b", priorities).toString());
		assertEquals("P3", TracRepositoryConnector.getTaskPriority(null, priorities).toString());

		p1 = new TracPriority("1", 10);
		p2 = new TracPriority("2", 20);
		p3 = new TracPriority("3", 30);
		TracPriority p4 = new TracPriority("4", 40);
		TracPriority p5 = new TracPriority("5", 70);
		TracPriority p6 = new TracPriority("6", 100);
		priorities = new TracPriority[] { p1, p2, p3, p4, p5, p6 };
		assertEquals("P1", TracRepositoryConnector.getTaskPriority("1", priorities).toString());
		assertEquals("P1", TracRepositoryConnector.getTaskPriority("2", priorities).toString());
		assertEquals("P2", TracRepositoryConnector.getTaskPriority("3", priorities).toString());
		assertEquals("P2", TracRepositoryConnector.getTaskPriority("4", priorities).toString());
		assertEquals("P4", TracRepositoryConnector.getTaskPriority("5", priorities).toString());
		assertEquals("P5", TracRepositoryConnector.getTaskPriority("6", priorities).toString());
	}

	public void testUpdateTaskFromTaskData() throws Exception {
		TracTicket ticket = new TracTicket(123);
		ticket.putBuiltinValue(Key.DESCRIPTION, "mydescription");
		ticket.putBuiltinValue(Key.PRIORITY, "mypriority");
		ticket.putBuiltinValue(Key.SUMMARY, "mysummary");
		ticket.putBuiltinValue(Key.TYPE, "mytype");

		TracTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		ITracClient client = connector.getClientManager().getTracClient(repository);
		client.updateAttributes(new NullProgressMonitor(), false);
		TaskData taskData = taskDataHandler.createTaskDataFromTicket(client, repository, ticket, null);
		ITask task = TasksUi.getRepositoryModel().createTask(repository, taskData.getTaskId());

		connector.updateTaskFromTaskData(repository, task, taskData);
		assertEquals(repository.getRepositoryUrl() + ITracClient.TICKET_URL + "123", task.getUrl());
		assertEquals("123", task.getTaskKey());
		assertEquals("mysummary", task.getSummary());
		assertEquals("P3", task.getPriority());
		assertEquals("mytype", task.getTaskKind());
	}

	public void testUpdateTaskFromTaskDataSummaryOnly() throws Exception {
		TracTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		ITracClient client = connector.getClientManager().getTracClient(repository);
		// ensure that client has the correct field configuration
		client.updateAttributes(new NullProgressMonitor(), true);
		assertEquals(client.getAccessMode().name(), repository.getVersion());

		// prepare task data
		TracTicket ticket = new TracTicket(456);
		ticket.putBuiltinValue(Key.SUMMARY, "mysummary");
		TaskData taskData = taskDataHandler.createTaskDataFromTicket(client, repository, ticket, null);
		TaskAttribute attribute = taskData.getRoot().getMappedAttribute(TaskAttribute.PRIORITY);
		if (attribute != null) {
			assertEquals("major", attribute.getValue());
		}

		ITask task = TasksUi.getRepositoryModel().createTask(repository, taskData.getTaskId());
		task.setPriority("P2");

		// create task from task data
		connector.updateTaskFromTaskData(repository, task, taskData);
		assertEquals(repository.getRepositoryUrl() + ITracClient.TICKET_URL + "456", task.getUrl());
		assertEquals("456", task.getTaskKey());
		assertEquals("mysummary", task.getSummary());
		// depending on the access mode createTaskDataFromTicket() creates different default attributes  
		if (client.getAccessMode() == Version.TRAC_0_9) {
			// the ticket type varies depending on Trac version
			//assertEquals(AbstractTask.DEFAULT_TASK_KIND, task.getTaskKind());
			assertEquals("P2", task.getPriority());
		} else {
			assertEquals("Defect", task.getTaskKind());
			assertEquals("P3", task.getPriority());
		}
	}

	public void testUpdateTaskFromTaskDataClosed() throws Exception {
		TracTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		ITracClient client = connector.getClientManager().getTracClient(repository);
		ITask task = TasksUi.getRepositoryModel().createTask(repository, "1");

		TracTicket ticket = new TracTicket(123);
		ticket.putBuiltinValue(Key.STATUS, "resolved");
		TaskData taskData = taskDataHandler.createTaskDataFromTicket(client, repository, ticket, null);
		connector.updateTaskFromTaskData(repository, task, taskData);
		assertEquals(null, task.getCompletionDate());

		ticket.putBuiltinValue(Key.STATUS, "closed");
		taskData = taskDataHandler.createTaskDataFromTicket(client, repository, ticket, null);
		connector.updateTaskFromTaskData(repository, task, taskData);
		assertEquals(new Date(0), task.getCompletionDate());

		ticket.putBuiltinValue(Key.STATUS, "closed");
		ticket.putBuiltinValue(Key.CHANGE_TIME, "123");
		taskData = taskDataHandler.createTaskDataFromTicket(client, repository, ticket, null);
		connector.updateTaskFromTaskData(repository, task, taskData);
		assertEquals(new Date(123 * 1000), task.getCompletionDate());
	}

	public void testDeleteNewTask() throws Exception {
		ITracClient client = connector.getClientManager().getTracClient(repository);
		if (client.getAccessMode() == Version.TRAC_0_9) {
			// not supported in web mode
			return;
		}
		ITask task = harness.createTask("deleteNewTask");
		assertTrue(connector.canDeleteTask(repository, task));
		connector.deleteTask(repository, task, null);
		try {
			connector.getTaskData(repository, task.getTaskId(), null);
			fail("Task should be gone");
		} catch (CoreException e) {
			assertTrue(e.getMessage().contains("does not exist"));
		}
	}

}
