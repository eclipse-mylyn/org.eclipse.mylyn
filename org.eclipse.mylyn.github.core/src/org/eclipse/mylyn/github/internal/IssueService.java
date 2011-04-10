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
import java.util.List;

import org.eclipse.core.runtime.Assert;

/**
 * Issue service class for listing, searching, and fetching {@link Issue}
 * objects using a {@link GitHubClient}.
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class IssueService {

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

}
