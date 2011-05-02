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

/**
 * Interface for accepting collections of resources page by page.
 * 
 * @param <V>
 */
public interface IResourceCollector<V> {

	/**
	 * Accept page response.
	 * 
	 * @param page
	 * @param response
	 * @return true to continue collecting, false to abort
	 */
	boolean accept(int page, Collection<V> response);

}
