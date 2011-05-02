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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link LinkedList} based resource collector
 * 
 * @param <V>
 */
public class ListResourceCollector<V> implements IResourceCollector<V> {

	private List<V> resources = new LinkedList<V>();

	/**
	 * Clear resources from collector
	 * 
	 * @return this collector
	 */
	public ListResourceCollector<V> clear() {
		this.resources.clear();
		return this;
	}

	/**
	 * Get resources
	 * 
	 * @return collection of resources
	 */
	public List<V> getResources() {
		return this.resources;
	}

	/**
	 * @see org.eclipse.mylyn.github.internal.IResourceCollector#accept(int,
	 *      java.util.Collection)
	 */
	public boolean accept(int page, Collection<V> resources) {
		return this.resources.addAll(resources);
	}

}
