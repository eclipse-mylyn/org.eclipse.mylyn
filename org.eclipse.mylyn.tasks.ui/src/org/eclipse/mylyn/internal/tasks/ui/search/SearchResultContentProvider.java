/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Rob Elves (moved into task.ui)
 * @see org.eclipse.jface.viewers.IContentProvider
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
