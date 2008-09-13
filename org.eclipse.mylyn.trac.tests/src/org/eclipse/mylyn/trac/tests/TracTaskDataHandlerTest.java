/*******************************************************************************
* Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylyn.internal.trac.core.util.TracUtil;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.data.TaskRelation;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.trac.tests.support.TestFixture;
import org.eclipse.mylyn.trac.tests.support.TracTestConstants;
import org.eclipse.mylyn.trac.tests.support.TracTestUtil;
import org.eclipse.mylyn.trac.tests.support.XmlRpcServer.TestData;

/**
 * @author Steffen Pingel
 */
public class TracTaskDataHandlerTest extends TestCase {

	private TracRepositoryConnector connector;

	private TaskRepository repository;

	private TestData data;

	private TracTaskDataHandler taskDataHandler;

	private ITracClient client;

	public TracTaskDataHandlerTest() {
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		data = TestFixture.init010();
		connector = (TracRepositoryConnector) TasksUi.getRepositoryConnector(TracCorePlugin.CONNECTOR_KIND);
		taskDataHandler = connector.getTaskDataHandler();
	}

	protected void init(String url, Version version) {
		repository = TracTestUtil.init(url, version);
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

	public void testPreSynchronizationWeb096() throws Exception {
		init(TracTestConstants.TEST_TRAC_096_URL, Version.TRAC_0_9);
		ITask task = TracTestUtil.createTask(repository, data.offlineHandlerTicketId + "");

		Set<ITask> tasks = new HashSet<ITask>();
		tasks.add(task);
		SynchronizationSession session = createSession();
		session.setTasks(tasks);

		assertEquals(null, repository.getSynchronizationTimeStamp());
		connector.preSynchronization(session, null);
		assertTrue(session.needsPerformQueries());
		assertEquals(null, repository.getSynchronizationTimeStamp());
		// bug 238043: assertEquals(Collections.emptySet(), session.getStaleTasks());
		assertEquals(null, session.getStaleTasks());

		int time = (int) (System.currentTimeMillis() / 1000) + 1;
		repository.setSynchronizationTimeStamp(time + "");
		connector.preSynchronization(session, null);
		assertTrue(session.needsPerformQueries());
		// bug 238043: assertEquals(Collections.emptySet(), session.getStaleTasks());
		assertEquals(null, session.getStaleTasks());
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
		SynchronizationSession session;
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
		// TODO this was fixed so it returns false now but only if the 
		// query returns a single task
		assertFalse(session.needsPerformQueries());
		// bug 238043: assertEquals(Collections.emptySet(), session.getStaleTasks());
		assertEquals(null, session.getStaleTasks());

		repository.setSynchronizationTimeStamp((lastModified + 1) + "");
		session = createSession(task);
		connector.preSynchronization(session, null);
		assertFalse(session.needsPerformQueries());
		// bug 238043: assertEquals(Collections.emptySet(), session.getStaleTasks());
		assertEquals(null, session.getStaleTasks());

		// change ticket making sure it gets a new change time
		Thread.sleep(1000);
		ticket.putBuiltinValue(Key.DESCRIPTION, lastModified + "");
		client.updateTicket(ticket, "comment", null);

		repository.setSynchronizationTimeStamp((lastModified + 1) + "");
		session = createSession(task);
		connector.preSynchronization(session, null);
		assertTrue(session.needsPerformQueries());
		assertEquals(Collections.singleton(task), session.getStaleTasks());
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

	public void testAttachmentChangesLastModifiedDate010() throws Exception {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC);
		attachmentChangesLastModifiedDate();
	}

	public void testAttachmentChangesLastModifiedDate011() throws Exception {
		init(TracTestConstants.TEST_TRAC_011_URL, Version.XML_RPC);
		attachmentChangesLastModifiedDate();
	}

	private void attachmentChangesLastModifiedDate() throws Exception {
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

	public void testAttachmentUrlEncoding010() throws Exception {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC);
		attachmentUrlEncoding();
	}

	public void testAttachmentUrlEncoding011() throws Exception {
		init(TracTestConstants.TEST_TRAC_011_URL, Version.XML_RPC);
		attachmentUrlEncoding();
	}

	private void attachmentUrlEncoding() throws Exception {
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

	public void testPostTaskDataInvalidCredentials010() throws Exception {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC);
		postTaskDataInvalidCredentials();
	}

	public void testPostTaskDataInvalidCredentials011() throws Exception {
		init(TracTestConstants.TEST_TRAC_011_URL, Version.XML_RPC);
		postTaskDataInvalidCredentials();
	}

	private void postTaskDataInvalidCredentials() throws Exception {
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
		init(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC);

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
		init(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC);
		TaskData parentTaskData = taskDataHandler.getTaskData(repository, data.offlineHandlerTicketId + "",
				new NullProgressMonitor());
		try {
			taskDataHandler.initializeSubTaskData(repository, parentTaskData, parentTaskData, null);
			fail("expected CoreException");
		} catch (CoreException expected) {
		}
	}

	public void testInitializeSubTaskData() throws Exception {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC);
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
		assertEquals(parentTaskData.getTaskId(), subTaskData.getRoot().getMappedAttribute(
				TracTaskDataHandler.ATTRIBUTE_BLOCKING).getValue());
		assertEquals("", parentTaskData.getRoot()
				.getMappedAttribute(TracTaskDataHandler.ATTRIBUTE_BLOCKED_BY)
				.getValue());
	}

	public void testGetSubTaskIds() throws Exception {
		TaskData taskData = new TaskData(new TracAttributeMapper(new TaskRepository("", "")),
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

}
