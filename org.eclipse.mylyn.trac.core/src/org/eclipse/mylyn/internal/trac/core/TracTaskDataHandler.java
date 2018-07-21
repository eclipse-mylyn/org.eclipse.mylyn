/*******************************************************************************
 * Copyright (c) 2006, 2012 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *     Benjamin Muskalla - bug 386920
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.InvalidTicketException;
import org.eclipse.mylyn.internal.trac.core.model.TracAction;
import org.eclipse.mylyn.internal.trac.core.model.TracAttachment;
import org.eclipse.mylyn.internal.trac.core.model.TracComment;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketField;
import org.eclipse.mylyn.internal.trac.core.util.TracUtil;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.data.TaskOperation;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 * @author Benjamin Muskalla
 */
public class TracTaskDataHandler extends AbstractTaskDataHandler {

	private static final String TASK_TYPE_TASK = "task"; //$NON-NLS-1$

	public static final String TASK_TYPE_STORY = "story"; //$NON-NLS-1$

	private static final String TASK_DATA_VERSION = "2"; //$NON-NLS-1$

	public static final String ATTRIBUTE_BLOCKED_BY = "blockedby"; //$NON-NLS-1$

	public static final String ATTRIBUTE_BLOCKING = "blocking"; //$NON-NLS-1$

	private static final String CC_DELIMETER = ", "; //$NON-NLS-1$

	private static final String TRAC_KEY = "tracKey"; //$NON-NLS-1$

	private final TracRepositoryConnector connector;

	public TracTaskDataHandler(TracRepositoryConnector connector) {
		this.connector = connector;
	}

	public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		monitor = Policy.monitorFor(monitor);
		try {
			monitor.beginTask("Task Download", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
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
				repository.getRepositoryUrl(), ticket.getId() + ""); //$NON-NLS-1$
		taskData.setVersion(TASK_DATA_VERSION);
		try {
			if (!TracRepositoryConnector.hasRichEditor(repository)) {
				createDefaultAttributes(taskData, client, true);
				Set<TaskAttribute> changedAttributes = updateTaskData(repository, taskData, ticket);
				// remove attributes that were not set, i.e. were not received from the server
				List<TaskAttribute> attributes = new ArrayList<TaskAttribute>(taskData.getRoot()
						.getAttributes()
						.values());
				for (TaskAttribute attribute : attributes) {
					if (!changedAttributes.contains(attribute) && !TracAttributeMapper.isInternalAttribute(attribute)) {
						taskData.getRoot().removeAttribute(attribute.getId());
					}
				}
				taskData.setPartial(true);
			} else {
				createDefaultAttributes(taskData, client, true);
				updateTaskData(repository, taskData, ticket);
			}
			removeEmptySingleSelectAttributes(taskData);
			return taskData;
		} catch (OperationCanceledException e) {
			throw e;
		} catch (Exception e) {
			// TODO catch TracException
			throw new CoreException(TracCorePlugin.toStatus(e, repository));
		}
	}

	private void removeEmptySingleSelectAttributes(TaskData taskData) {
		List<TaskAttribute> attributes = new ArrayList<TaskAttribute>(taskData.getRoot().getAttributes().values());
		for (TaskAttribute attribute : attributes) {
			if (TaskAttribute.TYPE_SINGLE_SELECT.equals(attribute.getMetaData().getType())
					&& attribute.getValue().length() == 0 && attribute.getOptions().isEmpty()) {
				taskData.getRoot().removeAttribute(attribute.getId());
			}
		}
	}

	public static Set<TaskAttribute> updateTaskData(TaskRepository repository, TaskData data, TracTicket ticket) {
		Set<TaskAttribute> changedAttributes = new HashSet<TaskAttribute>();

		Date lastChanged = ticket.getLastChanged();
		if (lastChanged != null) {
			TaskAttribute taskAttribute = data.getRoot().getAttribute(TracAttribute.CHANGE_TIME.getTracKey());
			taskAttribute.setValue(TracUtil.toTracTime(lastChanged) + ""); //$NON-NLS-1$
			changedAttributes.add(taskAttribute);
		}

		if (ticket.getCreated() != null) {
			TaskAttribute taskAttribute = data.getRoot().getAttribute(TracAttribute.TIME.getTracKey());
			taskAttribute.setValue(TracUtil.toTracTime(ticket.getCreated()) + ""); //$NON-NLS-1$
			changedAttributes.add(taskAttribute);
		}

		Map<String, String> valueByKey = ticket.getValues();
		for (String key : valueByKey.keySet()) {
			TaskAttribute taskAttribute = data.getRoot().getAttribute(key);
			if (taskAttribute != null) {
				if (Key.CC.getKey().equals(key)) {
					StringTokenizer t = new StringTokenizer(valueByKey.get(key), CC_DELIMETER);
					while (t.hasMoreTokens()) {
						taskAttribute.addValue(t.nextToken());
					}
				} else {
					taskAttribute.setValue(valueByKey.get(key));
				}
				changedAttributes.add(taskAttribute);
			} else {
				// TODO log missing attribute?
			}
		}

		TracComment[] comments = ticket.getComments();
		if (comments != null) {
			int count = 1;
			for (int i = 0; i < comments.length; i++) {
				if (!"comment".equals(comments[i].getField()) || "".equals(comments[i].getNewValue())) { //$NON-NLS-1$ //$NON-NLS-2$
					continue;
				}

				TaskCommentMapper mapper = new TaskCommentMapper();
				mapper.setAuthor(repository.createPerson(comments[i].getAuthor()));
				mapper.setCreationDate(comments[i].getCreated());
				mapper.setText(comments[i].getNewValue());
				// TODO mapper.setUrl();
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
				mapper.setUrl(repository.getRepositoryUrl() + ITracClient.TICKET_ATTACHMENT_URL + ticket.getId() + "/" //$NON-NLS-1$
						+ TracUtil.encodeUrl(attachments[i].getFilename()));
				mapper.setAttachmentId(i + ""); //$NON-NLS-1$

				TaskAttribute attribute = data.getRoot().createAttribute(TaskAttribute.PREFIX_ATTACHMENT + (i + 1));
				mapper.applyTo(attribute);
			}
		}

		TracAction[] actions = ticket.getActions();
		if (actions != null) {
			// add actions and set first as default
			for (TracAction action : actions) {
				addOperation(repository, data, ticket, action, action == actions[0]);
			}
		}

		return changedAttributes;
	}

	private static void addOperation(TaskRepository repository, TaskData data, TracTicket ticket, TracAction action,
			boolean setAsDefault) {
		String label = action.getLabel();
		if (label == null) {
			if ("leave".equals(action.getId())) { //$NON-NLS-1$
				String status = ticket.getValue(Key.STATUS);
				if (status != null) {
					String resolution = ticket.getValue(Key.RESOLUTION);
					if (resolution != null) {
						label = NLS.bind(Messages.TracTaskDataHandler_Leave_as_Status_Resolution, status, resolution);
					} else {
						label = NLS.bind(Messages.TracTaskDataHandler_Leave_as_Status, status);
					}
				} else {
					label = Messages.TracTaskDataHandler_Leave;
				}
			} else if ("accept".equals(action.getId())) { //$NON-NLS-1$
				label = Messages.TracTaskDataHandler_Accept;
			} else if ("resolve".equals(action.getId())) { //$NON-NLS-1$
				label = Messages.TracTaskDataHandler_Resolve_as;
			} else if ("reopen".equals(action.getId())) { //$NON-NLS-1$
				label = Messages.TracTaskDataHandler_Reopen;
			} else if ("reassign".equals(action.getId())) { //$NON-NLS-1$
				// do not add reassign for Trac 0.10 since the assigned to field is editable  
			} else {
				label = action.getId();
			}
		}

		if (label != null) {
			TaskAttribute attribute = data.getRoot().createAttribute(TaskAttribute.PREFIX_OPERATION + action.getId());
			TaskOperation.applyTo(attribute, action.getId(), label);
			if (!action.getFields().isEmpty()) {
				// TODO support more than one field
				TracTicketField field = action.getFields().get(0);
				TaskAttribute fieldAttribute = createAttribute(data, field);
				fieldAttribute.getMetaData().setKind(null);
				attribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, fieldAttribute.getId());
			} else if ("resolve".equals(action.getId())) { //$NON-NLS-1$
				attribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID,
						TracAttribute.RESOLUTION.getTracKey());
			}

			if (setAsDefault) {
				TaskAttribute operationAttribute = data.getRoot().createAttribute(TaskAttribute.OPERATION);
				TaskOperation.applyTo(operationAttribute, action.getId(), label);
			}
		}
	}

	public static void createDefaultAttributes(TaskData data, ITracClient client, boolean existingTask) {
		data.setVersion(TASK_DATA_VERSION);

		createAttribute(data, client, TracAttribute.SUMMARY);
		createAttribute(data, client, TracAttribute.DESCRIPTION);
		if (existingTask) {
			createAttribute(data, client, TracAttribute.TIME);
			createAttribute(data, client, TracAttribute.CHANGE_TIME);
			createAttribute(data, client, TracAttribute.STATUS);
			TaskAttribute attribute = createAttribute(data, client, TracAttribute.RESOLUTION);
			// reset default value to avoid "fixed" resolution on tasks created through web 
			attribute.setValue(""); //$NON-NLS-1$
			// internal attributes
			createAttribute(data, null, TracAttribute.TOKEN);
		}
		createAttribute(data, client, TracAttribute.COMPONENT);
		createAttribute(data, client, TracAttribute.VERSION);
		createAttribute(data, client, TracAttribute.PRIORITY);
		createAttribute(data, client, TracAttribute.SEVERITY);
		createAttribute(data, client, TracAttribute.MILESTONE);
		createAttribute(data, client, TracAttribute.TYPE);
		createAttribute(data, client, TracAttribute.KEYWORDS);
		// custom fields
		TracTicketField[] fields = client.getTicketFields();
		if (fields != null) {
			for (TracTicketField field : fields) {
				if (field.isCustom()) {
					createAttribute(data, field);
				}
			}
		}
		// people
		createAttribute(data, client, TracAttribute.OWNER);
		if (existingTask) {
			createAttribute(data, client, TracAttribute.REPORTER);
		}
		createAttribute(data, client, TracAttribute.CC);
		if (existingTask) {
			data.getRoot()
					.createAttribute(TracAttributeMapper.NEW_CC)
					.getMetaData()
					.setType(TaskAttribute.TYPE_SHORT_TEXT)
					.setReadOnly(false);
			data.getRoot().createAttribute(TracAttributeMapper.REMOVE_CC);
			data.getRoot()
					.createAttribute(TaskAttribute.COMMENT_NEW)
					.getMetaData()
					.setType(TaskAttribute.TYPE_LONG_RICH_TEXT)
					.setReadOnly(false);
		}
		// operations
		data.getRoot().createAttribute(TaskAttribute.OPERATION).getMetaData().setType(TaskAttribute.TYPE_OPERATION);
	}

	private static TaskAttribute createAttribute(TaskData data, TracTicketField field) {
		TaskAttribute attr = data.getRoot().createAttribute(field.getName());
		TaskAttributeMetaData metaData = attr.getMetaData();
		metaData.defaults();
		metaData.setLabel(field.getLabel() + ":"); //$NON-NLS-1$
		metaData.setKind(TaskAttribute.KIND_DEFAULT);
		metaData.setReadOnly(false);
		metaData.putValue(TRAC_KEY, field.getName());
		if (field.getType() == TracTicketField.Type.CHECKBOX) {
			// attr.addOption("True", "1");
			// attr.addOption("False", "0");
			metaData.setType(TaskAttribute.TYPE_BOOLEAN);
			attr.putOption("1", "1"); //$NON-NLS-1$ //$NON-NLS-2$
			attr.putOption("0", "0"); //$NON-NLS-1$ //$NON-NLS-2$
			if (field.getDefaultValue() != null) {
				attr.setValue(field.getDefaultValue());
			}
		} else if (field.getType() == TracTicketField.Type.SELECT || field.getType() == TracTicketField.Type.RADIO) {
			metaData.setType(TaskAttribute.TYPE_SINGLE_SELECT);
			String[] values = field.getOptions();
			if (values != null && values.length > 0) {
				if (field.isOptional()) {
					attr.putOption("", ""); //$NON-NLS-1$ //$NON-NLS-2$
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
		} else if (field.getType() == TracTicketField.Type.TEXTAREA) {
			metaData.setType(TaskAttribute.TYPE_LONG_TEXT);
			if (field.getDefaultValue() != null) {
				attr.setValue(field.getDefaultValue());
			}
		} else {
			metaData.setType(TaskAttribute.TYPE_SHORT_TEXT);
			if (field.getDefaultValue() != null) {
				attr.setValue(field.getDefaultValue());
			}
		}
		if (ATTRIBUTE_BLOCKED_BY.equals(field.getName()) || ATTRIBUTE_BLOCKING.equals(field.getName())) {
			metaData.setType(TaskAttribute.TYPE_TASK_DEPENDENCY);
		}
		return attr;
	}

	public static TaskAttribute createAttribute(TaskData data, TracAttribute tracAttribute) {
		return createAttribute(data, null, tracAttribute);
	}

	public static TaskAttribute createAttribute(TaskData data, ITracClient client, TracAttribute tracAttribute) {
		TaskAttribute attr = data.getRoot().createAttribute(tracAttribute.getTracKey());
		TaskAttributeMetaData metaData = attr.getMetaData();
		metaData.setType(tracAttribute.getType());
		metaData.setKind(tracAttribute.getKind());
		metaData.setLabel(tracAttribute.toString());
		metaData.setReadOnly(tracAttribute.isReadOnly());
		metaData.putValue(TRAC_KEY, tracAttribute.getTracKey());
		if (client != null) {
			TracTicketField field = client.getTicketFieldByName(tracAttribute.getTracKey());
			Map<String, String> values = TracAttributeMapper.getRepositoryOptions(client, attr.getId());
			if (values != null && values.size() > 0) {
				boolean setDefault = field == null || !field.isOptional();
				for (Entry<String, String> value : values.entrySet()) {
					attr.putOption(value.getKey(), value.getValue());
					// set first value as default, may get overwritten below
					if (setDefault) {
						attr.setValue(value.getKey());
					}
					setDefault = false;
				}
			} else if (TaskAttribute.TYPE_SINGLE_SELECT.equals(tracAttribute.getType())) {
				attr.getMetaData().setReadOnly(true);
			}
			if (field != null) {
				String defaultValue = field.getDefaultValue();
				if (defaultValue != null && defaultValue.length() > 0) {
					attr.setValue(defaultValue);
				}
			}
		}
		return attr;
	}

	@Override
	public RepositoryResponse postTaskData(TaskRepository repository, TaskData taskData,
			Set<TaskAttribute> oldAttributes, IProgressMonitor monitor) throws CoreException {
		try {
			TracTicket ticket = TracTaskDataHandler.getTracTicket(repository, taskData);
			ITracClient server = connector.getClientManager().getTracClient(repository);
			if (taskData.isNew()) {
				int id = server.createTicket(ticket, monitor);
				return new RepositoryResponse(ResponseKind.TASK_CREATED, id + ""); //$NON-NLS-1$
			} else {
				String newComment = ""; //$NON-NLS-1$
				TaskAttribute newCommentAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.COMMENT_NEW);
				if (newCommentAttribute != null) {
					newComment = newCommentAttribute.getValue();
				}
				server.updateTicket(ticket, newComment, monitor);
				return new RepositoryResponse(ResponseKind.TASK_UPDATED, ticket.getId() + ""); //$NON-NLS-1$
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
		monitor = Policy.monitorFor(monitor);
		try {
			ITracClient client = connector.getClientManager().getTracClient(repository);
			client.updateAttributes(monitor, false);
			createDefaultAttributes(data, client, false);
			removeEmptySingleSelectAttributes(data);
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
		TaskAttribute blockingAttribute = taskData.getRoot().getMappedAttribute(ATTRIBUTE_BLOCKING);
		if (blockingAttribute == null) {
			throw new CoreException(new RepositoryStatus(repository, IStatus.ERROR, TracCorePlugin.ID_PLUGIN,
					RepositoryStatus.ERROR_REPOSITORY, "The repository does not support subtasks")); //$NON-NLS-1$
		}

		TaskMapper mapper = new TaskMapper(taskData);
		mapper.merge(new TaskMapper(parentTaskData));
		mapper.setDescription(""); //$NON-NLS-1$
		mapper.setSummary(""); //$NON-NLS-1$
		blockingAttribute.setValue(parentTaskData.getTaskId());
		TaskAttribute blockedByAttribute = taskData.getRoot().getMappedAttribute(ATTRIBUTE_BLOCKED_BY);
		if (blockedByAttribute != null) {
			blockedByAttribute.clearValues();
		}
		// special handling for stories which should have tasks as subtasks
		TaskAttribute typeAttribute = taskData.getRoot().getAttribute(TracAttribute.TYPE.getTracKey());
		if (typeAttribute != null && TASK_TYPE_STORY.equals(typeAttribute.getValue())) {
			if (typeAttribute.getOptions().containsKey(TASK_TYPE_TASK)) {
				typeAttribute.setValue(TASK_TYPE_TASK);
			}
		}
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
	public TaskAttributeMapper getAttributeMapper(TaskRepository repository) {
		ITracClient client = connector.getClientManager().getTracClient(repository);
		return new TracAttributeMapper(repository, client);
	}

	public boolean supportsSubtasks(TaskData taskData) {
		return taskData.getRoot().getAttribute(ATTRIBUTE_BLOCKED_BY) != null;
	}

	public static TracTicket getTracTicket(TaskRepository repository, TaskData data) throws InvalidTicketException,
			CoreException {
		TracTicket ticket = (data.isNew()) ? new TracTicket() : new TracTicket(
				TracRepositoryConnector.getTicketId(data.getTaskId()));

		Collection<TaskAttribute> attributes = data.getRoot().getAttributes().values();
		for (TaskAttribute attribute : attributes) {
			if (TracAttributeMapper.isInternalAttribute(attribute)
					|| TracAttribute.RESOLUTION.getTracKey().equals(attribute.getId())) {
				// ignore internal attributes, resolution is set through operations
			} else if (!attribute.getMetaData().isReadOnly() || Key.TOKEN.getKey().equals(attribute.getId())) {
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
					sb.append(","); //$NON-NLS-1$
				}
				sb.append(user);
			}
		}
		if (TracRepositoryConnector.getAttributeValue(data, TracAttributeMapper.NEW_CC).length() > 0) {
			if (sb.length() > 0) {
				sb.append(","); //$NON-NLS-1$
			}
			sb.append(TracRepositoryConnector.getAttributeValue(data, TracAttributeMapper.NEW_CC));
		}
		if (Boolean.TRUE.equals(TracRepositoryConnector.getAttributeValue(data, TaskAttribute.ADD_SELF_CC))) {
			if (sb.length() > 0) {
				sb.append(","); //$NON-NLS-1$
			}
			sb.append(repository.getUserName());
		}
		ticket.putBuiltinValue(Key.CC, sb.toString());

		ticket.putValue("owner", TracRepositoryConnector.getAttributeValue(data, TaskAttribute.USER_ASSIGNED)); //$NON-NLS-1$

		TaskAttribute operationAttribute = data.getRoot().getMappedAttribute(TaskAttribute.OPERATION);
		if (operationAttribute != null) {
			TaskOperation operation = TaskOperation.createFrom(operationAttribute);
			String action = operation.getOperationId();
			if (!"leave".equals(action)) { //$NON-NLS-1$
				if ("accept".equals(action)) { //$NON-NLS-1$
					ticket.putValue("status", TracRepositoryConnector.TaskStatus.ASSIGNED.toStatusString()); //$NON-NLS-1$
				} else if ("resolve".equals(action)) { //$NON-NLS-1$
					ticket.putValue("status", TracRepositoryConnector.TaskStatus.CLOSED.toStatusString()); //$NON-NLS-1$
					ticket.putValue("resolution", TracRepositoryConnector.getAttributeValue(data, //$NON-NLS-1$
							TaskAttribute.RESOLUTION));
				} else if ("reopen".equals(action)) { //$NON-NLS-1$
					ticket.putValue("status", TracRepositoryConnector.TaskStatus.REOPENED.toStatusString()); //$NON-NLS-1$
					ticket.putValue("resolution", ""); //$NON-NLS-1$ //$NON-NLS-2$
				} else if ("reassign".equals(action)) { //$NON-NLS-1$
					ticket.putValue("status", TracRepositoryConnector.TaskStatus.NEW.toStatusString()); //$NON-NLS-1$
				}
			}
			ticket.putValue("action", action); //$NON-NLS-1$
		}

		Date lastChanged = TracUtil.parseDate(TracRepositoryConnector.getAttributeValue(data,
				TracAttribute.CHANGE_TIME.getTracKey()));
		ticket.setLastChanged(lastChanged);

		return ticket;
	}

	@Override
	public void migrateTaskData(TaskRepository taskRepository, TaskData taskData) {
		int version = 0;
		if (taskData.getVersion() != null) {
			try {
				version = Integer.parseInt(taskData.getVersion());
			} catch (NumberFormatException e) {
				// ignore
			}
		}

		if (version < 1) {
			TaskAttribute root = taskData.getRoot();
			List<TaskAttribute> attributes = new ArrayList<TaskAttribute>(root.getAttributes().values());
			for (TaskAttribute attribute : attributes) {
				if (TaskAttribute.TYPE_OPERATION.equals(attribute.getMetaData().getType())
						&& "reassign".equals(attribute.getValue())) { //$NON-NLS-1$
					root.removeAttribute(attribute.getId());
				} else if (TaskAttribute.OPERATION.equals(attribute.getId())) {
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
			if (root.getAttribute(TracAttributeMapper.REMOVE_CC) == null) {
				root.createAttribute(TracAttributeMapper.REMOVE_CC);
			}
			if (root.getAttribute(TaskAttribute.COMMENT_NEW) == null) {
				root.createAttribute(TaskAttribute.COMMENT_NEW)
						.getMetaData()
						.setType(TaskAttribute.TYPE_LONG_RICH_TEXT)
						.setReadOnly(false);
			}
		}
		if (version < 2) {
			List<TaskAttribute> attributes = new ArrayList<TaskAttribute>(taskData.getRoot().getAttributes().values());
			for (TaskAttribute attribute : attributes) {
				if (!TracAttributeMapper.isInternalAttribute(attribute)) {
					TaskAttributeMetaData metaData = attribute.getMetaData();
					metaData.putValue(TRAC_KEY, attribute.getId());
					if (metaData.getType() == null) {
						metaData.setType(TaskAttribute.TYPE_SHORT_TEXT);
					}
				}
			}
			taskData.setVersion(TASK_DATA_VERSION);
		}
	}

}
