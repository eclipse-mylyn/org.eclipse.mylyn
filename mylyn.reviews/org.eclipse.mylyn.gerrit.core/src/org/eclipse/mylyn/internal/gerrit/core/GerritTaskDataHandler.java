/*********************************************************************
 * Copyright (c) 2010, 2016 Sony Ericsson/ST Ericsson and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Tasktop Technologies - improvements
 *      GitHub, Inc. - fixes for bug 354753
 *      Sascha Scholz (SAP) - improvements
 *      Marc-Andre Laperle (Ericsson) - Add topic
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.data.GerritPerson;
import org.eclipse.mylyn.internal.gerrit.core.client.data.GerritQueryResult;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.GerritReviewLabel;
import org.eclipse.mylyn.internal.gerrit.core.remote.GerritRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfObserver;
import org.eclipse.mylyn.reviews.internal.core.BuildResult;
import org.eclipse.mylyn.reviews.internal.core.ReviewFileCommentsMapper;
import org.eclipse.mylyn.reviews.internal.core.TaskBuildStatusMapper;
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
import org.eclipse.osgi.util.NLS;

import com.google.gerrit.common.data.AccountInfo;
import com.google.gerrit.common.data.ApprovalDetail;
import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.ChangeInfo;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.ApprovalCategory;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.ChangeMessage;
import com.google.gerrit.reviewdb.PatchSetApproval;
import com.google.gerrit.reviewdb.Project;

/**
 * @author Mikael Kober
 * @author Thomas Westling
 * @author Steffen Pingel
 * @author Kevin Sawicki
 */
public class GerritTaskDataHandler extends AbstractTaskDataHandler {

	private final GerritConnector connector;

	private final String ANONYMOUS = "Anonymous"; //$NON-NLS-1$

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
		data.setPartial(true);
		return data;
	}

	@Override
	public TaskAttributeMapper getAttributeMapper(TaskRepository repository) {
		return new TaskAttributeMapper(repository) {
			@Override
			public boolean equals(TaskAttribute newAttribute, TaskAttribute oldAttribute) {

				if (oldAttribute == null) {
					return false;
				}

				if (!TaskBuildStatusMapper.BUILD_RESULT_TYPE.equals(newAttribute.getMetaData().getType())) {
					return super.equals(newAttribute, oldAttribute);
				}

				if (!super.equals(newAttribute, oldAttribute)) {
					return false;
				}

				for (TaskAttribute newChild : newAttribute.getAttributes().values()) {
					if (!equals(newChild, oldAttribute.getAttribute(newChild.getId()))) {
						return false;
					}
				}

				return true;
			}
		};
	}

	/**
	 * Retrieves task data for the given review from repository.
	 */
	public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		ReviewObserver reviewObserver = new ReviewObserver();
		try {
			GerritClient client = connector.getClient(repository);
			boolean anonymous = client.isAnonymous();
			String id = null;
			if (!anonymous) {
				id = getAccountId(client, repository, monitor);
			}
			taskId = client.toReviewId(taskId, monitor);
			TaskData taskData = createTaskData(repository, taskId, monitor);

			RemoteEmfConsumer<IRepository, IReview, String, GerritChange, String, Date> consumer = updateModelData(
					repository, taskData, reviewObserver, monitor);
			GerritChange gerritChange = consumer.getRemoteObject();
			if (gerritChange == null) {
				throw new CoreException(connector.createErrorStatus(repository,
						NLS.bind("Couldn't retrieve remote object for task: {0}. Check remote connection", taskId))); //$NON-NLS-1$
			}
			Project.NameKey project = gerritChange.getChangeDetail().getChange().getProject();
			client.refreshConfigOnce(project, monitor);
			if (!monitor.isCanceled()) {
				updateTaskData(repository, taskData, gerritChange, consumer.getModelObject(), !anonymous, id);
			}

			return taskData;
		} catch (GerritException e) {
			throw connector.toCoreException(repository, NLS.bind("Problem retrieving task data for task: {0}", taskId), //$NON-NLS-1$
					e);
		} finally {
			reviewObserver.dispose();
		}
	}

	private RemoteEmfConsumer<IRepository, IReview, String, GerritChange, String, Date> updateModelData(
			TaskRepository repository, TaskData taskData, ReviewObserver reviewObserver, IProgressMonitor monitor)
			throws CoreException {
		GerritClient client = connector.getClient(repository);
		GerritRemoteFactoryProvider factoryProvider = (GerritRemoteFactoryProvider) client.getFactoryProvider();
		RemoteEmfConsumer<IRepository, IReview, String, GerritChange, String, Date> consumer = factoryProvider
				.getReviewFactory()
				.getConsumerForLocalKey(factoryProvider.getRoot(), taskData.getTaskId());

		consumer.addObserver(reviewObserver);
		if (!consumer.isRetrieving()) {
			if (monitor.isCanceled()) {
				return consumer;
			}
			consumer.open();
			if (monitor.isCanceled()) {
				return consumer;
			}
			consumer.setAsynchronous(false);
			consumer.retrieve(true);
			consumer.setAsynchronous(true);
		}

		while (!reviewObserver.complete && !monitor.isCanceled()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				reviewObserver.dispose();
				Thread.currentThread().interrupt();
			}
		}

		consumer.save();
		if (!consumer.getStatus().isOK()) {
			if (consumer.getStatus().getException() instanceof CoreException) {
				throw (CoreException) consumer.getStatus().getException();
			}
			throw new CoreException(consumer.getStatus());
		}
		return consumer;
	}

	private class ReviewObserver extends RemoteEmfObserver<IRepository, IReview, String, Date> {
		boolean complete;

		@Override
		public void updated(boolean modified) {
			complete = true;
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

	public void updateTaskData(TaskRepository repository, TaskData data, GerritChange gerritReview, IReview modelReview,
			boolean canPublish, String accountId) {
		GerritTaskSchema schema = GerritTaskSchema.getDefault();

		ChangeDetail changeDetail = gerritReview.getChangeDetail();
		Change change = changeDetail.getChange();
		AccountInfo owner = changeDetail.getAccounts().get(change.getOwner());

		updatePartialTaskData(repository, data, new GerritQueryResult(new ChangeInfo(change)));
		setAttributeValue(data, schema.BRANCH, change.getDest().get());
		String userId = GerritUtil.getUserId(owner);
		String userLabel = GerritUtil.getUserLabel(owner);
		TaskAttribute ownerAttribute = setAttributeValue(data, schema.OWNER, userId);
		if (ownerAttribute != null) {
			ownerAttribute.putOption(userId, userLabel);
		}
		setAttributeValue(data, schema.UPLOADED, dateToString(((ChangeDetailX) changeDetail).getDateCreated()));
		setAttributeValue(data, schema.UPDATED, dateToString(((ChangeDetailX) changeDetail).getLastModified()));
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
					userName = email != null ? email : id;
				}
				IRepositoryPerson person = repository.createPerson(userName);
				person.setName(author.getFullName());
				mapper.setAuthor(person);
			} else {
				// messages without an author are from Gerrit itself
				IRepositoryPerson person = repository.createPerson("Gerrit Code Review"); //$NON-NLS-1$
				mapper.setAuthor(person);
			}
			mapper.setText(message.getMessage());
			mapper.setCreationDate(message.getWrittenOn());
			mapper.setNumber(i);
			TaskAttribute attribute = data.getRoot().createAttribute(TaskAttribute.PREFIX_COMMENT + i);
			mapper.applyTo(attribute);
			i++;
		}

//		ListMultimap<Integer, BuildResult> resultsByPatchNumber = ArrayListMultimap.create();
//		for (BuildResult buildResult : ((ChangeDetailX) changeDetail).getPatchSetBuildStatuses()) {
//			resultsByPatchNumber.put(buildResult.getPatchSetNumber(), buildResult);
//		}
//
//
//		for (Entry<Integer, Collection<BuildResult>> buildResult : resultsByPatchNumber.asMap().entrySet()) {

		MultiValuedMap<Integer, BuildResult> resultsByPatchNumber = new ArrayListValuedHashMap<>();
		for (BuildResult buildResult : ((ChangeDetailX) changeDetail).getPatchSetBuildStatuses()) {
			resultsByPatchNumber.put(buildResult.getPatchSetNumber(), buildResult);
		}

		for (Entry<Integer, Collection<BuildResult>> buildResult : resultsByPatchNumber.asMap().entrySet()) {
			int patchNumber = buildResult.getKey();
			TaskBuildStatusMapper mapper = new TaskBuildStatusMapper(buildResult.getValue());
			TaskAttribute attribute = data.getRoot()
					.createAttribute(TaskBuildStatusMapper.ATTR_TYPE_PATCH_SET + patchNumber);
			mapper.applyTo(attribute);
		}

		setAttributeValue(data, schema.CAN_PUBLISH, Boolean.toString(canPublish));

		// Retrieve the 'starred' state
		setAttributeValue(data, schema.IS_STARRED, Boolean.toString(changeDetail.isStarred()));

		// Retrieve the approvals
		Short reviewState = 0;
		Short verifyState = 0;
		for (ApprovalDetail approvals : changeDetail.getApprovals()) {
			Map<ApprovalCategory.Id, PatchSetApproval> map = approvals.getApprovalMap();
			PatchSetApproval approval = map.get(new ApprovalCategory.Id("CRVW")); //$NON-NLS-1$
			if (approval != null && approval.getValue() != 0) {
				reviewState = getStateValue(approval.getValue(), reviewState);
			}
			approval = map.get(new ApprovalCategory.Id("VRIF")); //$NON-NLS-1$
			if (approval != null && approval.getValue() != 0) {
				verifyState = getStateValue(approval.getValue(), verifyState);
			}
		}
		setAttributeValue(data, schema.REVIEW_STATE, reviewState.toString());
		setAttributeValue(data, schema.VERIFY_STATE, verifyState.toString());

		new ReviewFileCommentsMapper(modelReview).applyTo(data);
	}

	private Short getStateValue(Short value, Short oldState) {
		short state = 0;
		if (value < 0) {
			state = (short) Math.min(oldState, value);
		} else {
			state = (short) Math.max(oldState, value);
		}
		return state;
	}

	@Override
	public void migrateTaskData(TaskRepository repository, TaskData taskData) {
		super.migrateTaskData(repository, taskData);
		//Support 1.1.0 commenting capability see https://bugs.eclipse.org/bugs/show_bug.cgi?id=344108
		if (taskData.getRoot().getAttribute(GerritTaskSchema.getDefault().NEW_COMMENT.getKey()) == null) {
			taskData.getRoot().createAttribute(GerritTaskSchema.getDefault().NEW_COMMENT.getKey());
		}
	}

	public void updatePartialTaskData(TaskRepository repository, TaskData data, GerritQueryResult queryResult) {
		GerritQueryResultSchema schema = GerritQueryResultSchema.getDefault();
		setAttributeValue(data, schema.KEY, shortenChangeId(queryResult.getId()));
		setAttributeValue(data, schema.PROJECT, queryResult.getProject());
		setAttributeValue(data, schema.SUMMARY, queryResult.getSubject());
		setAttributeValue(data, schema.STATUS, queryResult.getStatus());
		setAttributeValue(data, schema.URL, connector.getTaskUrl(repository.getUrl(), data.getTaskId()));
		setAttributeValue(data, schema.UPDATED, dateToString(queryResult.getUpdated()));
		setAttributeValue(data, schema.CHANGE_ID, queryResult.getId());
		if (GerritConnector.isClosed(queryResult.getStatus())) {
			setAttributeValue(data, schema.COMPLETED, dateToString(queryResult.getUpdated()));
		}

		GerritPerson owner = queryResult.getOwner();
		if (owner != null) {
			String fullName = getFullNameFromAccount(repository);
			if (fullName != null && fullName.equals(owner.getName())) {
				// populate ITask.ownerId so that My Tasks filter works
				String preferredEmail = getPreferredEmailFromAccount(repository);
				TaskAttribute ownerAttribute = setAttributeValue(data, schema.OWNER, preferredEmail);
				ownerAttribute.putOption(preferredEmail, fullName);
			} else {
				// we don't have the owner id and it could be expensive to include it in the query results
				setAttributeValue(data, schema.OWNER, owner.getName());
			}
		} else {
			setAttributeValue(data, schema.OWNER, ANONYMOUS);
		}
		setAttributeValue(data, schema.BRANCH, queryResult.getBranch());
		setAttributeValue(data, schema.IS_STARRED, (queryResult.isStarred() ? Boolean.TRUE : Boolean.FALSE).toString());
		setAttributeValue(data, schema.TOPIC, queryResult.getTopic());

		GerritReviewLabel reviewLabel = queryResult.getReviewLabel();
		if (reviewLabel != null) {
			if (reviewLabel.getVerifyStatus() != null) {
				setAttributeValue(data, schema.VERIFY_STATE, reviewLabel.getVerifyStatus().getStatus());
			}
			if (reviewLabel.getCodeReviewStatus() != null) {
				setAttributeValue(data, schema.REVIEW_STATE, reviewLabel.getCodeReviewStatus().getStatus());
			}
		}
	}

	private String getFullNameFromAccount(TaskRepository repository) {
		GerritConfiguration config = connector.getConfiguration(repository);
		if (config != null && config.getAccount() != null) {
			return config.getAccount().getFullName();
		}
		return null;
	}

	private String getPreferredEmailFromAccount(TaskRepository repository) {
		GerritConfiguration config = connector.getConfiguration(repository);
		if (config != null && config.getAccount() != null) {
			return config.getAccount().getPreferredEmail();
		}
		return null;
	}

	private String shortenChangeId(String changeId) {
		changeId = GerritUtil.toChangeId(changeId);
		return changeId.substring(0, Math.min(9, changeId.length()));
	}

	/**
	 * Convenience method to set the value of a given Attribute in the given {@link TaskData}.
	 */
	private TaskAttribute setAttributeValue(TaskData data, Field gerritAttribute, String value) {
		TaskAttribute attribute = data.getRoot().getAttribute(gerritAttribute.getKey());
		if (value != null) {
			attribute.setValue(value);
		}
		return attribute;
	}

	private static String dateToString(Date date) {
		if (date == null) {
			return ""; //$NON-NLS-1$
		} else {
			return Long.toString(date.getTime());
		}
	}

}
