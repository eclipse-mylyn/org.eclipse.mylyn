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
import java.util.Collection;
import java.util.Collections;

import org.eclipse.egit.github.core.Assert;
import org.eclipse.egit.github.core.IResourceCollector;
import org.eclipse.egit.github.core.IResourceProvider;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.client.PagedRequest;

/**
 * Base GitHub service class.
 */
public abstract class GitHubService {

	/**
	 * Client field
	 */
	protected final GitHubClient client;

	/**
	 * Create service for client
	 * 
	 * @param client
	 */
	public GitHubService(GitHubClient client) {
		Assert.notNull("Client cannot be null", client); //$NON-NLS-1$
		this.client = client;
	}

	/**
	 * Unified request creation method that all sub-classes should use so
	 * overriding classes can extend and configure the default request.
	 * 
	 * @return request
	 */
	protected GitHubRequest createRequest() {
		return new GitHubRequest();
	}

	/**
	 * Unified paged request creation method that all sub-classes should use so
	 * overriding classes can extend and configure the default request.
	 * 
	 * @param collector
	 * @return request
	 */
	protected <V> PagedRequest<V> createPagedRequest(IResourceCollector<V> collector) {
		return new PagedRequest<V>(collector);
	}

	/**
	 * Get paged request by performing multiple requests until no more pages are
	 * available or the request collector no longer accepts resource results.
	 * 
	 * @param <V>
	 * @param request
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected <V> void getAll(PagedRequest<V> request) throws IOException {
		IResourceCollector<V> collector = request.getCollector();
		String next = null;
		int page = 1;
		do {
			GitHubResponse response = client.get(request);
			Collection<V> resources = null;
			Object body = response.getBody();
			if (body instanceof Collection)
				resources = (Collection<V>) body;
			else if (body instanceof IResourceProvider)
				resources = ((IResourceProvider<V>) body).getResources();
			else
				resources = (Collection<V>) Collections.singletonList(body);

			if (!collector.accept(page, resources))
				break;
			next = response.getNext();
			if (next != null)
				request.setUri(next);
			page++;
		} while (next != null);
	}
}
