/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.core.pr;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GsonUtils;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.internal.github.core.GitHubTaskDataHandler;
import org.eclipse.mylyn.internal.github.core.issue.IssueConnector;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * GitHub issue task data handler
 */
public class PullRequestTaskDataHandler extends GitHubTaskDataHandler {

	private static final String DATA_VERSION = "1.1"; //$NON-NLS-1$

	/**
	 * Create GitHub issue task data handler for connector
	 *
	 * @param connector
	 */
	public PullRequestTaskDataHandler(PullRequestConnector connector) {
		// empty
	}

	/**
	 * Create task data for pull request
	 *
	 * @param repository
	 * @param monitor
	 * @param repo
	 * @param prComp
	 * @return task data
	 */
	public TaskData createTaskData(TaskRepository repository,
			IProgressMonitor monitor, IRepositoryIdProvider repo,
			PullRequestComposite prComp) {
		PullRequest pr = prComp.getRequest();
		String key = Integer.toString(pr.getNumber());
		TaskData data = new TaskData(getAttributeMapper(repository),
				PullRequestConnector.KIND, repository.getRepositoryUrl(), key);
		data.setVersion(DATA_VERSION);

		createOperations(data, pr);

		createAttribute(data, PullRequestAttribute.KEY.getMetadata(), key);
		createAttribute(data, PullRequestAttribute.TITLE.getMetadata(),
				pr.getTitle());
		createAttribute(data, PullRequestAttribute.BODY.getMetadata(),
				pr.getBody());
		createAttribute(data, PullRequestAttribute.STATUS.getMetadata(),
				pr.getState());
		createAttribute(data, PullRequestAttribute.CREATION_DATE.getMetadata(),
				pr.getCreatedAt());
		createAttribute(data,
				PullRequestAttribute.MODIFICATION_DATE.getMetadata(),
				pr.getUpdatedAt());
		createAttribute(data, PullRequestAttribute.CLOSED_DATE.getMetadata(),
				pr.getClosedAt());

		User reporter = pr.getUser();
		createAttribute(data, PullRequestAttribute.REPORTER.getMetadata(),
				reporter, repository);
		String reporterGravatar = reporter != null ? reporter.getAvatarUrl()
				: null;
		createAttribute(data,
				PullRequestAttribute.REPORTER_GRAVATAR.getMetadata(),
				reporterGravatar);

		createAttribute(data, PullRequestAttribute.COMMENT_NEW.getMetadata());

		createAttribute(data, PullRequestAttribute.MODEL.getMetadata(),
				GsonUtils.toJson(prComp));

		return data;
	}

	private void createOperations(TaskData data, PullRequest pr) {
		createOperationAttribute(data);

		if (data.isNew())
			return;

		// Merged pull requests cannot be reopened
		if (pr.isMerged())
			return;

		String state = pr.getState();
		if (state != null) {
			addOperation(data, pr, PullRequestOperation.LEAVE, true);
			if (state.equals(IssueService.STATE_OPEN))
				addOperation(data, pr, PullRequestOperation.CLOSE, false);
			else if (state.equals(IssueService.STATE_CLOSED))
				addOperation(data, pr, PullRequestOperation.REOPEN, false);
		}
	}

	private void addOperation(TaskData data, PullRequest pr,
			PullRequestOperation operation, boolean isDefault) {
		String id = operation.getId();
		String label = createOperationLabel(pr, operation);
		addOperation(data, id, label, isDefault);
	}

	private String createOperationLabel(PullRequest pr,
			PullRequestOperation operation) {
		return operation == PullRequestOperation.LEAVE ? operation.getLabel()
				+ pr.getState() : operation.getLabel();
	}

	/**
	 * Create task data for pull request
	 *
	 * @param repository
	 * @param monitor
	 * @param repo
	 * @param pr
	 * @param comments
	 * @return task data
	 */
	public TaskData createTaskData(TaskRepository repository,
			IProgressMonitor monitor, IRepositoryIdProvider repo,
			PullRequestComposite pr, List<Comment> comments) {
		TaskData taskData = createTaskData(repository, monitor, repo, pr);
		taskData.setPartial(false);

		addComments(taskData.getRoot(), comments, repository);

		return taskData;
	}

	private PullRequest createPullRequest(TaskData taskData) {
		PullRequest pr = new PullRequest();
		if (!taskData.isNew())
			pr.setNumber(Integer.parseInt(taskData.getTaskId()));

		pr.setBody(getAttributeValue(taskData,
				PullRequestAttribute.BODY.getMetadata()));
		pr.setTitle(getAttributeValue(taskData,
				PullRequestAttribute.TITLE.getMetadata()));

		return pr;
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, TaskData data,
			ITaskMapping initializationData, IProgressMonitor monitor)
			throws CoreException {
		data.setVersion(DATA_VERSION);
		for (PullRequestAttribute attr : PullRequestAttribute.values())
			if (attr.getMetadata().isInitTask())
				createAttribute(data, attr.getMetadata(), (String) null);
		return true;
	}

	@Override
	public RepositoryResponse postTaskData(TaskRepository repository,
			TaskData taskData, Set<TaskAttribute> oldAttributes,
			IProgressMonitor monitor) throws CoreException {
		String taskId = taskData.getTaskId();
		PullRequest pr = createPullRequest(taskData);
		RepositoryId repo = PullRequestConnector.getRepository(repository
				.getRepositoryUrl());
		try {
			GitHubClient client = IssueConnector.createClient(repository);
			boolean collaborator = isCollaborator(client, repo);
			PullRequestService prService = new PullRequestService(client);
			IssueService issueService = new IssueService(client);
			if (taskData.isNew()) {
				pr.setState(IssueService.STATE_OPEN);
				pr = prService.createPullRequest(repo, pr);
				taskId = Integer.toString(pr.getNumber());
			} else {
				// Handle new comment
				String comment = getAttributeValue(taskData,
						PullRequestAttribute.COMMENT_NEW.getMetadata());
				if (comment != null && comment.length() > 0)
					issueService.createComment(repo.getOwner(), repo.getName(),
							taskId, comment);

				boolean reporter = attributeMatchesUser(client,
						PullRequestAttribute.REPORTER.getMetadata(), taskData);
				if (collaborator || reporter) {
					// Handle state change
					TaskAttribute operationAttribute = taskData.getRoot()
							.getAttribute(TaskAttribute.OPERATION);
					if (operationAttribute != null) {
						PullRequestOperation operation = PullRequestOperation
								.fromId(operationAttribute.getValue());
						if (operation == PullRequestOperation.REOPEN)
							pr.setState(IssueService.STATE_OPEN);
						else if (operation == PullRequestOperation.CLOSE)
							pr.setState(IssueService.STATE_CLOSED);
					}
					prService.editPullRequest(repo, pr);
				}
			}
			return new RepositoryResponse(
					taskData.isNew() ? ResponseKind.TASK_CREATED
							: ResponseKind.TASK_UPDATED, taskId);
		} catch (IOException e) {
			throw new CoreException(GitHub.createWrappedStatus(e));
		}
	}
}
