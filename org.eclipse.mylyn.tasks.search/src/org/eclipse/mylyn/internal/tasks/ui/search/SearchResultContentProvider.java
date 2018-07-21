/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Rob Elves
 */
public abstract class SearchResultContentProvider implements ITreeContentProvider {

	/** An empty array of objects */
	protected final Object[] EMPTY_ARR = new Object[0];

	/** The search result for this content provider */
	protected RepositorySearchResult searchResult;

	public void dispose() {
		// nothing to do
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof RepositorySearchResult) {
			initialize((RepositorySearchResult) newInput);
		} else {
			searchResult = null;
		}
	}

	/**
	 * Initializes the content provider with the given search result.
	 * 
	 * @param result
	 *            The search result to use with this content provider
	 */
	protected void initialize(RepositorySearchResult result) {
		searchResult = result;
	}

	/**
	 * This method is called whenever the set of matches for the given elements changes.
	 * 
	 * @param updatedElements
	 *            The array of objects that has to be refreshed
	 * @see
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#elementsChanged(java.lang.Object[])
	 */
	public abstract void elementsChanged(Object[] updatedElements);

	/**
	 * Clears the viewer.
	 */
	public abstract void clear();
}
