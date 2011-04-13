/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.github.internal;

/**
 * GitHub constants
 */
public interface IGitHubConstants {

	/**
	 * REPOS_SEGMENT
	 */
	String SEGMENT_REPOS = "/repos"; //$NON-NLS-1$

	/**
	 * SEGMENT_ISSUES
	 */
	String SEGMENT_ISSUES = "/issues"; //$NON-NLS-1$

	/**
	 * SEGMENT_COMMENTS
	 */
	String SEGMENT_COMMENTS = "/comments"; //$NON-NLS-1$

	/**
	 * SEGMENT_LABELS
	 */
	String SEGMENT_LABELS = "/labels"; //$NON-NLS-1$

	/**
	 * SEGMENT_MILESTONES
	 */
	String SEGMENT_MILESTONES = "/milestones"; //$NON-NLS-1$

	/**
	 * SEGMENT_GISTS
	 */
	String SEGMENT_GISTS = "/gists"; //$NON-NLS-1$

	/**
	 * SEGMENT_USERS
	 */
	String SEGMENT_USERS = "/users"; //$NON-NLS-1$

	/**
	 * SUFFIX_JSON
	 */
	String SUFFIX_JSON = ".json"; //$NON-NLS-1$

	/**
	 * HOST_API
	 */
	String HOST_API = "api.github.com"; //$NON-NLS-1$

	/**
	 * PROTOCOL_HTTPS
	 */
	String PROTOCOL_HTTPS = "https"; //$NON-NLS-1$

	/**
	 * DATE_FORMAT
	 */
	String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"; //$NON-NLS-1$

	/**
	 * CONTENT_TYPE_JSON
	 */
	String CONTENT_TYPE_JSON = "application/json"; //$NON-NLS-1$

	/**
	 * CHARSET_UTF8
	 */
	String CHARSET_UTF8 = "UTF-8"; //$NON-NLS-1$

}
