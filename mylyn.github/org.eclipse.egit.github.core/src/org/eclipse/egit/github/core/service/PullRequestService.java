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

import java.io.IOException;
import java.util.List;

import org.eclipse.egit.github.core.Assert;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.client.IGitHubConstants;

/**
 * Service class getting and listing pull requests.
 */
public class PullRequestService extends GitHubService {

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

	/**
	 * @param client
	 */
	public PullRequestService(GitHubClient client) {
		super(client);
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
		Assert.notNull("Repository cannot be null", repository); //$NON-NLS-1$
		Assert.notNull("Id cannot be null", id); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_V2_API);
		uri.append(IGitHubConstants.SEGMENT_PULLS);
		uri.append('/').append(repository.getId());
		uri.append('/').append(id);
		GitHubRequest request = new GitHubRequest();
		request.setUri(uri);
		request.setType(PullRequestWrapper.class);
		GitHubResponse response = this.client.get(request);
		return ((PullRequestWrapper) response.getBody()).getPull();
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
		Assert.notNull("Repository cannot be null", repository); //$NON-NLS-1$
		Assert.notNull("State cannot be null", state); //$NON-NLS-1$
		String repositoryId = repository.getId();
		Assert.notNull("Repository id cannot be null", repositoryId); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_V2_API);
		uri.append(IGitHubConstants.SEGMENT_PULLS);
		uri.append('/').append(repositoryId);
		uri.append('/').append(state);
		GitHubRequest request = new GitHubRequest();
		request.setUri(uri);
		request.setType(PullRequestsWrapper.class);
		GitHubResponse response = this.client.get(request);
		return ((PullRequestsWrapper) response.getBody()).getPulls();
	}
}
