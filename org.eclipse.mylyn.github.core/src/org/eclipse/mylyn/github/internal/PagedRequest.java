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

import org.eclipse.core.runtime.Assert;

/**
 * Pages request class that contains a collector for accept resources page by
 * page.
 * 
 * @param <V>
 */
public class PagedRequest<V> extends GitHubRequest {

	private IResourceCollector<V> collector;

	/**
	 * Create paged request with non-null collector
	 * 
	 * @param collector
	 */
	public PagedRequest(IResourceCollector<V> collector) {
		Assert.isNotNull(collector, "Collecto cannot be null"); //$NON-NLS-1$
		this.collector = collector;
	}

	/**
	 * @return collector
	 */
	public IResourceCollector<V> getCollector() {
		return this.collector;
	}

}
