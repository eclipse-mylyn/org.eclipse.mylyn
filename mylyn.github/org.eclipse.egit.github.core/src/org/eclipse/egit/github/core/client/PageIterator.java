/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.client;

import static org.eclipse.egit.github.core.client.IGitHubConstants.PARAM_PAGE;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.eclipse.egit.github.core.IResourceProvider;

/**
 * Iterator for getting paged responses
 *
 * @param <V>
 */
public class PageIterator<V> implements Iterator<Collection<V>>,
		Iterable<Collection<V>> {

	/**
	 * Request
	 */
	protected final PagedRequest<V> request;

	/**
	 * Client
	 */
	protected final GitHubClient client;

	/**
	 * Current page number
	 */
	protected int nextPage;

	/**
	 * Last page number
	 */
	protected int lastPage;

	/**
	 * Next uri to be fetched
	 */
	protected String next;

	/**
	 * Last uri to be fetched
	 */
	protected String last;

	/**
	 * Create page iterator
	 *
	 * @param request
	 * @param client
	 */
	public PageIterator(PagedRequest<V> request, GitHubClient client) {
		this.request = request;
		this.client = client;
		next = request.getUri();
		nextPage = parsePageNumber(next);
	}

	/**
	 * Parse page number from uri
	 *
	 * @param uri
	 * @return page number
	 */
	protected int parsePageNumber(String uri) {
		if (uri == null || uri.length() == 0)
			return -1;
		try {
			for (NameValuePair pair : URLEncodedUtils.parse(new URI(uri), null))
				if (PARAM_PAGE.equals(pair.getName()))
					return Integer.parseInt(pair.getValue());
		} catch (URISyntaxException e) {
			return -1;
		} catch (NumberFormatException nfe) {
			return -1;
		}
		return -1;
	}

	/**
	 * Get number of next page to be read
	 *
	 * @return next page
	 */
	public int getNextPage() {
		return nextPage;
	}

	/**
	 * Get number of last page
	 *
	 * @return page number
	 */
	public int getLastPage() {
		return lastPage;
	}

	/**
	 * Get URI of next request
	 *
	 * @return next page uri
	 */
	public String getNextUri() {
		return next;
	}

	/**
	 * Get uri of last page
	 *
	 * @return last page uri
	 */
	public String getLastUri() {
		return last;
	}

	public boolean hasNext() {
		return nextPage == 0 || next != null;
	}

	public void remove() {
		throw new UnsupportedOperationException("Remove not supported"); //$NON-NLS-1$
	}

	@SuppressWarnings("unchecked")
	public Collection<V> next() {
		if (!hasNext())
			throw new NoSuchElementException();
		Collection<V> resources = null;
		try {
			if (next != null)
				request.setUri(next);
			GitHubResponse response = client.get(request);
			Object body = response.getBody();
			if (body instanceof Collection)
				resources = (Collection<V>) body;
			else if (body instanceof IResourceProvider)
				resources = ((IResourceProvider<V>) body).getResources();
			else
				resources = (Collection<V>) Collections.singletonList(body);
			nextPage++;
			next = response.getNext();
			nextPage = parsePageNumber(next);
			last = response.getLast();
			lastPage = parsePageNumber(last);
		} catch (IOException e) {
			throw new NoSuchPageException(e);
		}
		return resources;
	}

	/**
	 * Get request being executed
	 *
	 * @return request
	 */
	public PagedRequest<V> getRequest() {
		return request;
	}

	/**
	 * @return this page iterator
	 */
	public Iterator<Collection<V>> iterator() {
		return this;
	}
}
