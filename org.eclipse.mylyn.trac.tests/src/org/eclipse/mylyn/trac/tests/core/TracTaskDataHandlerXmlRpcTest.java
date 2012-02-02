/*******************************************************************************
 * Copyright (c) 2006, 2010 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.core.data.TextTaskAttachmentSource;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizationSession;
import org.eclipse.mylyn.internal.trac.core.TracAttribute;
import org.eclipse.mylyn.internal.trac.core.TracAttributeMapper;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.TracTaskDataHandler;
import org.eclipse.mylyn.internal.trac.core.TracTaskMapper;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylyn.internal.trac.core.util.TracUtil;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.data.TaskOperation;
import org.eclipse.mylyn.tasks.core.data.TaskRelation;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.trac.tests.support.TracFixture;
import org.eclipse.mylyn.trac.tests.support.TracTestUtil;
import org.eclipse.mylyn.trac.tests.support.XmlRpcServer.TestData;

/**
 * @author Steffen Pingel
 */
public class TracTaskDataHandlerXmlRpcTest extends TestCase {

	private TracRepositoryConnector connector;

	private TaskRepository repository;

	private TestData data;

	private TracTaskDataHandler taskDataHandler;

	private ITracClient client;

	public TracTaskDataHandlerXmlRpcTest() {
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		data = TracFixture.init010();
		connector = (TracRepositoryConnector) TasksUi.getRepositoryConnector(TracCorePlugin.CONNECTOR_KIND);
		taskDataHandler = connector.getTaskDataHandler();
		repository = TracFixture.current().singleRepository(connector);
		client = connector.getClientManager().getTracClient(repository);
	}

	private SynchronizationSession createSession(ITask... tasks) {
		SynchronizationSession session = new SynchronizationSession();
		session.setNeedsPerformQueries(true);
		session.setTaskRepository(repository);
		session.setFullSynchronization(true);
		session.setTasks(new HashSet<ITask>(Arrays.asList(tasks)));
		return session;
	}

	public void testMarkStaleTasks() throws Exception {
		SynchronizationSession session;
		// sleep for one second to ensure that the created ticket has a unique time stamp
		Thread.sleep(1000);
		TracTicket ticket = TracTestUtil.createTicket(client, "markStaleTasks");
		ITask task = TracTestUtil.createTask(repository, ticket.getId() + "");
		long lastModified = TracUtil.toTracTime(task.getModificationDate());

		// an empty set should not cause contact to the repository
		repository.setSynchronizationTimeStamp(null);
		session = createSession(task);
		connector.preSynchronization(session, null);
		assertTrue(session.needsPerformQueries());
		assertNull(repository.getSynchronizationTimeStamp());

		repository.setSynchronizationTimeStamp(null);
		session = createSession(task);
		connector.preSynchronization(session, null);
		assertTrue(session.needsPerformQueries());
		assertEquals(Collections.singleton(task), session.getStaleTasks());

		// always returns the ticket because time comparison mode is >=
		repository.setSynchronizationTimeStamp(lastModified + "");
		session = createSession(task);
		connector.preSynchronization(session, null);
		// false since query that check for changed tasks only returns a single task
		assertFalse(session.needsPerformQueries());
		assertEquals(Collections.emptySet(), session.getStaleTasks());

		// nothing has changed, should detect a change
		repository.setSynchronizationTimeStamp((lastModified + 1) + "");
		session = createSession(task);
		connector.preSynchronization(session, null);
		assertFalse(session.needsPerformQueries());
		assertEquals(Collections.emptySet(), session.getStaleTasks());

		long mostRecentlyModified = 0;
		// try changing ticket 3x to make sure it gets a new change time
		for (int i = 0; i < 3; i++) {
			ticket = client.getTicket(ticket.getId(), null);
			ticket.putBuiltinValue(Key.DESCRIPTION, lastModified + "");
			client.updateTicket(ticket, "comment", null);
			mostRecentlyModified = TracUtil.toTracTime(ticket.getLastChanged());
			// needs to be at least one second ahead of repository time stamp   
			if (mostRecentlyModified > lastModified + 1) {
				break;
			} else if (i == 2) {
				fail("Failed to update ticket modification time: ticket id=" + ticket.getId() + ", lastModified="
						+ lastModified + ", mostRectentlyModified=" + mostRecentlyModified);
			}
			Thread.sleep(1500);
		}

		// should now detect a change
		repository.setSynchronizationTimeStamp((lastModified + 1) + "");
		session = createSession(task);
		connector.preSynchronization(session, null);
		assertTrue("Expected change: ticket id=" + ticket.getId() + ", lastModified=" + lastModified
				+ ", mostRectentlyModified=" + mostRecentlyModified, session.needsPerformQueries());
		assertEquals(Collections.singleton(task), session.getStaleTasks());
	}

	public void testMarkStaleTasksNoTimeStamp() throws Exception {
		SynchronizationSession session;
		ITask task = TracTestUtil.createTask(repository, data.offlineHandlerTicketId + "");

		session = createSession(task);
		repository.setSynchronizationTimeStamp(null);
		connector.preSynchronization(session, null);
		assertTrue(session.needsPerformQueries());
		assertEquals(Collections.singleton(task), session.getStaleTasks());

		session = createSession(task);
		repository.setSynchronizationTimeStamp("");
		connector.preSynchronization(session, null);
		assertTrue(session.needsPerformQueries());
		assertEquals(Collections.singleton(task), session.getStaleTasks());

		session = createSession(task);
		repository.setSynchronizationTimeStamp("0");
		connector.preSynchronization(session, null);
		assertTrue(session.needsPerformQueries());
		assertEquals(Collections.singleton(task), session.getStaleTasks());

		session = createSession(task);
		repository.setSynchronizationTimeStamp("abc");
		connector.preSynchronization(session, null);
		assertTrue(session.needsPerformQueries());
		assertEquals(Collections.singleton(task), session.getStaleTasks());
	}

	public void testNonNumericTaskId() {
		try {
			connector.getTaskData(repository, "abc", null);
			fail("Expected CoreException");
		} catch (CoreException e) {
		}
	}

	public void testAttachmentChangesLastModifiedDate() throws Exception {
		AbstractTaskAttachmentHandler attachmentHandler = connector.getTaskAttachmentHandler();
		ITask task = TracTestUtil.createTask(repository, data.attachmentTicketId + "");
		Date lastModified = task.getModificationDate();
		// XXX the test case fails when comment == null
		attachmentHandler.postContent(repository, task, new TextTaskAttachmentSource("abc"), "comment", null, null);

		task = TracTestUtil.createTask(repository, data.attachmentTicketId + "");
		Date newLastModified = task.getModificationDate();
		assertTrue("Expected " + newLastModified + " to be more recent than " + lastModified,
				newLastModified.after(lastModified));
	}

	public void testAttachmentUrlEncoding() throws Exception {
		AbstractTaskAttachmentHandler attachmentHandler = connector.getTaskAttachmentHandler();
		TracTicket ticket = TracTestUtil.createTicket(client, "attachment url test");
		ITask task = TracTestUtil.createTask(repository, ticket.getId() + "");
		attachmentHandler.postContent(repository, task, new TextTaskAttachmentSource("abc") {
			@Override
			public String getName() {
				return "https%3A%2F%2Fbugs.eclipse.org%2Fbugs.xml.zip";
			}
		}, "comment", null, null);

		task = TracTestUtil.createTask(repository, ticket.getId() + "");
		List<ITaskAttachment> attachments = TracTestUtil.getTaskAttachments(task);
		assertEquals(1, attachments.size());
		assertEquals(repository.getUrl() + "/attachment/ticket/" + ticket.getId()
				+ "/https%253A%252F%252Fbugs.eclipse.org%252Fbugs.xml.zip", attachments.get(0).getUrl());
	}

	public void testPostTaskDataInvalidCredentials() throws Exception {
		ITask task = TracTestUtil.createTask(repository, data.offlineHandlerTicketId + "");
		TaskData taskData = TasksUi.getTaskDataManager().getTaskData(task);
		taskData.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW).setValue("new comment");
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials("foo", "bar"), false);
		try {
			taskDataHandler.postTaskData(repository, taskData, null, null);
		} catch (CoreException expected) {
			assertEquals(RepositoryStatus.ERROR_REPOSITORY_LOGIN, expected.getStatus().getCode());
		}
		assertEquals("new comment", taskData.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW).getValue());
	}

	public void testCanInitializeTaskData() throws Exception {
		ITask task = new TaskTask(TracCorePlugin.CONNECTOR_KIND, "", "");
		assertFalse(taskDataHandler.canInitializeSubTaskData(repository, task));
		task.setAttribute(TracRepositoryConnector.TASK_KEY_SUPPORTS_SUBTASKS, Boolean.TRUE.toString());
		assertTrue(taskDataHandler.canInitializeSubTaskData(repository, task));

		task = TracTestUtil.createTask(repository, data.offlineHandlerTicketId + "");
		TaskData taskData = taskDataHandler.getTaskData(repository, data.offlineHandlerTicketId + "", null);
		assertFalse(taskDataHandler.canInitializeSubTaskData(repository, task));

		taskData.getRoot().createAttribute(TracTaskDataHandler.ATTRIBUTE_BLOCKED_BY);
		connector.updateTaskFromTaskData(repository, task, taskData);
		assertTrue(taskDataHandler.canInitializeSubTaskData(repository, task));

		task.setAttribute(TracRepositoryConnector.TASK_KEY_SUPPORTS_SUBTASKS, Boolean.FALSE.toString());
		connector.updateTaskFromTaskData(repository, task, taskData);
		assertTrue(taskDataHandler.canInitializeSubTaskData(repository, task));
	}

	public void testInitializeSubTaskDataInvalidParent() throws Exception {
		TaskData parentTaskData = taskDataHandler.getTaskData(repository, data.offlineHandlerTicketId + "",
				new NullProgressMonitor());
		try {
			taskDataHandler.initializeSubTaskData(repository, parentTaskData, parentTaskData, null);
			fail("expected CoreException");
		} catch (CoreException expected) {
		}
	}

	public void testInitializeSubTaskData() throws Exception {
		TaskData parentTaskData = taskDataHandler.getTaskData(repository, data.offlineHandlerTicketId + "", null);
		TaskMapper parentTaskMapper = new TracTaskMapper(parentTaskData, null);
		parentTaskMapper.setSummary("abc");
		parentTaskMapper.setDescription("def");
		String component = parentTaskData.getRoot()
				.getMappedAttribute(TracAttribute.COMPONENT.getTracKey())
				.getOptions()
				.get(0);
		parentTaskMapper.setComponent(component);
		parentTaskData.getRoot().createAttribute(TracTaskDataHandler.ATTRIBUTE_BLOCKED_BY);
		TaskData subTaskData = new TaskData(parentTaskData.getAttributeMapper(), TracCorePlugin.CONNECTOR_KIND, "", "");
		subTaskData.getRoot().createAttribute(TracTaskDataHandler.ATTRIBUTE_BLOCKING);
		taskDataHandler.initializeSubTaskData(repository, subTaskData, parentTaskData, new NullProgressMonitor());
		TaskMapper subTaskMapper = new TracTaskMapper(subTaskData, null);
		assertEquals("", subTaskMapper.getSummary());
		assertEquals("", subTaskMapper.getDescription());
		assertEquals(component, subTaskMapper.getComponent());
		TaskAttribute attribute = subTaskData.getRoot().getMappedAttribute(TracTaskDataHandler.ATTRIBUTE_BLOCKING);
		assertEquals(parentTaskData.getTaskId(), attribute.getValue());
		attribute = parentTaskData.getRoot().getMappedAttribute(TracTaskDataHandler.ATTRIBUTE_BLOCKED_BY);
		assertEquals("", attribute.getValue());
	}

	public void testGetSubTaskIds() throws Exception {
		TaskData taskData = new TaskData(new TracAttributeMapper(new TaskRepository("", ""), client),
				TracCorePlugin.CONNECTOR_KIND, "", "");
		TaskAttribute blockedBy = taskData.getRoot().createAttribute(TracTaskDataHandler.ATTRIBUTE_BLOCKED_BY);
		Collection<String> subTaskIds;

		blockedBy.setValue("123 456");
		subTaskIds = getSubTaskIds(taskData);
		assertEquals(2, subTaskIds.size());
		assertTrue(subTaskIds.contains("123"));
		assertTrue(subTaskIds.contains("456"));

		blockedBy.setValue("7,8");
		subTaskIds = getSubTaskIds(taskData);
		assertEquals(2, subTaskIds.size());
		assertTrue(subTaskIds.contains("7"));
		assertTrue(subTaskIds.contains("8"));

		blockedBy.setValue("  7 ,   8,  ");
		subTaskIds = getSubTaskIds(taskData);
		assertEquals(2, subTaskIds.size());
		assertTrue(subTaskIds.contains("7"));
		assertTrue(subTaskIds.contains("8"));

		blockedBy.setValue("7");
		subTaskIds = getSubTaskIds(taskData);
		assertEquals(1, subTaskIds.size());
		assertTrue(subTaskIds.contains("7"));

		blockedBy.setValue("");
		subTaskIds = getSubTaskIds(taskData);
		assertEquals(0, subTaskIds.size());

		blockedBy.setValue("  ");
		subTaskIds = getSubTaskIds(taskData);
		assertEquals(0, subTaskIds.size());
	}

	private Collection<String> getSubTaskIds(TaskData taskData) {
		List<String> subTaskIds = new ArrayList<String>();
		Collection<TaskRelation> relations = connector.getTaskRelations(taskData);
		for (TaskRelation taskRelation : relations) {
			subTaskIds.add(taskRelation.getTaskId());
		}
		return subTaskIds;
	}

	public void testInitializeTaskData() throws Exception {
		TaskData taskData = new TaskData(taskDataHandler.getAttributeMapper(repository), TracCorePlugin.CONNECTOR_KIND,
				"", "");
		TaskMapping mapping = new TaskMapping() {
			@Override
			public String getDescription() {
				return "description";
			}

			@Override
			public String getSummary() {
				return "summary";
			}
		};
		taskDataHandler.initializeTaskData(repository, taskData, mapping, new NullProgressMonitor());
		// initializeTaskData() should ignore the initialization data 
		TaskMapper mapper = new TracTaskMapper(taskData, null);
		assertEquals(null, mapper.getResolution());
		assertEquals("", mapper.getSummary());
		assertEquals("", mapper.getDescription());
		// check for default values
		assertEquals("Defect", mapper.getTaskKind());
		assertEquals("major", mapper.getPriority());
		// empty attributes should not exist
		assertNull(taskData.getRoot().getAttribute(TracAttribute.SEVERITY.getTracKey()));
	}

	public void testOperations() throws Exception {
		boolean hasReassign = TracFixture.current().getVersion().compareTo("0.11") >= 0;

		TaskData taskData = taskDataHandler.getTaskData(repository, "1", new NullProgressMonitor());
		List<TaskAttribute> operations = taskData.getAttributeMapper().getAttributesByType(taskData,
				TaskAttribute.TYPE_OPERATION);
		assertEquals("Unexpected operations: " + operations, (hasReassign ? 5 : 4), operations.size());

		TaskOperation operation = taskData.getAttributeMapper().getTaskOperation(operations.get(0));
		assertEquals(TaskAttribute.OPERATION, operation.getTaskAttribute().getId());

		operation = taskData.getAttributeMapper().getTaskOperation(operations.get(1));
		assertEquals("leave", operation.getOperationId());
		assertNotNull(operation.getLabel());

		operation = taskData.getAttributeMapper().getTaskOperation(operations.get(2));
		assertEquals("resolve", operation.getOperationId());
		assertNotNull(operation.getLabel());
		String associatedId = operation.getTaskAttribute()
				.getMetaData()
				.getValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID);
		assertNotNull(associatedId);

		if (hasReassign) {
			operation = taskData.getAttributeMapper().getTaskOperation(operations.get(3));
			assertEquals("reassign", operation.getOperationId());
			assertNotNull(operation.getLabel());

			operation = taskData.getAttributeMapper().getTaskOperation(operations.get(4));
			assertEquals("accept", operation.getOperationId());
			assertNotNull(operation.getLabel());
		} else {
			operation = taskData.getAttributeMapper().getTaskOperation(operations.get(3));
			assertEquals("accept", operation.getOperationId());
			assertNotNull(operation.getLabel());
		}
	}

	public void testPostTaskDataUnsetResolution() throws Exception {
		TracTicket ticket = TracTestUtil.createTicket(client, "postTaskDataUnsetResolution");
		TaskData taskData = taskDataHandler.getTaskData(repository, ticket.getId() + "", new NullProgressMonitor());
		TaskAttribute attribute = taskData.getRoot().getMappedAttribute(TaskAttribute.RESOLUTION);
		attribute.setValue("fixed");
		taskDataHandler.postTaskData(repository, taskData, null, new NullProgressMonitor());

		// should not set resolution unless resolve operation is selected
		taskData = taskDataHandler.getTaskData(repository, ticket.getId() + "", new NullProgressMonitor());
		attribute = taskData.getRoot().getMappedAttribute(TaskAttribute.RESOLUTION);
		assertEquals("", attribute.getValue());
	}

	public void testPostTaskDataMidAirCollision() throws Exception {
		TracTicket ticket = TracTestUtil.createTicket(client, "midAirCollision");
		if (ticket.getValue(Key.TOKEN) == null) {
			// repository does not have mid-air collision support
			System.err.println("Skipping TracTaskDataHandler.testPostTaskDataMidAirCollision() due to lack of mid-air collision support on "
					+ repository.getRepositoryUrl());
			return;
		}
		TaskData taskData = taskDataHandler.getTaskData(repository, ticket.getId() + "", new NullProgressMonitor());
		TaskAttribute attribute = taskData.getRoot().getMappedAttribute(TaskAttribute.PRIORITY);
		attribute.setValue("blocker");

		// change ticket in repository
		ticket.putBuiltinValue(Key.PRIORITY, "trivial");
		client.updateTicket(ticket, "changing priority", null);

		// submit conflicting change 
		try {
			taskDataHandler.postTaskData(repository, taskData, null, new NullProgressMonitor());
			fail("Expected CoreException due to mid-air collision");
		} catch (CoreException e) {
			assertEquals(RepositoryStatus.createCollisionError(repository.getRepositoryUrl(), TracCorePlugin.ID_PLUGIN)
					.getMessage(), e.getStatus().getMessage());
		}
	}

}
