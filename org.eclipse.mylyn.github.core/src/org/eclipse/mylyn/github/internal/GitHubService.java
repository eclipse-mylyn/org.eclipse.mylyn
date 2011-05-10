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
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.Assert;

/**
 * Base GitHub service class.
 */
public abstract class GitHubService {

	/**
	 * Client field
	 */
	protected GitHubClient client;

	/**
	 * Create service for client
	 * 
	 * @param client
	 */
	public GitHubService(GitHubClient client) {
		Assert.isNotNull(client, "Client cannot be null"); //$NON-NLS-1$
		this.client = client;
	}

	/**
	 * Get paged request by performing multiple requests until no more pages are
	 * available of the request collector no longer accepts resource results.
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
