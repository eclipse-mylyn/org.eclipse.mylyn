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

package org.eclipse.mylyn.internal.bugzilla.core;

import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.IMylarStatusConstants;
import org.eclipse.mylyn.tasks.core.TasksMessages;

/**
 * @author Rob Elves
 */
public class BugzillaStatus extends Status implements IMylarStatusConstants {

	private String errorMessage;

	private String repositoryUrl = "";

	public BugzillaStatus(int severity, String pluginId, int code) {
		super(severity, pluginId, code, "MylarStatus", null);
		this.errorMessage = null;
	}

	public BugzillaStatus(int severity, String pluginId, int code, String errorMessage) {
		super(severity, pluginId, code, "MylarStatus", null);
		this.errorMessage = errorMessage;
	}

	public BugzillaStatus(int severity, String pluginId, int code, String repositoryUrl, Throwable e) {
		super(severity, pluginId, code, "MylarStatus", e);
		this.repositoryUrl = repositoryUrl;
		this.errorMessage = e.getMessage();
	}

	public BugzillaStatus(int severity, String pluginId, int code, String repositoryUrl, String errorMessage) {
		super(severity, pluginId, code, "MylarStatus", null);
		this.errorMessage = errorMessage;
		this.repositoryUrl = repositoryUrl;
	}

	public BugzillaStatus(int severity, String pluginId, int code, String repositoryUrl, String errorMessage, Throwable e) {
		super(severity, pluginId, code, "MylarStatus", e);
		this.errorMessage = errorMessage;
		this.repositoryUrl = repositoryUrl;
	}

	/**
	 * Returns the message that is relevant to the code of this status.
	 */
	public String getMessage() {

		switch (getCode()) {
		case REPOSITORY_LOGIN_ERROR:
			return TasksMessages
					.bind(TasksMessages.repository_login_failure, this.getRepositoryUrl(), this.errorMessage);
		case REPOSITORY_NOT_FOUND:
			return TasksMessages.bind(TasksMessages.repository_not_found, this.errorMessage);
		case REPOSITORY_ERROR:
			return TasksMessages.bind(TasksMessages.repository_error, this.getRepositoryUrl(), this.errorMessage);
		case IO_ERROR:
			String string1 = "Unknown IO error occurred";
			String string2 = "No message provided";
			if(getException() != null) {
				string1 = getException().getClass().getSimpleName();
				string2 = getException().getMessage();
			}
			Object[] strings = { getRepositoryUrl(), string1, string2 };
			return TasksMessages.bind(TasksMessages.io_error, strings);
		case INTERNAL_ERROR:
			return TasksMessages.bind(TasksMessages.internal_error, this.errorMessage);
		case OPERATION_CANCELLED:
			return TasksMessages.bind(TasksMessages.operation_cancelled, this.errorMessage);
		case REPOSITORY_COLLISION:
			return TasksMessages.bind(TasksMessages.repository_collision, this.errorMessage);
		case REPOSITORY_COMMENT_REQD:
			if (errorMessage == null) {
				return TasksMessages.repository_comment_reqd;
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
		return "Unknown";
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}
}
