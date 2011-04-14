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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

import com.google.gson.reflect.TypeToken;

/**
 * Service class for getting and list gists.
 */
public class GistService {

	private GitHubClient client;

	/**
	 * Create gist service
	 * 
	 * @param client
	 */
	public GistService(GitHubClient client) {
		Assert.isNotNull(client, "Client cannot be null");
		this.client = client;
	}

	/**
	 * Get gist
	 * 
	 * @param id
	 * @return gist
	 * @throws IOException
	 */
	public Gist getGist(String id) throws IOException {
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_GISTS);
		uri.append('/').append(id).append(IGitHubConstants.SUFFIX_JSON);
		return this.client.get(uri.toString(), Gist.class);
	}

	/**
	 * Get gists for specified user
	 * 
	 * @param user
	 * @return list of gists
	 * @throws IOException
	 */
	public List<Gist> getGists(String user) throws IOException {
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_USERS);
		uri.append('/').append(user);
		uri.append(IGitHubConstants.SEGMENT_GISTS).append(
				IGitHubConstants.SUFFIX_JSON);
		TypeToken<List<Gist>> gistToken = new TypeToken<List<Gist>>() {
		};
		return this.client.get(uri.toString(), gistToken.getType());
	}

	/**
	 * Create a gist
	 * 
	 * @param gist
	 * @return created gist
	 * @throws IOException
	 */
	public Gist createGist(Gist gist) throws IOException {
		StringBuilder uri = new StringBuilder();
		User user = gist.getUser();
		if (user != null) {
			uri.append(IGitHubConstants.SEGMENT_USERS);
			uri.append('/').append(user.getLogin());
		}
		uri.append(IGitHubConstants.SEGMENT_GISTS).append(
				IGitHubConstants.SUFFIX_JSON);
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
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_GISTS);
		uri.append('/').append(gistId);
		uri.append(IGitHubConstants.SEGMENT_COMMENTS).append(
				IGitHubConstants.SUFFIX_JSON);

		TypeToken<List<Comment>> commentToken = new TypeToken<List<Comment>>() {
		};
		return this.client.get(uri.toString(), commentToken.getType());
	}

}
