/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

/**
 * Utility for working and capturing status specific to repository connections.
 *
 * @author Rob Elves
 * @author Steffen Pingel
 * @since 2.0
 */
public class RepositoryStatus extends Status {

	public final static int ERROR_IO = 5;

	public final static int ERROR_NETWORK = 11;

	public final static int ERROR_PERMISSION_DENIED = 12;

	/**
	 * requires construction with repositoryUrl and error message
	 */
	public final static int ERROR_REPOSITORY = 1;

	public final static int ERROR_REPOSITORY_LOGIN = 3;

	public final static int ERROR_REPOSITORY_NOT_FOUND = 4;

	public final static int OPERATION_CANCELLED = 8;

	public final static int REPOSITORY_COLLISION = 6;

	public final static int REPOSITORY_COMMENT_REQUIRED = 9;

	public final static int REPOSITORY_LOGGED_OUT = 10;

	public final static int ERROR_INTERNAL = 7;

	/**
	 * @since 3.6
	 */
	public final static int ERROR_EMPTY_PASSWORD = 13;

	private String htmlMessage;

	protected String repositoryUrl;

	public RepositoryStatus(TaskRepository repository, int severity, String pluginId, int code, String message) {
		this(repository.getRepositoryUrl(), severity, pluginId, code, message, null);
	}

	public RepositoryStatus(TaskRepository repository, int severity, String pluginId, int code, String message,
			Throwable e) {
		this(repository.getRepositoryUrl(), severity, pluginId, code, message, e);
	}

	public RepositoryStatus(String repositoryUrl, int severity, String pluginId, int code, String message) {
		this(repositoryUrl, severity, pluginId, code, message, null);
	}

	public RepositoryStatus(String repositoryUrl, int severity, String pluginId, int code, String message,
			Throwable e) {
		super(severity, pluginId, code, message, e);

		if (repositoryUrl == null) {
			throw new IllegalArgumentException("repositoryUrl must not be null"); //$NON-NLS-1$
		}

		this.repositoryUrl = repositoryUrl;
	}

	/**
	 * Constructs a status object with a message.
	 */
	public RepositoryStatus(int severity, String pluginId, int code, String message) {
		super(severity, pluginId, code, message, null);
	}

	/**
	 * Constructs a status object with a message and an exception. that caused the error.
	 */
	public RepositoryStatus(int severity, String pluginId, int code, String message, Throwable e) {
		super(severity, pluginId, code, message, e);
	}

	/**
	 * Returns the message that is relevant to the code of this status.
	 */
	@Override
	public String getMessage() {
		String message = super.getMessage();
		if (message != null && !"".equals(message)) { //$NON-NLS-1$
			return message;
		}

		Throwable exception = getException();
		if (exception != null) {
			if (exception.getMessage() != null) {
				return exception.getMessage();
			}
			return exception.toString();
		}

		return ""; //$NON-NLS-1$
	}

	@Override
	protected void setMessage(String message) {
		super.setMessage(message != null ? message : ""); //$NON-NLS-1$
	}

	protected void setHtmlMessage(String htmlMessage) {
		this.htmlMessage = htmlMessage;
	}

	public String getHtmlMessage() {
		return htmlMessage;
	}

	public boolean isHtmlMessage() {
		return htmlMessage != null;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public static RepositoryStatus createInternalError(String pluginId, String message, Throwable t) {
		return new RepositoryStatus(IStatus.ERROR, pluginId, RepositoryStatus.ERROR_INTERNAL, message, t);
	}

	public static RepositoryStatus createHtmlStatus(int severity, String pluginId, int code, String message,
			String htmlMessage) {
		if (htmlMessage == null) {
			throw new IllegalArgumentException("htmlMessage must not be null"); //$NON-NLS-1$
		}

		RepositoryStatus status = new RepositoryStatus(severity, pluginId, code, message);
		status.setHtmlMessage(htmlMessage);
		return status;
	}

	public static RepositoryStatus createStatus(TaskRepository repository, int severity, String pluginId,
			String message) {
		return createStatus(repository.getRepositoryUrl(), severity, pluginId, message);
	}

	public static RepositoryStatus createStatus(String repositoryUrl, int severity, String pluginId, String message) {
		return new RepositoryStatus(repositoryUrl, severity, pluginId, RepositoryStatus.ERROR_REPOSITORY, message);
	}

	public static RepositoryStatus createLoginError(String repositoryUrl, String pluginId) {
		return new RepositoryStatus(repositoryUrl, IStatus.ERROR, pluginId, RepositoryStatus.ERROR_REPOSITORY_LOGIN,
				NLS.bind("Unable to login to {0}. Please validate credentials via Task Repositories view.", //$NON-NLS-1$
						repositoryUrl));
	}

	public static RepositoryStatus createNotFoundError(String repositoryUrl, String pluginId) {
		return new RepositoryStatus(repositoryUrl, IStatus.ERROR, pluginId, RepositoryStatus.ERROR_REPOSITORY_NOT_FOUND,
				NLS.bind("Repository {0} could not be found.", //$NON-NLS-1$
						repositoryUrl));
	}

	public static RepositoryStatus createCollisionError(String repositoryUrl, String pluginId) {
		return new RepositoryStatus(repositoryUrl, IStatus.ERROR, pluginId, RepositoryStatus.REPOSITORY_COLLISION,
				"Mid-air collision occurred. Synchronize task and re-submit changes."); //$NON-NLS-1$
	}

	public static RepositoryStatus createCommentRequiredError(String repositoryUrl, String pluginId) {
		return new RepositoryStatus(repositoryUrl, IStatus.ERROR, pluginId,
				RepositoryStatus.REPOSITORY_COMMENT_REQUIRED,
				"You have to specify a new comment when making this change. Please comment on the reason for this change."); //$NON-NLS-1$
	}

	public static RepositoryStatus createHtmlStatus(String repositoryUrl, int severity, String pluginId, int code,
			String message, String htmlMessage) {
		if (htmlMessage == null) {
			throw new IllegalArgumentException("htmlMessage must not be null"); //$NON-NLS-1$
		}

		RepositoryStatus status = new RepositoryStatus(repositoryUrl, severity, pluginId, code, message);
		status.setHtmlMessage(htmlMessage);
		return status;
	}

}
