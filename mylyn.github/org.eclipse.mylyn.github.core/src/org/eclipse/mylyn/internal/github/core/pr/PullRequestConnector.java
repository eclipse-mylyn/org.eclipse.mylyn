/******************************************************************************
 *  Copyright (c) 2011, 2020 GitHub Inc. and others
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
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GsonUtils;
import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.internal.github.core.QueryUtils;
import org.eclipse.mylyn.internal.github.core.RepositoryConnector;
import org.eclipse.mylyn.internal.github.core.issue.IssueConnector;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

/**
 * GitHub pull request connector.
 */
public class PullRequestConnector extends RepositoryConnector {

	/**
	 * SEGMENT_PULL
	 */
	public static final String SEGMENT_PULL = "/pull"; //$NON-NLS-1$

	/**
	 * GitHub kind.
	 */
	public static final String KIND = "githubPullRequests"; //$NON-NLS-1$

	/**
	 * Get repository label for id provider.
	 *
	 * @param repo
	 * @return label
	 */
	public static String getRepositoryLabel(IRepositoryIdProvider repo) {
		return repo.generateId() + Messages.PullRequestConnector_LabelPullRequests;
	}

	/**
	 * Creates a pull request task repository.
	 *
	 * @param repo
	 *            internal model to create the task repository from
	 * @param username
	 *            for authentication
	 * @param password
	 *            for authentication
	 * @param isToken
	 *            whether the password is a token
	 * @return the {@link TaskRepository}
	 */
	public static TaskRepository createTaskRepository(Repository repo, String username, String password,
			boolean isToken) {
		String url = PullRequestConnector.appendPulls(GitHub.createGitHubUrl(
				repo.getOwner().getLogin(), repo.getName()));
		TaskRepository repository = new TaskRepository(KIND, url);
		repository.setRepositoryLabel(getRepositoryLabel(repo));
		String loginName = username;
		if (loginName == null && isToken) {
			loginName = ""; //$NON-NLS-1$
		}
		if (loginName != null && password != null) {
			repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials(loginName, password),
					true);
		}
		repository.setCategory(TaskRepository.CATEGORY_REVIEW);
		repository.setProperty(IRepositoryConstants.PROPERTY_USE_TOKEN, Boolean.toString(isToken));
		return repository;
	}

	/**
	 * Appends a trailing '/pull's segment to the given url
	 *
	 * @param repoUrl
	 * @return appended string
	 */
	public static String appendPulls(final String repoUrl) {
		return repoUrl + IGitHubConstants.SEGMENT_PULLS;
	}

	/**
	 * Strip trailing '/pulls' segment from string if it ends with it.
	 *
	 * @param repoUrl
	 * @return stripped string
	 */
	public static String stripPulls(String repoUrl) {
		if (repoUrl.endsWith(IGitHubConstants.SEGMENT_PULLS)) {
			return repoUrl.substring(0, repoUrl.length() - IGitHubConstants.SEGMENT_PULLS.length());
		}
		return repoUrl;
	}

	/**
	 * Get pull request from task data
	 *
	 * @param data
	 * @return pull request
	 */
	public static PullRequestComposite getPullRequest(TaskData data) {
		if (data == null) {
			return null;
		}
		String value = PullRequestAttribute.MODEL.getMetadata().getValue(data);
		if (value.length() == 0) {
			return null;
		}
		return GsonUtils.fromJson(value, PullRequestComposite.class);
	}

	/**
	 * Get repository id from pull request task repository url
	 *
	 * @param prRepoUrl
	 * @return repository id
	 */
	public static RepositoryId getRepository(String prRepoUrl) {
		return GitHub.getRepository(stripPulls(prRepoUrl));
	}

	/**
	 * GitHub specific {@link AbstractTaskDataHandler}.
	 */
	private final PullRequestTaskDataHandler taskDataHandler;

	/**
	 * Create GitHub issue repository connector
	 */
	public PullRequestConnector() {
		taskDataHandler = new PullRequestTaskDataHandler(this);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see #KIND
	 */
	@Override
	public String getConnectorKind() {
		return KIND;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return Messages.PullRequestConnector_Label;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractTaskDataHandler getTaskDataHandler() {
		return taskDataHandler;
	}

	@Override
	public IStatus performQuery(TaskRepository repository, IRepositoryQuery query, TaskDataCollector collector,
			ISynchronizationSession session, IProgressMonitor monitor) {
		IStatus result = Status.OK_STATUS;
		List<String> statuses = QueryUtils.getAttributes(
				IssueService.FILTER_STATE, query);

		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.PullRequestConnector_TaskFetching,
				statuses.size() * 100);
		try {
			RepositoryId repo = getRepository(repository.getRepositoryUrl());

			GitHubClient client = IssueConnector.createClient(repository);
			PullRequestService service = new PullRequestService(client);
			IssueService commentService = new IssueService(client);

			for (String status : statuses) {
				SubMonitor statusMonitor = subMonitor.split(100);
				statusMonitor.setTaskName(status + Messages.PullRequestConnector_LabelPullRequests);

				List<PullRequest> pulls = service.getPullRequests(repo, status);
				statusMonitor.checkCanceled();

				// collect task data
				statusMonitor.setWorkRemaining(pulls.size());
				for (PullRequest pr : pulls) {
					TaskData taskData = getTaskData(repository, pr.getNumber(), statusMonitor, repo, commentService,
							service);
					collector.accept(taskData);

					statusMonitor.split(1);
				}
				statusMonitor.done();
			}
		} catch (IOException e) {
			result = GitHub.createWrappedStatus(e);
		} finally {
			subMonitor.done();
		}

		return result;
	}

	@Override
	public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		RepositoryId repo = getRepository(repository.getRepositoryUrl());

		try {
			GitHubClient client = IssueConnector.createClient(repository);
			PullRequestService service = new PullRequestService(client);
			IssueService commentService = new IssueService(client);

			return getTaskData(repository, Integer.parseInt(taskId), monitor, repo, commentService, service);
		} catch (IOException e) {
			throw new CoreException(GitHub.createWrappedStatus(e));
		}
	}

	private TaskData getTaskData(TaskRepository repository, int taskId, IProgressMonitor monitor, RepositoryId repo,
			IssueService commentService, PullRequestService service) throws IOException, NumberFormatException {

		PullRequest pr = service.getPullRequest(repo, taskId);
		PullRequestComposite prComp = new PullRequestComposite();
		prComp.setRequest(pr);

		List<Comment> comments = Collections.emptyList();
		if (pr.getComments() > 0) {
			comments = commentService.getComments(repo.getOwner(), repo.getName(), taskId);
		}

		List<CommitComment> commitComments = Collections.emptyList();
		if (pr.getReviewComments() > 0) {
			commitComments = service.getComments(repo, pr.getNumber());
		}

		if (pr.getCommits() > 0) {
			prComp.setCommits(service.getCommits(repo, pr.getNumber()));
		}
		return taskDataHandler.createTaskData(repository, monitor, repo, prComp, comments, commitComments);
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String taskFullUrl) {
		int lastPull = taskFullUrl.lastIndexOf(SEGMENT_PULL);
		if (lastPull != -1) {
			return taskFullUrl.substring(0, lastPull) + IGitHubConstants.SEGMENT_PULLS;
		}
		return null;
	}

	@Override
	public String getTaskUrl(String repositoryUrl, String taskId) {
		return stripPulls(repositoryUrl) + SEGMENT_PULL + "/" + taskId; //$NON-NLS-1$
	}
}
