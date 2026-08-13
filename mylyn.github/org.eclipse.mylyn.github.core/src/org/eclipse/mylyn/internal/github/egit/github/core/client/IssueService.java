/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *    See git history
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.egit.github.core.client;

import org.kohsuke.github.GHEventPayload.Issue;

/**
 * Issue service class for listing, searching, and fetching {@link Issue} objects using a {@link GitHubClient}.
 *
 * @see <a href="http://developer.github.com/v3/issues">GitHub Issues API documentation</a>
 */
public class IssueService {

	/**
	 * Filter field key
	 */
	public static final String FIELD_FILTER = "filter"; //$NON-NLS-1$

	/**
	 * Filter by issue assignee
	 */
	public static final String FILTER_ASSIGNEE = "assignee"; //$NON-NLS-1$

	/**
	 * Filter by issue's milestone
	 */
	public static final String FILTER_MILESTONE = "milestone"; //$NON-NLS-1$

	/**
	 * Filter by user mentioned in issue
	 */
	public static final String FILTER_MENTIONED = "mentioned"; //$NON-NLS-1$

	/**
	 * Filter by subscribed issues for user
	 */
	public static final String FILTER_SUBSCRIBED = "subscribed"; //$NON-NLS-1$

	/**
	 * Filter by created issues by user
	 */
	public static final String FILTER_CREATED = "created"; //$NON-NLS-1$

	/**
	 * Filter by assigned issues for user
	 */
	public static final String FILTER_ASSIGNED = "assigned"; //$NON-NLS-1$

	/**
	 * Filter by issue's labels
	 */
	public static final String FILTER_LABELS = "labels"; //$NON-NLS-1$

	/**
	 * Filter by issue's state
	 */
	public static final String FILTER_STATE = "state"; //$NON-NLS-1$

	/**
	 * Issue open state filter value
	 */
	public static final String STATE_OPEN = "open"; //$NON-NLS-1$

	/**
	 * Issue closed state filter value
	 */
	public static final String STATE_CLOSED = "closed"; //$NON-NLS-1$

	/**
	 * Issue body field name
	 */
	public static final String FIELD_BODY = "body"; //$NON-NLS-1$

	/**
	 * Issue title field name
	 */
	public static final String FIELD_TITLE = "title"; //$NON-NLS-1$

	/**
	 * Since date field
	 */
	public static final String FIELD_SINCE = "since"; //$NON-NLS-1$

	/**
	 * Sort direction of output
	 */
	public static final String FIELD_DIRECTION = "direction"; //$NON-NLS-1$

	/**
	 * Ascending direction sort order
	 */
	public static final String DIRECTION_ASCENDING = "asc"; //$NON-NLS-1$

	/**
	 * Descending direction sort order
	 */
	public static final String DIRECTION_DESCENDING = "desc"; //$NON-NLS-1$

	/**
	 * Sort field key
	 */
	public static final String FIELD_SORT = "sort"; //$NON-NLS-1$

	/**
	 * Sort by created at
	 */
	public static final String SORT_CREATED = "created"; //$NON-NLS-1$

	/**
	 * Sort by updated at
	 */
	public static final String SORT_UPDATED = "updated"; //$NON-NLS-1$

	/**
	 * Sort by commented on at
	 */
	public static final String SORT_COMMENTS = "comments"; //$NON-NLS-1$
}
