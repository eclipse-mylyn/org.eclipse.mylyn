/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core;

import java.util.Date;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;


/**
 * Handler for task data from gerrit.
 * @author Mikael Kober, Sony Ericsson
 * @author Tomas Westling, Sony Ericsson -
 *         thomas.westling@sonyericsson.com
 */
public class GerritTaskDataHandler extends AbstractTaskDataHandler {
    private GerritClient gerritClient = null;

	
	/**
	 * Constructor.
	 * @param connector
	 */
	public GerritTaskDataHandler() { //GerritConnector connector
		//this.connector = connector;
//	    gerritClient = new GerritClient();
	}

	
	/**
	 * Get task data for the given task id from the given repository.
	 * @param repository 
	 * @param taskId
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	public TaskData getTaskData(TaskRepository repository, String taskId,
			IProgressMonitor monitor) throws CoreException {
	    gerritClient = GerritClient.getGerritClient(repository);
		GerritTask gerritTask = gerritClient.getTaskData(repository, taskId, monitor);
		return createTaskDataFromGerritTask(repository, taskId, gerritTask, monitor);
	}


	private TaskData createTaskDataFromGerritTask(TaskRepository repository, String taskId,
			GerritTask gerritTask, IProgressMonitor monitor) throws CoreException {
		TaskData taskData = createTaskData(repository, taskId, monitor);
		updateTaskData(taskData, gerritTask);
		return taskData;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler#getAttributeMapper(org.eclipse.mylyn.tasks.core.TaskRepository)
	 */
	@Override
	public TaskAttributeMapper getAttributeMapper(TaskRepository repository) {
		return new GerritTaskAttributeMapper(repository);
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler#initializeTaskData(org.eclipse.mylyn.tasks.core.TaskRepository, org.eclipse.mylyn.tasks.core.data.TaskData, org.eclipse.mylyn.tasks.core.ITaskMapping, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public boolean initializeTaskData(TaskRepository repository, TaskData taskData, ITaskMapping initializationData,
			IProgressMonitor monitor) throws CoreException {
		createDefaultAttributes(taskData);
		return true;
	}

	@Override
	public RepositoryResponse postTaskData(TaskRepository arg0, TaskData arg1,
			Set<TaskAttribute> arg2, IProgressMonitor arg3)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Update taskdata with the given gerrit-task.
	 * @param data taskdata to update
	 * @param gerritTask gerrit task containing the data
	 */
	public void updateTaskData(TaskData data, GerritTask gerritTask) {
		setAttributeValue(data, GerritAttribute.ID, gerritTask.getId());
		setAttributeValue(data, GerritAttribute.OWNER, gerritTask.getOwner());
		setAttributeValue(data, GerritAttribute.PROJECT, gerritTask.getProject());
		setAttributeValue(data, GerritAttribute.SUMMARY, gerritTask.getTitle());
		setAttributeValue(data, GerritAttribute.STATUS, gerritTask.getStatus());
		setAttributeValue(data, GerritAttribute.URL, gerritTask.getUrl());
		setAttributeValue(data, GerritAttribute.UPDATED, dateToString(gerritTask.getUpdated()));
		setAttributeValue(data, GerritAttribute.UPLOADED, dateToString(gerritTask.getUploaded()));
		setAttributeValue(data, GerritAttribute.DESCRIPTION, gerritTask.getDescription());
	}


	/**
	 * Create TaskAttribute for the given taskdata and GerritAttribute.
	 * @param data
	 * @param gerritAttribute
	 * @return
	 */
	public static TaskAttribute createAttribute(TaskData data, GerritAttribute gerritAttribute) {
		TaskAttribute attr = data.getRoot().createAttribute(gerritAttribute.getGerritKey());
		TaskAttributeMetaData metaData = attr.getMetaData();
		metaData.setType(gerritAttribute.getType());
		metaData.setKind(gerritAttribute.getKind());
		metaData.setLabel(gerritAttribute.toString());
		metaData.setReadOnly(gerritAttribute.isReadOnly());
		return attr;
	}
	
	/**
	 * Create all default attributes for the given taskdata.
	 * @param data
	 */
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
	
	/**
	 * Create task data.
	 * @param repository current repository
	 * @param taskId task id
	 * @param monitor progress monitor.
	 * @return initialized taskdata
	 * @throws CoreException
	 */
	public TaskData createTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor) throws CoreException {
		TaskData data = new TaskData(getAttributeMapper(repository), GerritConnector.CONNECTOR_KIND,
				repository.getRepositoryUrl(), taskId);
		initializeTaskData(repository, data, new TaskMapper(data), monitor);
		return data;
	}
	
	/**
	 * Convenience method to set the value of a given Attribute in the given taskdata.
	 * @param data
	 * @param gerritAttribut
	 * @param value
	 * @return
	 */
	private TaskAttribute setAttributeValue(TaskData data, GerritAttribute gerritAttribut, String value) {
		TaskAttribute attribute = data.getRoot().getAttribute(gerritAttribut.getGerritKey());
		if (value != null) {
			attribute.setValue(value);
		}
		return attribute;
	}

	/**
	 * Helper method.
	 * @param date date to convert to String
	 * @return
	 */
	public static String dateToString(Date date) {
		if (date == null) {
			return ""; //$NON-NLS-1$
		} else {
			return date.getTime() + ""; //$NON-NLS-1$
		}
	}
}
