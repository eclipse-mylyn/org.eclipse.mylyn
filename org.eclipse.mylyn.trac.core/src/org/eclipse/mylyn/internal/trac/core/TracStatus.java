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

package org.eclipse.mylar.internal.trac.core;

import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.tasks.core.IMylarStatusConstants;
import org.eclipse.osgi.util.NLS;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class TracStatus extends Status implements IMylarStatusConstants {

	private String repositoryUrl;

	public TracStatus(int severity, String pluginId, int code) {
		super(severity, pluginId, code, null, null);
	}

	public TracStatus(int severity, String pluginId, int code, String message) {
		super(severity, pluginId, code, message, null);
	}

	public TracStatus(int severity, String pluginId, int code, String message, Throwable e) {
		super(severity, pluginId, code, message, e);
	}

	/**
	 * Returns the message that is relevant to the code of this status.
	 */
	public String getMessage() {
		String message = super.getMessage();
		if (message != null) {
			return message;
		}

		Throwable exception = getException();
		if (exception != null) {
			if (exception.getMessage() != null) {
				return exception.getMessage();
			}
			return exception.toString();
		}

		switch (getCode()) {
		case REPOSITORY_LOGIN_ERROR:
			return NLS.bind("Unable to login to {0}. Please validate credentials via Task Repositories view.", getRepositoryUrl());
		case REPOSITORY_NOT_FOUND:
			return NLS.bind("Repository {0} could not be found.", getRepositoryUrl());
		default:
			return "";
		}
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(String repsitoryUrl) {
		this.repositoryUrl = repsitoryUrl;
	}
	
}
