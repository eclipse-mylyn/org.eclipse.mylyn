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

import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_COMMENTS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_EVENTS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_ISSUES;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_REPOS;
import static org.eclipse.egit.github.core.client.PagedRequest.PAGE_FIRST;
import static org.eclipse.egit.github.core.client.PagedRequest.PAGE_SIZE;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.IssueEvent;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.client.PagedRequest;

/**
 * Issue service class for listing, searching, and fetching {@link Issue}
 * objects using a {@link GitHubClient}.
 *
 * @see <a href="http://developer.github.com/v3/issues">GitHub Issues API
 *      documentation</a>
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
	 * Filter by subscribed issues for user
	 */
	public static final String FILTER_SUBSCRIBED = "subscribed"; //$NON-NLS-1$

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
	 * Create issue service
	 */
	public IssueService() {
		super();
	}

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
	 * Get issues for currently authenticated user
	 *
	 * @return non-null but possibly empty list of issues
	 * @throws IOException
	 */
	public List<Issue> getIssues() throws IOException {
		return getIssues(null);
	}

	/**
	 * Get issues for currently authenticated user
	 *
	 * @param filterData
	 * @return non-null but possibly empty list of issues
	 * @throws IOException
	 */
	public List<Issue> getIssues(Map<String, String> filterData)
			throws IOException {
		return getAll(pageIssues(filterData));
	}

	/**
	 * Page issues for currently authenticated user
	 *
	 * @return iterator over pages of issues
	 */
	public PageIterator<Issue> pageIssues() {
		return pageIssues(null);
	}

	/**
	 * Page issues for currently authenticated user
	 *
	 * @param filterData
	 * @return iterator over pages of issues
	 */
	public PageIterator<Issue> pageIssues(Map<String, String> filterData) {
		return pageIssues(filterData, PAGE_SIZE);
	}

	/**
	 * Page issues for currently authenticated user
	 *
	 * @param filterData
	 * @param size
	 * @return iterator over pages of issues
	 */
	public PageIterator<Issue> pageIssues(Map<String, String> filterData,
			int size) {
		return pageIssues(filterData, PAGE_FIRST, size);
	}

	/**
	 * Page issues for currently authenticated user
	 *
	 * @param filterData
	 * @param start
	 * @param size
	 * @return iterator over pages of issues
	 */
	public PageIterator<Issue> pageIssues(Map<String, String> filterData,
			int start, int size) {
		PagedRequest<Issue> request = createPagedRequest(start, size);
		request.setParams(filterData);
		request.setUri(SEGMENT_ISSUES);
		request.setType(new TypeToken<List<Issue>>() {
		}.getType());
		return createPageIterator(request);
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
	public Issue getIssue(String user, String repository, int id)
			throws IOException {
		return getIssue(user, repository, Integer.toString(id));
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
		verifyRepository(user, repository);
		if (id == null)
			throw new IllegalArgumentException("Id cannot be null"); //$NON-NLS-1$
		if (id.length() == 0)
			throw new IllegalArgumentException("Id cannot be empty"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(SEGMENT_ISSUES);
		uri.append('/').append(id);
		GitHubRequest request = createRequest();
		request.setUri(uri);
		request.setType(Issue.class);
		return (Issue) client.get(request).getBody();
	}

	/**
	 * Get an issue's comments
	 *
	 * @param user
	 * @param repository
	 * @param id
	 * @return list of comments
	 * @throws IOException
	 */
	public List<Comment> getComments(String user, String repository, int id)
			throws IOException {
		return getComments(user, repository, Integer.toString(id));
	}

	/**
	 * Get an issue's comments
	 *
	 * @param user
	 * @param repository
	 * @param id
	 * @return list of comments
	 * @throws IOException
	 */
	public List<Comment> getComments(String user, String repository, String id)
			throws IOException {
		verifyRepository(user, repository);
		if (id == null)
			throw new IllegalArgumentException("Id cannot be null"); //$NON-NLS-1$
		if (id.length() == 0)
			throw new IllegalArgumentException("Id cannot be empty"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(SEGMENT_ISSUES);
		uri.append('/').append(id);
		uri.append(SEGMENT_COMMENTS);
		PagedRequest<Comment> request = createPagedRequest();
		request.setUri(uri);
		request.setType(new TypeToken<List<Comment>>() {
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
		verifyRepository(user, repository);

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(SEGMENT_ISSUES);
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
				filterData, PAGE_FIRST, PAGE_SIZE);
		return getAll(request);
	}

	/**
	 * Get page iterator over issues query
	 *
	 * @param user
	 * @param repository
	 * @return iterator over issue pages
	 */
	public PageIterator<Issue> pageIssues(String user, String repository) {
		return pageIssues(user, repository, null);
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
		return pageIssues(user, repository, filterData, PAGE_SIZE);
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
		return pageIssues(user, repository, filterData, PAGE_FIRST, size);
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
		verifyRepository(user, repository);

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(SEGMENT_ISSUES);

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
		verifyRepository(user, repository);
		if (issue == null)
			throw new IllegalArgumentException("Issue cannot be null"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(SEGMENT_ISSUES);
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
	public Comment createComment(String user, String repository, int issueId,
			String comment) throws IOException {
		return createComment(user, repository, Integer.toString(issueId),
				comment);
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
		verifyRepository(user, repository);
		if (issueId == null)
			throw new IllegalArgumentException("Issue id cannot be null"); //$NON-NLS-1$
		if (issueId.length() == 0)
			throw new IllegalArgumentException("Issue id cannot be empty"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(SEGMENT_ISSUES);
		uri.append('/').append(issueId);
		uri.append(SEGMENT_COMMENTS);

		Map<String, String> params = new HashMap<String, String>(1, 1);
		params.put(FIELD_BODY, comment);

		return client.post(uri.toString(), params, Comment.class);
	}

	/**
	 * Get issue comment
	 *
	 * @param user
	 * @param repository
	 * @param commentId
	 * @return comment
	 * @throws IOException
	 */
	public Comment getComment(String user, String repository, long commentId)
			throws IOException {
		verifyRepository(user, repository);

		GitHubRequest request = createRequest();
		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(SEGMENT_ISSUES).append(SEGMENT_COMMENTS);
		uri.append('/').append(commentId);
		request.setUri(uri);
		request.setType(Comment.class);
		return (Comment) client.get(request).getBody();
	}

	/**
	 * Edit issue comment
	 *
	 * @param user
	 * @param repository
	 * @param comment
	 * @return edited comment
	 * @throws IOException
	 */
	public Comment editComment(String user, String repository, Comment comment)
			throws IOException {
		verifyRepository(user, repository);
		if (comment == null)
			throw new IllegalArgumentException("Comment cannot be null");

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(SEGMENT_ISSUES).append(SEGMENT_COMMENTS);
		uri.append('/').append(comment.getId());
		return client.post(uri.toString(), comment, Comment.class);
	}

	/**
	 * Delete the issue comment with the given id
	 *
	 * @param user
	 * @param repository
	 * @param commentId
	 * @throws IOException
	 */
	public void deleteComment(String user, String repository, long commentId)
			throws IOException {
		deleteComment(user, repository, Long.toString(commentId));
	}

	/**
	 * Delete the issue comment with the given id
	 *
	 * @param user
	 * @param repository
	 * @param commentId
	 * @throws IOException
	 */
	public void deleteComment(String user, String repository, String commentId)
			throws IOException {
		verifyRepository(user, repository);
		if (commentId == null)
			throw new IllegalArgumentException("Comment cannot be null"); //$NON-NLS-1$
		if (commentId.length() == 0)
			throw new IllegalArgumentException("Comment cannot be empty"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(SEGMENT_ISSUES).append(SEGMENT_COMMENTS);
		uri.append('/').append(commentId);
		client.delete(uri.toString());
	}

	/**
	 * Page issue events for repository
	 *
	 * @param user
	 * @param repository
	 * @return iterator over issue event pages
	 * @throws IOException
	 */
	public PageIterator<IssueEvent> pageEvents(String user, String repository)
			throws IOException {
		return pageEvents(user, repository, PAGE_SIZE);
	}

	/**
	 * Page issue events for repository
	 *
	 * @param user
	 * @param repository
	 * @param size
	 * @return iterator over issue event pages
	 * @throws IOException
	 */
	public PageIterator<IssueEvent> pageEvents(String user, String repository,
			int size) throws IOException {
		return pageEvents(user, repository, PAGE_FIRST, size);
	}

	/**
	 * Page events for issue in repository
	 *
	 * @param user
	 * @param repository
	 * @param start
	 * @param size
	 * @return iterator over issue event pages
	 * @throws IOException
	 */
	public PageIterator<IssueEvent> pageEvents(String user, String repository,
			int start, int size) throws IOException {
		verifyRepository(user, repository);

		PagedRequest<IssueEvent> request = createPagedRequest(start, size);
		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(SEGMENT_ISSUES);
		uri.append(SEGMENT_EVENTS);
		request.setUri(uri);
		request.setType(new TypeToken<List<IssueEvent>>() {
		}.getType());
		return createPageIterator(request);
	}

	/**
	 * Page events for issue in repository
	 *
	 * @param user
	 * @param repository
	 * @param issueId
	 * @return iterator over issue event pages
	 * @throws IOException
	 */
	public PageIterator<IssueEvent> pageIssueEvents(String user,
			String repository, int issueId) throws IOException {
		return pageIssueEvents(user, repository, issueId, PAGE_SIZE);
	}

	/**
	 * Page events for issue in repository
	 *
	 * @param user
	 * @param repository
	 * @param issueId
	 * @param size
	 * @return iterator over issue event pages
	 * @throws IOException
	 */
	public PageIterator<IssueEvent> pageIssueEvents(String user,
			String repository, int issueId, int size) throws IOException {
		return pageIssueEvents(user, repository, issueId, PAGE_FIRST, size);
	}

	/**
	 * Page issue events for repository
	 *
	 * @param user
	 * @param repository
	 * @param issueId
	 * @param start
	 * @param size
	 * @return iterator over issue event pages
	 * @throws IOException
	 */
	public PageIterator<IssueEvent> pageIssueEvents(String user,
			String repository, int issueId, int start, int size)
			throws IOException {
		verifyRepository(user, repository);

		PagedRequest<IssueEvent> request = createPagedRequest(start, size);
		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(SEGMENT_ISSUES);
		uri.append('/').append(issueId);
		uri.append(SEGMENT_EVENTS);
		request.setUri(uri);
		request.setType(new TypeToken<List<IssueEvent>>() {
		}.getType());
		return createPageIterator(request);
	}

	/**
	 * Get issue event for repository
	 *
	 * @param user
	 * @param repository
	 * @param eventId
	 * @return iterator over issue event pages
	 * @throws IOException
	 */
	public IssueEvent getIssueEvent(String user, String repository, long eventId)
			throws IOException {
		verifyRepository(user, repository);

		GitHubRequest request = createRequest();
		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(user).append('/').append(repository);
		uri.append(SEGMENT_ISSUES);
		uri.append(SEGMENT_EVENTS);
		uri.append('/').append(eventId);
		request.setUri(uri);
		request.setType(IssueEvent.class);
		return (IssueEvent) client.get(request).getBody();
	}
}
