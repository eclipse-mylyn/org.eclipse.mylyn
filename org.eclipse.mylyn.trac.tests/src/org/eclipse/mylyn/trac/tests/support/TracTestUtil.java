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

package org.eclipse.mylyn.trac.tests.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.context.tests.support.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Steffen Pingel
 */
public class TracTestUtil {

	public static TracTicket createTicket(ITracClient client, String summary) throws Exception {
		TracTicket ticket = new TracTicket();
		ticket.putBuiltinValue(Key.SUMMARY, summary);
		ticket.putBuiltinValue(Key.DESCRIPTION, "");
		int id = client.createTicket(ticket, null);
		return client.getTicket(id, null);
	}

	public static ITask createTask(TaskRepository taskRepository, String taskId) throws Exception {
		AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(TracCorePlugin.CONNECTOR_KIND);
		TaskData taskData = connector.getTaskData(taskRepository, taskId, null);
		ITask task = TasksUi.getRepositoryModel().createTask(taskRepository, taskData.getTaskId());
		TasksUiPlugin.getTaskDataManager().putUpdatedTaskData(task, taskData, true);
		return task;
	}

	public static TaskRepository init(String repositoryUrl, Version version) {
		TracCorePlugin.getDefault().getConnector().getClientManager().writeCache();
		TaskRepositoryManager manager = TasksUiPlugin.getRepositoryManager();
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());

		Credentials credentials = TestUtil.readCredentials(PrivilegeLevel.USER);
		TaskRepository repository = new TaskRepository(TracCorePlugin.CONNECTOR_KIND, repositoryUrl);
		repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials(credentials.username,
				credentials.password), false);
		repository.setTimeZoneId(ITracClient.TIME_ZONE);
		repository.setCharacterEncoding(ITracClient.CHARSET);
		repository.setVersion(version.name());

		manager.addRepository(repository);
		TracCorePlugin.getDefault().getConnector().getClientManager().readCache();

		return repository;
	}

	public static List<ITaskAttachment> getTaskAttachments(ITask task) throws CoreException {
		TaskData taskData = TasksUi.getTaskDataManager().getTaskData(task);
		List<ITaskAttachment> attachments = new ArrayList<ITaskAttachment>();
		List<TaskAttribute> attributes = taskData.getAttributeMapper().getAttributesByType(taskData,
				TaskAttribute.TYPE_ATTACHMENT);
		if (attributes != null) {
			for (TaskAttribute taskAttribute : attributes) {
				ITaskAttachment taskAttachment = TasksUiPlugin.getRepositoryModel().createTaskAttachment(taskAttribute);
				taskData.getAttributeMapper().updateTaskAttachment(taskAttachment, taskAttribute);
				attachments.add(taskAttachment);
			}
		}
		return attachments;
	}

}
