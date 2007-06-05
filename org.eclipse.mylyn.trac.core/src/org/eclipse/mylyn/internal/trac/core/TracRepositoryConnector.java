/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;
import org.eclipse.mylar.internal.trac.core.TracAttributeFactory.Attribute;
import org.eclipse.mylar.internal.trac.core.TracTask.Kind;
import org.eclipse.mylar.internal.trac.core.model.TracTicket;
import org.eclipse.mylar.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylar.internal.trac.core.util.TracUtils;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskDataHandler;
import org.eclipse.mylar.tasks.core.QueryHitCollector;
import org.eclipse.mylar.tasks.core.RepositoryOperation;
import org.eclipse.mylar.tasks.core.RepositoryStatus;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.Task;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class TracRepositoryConnector extends AbstractRepositoryConnector {

	private final static String CLIENT_LABEL = "Trac (supports 0.9 or 0.10 through Web and XML-RPC)";

	private TracClientManager clientManager;

	private TracTaskDataHandler taskDataHandler = new TracTaskDataHandler(this);

	private TracAttachmentHandler attachmentHandler = new TracAttachmentHandler(this);

	public TracRepositoryConnector() {
		TracCorePlugin.getDefault().setConnector(this);
	}

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return true;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return true;
	}

	@Override
	public String getLabel() {
		return CLIENT_LABEL;
	}

	@Override
	public String getRepositoryType() {
		return TracCorePlugin.REPOSITORY_KIND;
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String url) {
		if (url == null) {
			return null;
		}
		int index = url.lastIndexOf(ITracClient.TICKET_URL);
		return index == -1 ? null : url.substring(0, index);
	}

	public String getTaskIdFromTaskUrl(String url) {
		if (url == null) {
			return null;
		}
		int index = url.lastIndexOf(ITracClient.TICKET_URL);
		return index == -1 ? null : url.substring(index + ITracClient.TICKET_URL.length());
	}

	@Override
	public String getTaskWebUrl(String repositoryUrl, String taskId) {
		return repositoryUrl + ITracClient.TICKET_URL + taskId;
	}

	@Override
	public IAttachmentHandler getAttachmentHandler() {
		return attachmentHandler;
	}

	@Override
	public ITaskDataHandler getTaskDataHandler() {
		return taskDataHandler;
	}

	@Override
	public void updateTaskFromRepository(TaskRepository repository, AbstractRepositoryTask repositoryTask,
			IProgressMonitor monitor) throws CoreException {
		if (repositoryTask instanceof TracTask) {
			// String taskId =
			// RepositoryTaskHandleUtil.getTaskId(repositoryTask.getHandleIdentifier());
			try {
				ITracClient connection = getClientManager().getRepository(repository);
				TracTicket ticket = connection.getTicket(Integer.parseInt(repositoryTask.getTaskId()));
				updateTaskFromTicket((TracTask) repositoryTask, ticket, false);
			} catch (Exception e) {
				throw new CoreException(TracCorePlugin.toStatus(e));
			}
		}
	}

	@Override
	public IStatus performQuery(AbstractRepositoryQuery query, TaskRepository repository, IProgressMonitor monitor,
			QueryHitCollector resultCollector, boolean force) {

		final List<TracTicket> tickets = new ArrayList<TracTicket>();

		ITracClient tracClient;
		try {
			tracClient = getClientManager().getRepository(repository);
			if (query instanceof TracRepositoryQuery) {
				tracClient.search(((TracRepositoryQuery) query).getTracSearch(), tickets);
			}

			for (TracTicket ticket : tickets) {
				TracAttributeFactory attrFactory = new TracAttributeFactory();
				RepositoryTaskData data = new RepositoryTaskData(attrFactory, TracCorePlugin.REPOSITORY_KIND,
						repository.getUrl(), "" + ticket.getId(), Task.DEFAULT_TASK_KIND);
				TracTaskDataHandler.createDefaultAttributes(attrFactory, data, tracClient, true);
				TracTaskDataHandler.updateTaskData(repository, attrFactory, data, ticket);

				resultCollector.accept(data);
			}
		} catch (Throwable e) {
			return TracCorePlugin.toStatus(e, repository);
		}

		return Status.OK_STATUS;
	}

	@Override
	public Set<AbstractRepositoryTask> getChangedSinceLastSync(TaskRepository repository,
			Set<AbstractRepositoryTask> tasks) throws CoreException {
		if (repository.getSyncTimeStamp() == null) {
			return tasks;
		}

		if (!TracRepositoryConnector.hasChangedSince(repository)) {
			// return an empty list to avoid causing all tasks to synchronized
			return Collections.emptySet();
		}

		Date since = new Date(0);
		try {
			since = TracUtils.parseDate(Integer.parseInt(repository.getSyncTimeStamp()));
		} catch (NumberFormatException e) {
		}

		ITracClient client;
		try {
			client = getClientManager().getRepository(repository);
			Set<Integer> ids = client.getChangedTickets(since);

			Set<AbstractRepositoryTask> result = new HashSet<AbstractRepositoryTask>();
			if (!ids.isEmpty()) {
				for (AbstractRepositoryTask task : tasks) {
					Integer id = Integer.parseInt(task.getTaskId());
					if (ids.contains(id)) {
						result.add(task);
					}
				}
			}
			return result;
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, TracCorePlugin.PLUGIN_ID, IStatus.OK,
					"could not determine changed tasks", e));
		}
	}

	@Override
	public AbstractRepositoryTask createTaskFromExistingId(TaskRepository repository, String taskId,
			IProgressMonitor monitor) throws CoreException {
		AbstractRepositoryTask task = super.createTaskFromExistingId(repository, taskId, monitor);
		if (task == null) {
			// repository does not support XML-RPC, fall back to web access
			try {
				int taskIdInt = getTicketId(taskId);
				ITracClient connection = getClientManager().getRepository(repository);
				TracTicket ticket = connection.getTicket(taskIdInt);

				task = new TracTask(repository.getUrl(), taskId, getTicketDescription(ticket), true);
				updateTaskFromTicket((TracTask) task, ticket, false);
				taskList.addTask(task);
			} catch (Exception e) {
				throw new CoreException(TracCorePlugin.toStatus(e, repository));
			}
		}

		return task;
	}

	public AbstractRepositoryTask createTask(String repositoryUrl, String id, String summary) {
		return new TracTask(repositoryUrl, id, "<description not set>", true);
	}

	public void updateTaskFromTaskData(TaskRepository repository, AbstractRepositoryTask repositoryTask,
			RepositoryTaskData taskData) {
		if (taskData != null) {
			repositoryTask.setSummary(getTicketDescription(taskData));
			repositoryTask.setOwner(taskData.getAttributeValue(RepositoryTaskAttribute.USER_ASSIGNED));
			repositoryTask.setCompleted(TracTask.isCompleted(taskData.getStatus()));
			repositoryTask.setTaskUrl(repository.getUrl() + ITracClient.TICKET_URL + taskData.getId());
			repositoryTask.setPriority(TracTask.getMylarPriority(taskData.getAttributeValue(Attribute.PRIORITY
					.getTracKey())));
			Kind kind = TracTask.Kind.fromType(taskData.getAttributeValue(Attribute.TYPE.getTracKey()));
			repositoryTask.setKind((kind != null) ? kind.toString() : null);
			// TODO: Completion Date
		}
	}

	public static int getTicketId(String taskId) throws CoreException {
		try {
			return Integer.parseInt(taskId);
		} catch (NumberFormatException e) {
			throw new CoreException(new Status(IStatus.ERROR, TracCorePlugin.PLUGIN_ID, IStatus.OK,
					"Invalid ticket id: " + taskId, e));
		}
	}

	public synchronized TracClientManager getClientManager() {
		if (clientManager == null) {
			File cacheFile = null;
			if (TracCorePlugin.getDefault().getRepostioryAttributeCachePath() != null) {
				cacheFile = TracCorePlugin.getDefault().getRepostioryAttributeCachePath().toFile();
			}
			clientManager = new TracClientManager(cacheFile);
		}
		return clientManager;
	}

	public TracTask createTask(TracTicket ticket, String repositoryUrl, String taskId) {
		TracTask task;
		// String handleIdentifier =
		// AbstractRepositoryTask.getHandle(repositoryUrl, taskId);
		ITask existingTask = taskList.getTask(repositoryUrl, taskId);
		if (existingTask instanceof TracTask) {
			task = (TracTask) existingTask;
		} else {
			task = new TracTask(repositoryUrl, taskId, getTicketDescription(ticket), true);
			taskList.addTask(task);
		}
		return task;
	}

	/**
	 * Updates fields of <code>task</code> from <code>ticket</code>.
	 */
	public void updateTaskFromTicket(TracTask task, TracTicket ticket, boolean notify) {
		if (ticket.getValue(Key.SUMMARY) != null) {
			task.setSummary(getTicketDescription(ticket));
		}
		task.setCompleted(TracTask.isCompleted(ticket.getValue(Key.STATUS)));
		task.setPriority(TracTask.getMylarPriority(ticket.getValue(Key.PRIORITY)));
		if (ticket.getValue(Key.TYPE) != null) {
			Kind kind = TracTask.Kind.fromType(ticket.getValue(Key.TYPE));
			task.setKind((kind != null) ? kind.toString() : ticket.getValue(Key.TYPE));
		}
		if (ticket.getCreated() != null) {
			task.setCreationDate(ticket.getCreated());
		}

		if (notify) {
			taskList.notifyLocalInfoChanged(task);
		}
	}

	public static String getTicketDescription(TracTicket ticket) {
		return /* ticket.getId() + ": " + */ticket.getValue(Key.SUMMARY);
	}

	public static String getTicketDescription(RepositoryTaskData taskData) {
		return /* taskData.getId() + ":" + */taskData.getSummary();
	}

	public static boolean hasChangedSince(TaskRepository repository) {
		return Version.XML_RPC.name().equals(repository.getVersion());
	}

	public static boolean hasRichEditor(TaskRepository repository) {
		return Version.XML_RPC.name().equals(repository.getVersion());
	}

	public static boolean hasRichEditor(TaskRepository repository, AbstractRepositoryTask task) {
		return hasRichEditor(repository);
	}

	public static boolean hasAttachmentSupport(TaskRepository repository, AbstractRepositoryTask task) {
		return Version.XML_RPC.name().equals(repository.getVersion());
	}

	public void stop() {
		if (clientManager != null) {
			clientManager.writeCache();
		}
	}

	@Override
	public void updateAttributes(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		try {
			ITracClient client = getClientManager().getRepository(repository);
			client.updateAttributes(monitor, true);
		} catch (Exception e) {
			throw new CoreException(RepositoryStatus.createStatus(repository.getUrl(), IStatus.WARNING,
					TracCorePlugin.PLUGIN_ID, "Could not update attributes"));
		}
	}

	public static String getDisplayUsername(TaskRepository repository) {
		if (!repository.hasCredentials()) {
			return ITracClient.DEFAULT_USERNAME;
		}
		return repository.getUserName();
	}

	@Override
	public String getTaskIdPrefix() {
		return "#";
	}

	public static TracTicket getTracTicket(TaskRepository repository, RepositoryTaskData data)
			throws InvalidTicketException {
		TracTicket ticket = new TracTicket(Integer.parseInt(data.getId()));

		List<RepositoryTaskAttribute> attributes = data.getAttributes();
		for (RepositoryTaskAttribute attribute : attributes) {
			if (TracAttributeFactory.isInternalAttribute(attribute.getID())) {
				// ignore
			} else if (!attribute.isReadOnly()) {
				ticket.putValue(attribute.getID(), attribute.getValue());
			}
		}

		// set cc value
		StringBuilder sb = new StringBuilder();
		List<String> removeValues = data.getAttributeValues(RepositoryTaskAttribute.REMOVE_CC);
		List<String> values = data.getAttributeValues(RepositoryTaskAttribute.USER_CC);
		for (String user : values) {
			if (!removeValues.contains(user)) {
				if (sb.length() > 0) {
					sb.append(",");
				}
				sb.append(user);
			}
		}
		if (data.getAttributeValue(RepositoryTaskAttribute.NEW_CC).length() > 0) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(data.getAttributeValue(RepositoryTaskAttribute.NEW_CC));
		}
		if (RepositoryTaskAttribute.TRUE.equals(data.getAttributeValue(RepositoryTaskAttribute.ADD_SELF_CC))) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(repository.getUserName());
		}
		ticket.putBuiltinValue(Key.CC, sb.toString());

		RepositoryOperation operation = data.getSelectedOperation();
		if (operation != null) {
			String action = operation.getKnobName();
			if (!"leave".equals(action)) {
				if ("accept".equals(action)) {
					ticket.putValue("status", "assigned");
					ticket.putValue("owner", getDisplayUsername(repository));
				} else if ("resolve".equals(action)) {
					ticket.putValue("status", "closed");
					ticket.putValue("resolution", operation.getOptionSelection());
				} else if ("reopen".equals(action)) {
					ticket.putValue("status", "reopened");
					ticket.putValue("resolution", "");
				} else if ("reassign".equals(operation.getKnobName())) {
					ticket.putValue("status", "new");
					ticket.putValue("owner", operation.getInputValue());
				}
			}
		}

		return ticket;
	}

}