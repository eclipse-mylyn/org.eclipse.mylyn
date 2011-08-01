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
package org.eclipse.egit.github.core.service;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.Assert;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.client.PagedRequest;

/**
 * Issue service class for listing, searching, and fetching {@link Issue}
 * objects using a {@link GitHubClient}.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class IssueService extends GitHubService {

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
	 * Create issue service
	 * 
	 * @param client
	 *            cannot be null
	 */
	public IssueService(GitHubClient client) {
		super(client);
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
		Assert.notNull("User cannot be null", user); //$NON-NLS-1$
		Assert.notNull("Repository cannot be null", repository); //$NON-NLS-1$
		Assert.notNull("Id cannot be null", id); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(IGitHubConstants.SEGMENT_ISSUES);
		uri.append('/').append(id);
		GitHubRequest request = createRequest().setUri(uri)
				.setType(Issue.class);
		return (Issue) client.get(request).getBody();
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
		Assert.notNull("User cannot be null", user); //$NON-NLS-1$
		Assert.notNull("Repository cannot be null", repository); //$NON-NLS-1$
		Assert.notNull("Id cannot be null", id); //$NON-NLS-1$
		StringBuilder builder = new StringBuilder(
				IGitHubConstants.SEGMENT_REPOS);
		builder.append('/').append(user).append('/').append(repository);
		builder.append(IGitHubConstants.SEGMENT_ISSUES);
		builder.append('/').append(id);
		builder.append(IGitHubConstants.SEGMENT_COMMENTS);
		PagedRequest<Comment> request = createPagedRequest();
		request.setUri(builder.toString()).setType(
				new TypeToken<List<Comment>>() {
				}.getType());
		return getAll(request);
	}

	/**
	 * Get bulk issues request
	 * 
	 * @param user
	 * @param repository
	 * @param filterData
	 * @param start
	 * @param size
	 * @return paged request
	 */
	protected PagedRequest<Issue> createIssuesRequest(String user,
			String repository, Map<String, String> filterData, int start,
			int size) {
		Assert.notNull("User cannot be null", user); //$NON-NLS-1$
		Assert.notNull("Repository cannot be null", repository); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(IGitHubConstants.SEGMENT_ISSUES);
		PagedRequest<Issue> request = createPagedRequest(start, size);
		request.setParams(filterData).setUri(uri);
		request.setType(new TypeToken<List<Issue>>() {
		}.getType());
		return request;
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
		PagedRequest<Issue> request = createIssuesRequest(user, repository,
				filterData, PagedRequest.PAGE_FIRST, PagedRequest.PAGE_SIZE);
		return getAll(request);
	}

	/**
	 * Get page iterator over issues query
	 * 
	 * @param user
	 * @param repository
	 * @param filterData
	 * @return iterator
	 */
	public PageIterator<Issue> pageIssues(String user, String repository,
			Map<String, String> filterData) {
		return pageIssues(user, repository, filterData, PagedRequest.PAGE_SIZE);
	}

	/**
	 * Get page iterator over issues query
	 * 
	 * @param user
	 * @param repository
	 * @param filterData
	 * @param size
	 * @return iterator
	 */
	public PageIterator<Issue> pageIssues(String user, String repository,
			Map<String, String> filterData, int size) {
		return pageIssues(user, repository, filterData,
				PagedRequest.PAGE_FIRST, size);
	}

	/**
	 * Get page iterator over issues query
	 * 
	 * @param user
	 * @param repository
	 * @param filterData
	 * @param size
	 *            page size
	 * @param start
	 *            starting page number
	 * @return iterator
	 */
	public PageIterator<Issue> pageIssues(String user, String repository,
			Map<String, String> filterData, int start, int size) {
		PagedRequest<Issue> request = createIssuesRequest(user, repository,
				filterData, start, size);
		return createPageIterator(request);
	}

	/**
	 * Create issue map for issue
	 * 
	 * @param issue
	 * @param newIssue
	 * @return map
	 */
	protected Map<Object, Object> createIssueMap(Issue issue, boolean newIssue) {
		Map<Object, Object> params = new HashMap<Object, Object>();
		if (issue != null) {
			params.put(FIELD_BODY, issue.getBody());
			params.put(FIELD_TITLE, issue.getTitle());
			User assignee = issue.getAssignee();
			if (assignee != null)
				params.put(FILTER_ASSIGNEE, assignee.getName());

			Milestone milestone = issue.getMilestone();
			if (milestone != null) {
				int number = milestone.getNumber();
				if (number > 0)
					params.put(FILTER_MILESTONE, Integer.toString(number));
				else {
					if (!newIssue)
						params.put(FILTER_MILESTONE, null);
				}
			}
			List<Label> labels = issue.getLabels();
			if (labels != null) {
				List<String> labelNames = new ArrayList<String>(labels.size());
				for (Label label : labels)
					labelNames.add(label.getName());
				params.put(FILTER_LABELS, labelNames);
			}
		}
		return params;
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
		Assert.notNull("User cannot be null", user); //$NON-NLS-1$
		Assert.notNull("Repository cannot be null", repository); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(IGitHubConstants.SEGMENT_ISSUES);

		Map<Object, Object> params = createIssueMap(issue, true);
		return client.post(uri.toString(), params, Issue.class);
	}

	/**
	 * Edit issue
	 * 
	 * @param user
	 * @param repository
	 * @param issue
	 * @return created issue
	 * @throws IOException
	 */
	public Issue editIssue(String user, String repository, Issue issue)
			throws IOException {
		Assert.notNull("User cannot be null", user); //$NON-NLS-1$
		Assert.notNull("Repository cannot be null", repository); //$NON-NLS-1$
		Assert.notNull("Issue cannot be null", issue); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(IGitHubConstants.SEGMENT_ISSUES);
		uri.append('/').append(issue.getNumber());

		Map<Object, Object> params = createIssueMap(issue, false);
		String state = issue.getState();
		if (state != null)
			params.put(FILTER_STATE, state);
		return client.post(uri.toString(), params, Issue.class);
	}

	/**
	 * Create comment on specified issue id
	 * 
	 * @param user
	 * @param repository
	 * @param issueId
	 * @param comment
	 * @return created issue
	 * @throws IOException
	 */
	public Comment createComment(String user, String repository,
			String issueId, String comment) throws IOException {
		Assert.notNull("User cannot be null", user); //$NON-NLS-1$
		Assert.notNull("Repository cannot be null", repository); //$NON-NLS-1$
		Assert.notNull("Issue id cannot be null", issueId); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(IGitHubConstants.SEGMENT_ISSUES);
		uri.append('/').append(issueId);
		uri.append(IGitHubConstants.SEGMENT_COMMENTS);

		Map<String, String> params = new HashMap<String, String>(1, 1);
		params.put(FIELD_BODY, comment);

		return client.post(uri.toString(), params, Comment.class);
	}

	/**
	 * Delete the issue comment with the given id
	 * 
	 * @param user
	 * @param repository
	 * @param comment
	 * @throws IOException
	 */
	public void deleteComment(String user, String repository, String comment)
			throws IOException {
		Assert.notNull("User cannot be null", user); //$NON-NLS-1$
		Assert.notNull("Repository cannot be null", repository); //$NON-NLS-1$
		Assert.notNull("Comment cannot be null", comment); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(IGitHubConstants.SEGMENT_ISSUES);
		uri.append(IGitHubConstants.SEGMENT_COMMENTS);
		uri.append('/').append(comment);
		client.delete(uri.toString());
	}
}
