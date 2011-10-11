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

import static org.eclipse.egit.github.core.client.IGitHubConstants.PARAM_LANGUAGE;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_BRANCHES;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_FORKS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_LANGUAGES;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_ORGS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_REPOS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_SEARCH;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_TAGS;
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
import org.eclipse.egit.github.core.RepositoryBranch;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.RepositoryTag;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
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

	/**
	 * Type filter key
	 */
	public static final String FILTER_TYPE = "type"; //$NON-NLS-1$

	/**
	 * Public repository filter type
	 */
	public static final String TYPE_PUBLIC = "public"; //$NON-NLS-1$

	/**
	 * Private repository filter type
	 */
	public static final String TYPE_PRIVATE = "private"; //$NON-NLS-1$

	/**
	 * Member repository filter type
	 */
	public static final String TYPE_MEMBER = "member"; //$NON-NLS-1$

	/**
	 * All repositories filter type
	 */
	public static final String TYPE_ALL = "all"; //$NON-NLS-1$

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
	 */
	public RepositoryService() {
		super();
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
		return getRepositories((Map<String, String>) null);
	}

	/**
	 * Get repositories for the currently authenticated user
	 *
	 * @param filterData
	 * @return list of repositories
	 * @throws IOException
	 */
	public List<Repository> getRepositories(Map<String, String> filterData)
			throws IOException {
		return getAll(pageRepositories(filterData));
	}

	/**
	 * Page repositories for currently authenticated user
	 *
	 * @return iterator over pages of repositories
	 * @throws IOException
	 */
	public PageIterator<Repository> pageRepositories() throws IOException {
		return pageRepositories(PAGE_SIZE);
	}

	/**
	 * Page repositories for currently authenticated user
	 *
	 * @param size
	 * @return iterator over pages of repositories
	 * @throws IOException
	 */
	public PageIterator<Repository> pageRepositories(int size)
			throws IOException {
		return pageRepositories(PAGE_FIRST, size);
	}

	/**
	 * Page repositories for currently authenticated user
	 *
	 * @param start
	 * @param size
	 * @return iterator over pages of repositories
	 * @throws IOException
	 */
	public PageIterator<Repository> pageRepositories(int start, int size)
			throws IOException {
		return pageRepositories((Map<String, String>) null, start, size);
	}

	/**
	 * Page repositories for currently authenticated user
	 *
	 * @param filterData
	 * @return iterator over pages of repositories
	 * @throws IOException
	 */
	public PageIterator<Repository> pageRepositories(
			Map<String, String> filterData) throws IOException {
		return pageRepositories(filterData, PAGE_SIZE);
	}

	/**
	 * Page repositories for currently authenticated user
	 *
	 * @param filterData
	 * @param size
	 * @return iterator over pages of repositories
	 * @throws IOException
	 */
	public PageIterator<Repository> pageRepositories(
			Map<String, String> filterData, int size) throws IOException {
		return pageRepositories(filterData, PAGE_FIRST, size);
	}

	/**
	 * Page repositories for currently authenticated user
	 *
	 * @param filterData
	 * @param start
	 * @param size
	 * @return iterator over pages of repositories
	 * @throws IOException
	 */
	public PageIterator<Repository> pageRepositories(
			Map<String, String> filterData, int start, int size)
			throws IOException {
		PagedRequest<Repository> request = createPagedRequest(start, size);
		request.setUri(SEGMENT_USER + SEGMENT_REPOS);
		request.setParams(filterData);
		request.setType(new TypeToken<List<Repository>>() {
		}.getType());
		return createPageIterator(request);
	}

	/**
	 * Get repositories for the given user
	 *
	 * @param user
	 * @return list of repositories
	 * @throws IOException
	 */
	public List<Repository> getRepositories(String user) throws IOException {
		return getAll(pageRepositories(user));
	}

	/**
	 * Page repositories for given user
	 *
	 * @param user
	 * @return iterator over pages of repositories
	 * @throws IOException
	 */
	public PageIterator<Repository> pageRepositories(String user)
			throws IOException {
		return pageRepositories(user, PAGE_SIZE);
	}

	/**
	 * Page repositories for given user
	 *
	 * @param user
	 * @param size
	 * @return iterator over pages of repositories
	 * @throws IOException
	 */
	public PageIterator<Repository> pageRepositories(String user, int size)
			throws IOException {
		return pageRepositories(user, PAGE_FIRST, size);
	}

	/**
	 * Page repositories for given user
	 *
	 * @param user
	 * @param start
	 * @param size
	 * @return iterator over repository page
	 * @throws IOException
	 */
	public PageIterator<Repository> pageRepositories(String user, int start,
			int size) throws IOException {
		if (user == null)
			throw new IllegalArgumentException("User cannot be null"); //$NON-NLS-1$
		if (user.length() == 0)
			throw new IllegalArgumentException("User cannot be empty"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_USERS);
		uri.append('/').append(user);
		uri.append(SEGMENT_REPOS);
		PagedRequest<Repository> request = createPagedRequest(start, size);
		request.setUri(uri);
		request.setType(new TypeToken<List<Repository>>() {
		}.getType());
		return createPageIterator(request);
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
		return getOrgRepositories(organization, null);
	}

	/**
	 * Page repositories for the given organization
	 *
	 * @param organization
	 * @return iterator over pages of repositories
	 */
	public PageIterator<Repository> pageOrgRepositories(String organization) {
		return pageOrgRepositories(organization, PAGE_SIZE);
	}

	/**
	 * Page repositories for the given organization
	 *
	 * @param organization
	 * @param size
	 * @return iterator over pages of repositories
	 */
	public PageIterator<Repository> pageOrgRepositories(String organization,
			int size) {
		return pageOrgRepositories(organization, PAGE_FIRST, size);
	}

	/**
	 * Page repositories for the given organization
	 *
	 * @param organization
	 * @param start
	 * @param size
	 * @return iterator over pages of repositories
	 */
	public PageIterator<Repository> pageOrgRepositories(String organization,
			int start, int size) {
		return pageOrgRepositories(organization, null, start, size);
	}

	/**
	 * Get organization repositories for the given organization
	 *
	 * @param organization
	 * @param filterData
	 * @return list of repositories
	 * @throws IOException
	 */
	public List<Repository> getOrgRepositories(String organization,
			Map<String, String> filterData) throws IOException {
		return getAll(pageOrgRepositories(organization, filterData));
	}

	/**
	 * Page repositories for the given organization
	 *
	 * @param organization
	 * @param filterData
	 * @return iterator over pages of repositories
	 */
	public PageIterator<Repository> pageOrgRepositories(String organization,
			Map<String, String> filterData) {
		return pageOrgRepositories(organization, filterData, PAGE_SIZE);
	}

	/**
	 * Page repositories for the given organization
	 *
	 * @param organization
	 * @param filterData
	 * @param size
	 * @return iterator over pages of repositories
	 */
	public PageIterator<Repository> pageOrgRepositories(String organization,
			Map<String, String> filterData, int size) {
		return pageOrgRepositories(organization, filterData, PAGE_FIRST, size);
	}

	/**
	 * Page repositories for the given organization
	 *
	 * @param organization
	 * @param filterData
	 * @param start
	 * @param size
	 * @return iterator over pages of repositories
	 */
	public PageIterator<Repository> pageOrgRepositories(String organization,
			Map<String, String> filterData, int start, int size) {
		if (organization == null)
			throw new IllegalArgumentException("Organization cannot be null"); //$NON-NLS-1$
		if (organization.length() == 0)
			throw new IllegalArgumentException("Organization cannot be empty"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_ORGS);
		uri.append('/').append(organization);
		uri.append(SEGMENT_REPOS);
		PagedRequest<Repository> request = createPagedRequest(start, size);
		request.setParams(filterData);
		request.setUri(uri);
		request.setType(new TypeToken<List<Repository>>() {
		}.getType());
		return createPageIterator(request);
	}

	/**
	 * Search for repositories matching query.
	 *
	 * This method requires an API v2 configured {@link GitHubClient} as it is
	 * not yet supported in API v3 clients.
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
	 * Search for repositories matching language and query.
	 *
	 * This method requires an API v2 configured {@link GitHubClient} as it is
	 * not yet supported in API v3 clients.
	 *
	 * @param query
	 * @param language
	 * @return list of repositories
	 * @throws IOException
	 */
	public List<SearchRepository> searchRepositories(final String query,
			final String language) throws IOException {
		if (query == null)
			throw new IllegalArgumentException("Query cannot be null"); //$NON-NLS-1$
		if (query.length() == 0)
			throw new IllegalArgumentException("Query cannot be empty"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_V2_API);
		uri.append(SEGMENT_REPOS);
		uri.append(SEGMENT_SEARCH);
		uri.append('/').append(query);
		PagedRequest<SearchRepository> request = createPagedRequest();

		if (language != null && language.length() > 0)
			request.setParams(Collections
					.singletonMap(PARAM_LANGUAGE, language));

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
		if (organization.length() == 0)
			throw new IllegalArgumentException("Organization cannot be empty"); //$NON-NLS-1$
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
		return getAll(pageForks(repository));
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
	 * Edit given repository
	 *
	 * @param repository
	 * @return edited repository
	 * @throws IOException
	 */
	public Repository editRepository(Repository repository) throws IOException {
		if (repository == null)
			throw new IllegalArgumentException("Repository cannot be null"); //$NON-NLS-1$

		final String repoId = getId(repository);
		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(repoId);
		return client.post(uri.toString(), repository, Repository.class);
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

	/**
	 * Get languages used in given repository
	 *
	 * @param repository
	 * @return map of language names mapped to line counts
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked" })
	public Map<String, Long> getLanguages(IRepositoryIdProvider repository)
			throws IOException {
		String id = getId(repository);
		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_LANGUAGES);
		GitHubRequest request = createRequest();
		request.setUri(uri);
		request.setType(new TypeToken<Map<String, Long>>() {
		}.getType());
		return (Map<String, Long>) client.get(request).getBody();
	}

	/**
	 * Get branches in given repository
	 *
	 * @param repository
	 * @return list of branches
	 * @throws IOException
	 */
	public List<RepositoryBranch> getBranches(IRepositoryIdProvider repository)
			throws IOException {
		String id = getId(repository);
		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_BRANCHES);
		PagedRequest<RepositoryBranch> request = createPagedRequest();
		request.setUri(uri);
		request.setType(new TypeToken<List<RepositoryBranch>>() {
		}.getType());
		return getAll(request);
	}

	/**
	 * Get tags in given repository
	 *
	 * @param repository
	 * @return list of tags
	 * @throws IOException
	 */
	public List<RepositoryTag> getTags(IRepositoryIdProvider repository)
			throws IOException {
		String id = getId(repository);
		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_TAGS);
		PagedRequest<RepositoryTag> request = createPagedRequest();
		request.setUri(uri);
		request.setType(new TypeToken<List<RepositoryTag>>() {
		}.getType());
		return getAll(request);
	}
}
