/*******************************************************************************
 * Copyright (c) 2011, 2020 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green <david.green@tasktop.com> - initial contribution
 *     Christian Trutz <christian.trutz@gmail.com> - initial contribution
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.core.issue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.internal.github.core.QueryUtils;
import org.eclipse.mylyn.internal.github.core.RepositoryConnector;
import org.eclipse.mylyn.internal.github.egit.github.core.RepositoryId;
import org.eclipse.mylyn.internal.github.egit.github.core.client.IssueService;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssue.PullRequest;
import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHIssueQueryBuilder.ForRepository;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHMilestone;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHubAbuseLimitHandler;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.connector.GitHubConnector;
import org.kohsuke.github.connector.GitHubConnectorResponse;
import org.kohsuke.github.internal.DefaultGitHubConnector;

/**
 * GitHub issue repository connector.
 */
public class IssueConnector extends RepositoryConnector {

	/**
	 * GitHub kind.
	 */
	public static final String KIND = GitHub.CONNECTOR_KIND;

	/**
	 * Get repository label for id provider
	 *
	 * @param repo
	 * @return label
	 */
	public static String getRepositoryLabel(GHRepository repo) {
		return repo.getFullName() + Messages.IssueConnector_LabelIssues;
	}

	public static String getRepositoryLabel(RepositoryId repo) {
		return repo.generateId() + Messages.IssueConnector_LabelIssues;
	}

	/**
	 * Creates an issue task repository.
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
		String url = repo.getHtmlUrl().toString();
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
		repository.setCategory(TaskRepository.CATEGORY_BUGS);
		repository.setProperty(IRepositoryConstants.PROPERTY_USE_TOKEN, Boolean.toString(isToken));
		return repository;
	}

	/**
	 * Create client for repository
	 *
	 * @param repository
	 * @return client
	 * @throws IOException
	 */
	public static GHRepository createClient(TaskRepository repository) throws IOException {
		return createClient(repository.getRepositoryUrl(), repository.getCredentials(AuthenticationType.REPOSITORY));
	}

	public static org.kohsuke.github.GitHub createGithubClient(AuthenticationCredentials credentials) throws IOException {
		GitHubConnector connector = DefaultGitHubConnector.create();
		GitHubAbuseLimitHandler limitHandler = new GitHubAbuseLimitHandler() {
			@Override
			public void onError(GitHubConnectorResponse connectorResponse) throws IOException {
				throw new IOException(
						"GitHub abuse limit exceeded"); //$NON-NLS-1$
			}
		};
		GitHubBuilder github = new GitHubBuilder() //
				.withConnector(connector) //
				.withAbuseLimitHandler(limitHandler);

		if (!credentials.getPassword().isEmpty()) {
			github.withOAuthToken(credentials.getPassword(), ""); //$NON-NLS-1$
		}
		org.kohsuke.github.GitHub client = github.build();
		return client;
	}

	public static GHRepository createClient(String url, AuthenticationCredentials credentials) throws IOException {
		org.kohsuke.github.GitHub client = createGithubClient(credentials);
		try {
			URI uri = new URI(url);
			GHRepository repo = client.getRepository(uri.getPath().substring(1));
			return repo;
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
	}

	/**
	 * GitHub specific {@link AbstractTaskDataHandler}.
	 */
	private final IssueTaskDataHandler taskDataHandler;

	private final Map<TaskRepository, List<GHLabel>> repositoryLabels = Collections
			.synchronizedMap(new HashMap<TaskRepository, List<GHLabel>>());

	private final Map<TaskRepository, List<GHMilestone>> repositoryMilestones = Collections
			.synchronizedMap(new HashMap<TaskRepository, List<GHMilestone>>());

	/**
	 * Create GitHub issue repository connector
	 */
	public IssueConnector() {
		taskDataHandler = new IssueTaskDataHandler(this);
	}

	/**
	 * Refresh labels for repository
	 *
	 * @param repository
	 * @return labels
	 * @throws CoreException
	 * @throws IOException
	 */
	public List<GHLabel> refreshLabels(TaskRepository repository) throws CoreException {
		Assert.isNotNull(repository, "Repository cannot be null"); //$NON-NLS-1$
		try {
			GHRepository client = createClient(repository);
			List<GHLabel> labels = client.listLabels().toList();
			repositoryLabels.put(repository, labels);
			return labels;
		} catch (IOException e) {
			throw new CoreException(GitHub.createWrappedStatus(e));
		}
	}

	/**
	 * Get labels for task repository.
	 *
	 * @param repository
	 * @return non-null but possibly empty list of labels
	 */
	public List<GHLabel> getLabels(TaskRepository repository) {
		Assert.isNotNull(repository, "Repository cannot be null"); //$NON-NLS-1$
		List<GHLabel> labels = new LinkedList<>();
		List<GHLabel> cached = repositoryLabels.get(repository);
		if (cached != null) {
			labels.addAll(cached);
		}
		return labels;
	}

	/**
	 * Are there cached labels for the specified task repository?
	 *
	 * @param repository
	 * @return true if contains labels, false otherwise
	 */
	public boolean hasCachedLabels(TaskRepository repository) {
		return repositoryLabels.containsKey(repository);
	}

	/**
	 * Refresh milestones for repository
	 *
	 * @param repository
	 * @return milestones
	 * @throws CoreException
	 */
	public List<GHMilestone> refreshMilestones(TaskRepository repository) throws CoreException {
		Assert.isNotNull(repository, "Repository cannot be null"); //$NON-NLS-1$
		try {
			GHRepository repo = createClient(repository);
			List<GHMilestone> milestones = new LinkedList<>(
					repo.listMilestones(GHIssueState.OPEN).toList());
			milestones.addAll(repo.listMilestones(GHIssueState.CLOSED).toList());
			repositoryMilestones.put(repository, milestones);
			return milestones;
		} catch (IOException e) {
			throw new CoreException(GitHub.createWrappedStatus(e));
		}
	}

	/**
	 * Get milestones for task repository.
	 *
	 * @param repository
	 * @return non-null but possibly empty list of milestones
	 */
	public List<GHMilestone> getMilestones(TaskRepository repository) {
		Assert.isNotNull(repository, "Repository cannot be null"); //$NON-NLS-1$
		List<GHMilestone> milestones = new LinkedList<>();
		List<GHMilestone> cached = repositoryMilestones.get(repository);
		if (cached != null) {
			milestones.addAll(cached);
		}
		return milestones;
	}

	/**
	 * Are there cached milestones for the specified task repository?
	 *
	 * @param repository
	 * @return true if contains milestones, false otherwise
	 */
	public boolean hasCachedMilestones(TaskRepository repository) {
		return repositoryMilestones.containsKey(repository);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return always {@code true}
	 */
	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return always {@code true}
	 */
	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return true;
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
		return Messages.IssueConnector_LabelConnector;
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

		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.IssueConector_TaskQuerying, statuses.size() * 100);
		try {
			GHRepository repo = createClient(repository);

			ForRepository queryBuilder = repo.queryIssues();

			String mentions = query.getAttribute(IssueService.FILTER_MENTIONED);
			if (mentions != null) {
				queryBuilder.mentioned(mentions);
			}

			String assignee = query.getAttribute(IssueService.FILTER_ASSIGNEE);
			if (assignee != null) {
				queryBuilder.assignee(assignee);
			}

			String milestone = query.getAttribute(IssueService.FILTER_MILESTONE);
			if (milestone != null) {
				queryBuilder.milestone(milestone);
			}

			List<String> labels = QueryUtils.getAttributes(
					IssueService.FILTER_LABELS, query);
			if (!labels.isEmpty()) {
				StringBuilder labelsQuery = new StringBuilder();
				for (String label : labels) {
					queryBuilder.label(label);
				}
			}

			String owner = repo.getOwner().getName();
			String name = repo.getName();
			for (String status : statuses) {
				if ("open".equalsIgnoreCase(status)) { //$NON-NLS-1$
					queryBuilder.state(GHIssueState.OPEN);
				} else if ("closed".equalsIgnoreCase(status)) { //$NON-NLS-1$
					queryBuilder.state(GHIssueState.CLOSED);
				} else if ("all".equalsIgnoreCase(status)) { //$NON-NLS-1$
					queryBuilder.state(GHIssueState.ALL);
				}

				SubMonitor statusMonitor = subMonitor.split(100);
				statusMonitor.setTaskName(status + Messages.IssueConnector_LabelIssues);
//				List<Issue> issues = service.getIssues(repo.getOwner(), repoId.getName(), filterData);
				List<GHIssue> issues = queryBuilder.list().toList();
				statusMonitor.checkCanceled();

// collect task data
				statusMonitor.setWorkRemaining(issues.size());
				for (GHIssue issue : issues) {
					List<GHIssueComment> comments = null;
					if (issue.getCommentsCount() > 0) {
						comments = issue.getComments();
					}
					TaskData taskData = taskDataHandler.createTaskData(repository, statusMonitor, owner, name,
							issue, comments);
					collector.accept(taskData);
				}
				statusMonitor.split(1);
				statusMonitor.done();
			}
		} catch (IOException e) {
			result = GitHub.createWrappedStatus(e);
		} finally {
			subMonitor.done();
		}
		return result;
	}

	private boolean isPullRequest(GHIssue issue) {
		PullRequest request = issue.getPullRequest();
		return request != null && request.getDiffUrl() != null;
	}

	@Override
	public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		RepositoryId repo = GitHub.getRepository(repository.getRepositoryUrl());

		try {
			GHRepository client = createClient(repository);
			GHIssue issue = client.getIssue(Integer.parseInt(taskId));
			if (isPullRequest(issue)) {
				return null;
			}
			List<GHIssueComment> comments = null;
			if (issue.getCommentsCount() > 0) {
				comments = issue.getComments();
			}
			return taskDataHandler.createTaskData(repository, monitor, repo.getOwner(), repo.getName(), issue,
					comments);
		} catch (IOException e) {
			throw new CoreException(GitHub.createWrappedStatus(e));
		}
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String taskFullUrl) {
		if (taskFullUrl != null) {
			Matcher matcher = Pattern.compile(
					"(http://.+?)/issues/([^/]+)").matcher(taskFullUrl); //$NON-NLS-1$
			if (matcher.matches()) {
				return matcher.group(1);
			}
		}
		return null;
	}

	@Override
	public String getTaskIdFromTaskUrl(String taskFullUrl) {
		if (taskFullUrl != null) {
			Matcher matcher = Pattern.compile(".+?/issues/([^/]+)").matcher(taskFullUrl); //$NON-NLS-1$
			if (matcher.matches()) {
				return matcher.group(1);
			}
		}
		return null;
	}

	@Override
	public String getTaskUrl(String repositoryUrl, String taskId) {
		return repositoryUrl + "/issues/" + taskId; //$NON-NLS-1$
	}

	@Override
	public void updateRepositoryConfiguration(TaskRepository taskRepository, IProgressMonitor monitor)
			throws CoreException {
		IProgressMonitor m = Policy.monitorFor(monitor);
		m.beginTask("", 2); //$NON-NLS-1$
		m.setTaskName(Messages.IssueConnector_TaskUpdatingLabels);
		refreshLabels(taskRepository);
		m.worked(1);
		m.setTaskName(Messages.IssueConnector_TaskUpdatingMilestones);
		refreshMilestones(taskRepository);
		m.done();
	}

}
