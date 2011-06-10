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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.Assert;
import org.eclipse.egit.github.core.IResourceProvider;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.eclipse.egit.github.core.client.PagedRequest;

/**
 * Repository service class.
 */
public class RepositoryService extends GitHubService {

	/**
	 * FIELD_NAME
	 */
	public static final String FIELD_NAME = "name"; //$NON-NLS-1$

	/**
	 * FIELD_DESCRIPTION
	 */
	public static final String FIELD_DESCRIPTION = "description"; //$NON-NLS-1$

	/**
	 * FIELD_HOMEPAGE
	 */
	public static final String FIELD_HOMEPAGE = "homepage"; //$NON-NLS-1$

	/**
	 * FIELD_PUBLIC
	 */
	public static final String FIELD_PUBLIC = "public"; //$NON-NLS-1$

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

	private static class RepositoryWrapper {

		Repository repository;

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
		PagedRequest<Repository> request = createPagedRequest();
		request.setUri(uri);
		request.setType(RepositoryContainer.class);
		return getAll(request);
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

		PagedRequest<Repository> request = createPagedRequest();
		request.setUri(uri);
		request.setType(RepositoryContainer.class);
		return getAll(request);
	}

	/**
	 * Create a new repository
	 * 
	 * @param repository
	 * @return created repository
	 * @throws IOException
	 */
	public Repository createRepository(Repository repository)
			throws IOException {
		Assert.notNull("Repository cannot be null", repository); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_V2_API);
		uri.append(IGitHubConstants.SEGMENT_REPOS).append(
				IGitHubConstants.SEGMENT_CREATE);

		// Name is required, all others are optional
		Map<String, String> params = new HashMap<String, String>();
		params.put(FIELD_NAME, repository.getName());
		String desc = repository.getDescription();
		if (desc != null)
			params.put(FIELD_DESCRIPTION, desc);
		String homepage = repository.getHomepage();
		if (homepage != null)
			params.put(FIELD_HOMEPAGE, homepage);
		params.put(FIELD_PUBLIC, repository.isPrivate() ? Integer.toString(0)
				: Integer.toString(1));

		RepositoryWrapper wrapper = client.post(uri.toString(), params,
				RepositoryWrapper.class);
		return wrapper.repository;
	}
}
