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

import org.eclipse.egit.github.core.Assert;
import org.eclipse.egit.github.core.IResourceCollector;

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
	 *            must be non-null
	 */
	public PagedRequest(IResourceCollector<V> collector) {
		Assert.notNull("Collector cannot be null", collector); //$NON-NLS-1$
		this.collector = collector;
	}

	/**
	 * @return collector
	 */
	public IResourceCollector<V> getCollector() {
		return this.collector;
	}

}
