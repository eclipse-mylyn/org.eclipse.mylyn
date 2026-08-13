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
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.internal.github.core.GithubApi;
import org.eclipse.mylyn.internal.github.core.RepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;
import org.kohsuke.github.GHGist;
import org.kohsuke.github.GHIssueComment;

/**
 * Gist repository connector class.
 */
public class GistConnector extends RepositoryConnector {

	/**
	 * KIND
	 */
	public static final String KIND = "githubGists"; //$NON-NLS-1$

	private final GistTaskDataHandler dataHandler = new GistTaskDataHandler();

	private final GistAttachmentHandler attachmentHandler = new GistAttachmentHandler();

	/**
	 * @see org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#getTaskDataHandler()
	 */
	@Override
	public AbstractTaskDataHandler getTaskDataHandler() {
		return dataHandler;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#getTaskAttachmentHandler()
	 */
	@Override
	public AbstractTaskAttachmentHandler getTaskAttachmentHandler() {
		return attachmentHandler;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#canCreateNewTask(org.eclipse.mylyn.tasks.core.TaskRepository)
	 */
	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		// Gists are created from menu actions on files and text selections
		return false;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#getConnectorKind()
	 */
	@Override
	public String getConnectorKind() {
		return KIND;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#getLabel()
	 */
	@Override
	public String getLabel() {
		return Messages.GistConnector_LabelConnector;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#getRepositoryUrlFromTaskUrl(java.lang.String)
	 */
	@Override
	public String getRepositoryUrlFromTaskUrl(String taskFullUrl) {
		int lastSlash = taskFullUrl.lastIndexOf('/');
		return lastSlash >= 0 ? taskFullUrl.substring(0, lastSlash) : null;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#getTaskData(org.eclipse.mylyn.tasks.core.TaskRepository,
	 *      java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		try {
			TaskAttributeMapper mapper = dataHandler.getAttributeMapper(repository);
			GithubApi githubApi = GithubApi.createGithubClient(
					repository.getCredentials(AuthenticationType.REPOSITORY));
			GHGist gist = githubApi.getGist(taskId);
			TaskData data = new TaskData(mapper, getConnectorKind(), repository.getUrl(), gist.getGistId());
			data.setPartial(false);
			dataHandler.fillTaskData(repository, data, gist);
			if (gist.getCommentCount() > 0) {
				List<GHIssueComment> comments = githubApi.listGistComments(gist);
				dataHandler.fillComments(repository, data, comments);
			}
			return data;
		} catch (IOException e) {
			throw new CoreException(GitHub.createWrappedStatus(e));
		}
	}

	/**
	 * @see org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#performQuery(org.eclipse.mylyn.tasks.core.TaskRepository,
	 *      org.eclipse.mylyn.tasks.core.IRepositoryQuery, org.eclipse.mylyn.tasks.core.data.TaskDataCollector,
	 *      org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus performQuery(TaskRepository repository, IRepositoryQuery query, TaskDataCollector collector,
			ISynchronizationSession session, IProgressMonitor monitor) {
		IStatus status = Status.OK_STATUS;
		try {
			GithubApi github = GithubApi.createGithubClient(repository.getCredentials(AuthenticationType.REPOSITORY));
			TaskAttributeMapper mapper = dataHandler.getAttributeMapper(repository);
			for (GHGist gist : github.listGists()) {
				TaskData data = new TaskData(mapper, getConnectorKind(), repository.getUrl(), gist.getGistId());
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