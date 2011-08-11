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
package org.eclipse.egit.github.core.client;

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
	 * SEGMENT_SEARCH
	 */
	String SEGMENT_SEARCH = "/search"; //$NON-NLS-1$

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
	 * SEGMENT_STARRED
	 */
	String SEGMENT_STARRED = "/starred"; //$NON-NLS-1$

	/**
	 * SEGMENT_STAR
	 */
	String SEGMENT_STAR = "/star"; //$NON-NLS-1$

	/**
	 * SEGMENT_PUBLIC
	 */
	String SEGMENT_PUBLIC = "/public"; //$NON-NLS-1$

	/**
	 * SEGMENT_PULLS
	 */
	String SEGMENT_PULLS = "/pulls"; //$NON-NLS-1$

	/**
	 * SEGMENT_USER
	 */
	String SEGMENT_USER = "/user"; //$NON-NLS-1$

	/**
	 * SEGMENT_USERS
	 */
	String SEGMENT_USERS = "/users"; //$NON-NLS-1$

	/**
	 * SEGMENT_CREATE
	 */
	String SEGMENT_CREATE = "/create"; //$NON-NLS-1$

	/**
	 * SEGMENT_FOLLOWERS
	 */
	String SEGMENT_FOLLOWERS = "/followers"; //$NON-NLS-1$

	/**
	 * SEGMENT_FOLLOWERS
	 */
	String SEGMENT_FOLLOWING = "/following"; //$NON-NLS-1$

	/**
	 * SEGMENT_ORGS
	 */
	String SEGMENT_ORGS = "/orgs"; //$NON-NLS-1$

	/**
	 * SEGMENT_MEMBERS
	 */
	String SEGMENT_MEMBERS = "/members"; //$NON-NLS-1$

	/**
	 * SEGMENT_PUBLIC_MEMBERS
	 */
	String SEGMENT_PUBLIC_MEMBERS = "/public_members"; //$NON-NLS-1$

	/**
	 * SEGMENT_TEAMS
	 */
	String SEGMENT_TEAMS = "/teams"; //$NON-NLS-1$

	/**
	 * SEGMENT_COMMITS
	 */
	String SEGMENT_COMMITS = "/commits"; //$NON-NLS-1$

	/**
	 * SEGMENT_FILES
	 */
	String SEGMENT_FILES = "/files"; //$NON-NLS-1$

	/**
	 * SEGMENT_DOWNLOADS
	 */
	String SEGMENT_DOWNLOADS = "/downloads"; //$NON-NLS-1$

	/**
	 * SEGMENT_GIT
	 */
	String SEGMENT_GIT = "/git"; //$NON-NLS-1$

	/**
	 * SEGMENT_BLOBS
	 */
	String SEGMENT_BLOBS = "/blobs"; //$NON-NLS-1$

	/**
	 * SEGMENT_TREES
	 */
	String SEGMENT_TREES = "/trees"; //$NON-NLS-1$

	/**
	 * SEGMENT_REFS
	 */
	String SEGMENT_REFS = "/refs"; //$NON-NLS-1$

	/**
	 * SEGMENT_TAGS
	 */
	String SEGMENT_TAGS = "/tags"; //$NON-NLS-1$

	/**
	 * SEGMENT_V2_API
	 */
	String SEGMENT_V2_API = "/api/v2/json"; //$NON-NLS-1$

	/**
	 * SUBDOMAIN_API
	 */
	String SUBDOMAIN_API = "api"; //$NON-NLS-1$

	/**
	 * HOST_API
	 */
	String HOST_API = SUBDOMAIN_API + ".github.com"; //$NON-NLS-1$

	/**
	 * HOST_API_V2
	 */
	String HOST_API_V2 = "github.com"; //$NON-NLS-1$

	/**
	 * HOST_DEFAULT
	 */
	String HOST_DEFAULT = "github.com"; //$NON-NLS-1$

	/**
	 * PROTOCOL_HTTPS
	 */
	String PROTOCOL_HTTPS = "https"; //$NON-NLS-1$

	/**
	 * URL_API
	 */
	String URL_API = PROTOCOL_HTTPS + "://" + HOST_API; //$NON-NLS-1$

	/**
	 * URL_API_V2
	 */
	String URL_API_V2 = PROTOCOL_HTTPS + "://" + HOST_API_V2; //$NON-NLS-1$

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
	 * PARAM_LANGUAGE
	 */
	String PARAM_LANGUAGE = "language"; //$NON-NLS-1$

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

	/**
	 * SUFFIX_GIT
	 */
	String SUFFIX_GIT = ".git"; //$NON-NLS-1$

	/**
	 * AUTH_TOKEN
	 */
	String AUTH_TOKEN = "token"; //$NON-NLS-1$

	/**
	 * SCHEME_OAUTH2
	 */
	String SCHEME_OAUTH2 = "oauth2"; //$NON-NLS-1$
}
