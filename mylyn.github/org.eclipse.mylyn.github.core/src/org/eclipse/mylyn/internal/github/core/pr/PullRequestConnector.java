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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.internal.github.core.GithubApi;
import org.eclipse.mylyn.internal.github.core.QueryUtils;
import org.eclipse.mylyn.internal.github.core.RepositoryConnector;
import org.eclipse.mylyn.internal.github.egit.github.core.RepositoryCommit;
import org.eclipse.mylyn.internal.github.egit.github.core.RepositoryId;
import org.eclipse.mylyn.internal.github.egit.github.core.client.IGitHubConstants;
import org.eclipse.mylyn.internal.github.egit.github.core.client.IssueService;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestCommitDetail;
import org.kohsuke.github.GHPullRequestQueryBuilder;
import org.kohsuke.github.GHPullRequestReviewComment;
import org.kohsuke.github.GHRepository;

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
	public static String getRepositoryLabel(GHRepository repo) {
		return repo.getFullName() + Messages.PullRequestConnector_LabelPullRequests;
	}
	public static String getRepositoryLabel(RepositoryId repo) {
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
	 * @throws IOException
	 */
	public static TaskRepository createTaskRepository(GHRepository repo, String username, String password,
			boolean isToken) throws IOException {
		String url = PullRequestConnector.appendPulls(repo.getHtmlUrl().toString());
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
		if (repoUrl.endsWith(IGitHubConstants.SEGMENT_PULLS)) {
			return repoUrl;
		} else {
			return repoUrl + IGitHubConstants.SEGMENT_PULLS;
		}
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
	 * @throws IOException
	 */
	public static PullRequestComposite getPullRequest(TaskData data) throws IOException {
		if (data == null) {
			return null;
		}
		String value = PullRequestAttribute.MODEL.getMetadata().getValue(data);
		if (value.length() == 0) {
			return null;
		}

		return PullRequestComposite.valueOf(value);
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
			GHRepository repo = createClient(repository);
			for (String status : statuses) {
				SubMonitor statusMonitor = subMonitor.split(100);
				statusMonitor.setTaskName(status + Messages.PullRequestConnector_LabelPullRequests);

				GHPullRequestQueryBuilder queryBuilder = repo.queryPullRequests();
				if ("open".equalsIgnoreCase(status)) { //$NON-NLS-1$
					queryBuilder.state(GHIssueState.OPEN);
				} else if ("closed".equalsIgnoreCase(status)) { //$NON-NLS-1$
					queryBuilder.state(GHIssueState.CLOSED);
				} else if ("all".equalsIgnoreCase(status)) { //$NON-NLS-1$
					queryBuilder.state(GHIssueState.ALL);
				}
				List<GHPullRequest> pulls = queryBuilder.list().toList();
				statusMonitor.checkCanceled();

				// collect task data
				statusMonitor.setWorkRemaining(pulls.size());
				for (GHPullRequest pr : pulls) {
					TaskData taskData = getTaskData(repository, pr.getNumber(), statusMonitor, repo, pr);
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
		try {
			int taskNr = Integer.parseInt(taskId);
			GHRepository repo = createClient(repository);
			GHPullRequest pr = repo.getPullRequest(taskNr);
			return getTaskData(repository, taskNr, monitor, repo, pr);
		} catch (IOException e) {
			throw new CoreException(GitHub.createWrappedStatus(e));
		}
	}

	private TaskData getTaskData(TaskRepository repository, int taskId, IProgressMonitor monitor, GHRepository repo,
			GHPullRequest pr) throws IOException, NumberFormatException {

		PullRequestComposite prComp = new PullRequestComposite();
		prComp.setRequest(pr);

		List<GHIssueComment> comments = Collections.emptyList();
		if (pr.getCommentsCount() > 0) {
			comments = pr.getComments();
		}

		List<GHPullRequestReviewComment> commitComments = Collections.emptyList();
		if (pr.getReviewComments() > 0) {
			commitComments = pr.listReviewComments().toList();
		}

		if (pr.getCommits() > 0) {

			List<GHPullRequestCommitDetail> commits = pr.listCommits().toList();
			List<RepositoryCommit> repComposits = new ArrayList<>(commits.size());
			for (GHPullRequestCommitDetail commit : commits) {
				RepositoryCommit repComposite = new RepositoryCommit();

				repComposite.setSha(commit.getSha());
				repComposite.setAuthor(commit.getCommit().getAuthor());
				repComposite.setCommitter(commit.getCommit().getCommitter());
				GHCommit fullCommit = repo.getCommit(commit.getSha());
				repComposite.setFiles(fullCommit.listFiles().toList());
				repComposite.setCommit(fullCommit);

				repComposits.add(repComposite);
			}
			prComp.setCommits(repComposits);
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
		return appendPulls(repositoryUrl) + "/" + taskId; //$NON-NLS-1$
	}

	public static GHRepository createClient(TaskRepository taskRepository) throws IOException {
		return getRepo(GithubApi.createGithubClient(taskRepository), taskRepository);
	}

	public static GHRepository getRepo(GithubApi client, TaskRepository repository) throws IOException {
		return client.getRepo(stripPulls(repository.getRepositoryUrl()));
	}
}
