/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
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
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueBuilder;
import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHMilestone;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;

/**
 * GitHub issue task data handler
 */
public class IssueTaskDataHandler extends GitHubTaskDataHandler {

	private static final String DATA_VERSION = "1"; //$NON-NLS-1$

	private static final String MILESTONE_NONE_KEY = "0"; //$NON-NLS-1$

	private final IssueConnector connector;

	/**
	 * Create GitHub issue task data handler for connector
	 *
	 * @param connector
	 */
	public IssueTaskDataHandler(IssueConnector connector) {
		this.connector = connector;
	}

	/**
	 * Create task data for issue
	 *
	 * @param repository
	 * @param monitor
	 * @param user
	 * @param project
	 * @param issue
	 * @param comments
	 * @return task data
	 * @throws IOException
	 */
	public TaskData createTaskData(TaskRepository repository, IProgressMonitor monitor, String user, String project,
			GHIssue issue, List<GHIssueComment> comments) throws IOException {
		TaskData taskData = createTaskData(repository, monitor, user, project, issue);
		taskData.setPartial(false);

		addComments(taskData.getRoot(), comments, repository);

		return taskData;
	}

	/**
	 * Create task data
	 *
	 * @param repository
	 * @param monitor
	 * @param user
	 * @param project
	 * @param issue
	 * @return task data
	 * @throws IOException
	 */
	public TaskData createTaskData(TaskRepository repository, IProgressMonitor statusMonitor, String owner, String name,
			GHIssue issue) throws IOException {

		String key = Integer.toString(issue.getNumber());
		TaskData data = new TaskData(getAttributeMapper(repository), IssueConnector.KIND, repository.getRepositoryUrl(),
				key);
		data.setVersion(DATA_VERSION);

		createOperations(data, issue);

		createAttribute(data, IssueAttribute.KEY.getMetadata(), key);
		createAttribute(data, IssueAttribute.TITLE.getMetadata(), issue.getTitle());
		createAttribute(data, IssueAttribute.BODY.getMetadata(), issue.getBody());
		createAttribute(data, IssueAttribute.STATUS.getMetadata(), issue.getState().toString());
		createAttribute(data, IssueAttribute.CREATION_DATE.getMetadata(), Date.from(issue.getCreatedAt()));
		createAttribute(data, IssueAttribute.MODIFICATION_DATE.getMetadata(), Date.from(issue.getUpdatedAt()));
		if (issue.getClosedAt() != null) {
			createAttribute(data, IssueAttribute.CLOSED_DATE.getMetadata(), Date.from(issue.getClosedAt()));
		}

		GHUser reporter = issue.getUser();
		createAttribute(data, IssueAttribute.REPORTER.getMetadata(), reporter, repository);
		String reporterGravatar = reporter != null ? reporter.getAvatarUrl() : null;
		createAttribute(data, IssueAttribute.REPORTER_GRAVATAR.getMetadata(), reporterGravatar);

		GHUser assignee = issue.getAssignee();
		createAttribute(data, IssueAttribute.ASSIGNEE.getMetadata(), assignee, repository);
		String assigneeGravatar = assignee != null ? assignee.getAvatarUrl() : null;
		createAttribute(data, IssueAttribute.ASSIGNEE_GRAVATAR.getMetadata(), assigneeGravatar);

		createAttribute(data, IssueAttribute.COMMENT_NEW.getMetadata());

		createLabels(repository, data, issue);

		createMilestones(repository, data, issue);

		return data;
	}

	private void createMilestones(TaskRepository repository, TaskData data, GHIssue issue) {
		GHMilestone current = issue.getMilestone();
		String number = current != null ? Integer.toString(current.getNumber()) : MILESTONE_NONE_KEY;
		TaskAttribute milestoneAttribute = createAttribute(data, IssueAttribute.MILESTONE.getMetadata(), number);

		if (!connector.hasCachedMilestones(repository)) {
			try {
				connector.refreshMilestones(repository);
			} catch (CoreException ignore) {
				// Ignored
			}
		}

		List<GHMilestone> cachedMilestones = connector.getMilestones(repository);
		milestoneAttribute.putOption(MILESTONE_NONE_KEY, Messages.IssueAttribute_MilestoneNone);
		for (GHMilestone milestone : cachedMilestones) {
			milestoneAttribute.putOption(
					Integer.toString(milestone.getNumber()), milestone.getTitle());
		}
	}

	private void createLabels(TaskRepository repository, TaskData data, GHIssue issue) throws IOException {
		TaskAttribute labels = createAttribute(data, IssueAttribute.LABELS, issue.getLabels());

		if (!connector.hasCachedLabels(repository)) {
			try {
				connector.refreshLabels(repository);
			} catch (CoreException ignore) {
				// Ignored
			}
		}

		List<GHLabel> cachedLabels = connector.getLabels(repository);
		for (GHLabel label : cachedLabels) {
			labels.putOption(label.getName(), label.getName());
		}
	}

	private void createOperations(TaskData data, GHIssue issue) {
		createOperationAttribute(data);

		if (!data.isNew()) {
			GHIssueState state = issue.getState();
			if (state != null) {
				addOperation(data, issue, IssueOperation.LEAVE, true);
				if (state.equals(GHIssueState.OPEN)) {
					addOperation(data, issue, IssueOperation.CLOSE, false);
				} else if (state.equals(GHIssueState.CLOSED)) {
					addOperation(data, issue, IssueOperation.REOPEN, false);
				}
			}
		}
	}

	private void addOperation(TaskData data, GHIssue issue, IssueOperation operation, boolean isDefault) {
		String id = operation.getId();
		String label = createOperationLabel(issue, operation);
		addOperation(data, id, label, isDefault);
	}

	private String createOperationLabel(GHIssue issue, IssueOperation operation) {
		return operation == IssueOperation.LEAVE ? operation.getLabel() + issue.getState() : operation.getLabel();
	}

	/**
	 * Create or update issue from task data
	 *
	 * @param repo
	 * @param taskData
	 * @return
	 * @throws IOException
	 */
	private GHIssue createIssue(GHRepository repo, TaskData taskData) throws IOException {
		GHIssueBuilder issueBuilder = repo.createIssue(getAttributeValue(taskData, IssueAttribute.TITLE.getMetadata()));
		if (!taskData.isNew()) {
			GHIssue issue = repo.getIssue(Integer.parseInt(taskData.getTaskId()));

			String milestoneValue = getAttributeValue(taskData, IssueAttribute.MILESTONE.getMetadata());
			if (milestoneValue != null) {
				try {
					repo.queryMilestones()
							.list()
							.toList()
							.stream()
							.filter(m -> Integer.toString(m.getNumber()).equals(milestoneValue))
							.findFirst()
							.ifPresent(t -> {
								try {
									issue.setMilestone(t);
								} catch (IOException e) {
									throw new UncheckedIOException(e);
								}
							});
				} catch (UncheckedIOException e) {
					throw e.getCause();
				}
			}

			return issue;
		}
		issueBuilder.body(getAttributeValue(taskData, IssueAttribute.BODY.getMetadata()));

		String assigneeValue = getAttributeValue(taskData, IssueAttribute.ASSIGNEE.getMetadata());
		if (assigneeValue != null) {
			if (assigneeValue.trim().length() == 0) {
				assigneeValue = null;
			}
			issueBuilder.assignee(assigneeValue);
		}

		String milestoneValue = getAttributeValue(taskData, IssueAttribute.MILESTONE.getMetadata());
		if (milestoneValue != null) {
			repo.queryMilestones()
			.list()
			.toList()
			.stream()
			.filter(m -> Integer.toString(m.getNumber()).equals(milestoneValue))
			.findFirst()
			.ifPresent(issueBuilder::milestone);
		}
		return issueBuilder.create();
	}

	private TaskAttribute createAttribute(TaskData data, IssueAttribute attribute, Collection<GHLabel> collection) {
		TaskAttribute attr = createAttribute(data, attribute.getMetadata());
		if (collection != null) {
			List<String> labels = new ArrayList<>(collection.size());
			for (GHLabel label : collection) {
				labels.add(label.getName());
			}
			data.getAttributeMapper().setValues(attr, labels);
		}
		return attr;
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, TaskData data, ITaskMapping initializationData,
			IProgressMonitor monitor) throws CoreException {
		data.setVersion(DATA_VERSION);
		try {
			for (IssueAttribute attr : IssueAttribute.values()) {
				if (attr.getMetadata().isInitTask()) {
					createAttribute(data, attr.getMetadata(), (String) null);
				}
			}
			GHIssue dummy = new GHIssue();
			createLabels(repository, data, dummy);
			createMilestones(repository, data, dummy);
			return true;
		} catch (IOException e) {
			throw new CoreException(GitHub.createWrappedStatus(e));
		}
	}

	/**
	 * Create any new labels that have been added to the issue and set the issues labels to the current value of labels attribute.
	 *
	 * @param user
	 * @param repo
	 * @param client
	 * @param repository
	 * @param data
	 * @param oldAttributes
	 * @param issue
	 * @throws IOException
	 */
	protected void updateLabels(GHRepository repo, TaskRepository repository, TaskData data,
			Set<TaskAttribute> oldAttributes, GHIssue issue) throws IOException {
		TaskAttribute labelsAttribute = data.getRoot()
				.getAttribute(
						IssueAttribute.LABELS.getMetadata().getId());
		if (oldAttributes.contains(labelsAttribute) || data.isNew()) {
			if (!connector.hasCachedLabels(repository)) {
				try {
					connector.refreshLabels(repository);
				} catch (CoreException ignore) {
					// Ignore
				}
			}
			Collection<GHLabel> currentLabels = issue.getLabels();
			Collection<String> newLabels = new ArrayList<>();
			for (String value : labelsAttribute.getValues()) {
				boolean found = false;
				for (GHLabel ghLabel : currentLabels) {
					if (ghLabel.getName().equals(value)) {
						found = true;
					}
				}
				if (!found) {
					newLabels.add(value);
				}
			}

			issue.addLabels(newLabels.toArray(new String[0]));
		}
	}

	@Override
	public RepositoryResponse postTaskData(TaskRepository repository, TaskData taskData,
			Set<TaskAttribute> oldAttributes, IProgressMonitor monitor) throws CoreException {
		String taskId = taskData.getTaskId();
		try {
			GithubApi client = GithubApi.createGithubClient(repository);
			GHRepository repo = client.getRepo(repository.getRepositoryUrl());
			boolean collaborator = isCollaborator(client, repo);

			GHIssue issue = createIssue(repo, taskData);

			if (collaborator) {
				updateLabels(repo, repository, taskData, oldAttributes, issue);
			}
			if (taskData.isNew()) {

				taskId = Integer.toString(issue.getNumber());
			} else {
				// Handle new comment
				String comment = getAttributeValue(taskData, IssueAttribute.COMMENT_NEW.getMetadata());
				if (comment != null && comment.length() > 0) {
					issue.comment(comment);
				}

				boolean reporter = attributeMatchesUser(client, IssueAttribute.REPORTER.getMetadata(), taskData);
				if (collaborator || reporter) {
					// Handle state change
					TaskAttribute operationAttribute = taskData.getRoot().getAttribute(TaskAttribute.OPERATION);
					if (operationAttribute != null) {
						IssueOperation operation = IssueOperation.fromId(operationAttribute.getValue());
						if (operation == IssueOperation.REOPEN) {
							issue.reopen();
						} else if (operation == IssueOperation.CLOSE) {
							issue.close();
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
