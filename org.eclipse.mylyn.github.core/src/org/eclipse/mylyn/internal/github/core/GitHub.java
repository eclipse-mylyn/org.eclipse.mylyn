/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green <david.green@tasktop.com> - initial contribution
 *     Christian Trutz <christian.trutz@gmail.com> - initial contribution
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.core;

import java.io.IOException;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * GitHub class
 */
public class GitHub {

	/** BUNDLE_ID */
	public static final String BUNDLE_ID = "org.eclipse.mylyn.github.core"; //$NON-NLS-1$

	/** CONNECTOR_KIND */
	public static final String CONNECTOR_KIND = "github"; //$NON-NLS-1$

	/** HTTP_WWW_GITHUB_ORG */
	public static final String HTTP_WWW_GITHUB_ORG = "http://www.github.org"; //$NON-NLS-1$

	/** HTTP_GITHUB_COM */
	public static final String HTTP_GITHUB_COM = "http://github.com"; //$NON-NLS-1$

	/** URL_PATTERN */
	public static final Pattern URL_PATTERN = Pattern.compile("(?:" //$NON-NLS-1$
			+ Pattern.quote(HTTP_WWW_GITHUB_ORG) + "|" //$NON-NLS-1$
			+ Pattern.quote(HTTP_GITHUB_COM) + ")/([^/]+)/([^/]+)"); //$NON-NLS-1$

	/** USER_AGENT */
	public static final String USER_AGENT = "GitHubEclipse/1.3.0"; //$NON-NLS-1$

	/** REPOSITORY_SEGMENTS */
	public static final String REPOSITORY_SEGMENTS = "/user/repository"; //$NON-NLS-1$

	/**
	 * Configure client with standard configuration
	 *
	 * @param client
	 * @return given client
	 */
	public static GitHubClient configureClient(GitHubClient client) {
		return client.setUserAgent(USER_AGENT);
	}

	/**
	 * Set credentials on client from task repository
	 *
	 * @param client
	 * @param repository
	 * @return specified client
	 */
	public static GitHubClient addCredentials(GitHubClient client,
			TaskRepository repository) {
		AuthenticationCredentials credentials = repository
				.getCredentials(AuthenticationType.REPOSITORY);
		if (credentials != null)
			client.setCredentials(credentials.getUserName(),
					credentials.getPassword());
		return client;
	}

	/**
	 * Create status of severity with message
	 *
	 * @param severity
	 * @param message
	 * @return status
	 */
	public static IStatus createStatus(int severity, String message) {
		return new Status(severity, BUNDLE_ID, message);
	}

	/**
	 * Create status of severity with message and throwable
	 *
	 * @param severity
	 * @param message
	 * @param e
	 * @return status
	 */
	public static IStatus createStatus(int severity, String message, Throwable e) {
		return new Status(severity, BUNDLE_ID, message, e);
	}

	/**
	 * Create error status from message
	 *
	 * @param message
	 * @return status
	 */
	public static IStatus createErrorStatus(String message) {
		return createStatus(IStatus.ERROR, message);
	}

	/**
	 * Create error status from message and throwable
	 *
	 * @param message
	 * @param t
	 * @return status
	 */
	public static IStatus createErrorStatus(String message, Throwable t) {
		return createStatus(IStatus.ERROR, message, t);
	}

	/**
	 * Create error status from throwable
	 *
	 * @param e
	 * @return status
	 */
	public static IStatus createErrorStatus(Throwable e) {
		return createStatus(IStatus.ERROR,
				"Unexpected error: " + e.getLocalizedMessage(), e); //$NON-NLS-1$
	}

	/**
	 * Create error status from {@link IOException} that wraps it in a
	 * {@link GitHubException} if it is a {@link RequestException}
	 *
	 * @param e
	 * @return status
	 */
	public static IStatus createWrappedStatus(IOException e) {
		return createErrorStatus(GitHubException.wrap(e));
	}

	/**
	 * Get log
	 *
	 * @return log
	 */
	public static ILog getLog() {
		return Platform.getLog(Platform.getBundle(BUNDLE_ID));
	}

	/**
	 * Log message and throwable as error status
	 *
	 * @param message
	 * @param t
	 */
	public static void logError(String message, Throwable t) {
		getLog().log(createErrorStatus(message, t));
	}

	/**
	 * Log throwable as error status
	 *
	 * @param t
	 */
	public static void logError(Throwable t) {
		getLog().log(createErrorStatus(t.getMessage(), t));
	}

	/**
	 * Get repository for url
	 *
	 * @param repositoryUrl
	 * @return repository or null if not present in url
	 */
	public static RepositoryId getRepository(String repositoryUrl) {
		return RepositoryId.createFromUrl(repositoryUrl);
	}

	/**
	 * Create url with github.com host
	 *
	 * @param user
	 * @param project
	 * @return url
	 *
	 * @see #createGitHubUrlAlternate(String, String)
	 */
	public static String createGitHubUrl(String user, String project) {
		return HTTP_GITHUB_COM + '/' + user + '/' + project;
	}

	/**
	 * Create url with github.org host
	 *
	 * @param user
	 * @param project
	 * @return url
	 *
	 * @see #createGitHubUrl(String, String)
	 */
	public static String createGitHubUrlAlternate(String user, String project) {
		return HTTP_WWW_GITHUB_ORG + '/' + user + '/' + project;
	}
}
