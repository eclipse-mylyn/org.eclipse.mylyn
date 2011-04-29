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
import java.util.List;

import org.eclipse.core.runtime.Assert;

/**
 * @author Kevin Sawicki (kevin@github.com)
 */
public class PullRequestService {

	/**
	 * Pull request wrapper
	 */
	private static class PullRequestWrapper {

		private PullRequest pull;

		public PullRequest getPull() {
			return this.pull;
		}

	}

	/**
	 * Pull requests wrapper
	 */
	private static class PullRequestsWrapper {

		private List<PullRequest> pulls;

		public List<PullRequest> getPulls() {
			return this.pulls;
		}

	}

	private GitHubClient client;

	/**
	 * Create pull request service
	 * 
	 * @param client
	 *            cannot be null
	 */
	public PullRequestService(GitHubClient client) {
		Assert.isNotNull(client, "Client cannot be null"); //$NON-NLS-1$
		this.client = client;
	}

	/**
	 * Get pull request from repository with id
	 * 
	 * @param repository
	 * @param id
	 * @return pull request
	 * @throws IOException
	 */
	public PullRequest getPullRequest(Repository repository, String id)
			throws IOException {
		Assert.isNotNull(repository, "Repository cannot be null"); //$NON-NLS-1$
		Assert.isNotNull(id, "Id cannot be null"); //$NON-NLS-1$
		String repositoryId = repository.getId();
		Assert.isNotNull(repositoryId, "Repository id cannot be null"); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_V2_API);
		uri.append(IGitHubConstants.SEGMENT_PULLS);
		uri.append('/').append(repositoryId);
		uri.append('/').append(id);
		PullRequestWrapper wrapper = this.client.get(uri.toString(),
				PullRequestWrapper.class);
		return wrapper.getPull();
	}

	/**
	 * Get pull requests from repository matching state
	 * 
	 * @param repository
	 * @param state
	 * @return list of pull requests
	 * @throws IOException
	 */
	public List<PullRequest> getPullRequests(Repository repository, String state)
			throws IOException {
		Assert.isNotNull(repository, "Repository cannot be null"); //$NON-NLS-1$
		Assert.isNotNull(state, "State cannot be null"); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_V2_API);
		uri.append(IGitHubConstants.SEGMENT_PULLS);
		uri.append('/').append(repository.getId());
		uri.append('/').append(state);
		PullRequestsWrapper wrapper = this.client.get(uri.toString(),
				PullRequestsWrapper.class);
		return wrapper.getPulls();
	}
}
