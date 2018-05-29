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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.internal.github.core.GitHubTaskDataHandler;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

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
	 * Create task data
	 *
	 * @param repository
	 * @param monitor
	 * @param user
	 * @param project
	 * @param issue
	 * @return task data
	 */
	public TaskData createTaskData(TaskRepository repository,
			IProgressMonitor monitor, String user, String project, Issue issue) {

		String key = Integer.toString(issue.getNumber());
		TaskData data = new TaskData(getAttributeMapper(repository),
				IssueConnector.KIND, repository.getRepositoryUrl(), key);
		data.setVersion(DATA_VERSION);

		createOperations(data, issue);

		createAttribute(data, IssueAttribute.KEY.getMetadata(), key);
		createAttribute(data, IssueAttribute.TITLE.getMetadata(),
				issue.getTitle());
		createAttribute(data, IssueAttribute.BODY.getMetadata(),
				issue.getBody());
		createAttribute(data, IssueAttribute.STATUS.getMetadata(),
				issue.getState());
		createAttribute(data, IssueAttribute.CREATION_DATE.getMetadata(),
				issue.getCreatedAt());
		createAttribute(data, IssueAttribute.MODIFICATION_DATE.getMetadata(),
				issue.getUpdatedAt());
		createAttribute(data, IssueAttribute.CLOSED_DATE.getMetadata(),
				issue.getClosedAt());

		User reporter = issue.getUser();
		createAttribute(data, IssueAttribute.REPORTER.getMetadata(), reporter,
				repository);
		String reporterGravatar = reporter != null ? reporter.getAvatarUrl()
				: null;
		createAttribute(data, IssueAttribute.REPORTER_GRAVATAR.getMetadata(),
				reporterGravatar);

		User assignee = issue.getAssignee();
		createAttribute(data, IssueAttribute.ASSIGNEE.getMetadata(), assignee,
				repository);
		String assigneeGravatar = assignee != null ? assignee.getAvatarUrl()
				: null;
		createAttribute(data, IssueAttribute.ASSIGNEE_GRAVATAR.getMetadata(),
				assigneeGravatar);

		createAttribute(data, IssueAttribute.COMMENT_NEW.getMetadata());

		createLabels(repository, data, issue);

		createMilestones(repository, data, issue);

		return data;
	}

	private void createMilestones(TaskRepository repository, TaskData data,
			Issue issue) {
		Milestone current = issue.getMilestone();
		String number = current != null ? Integer.toString(current.getNumber())
				: MILESTONE_NONE_KEY;
		TaskAttribute milestoneAttribute = createAttribute(data,
				IssueAttribute.MILESTONE.getMetadata(), number);

		if (!connector.hasCachedMilestones(repository))
			try {
				connector.refreshMilestones(repository);
			} catch (CoreException ignore) {
				// Ignored
			}

		List<Milestone> cachedMilestones = connector.getMilestones(repository);
		milestoneAttribute.putOption(MILESTONE_NONE_KEY,
				Messages.IssueAttribute_MilestoneNone);
		for (Milestone milestone : cachedMilestones)
			milestoneAttribute.putOption(
					Integer.toString(milestone.getNumber()),
					milestone.getTitle());
	}

	private void createLabels(TaskRepository repository, TaskData data,
			Issue issue) {
		TaskAttribute labels = createAttribute(data, IssueAttribute.LABELS,
				issue.getLabels());

		if (!connector.hasCachedLabels(repository))
			try {
				connector.refreshLabels(repository);
			} catch (CoreException ignore) {
				// Ignored
			}

		List<Label> cachedLabels = connector.getLabels(repository);
		for (Label label : cachedLabels)
			labels.putOption(label.getName(), label.getName());
	}

	private void createOperations(TaskData data, Issue issue) {
		createOperationAttribute(data);

		if (!data.isNew()) {
			String state = issue.getState();
			if (state != null) {
				addOperation(data, issue, IssueOperation.LEAVE, true);
				if (state.equals(IssueService.STATE_OPEN))
					addOperation(data, issue, IssueOperation.CLOSE, false);
				else if (state.equals(IssueService.STATE_CLOSED))
					addOperation(data, issue, IssueOperation.REOPEN, false);
			}
		}
	}

	private void addOperation(TaskData data, Issue issue,
			IssueOperation operation, boolean isDefault) {
		String id = operation.getId();
		String label = createOperationLabel(issue, operation);
		addOperation(data, id, label, isDefault);
	}

	private String createOperationLabel(Issue issue, IssueOperation operation) {
		return operation == IssueOperation.LEAVE ? operation.getLabel()
				+ issue.getState() : operation.getLabel();
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
	 */
	public TaskData createTaskData(TaskRepository repository,
			IProgressMonitor monitor, String user, String project, Issue issue,
			List<Comment> comments) {
		TaskData taskData = createTaskData(repository, monitor, user, project,
				issue);
		taskData.setPartial(false);

		addComments(taskData.getRoot(), comments, repository);

		return taskData;
	}

	private Issue createIssue(TaskData taskData) {
		Issue issue = new Issue();
		if (!taskData.isNew())
			issue.setNumber(Integer.parseInt(taskData.getTaskId()));

		issue.setBody(getAttributeValue(taskData,
				IssueAttribute.BODY.getMetadata()));
		issue.setTitle(getAttributeValue(taskData,
				IssueAttribute.TITLE.getMetadata()));

		String assigneeValue = getAttributeValue(taskData,
				IssueAttribute.ASSIGNEE.getMetadata());
		if (assigneeValue != null) {
			if (assigneeValue.trim().length() == 0)
				assigneeValue = null;
			User assignee = new User().setLogin(assigneeValue);
			issue.setAssignee(assignee);
		}

		String milestoneValue = getAttributeValue(taskData,
				IssueAttribute.MILESTONE.getMetadata());
		if (milestoneValue != null) {
			Milestone milestone = new Milestone();
			if (milestoneValue.length() > 0)
				milestone.setNumber(Integer.parseInt(milestoneValue));
			issue.setMilestone(milestone);
		}
		return issue;
	}

	private TaskAttribute createAttribute(TaskData data,
			IssueAttribute attribute, List<Label> values) {
		TaskAttribute attr = createAttribute(data, attribute.getMetadata());
		if (values != null) {
			List<String> labels = new ArrayList<String>(values.size());
			for (Label label : values)
				labels.add(label.getName());
			data.getAttributeMapper().setValues(attr, labels);
		}
		return attr;
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, TaskData data,
			ITaskMapping initializationData, IProgressMonitor monitor)
			throws CoreException {
		data.setVersion(DATA_VERSION);
		for (IssueAttribute attr : IssueAttribute.values())
			if (attr.getMetadata().isInitTask())
				createAttribute(data, attr.getMetadata(), (String) null);
		Issue dummy = new Issue();
		createLabels(repository, data, dummy);
		createMilestones(repository, data, dummy);
		return true;
	}

	/**
	 * Create any new labels that have been added to the issue and set the
	 * issues labels to the current value of labels attribute.
	 *
	 * @param user
	 * @param repo
	 * @param client
	 * @param repository
	 * @param data
	 * @param oldAttributes
	 * @param issue
	 */
	protected void updateLabels(String user, String repo, GitHubClient client,
			TaskRepository repository, TaskData data,
			Set<TaskAttribute> oldAttributes, Issue issue) {
		TaskAttribute labelsAttribute = data.getRoot().getAttribute(
				IssueAttribute.LABELS.getMetadata().getId());
		if (oldAttributes.contains(labelsAttribute) || data.isNew()) {
			LabelService labelService = new LabelService(client);

			if (!connector.hasCachedLabels(repository))
				try {
					connector.refreshLabels(repository);
				} catch (CoreException ignore) {
					// Ignore
				}
			List<Label> currentLabels = connector.getLabels(repository);
			List<Label> newLabels = new LinkedList<Label>();
			List<Label> labels = new LinkedList<Label>();
			for (String value : labelsAttribute.getValues()) {
				Label label = new Label().setName(value);
				if (!currentLabels.contains(label))
					newLabels.add(label);
				labels.add(label);
			}
			issue.setLabels(labels);
			for (Label label : newLabels)
				try {
					labelService.createLabel(user, repo, label);
				} catch (IOException e) {
					// TODO detect failure and handle label already created
				}

			if (!newLabels.isEmpty())
				try {
					connector.refreshLabels(repository);
				} catch (CoreException ignore) {
					// Ignore
				}
		}
	}

	@Override
	public RepositoryResponse postTaskData(TaskRepository repository,
			TaskData taskData, Set<TaskAttribute> oldAttributes,
			IProgressMonitor monitor) throws CoreException {
		String taskId = taskData.getTaskId();
		Issue issue = createIssue(taskData);
		RepositoryId repo = GitHub.getRepository(repository.getRepositoryUrl());
		try {
			GitHubClient client = IssueConnector.createClient(repository);
			boolean collaborator = isCollaborator(client, repo);
			if (collaborator)
				updateLabels(repo.getOwner(), repo.getName(), client,
						repository, taskData, oldAttributes, issue);
			IssueService service = new IssueService(client);
			if (taskData.isNew()) {
				issue.setState(IssueService.STATE_OPEN);
				issue = service.createIssue(repo.getOwner(), repo.getName(),
						issue);
				taskId = Integer.toString(issue.getNumber());
			} else {
				// Handle new comment
				String comment = getAttributeValue(taskData,
						IssueAttribute.COMMENT_NEW.getMetadata());
				if (comment != null && comment.length() > 0)
					service.createComment(repo.getOwner(), repo.getName(),
							taskId, comment);

				boolean reporter = attributeMatchesUser(client,
						IssueAttribute.REPORTER.getMetadata(), taskData);
				if (collaborator || reporter) {
					// Handle state change
					TaskAttribute operationAttribute = taskData.getRoot()
							.getAttribute(TaskAttribute.OPERATION);
					if (operationAttribute != null) {
						IssueOperation operation = IssueOperation
								.fromId(operationAttribute.getValue());
						if (operation == IssueOperation.REOPEN)
							issue.setState(IssueService.STATE_OPEN);
						else if (operation == IssueOperation.CLOSE)
							issue.setState(IssueService.STATE_CLOSED);
					}
					service.editIssue(repo.getOwner(), repo.getName(), issue);
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
