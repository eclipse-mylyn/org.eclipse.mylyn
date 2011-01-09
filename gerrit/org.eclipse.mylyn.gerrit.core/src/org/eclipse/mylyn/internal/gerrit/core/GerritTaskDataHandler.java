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
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.data.TaskData;

import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.ChangeInfo;
import com.google.gerrit.reviewdb.Change;

/**
 * @author Mikael Kober
 * @author Thomas Westling
 * @author Steffen Pingel
 */
public class GerritTaskDataHandler extends AbstractTaskDataHandler {

	public static TaskAttribute createAttribute(TaskData data, GerritAttribute gerritAttribute) {
		TaskAttribute attr = data.getRoot().createAttribute(gerritAttribute.getGerritKey());
		TaskAttributeMetaData metaData = attr.getMetaData();
		metaData.setType(gerritAttribute.getType());
		metaData.setKind(gerritAttribute.getKind());
		metaData.setLabel(gerritAttribute.toString());
		metaData.setReadOnly(gerritAttribute.isReadOnly());
		return attr;
	}

	public static void createDefaultAttributes(TaskData data) {
		createAttribute(data, GerritAttribute.ID);
		createAttribute(data, GerritAttribute.OWNER);
		createAttribute(data, GerritAttribute.PROJECT);
		createAttribute(data, GerritAttribute.SUMMARY);
		createAttribute(data, GerritAttribute.STATUS);
		createAttribute(data, GerritAttribute.URL);
		createAttribute(data, GerritAttribute.UPDATED);
		createAttribute(data, GerritAttribute.UPLOADED);
		createAttribute(data, GerritAttribute.DESCRIPTION);
	}

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
		createDefaultAttributes(data);
		return data;
	}

	@Override
	public TaskAttributeMapper getAttributeMapper(TaskRepository repository) {
		return new GerritTaskAttributeMapper(repository);
	}

	/**
	 * Retrieves task data for the given review from repository.
	 */
	public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		try {
			ChangeDetail changeDetail = connector.getClient(repository).getReview(taskId, monitor);
			TaskData taskData = createTaskData(repository, taskId, monitor);
			updateTaskData(taskData, changeDetail);
			return taskData;
		} catch (GerritException e) {
			throw connector.toCoreException(repository, e);
		}
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, TaskData taskData, ITaskMapping initializationData,
			IProgressMonitor monitor) throws CoreException {
		createDefaultAttributes(taskData);
		return true;
	}

	@Override
	public RepositoryResponse postTaskData(TaskRepository repository, TaskData taskData,
			Set<TaskAttribute> oldAttributes, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	public void updateTaskData(TaskData data, ChangeDetail changeDetail) {
		Change change = changeDetail.getChange();

		setAttributeValue(data, GerritAttribute.ID, change.getChangeId() + ""); //$NON-NLS-1$
		setAttributeValue(data, GerritAttribute.OWNER, change.getOwner().toString());
		setAttributeValue(data, GerritAttribute.PROJECT, change.getProject().get());
		setAttributeValue(data, GerritAttribute.SUMMARY, change.getSubject());
		setAttributeValue(data, GerritAttribute.STATUS, change.getStatus().toString());
		//setAttributeValue(data, GerritAttribute.URL, change.getUrl());
		setAttributeValue(data, GerritAttribute.UPDATED, dateToString(change.getLastUpdatedOn()));
		setAttributeValue(data, GerritAttribute.UPLOADED, dateToString(change.getCreatedOn()));
		setAttributeValue(data, GerritAttribute.DESCRIPTION, changeDetail.getDescription());
	}

	public void updateTaskData(TaskData data, ChangeInfo changeInfo) {
		setAttributeValue(data, GerritAttribute.ID, changeInfo.getId() + ""); //$NON-NLS-1$
		setAttributeValue(data, GerritAttribute.OWNER, changeInfo.getOwner().toString());
		setAttributeValue(data, GerritAttribute.PROJECT, changeInfo.getProject().getName());
		setAttributeValue(data, GerritAttribute.SUMMARY, changeInfo.getSubject());
		setAttributeValue(data, GerritAttribute.STATUS, changeInfo.getStatus().toString());
		//setAttributeValue(data, GerritAttribute.URL, change.getUrl());
		setAttributeValue(data, GerritAttribute.UPDATED, dateToString(changeInfo.getLastUpdatedOn()));
	}

	/**
	 * Convenience method to set the value of a given Attribute in the given {@link TaskData}.
	 */
	private TaskAttribute setAttributeValue(TaskData data, GerritAttribute gerritAttribut, String value) {
		TaskAttribute attribute = data.getRoot().getAttribute(gerritAttribut.getGerritKey());
		if (value != null) {
			attribute.setValue(value);
		}
		return attribute;
	}

}
