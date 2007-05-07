/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasks.core;

import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class RepositoryStatus extends MylarStatus {

	private String repositoryUrl;

	public RepositoryStatus(TaskRepository repository, int severity, String pluginId, int code, String message) {
		this(repository.getUrl(), severity, pluginId, code, message, null);
	}

	public RepositoryStatus(TaskRepository repository, int severity, String pluginId, int code, String message,
			Throwable e) {
		this(repository.getUrl(), severity, pluginId, code, message, e);
	}

	public RepositoryStatus(String repositoryUrl, int severity, String pluginId, int code, String message) {
		this(repositoryUrl, severity, pluginId, code, message, null);
	}

	public RepositoryStatus(String repositoryUrl, int severity, String pluginId, int code, String message, Throwable e) {
		super(severity, pluginId, code, message, e);

		if (repositoryUrl == null) {
			throw new IllegalArgumentException("repositoryUrl must not be null");
		}

		this.repositoryUrl = repositoryUrl;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public static RepositoryStatus createStatus(TaskRepository repository, int severity, String pluginId, String message) {
		return createStatus(repository.getUrl(), severity, pluginId, message);
	}

	public static RepositoryStatus createStatus(String repositoryUrl, int severity, String pluginId, String message) {
		return new RepositoryStatus(repositoryUrl, severity, pluginId, IMylarStatusConstants.REPOSITORY_ERROR, message);
	}

	public static RepositoryStatus createLoginError(String repositoryUrl, String pluginId) {
		return new RepositoryStatus(repositoryUrl, Status.ERROR, pluginId,
				IMylarStatusConstants.REPOSITORY_LOGIN_ERROR, NLS.bind(
						"Unable to login to {0}. Please validate credentials via Task Repositories view.",
						repositoryUrl));
	}

	public static RepositoryStatus createNotFoundError(String repositoryUrl, String pluginId) {
		return new RepositoryStatus(repositoryUrl, Status.ERROR, pluginId, IMylarStatusConstants.REPOSITORY_NOT_FOUND,
				NLS.bind("Repository {0} could not be found.", repositoryUrl));
	}

	public static RepositoryStatus createCollisionError(String repositoryUrl, String pluginId) {
		return new RepositoryStatus(
				repositoryUrl,
				Status.ERROR,
				pluginId,
				IMylarStatusConstants.REPOSITORY_COLLISION,
				NLS
						.bind(
								"Mid-air collision occurred while submitting to {0}.\n\nSynchronize task and re-submit changes.",
								repositoryUrl));
	}

	public static RepositoryStatus createCommentRequiredError(String repositoryUrl, String pluginId) {
		return new RepositoryStatus(repositoryUrl, Status.ERROR, pluginId,
				IMylarStatusConstants.REPOSITORY_COMMENT_REQD,
				"You have to specify a new comment when making this change. Please comment on the reason for this change.");
	}

	public static RepositoryStatus createHtmlStatus(String repositoryUrl, int severity, String pluginId, int code,
			String message, String htmlMessage) {
		if (htmlMessage == null) {
			throw new IllegalArgumentException("htmlMessage must not be null");
		}

		RepositoryStatus status = new RepositoryStatus(repositoryUrl, severity, pluginId, code, message);
		status.setHtmlMessage(htmlMessage);
		return status;
	}

}
