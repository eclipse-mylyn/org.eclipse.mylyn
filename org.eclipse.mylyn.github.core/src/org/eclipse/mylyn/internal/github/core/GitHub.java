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
package org.eclipse.mylyn.internal.github.core;

import java.util.regex.Pattern;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.github.core.Repository;

/**
 * GitHub class
 */
public class GitHub {

	/** BUNDLE_ID */
	public static final String BUNDLE_ID = "org.eclipse.mylyn.github.core";

	/** CONNECTOR_KIND */
	public static final String CONNECTOR_KIND = "github";

	/** HTTP_WWW_GITHUB_ORG */
	public static final String HTTP_WWW_GITHUB_ORG = "http://www.github.org";

	/** HTTP_GITHUB_COM */
	public static final String HTTP_GITHUB_COM = "http://github.com";

	/** URL_PATTERN */
	public static final Pattern URL_PATTERN = Pattern.compile("(?:"
			+ Pattern.quote(HTTP_WWW_GITHUB_ORG) + "|"
			+ Pattern.quote(HTTP_GITHUB_COM) + ")/([^/]+)/([^/]+)");

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
				"Unexpected error: " + e.getLocalizedMessage(), e);
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
	public static Repository getRepository(String repositoryUrl) {
		return Repository.createFromUrl(repositoryUrl);
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
