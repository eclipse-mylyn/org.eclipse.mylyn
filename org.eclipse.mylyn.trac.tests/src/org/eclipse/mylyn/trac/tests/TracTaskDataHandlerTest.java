/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.context.tests.support.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.tasks.core.IdentityAttributeFactory;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.InvalidTicketException;
import org.eclipse.mylyn.internal.trac.core.TracAttributeFactory;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.TracTask;
import org.eclipse.mylyn.internal.trac.core.TracTaskDataHandler;
import org.eclipse.mylyn.internal.trac.core.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylyn.tasks.core.AbstractAttachmentHandler;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.DefaultTaskSchema;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationContext;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.trac.tests.support.TestFixture;
import org.eclipse.mylyn.trac.tests.support.TracTestUtil;
import org.eclipse.mylyn.trac.tests.support.XmlRpcServer.TestData;

/**
 * @author Steffen Pingel
 */
public class TracTaskDataHandlerTest extends TestCase {

	private TracRepositoryConnector connector;

	private TaskRepository repository;

	private TaskRepositoryManager manager;

	private TestData data;

	private TracTaskDataHandler taskDataHandler;

	private ITracClient client;

	public TracTaskDataHandlerTest() {
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		data = TestFixture.init010();

		manager = TasksUiPlugin.getRepositoryManager();
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());

		connector = (TracRepositoryConnector) manager.getRepositoryConnector(TracCorePlugin.REPOSITORY_KIND);

		taskDataHandler = (TracTaskDataHandler) connector.getTaskDataHandler();
	}

	protected void init(String url, Version version) {
		Credentials credentials = TestUtil.readCredentials(PrivilegeLevel.USER);

		repository = new TaskRepository(TracCorePlugin.REPOSITORY_KIND, url);
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials(credentials.username,
				credentials.password), false);
		repository.setTimeZoneId(ITracClient.TIME_ZONE);
		repository.setCharacterEncoding(ITracClient.CHARSET);
		repository.setVersion(version.name());

		manager.addRepository(repository, TasksUiPlugin.getDefault().getRepositoriesFilePath());

		client = connector.getClientManager().getTracClient(repository);
	}

	public void testGetChangedSinceLastSyncWeb096() throws Exception {
		init(TracTestConstants.TEST_TRAC_096_URL, Version.TRAC_0_9);
		TracTask task = (TracTask) TasksUiUtil.createTask(repository, data.offlineHandlerTicketId + "", null);

		Set<AbstractTask> tasks = new HashSet<AbstractTask>();
		tasks.add(task);
		SynchronizationContext event = new SynchronizationContext();
		event.performQueries = true;
		event.taskRepository = repository;
		event.fullSynchronization = true;
		event.tasks = tasks;

		assertEquals(null, repository.getSynchronizationTimeStamp());
		connector.preSynchronization(event, null);
		assertTrue(event.performQueries);
		assertEquals(null, repository.getSynchronizationTimeStamp());
		assertFalse(task.isStale());

		int time = (int) (System.currentTimeMillis() / 1000) + 1;
		repository.setSynchronizationTimeStamp(time + "");
		connector.preSynchronization(event, null);
		assertTrue(event.performQueries);
		assertFalse(task.isStale());
	}

	public void testMarkStaleTasksXmlRpc010() throws Exception {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC);
		markStaleTasks();
	}

	public void testMarkStaleTasksXmlRpc011() throws Exception {
		init(TracTestConstants.TEST_TRAC_011_URL, Version.XML_RPC);
		markStaleTasks();
	}

	private void markStaleTasks() throws Exception {
		TracTicket ticket = TracTestUtil.createTicket(client, "markStaleTasks");
		TracTask task = (TracTask) TasksUiUtil.createTask(repository, ticket.getId() + "", null);
		TasksUiInternal.synchronizeTask(connector, task, true, null);
		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(),
				task.getTaskId());

		int lastModified = Integer.parseInt(taskData.getLastModified());

		Set<AbstractTask> tasks = new HashSet<AbstractTask>();
		tasks.add(task);
		SynchronizationContext event = new SynchronizationContext();
		event.performQueries = true;
		event.taskRepository = repository;
		event.fullSynchronization = true;

		// an empty set should not cause contact to the repository
		repository.setSynchronizationTimeStamp(null);
		event.tasks = new HashSet<AbstractTask>();
		connector.preSynchronization(event, null);
		assertTrue(event.performQueries);
		assertNull(repository.getSynchronizationTimeStamp());

		repository.setSynchronizationTimeStamp(null);
		event.tasks = tasks;
		connector.preSynchronization(event, null);
		assertTrue(event.performQueries);
		assertTrue(task.isStale());

		// always returns the ticket because time comparison mode is >=
		task.setStale(false);
		repository.setSynchronizationTimeStamp(lastModified + "");
		connector.preSynchronization(event, null);
		// TODO this was fixed so it returns false now but only if the 
		// query returns a single task
		assertFalse(event.performQueries);
		assertFalse(task.isStale());

		task.setStale(false);
		repository.setSynchronizationTimeStamp((lastModified + 1) + "");
		event.performQueries = true;
		connector.preSynchronization(event, null);
		assertFalse(event.performQueries);
		assertFalse(task.isStale());

		// change ticket making sure it gets a new change time
		Thread.sleep(1000);
		ticket.putBuiltinValue(Key.DESCRIPTION, lastModified + "");
		client.updateTicket(ticket, "comment", null);

		task.setStale(false);
		repository.setSynchronizationTimeStamp((lastModified + 1) + "");
		event.performQueries = true;
		connector.preSynchronization(event, null);
		assertTrue(event.performQueries);
		assertTrue(task.isStale());
	}

	public void testMarkStaleTasksNoTimeStampXmlRpc010() throws Exception {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC);
		markStaleTasksNoTimeStamp();
	}

	public void testMarkStaleTasksNoTimeStampXmlRpc011() throws Exception {
		init(TracTestConstants.TEST_TRAC_011_URL, Version.XML_RPC);
		markStaleTasksNoTimeStamp();
	}

	private void markStaleTasksNoTimeStamp() throws Exception {
		TracTask task = (TracTask) TasksUiUtil.createTask(repository, data.offlineHandlerTicketId + "", null);
		Set<AbstractTask> tasks = new HashSet<AbstractTask>();
		tasks.add(task);
		SynchronizationContext event = new SynchronizationContext();
		event.performQueries = true;
		event.taskRepository = repository;
		event.fullSynchronization = true;
		event.tasks = tasks;

		task.setStale(false);
		repository.setSynchronizationTimeStamp(null);
		connector.preSynchronization(event, null);
		assertTrue(event.performQueries);
		assertTrue(task.isStale());

		task.setStale(false);
		repository.setSynchronizationTimeStamp("");
		connector.preSynchronization(event, null);
		assertTrue(event.performQueries);
		assertTrue(task.isStale());

		task.setStale(false);
		repository.setSynchronizationTimeStamp("0");
		connector.preSynchronization(event, null);
		assertTrue(event.performQueries);
		assertTrue(task.isStale());

		task.setStale(false);
		repository.setSynchronizationTimeStamp("abc");
		connector.preSynchronization(event, null);
		assertTrue(event.performQueries);
		assertTrue(task.isStale());
	}

	public void testNonNumericTaskId() {
		try {
			connector.getTaskDataHandler().getTaskData(repository, "abc", new NullProgressMonitor());
			fail("Expected CoreException");
		} catch (CoreException e) {
		}
	}

	public void testAttachmentChangesLastModifiedDate010() throws Exception {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC);
		attachmentChangesLastModifiedDate();
	}

	public void testAttachmentChangesLastModifiedDate011() throws Exception {
		init(TracTestConstants.TEST_TRAC_011_URL, Version.XML_RPC);
		attachmentChangesLastModifiedDate();
	}

	private void attachmentChangesLastModifiedDate() throws Exception {
		RepositoryTaskData taskData = taskDataHandler.getTaskData(repository, data.attachmentTicketId + "",
				new NullProgressMonitor());
		TracTask task = new TracTask(repository.getRepositoryUrl(), data.attachmentTicketId + "", "");
		connector.updateTaskFromTaskData(repository, task, taskData);
		Date lastModified = taskDataHandler.getAttributeFactory(taskData).getDateForAttributeType(
				RepositoryTaskAttribute.DATE_MODIFIED, taskData.getLastModified());

		AbstractAttachmentHandler attachmentHandler = connector.getAttachmentHandler();
		ITaskAttachment attachment = new MockAttachment("abc".getBytes());
		attachmentHandler.uploadAttachment(repository, task, attachment, null, new NullProgressMonitor());

		taskData = taskDataHandler.getTaskData(repository, data.attachmentTicketId + "", new NullProgressMonitor());
		Date newLastModified = taskDataHandler.getAttributeFactory(taskData).getDateForAttributeType(
				RepositoryTaskAttribute.DATE_MODIFIED, taskData.getLastModified());
		assertTrue("Expected " + newLastModified + " to be more recent than " + lastModified,
				newLastModified.after(lastModified));
	}

	public void testPostTaskDataInvalidCredentials010() throws Exception {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC);
		postTaskDataInvalidCredentials();
	}

	public void testPostTaskDataInvalidCredentials011() throws Exception {
		init(TracTestConstants.TEST_TRAC_011_URL, Version.XML_RPC);
		postTaskDataInvalidCredentials();
	}

	private void postTaskDataInvalidCredentials() throws Exception {
		TracTask task = (TracTask) TasksUiUtil.createTask(repository, data.offlineHandlerTicketId + "", null);
		TasksUiInternal.synchronizeTask(connector, task, true, null);
		RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(task.getRepositoryUrl(),
				task.getTaskId());

		taskData.setNewComment("new comment");
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("foo", "bar"), false);
		try {
			taskDataHandler.postTaskData(repository, taskData, new NullProgressMonitor());
		} catch (CoreException expected) {
			assertEquals(RepositoryStatus.ERROR_REPOSITORY_LOGIN, expected.getStatus().getCode());
		}
		assertEquals("new comment", taskData.getNewComment());
	}

	public void testCanInitializeTaskData() throws Exception {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC);

		TracTask task = new TracTask("", "", "");
		assertFalse(taskDataHandler.canInitializeSubTaskData(task, null));
		task.setSupportsSubtasks(true);
		assertTrue(taskDataHandler.canInitializeSubTaskData(task, null));

		RepositoryTaskData taskData = taskDataHandler.getTaskData(repository, data.offlineHandlerTicketId + "",
				new NullProgressMonitor());
		assertFalse(taskDataHandler.canInitializeSubTaskData(null, taskData));
		taskData.setAttributeValue(TracTaskDataHandler.ATTRIBUTE_BLOCKED_BY, "");
		assertTrue(taskDataHandler.canInitializeSubTaskData(null, taskData));

		task.setSupportsSubtasks(false);
		connector.updateTaskFromTaskData(repository, task, taskData);
		assertTrue(taskDataHandler.canInitializeSubTaskData(task, null));
	}

	public void testInitializeSubTaskData() throws Exception {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC);

		RepositoryTaskData parentTaskData = taskDataHandler.getTaskData(repository, data.offlineHandlerTicketId + "",
				new NullProgressMonitor());
		try {
			taskDataHandler.initializeSubTaskData(repository, parentTaskData, parentTaskData, new NullProgressMonitor());
			fail("expected CoreException");
		} catch (CoreException expected) {
		}

		parentTaskData.setSummary("abc");
		parentTaskData.setDescription("def");
		String component = parentTaskData.getAttribute(TracAttributeFactory.Attribute.COMPONENT.getTracKey())
				.getOptions()
				.get(0);
		parentTaskData.setAttributeValue(TracAttributeFactory.Attribute.COMPONENT.getTracKey(), component);
		parentTaskData.setAttributeValue(TracTaskDataHandler.ATTRIBUTE_BLOCKED_BY, "");
		RepositoryTaskData subTaskData = new RepositoryTaskData(parentTaskData.getAttributeFactory(),
				TracCorePlugin.REPOSITORY_KIND, "", "");
		subTaskData.setAttributeValue(TracTaskDataHandler.ATTRIBUTE_BLOCKING, "");
		taskDataHandler.initializeSubTaskData(repository, subTaskData, parentTaskData, new NullProgressMonitor());
		assertEquals("", subTaskData.getSummary());
		assertEquals("", subTaskData.getDescription());
		assertEquals(component, subTaskData.getAttributeValue(TracAttributeFactory.Attribute.COMPONENT.getTracKey()));
		assertEquals(parentTaskData.getTaskId(), subTaskData.getAttributeValue(TracTaskDataHandler.ATTRIBUTE_BLOCKING));
		assertEquals("", parentTaskData.getAttributeValue(TracTaskDataHandler.ATTRIBUTE_BLOCKED_BY));
	}

	public void testGetSubTaskIds() throws Exception {
		RepositoryTaskData taskData = new RepositoryTaskData(new TracAttributeFactory(),
				TracCorePlugin.REPOSITORY_KIND, "", "");
		taskData.setAttributeValue(TracTaskDataHandler.ATTRIBUTE_BLOCKED_BY, "123 456");
		Set<String> subTaskIds = taskDataHandler.getSubTaskIds(taskData);
		assertEquals(2, subTaskIds.size());
		assertTrue(subTaskIds.contains("123"));
		assertTrue(subTaskIds.contains("456"));

		taskData.setAttributeValue(TracTaskDataHandler.ATTRIBUTE_BLOCKED_BY, "7,8");
		subTaskIds = taskDataHandler.getSubTaskIds(taskData);
		assertEquals(2, subTaskIds.size());
		assertTrue(subTaskIds.contains("7"));
		assertTrue(subTaskIds.contains("8"));

		taskData.setAttributeValue(TracTaskDataHandler.ATTRIBUTE_BLOCKED_BY, "  7 ,   8,  ");
		subTaskIds = taskDataHandler.getSubTaskIds(taskData);
		assertEquals(2, subTaskIds.size());
		assertTrue(subTaskIds.contains("7"));
		assertTrue(subTaskIds.contains("8"));

		taskData.setAttributeValue(TracTaskDataHandler.ATTRIBUTE_BLOCKED_BY, "7");
		subTaskIds = taskDataHandler.getSubTaskIds(taskData);
		assertEquals(1, subTaskIds.size());
		assertTrue(subTaskIds.contains("7"));

		taskData.setAttributeValue(TracTaskDataHandler.ATTRIBUTE_BLOCKED_BY, "");
		subTaskIds = taskDataHandler.getSubTaskIds(taskData);
		assertEquals(0, subTaskIds.size());

		taskData.setAttributeValue(TracTaskDataHandler.ATTRIBUTE_BLOCKED_BY, "  ");
		subTaskIds = taskDataHandler.getSubTaskIds(taskData);
		assertEquals(0, subTaskIds.size());
	}

	public void testUpdateTaskDetails() throws Exception {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.TRAC_0_9);

		TracTicket ticket = new TracTicket(123);
		ticket.putBuiltinValue(Key.DESCRIPTION, "mydescription");
		ticket.putBuiltinValue(Key.PRIORITY, "mypriority");
		ticket.putBuiltinValue(Key.SUMMARY, "mysummary");
		ticket.putBuiltinValue(Key.TYPE, "mytype");

		ITracClient client = connector.getClientManager().getTracClient(repository);
		RepositoryTaskData taskData = new RepositoryTaskData(IdentityAttributeFactory.getInstance(),
				TracCorePlugin.REPOSITORY_KIND, repository.getRepositoryUrl(), ticket.getId() + "");
		taskDataHandler.updateTaskDataFromTicket(taskData, ticket, client);

		TracTask task = new TracTask(TracTestConstants.TEST_TRAC_010_URL, "" + 123, "desc");
		assertEquals(TracTestConstants.TEST_TRAC_010_URL + ITracClient.TICKET_URL + "123", task.getUrl());
		assertEquals("desc", task.getSummary());

		DefaultTaskSchema schema = new DefaultTaskSchema(taskData);
		schema.applyTo(task);

		assertEquals(TracTestConstants.TEST_TRAC_010_URL + ITracClient.TICKET_URL + "123", task.getUrl());
		assertEquals("123", task.getTaskKey());
		assertEquals("mysummary", task.getSummary());
		assertEquals("P3", task.getPriority());
		assertEquals("mytype", task.getTaskKind());
	}

	public void testUpdateTaskDetailsSummaryOnly() throws InvalidTicketException {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.TRAC_0_9);

		TracTicket ticket = new TracTicket(456);
		ticket.putBuiltinValue(Key.SUMMARY, "mysummary");

		TracTask task = new TracTask(TracTestConstants.TEST_TRAC_010_URL, "" + 456, "desc");

		ITracClient client = connector.getClientManager().getTracClient(repository);
		RepositoryTaskData taskData = new RepositoryTaskData(IdentityAttributeFactory.getInstance(),
				TracCorePlugin.REPOSITORY_KIND, repository.getRepositoryUrl(), ticket.getId() + "");
		taskDataHandler.updateTaskDataFromTicket(taskData, ticket, client);
		DefaultTaskSchema schema = new DefaultTaskSchema(taskData);
		schema.applyTo(task);

		assertEquals(TracTestConstants.TEST_TRAC_010_URL + ITracClient.TICKET_URL + "456", task.getUrl());
		assertEquals("456", task.getTaskKey());
		assertEquals("mysummary", task.getSummary());
		assertEquals("P3", task.getPriority());
		assertEquals(AbstractTask.DEFAULT_TASK_KIND, task.getTaskKind());
	}

}
