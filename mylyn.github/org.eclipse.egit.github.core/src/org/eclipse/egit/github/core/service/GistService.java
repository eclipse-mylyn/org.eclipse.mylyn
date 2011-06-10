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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.eclipse.egit.github.core.Assert;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.client.PagedRequest;
import org.eclipse.egit.github.core.client.RequestException;

/**
 * Service class for getting and list gists.
 */
public class GistService extends GitHubService {

	/**
	 * Create gist service
	 * 
	 * @param client
	 */
	public GistService(GitHubClient client) {
		super(client);
	}

	/**
	 * Get gist
	 * 
	 * @param id
	 * @return gist
	 * @throws IOException
	 */
	public Gist getGist(String id) throws IOException {
		Assert.notNull("Gist id cannot be null", id); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_GISTS);
		uri.append('/').append(id);
		GitHubRequest request = new GitHubRequest();
		request.setUri(uri);
		request.setType(Gist.class);
		return (Gist) this.client.get(request).getBody();
	}

	/**
	 * Get starred gists for currently authenticated user
	 * 
	 * @return list of gists
	 * @throws IOException
	 */
	public List<Gist> getStarredGists() throws IOException {
		PagedRequest<Gist> request = createPagedRequest();
		request.setUri(IGitHubConstants.SEGMENT_GISTS
				+ IGitHubConstants.SEGMENT_STARRED);
		request.setType(new TypeToken<List<Gist>>() {
		}.getType());
		return getAll(request);
	}

	/**
	 * Create user gist paged request
	 * 
	 * @param user
	 * @param start
	 * @param size
	 * @return request
	 */
	protected PagedRequest<Gist> createUserGistRequest(String user, int start,
			int size) {
		Assert.notNull("User cannot be null", user); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_USERS);
		uri.append('/').append(user);
		uri.append(IGitHubConstants.SEGMENT_GISTS);
		PagedRequest<Gist> request = createPagedRequest(start, size);
		request.setUri(uri).setType(new TypeToken<List<Gist>>() {
		}.getType());
		return request;
	}

	/**
	 * Get gists for specified user
	 * 
	 * @param user
	 * @return list of gists
	 * @throws IOException
	 */
	public List<Gist> getGists(String user) throws IOException {
		PagedRequest<Gist> request = createUserGistRequest(user,
				PagedRequest.PAGE_FIRST, PagedRequest.PAGE_SIZE);
		return getAll(request);
	}

	/**
	 * Create page iterator for given user's gists
	 * 
	 * @param user
	 * @return gist page iterator
	 */
	public PageIterator<Gist> pageGists(final String user) {
		return pageGists(user, PagedRequest.PAGE_SIZE);
	}

	/**
	 * Create page iterator for given user's gists
	 * 
	 * @param user
	 * @param size
	 *            size of page
	 * @return gist page iterator
	 */
	public PageIterator<Gist> pageGists(final String user, final int size) {
		return pageGists(user, PagedRequest.PAGE_FIRST, size);
	}

	/**
	 * Create page iterator for given user's gists
	 * 
	 * @param user
	 * @param size
	 *            size of page
	 * @param start
	 *            starting page
	 * @return gist page iterator
	 */
	public PageIterator<Gist> pageGists(final String user, final int start,
			final int size) {
		PagedRequest<Gist> request = createUserGistRequest(user, start, size);
		return createPageIterator(request);
	}

	/**
	 * Create a gist
	 * 
	 * @param gist
	 * @return created gist
	 * @throws IOException
	 */
	public Gist createGist(Gist gist) throws IOException {
		Assert.notNull("Gist cannot be null", gist);
		return client.post(IGitHubConstants.SEGMENT_GISTS, gist, Gist.class);
	}

	/**
	 * Update a gist
	 * 
	 * @param gist
	 * @return updated gist
	 * @throws IOException
	 */
	public Gist updateGist(Gist gist) throws IOException {
		Assert.notNull("Gist cannot be null", gist); //$NON-NLS-1$
		String id = gist.getId();
		Assert.notNull("Gist id cannot be null", id); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_GISTS);
		uri.append('/').append(id);
		return this.client.post(uri.toString(), gist, Gist.class);
	}

	/**
	 * Create comment on specified gist id
	 * 
	 * @param gistId
	 * @param comment
	 * @return created issue
	 * @throws IOException
	 */
	public Comment createComment(String gistId, String comment)
			throws IOException {
		Assert.notNull("Gist id cannot be null", gistId); //$NON-NLS-1$
		Assert.notNull("Gist comment cannot be null", comment);
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_GISTS);
		uri.append('/').append(gistId);
		uri.append(IGitHubConstants.SEGMENT_COMMENTS);

		Map<String, String> params = new HashMap<String, String>(1, 1);
		params.put(IssueService.FIELD_BODY, comment);
		return this.client.post(uri.toString(), params, Comment.class);
	}

	/**
	 * Get comments for specified gist id
	 * 
	 * @param gistId
	 * @return list of comments
	 * @throws IOException
	 */
	public List<Comment> getComments(String gistId) throws IOException {
		Assert.notNull("Gist id cannot be null", gistId); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_GISTS);
		uri.append('/').append(gistId);
		uri.append(IGitHubConstants.SEGMENT_COMMENTS);
		PagedRequest<Comment> request = createPagedRequest();
		request.setUri(uri).setType(new TypeToken<List<Comment>>() {
		}.getType());
		return getAll(request);
	}

	/**
	 * Delete the Gist with the given id
	 * 
	 * @param gistId
	 * @throws IOException
	 */
	public void deleteGist(String gistId) throws IOException {
		Assert.notNull("Gist id cannot be null", gistId); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_GISTS);
		uri.append('/').append(gistId);
		client.delete(uri.toString());
	}

	/**
	 * Delete the Gist comment with the given id
	 * 
	 * @param commentId
	 * @throws IOException
	 */
	public void deleteComment(String commentId) throws IOException {
		Assert.notNull("Gist comment id cannot be null", commentId); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_GISTS);
		uri.append(IGitHubConstants.SEGMENT_COMMENTS);
		uri.append('/').append(commentId);
		client.delete(uri.toString());
	}

	/**
	 * Star the gist with the given id
	 * 
	 * @param gistId
	 * @throws IOException
	 */
	public void starGist(String gistId) throws IOException {
		Assert.notNull("Gist id cannot be null", gistId); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_GISTS);
		uri.append('/').append(gistId);
		uri.append(IGitHubConstants.SEGMENT_STAR);
		client.put(uri.toString(), null, null);
	}

	/**
	 * Unstar the gist with the given id
	 * 
	 * @param gistId
	 * @throws IOException
	 */
	public void unstarGist(String gistId) throws IOException {
		Assert.notNull("Gist id cannot be null", gistId); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_GISTS);
		uri.append('/').append(gistId);
		uri.append(IGitHubConstants.SEGMENT_STAR);
		client.delete(uri.toString());
	}

	/**
	 * Check if a gist is starred
	 * 
	 * @param gistId
	 * @return true if starred, false if not starred
	 * @throws IOException
	 */
	public boolean isStarred(String gistId) throws IOException {
		Assert.notNull("Gist id cannot be null", gistId); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_GISTS);
		uri.append('/').append(gistId);
		uri.append(IGitHubConstants.SEGMENT_STAR);
		try {
			client.get(new GitHubRequest().setUri(uri));
			return true;
		} catch (RequestException e) {
			if (e.getStatus() == HttpStatus.SC_NOT_FOUND)
				return false;
			throw e;
		}
	}

}
