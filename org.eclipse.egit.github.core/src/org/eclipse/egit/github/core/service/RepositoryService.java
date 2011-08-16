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

import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_FORKS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_ORGS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_REPOS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_SEARCH;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_USER;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_USERS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_V2_API;
import static org.eclipse.egit.github.core.client.PagedRequest.PAGE_FIRST;
import static org.eclipse.egit.github.core.client.PagedRequest.PAGE_SIZE;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.IResourceProvider;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.client.PagedRequest;

/**
 * Repository service class.
 *
 * @see <a href="http://developer.github.com/v3/repos">GitHub repository API
 *      documentation</a>
 * @see <a href="http://developer.github.com/v3/repos/forks">GitHub forks API
 *      documentation</a>
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
			return repositories;
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
		request.setUri(SEGMENT_USER + SEGMENT_REPOS);
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
		if (user == null)
			throw new IllegalArgumentException("User cannot be null"); //$NON-NLS-1$
		if (user.length() == 0)
			throw new IllegalArgumentException("User cannot be empty"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_USERS);
		uri.append('/').append(user);
		uri.append(SEGMENT_REPOS);
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
		if (organization == null)
			throw new IllegalArgumentException("Organization cannot be null"); //$NON-NLS-1$
		if (organization.length() == 0)
			throw new IllegalArgumentException("Organization cannot be empty"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_ORGS);
		uri.append('/').append(organization);
		uri.append(SEGMENT_REPOS);
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
		StringBuilder uri = new StringBuilder(SEGMENT_V2_API);
		uri.append(SEGMENT_REPOS);
		uri.append(SEGMENT_SEARCH);
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
		if (repository == null)
			throw new IllegalArgumentException("Repository cannot be null"); //$NON-NLS-1$

		return client.post(SEGMENT_USER + SEGMENT_REPOS, repository,
				Repository.class);
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
		if (organization == null)
			throw new IllegalArgumentException("Organization cannot be null"); //$NON-NLS-1$
		if (repository == null)
			throw new IllegalArgumentException("Repository cannot be null"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_ORGS);
		uri.append('/').append(organization);
		uri.append(SEGMENT_REPOS);
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
		request.setUri(SEGMENT_REPOS + '/' + id);
		request.setType(Repository.class);
		return (Repository) client.get(request).getBody();
	}

	/**
	 * Create paged request for iterating over repositories forks
	 *
	 * @param repository
	 * @param start
	 * @param size
	 * @return paged request
	 */
	protected PagedRequest<Repository> createPagedForkRequest(
			IRepositoryIdProvider repository, int start, int size) {
		final String id = getId(repository);
		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_FORKS);
		PagedRequest<Repository> request = createPagedRequest(start, size);
		request.setUri(uri);
		request.setType(new TypeToken<List<Repository>>() {
		}.getType());
		return request;
	}

	/**
	 * Get all the forks of the given repository
	 *
	 * @param repository
	 * @return non-null but possibly empty list of repository
	 * @throws IOException
	 */
	public List<Repository> getForks(IRepositoryIdProvider repository)
			throws IOException {
		PagedRequest<Repository> request = createPagedForkRequest(repository,
				PAGE_FIRST, PAGE_SIZE);
		return getAll(request);
	}

	/**
	 * Page forks of given repository
	 *
	 * @param repository
	 * @return iterator over repositories
	 */
	public PageIterator<Repository> pageForks(IRepositoryIdProvider repository) {
		return pageForks(repository, PAGE_SIZE);
	}

	/**
	 * Page forks of given repository
	 *
	 * @param repository
	 * @param size
	 * @return iterator over repositories
	 */
	public PageIterator<Repository> pageForks(IRepositoryIdProvider repository,
			int size) {
		return pageForks(repository, PAGE_FIRST, size);
	}

	/**
	 * Page forks of given repository
	 *
	 * @param repository
	 * @param start
	 * @param size
	 * @return iterator over repositories
	 */
	public PageIterator<Repository> pageForks(IRepositoryIdProvider repository,
			int start, int size) {
		PagedRequest<Repository> request = createPagedForkRequest(repository,
				start, size);
		return createPageIterator(request);
	}

	/**
	 * Fork given repository into new repository under the currently
	 * authenticated user.
	 *
	 * @param repository
	 * @return forked repository
	 * @throws IOException
	 */
	public Repository forkRepository(IRepositoryIdProvider repository)
			throws IOException {
		return forkRepository(repository, null);
	}

	/**
	 * Fork given repository into new repository.
	 *
	 * The new repository will be under the given organization if non-null, else
	 * it will be under the currently authenticated user.
	 *
	 * @param repository
	 * @param organization
	 * @return forked repository
	 * @throws IOException
	 */
	public Repository forkRepository(IRepositoryIdProvider repository,
			String organization) throws IOException {
		final String id = getId(repository);
		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_FORKS);
		Map<String, String> params = null;
		if (organization != null)
			params = Collections.singletonMap("org", organization); //$NON-NLS-1$
		return client.post(uri.toString(), params, Repository.class);
	}
}
