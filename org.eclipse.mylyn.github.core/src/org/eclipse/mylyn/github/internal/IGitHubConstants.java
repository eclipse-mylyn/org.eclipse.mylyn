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
	 * SEGMENT_SHOW
	 */
	String SEGMENT_SHOW = "/show"; //$NON-NLS-1$

	/**
	 * SEGMENT_ORGANIZATIONS
	 */
	String SEGMENT_ORGANIZATIONS = "/organizations"; //$NON-NLS-1$

	/**
	 * SEGMENT_REPOSITORIES
	 */
	String SEGMENT_REPOSITORIES = "/repositories"; //$NON-NLS-1$

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
	 * SEGMENT_PULLS
	 */
	String SEGMENT_PULLS = "/pulls"; //$NON-NLS-1$

	/**
	 * SEGMENT_USERS
	 */
	String SEGMENT_USERS = "/users"; //$NON-NLS-1$

	/**
	 * SEGMENT_V2_API
	 */
	String SEGMENT_V2_API = "/api/v2/json"; //$NON-NLS-1$

	/**
	 * HOST_API
	 */
	String HOST_API = "api.github.com"; //$NON-NLS-1$

	/**
	 * HOST_API_V2
	 */
	String HOST_API_V2 = "github.com"; //$NON-NLS-1$

	/**
	 * PROTOCOL_HTTPS
	 */
	String PROTOCOL_HTTPS = "https"; //$NON-NLS-1$

	/**
	 * DATE_FORMAT
	 */
	String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"; //$NON-NLS-1$

	/**
	 * DATE_FORMAT_V2_1
	 */
	String DATE_FORMAT_V2_1 = "yyyy/MM/dd HH:mm:ss Z"; //$NON-NLS-N$

	/**
	 * DATE_FORMAT_V2_2
	 */
	String DATE_FORMAT_V2_2 = "yyyy-MM-dd'T'HH:mm:ss"; //$NON-NLS-1$

	/**
	 * CONTENT_TYPE_JSON
	 */
	String CONTENT_TYPE_JSON = "application/json"; //$NON-NLS-1$

	/**
	 * CHARSET_UTF8
	 */
	String CHARSET_UTF8 = "UTF-8"; //$NON-NLS-1$

	/**
	 * PARAM_PER_PAGE
	 */
	String PARAM_PER_PAGE = "per_page"; //$NON-NLS-1$

	/**
	 * PARAM_PAGE
	 */
	String PARAM_PAGE = "page"; //$NON-NLS-1$

	/**
	 * HEADER_LINK
	 */
	String HEADER_LINK = "Link"; //$NON-NLS-1$

	/**
	 * HEADER_NEXT
	 */
	String HEADER_NEXT = "X-Next"; //$NON-NLS-1$

	/**
	 * HEADER_LAST
	 */
	String HEADER_LAST = "X-Last"; //$NON-NLS-1$

	/**
	 * META_REL
	 */
	String META_REL = "rel"; //$NON-NLS-1$

	/**
	 * META_LAST
	 */
	String META_LAST = "last"; //$NON-NLS-1$

	/**
	 * META_NEXT
	 */
	String META_NEXT = "next"; //$NON-NLS-1$

	/**
	 * META_FIRST
	 */
	String META_FIRST = "first"; //$NON-NLS-1$

	/**
	 * META_PREV
	 */
	String META_PREV = "prev"; //$NON-NLS-1$

}
