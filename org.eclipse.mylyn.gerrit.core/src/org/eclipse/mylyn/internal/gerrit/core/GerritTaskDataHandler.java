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
 *      GitHub, Inc. - fixes for bug 354753
 *      Sascha Scholz (SAP) - improvements
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core;

import java.util.Date;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.JSonSupport;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema.Field;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

import com.google.gerrit.common.data.AccountInfo;
import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.ChangeInfo;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.ChangeMessage;

/**
 * @author Mikael Kober
 * @author Thomas Westling
 * @author Steffen Pingel
 * @author Kevin Sawicki
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

	public TaskData createPartialTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor) {
		TaskData data = new TaskData(getAttributeMapper(repository), GerritConnector.CONNECTOR_KIND,
				repository.getRepositoryUrl(), taskId);
		GerritQueryResultSchema.getDefault().initialize(data);
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
			client.refreshConfigOnce(monitor);
			boolean anonymous = client.isAnonymous();
			String id = null;
			if (!anonymous) {
				id = getAccountId(client, repository, monitor);
			}
			GerritChange review = client.getChange(taskId, monitor);
			int reviewId = review.getChangeDetail().getChange().getId().get();
			TaskData taskData = createTaskData(repository, Integer.toString(reviewId), monitor);
			updateTaskData(repository, taskData, review, !anonymous, id);
			return taskData;
		} catch (GerritException e) {
			throw connector.toCoreException(repository, e);
		}
	}

	/**
	 * Get account id for repository
	 * 
	 * @param client
	 * @param repository
	 * @param monitor
	 * @return account id or null if not found
	 * @throws GerritException
	 */
	protected String getAccountId(GerritClient client, TaskRepository repository, IProgressMonitor monitor)
			throws GerritException {
		String id = repository.getProperty(GerritConnector.KEY_REPOSITORY_ACCOUNT_ID);
		if (id == null) {
			Account account = client.getAccount(monitor);
			if (account != null) {
				id = account.getId().toString();
				repository.setProperty(GerritConnector.KEY_REPOSITORY_ACCOUNT_ID, id);
			}
		}
		return id;
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

	public void updateTaskData(TaskRepository repository, TaskData data, GerritChange review, boolean canPublish,
			String accountId) {
		GerritTaskSchema schema = GerritTaskSchema.getDefault();

		ChangeDetail changeDetail = review.getChangeDetail();
		Change change = changeDetail.getChange();
		AccountInfo owner = changeDetail.getAccounts().get(change.getOwner());

		updateTaskData(repository, data, new ChangeInfo(change));
		setAttributeValue(data, schema.BRANCH, change.getDest().get());
		setAttributeValue(data, schema.OWNER, GerritUtil.getUserLabel(owner));
		setAttributeValue(data, schema.UPLOADED, dateToString(change.getCreatedOn()));
		setAttributeValue(data, schema.DESCRIPTION, changeDetail.getDescription());
		int i = 1;
		String accountName = repository.getUserName();
		for (ChangeMessage message : changeDetail.getMessages()) {
			TaskCommentMapper mapper = new TaskCommentMapper();
			if (message.getAuthor() != null) {
				AccountInfo author = changeDetail.getAccounts().get(message.getAuthor());
				String userName;
				String id = author.getId().toString();
				if (id.equals(accountId) && accountName != null) {
					userName = accountName;
				} else {
					String email = author.getPreferredEmail();
					userName = (email != null) ? email : id;
				}
				IRepositoryPerson person = repository.createPerson(userName);
				person.setName(author.getFullName());
				mapper.setAuthor(person);
			} else {
				// messages without an author are from Gerrit itself
				IRepositoryPerson person = repository.createPerson("Gerrit Code Review");
				mapper.setAuthor(person);
			}
			mapper.setText(message.getMessage());
			mapper.setCreationDate(message.getWrittenOn());
			mapper.setNumber(i);
			TaskAttribute attribute = data.getRoot().createAttribute(TaskAttribute.PREFIX_COMMENT + i);
			mapper.applyTo(attribute);
			i++;
		}

		JSonSupport json = new JSonSupport();
		setAttributeValue(data, schema.OBJ_REVIEW, json.getGson().toJson(review));
		setAttributeValue(data, schema.CAN_PUBLISH, Boolean.toString(canPublish));
	}

	public void updateTaskData(TaskRepository repository, TaskData data, ChangeInfo changeInfo) {
		GerritQueryResultSchema schema = GerritQueryResultSchema.getDefault();
		setAttributeValue(data, schema.KEY, changeInfo.getKey().abbreviate());
		setAttributeValue(data, schema.PROJECT, changeInfo.getProject().getName());
		setAttributeValue(data, schema.SUMMARY, changeInfo.getSubject());
		setAttributeValue(data, schema.STATUS, changeInfo.getStatus().toString());
		setAttributeValue(data, schema.URL, connector.getTaskUrl(repository.getUrl(), data.getTaskId()));
		setAttributeValue(data, schema.UPDATED, dateToString(changeInfo.getLastUpdatedOn()));
		setAttributeValue(data, schema.CHANGE_ID, changeInfo.getKey().get());
		if (changeInfo.getStatus() != null && changeInfo.getStatus().isClosed()) {
			setAttributeValue(data, schema.COMPLETED, dateToString(changeInfo.getLastUpdatedOn()));
		}
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
