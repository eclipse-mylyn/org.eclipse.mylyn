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
package org.eclipse.egit.github.core.client;

import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Paged request class that contains the initial page size and page number of
 * the request.
 *
 * @param <V>
 */
public class PagedRequest<V> extends GitHubRequest {

	/**
	 * First page
	 */
	public static final int PAGE_FIRST = 1;

	/**
	 * Default page size
	 */
	public static final int PAGE_SIZE = 100;

	private final int pageSize;

	private final int page;

	/**
	 * Create paged request with default size
	 */
	public PagedRequest() {
		this(PAGE_FIRST, PAGE_SIZE);
	}

	/**
	 * Create paged request with given starting page and page size.
	 *
	 * @param start
	 * @param size
	 */
	public PagedRequest(int start, int size) {
		page = start;
		pageSize = size;
	}

	/**
	 * @return pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	@Override
	protected List<NameValuePair> getPairs(Map<String, String> data) {
		List<NameValuePair> pairs = super.getPairs(data);
		int size = getPageSize();
		if (size > 0)
			pairs.add(new BasicNameValuePair(IGitHubConstants.PARAM_PER_PAGE,
					Integer.toString(size)));
		int number = getPage();
		if (number > 0)
			pairs.add(new BasicNameValuePair(IGitHubConstants.PARAM_PAGE,
					Integer.toString(number)));
		return pairs;
	}

	/**
	 * @return page
	 */
	public int getPage() {
		return page;
	}
}
