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
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.internal.github.core.GitHubTaskDataHandler;
import org.eclipse.mylyn.internal.github.core.GithubApi;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestReviewComment;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;

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
	 * @throws IOException
	 */
	public TaskData createTaskData(TaskRepository repository, IProgressMonitor monitor, GHRepository repo,
			PullRequestComposite prComp) throws IOException {
		GHPullRequest pr = prComp.getRequest();
		String key = Integer.toString(pr.getNumber());
		TaskData data = new TaskData(getAttributeMapper(repository), PullRequestConnector.KIND,
				repository.getRepositoryUrl(), key);
		data.setVersion(DATA_VERSION);

		createOperations(data, pr);

		createAttribute(data, PullRequestAttribute.KEY.getMetadata(), key);
		createAttribute(data, PullRequestAttribute.TITLE.getMetadata(), pr.getTitle());
		createAttribute(data, PullRequestAttribute.BODY.getMetadata(), pr.getBody());
		createAttribute(data, PullRequestAttribute.STATUS.getMetadata(), pr.getState().toString());
		createAttribute(data, PullRequestAttribute.CREATION_DATE.getMetadata(), Date.from(pr.getCreatedAt()));
		createAttribute(data, PullRequestAttribute.MODIFICATION_DATE.getMetadata(), Date.from(pr.getUpdatedAt()));
		if (pr.getClosedAt() != null) {
			createAttribute(data, PullRequestAttribute.CLOSED_DATE.getMetadata(), Date.from(pr.getClosedAt()));
		}

		GHUser reporter = pr.getUser();
		createAttribute(data, PullRequestAttribute.REPORTER.getMetadata(), reporter, repository);
		String reporterGravatar = reporter != null ? reporter.getAvatarUrl() : null;
		createAttribute(data, PullRequestAttribute.REPORTER_GRAVATAR.getMetadata(), reporterGravatar);

		createAttribute(data, PullRequestAttribute.COMMENT_NEW.getMetadata());

		createAttribute(data, PullRequestAttribute.MODEL.getMetadata(), prComp.valueOf());

		return data;
	}

	private void createOperations(TaskData data, GHPullRequest pr) throws IOException {
		createOperationAttribute(data);

		// Merged pull requests cannot be reopened
		if (data.isNew() || pr.isMerged()) {
			return;
		}

		GHIssueState state = pr.getState();
		if (state != null) {
			addOperation(data, pr, PullRequestOperation.LEAVE, true);
			if (state.equals(GHIssueState.OPEN)) {
				addOperation(data, pr, PullRequestOperation.CLOSE, false);
			} else if (state.equals(GHIssueState.CLOSED)) {
				addOperation(data, pr, PullRequestOperation.REOPEN, false);
			}
		}
	}

	private void addOperation(TaskData data, GHPullRequest pr, PullRequestOperation operation, boolean isDefault) {
		String id = operation.getId();
		String label = createOperationLabel(pr, operation);
		addOperation(data, id, label, isDefault);
	}

	private String createOperationLabel(GHPullRequest pr, PullRequestOperation operation) {
		return operation == PullRequestOperation.LEAVE ? operation.getLabel() + pr.getState() : operation.getLabel();
	}

	/**
	 * Create task data for pull request
	 *
	 * @param repository
	 * @param monitor
	 * @param repo
	 * @param prComp
	 * @param comments
	 * @return task data
	 * @throws IOException
	 */
	public TaskData createTaskData(TaskRepository repository, IProgressMonitor monitor, GHRepository repo,
			PullRequestComposite prComp, List<GHIssueComment> comments,
			final List<GHPullRequestReviewComment> commitComments)
					throws IOException {
		TaskData taskData = createTaskData(repository, monitor, repo, prComp);
		taskData.setPartial(false);

		addComments(taskData.getRoot(), comments, commitComments, repository);

		return taskData;
	}

	private GHPullRequest createPullRequest(TaskData taskData) throws IOException {
		GHPullRequest pr = new GHPullRequest();
//		if (!taskData.isNew()) {
//			pr.setNumber(Integer.parseInt(taskData.getTaskId()));
//		}

		pr.setBody(getAttributeValue(taskData, PullRequestAttribute.BODY.getMetadata()));
		pr.setTitle(getAttributeValue(taskData, PullRequestAttribute.TITLE.getMetadata()));

		return pr;
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, TaskData data, ITaskMapping initializationData,
			IProgressMonitor monitor) throws CoreException {
		data.setVersion(DATA_VERSION);
		for (PullRequestAttribute attr : PullRequestAttribute.values()) {
			if (attr.getMetadata().isInitTask()) {
				createAttribute(data, attr.getMetadata(), (String) null);
			}
		}
		return true;
	}

	@Override
	public RepositoryResponse postTaskData(TaskRepository repository, TaskData taskData,
			Set<TaskAttribute> oldAttributes, IProgressMonitor monitor) throws CoreException {
		String taskId = taskData.getTaskId();
		try {
			GithubApi client = GithubApi.createGithubClient(repository);
			GHRepository repo = PullRequestConnector.getRepo(client, repository);
			boolean collaborator = isCollaborator(client, repo);

			if (taskData.isNew()) {
//				GHPullRequest pr = createPullRequest(taskData);
//				GHPullRequest newPr = repo.createPullRequest(repo, pr);
//				taskId = Integer.toString(newPr.getNumber());
				/*
				 * need branches to create a new pull request, so for now just throw an exception
				 */
				throw new CoreException(GitHub.createErrorStatus("Creating new pull requests is not supported.")); //$NON-NLS-1$
			} else {
				GHPullRequest pr = repo.getPullRequest(Integer.parseInt(taskId));
				// Handle new comment
				String comment = getAttributeValue(taskData, PullRequestAttribute.COMMENT_NEW.getMetadata());
				if (comment != null && !comment.isEmpty()) {
					pr.comment(comment);
				}

				boolean reporter = attributeMatchesUser(client, PullRequestAttribute.REPORTER.getMetadata(), taskData);
				if (collaborator || reporter) {
					// Handle state change
					TaskAttribute operationAttribute = taskData.getRoot().getAttribute(TaskAttribute.OPERATION);
					if (operationAttribute != null) {
						PullRequestOperation operation = PullRequestOperation.fromId(operationAttribute.getValue());
						if (operation == PullRequestOperation.REOPEN) {
							pr.reopen();
						} else if (operation == PullRequestOperation.CLOSE) {
							pr.close();
						}
					}
				}
			}
			return new RepositoryResponse(
					taskData.isNew() ? ResponseKind.TASK_CREATED : ResponseKind.TASK_UPDATED, taskId);
		} catch (IOException e) {
			throw new CoreException(GitHub.createWrappedStatus(e));
		}
	}
}
