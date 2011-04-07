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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

/**
 * GitHub connector.
 */
public class GitHubRepositoryConnector extends AbstractRepositoryConnector {


	/**
	 * GitHub kind.
	 */
	protected static final String KIND = GitHub.CONNECTOR_KIND;

	/**
	 * GitHub service which creates, lists, deletes, etc. GitHub tasks.
	 */
	private final GitHubService service = new GitHubService();

	/**
	 * GitHub specific {@link AbstractTaskDataHandler}.
	 */
	private final GitHubTaskDataHandler taskDataHandler;

	public GitHubRepositoryConnector() {
		taskDataHandler = new GitHubTaskDataHandler(this);
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
		return "GitHub";
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
		String queryStatus = query.getAttribute("status");
		
		String[] statuses;
		if (queryStatus.equals("all")) {
			statuses = new String[] {"open","closed"};
		} else {
			statuses = new String[] { queryStatus };
		}
		
		monitor.beginTask("Querying repository ...", statuses.length);
		try {
			String user = GitHub.computeTaskRepositoryUser(repository.getUrl());
			String project = GitHub.computeTaskRepositoryProject(repository.getUrl());
			GitHubCredentials credentials = GitHubCredentials.create(repository);
			
			// perform query
			
			for (String status: statuses) {
				GitHubIssues issues = service.searchIssues(user, project,
						status, query.getAttribute("queryText"), credentials);
	
				// collect task data
				for (GitHubIssue issue : issues.getIssues()) {
					TaskData taskData = taskDataHandler.createPartialTaskData(
							repository, monitor,user, project, issue);
					collector.accept(taskData);
				}
				monitor.worked(1);
			}

			result = Status.OK_STATUS;
		} catch (GitHubServiceException e) {
			result = GitHub.createErrorStatus(e);
		}

		monitor.done();
		return result;
	}


	@Override
	public TaskData getTaskData(TaskRepository repository, String taskId,
			IProgressMonitor monitor) throws CoreException {

		String user = GitHub.computeTaskRepositoryUser(repository.getUrl());
		String project = GitHub.computeTaskRepositoryProject(repository.getUrl());
		
		try {
			GitHubCredentials credentials = GitHubCredentials.create(repository);
			GitHubIssue issue = service.showIssue(user, project, taskId, credentials);
			TaskData taskData = taskDataHandler.createTaskData(repository, monitor, user, project, issue);
			
			return taskData;
		} catch (GitHubServiceException e) {
			throw new CoreException(GitHub.createErrorStatus(e));
		}
	}


	@Override
	public String getRepositoryUrlFromTaskUrl(String taskFullUrl) {
		if (taskFullUrl != null) {
			Matcher matcher = Pattern.compile("(http://.+?)/issues/issue/([^/]+)").matcher(taskFullUrl);
			if (matcher.matches()) {
				return matcher.group(1);
			}
		}
		return null;
	}

	@Override
	public String getTaskIdFromTaskUrl(String taskFullUrl) {
		if (taskFullUrl != null) {
			Matcher matcher = Pattern.compile(".+?/issues/issue/([^/]+)").matcher(taskFullUrl);
			if (matcher.matches()) {
				return matcher.group(1);
			}
		}
		return null;
	}

	@Override
	public String getTaskUrl(String repositoryUrl, String taskId) {
		return repositoryUrl+"/issues/issue/"+taskId;
	}

	@Override
	public void updateRepositoryConfiguration(TaskRepository taskRepository,
			IProgressMonitor monitor) throws CoreException {
	}

	@Override
	public boolean hasTaskChanged(TaskRepository repository, ITask task,
			TaskData taskData) {
		return new TaskMapper(taskData).hasChanges(task);
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository taskRepository,
			ITask task, TaskData taskData) {
		if (!taskData.isNew()) {
			task.setUrl(getTaskUrl(taskRepository.getUrl(), taskData.getTaskId()));
		}
		new TaskMapper(taskData).applyTo(task);
	}

	public GitHubService getService() {
		return service;
	}
}
