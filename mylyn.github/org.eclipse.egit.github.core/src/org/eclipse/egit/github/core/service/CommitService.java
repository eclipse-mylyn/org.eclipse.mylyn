/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.service;

import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_COMMENTS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_COMMITS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_REPOS;
import static org.eclipse.egit.github.core.client.PagedRequest.PAGE_FIRST;
import static org.eclipse.egit.github.core.client.PagedRequest.PAGE_SIZE;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.client.PagedRequest;

/**
 * Service for interacting with repository commits
 *
 * @see <a href="http://developer.github.com/v3/repos/commits">GitHub commit API
 *      documentation</a>
 */
public class CommitService extends GitHubService {

	/**
	 * @param client
	 */
	public CommitService(GitHubClient client) {
		super(client);
	}

	/**
	 * Get all commits in given repository
	 *
	 * @param repository
	 * @return non-null but possibly empty list of repository commits
	 * @throws IOException
	 */
	public List<RepositoryCommit> getCommits(IRepositoryIdProvider repository)
			throws IOException {
		return getCommits(repository, null, null);
	}

	/**
	 * Get all commits in given repository
	 *
	 * @param repository
	 * @param sha
	 * @param path
	 * @return non-null but possibly empty list of repository commits
	 * @throws IOException
	 */
	public List<RepositoryCommit> getCommits(IRepositoryIdProvider repository,
			String sha, String path) throws IOException {
		return getAll(pageCommits(repository, sha, path));
	}

	/**
	 * Page commits in given repository
	 *
	 * @param repository
	 * @return page iterator
	 */
	public PageIterator<RepositoryCommit> pageCommits(
			IRepositoryIdProvider repository) {
		return pageCommits(repository, null, null);
	}

	/**
	 * Page commits in given repository
	 *
	 * @param repository
	 * @param size
	 * @return page iterator
	 */
	public PageIterator<RepositoryCommit> pageCommits(
			IRepositoryIdProvider repository, int size) {
		return pageCommits(repository, null, null, size);
	}

	/**
	 * Page commits in given repository
	 *
	 * @param repository
	 * @param sha
	 * @param path
	 * @return page iterator
	 */
	public PageIterator<RepositoryCommit> pageCommits(
			IRepositoryIdProvider repository, String sha, String path) {
		return pageCommits(repository, sha, path, PAGE_SIZE);
	}

	/**
	 * Page commits in given repository
	 *
	 * @param repository
	 * @param sha
	 * @param path
	 * @param size
	 * @return page iterator
	 */
	public PageIterator<RepositoryCommit> pageCommits(
			IRepositoryIdProvider repository, String sha, String path, int size) {
		String id = getId(repository);
		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_COMMITS);
		PagedRequest<RepositoryCommit> request = createPagedRequest(PAGE_FIRST,
				size);
		request.setUri(uri);
		request.setType(new TypeToken<List<RepositoryCommit>>() {
		}.getType());

		if (sha != null || path != null) {
			Map<String, String> params = new HashMap<String, String>();
			if (sha != null)
				params.put("sha", sha); //$NON-NLS-1$
			if (path != null)
				params.put("path", path); //$NON-NLS-1$
			request.setParams(params);
		}

		return createPageIterator(request);
	}

	/**
	 * Get commit with given SHA-1 from given repository
	 *
	 * @param repository
	 * @param sha
	 * @return repository commit
	 * @throws IOException
	 */
	public RepositoryCommit getCommit(IRepositoryIdProvider repository,
			String sha) throws IOException {
		String id = getId(repository);
		if (sha == null)
			throw new IllegalArgumentException("Sha cannot be null"); //$NON-NLS-1$
		if (sha.length() == 0)
			throw new IllegalArgumentException("Sha cannot be empty"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_COMMITS);
		uri.append('/').append(sha);
		GitHubRequest request = createRequest();
		request.setUri(uri);
		request.setType(RepositoryCommit.class);
		return (RepositoryCommit) client.get(request).getBody();
	}

	/**
	 * Get all comments on commit with given SHA-1
	 *
	 * @param repository
	 * @param sha
	 * @return non-null but possibly empty list of commits
	 * @throws IOException
	 */
	public List<CommitComment> getComments(IRepositoryIdProvider repository,
			String sha) throws IOException {
		return getAll(pageComments(repository, sha));
	}

	/**
	 * Page comments on commit with given SHA-1
	 *
	 * @param repository
	 * @param sha
	 * @return page iterator over comments
	 */
	public PageIterator<CommitComment> pageComments(
			IRepositoryIdProvider repository, String sha) {
		return pageComments(repository, sha, PAGE_SIZE);
	}

	/**
	 * Page comments on commit with given SHA-1
	 *
	 * @param repository
	 * @param sha
	 * @param size
	 * @return page iterator over comments
	 */
	public PageIterator<CommitComment> pageComments(
			IRepositoryIdProvider repository, String sha, int size) {
		return pageComments(repository, sha, PAGE_FIRST, size);
	}

	/**
	 * Page comments on commit with given SHA-1
	 *
	 * @param repository
	 * @param sha
	 * @param start
	 * @param size
	 * @return page iterator over comments
	 */
	public PageIterator<CommitComment> pageComments(
			IRepositoryIdProvider repository, String sha, int start, int size) {
		String id = getId(repository);
		if (sha == null)
			throw new IllegalArgumentException("Sha cannot be null"); //$NON-NLS-1$
		if (sha.length() == 0)
			throw new IllegalArgumentException("Sha cannot be empty"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_COMMITS);
		uri.append('/').append(sha);
		uri.append(SEGMENT_COMMENTS);
		PagedRequest<CommitComment> request = createPagedRequest(start, size);
		request.setUri(uri);
		request.setType(new TypeToken<List<CommitComment>>() {
		}.getType());
		return createPageIterator(request);
	}

	/**
	 * Get commit comment with given id
	 *
	 * @param repository
	 * @param id
	 * @return commit comment
	 * @throws IOException
	 */
	public CommitComment getComment(IRepositoryIdProvider repository, String id)
			throws IOException {
		String repoId = getId(repository);
		if (id == null)
			throw new IllegalArgumentException("Id cannot be null"); //$NON-NLS-1$
		if (id.length() == 0)
			throw new IllegalArgumentException("Id cannot be empty"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(repoId);
		uri.append(SEGMENT_COMMENTS);
		uri.append('/').append(id);
		GitHubRequest request = createRequest();
		request.setUri(uri);
		request.setType(CommitComment.class);
		return (CommitComment) client.get(request).getBody();
	}

	/**
	 * Add comment to given commit
	 *
	 * @param repository
	 * @param sha
	 * @param comment
	 * @return created comment
	 * @throws IOException
	 */
	public CommitComment addComment(IRepositoryIdProvider repository,
			String sha, CommitComment comment) throws IOException {
		String id = getId(repository);
		if (sha == null)
			throw new IllegalArgumentException("Sha cannot be null"); //$NON-NLS-1$
		if (sha.length() == 0)
			throw new IllegalArgumentException("Sha cannot be empty"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_COMMITS);
		uri.append('/').append(sha);
		uri.append(SEGMENT_COMMENTS);
		return client.post(uri.toString(), comment, CommitComment.class);
	}

	/**
	 * Edit given comment
	 *
	 * @param repository
	 * @param comment
	 * @return edited comment
	 * @throws IOException
	 */
	public CommitComment editComment(IRepositoryIdProvider repository,
			CommitComment comment) throws IOException {
		String id = getId(repository);
		if (comment == null)
			throw new IllegalArgumentException("Comment cannot be null"); //$NON-NLS-1$
		String commentId = comment.getId();
		if (commentId == null)
			throw new IllegalArgumentException("Comment id cannot be null"); //$NON-NLS-1$
		if (commentId.length() == 0)
			throw new IllegalArgumentException("Comment id cannot be empty"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_COMMENTS);
		uri.append('/').append(commentId);
		Map<String, String> params = Collections.singletonMap(
				"body", comment.getBody()); //$NON-NLS-1$
		return client.post(uri.toString(), params, CommitComment.class);
	}

	/**
	 * Delete commit comment with given id from given repository
	 *
	 * @param repository
	 * @param commentId
	 * @throws IOException
	 */
	public void deleteComment(IRepositoryIdProvider repository, String commentId)
			throws IOException {
		String id = getId(repository);
		if (commentId == null)
			throw new IllegalArgumentException("Comment Id cannot be null"); //$NON-NLS-1$
		if (commentId.length() == 0)
			throw new IllegalArgumentException("Comment Id cannot be empty"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_COMMENTS);
		uri.append('/').append(commentId);
		client.delete(uri.toString());
	}
}
