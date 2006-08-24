/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.trac;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.internal.trac.TracAttributeFactory.Attribute;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.TracUtils;
import org.eclipse.mylar.internal.trac.model.TracAttachment;
import org.eclipse.mylar.internal.trac.model.TracComment;
import org.eclipse.mylar.internal.trac.model.TracTicket;
import org.eclipse.mylar.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.RepositoryAttachment;
import org.eclipse.mylar.tasks.core.RepositoryOperation;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskComment;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Steffen Pingel
 */
public class TracOfflineTaskHandler implements IOfflineTaskHandler {

	private AbstractAttributeFactory attributeFactory = new TracAttributeFactory();

	private TracRepositoryConnector connector;

	public TracOfflineTaskHandler(TracRepositoryConnector connector) {
		this.connector = connector;
	}

	public RepositoryTaskData downloadTaskData(final AbstractRepositoryTask task) throws CoreException {
		if (!connector.hasRichEditor(task)) {
			// offline mode is only supported for XML-RPC
			return null;
		}

		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(TracUiPlugin.REPOSITORY_KIND,
				task.getRepositoryUrl());
		try {
			int id = Integer.parseInt(AbstractRepositoryTask.getTaskId(task.getHandleIdentifier()));
			RepositoryTaskData data = new RepositoryTaskData(attributeFactory, TracUiPlugin.REPOSITORY_KIND, repository
					.getUrl(), id + "");
			ITracClient client = connector.getClientManager().getRepository(repository);
			client.updateAttributes(new NullProgressMonitor(), false);
			TracTicket ticket = client.getTicket(id);
			createDefaultAttributes(attributeFactory, data, client);
			updateTaskData(repository, attributeFactory, data, ticket);
			return data;
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, TracUiPlugin.PLUGIN_ID, 0, "Ticket download from "
					+ task.getRepositoryUrl() + " failed, please see details.", e));
		}
	}

	public AbstractAttributeFactory getAttributeFactory() {
		return attributeFactory;
	}

	public Date getDateForAttributeType(String attributeKey, String dateString) {
		if (dateString == null || dateString.length() == 0) {
			return null;
		}

		try {
			String mappedKey = attributeFactory.mapCommonAttributeKey(attributeKey);
			if (mappedKey.equals(Attribute.TIME.getTracKey()) || mappedKey.equals(Attribute.CHANGE_TIME.getTracKey())) {
				return TracUtils.parseDate(Integer.valueOf(dateString));
			}
		} catch (Exception e) {
		}
		return null;
	}

	public static void updateTaskData(TaskRepository repository, AbstractAttributeFactory factory,
			RepositoryTaskData data, TracTicket ticket) {
		if (ticket.getCreated() != null) {
			data.setAttributeValue(Attribute.TIME.getTracKey(), TracUtils.toTracTime(ticket.getCreated()) + "");
		}
		if (ticket.getLastChanged() != null) {
			data.setAttributeValue(Attribute.CHANGE_TIME.getTracKey(), TracUtils.toTracTime(ticket.getLastChanged())
					+ "");
		}
		Map<String, String> valueByKey = ticket.getValues();
		for (String key : valueByKey.keySet()) {
			data.setAttributeValue(key, valueByKey.get(key));
		}

		TracComment[] comments = ticket.getComments();
		if (comments != null) {
			for (int i = 0; i < comments.length; i++) {
				if (!"comment".equals(comments[i].getField())
						|| "".equals(comments[i].getNewValue())) {
					continue;
				}

				TaskComment taskComment = new TaskComment(factory, data, data.getComments().size() + 1);
				taskComment.setAttributeValue(RepositoryTaskAttribute.USER_OWNER, comments[i].getAuthor());
				taskComment
						.setAttributeValue(RepositoryTaskAttribute.COMMENT_DATE, comments[i].getCreated().toString());
				taskComment.setAttributeValue(RepositoryTaskAttribute.COMMENT_TEXT, comments[i].getNewValue());
				data.addComment(taskComment);
			}
		}

		TracAttachment[] attachments = ticket.getAttachments();
		if (attachments != null) {
			for (int i = 0; i < attachments.length; i++) {
				RepositoryAttachment taskAttachment = new RepositoryAttachment(factory);
				taskAttachment.setCreator(attachments[i].getAuthor());
				taskAttachment.setAttributeValue(Attribute.DESCRIPTION.getTracKey(), attachments[i].getDescription());
				taskAttachment.setAttributeValue(RepositoryTaskAttribute.ATTACHMENT_FILENAME, attachments[i]
						.getFilename());
				taskAttachment.setAttributeValue(RepositoryTaskAttribute.USER_OWNER, attachments[i].getAuthor());
				taskAttachment.setAttributeValue(RepositoryTaskAttribute.ATTACHMENT_DATE, attachments[i].getCreated()
						.toString());
				taskAttachment.setAttributeValue(RepositoryTaskAttribute.ATTACHMENT_URL, repository.getUrl()
						+ ITracClient.TICKET_ATTACHMENT_URL + ticket.getId() + "/" + attachments[i].getFilename());
				taskAttachment.setAttributeValue(RepositoryTaskAttribute.ATTACHMENT_ID, i + "");
				data.addAttachment(taskAttachment);
			}
		}
		
		String[] actions = ticket.getActions();
		for (String action : actions) {
			addOperation(repository, data, ticket, action);
		}
	}

	// TODO Reuse Labels from BugzillaServerFacade
	private static void addOperation(TaskRepository repository, RepositoryTaskData data, TracTicket ticket, String action) {
		RepositoryOperation operation = null;
		if ("leave".equals(action)) {
			operation = new RepositoryOperation(action, "Leave as " + data.getStatus() + " "
					+ data.getResolution());
			operation.setChecked(true);
		} else if ("accept".equals(action)) {
			operation = new RepositoryOperation(action, "Accept");
		} else if ("resolve".equals(action)) {
			operation = new RepositoryOperation(action, "Resolve bug, changing resolution to");
			operation.setUpOptions("resolution");
			for (String resolution : ticket.getResolutions()) {
				operation.addOption(resolution, resolution);
			}
		} else if ("reassign".equals(action)) {
			String localUser = repository.getUserName();
			operation = new RepositoryOperation(action, "Reassing bug to");
			// FIXME owner?
			operation.setInputName("assign_to");
			operation.setInputValue(localUser);
		} else if ("reopen".equals(action)) {
			operation = new RepositoryOperation(action, "Reopen");
		}
		
		if (operation != null) {
			data.addOperation(operation);
		}
	}

	public static void createDefaultAttributes(AbstractAttributeFactory factory, RepositoryTaskData data,
			ITracClient client) {
		createAttribute(factory, data, Attribute.STATUS, client.getTicketStatus());
		createAttribute(factory, data, Attribute.RESOLUTION, client.getTicketResolutions());

		createAttribute(factory, data, Attribute.COMPONENT, client.getComponents());
		createAttribute(factory, data, Attribute.VERSION, client.getVersions(), true);
		createAttribute(factory, data, Attribute.PRIORITY, client.getPriorities());
		createAttribute(factory, data, Attribute.SEVERITY, client.getSeverities());

		createAttribute(factory, data, Attribute.TYPE, client.getTicketTypes());
		createAttribute(factory, data, Attribute.OWNER);
		createAttribute(factory, data, Attribute.MILESTONE, client.getMilestones(), true);
		createAttribute(factory, data, Attribute.REPORTER);

		createAttribute(factory, data, Attribute.CC);
		createAttribute(factory, data, Attribute.KEYWORDS);
	}

	private static RepositoryTaskAttribute createAttribute(AbstractAttributeFactory factory, RepositoryTaskData data,
			Attribute attribute, Object[] values, boolean allowEmtpy) {
		RepositoryTaskAttribute attr = factory.createAttribute(attribute.getTracKey());
		if (values != null && values.length > 0) {
			if (allowEmtpy) {
				attr.addOptionValue("", "");
			}
			for (int i = 0; i < values.length; i++) {
				attr.addOptionValue(values[i].toString(), values[i].toString());
			}
		} else {
			//attr.setHidden(true);
			attr.setReadOnly(true);
		}
		data.addAttribute(attribute.getTracKey(), attr);
		return attr;
	}

	private static RepositoryTaskAttribute createAttribute(AbstractAttributeFactory factory, RepositoryTaskData data,
			Attribute attribute) {
		RepositoryTaskAttribute attr = factory.createAttribute(attribute.getTracKey());
		data.addAttribute(attribute.getTracKey(), attr);
		return attr;
	}

	private static RepositoryTaskAttribute createAttribute(AbstractAttributeFactory factory, RepositoryTaskData data,
			Attribute attribute, Object[] values) {
		return createAttribute(factory, data, attribute, values, false);
	}

	public Set<AbstractRepositoryTask> getChangedSinceLastSync(TaskRepository repository,
			Set<AbstractRepositoryTask> tasks) throws Exception {
		if (repository.getSyncTimeStamp() == null) {
			return tasks;
		}

		Date since = new Date(0);
		if (repository.getSyncTimeStamp() != null) {
			try {
				since = TracUtils.parseDate(Integer.parseInt(repository.getSyncTimeStamp()));
			} catch (NumberFormatException e) {
			}
		}

		ITracClient client = connector.getClientManager().getRepository(repository);
		Set<Integer> ids = client.getChangedTickets(since);
		if (ids == null) {
			// the call is not supported, any task may have changed
			return tasks;
		}
		
		Set<AbstractRepositoryTask> result = new HashSet<AbstractRepositoryTask>();
		for (AbstractRepositoryTask task : tasks) {
			Integer id = Integer.parseInt(AbstractRepositoryTask.getTaskId(task.getHandleIdentifier()));
			if (ids.contains(id)) {
				result.add(task);
			}
		}
		return result;
	}

}
