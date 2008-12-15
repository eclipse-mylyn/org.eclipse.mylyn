/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.osgi.util.NLS;

/**
 * @author Rob Elves
 */
public class BugzillaStatus extends Status {

	private final String errorMessage;

	private String repositoryUrl = ""; //$NON-NLS-1$

	public BugzillaStatus(int severity, String pluginId, int code) {
		super(severity, pluginId, code, "MylynStatus", null); //$NON-NLS-1$
		this.errorMessage = null;
	}

	public BugzillaStatus(int severity, String pluginId, int code, String errorMessage) {
		super(severity, pluginId, code, "MylynStatus", null); //$NON-NLS-1$
		this.errorMessage = errorMessage;
	}

	public BugzillaStatus(int severity, String pluginId, int code, String repositoryUrl, Throwable e) {
		super(severity, pluginId, code, "MylynStatus", e); //$NON-NLS-1$
		this.repositoryUrl = repositoryUrl;
		this.errorMessage = e.getMessage();
	}

	public BugzillaStatus(int severity, String pluginId, int code, String repositoryUrl, String errorMessage) {
		super(severity, pluginId, code, "MylynStatus", null); //$NON-NLS-1$
		this.errorMessage = errorMessage;
		this.repositoryUrl = repositoryUrl;
	}

	public BugzillaStatus(int severity, String pluginId, int code, String repositoryUrl, String errorMessage,
			Throwable e) {
		super(severity, pluginId, code, "MylynStatus", e); //$NON-NLS-1$
		this.errorMessage = errorMessage;
		this.repositoryUrl = repositoryUrl;
	}

	/**
	 * Returns the message that is relevant to the code of this status.
	 */
	@Override
	public String getMessage() {

		switch (getCode()) {
		case RepositoryStatus.ERROR_REPOSITORY_LOGIN:
			return NLS.bind(BugzillaMessages.repositoryLoginFailure, this.getRepositoryUrl(), this.errorMessage);
		case RepositoryStatus.ERROR_REPOSITORY_NOT_FOUND:
			return NLS.bind(BugzillaMessages.repositoryNotFound, this.errorMessage);
		case RepositoryStatus.ERROR_REPOSITORY:
			return NLS.bind(BugzillaMessages.errorRepository, this.getRepositoryUrl(), this.errorMessage);
		case RepositoryStatus.ERROR_IO:
			String string1 = "Unknown IO error occurred"; //$NON-NLS-1$
			String string2 = "No message provided"; //$NON-NLS-1$
			if (getException() != null) {
				string1 = getException().getClass().getSimpleName();
				string2 = getException().getMessage();
			}
			Object[] strings = { getRepositoryUrl(), string1, string2 };
			return NLS.bind(BugzillaMessages.errorIo, strings);
		case RepositoryStatus.ERROR_INTERNAL:
			return NLS.bind(BugzillaMessages.errorInternal, this.errorMessage);
		case RepositoryStatus.OPERATION_CANCELLED:
			return NLS.bind(BugzillaMessages.operationCancelled, this.errorMessage);
		case RepositoryStatus.REPOSITORY_COLLISION:
			return NLS.bind(BugzillaMessages.repositoryCollision, this.errorMessage);
		case RepositoryStatus.REPOSITORY_COMMENT_REQUIRED:
			if (errorMessage == null) {
				return BugzillaMessages.repositoryCommentRequired;
			} else {
				return errorMessage;
			}
		}
		if (errorMessage != null) {
			return errorMessage;
		} else if (getException() != null) {
			String message = getException().getMessage();
			if (message != null) {
				return message;
			} else {
				return getException().toString();
			}
		}
		return "Unknown"; //$NON-NLS-1$
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}
}
