/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.trac.core.model.TracAttachment;
import org.eclipse.mylyn.internal.trac.core.model.TracComment;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketField;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylyn.internal.trac.core.util.TracUtils;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.data.TaskOperation;

/**
 * @author Steffen Pingel
 */
public class TracTaskDataHandler extends AbstractTaskDataHandler {

	public static final String ATTRIBUTE_BLOCKED_BY = "blockedby";

	public static final String ATTRIBUTE_BLOCKING = "blocking";

	private static final String CC_DELIMETER = ", ";

	private final TracRepositoryConnector connector;

	public TracTaskDataHandler(TracRepositoryConnector connector) {
		this.connector = connector;
	}

	public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		monitor = Policy.monitorFor(monitor);
		try {
			monitor.beginTask("Task Download", IProgressMonitor.UNKNOWN);
			return downloadTaskData(repository, TracRepositoryConnector.getTicketId(taskId), monitor);
		} finally {
			monitor.done();
		}
	}

	public TaskData downloadTaskData(TaskRepository repository, int taskId, IProgressMonitor monitor)
			throws CoreException {
		ITracClient client = connector.getClientManager().getTracClient(repository);
		TracTicket ticket;
		try {
			client.updateAttributes(monitor, false);
			ticket = client.getTicket(taskId, monitor);
		} catch (OperationCanceledException e) {
			throw e;
		} catch (Exception e) {
			// TODO catch TracException
			throw new CoreException(TracCorePlugin.toStatus(e, repository));
		}
		return createTaskDataFromTicket(client, repository, ticket, monitor);
	}

	public TaskData createTaskDataFromTicket(ITracClient client, TaskRepository repository, TracTicket ticket,
			IProgressMonitor monitor) throws CoreException {
		TaskData taskData = new TaskData(getAttributeMapper(repository), TracCorePlugin.CONNECTOR_KIND,
				repository.getRepositoryUrl(), ticket.getId() + "");
		try {
			if (!TracRepositoryConnector.hasRichEditor(repository)) {
				createDefaultAttributes(taskData, client, true);
				updateTaskData(repository, taskData, ticket);
				//updateTaskDataFromTicket(taskData, ticket, client);
				taskData.setPartial(true);
			} else {
				createDefaultAttributes(taskData, client, true);
				updateTaskData(repository, taskData, ticket);
			}
			return taskData;
		} catch (OperationCanceledException e) {
			throw e;
		} catch (Exception e) {
			// TODO catch TracException
			throw new CoreException(TracCorePlugin.toStatus(e, repository));
		}
	}

	public static void updateTaskData(TaskRepository repository, TaskData data, TracTicket ticket) {
		if (ticket.getCreated() != null) {
			data.getRoot().getAttribute(TracAttribute.TIME.getTracKey()).setValue(
					TracUtils.toTracTime(ticket.getCreated()) + "");
		}

		Date lastChanged = ticket.getLastChanged();

		Map<String, String> valueByKey = ticket.getValues();
		for (String key : valueByKey.keySet()) {
			if (Key.CC.getKey().equals(key)) {
				StringTokenizer t = new StringTokenizer(valueByKey.get(key), CC_DELIMETER);
				while (t.hasMoreTokens()) {
					data.getRoot().getAttribute(key).addValue(t.nextToken());
				}
			} else {
				data.getRoot().getAttribute(key).setValue(valueByKey.get(key));
			}
		}

		TracComment[] comments = ticket.getComments();
		if (comments != null) {
			int count = 1;
			for (int i = 0; i < comments.length; i++) {
				if (!"comment".equals(comments[i].getField()) || "".equals(comments[i].getNewValue())) {
					continue;
				}

				TaskCommentMapper mapper = new TaskCommentMapper();
				mapper.setAuthor(repository.createPerson(comments[i].getAuthor()));
				mapper.setCreationDate(comments[i].getCreated());
				mapper.setText(comments[i].getNewValue());
				mapper.setNumber(count);

				TaskAttribute attribute = data.getRoot().createAttribute(TaskAttribute.PREFIX_COMMENT + count);
				mapper.applyTo(attribute);
				count++;
			}
		}

		TracAttachment[] attachments = ticket.getAttachments();
		if (attachments != null) {
			for (int i = 0; i < attachments.length; i++) {
				TaskAttachmentMapper mapper = new TaskAttachmentMapper();
				mapper.setAuthor(repository.createPerson(attachments[i].getAuthor()));
				mapper.setDescription(attachments[i].getDescription());
				mapper.setFileName(attachments[i].getFilename());
				mapper.setLength((long) attachments[i].getSize());
				if (attachments[i].getCreated() != null) {
					if (lastChanged == null || attachments[i].getCreated().after(lastChanged)) {
						lastChanged = attachments[i].getCreated();
					}
					mapper.setCreationDate(attachments[i].getCreated());
				}
				mapper.setUrl(repository.getRepositoryUrl() + ITracClient.TICKET_ATTACHMENT_URL + ticket.getId() + "/"
						+ attachments[i].getFilename());
				mapper.setAttachmentId(i + "");

				TaskAttribute attribute = data.getRoot().createAttribute(TaskAttribute.PREFIX_ATTACHMENT + (i + 1));
				mapper.applyTo(attribute);
			}
		}

		String[] actions = ticket.getActions();
		if (actions != null) {
			// add operations in a defined order
			List<String> actionList = new ArrayList<String>(Arrays.asList(actions));
			addOperation(repository, data, ticket, actionList, "leave");
			addOperation(repository, data, ticket, actionList, "accept");
			addOperation(repository, data, ticket, actionList, "resolve");
			addOperation(repository, data, ticket, actionList, "reopen");
		}

		if (lastChanged != null) {
			data.getRoot().getAttribute(TracAttribute.CHANGE_TIME.getTracKey()).setValue(
					TracUtils.toTracTime(lastChanged) + "");
		}
	}

	// TODO Reuse Labels from BugzillaServerFacade
	private static void addOperation(TaskRepository repository, TaskData data, TracTicket ticket, List<String> actions,
			String action) {
		if (!actions.remove(action)) {
			return;
		}

		String label = null;
		if ("leave".equals(action)) {
			// TODO
			//label = "Leave as " + data.getStatus() + " " + data.getResolution();
			label = "Leave";
		} else if ("accept".equals(action)) {
			label = "Accept";
		} else if ("resolve".equals(action)) {
			label = "Resolve as";
		} else if ("reopen".equals(action)) {
			label = "Reopen";
		}

		if (label != null) {
			TaskAttribute attribute = data.getRoot().createAttribute(TaskAttribute.PREFIX_OPERATION + action);
			TaskOperation.applyTo(attribute, action, label);
			if ("resolve".equals(action)) {
				attribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID,
						TracAttribute.RESOLUTION.getTracKey());
			}
		}
	}

	public static void createDefaultAttributes(TaskData data, ITracClient client, boolean existingTask) {
		createAttribute(data, TracAttribute.SUMMARY);
		createAttribute(data, TracAttribute.DESCRIPTION);
		if (existingTask) {
			createAttribute(data, TracAttribute.TIME);
			createAttribute(data, TracAttribute.CHANGE_TIME);
			createAttribute(data, TracAttribute.STATUS, client.getTicketStatus());
			createAttribute(data, TracAttribute.RESOLUTION, client.getTicketResolutions());
		}
		createAttribute(data, TracAttribute.COMPONENT, client.getComponents());
		createAttribute(data, TracAttribute.VERSION, client.getVersions(), true);
		createAttribute(data, TracAttribute.PRIORITY, client.getPriorities());
		createAttribute(data, TracAttribute.SEVERITY, client.getSeverities());
		createAttribute(data, TracAttribute.MILESTONE, client.getMilestones(), true);
		createAttribute(data, TracAttribute.TYPE, client.getTicketTypes());
		createAttribute(data, TracAttribute.KEYWORDS);
		TracTicketField[] fields = client.getTicketFields();
		if (fields != null) {
			for (TracTicketField field : fields) {
				if (field.isCustom()) {
					createAttribute(data, field);
				}
			}
		}
		// people
		createAttribute(data, TracAttribute.OWNER);
		if (existingTask) {
			createAttribute(data, TracAttribute.REPORTER);
		}
		createAttribute(data, TracAttribute.CC);
		if (existingTask) {
			data.getRoot().createAttribute(TracAttributeMapper.NEW_CC).getMetaData().setType(
					TaskAttribute.TYPE_SHORT_TEXT).setReadOnly(false);
			data.getRoot().createAttribute(TracAttributeMapper.REMOVE_CC);
			data.getRoot().createAttribute(TaskAttribute.COMMENT_NEW).getMetaData().setType(
					TaskAttribute.TYPE_LONG_RICH_TEXT).setReadOnly(false);
		}
		// operations
		data.getRoot().createAttribute(TaskAttribute.OPERATION).getMetaData().setType(TaskAttribute.TYPE_OPERATION);
	}

	private static void createAttribute(TaskData data, TracTicketField field) {
		TaskAttribute attr = data.getRoot().createAttribute(field.getName());
		attr.getMetaData().setLabel(field.getLabel());
		attr.getMetaData().setKind(TaskAttribute.KIND_DEFAULT);
		if (field.getType() == TracTicketField.Type.CHECKBOX) {
			// attr.addOption("True", "1");
			// attr.addOption("False", "0");
			attr.getMetaData().setType(TaskAttribute.TYPE_BOOLEAN);
			attr.putOption("1", "1");
			attr.putOption("0", "0");
			if (field.getDefaultValue() != null) {
				attr.setValue(field.getDefaultValue());
			}
		} else if (field.getType() == TracTicketField.Type.SELECT || field.getType() == TracTicketField.Type.RADIO) {
			attr.getMetaData().setType(TaskAttribute.TYPE_SINGLE_SELECT);
			String[] values = field.getOptions();
			if (values != null && values.length > 0) {
				if (field.isOptional()) {
					attr.putOption("", "");
				}
				for (String value : values) {
					attr.putOption(value, value);
				}
				if (field.getDefaultValue() != null) {
					try {
						int index = Integer.parseInt(field.getDefaultValue());
						if (index > 0 && index < values.length) {
							attr.setValue(values[index]);
						}
					} catch (NumberFormatException e) {
						for (String value : values) {
							if (field.getDefaultValue().equals(value.toString())) {
								attr.setValue(value);
								break;
							}
						}
					}
				}
			}
		} else {
			if (field.getDefaultValue() != null) {
				attr.setValue(field.getDefaultValue());
			}
		}
	}

	private static TaskAttribute createAttribute(TaskData data, TracAttribute tracAttribute) {
		TaskAttribute attr = data.getRoot().createAttribute(tracAttribute.getTracKey());
		attr.getMetaData().setType(tracAttribute.getType());
		attr.getMetaData().setKind(tracAttribute.getKind());
		attr.getMetaData().setLabel(tracAttribute.toString());
		attr.getMetaData().setReadOnly(tracAttribute.isReadOnly());
		return attr;
	}

	private static TaskAttribute createAttribute(TaskData data, TracAttribute tracAttribute, Object[] values,
			boolean allowEmtpy) {
		TaskAttribute attr = createAttribute(data, tracAttribute);
		if (values != null && values.length > 0) {
			if (allowEmtpy) {
				attr.putOption("", "");
			}
			for (Object value : values) {
				attr.putOption(value.toString(), value.toString());
			}
		} else {
			attr.getMetaData().setReadOnly(true);
		}
		return attr;
	}

	private static TaskAttribute createAttribute(TaskData data, TracAttribute tracAttribute, Object[] values) {
		return createAttribute(data, tracAttribute, values, false);
	}

	@Override
	public RepositoryResponse postTaskData(TaskRepository repository, TaskData taskData,
			Set<TaskAttribute> oldAttributes, IProgressMonitor monitor) throws CoreException {
		try {
			TracTicket ticket = TracTaskDataHandler.getTracTicket(repository, taskData);
			ITracClient server = connector.getClientManager().getTracClient(repository);
			if (taskData.isNew()) {
				int id = server.createTicket(ticket, monitor);
				return new RepositoryResponse(ResponseKind.TASK_CREATED, id + "");
			} else {
				String newComment = "";
				TaskAttribute newCommentAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW);
				if (newCommentAttribute != null) {
					newComment = newCommentAttribute.getValue();
				}
				server.updateTicket(ticket, newComment, monitor);
				return new RepositoryResponse(ResponseKind.TASK_UPDATED, ticket.getId() + "");
			}
		} catch (OperationCanceledException e) {
			throw e;
		} catch (Exception e) {
			// TODO catch TracException
			throw new CoreException(TracCorePlugin.toStatus(e, repository));
		}
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, TaskData data, ITaskMapping initializationData,
			IProgressMonitor monitor) throws CoreException {
		try {
			ITracClient client = connector.getClientManager().getTracClient(repository);
			client.updateAttributes(monitor, false);
			createDefaultAttributes(data, client, false);
			return true;
		} catch (OperationCanceledException e) {
			throw e;
		} catch (Exception e) {
			// TODO catch TracException
			throw new CoreException(TracCorePlugin.toStatus(e, repository));
		}
	}

	@Override
	public boolean initializeSubTaskData(TaskRepository repository, TaskData taskData, TaskData parentTaskData,
			IProgressMonitor monitor) throws CoreException {
		initializeTaskData(repository, taskData, null, monitor);
		TaskAttribute attribute = taskData.getRoot().getMappedAttribute(ATTRIBUTE_BLOCKING);
		if (attribute == null) {
			throw new CoreException(new RepositoryStatus(repository, IStatus.ERROR, TracCorePlugin.ID_PLUGIN,
					RepositoryStatus.ERROR_REPOSITORY, "The repository does not support subtasks"));
		}

		TaskMapper mapper = new TaskMapper(taskData);
		mapper.merge(new TaskMapper(parentTaskData));
		mapper.setDescription("");
		mapper.setSummary("");
		attribute.setValue(parentTaskData.getTaskId());
		return true;
	}

	@Override
	public boolean canInitializeSubTaskData(TaskRepository taskRepository, ITask task) {
		return Boolean.parseBoolean(task.getAttribute(TracRepositoryConnector.TASK_KEY_SUPPORTS_SUBTASKS));
	}

//	/**
//	 * Updates attributes of <code>taskData</code> from <code>ticket</code>.
//	 */
//	public void updateTaskDataFromTicket(TaskData taskData, TracTicket ticket, ITracClient client) {
//		DefaultTaskSchema schema = new DefaultTaskSchema(taskData);
//		if (ticket.getValue(Key.SUMMARY) != null) {
//			schema.setSummary(ticket.getValue(Key.SUMMARY));
//		}
//
//		if (TracRepositoryConnector.isCompleted(ticket.getValue(Key.STATUS))) {
//			schema.setCompletionDate(ticket.getLastChanged());
//		} else {
//			schema.setCompletionDate(null);
//		}
//
//		String priority = ticket.getValue(Key.PRIORITY);
//		TracPriority[] tracPriorities = client.getPriorities();
//		schema.setPriority(TracRepositoryConnector.getTaskPriority(priority, tracPriorities));
//
//		if (ticket.getValue(Key.TYPE) != null) {
//			TaskKind taskKind = TracRepositoryConnector.TaskKind.fromType(ticket.getValue(Key.TYPE));
//			schema.setTaskKind((taskKind != null) ? taskKind.toString() : ticket.getValue(Key.TYPE));
//		}
//
//		if (ticket.getCreated() != null) {
//			schema.setCreationDate(ticket.getCreated());
//		}
//
//		if (ticket.getCustomValue(TracTaskDataHandler.ATTRIBUTE_BLOCKING) != null) {
//			taskData.addAttribute(ATTRIBUTE_BLOCKED_BY, new TaskAttribute(ATTRIBUTE_BLOCKED_BY, "Blocked by", true));
//		}
//	}

	@Override
	public TaskAttributeMapper getAttributeMapper(TaskRepository taskRepository) {
		return new TracAttributeMapper(taskRepository);
	}

	public boolean supportsSubtasks(TaskData taskData) {
		return taskData.getRoot().getAttribute(ATTRIBUTE_BLOCKING) != null;
	}

	public static TracTicket getTracTicket(TaskRepository repository, TaskData data) throws InvalidTicketException,
			CoreException {
		TracTicket ticket = (data.isNew()) ? new TracTicket() : new TracTicket(
				TracRepositoryConnector.getTicketId(data.getTaskId()));

		Collection<TaskAttribute> attributes = data.getRoot().getAttributes().values();
		for (TaskAttribute attribute : attributes) {
			if (TracAttributeMapper.isInternalAttribute(attribute)) {
				// ignore
			} else if (!attribute.getMetaData().isReadOnly()) {
				ticket.putValue(attribute.getId(), attribute.getValue());
			}
		}

		// set cc value
		StringBuilder sb = new StringBuilder();
		List<String> removeValues = TracRepositoryConnector.getAttributeValues(data, TracAttributeMapper.REMOVE_CC);
		List<String> values = TracRepositoryConnector.getAttributeValues(data, TaskAttribute.USER_CC);
		for (String user : values) {
			if (!removeValues.contains(user)) {
				if (sb.length() > 0) {
					sb.append(",");
				}
				sb.append(user);
			}
		}
		if (TracRepositoryConnector.getAttributeValue(data, TracAttributeMapper.NEW_CC).length() > 0) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(TracRepositoryConnector.getAttributeValue(data, TracAttributeMapper.NEW_CC));
		}
		if (Boolean.TRUE.equals(TracRepositoryConnector.getAttributeValue(data, TaskAttribute.ADD_SELF_CC))) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(repository.getUserName());
		}
		ticket.putBuiltinValue(Key.CC, sb.toString());

		ticket.putValue("owner", TracRepositoryConnector.getAttributeValue(data, TaskAttribute.USER_ASSIGNED));

		TaskAttribute operationAttribute = data.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
		if (operationAttribute != null) {
			TaskOperation operation = TaskOperation.createFrom(operationAttribute);
			String action = operation.getOperationId();
			if (!"leave".equals(action)) {
				if ("accept".equals(action)) {
					ticket.putValue("status", TracRepositoryConnector.TaskStatus.ASSIGNED.toStatusString());
				} else if ("resolve".equals(action)) {
					ticket.putValue("status", TracRepositoryConnector.TaskStatus.CLOSED.toStatusString());
					ticket.putValue("resolution", TracRepositoryConnector.getAttributeValue(data,
							TaskAttribute.RESOLUTION));
				} else if ("reopen".equals(action)) {
					ticket.putValue("status", TracRepositoryConnector.TaskStatus.REOPENED.toStatusString());
					ticket.putValue("resolution", "");
				} else if ("reassign".equals(action)) {
					ticket.putValue("status", TracRepositoryConnector.TaskStatus.NEW.toStatusString());
				}
			}
		}

		return ticket;
	}

	@Override
	public void migrateTaskData(TaskRepository taskRepository, TaskData taskData) {
		int version = 0;
		try {
			version = Integer.parseInt(taskData.getVersion());
		} catch (NumberFormatException e) {
			// ignore
		}

		if (version < 1) {
			Map<String, TaskAttribute> attributes = taskData.getRoot().getAttributes();
			for (TaskAttribute attribute : attributes.values()) {
				if (TaskAttribute.OPERATION.equals(attribute.getId())) {
					attribute.getMetaData().setType(TaskAttribute.TYPE_OPERATION);
				} else if (TracAttributeMapper.NEW_CC.equals(attribute.getId())) {
					attribute.getMetaData().setType(TaskAttribute.TYPE_SHORT_TEXT).setReadOnly(false);
				} else {
					TracAttribute tracAttribute = TracAttribute.getByTracKey(attribute.getId());
					if (tracAttribute != null) {
						attribute.getMetaData().setType(tracAttribute.getType());
						attribute.getMetaData().setKind(tracAttribute.getKind());
						attribute.getMetaData().setReadOnly(tracAttribute.isReadOnly());
					}
				}
			}
		}

		taskData.setVersion("1");
	}

}
