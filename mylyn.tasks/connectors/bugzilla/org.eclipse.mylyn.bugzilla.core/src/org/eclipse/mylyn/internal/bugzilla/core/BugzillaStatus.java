/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
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

	public final static int ERROR_CONFIRM_MATCH = 1024;

	public final static int ERROR_MATCH_FAILED = 1025;

	private final String htmlMessage;

	private final BugzillaUserMatchResponse userMatchResponse;

	public BugzillaStatus(int severity, String pluginId, int code) {
		super(severity, pluginId, code, "MylynStatus", null); //$NON-NLS-1$
		this.errorMessage = null;
		this.htmlMessage = null;
		this.userMatchResponse = null;
	}

	public BugzillaStatus(int severity, String pluginId, int code, String errorMessage) {
		super(severity, pluginId, code, "MylynStatus", null); //$NON-NLS-1$
		this.errorMessage = errorMessage;
		this.htmlMessage = null;
		this.userMatchResponse = null;
	}

	public BugzillaStatus(int severity, String pluginId, int code, String repositoryUrl, Throwable e) {
		super(severity, pluginId, code, "MylynStatus", e); //$NON-NLS-1$
		this.repositoryUrl = repositoryUrl;
		this.errorMessage = e.getMessage();
		this.htmlMessage = null;
		this.userMatchResponse = null;
	}

	public BugzillaStatus(int severity, String pluginId, int code, String repositoryUrl, String errorMessage) {
		super(severity, pluginId, code, "MylynStatus", null); //$NON-NLS-1$
		this.errorMessage = errorMessage;
		this.repositoryUrl = repositoryUrl;
		this.htmlMessage = null;
		this.userMatchResponse = null;
	}

	public BugzillaStatus(int severity, String pluginId, int code, String repositoryUrl, String errorMessage,
			Throwable e) {
		super(severity, pluginId, code, "MylynStatus", e); //$NON-NLS-1$
		this.errorMessage = errorMessage;
		this.repositoryUrl = repositoryUrl;
		this.htmlMessage = null;
		this.userMatchResponse = null;
	}

	public BugzillaStatus(int severity, String pluginId, int code, String repositoryUrl, String errorMessage,
			String body) {
		super(severity, pluginId, code, "MylynStatus", null); //$NON-NLS-1$
		this.errorMessage = errorMessage;
		this.repositoryUrl = repositoryUrl;
		this.htmlMessage = body;
		this.userMatchResponse = null;
	}

	public BugzillaStatus(int severity, String pluginId, int code, String repositoryUrl, String errorMessage,
			String body, BugzillaUserMatchResponse userMatchResponse) {
		super(severity, pluginId, code, "MylynStatus", null); //$NON-NLS-1$
		this.errorMessage = errorMessage;
		this.repositoryUrl = repositoryUrl;
		this.htmlMessage = body;
		this.userMatchResponse = userMatchResponse;
	}

	/**
	 * Returns the message that is relevant to the code of this status.
	 */
	@Override
	public String getMessage() {

		switch (getCode()) {
		case RepositoryStatus.ERROR_EMPTY_PASSWORD:
			return NLS.bind(Messages.BugzillaStatus_emptyPassword, this.getRepositoryUrl(), this.errorMessage);
		case RepositoryStatus.ERROR_REPOSITORY_LOGIN:
			return NLS.bind(Messages.BugzillaStatus_repositoryLoginFailure, this.getRepositoryUrl(), this.errorMessage);
		case RepositoryStatus.ERROR_REPOSITORY_NOT_FOUND:
			return NLS.bind(Messages.BugzillaStatus_repositoryNotFound, this.errorMessage);
		case RepositoryStatus.ERROR_REPOSITORY:
			return NLS.bind(Messages.BugzillaStatus_errorRepository, this.getRepositoryUrl(), this.errorMessage);
		case RepositoryStatus.ERROR_IO:
			String string1 = "Unknown IO error occurred"; //$NON-NLS-1$
			String string2 = "No message provided"; //$NON-NLS-1$
			if (getException() != null) {
				string1 = getException().getClass().getSimpleName();
				string2 = getException().getMessage();
			}
			Object[] strings = { getRepositoryUrl(), string1, string2 };
			return NLS.bind(Messages.BugzillaStatus_errorIo, strings);
		case RepositoryStatus.ERROR_INTERNAL:
			return NLS.bind(Messages.BugzillaStatus_errorInternal, this.errorMessage);
		case RepositoryStatus.OPERATION_CANCELLED:
			return NLS.bind(Messages.BugzillaStatus_operationCancelled, this.errorMessage);
		case RepositoryStatus.REPOSITORY_COLLISION:
			return NLS.bind(Messages.BugzillaStatus_repositoryCollision, this.errorMessage);
		case IBugzillaConstants.REPOSITORY_STATUS_SUSPICIOUS_ACTION:
			return NLS.bind(Messages.BugzillaStatus_suspiciousAction, this.getRepositoryUrl(), this.errorMessage);
		case RepositoryStatus.REPOSITORY_COMMENT_REQUIRED:
			if (errorMessage == null) {
				return Messages.BugzillaStatus_repositoryCommentRequired;
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
		return "Unknown code: " + getCode(); //$NON-NLS-1$
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public String getHtmlMessage() {
		return htmlMessage;
	}

	public BugzillaUserMatchResponse getUserMatchResponse() {
		return userMatchResponse;
	}
}
