/*******************************************************************************
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
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.core.gist;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.GistService;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.internal.github.core.RepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

/**
 * Gist repository connector class.
 */
public class GistConnector extends RepositoryConnector {

	/**
	 * KIND
	 */
	public static final String KIND = "githubGists"; //$NON-NLS-1$

	/**
	 * Create client for repository
	 *
	 * @param repository
	 * @return client
	 */
	public static GitHubClient createClient(TaskRepository repository) {
		GitHubClient client = GitHubClient.createClient(repository
				.getRepositoryUrl());
		return configureClient(client, repository);
	}

	/**
	 * Configure client for repository
	 *
	 * @param client
	 * @param repository
	 * @return client
	 */
	public static GitHubClient configureClient(GitHubClient client,
			TaskRepository repository) {
		GitHub.addCredentials(client, repository);
		return GitHub.configureClient(client);
	}

	private GistTaskDataHandler dataHandler = new GistTaskDataHandler();

	private GistAttachmentHandler attachmentHandler = new GistAttachmentHandler();

	/**
	 * @see org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#getTaskDataHandler()
	 */
	public AbstractTaskDataHandler getTaskDataHandler() {
		return dataHandler;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#getTaskAttachmentHandler()
	 */
	public AbstractTaskAttachmentHandler getTaskAttachmentHandler() {
		return attachmentHandler;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#canCreateNewTask(org.eclipse.mylyn.tasks.core.TaskRepository)
	 */
	public boolean canCreateNewTask(TaskRepository repository) {
		// Gists are created from menu actions on files and text selections
		return false;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#getConnectorKind()
	 */
	public String getConnectorKind() {
		return KIND;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#getLabel()
	 */
	public String getLabel() {
		return Messages.GistConnector_LabelConnector;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#getRepositoryUrlFromTaskUrl(java.lang.String)
	 */
	public String getRepositoryUrlFromTaskUrl(String taskFullUrl) {
		int lastSlash = taskFullUrl.lastIndexOf('/');
		return lastSlash >= 0 ? taskFullUrl.substring(0, lastSlash) : null;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#getTaskData(org.eclipse.mylyn.tasks.core.TaskRepository,
	 *      java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public TaskData getTaskData(TaskRepository repository, String taskId,
			IProgressMonitor monitor) throws CoreException {
		GistService service = new GistService(createClient(repository));
		try {
			TaskAttributeMapper mapper = dataHandler
					.getAttributeMapper(repository);
			Gist gist = service.getGist(taskId);
			TaskData data = new TaskData(mapper, getConnectorKind(),
					repository.getUrl(), gist.getId());
			data.setPartial(false);
			dataHandler.fillTaskData(repository, data, gist);
			if (gist.getComments() > 0)
				dataHandler.fillComments(repository, data,
						service.getComments(gist.getId()));
			return data;
		} catch (IOException e) {
			throw new CoreException(GitHub.createWrappedStatus(e));
		}
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#performQuery(org.eclipse.mylyn.tasks.core.TaskRepository,
	 *      org.eclipse.mylyn.tasks.core.IRepositoryQuery,
	 *      org.eclipse.mylyn.tasks.core.data.TaskDataCollector,
	 *      org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus performQuery(TaskRepository repository,
			IRepositoryQuery query, TaskDataCollector collector,
			ISynchronizationSession session, IProgressMonitor monitor) {
		IStatus status = Status.OK_STATUS;
		GistService service = new GistService(createClient(repository));
		String user = query.getAttribute(IGistQueryConstants.USER);
		try {
			TaskAttributeMapper mapper = dataHandler
					.getAttributeMapper(repository);
			for (Gist gist : service.getGists(user)) {
				TaskData data = new TaskData(mapper, getConnectorKind(),
						repository.getUrl(), gist.getId());
				data.setPartial(true);
				dataHandler.fillTaskData(repository, data, gist);
				collector.accept(data);
			}
		} catch (IOException e) {
			status = GitHub.createWrappedStatus(e);
		}
		return status;
	}
}
