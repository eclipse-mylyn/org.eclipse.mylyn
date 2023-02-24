/*******************************************************************************
 * Copyright (c) 2013 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.support;

import java.io.ByteArrayInputStream;
import java.util.Collections;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.client.InvalidTicketException;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.client.TracXmlRpcClient;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;

public class TracHarness {

	private final TracFixture fixture;

	private TracXmlRpcClient priviledgedClient;

	private TaskRepository repository;

	public TracHarness(TracFixture fixture) {
		this.fixture = fixture;
	}

	public void attachFile(int ticketId, String name, String content) throws Exception {
		priviledgedClient().putAttachmentData(ticketId, name, "", new ByteArrayInputStream(content.getBytes("UTF-8")),
				null, true);
	}

	public TracRepositoryConnector connector() {
		return fixture.connector();
	}

	public void createMilestone(String milestone) throws Exception {
		new XmlRpcServer(priviledgedClient()).ticketMilestone(milestone).deleteAndCreate();
	}

	public ITask createTask(String summary) throws Exception {
		TracTicket ticket = createTicket(summary);
		return getTask(ticket);
	}

	public TaskData createTaskData(String summary) throws Exception {
		TracTicket ticket = createTicket(summary);
		return fixture.connector().getTaskData(repository(), Integer.toString(ticket.getId()), null);
	}

	public TracTicket createTicket(String summary) throws Exception {
		TracTicket ticket = newTicket(summary);
		return createTicket(ticket);
	}

	public TracTicket createTicket(TracTicket ticket) throws TracException, Exception {
		int id = priviledgedClient().createTicket(ticket, null);
		return priviledgedClient().getTicket(id, null);
	}

	public TracTicket createTicketWithMilestone(String summary, String milestone) throws Exception {
		TracTicket ticket = newTicket(summary);
		ticket.putBuiltinValue(Key.MILESTONE, milestone);
		return createTicket(ticket);
	}

	public void createWikiPage(String pageName, String content) throws Exception {
		priviledgedClient().putWikipage(pageName, content, Collections.<String, Object> emptyMap(), null);
	}

	public void dispose() {
		// TODO delete created tickets
	}

	public TracFixture getFixture() {
		return fixture;
	}

	public ITask getTask(String taskId) throws Exception {
		TaskRepository repository = repository();
		TaskData taskData = fixture.connector().getTaskData(repository, taskId, null);
		ITask task = TasksUi.getRepositoryModel().createTask(repository, taskData.getTaskId());
		TasksUiPlugin.getTaskDataManager().putUpdatedTaskData(task, taskData, true);
		return task;
	}

	public ITask getTask(TracTicket ticket) throws Exception {
		return getTask(Integer.toString(ticket.getId()));
	}

	public boolean hasMilestone(String milestone) {
		try {
			new XmlRpcServer(priviledgedClient()).ticketMilestone(milestone).get();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean isXmlRpc() {
		return Version.XML_RPC.name().equals(repository().getVersion());
	}

	public TracTicket newTicket(String summary) throws InvalidTicketException {
		TracTicket ticket = new TracTicket();
		ticket.putBuiltinValue(Key.SUMMARY, summary);
		ticket.putBuiltinValue(Key.DESCRIPTION, "");
		return ticket;
	}

	public TaskRepository repository() {
		if (repository == null) {
			repository = fixture.singleRepository();
		}
		return repository;
	}

	public void udpateTicket(TracTicket ticket) throws Exception {
		priviledgedClient().updateTicket(ticket, "", null);
	}

	private TracXmlRpcClient priviledgedClient() throws Exception {
		if (priviledgedClient == null) {
			priviledgedClient = (TracXmlRpcClient) fixture.connectXmlRpc(PrivilegeLevel.USER);
		}
		return priviledgedClient;
	}

}
