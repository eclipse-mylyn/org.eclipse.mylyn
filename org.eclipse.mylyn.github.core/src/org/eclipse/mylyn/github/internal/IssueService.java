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

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

/**
 * Issue service class for listing, searching, and fetching {@link Issue}
 * objects using a {@link GitHubClient}.
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class IssueService {

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
	public static final String STATE_OPEN = "open";

	/**
	 * Issue closed state filter value
	 */
	public static final String STATE_CLOSED = "closed";

	/**
	 * Issue body field name
	 */
	public static final String FIELD_BODY = "body";

	/**
	 * Issue title field name
	 */
	public static final String FIELD_TITLE = "title";

	private GitHubClient client;

	/**
	 * Create issue service
	 *
	 * @param client
	 *            cannot be null
	 */
	public IssueService(GitHubClient client) {
		Assert.isNotNull(client, "Client cannot be null"); //$NON-NLS-1$
		this.client = client;
	}

	/**
	 * Get issue
	 *
	 * @param user
	 * @param repository
	 * @param id
	 * @return issue
	 * @throws IOException
	 */
	public Issue getIssue(String user, String repository, String id)
			throws IOException {
		StringBuilder builder = new StringBuilder(
				IGitHubConstants.SEGMENT_REPOS);
		builder.append('/').append(user).append('/').append(repository);
		builder.append(IGitHubConstants.SEGMENT_ISSUES);
		builder.append('/').append(id).append(IGitHubConstants.SUFFIX_JSON);
		return this.client.get(builder.toString(), Issue.class);
	}

	/**
	 * Get an issue's comments
	 *
	 * @param user
	 * @param repository
	 * @param id
	 * @return list of matching issues
	 * @throws IOException
	 */
	public List<Comment> getComments(String user, String repository, String id)
			throws IOException {
		StringBuilder builder = new StringBuilder(
				IGitHubConstants.SEGMENT_REPOS);
		builder.append('/').append(user).append('/').append(repository);
		builder.append(IGitHubConstants.SEGMENT_ISSUES);
		builder.append('/').append(id);
		builder.append(IGitHubConstants.SEGMENT_COMMENTS).append(
				IGitHubConstants.SUFFIX_JSON);
		TypeToken<List<Comment>> commentToken = new TypeToken<List<Comment>>() {
		};
		return this.client.get(builder.toString(), commentToken.getType());
	}

	/**
	 * Get a list of {@link Issue} objects that match the specified filter data
	 *
	 * @param user
	 * @param repository
	 * @param filterData
	 * @return list of issues
	 * @throws IOException
	 */
	public List<Issue> getIssues(String user, String repository,
			Map<String, String> filterData) throws IOException {
		StringBuilder builder = new StringBuilder(
				IGitHubConstants.SEGMENT_REPOS);
		builder.append('/').append(user).append('/').append(repository);
		builder.append(IGitHubConstants.SEGMENT_ISSUES).append(
				IGitHubConstants.SUFFIX_JSON);
		TypeToken<List<Issue>> issueToken = new TypeToken<List<Issue>>() {
		};
		return this.client.get(builder.toString(), filterData,
				issueToken.getType());
	}

	/**
	 * Create issue
	 *
	 * @param user
	 * @param repository
	 * @param issue
	 * @return created issue
	 * @throws IOException
	 */
	public Issue createIssue(String user, String repository, Issue issue)
			throws IOException {
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(IGitHubConstants.SEGMENT_ISSUES).append(
				IGitHubConstants.SUFFIX_JSON);

		Map<String, String> params = new HashMap<String, String>();
		params.put(FIELD_BODY, issue.getBody());
		params.put(FIELD_TITLE, issue.getTitle());
		User assignee = issue.getAssignee();
		if (assignee != null) {
			params.put(FILTER_ASSIGNEE, assignee.getName());
		}
		Milestone milestone = issue.getMilestone();
		if (milestone != null) {
			params.put(FILTER_MILESTONE,
					Integer.toString(milestone.getNumber()));
		}

		return this.client.post(uri.toString(), params, Issue.class);
	}

}
