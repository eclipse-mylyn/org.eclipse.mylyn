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
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;
import org.eclipse.mylar.internal.trac.core.TracTask.Kind;
import org.eclipse.mylar.internal.trac.core.model.TracTicket;
import org.eclipse.mylar.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.QueryHitCollector;
import org.eclipse.mylar.tasks.core.RepositoryOperation;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class TracRepositoryConnector extends AbstractRepositoryConnector {

	private final static String CLIENT_LABEL = "Trac (supports 0.9 and later or XML-RPC)";

	private List<String> supportedVersions;

	private TracClientManager clientManager;

	private TracOfflineTaskHandler offlineTaskHandler = new TracOfflineTaskHandler(this);

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
		int i = url.lastIndexOf(ITracClient.TICKET_URL);
		return (i != -1) ? url.substring(0, i) : null;
	}

	@Override
	public List<String> getSupportedVersions() {
		if (supportedVersions == null) {
			supportedVersions = new ArrayList<String>();
			for (Version version : Version.values()) {
				supportedVersions.add(version.toString());
			}
		}
		return supportedVersions;
	}

	@Override
	public IAttachmentHandler getAttachmentHandler() {
		return attachmentHandler;
	}

	@Override
	public IOfflineTaskHandler getOfflineTaskHandler() {
		return offlineTaskHandler;
	}

	@Override
	public void updateTask(TaskRepository repository, AbstractRepositoryTask repositoryTask) throws CoreException {
		if (repositoryTask instanceof TracTask) {
			String id = AbstractRepositoryTask.getTaskId(repositoryTask.getHandleIdentifier());
			try {
				ITracClient connection = getClientManager().getRepository(repository);
				TracTicket ticket = connection.getTicket(Integer.parseInt(id));
				updateTaskDetails((TracTask) repositoryTask, ticket, false);
			} catch (Exception e) {
				throw new CoreException(TracCorePlugin.toStatus(e));
			}
		}
	}

	@Override
	public IStatus performQuery(AbstractRepositoryQuery query, TaskRepository repository,
			Proxy proxySettings, IProgressMonitor monitor, QueryHitCollector resultCollector) {

		final List<TracTicket> tickets = new ArrayList<TracTicket>();

		ITracClient tracClient;
		try {
			tracClient = getClientManager().getRepository(repository);
			if (query instanceof TracRepositoryQuery) {
				tracClient.search(((TracRepositoryQuery) query).getTracSearch(), tickets);
			}

			for (TracTicket ticket : tickets) {
				TracQueryHit hit = new TracQueryHit(taskList, query.getRepositoryUrl(), getTicketDescription(ticket), ticket
						.getId()
						+ "");
				hit.setPriority(TracTask.getMylarPriority(ticket.getValue(Key.PRIORITY)));
				hit.setCompleted(TracTask.isCompleted(ticket.getValue(Key.STATUS)));
				resultCollector.accept(hit);
			}
		} catch (Throwable e) {			
			return TracCorePlugin.toStatus(e);			
		}

		return Status.OK_STATUS;
	}
	
	@Override
	public ITask createTaskFromExistingKey(TaskRepository repository, String id, Proxy proxySettings) throws CoreException {
		int bugId = -1;
		try {
			bugId = Integer.parseInt(id);
		} catch (NumberFormatException e) {
			throw new CoreException(new Status(IStatus.ERROR, TracCorePlugin.PLUGIN_ID, IStatus.OK,
						"Invalid ticket id: " + id, e));
		}
		
		String handle = AbstractRepositoryTask.getHandle(repository.getUrl(), bugId);
		
		TracTask task;
		ITask existingTask = taskList.getTask(handle);
		if (existingTask instanceof TracTask) {
			task = (TracTask) existingTask;
		} else {
			RepositoryTaskData taskData = offlineTaskHandler.downloadTaskData(repository, bugId);
			if (taskData != null) {
				task = new TracTask(handle, getTicketDescription(taskData), true);
				task.setTaskData(taskData);
				taskList.addTask(task);
			} else {
				// repository does not support XML-RPC, fall back to web access
				try {
					ITracClient connection = getClientManager().getRepository(repository);
					TracTicket ticket = connection.getTicket(Integer.parseInt(id));

					task = new TracTask(handle, getTicketDescription(ticket), true);
					updateTaskDetails(task, ticket, false);
					taskList.addTask(task);
				} catch (Exception e) {
					throw new CoreException(TracCorePlugin.toStatus(e));
				}
			}
		}
		return task;
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

	public TracTask createTask(TracTicket ticket, String handleIdentifier) {
		TracTask task;
		ITask existingTask = taskList.getTask(handleIdentifier);
		if (existingTask instanceof TracTask) {
			task = (TracTask) existingTask;
		} else {
			task = new TracTask(handleIdentifier, getTicketDescription(ticket), true);
			taskList.addTask(task);
		}
		return task;
	}

	/**
	 * Updates fields of <code>task</code> from <code>ticket</code>.
	 */
	public void updateTaskDetails(TracTask task, TracTicket ticket, boolean notify) {
		if (ticket.getValue(Key.SUMMARY) != null) {
			task.setDescription(getTicketDescription(ticket));
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
		return ticket.getId() + ": " + ticket.getValue(Key.SUMMARY);
	}

	public static String getTicketDescription(RepositoryTaskData taskData) {
		return taskData.getId() + ":" + taskData.getSummary();
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
			MylarStatusHandler.fail(e, "Could not update attributes", false);
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

	public static TracTicket getTracTicket(TaskRepository repository, RepositoryTaskData data) throws InvalidTicketException {
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