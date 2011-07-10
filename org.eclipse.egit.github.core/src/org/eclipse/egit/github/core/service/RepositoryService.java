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
import java.util.Collections;
import java.util.List;

import org.eclipse.egit.github.core.Assert;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.IResourceProvider;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
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
			IResourceProvider<SearchRepository> {

		private List<SearchRepository> repositories;

		/**
		 * @see org.eclipse.egit.github.core.IResourceProvider#getResources()
		 */
		public List<SearchRepository> getResources() {
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
	 * Get repositories for the currently authenticated user
	 * 
	 * @return list of repositories
	 * @throws IOException
	 */
	public List<Repository> getRepositories() throws IOException {
		PagedRequest<Repository> request = createPagedRequest();
		request.setUri(IGitHubConstants.SEGMENT_USER
				+ IGitHubConstants.SEGMENT_REPOS);
		request.setType(new TypeToken<List<Repository>>() {
		}.getType());
		return getAll(request);
	}

	/**
	 * Get repositories for the given user
	 * 
	 * @param user
	 * @return list of repositories
	 * @throws IOException
	 */
	public List<Repository> getRepositories(String user) throws IOException {
		Assert.notNull("User cannot be null", user);
		Assert.notEmpty("User cannot be empty", user);
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_USERS);
		uri.append('/').append(user);
		uri.append(IGitHubConstants.SEGMENT_REPOS);
		PagedRequest<Repository> request = createPagedRequest();
		request.setUri(uri);
		request.setType(new TypeToken<List<Repository>>() {
		}.getType());
		return getAll(request);
	}

	/**
	 * Get organization repositories for the given organization
	 * 
	 * @param organization
	 * @return list of repositories
	 * @throws IOException
	 */
	public List<Repository> getOrgRepositories(String organization)
			throws IOException {
		Assert.notNull("Organization cannot be null", organization);
		Assert.notEmpty("Organization cannot be empty", organization);
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_ORGS);
		uri.append('/').append(organization);
		uri.append(IGitHubConstants.SEGMENT_REPOS);
		PagedRequest<Repository> request = createPagedRequest();
		request.setUri(uri);
		request.setType(new TypeToken<List<Repository>>() {
		}.getType());
		return getAll(request);
	}

	/**
	 * Search repositories
	 * 
	 * @param query
	 * @return list of repositories
	 * @throws IOException
	 */
	public List<SearchRepository> searchRepositories(String query)
			throws IOException {
		return searchRepositories(query, null);
	}

	/**
	 * Search for repositories matching language and query
	 * 
	 * @param query
	 * @param language
	 * @return list of repositories
	 * @throws IOException
	 */
	public List<SearchRepository> searchRepositories(final String query,
			final String language) throws IOException {
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_V2_API);
		uri.append(IGitHubConstants.SEGMENT_REPOS);
		uri.append(IGitHubConstants.SEGMENT_SEARCH);
		uri.append('/').append(query);
		PagedRequest<SearchRepository> request = createPagedRequest();

		if (language != null && language.length() > 0)
			request.setParams(Collections.singletonMap(
					IGitHubConstants.PARAM_LANGUAGE, language));

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
		return client.post(IGitHubConstants.SEGMENT_USER
				+ IGitHubConstants.SEGMENT_REPOS, repository, Repository.class);
	}

	/**
	 * Create a new repository
	 * 
	 * @param organization
	 * @param repository
	 * @return created repository
	 * @throws IOException
	 */
	public Repository createRepository(String organization,
			Repository repository) throws IOException {
		Assert.notNull("Organization cannot be null", organization); //$NON-NLS-1$
		Assert.notNull("Repository cannot be null", repository); //$NON-NLS-1$
		StringBuilder uri = new StringBuilder(IGitHubConstants.SEGMENT_ORGS);
		uri.append('/').append(organization);
		uri.append(IGitHubConstants.SEGMENT_REPOS);
		return client.post(uri.toString(), repository, Repository.class);
	}

	/**
	 * Get repository
	 * 
	 * @param owner
	 * @param name
	 * @return repository
	 * @throws IOException
	 */
	public Repository getRepository(final String owner, final String name)
			throws IOException {
		return getRepository(RepositoryId.create(owner, name));
	}

	/**
	 * Get repository
	 * 
	 * @param provider
	 * @return repository
	 * @throws IOException
	 */
	public Repository getRepository(final IRepositoryIdProvider provider)
			throws IOException {
		final String id = getId(provider);
		GitHubRequest request = createRequest();
		request.setUri(IGitHubConstants.SEGMENT_REPOS + "/" + id);
		request.setType(Repository.class);
		return (Repository) client.get(request).getBody();

	}
}
