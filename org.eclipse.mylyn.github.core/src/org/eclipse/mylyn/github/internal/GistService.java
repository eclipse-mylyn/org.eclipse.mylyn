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
		Assert.isNotNull(id, "Gist id cannot be null");
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_GISTS);
		uri.append('/').append(id).append(IGitHubConstants.SUFFIX_JSON);
		GitHubRequest request = new GitHubRequest();
		request.setUri(uri);
		request.setType(Gist.class);
		return (Gist) this.client.get(request).getBody();
	}

	/**
	 * Get gists for specified user
	 * 
	 * @param user
	 * @return list of gists
	 * @throws IOException
	 */
	public List<Gist> getGists(String user) throws IOException {
		Assert.isNotNull(user, "User cannot be null");
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_USERS);
		uri.append('/').append(user);
		uri.append(IGitHubConstants.SEGMENT_GISTS).append(
				IGitHubConstants.SUFFIX_JSON);
		ListResourceCollector<Gist> collector = new ListResourceCollector<Gist>();
		PagedRequest<Gist> request = new PagedRequest<Gist>(collector);
		request.setUri(uri).setType(new TypeToken<List<Gist>>() {
		}.getType());
		getAll(request);
		return collector.getResources();
	}

	/**
	 * Create a gist
	 * 
	 * @param gist
	 * @return created gist
	 * @throws IOException
	 */
	public Gist createGist(Gist gist) throws IOException {
		Assert.isNotNull(gist, "Gist cannot be null");
		StringBuilder uri = new StringBuilder();
		User user = gist.getUser();
		if (user != null) {
			String login = user.getLogin();
			Assert.isNotNull(login, "User login name cannot be null");
			uri.append(IGitHubConstants.SEGMENT_USERS);
			uri.append('/').append(login);
		}
		uri.append(IGitHubConstants.SEGMENT_GISTS).append(
				IGitHubConstants.SUFFIX_JSON);
		return this.client.post(uri.toString(), gist, Gist.class);
	}

	/**
	 * Update a gist
	 * 
	 * @param gist
	 * @return updated gist
	 * @throws IOException
	 */
	public Gist updateGist(Gist gist) throws IOException {
		Assert.isNotNull(gist, "Gist cannot be null"); //$NON-NLS-1$
		String id = gist.getId();
		Assert.isNotNull(id, "Gist id cannot be null"); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_GISTS);
		uri.append('/').append(id).append(IGitHubConstants.SUFFIX_JSON);
		return this.client.put(uri.toString(), gist, Gist.class);
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
		Assert.isNotNull(gistId, "Gist id cannot be null");
		Assert.isNotNull(comment, "Gist comment cannot be null");
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_GISTS);
		uri.append('/').append(gistId);
		uri.append(IGitHubConstants.SEGMENT_COMMENTS).append(
				IGitHubConstants.SUFFIX_JSON);

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
		Assert.isNotNull(gistId, "Gist id cannot be null");
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_GISTS);
		uri.append('/').append(gistId);
		uri.append(IGitHubConstants.SEGMENT_COMMENTS).append(
				IGitHubConstants.SUFFIX_JSON);
		ListResourceCollector<Comment> collector = new ListResourceCollector<Comment>();
		PagedRequest<Comment> request = new PagedRequest<Comment>(collector);
		request.setUri(uri).setType(new TypeToken<List<Comment>>() {
		}.getType());
		getAll(request);
		return collector.getResources();
	}

}
