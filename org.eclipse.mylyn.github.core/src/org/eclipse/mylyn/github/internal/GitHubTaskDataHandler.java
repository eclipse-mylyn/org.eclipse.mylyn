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
package org.eclipse.mylyn.github.internal;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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

public class GitHubTaskDataHandler extends AbstractTaskDataHandler {

	private static final String DATA_VERSION = "1";
	/**
	 * 
	 */
	private GitHubTaskAttributeMapper taskAttributeMapper = null;
	private final GitHubRepositoryConnector connector;

	public GitHubTaskDataHandler(GitHubRepositoryConnector connector) {
		this.connector = connector;
	}

	@Override
	public TaskAttributeMapper getAttributeMapper(TaskRepository taskRepository) {
		if (this.taskAttributeMapper == null)
			this.taskAttributeMapper = new GitHubTaskAttributeMapper(
					taskRepository);
		return this.taskAttributeMapper;
	}

	public TaskData createTaskData(TaskRepository repository,
			IProgressMonitor monitor, String user, String project, Issue issue) {

		String key = Integer.toString(issue.getNumber());
		TaskData data = new TaskData(getAttributeMapper(repository),
				GitHubRepositoryConnector.KIND, repository.getRepositoryUrl(),
				key);
		data.setVersion(DATA_VERSION);

		createOperations(data, issue);

		createAttribute(data, GitHubTaskAttributes.KEY, key);
		createAttribute(data, GitHubTaskAttributes.TITLE, issue.getTitle());
		createAttribute(data, GitHubTaskAttributes.BODY, issue.getBody());
		createAttribute(data, GitHubTaskAttributes.STATUS, issue.getState());
		createAttribute(data, GitHubTaskAttributes.CREATION_DATE,
				issue.getCreatedAt());
		createAttribute(data, GitHubTaskAttributes.MODIFICATION_DATE,
				issue.getUpdatedAt());
		createAttribute(data, GitHubTaskAttributes.CLOSED_DATE,
				issue.getClosedAt());

		User reporter = issue.getUser();
		createAttribute(data, GitHubTaskAttributes.REPORTER, reporter,
				repository);
		String reporterGravatar = reporter != null ? reporter.getGravatarUrl()
				: null;
		createAttribute(data, GitHubTaskAttributes.REPORTER_GRAVATAR,
				reporterGravatar);

		User assignee = issue.getAssignee();
		createAttribute(data, GitHubTaskAttributes.ASSIGNEE, assignee,
				repository);
		String assigneeGravatar = assignee != null ? assignee.getGravatarUrl()
				: null;
		createAttribute(data, GitHubTaskAttributes.ASSIGNEE_GRAVATAR,
				assigneeGravatar);

		createAttribute(data, GitHubTaskAttributes.COMMENT_NEW, "");
		createAttribute(data, GitHubTaskAttributes.LABELS, issue.getLabels());

		return data;
	}

	private void createOperations(TaskData data, Issue issue) {
		TaskAttribute operationAttribute = data.getRoot().createAttribute(
				TaskAttribute.OPERATION);
		operationAttribute.getMetaData().setType(TaskAttribute.TYPE_OPERATION);

		if (!data.isNew()) {
			if (issue.getState() != null) {
				addOperation(data, issue, GitHubTaskOperation.LEAVE, true);
				if (issue.getState().equals("open")) {
					addOperation(data, issue, GitHubTaskOperation.CLOSE, false);
				} else if (issue.getState().equals("closed")) {
					addOperation(data, issue, GitHubTaskOperation.REOPEN, false);
				}
			}
		}
	}

	private void addOperation(TaskData data, Issue issue,
			GitHubTaskOperation operation, boolean asDefault) {
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

	private String createOperationLabel(Issue issue,
			GitHubTaskOperation operation) {
		return operation == GitHubTaskOperation.LEAVE ? operation.getLabel()
				+ issue.getState() : operation.getLabel();
	}

	public TaskData createTaskData(TaskRepository repository,
			IProgressMonitor monitor, String user, String project, Issue issue,
			List<Comment> comments) {
		TaskData taskData = createTaskData(repository, monitor, user,
				project, issue);
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
		issue.setBody(getAttributeValue(taskData, GitHubTaskAttributes.BODY));
		issue.setTitle(getAttributeValue(taskData, GitHubTaskAttributes.TITLE));

		String assigneeValue = getAttributeValue(taskData,
				GitHubTaskAttributes.ASSIGNEE);
		if (assigneeValue != null) {
			User assignee = new User().setName(assigneeValue);
			issue.setAssignee(assignee);
		}
		return issue;
	}

	private String getAttributeValue(TaskData taskData,
			GitHubTaskAttributes attr) {
		TaskAttribute attribute = taskData.getRoot().getAttribute(attr.getId());
		return attribute == null ? null : attribute.getValue();
	}

	private TaskAttribute createAttribute(TaskData data,
			GitHubTaskAttributes attribute) {
		TaskAttribute attr = data.getRoot().createAttribute(attribute.getId());
		TaskAttributeMetaData metaData = attr.getMetaData();
		metaData.defaults().setType(attribute.getType())
				.setKind(attribute.getKind()).setLabel(attribute.getLabel())
				.setReadOnly(attribute.isReadOnly());
		return attr;
	}

	private void createAttribute(TaskData data, GitHubTaskAttributes attribute,
			String value) {
		TaskAttribute attr = createAttribute(data, attribute);
		if (value != null) {
			data.getAttributeMapper().setValue(attr, value);
		}
	}

	private void createAttribute(TaskData data, GitHubTaskAttributes attribute,
			Date value) {
		TaskAttribute attr = createAttribute(data, attribute);
		if (value != null) {
			data.getAttributeMapper().setDateValue(attr, value);
		}
	}

	private void createAttribute(TaskData data, GitHubTaskAttributes attribute,
			User value, TaskRepository repository) {
		TaskAttribute attr = createAttribute(data, attribute);
		if (value != null) {
			IRepositoryPerson person = repository
					.createPerson(value.getLogin());
			person.setName(value.getName());
			data.getAttributeMapper().setRepositoryPerson(attr, person);
		}
	}

	private void createAttribute(TaskData data, GitHubTaskAttributes attribute,
			List<Label> values) {
		TaskAttribute attr = createAttribute(data, attribute);
		if (values != null) {
			List<String> labels = new LinkedList<String>();
			for (Label label : values) {
				labels.add(label.getName());
			}
			data.getAttributeMapper().setValues(attr, labels);
		}
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, TaskData data,
			ITaskMapping initializationData, IProgressMonitor monitor)
			throws CoreException {

		data.setVersion(DATA_VERSION);

		for (GitHubTaskAttributes attr : GitHubTaskAttributes.values()) {
			if (attr.isInitTask()) {
				createAttribute(data, attr, (String) null);
			}
		}

		return true;
	}

	@Override
	public RepositoryResponse postTaskData(TaskRepository repository,
			TaskData taskData, Set<TaskAttribute> oldAttributes,
			IProgressMonitor monitor) throws CoreException {
		String taskId = taskData.getTaskId();
		Issue issue = createIssue(taskData);
		String user = GitHub.computeTaskRepositoryUser(repository.getUrl());
		String repo = GitHub.computeTaskRepositoryProject(repository.getUrl());
		try {

			GitHubClient client = new GitHubClient();
			GitHubCredentials credentials = GitHubCredentials
					.create(repository);
			client.setCredentials(credentials.getUsername(),
					credentials.getPassword());
			IssueService service = new IssueService(client);
			if (taskData.isNew()) {
				issue.setState(IssueService.STATE_OPEN);
				issue = service.createIssue(user, repo, issue);
				taskId = Integer.toString(issue.getNumber());
			} else {

				// Handle new comment
				String comment = getAttributeValue(taskData,
						GitHubTaskAttributes.COMMENT_NEW);
				if (comment != null && comment.length() > 0)
					service.createComment(user, repo, taskData.getTaskId(),
							comment);

				// Handle state change
				TaskAttribute operationAttribute = taskData.getRoot()
						.getAttribute(TaskAttribute.OPERATION);
				if (operationAttribute != null) {
					GitHubTaskOperation operation = GitHubTaskOperation
							.fromId(operationAttribute.getValue());
					if (operation != GitHubTaskOperation.LEAVE)
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

				service.editIssue(user, repo, issue);
			}
			return new RepositoryResponse(
					taskData.isNew() ? ResponseKind.TASK_CREATED
							: ResponseKind.TASK_UPDATED, taskId);
		} catch (IOException e) {
			throw new CoreException(GitHub.createErrorStatus(e));
		}

	}

}
