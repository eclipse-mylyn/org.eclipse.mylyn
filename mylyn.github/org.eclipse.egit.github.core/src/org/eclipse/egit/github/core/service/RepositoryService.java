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

import org.eclipse.egit.github.core.IResourceProvider;
import org.eclipse.egit.github.core.ListResourceCollector;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.eclipse.egit.github.core.client.PagedRequest;

/**
 * Repository service class.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class RepositoryService extends GitHubService {

	private static class RepositoryContainer implements
			IResourceProvider<Repository> {

		private List<Repository> repositories;

		/**
		 * @see org.eclipse.egit.github.core.IResourceProvider#getResources()
		 */
		public List<Repository> getResources() {
			return this.repositories;
		}

	}

	/**
	 * Create repository service
	 * 
	 * @param client
	 *            cannot be null
	 */
	public RepositoryService(GitHubClient client) {
		super(client);
	}

	/**
	 * Get all repositories accessible through organizational membership
	 * 
	 * @return list of repositories
	 * @throws IOException
	 */

	public List<Repository> getOrganizationRepositories() throws IOException {
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_V2_API);
		uri.append(IGitHubConstants.SEGMENT_ORGANIZATIONS).append(
				IGitHubConstants.SEGMENT_REPOSITORIES);

		ListResourceCollector<Repository> collector = new ListResourceCollector<Repository>();
		PagedRequest<Repository> request = new PagedRequest<Repository>(
				collector);
		request.setUri(uri);
		request.setType(RepositoryContainer.class);
		getAll(request);
		return collector.getResources();
	}

	/**
	 * Get repositories
	 * 
	 * @param user
	 * @return list of repositories
	 * @throws IOException
	 */
	public List<Repository> getRepositories(String user) throws IOException {
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_V2_API);
		uri.append(IGitHubConstants.SEGMENT_REPOS)
				.append(IGitHubConstants.SEGMENT_SHOW).append('/').append(user);

		ListResourceCollector<Repository> collector = new ListResourceCollector<Repository>();
		PagedRequest<Repository> request = new PagedRequest<Repository>(
				collector);
		request.setUri(uri);
		request.setType(RepositoryContainer.class);
		getAll(request);
		return collector.getResources();
	}
}
