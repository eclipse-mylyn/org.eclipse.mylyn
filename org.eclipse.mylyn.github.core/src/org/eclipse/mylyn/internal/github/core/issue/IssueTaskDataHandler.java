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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.internal.github.core.GitHubException;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskOperation;

/**
 * GitHub issue task data handler
 */
public class IssueTaskDataHandler extends AbstractTaskDataHandler {

	private static final String DATA_VERSION = "1"; //$NON-NLS-1$
	private static final String MILESTONE_NONE_KEY = "0"; //$NON-NLS-1$
	private IssueAttributeMapper taskAttributeMapper = null;
	private final IssueConnector connector;

	/**
	 * Create GitHub issue task data handler for connector
	 * 
	 * @param connector
	 */
	public IssueTaskDataHandler(IssueConnector connector) {
		this.connector = connector;
	}

	@Override
	public TaskAttributeMapper getAttributeMapper(TaskRepository taskRepository) {
		if (this.taskAttributeMapper == null)
			this.taskAttributeMapper = new IssueAttributeMapper(taskRepository);
		return this.taskAttributeMapper;
	}

	public TaskData createTaskData(TaskRepository repository,
			IProgressMonitor monitor, String user, String project, Issue issue) {

		String key = Integer.toString(issue.getNumber());
		TaskData data = new TaskData(getAttributeMapper(repository),
				IssueConnector.KIND, repository.getRepositoryUrl(), key);
		data.setVersion(DATA_VERSION);

		createOperations(data, issue);

		createAttribute(data, IssueAttribute.KEY, key);
		createAttribute(data, IssueAttribute.TITLE, issue.getTitle());
		createAttribute(data, IssueAttribute.BODY, issue.getBody());
		createAttribute(data, IssueAttribute.STATUS, issue.getState());
		createAttribute(data, IssueAttribute.CREATION_DATE,
				issue.getCreatedAt());
		createAttribute(data, IssueAttribute.MODIFICATION_DATE,
				issue.getUpdatedAt());
		createAttribute(data, IssueAttribute.CLOSED_DATE, issue.getClosedAt());

		User reporter = issue.getUser();
		createAttribute(data, IssueAttribute.REPORTER, reporter, repository);
		String reporterGravatar = reporter != null ? reporter.getAvatarUrl()
				: null;
		createAttribute(data, IssueAttribute.REPORTER_GRAVATAR,
				reporterGravatar);

		User assignee = issue.getAssignee();
		createAttribute(data, IssueAttribute.ASSIGNEE, assignee, repository);
		String assigneeGravatar = assignee != null ? assignee.getAvatarUrl()
				: null;
		createAttribute(data, IssueAttribute.ASSIGNEE_GRAVATAR,
				assigneeGravatar);

		createAttribute(data, IssueAttribute.COMMENT_NEW);

		createLabels(repository, data, issue);

		createMilestones(repository, data, issue);

		PullRequest pr = issue.getPullRequest();
		String prDiffUrl = pr != null ? pr.getDiffUrl() : null;
		createAttribute(data, IssueAttribute.PULL_REQUEST_DIFF, prDiffUrl);

		return data;
	}

	private void createMilestones(TaskRepository repository, TaskData data,
			Issue issue) {
		Milestone current = issue.getMilestone();
		String number = current != null ? Integer.toString(current.getNumber())
				: MILESTONE_NONE_KEY;
		TaskAttribute milestoneAttribute = createAttribute(data,
				IssueAttribute.MILESTONE, number);

		if (!this.connector.hasCachedMilestones(repository))
			try {
				this.connector.refreshMilestones(repository);
			} catch (CoreException ignore) {
				// Ignored
			}

		List<Milestone> cachedMilestones = this.connector
				.getMilestones(repository);
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

		if (!this.connector.hasCachedLabels(repository))
			try {
				this.connector.refreshLabels(repository);
			} catch (CoreException ignore) {
				// Ignored
			}

		List<Label> cachedLabels = this.connector.getLabels(repository);
		for (Label label : cachedLabels)
			labels.putOption(label.getName(), label.getName());
	}

	private void createOperations(TaskData data, Issue issue) {
		TaskAttribute operationAttribute = data.getRoot().createAttribute(
				TaskAttribute.OPERATION);
		operationAttribute.getMetaData().setType(TaskAttribute.TYPE_OPERATION);

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
			IssueOperation operation, boolean asDefault) {
		TaskAttribute attribute = data.getRoot().createAttribute(
				TaskAttribute.PREFIX_OPERATION + operation.getId());
		String label = createOperationLabel(issue, operation);
		TaskOperation.applyTo(attribute, operation.getId(), label);

		if (asDefault) {
			TaskAttribute operationAttribute = data.getRoot().getAttribute(
					TaskAttribute.OPERATION);
			TaskOperation.applyTo(operationAttribute, operation.getId(), label);
		}
	}

	private String createOperationLabel(Issue issue, IssueOperation operation) {
		return operation == IssueOperation.LEAVE ? operation.getLabel()
				+ issue.getState() : operation.getLabel();
	}

	public TaskData createTaskData(TaskRepository repository,
			IProgressMonitor monitor, String user, String project, Issue issue,
			List<Comment> comments) {
		TaskData taskData = createTaskData(repository, monitor, user, project,
				issue);
		taskData.setPartial(false);

		if (comments != null && !comments.isEmpty()) {
			int count = 1;
			TaskAttribute root = taskData.getRoot();
			for (Comment comment : comments) {
				TaskCommentMapper commentMapper = new TaskCommentMapper();
				User author = comment.getUser();
				IRepositoryPerson authorPerson = repository.createPerson(author
						.getLogin());
				authorPerson.setName(author.getName());
				commentMapper.setAuthor(authorPerson);
				commentMapper.setCreationDate(comment.getCreatedAt());
				commentMapper.setText(comment.getBody());
				commentMapper.setCommentId(comment.getUrl());
				commentMapper.setNumber(count);

				TaskAttribute attribute = root
						.createAttribute(TaskAttribute.PREFIX_COMMENT + count);
				commentMapper.applyTo(attribute);
				count++;
			}
		}

		return taskData;
	}

	private Issue createIssue(TaskData taskData) {
		Issue issue = new Issue();
		if (!taskData.isNew()) {
			issue.setNumber(Integer.parseInt(taskData.getTaskId()));
		}
		issue.setBody(getAttributeValue(taskData, IssueAttribute.BODY));
		issue.setTitle(getAttributeValue(taskData, IssueAttribute.TITLE));

		String assigneeValue = getAttributeValue(taskData,
				IssueAttribute.ASSIGNEE);
		if (assigneeValue != null) {
			if (assigneeValue.trim().length() == 0)
				assigneeValue = null;
			User assignee = new User().setName(assigneeValue);
			issue.setAssignee(assignee);
		}

		String milestoneValue = getAttributeValue(taskData,
				IssueAttribute.MILESTONE);
		if (milestoneValue != null) {
			Milestone milestone = new Milestone();
			if (milestoneValue.length() > 0)
				milestone.setNumber(Integer.parseInt(milestoneValue));
			issue.setMilestone(milestone);
		}
		return issue;
	}

	private String getAttributeValue(TaskData taskData, IssueAttribute attr) {
		TaskAttribute attribute = taskData.getRoot().getAttribute(attr.getId());
		return attribute == null ? null : attribute.getValue();
	}

	private TaskAttribute createAttribute(TaskData data,
			IssueAttribute attribute) {
		TaskAttribute attr = data.getRoot().createAttribute(attribute.getId());
		TaskAttributeMetaData metaData = attr.getMetaData();
		metaData.defaults().setType(attribute.getType())
				.setKind(attribute.getKind()).setLabel(attribute.getLabel())
				.setReadOnly(attribute.isReadOnly());
		return attr;
	}

	private TaskAttribute createAttribute(TaskData data,
			IssueAttribute attribute, String value) {
		TaskAttribute attr = createAttribute(data, attribute);
		if (value != null)
			data.getAttributeMapper().setValue(attr, value);
		return attr;
	}

	private TaskAttribute createAttribute(TaskData data,
			IssueAttribute attribute, Date value) {
		TaskAttribute attr = createAttribute(data, attribute);
		if (value != null)
			data.getAttributeMapper().setDateValue(attr, value);
		return attr;
	}

	private void createAttribute(TaskData data, IssueAttribute attribute,
			User value, TaskRepository repository) {
		TaskAttribute attr = createAttribute(data, attribute);
		if (value != null) {
			IRepositoryPerson person = repository
					.createPerson(value.getLogin());
			person.setName(value.getName());
			data.getAttributeMapper().setRepositoryPerson(attr, person);
		}
	}

	private TaskAttribute createAttribute(TaskData data,
			IssueAttribute attribute, List<Label> values) {
		TaskAttribute attr = createAttribute(data, attribute);
		if (values != null) {
			List<String> labels = new LinkedList<String>();
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
			if (attr.isInitTask())
				createAttribute(data, attr, (String) null);
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
				IssueAttribute.LABELS.getId());
		if (oldAttributes.contains(labelsAttribute)) {
			LabelService labelService = new LabelService(client);

			if (!this.connector.hasCachedLabels(repository))
				try {
					this.connector.refreshLabels(repository);
				} catch (CoreException ignore) {
					// Ignore
				}
			List<Label> currentLabels = this.connector.getLabels(repository);
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
					this.connector.refreshLabels(repository);
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
			updateLabels(repo.getOwner(), repo.getName(), client, repository,
					taskData, oldAttributes, issue);
			IssueService service = new IssueService(client);
			if (taskData.isNew()) {
				issue.setState(IssueService.STATE_OPEN);
				issue = service.createIssue(repo.getOwner(), repo.getName(),
						issue);
				taskId = Integer.toString(issue.getNumber());
			} else {
				// Handle new comment
				String comment = getAttributeValue(taskData,
						IssueAttribute.COMMENT_NEW);
				if (comment != null && comment.length() > 0)
					service.createComment(repo.getOwner(), repo.getName(),
							taskId, comment);

				// Handle state change
				TaskAttribute operationAttribute = taskData.getRoot()
						.getAttribute(TaskAttribute.OPERATION);
				if (operationAttribute != null) {
					IssueOperation operation = IssueOperation
							.fromId(operationAttribute.getValue());
					if (operation != IssueOperation.LEAVE)
						switch (operation) {
						case REOPEN:
							issue.setState(IssueService.STATE_OPEN);
							break;
						case CLOSE:
							issue.setState(IssueService.STATE_CLOSED);
							break;
						default:
							break;
						}
				}

				service.editIssue(repo.getOwner(), repo.getName(), issue);
			}
			return new RepositoryResponse(
					taskData.isNew() ? ResponseKind.TASK_CREATED
							: ResponseKind.TASK_UPDATED, taskId);
		} catch (RequestException e) {
			throw new CoreException(
					GitHub.createErrorStatus(new GitHubException(e)));
		} catch (IOException e) {
			throw new CoreException(GitHub.createErrorStatus(e));
		}

	}
}
