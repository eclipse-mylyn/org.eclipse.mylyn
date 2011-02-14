/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Tasktop Technologies - improvements
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core;

import java.util.Date;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.tasks.core.data.AbstractTaskSchema.Field;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

import com.google.gerrit.common.data.AccountInfo;
import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.ChangeInfo;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.ChangeMessage;

/**
 * @author Mikael Kober
 * @author Thomas Westling
 * @author Steffen Pingel
 */
public class GerritTaskDataHandler extends AbstractTaskDataHandler {

	public static String dateToString(Date date) {
		if (date == null) {
			return ""; //$NON-NLS-1$
		} else {
			return date.getTime() + ""; //$NON-NLS-1$
		}
	}

	private final GerritConnector connector;

	public GerritTaskDataHandler(GerritConnector connector) {
		this.connector = connector;
	}

	public TaskData createTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor) {
		TaskData data = new TaskData(getAttributeMapper(repository), GerritConnector.CONNECTOR_KIND,
				repository.getRepositoryUrl(), taskId);
		initializeTaskData(repository, data, null, monitor);
		return data;
	}

	@Override
	public TaskAttributeMapper getAttributeMapper(TaskRepository repository) {
		return new TaskAttributeMapper(repository);
	}

	/**
	 * Retrieves task data for the given review from repository.
	 */
	public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		try {
			GerritClient client = connector.getClient(repository);
			ChangeDetail changeDetail = client.getChangeDetail(client.id(taskId), monitor);
			TaskData taskData = createTaskData(repository, taskId, monitor);
			updateTaskData(repository, taskData, changeDetail);
			return taskData;
		} catch (GerritException e) {
			throw connector.toCoreException(repository, e);
		}
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, TaskData taskData, ITaskMapping initializationData,
			IProgressMonitor monitor) {
		GerritTaskSchema.getDefault().initialize(taskData);
		return true;
	}

	@Override
	public RepositoryResponse postTaskData(TaskRepository repository, TaskData taskData,
			Set<TaskAttribute> oldAttributes, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void updateTaskData(TaskRepository repository, TaskData data, ChangeDetail changeDetail) {
		GerritTaskSchema schema = GerritTaskSchema.getDefault();
		Change change = changeDetail.getChange();
		AccountInfo owner = changeDetail.getAccounts().get(change.getOwner());
		setAttributeValue(data, schema.KEY, change.getId().toString());
		//setAttributeValue(data, schema.KEY, change.getKey().abbreviate());
		setAttributeValue(data, schema.CHANGE_ID, change.getKey().get());
		setAttributeValue(data, schema.BRANCH, change.getDest().get());
		setAttributeValue(data, schema.OWNER, owner.getFullName());
		setAttributeValue(data, schema.PROJECT, change.getProject().get());
		setAttributeValue(data, schema.SUMMARY, change.getSubject());
		setAttributeValue(data, schema.STATUS, change.getStatus().toString());
		//setAttributeValue(data, GerritAttribute.URL, change.getUrl());
		setAttributeValue(data, schema.UPDATED, dateToString(change.getLastUpdatedOn()));
		setAttributeValue(data, schema.UPLOADED, dateToString(change.getCreatedOn()));
		setAttributeValue(data, schema.DESCRIPTION, changeDetail.getDescription());
		setAttributeValue(data, schema.URL, connector.getTaskUrl(repository.getUrl(), data.getTaskId()));
		if (change.getStatus() != null && change.getStatus().isClosed()) {
			setAttributeValue(data, schema.COMPLETED, dateToString(change.getLastUpdatedOn()));
		}
		int i = 1;
		for (ChangeMessage message : changeDetail.getMessages()) {
			TaskCommentMapper mapper = new TaskCommentMapper();
			if (message.getAuthor() != null) {
				AccountInfo author = changeDetail.getAccounts().get(message.getAuthor());
				IRepositoryPerson person = repository.createPerson((author.getPreferredEmail() != null) ? author.getPreferredEmail()
						: author.getId() + ""); //$NON-NLS-1$
				person.setName(author.getFullName());
				mapper.setAuthor(person);
			}
			mapper.setText(message.getMessage());
			mapper.setCreationDate(message.getWrittenOn());
			mapper.setNumber(i);
			TaskAttribute attribute = data.getRoot().createAttribute(TaskAttribute.PREFIX_COMMENT + i);
			mapper.applyTo(attribute);
			i++;
		}
	}

	public void updateTaskData(TaskData data, ChangeInfo changeInfo) {
		GerritTaskSchema schema = GerritTaskSchema.getDefault();
		setAttributeValue(data, schema.KEY, changeInfo.getId() + ""); //$NON-NLS-1$
		setAttributeValue(data, schema.OWNER, changeInfo.getOwner().toString());
		setAttributeValue(data, schema.PROJECT, changeInfo.getProject().getName());
		setAttributeValue(data, schema.SUMMARY, changeInfo.getSubject());
		setAttributeValue(data, schema.STATUS, changeInfo.getStatus().toString());
		//setAttributeValue(data, GerritAttribute.URL, change.getUrl());
		setAttributeValue(data, schema.UPDATED, dateToString(changeInfo.getLastUpdatedOn()));
	}

	/**
	 * Convenience method to set the value of a given Attribute in the given {@link TaskData}.
	 */
	private TaskAttribute setAttributeValue(TaskData data, Field gerritAttribut, String value) {
		TaskAttribute attribute = data.getRoot().getAttribute(gerritAttribut.getKey());
		if (value != null) {
			attribute.setValue(value);
		}
		return attribute;
	}

}
