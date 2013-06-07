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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.data.GerritQueryResult;
import org.eclipse.mylyn.internal.gerrit.core.remote.GerritRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfObserver;
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

	private boolean retrievePatchSets;

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
			TaskData taskData = createTaskData(repository, taskId, monitor);

			ReviewObserver reviewObserver = new ReviewObserver();
			RemoteEmfConsumer<IRepository, IReview, String, GerritChange, String, Date> consumer = updateModelData(
					repository, taskData, reviewObserver, monitor);
			if (!monitor.isCanceled()) {
				updateTaskData(repository, taskData, consumer.getRemoteObject(), !anonymous, id);
			}
			reviewObserver.dispose();
			return taskData;
		} catch (GerritException e) {
			throw connector.toCoreException(repository, "Problem retrieving task data", e);
		}
	}

	private RemoteEmfConsumer<IRepository, IReview, String, GerritChange, String, Date> updateModelData(
			TaskRepository repository, TaskData taskData, ReviewObserver reviewObserver, IProgressMonitor monitor)
			throws CoreException {
		GerritClient client = connector.getClient(repository);
		GerritRemoteFactoryProvider factoryProvider = (GerritRemoteFactoryProvider) client.getFactoryProvider();
		RemoteEmfConsumer<IRepository, IReview, String, GerritChange, String, Date> consumer = factoryProvider.getReviewFactory()
				.getConsumerForLocalKey(factoryProvider.getRoot(), taskData.getTaskId());

		if (!consumer.isRetrieving()) {
			if (monitor.isCanceled()) {
				return consumer;
			}
			consumer.addObserver(reviewObserver);
			consumer.open();
			Date priorModificationData = consumer.getModelObject() != null ? consumer.getModelObject()
					.getModificationDate() : null;
			if (monitor.isCanceled()) {
				return consumer;
			}
			consumer.setAsynchronous(false);
			consumer.retrieve(true);
			consumer.setAsynchronous(true);

			long startTime = System.currentTimeMillis();
			while (!reviewObserver.complete && !monitor.isCanceled()) {
//				if (System.currentTimeMillis() > startTime + GerritConnector.GERRIT_COLLECTION_TIMEOUT) {
//					reviewObserver.dispose();
//					throw new CoreException(new Status(IStatus.WARNING, GerritCorePlugin.PLUGIN_ID,
//							"Task retrieval taking too long. Connection issue?"));
//				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
//					if (monitor.isCanceled()) {
//						break; //We assume that this is what the interrupt was about
//					} else {
					reviewObserver.dispose();
					Thread.currentThread().interrupt();
//					}
				}
			}

			if (consumer.getModelObject().getModificationDate() != null
					&& !consumer.getModelObject().getModificationDate().equals(priorModificationData)) {
				Collection<ReviewItemSetClient> setClients = new ArrayList<ReviewItemSetClient>();
				if (reviewObserver.result.isOK() && retrievePatchSets) {
					int index = 0;
					for (IReviewItemSet set : consumer.getModelObject().getSets()) {
						//Retrieve the last patch set plus any patch sets the user has already seen.
						if (set.getItems() != null
								&& (set.getItems().size() > 0 || index == consumer.getModelObject().getSets().size() - 1)) {
							RemoteEmfConsumer<IReviewItemSet, List<IFileItem>, String, ?, ?, Long> contentConsumer = factoryProvider.getReviewItemSetContentFactory()
									.getConsumerForModel(set, set.getItems());
							ReviewItemSetClient setClient = new ReviewItemSetClient();
							contentConsumer.addObserver(setClient);
							setClients.add(setClient);
							contentConsumer.retrieve(true);
						}
						index++;
					}
					boolean done = false;
					while (!done && !monitor.isCanceled()) {
						int patchSetsUpdated = 0;
						for (ReviewItemSetClient setClient : setClients) {
							patchSetsUpdated += setClient.complete ? 1 : 0;
						}
						done = patchSetsUpdated == setClients.size();
						if (!done) {
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								if (monitor.isCanceled()) {
									break;
								} else {
									Thread.currentThread().interrupt();
								}
							}
						}
					}
				}
				for (ReviewItemSetClient setClient : setClients) {
					setClient.dispose();
				}
			}
			consumer.save();
			if (!reviewObserver.result.isOK()) {
				if (reviewObserver.result.getException() instanceof CoreException) {
					throw ((CoreException) reviewObserver.result.getException());
				}
				throw new CoreException(reviewObserver.result);
			}
		}
		return consumer;
	}

	private class ReviewItemSetClient extends RemoteEmfObserver<IReviewItemSet, List<IFileItem>, String, Long> {
		boolean complete;

		boolean failed;

		@Override
		public void updated(IReviewItemSet parentObject, List<IFileItem> modelObject, boolean modified) {
			complete = true;
		}

		@Override
		public void failed(IReviewItemSet parentObject, List<IFileItem> modelObject, IStatus status) {
			complete = true;
			failed = true;
		}
	}

	private class ReviewObserver extends RemoteEmfObserver<IRepository, IReview, String, Date> {
		boolean complete;

		IStatus result = Status.OK_STATUS;

		@Override
		public void updated(IRepository parentObject, IReview modelObject, boolean modified) {
			complete = true;
		}

		@Override
		public void failed(IRepository parentObject, IReview modelObject, IStatus status) {
			complete = true;
			result = status;
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

	public void updateTaskData(TaskRepository repository, TaskData data, GerritChange gerritReview, boolean canPublish,
			String accountId) {
		GerritTaskSchema schema = GerritTaskSchema.getDefault();

		ChangeDetail changeDetail = gerritReview.getChangeDetail();
		Change change = changeDetail.getChange();
		AccountInfo owner = changeDetail.getAccounts().get(change.getOwner());

		updateTaskData(repository, data, new GerritQueryResult(new ChangeInfo(change)));
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

		setAttributeValue(data, schema.CAN_PUBLISH, Boolean.toString(canPublish));
	}

	@Override
	public void migrateTaskData(TaskRepository repository, TaskData taskData) {
		super.migrateTaskData(repository, taskData);
		//Support 1.1.0 commenting capability see https://bugs.eclipse.org/bugs/show_bug.cgi?id=344108
		if (taskData.getRoot().getAttribute(GerritTaskSchema.getDefault().NEW_COMMENT.getKey()) == null) {
			taskData.getRoot().createAttribute(GerritTaskSchema.getDefault().NEW_COMMENT.getKey());
		}
	}

	public void updateTaskData(TaskRepository repository, TaskData data, GerritQueryResult changeInfo) {
		GerritQueryResultSchema schema = GerritQueryResultSchema.getDefault();
		setAttributeValue(data, schema.KEY, changeInfo.getId().substring(0, Math.min(9, changeInfo.getId().length())));
		setAttributeValue(data, schema.PROJECT, changeInfo.getProject());
		setAttributeValue(data, schema.SUMMARY, changeInfo.getSubject());
		setAttributeValue(data, schema.STATUS, changeInfo.getStatus());
		setAttributeValue(data, schema.URL, connector.getTaskUrl(repository.getUrl(), data.getTaskId()));
		setAttributeValue(data, schema.UPDATED, dateToString(changeInfo.getUpdated()));
		setAttributeValue(data, schema.CHANGE_ID, changeInfo.getId());
		if (GerritConnector.isClosed(changeInfo.getStatus())) {
			setAttributeValue(data, schema.COMPLETED, dateToString(changeInfo.getUpdated()));
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
