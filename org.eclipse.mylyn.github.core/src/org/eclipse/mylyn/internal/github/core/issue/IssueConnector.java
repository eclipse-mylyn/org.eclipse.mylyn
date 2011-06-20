/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green <david.green@tasktop.com> - initial contribution
 *     Christian Trutz <christian.trutz@gmail.com> - initial contribution
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.core.issue;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
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
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.egit.github.core.service.MilestoneService;
import org.eclipse.egit.github.core.util.LabelComparator;
import org.eclipse.egit.github.core.util.MilestoneComparator;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.internal.github.core.QueryUtils;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

/**
 * GitHub connector.
 */
public class IssueConnector extends AbstractRepositoryConnector {

	/**
	 * GitHub kind.
	 */
	public static final String KIND = GitHub.CONNECTOR_KIND;

	/**
	 * Create client for repository
	 * 
	 * @param repository
	 * @return client
	 */
	public static GitHubClient createClient(TaskRepository repository) {
		GitHubClient client = new GitHubClient();
		AuthenticationCredentials credentials = repository
				.getCredentials(AuthenticationType.REPOSITORY);
		if (credentials != null)
			client.setCredentials(credentials.getUserName(),
					credentials.getPassword());
		return client;
	}

	/**
	 * GitHub specific {@link AbstractTaskDataHandler}.
	 */
	private final IssueTaskDataHandler taskDataHandler;

	private final Map<TaskRepository, List<Label>> repositoryLabels = Collections
			.synchronizedMap(new HashMap<TaskRepository, List<Label>>());

	private final Map<TaskRepository, List<Milestone>> repositoryMilestones = Collections
			.synchronizedMap(new HashMap<TaskRepository, List<Milestone>>());

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
	 */
	public List<Label> refreshLabels(TaskRepository repository)
			throws CoreException {
		Assert.isNotNull(repository, "Repository cannot be null"); //$NON-NLS-1$
		RepositoryId repo = GitHub.getRepository(repository.getRepositoryUrl());
		GitHubClient client = createClient(repository);
		LabelService service = new LabelService(client);
		try {
			List<Label> labels = service.getLabels(repo.getOwner(),
					repo.getName());
			Collections.sort(labels, new LabelComparator());
			this.repositoryLabels.put(repository, labels);
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
	public List<Label> getLabels(TaskRepository repository) {
		Assert.isNotNull(repository, "Repository cannot be null"); //$NON-NLS-1$
		List<Label> labels = new LinkedList<Label>();
		List<Label> cached = this.repositoryLabels.get(repository);
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
		return this.repositoryLabels.containsKey(repository);
	}

	/**
	 * Refresh milestones for repository
	 * 
	 * @param repository
	 * @return milestones
	 * @throws CoreException
	 */
	public List<Milestone> refreshMilestones(TaskRepository repository)
			throws CoreException {
		Assert.isNotNull(repository, "Repository cannot be null"); //$NON-NLS-1$
		RepositoryId repo = GitHub.getRepository(repository.getRepositoryUrl());
		GitHubClient client = createClient(repository);
		MilestoneService service = new MilestoneService(client);
		try {
			List<Milestone> milestones = new LinkedList<Milestone>();
			milestones.addAll(service.getMilestones(repo.getOwner(),
					repo.getName(), IssueService.STATE_OPEN));
			milestones.addAll(service.getMilestones(repo.getOwner(),
					repo.getName(), IssueService.STATE_CLOSED));
			Collections.sort(milestones, new MilestoneComparator());
			this.repositoryMilestones.put(repository, milestones);
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
	public List<Milestone> getMilestones(TaskRepository repository) {
		Assert.isNotNull(repository, "Repository cannot be null"); //$NON-NLS-1$
		List<Milestone> milestones = new LinkedList<Milestone>();
		List<Milestone> cached = this.repositoryMilestones.get(repository);
		if (cached != null)
			milestones.addAll(cached);
		return milestones;
	}

	/**
	 * Are there cached milestones for the specified task repository?
	 * 
	 * @param repository
	 * @return true if contains milestones, false otherwise
	 */
	public boolean hasCachedMilestones(TaskRepository repository) {
		return this.repositoryMilestones.containsKey(repository);
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
		return this.taskDataHandler;
	}

	@Override
	public IStatus performQuery(TaskRepository repository,
			IRepositoryQuery query, TaskDataCollector collector,
			ISynchronizationSession session, IProgressMonitor monitor) {
		IStatus result = Status.OK_STATUS;
		List<String> statuses = QueryUtils.getAttributes(
				IssueService.FILTER_STATE, query);

		monitor.beginTask(Messages.IssueConector_TaskQuerying, statuses.size());
		try {
			RepositoryId repo = GitHub.getRepository(repository
					.getRepositoryUrl());

			GitHubClient client = createClient(repository);
			IssueService service = new IssueService(client);

			Map<String, String> filterData = new HashMap<String, String>();
			String mentions = query.getAttribute(IssueService.FILTER_MENTIONED);
			if (mentions != null)
				filterData.put(IssueService.FILTER_MENTIONED, mentions);

			String assignee = query.getAttribute(IssueService.FILTER_ASSIGNEE);
			if (assignee != null)
				filterData.put(IssueService.FILTER_ASSIGNEE, assignee);

			String milestone = query
					.getAttribute(IssueService.FILTER_MILESTONE);
			if (milestone != null)
				filterData.put(IssueService.FILTER_MILESTONE, milestone);

			List<String> labels = QueryUtils.getAttributes(
					IssueService.FILTER_LABELS, query);
			if (!labels.isEmpty()) {
				StringBuilder labelsQuery = new StringBuilder();
				for (String label : labels)
					labelsQuery.append(label).append(',');
				filterData.put(IssueService.FILTER_LABELS,
						labelsQuery.toString());
			}

			String owner = repo.getOwner();
			String name = repo.getName();
			for (String status : statuses) {
				filterData.put(IssueService.FILTER_STATE, status);
				List<Issue> issues = service.getIssues(repo.getOwner(),
						repo.getName(), filterData);

				// collect task data
				for (Issue issue : issues) {
					List<Comment> comments = null;
					if (issue.getComments() > 0)
						comments = service.getComments(owner, name,
								Integer.toString(issue.getNumber()));
					TaskData taskData = taskDataHandler.createTaskData(
							repository, monitor, owner, name, issue, comments);
					collector.accept(taskData);
				}
				monitor.worked(1);
			}
		} catch (IOException e) {
			result = GitHub.createWrappedStatus(e);
		}

		monitor.done();
		return result;
	}

	@Override
	public TaskData getTaskData(TaskRepository repository, String taskId,
			IProgressMonitor monitor) throws CoreException {
		RepositoryId repo = GitHub.getRepository(repository.getRepositoryUrl());

		try {
			GitHubClient client = createClient(repository);
			IssueService service = new IssueService(client);
			Issue issue = service.getIssue(repo.getOwner(), repo.getName(),
					taskId);
			List<Comment> comments = null;
			if (issue.getComments() > 0) {
				comments = service.getComments(repo.getOwner(), repo.getName(),
						taskId);
			}
			return taskDataHandler.createTaskData(repository, monitor,
					repo.getOwner(), repo.getName(), issue, comments);
		} catch (IOException e) {
			throw new CoreException(GitHub.createWrappedStatus(e));
		}
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String taskFullUrl) {
		if (taskFullUrl != null) {
			Matcher matcher = Pattern.compile(
					"(http://.+?)/issues/issue/([^/]+)").matcher(taskFullUrl); //$NON-NLS-1$
			if (matcher.matches()) {
				return matcher.group(1);
			}
		}
		return null;
	}

	@Override
	public String getTaskIdFromTaskUrl(String taskFullUrl) {
		if (taskFullUrl != null) {
			Matcher matcher = Pattern
					.compile(".+?/issues/issue/([^/]+)").matcher(taskFullUrl); //$NON-NLS-1$
			if (matcher.matches()) {
				return matcher.group(1);
			}
		}
		return null;
	}

	@Override
	public String getTaskUrl(String repositoryUrl, String taskId) {
		return repositoryUrl + "/issues/issue/" + taskId; //$NON-NLS-1$
	}

	@Override
	public void updateRepositoryConfiguration(TaskRepository taskRepository,
			IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask("", 2); //$NON-NLS-1$
		monitor.setTaskName(Messages.IssueConnector_TaskUpdatingLabels);
		refreshLabels(taskRepository);
		monitor.worked(1);
		monitor.setTaskName(Messages.IssueConnector_TaskUpdatingMilestones);
		refreshMilestones(taskRepository);
		monitor.done();
	}

	@Override
	public boolean hasTaskChanged(TaskRepository repository, ITask task,
			TaskData taskData) {
		Date dataDate = getTaskMapping(taskData).getModificationDate();
		Date taskDate = task.getModificationDate();
		return dataDate == null || !dataDate.equals(taskDate);
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository taskRepository,
			ITask task, TaskData taskData) {
		if (!taskData.isNew()) {
			task.setUrl(getTaskUrl(taskRepository.getUrl(),
					taskData.getTaskId()));
			String diffUrl = null;
			TaskAttribute prDiff = taskData.getRoot().getAttribute(
					IssueAttribute.PULL_REQUEST_DIFF.getId());
			if (prDiff != null) {
				diffUrl = taskData.getAttributeMapper().getValue(prDiff);
				if (diffUrl.length() == 0)
					diffUrl = null;
			}
			task.setAttribute(IssueAttribute.PULL_REQUEST_DIFF.getId(), diffUrl);
		}
		new TaskMapper(taskData).applyTo(task);
	}
}
