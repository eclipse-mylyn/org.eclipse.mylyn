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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.internal.ui.SearchPluginImages;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;

/**
 * Captures the results of a task repository search.
 * 
 * @author Rob Elves
 * @see org.eclipse.search.ui.text.AbstractTextSearchResult
 * @since 2.0
 */
public class RepositorySearchResult extends AbstractTextSearchResult {

	/**
	 * The query producing this result.
	 */
	private final ISearchQuery repositoryQuery;

	/**
	 * Constructor for <code>RepositorySearchResult</code> class.
	 * 
	 * @param query
	 *            <code>AbstractRepositorySearchQuery</code> that is producing this result.
	 */
	public RepositorySearchResult(ISearchQuery query) {
		super();
		repositoryQuery = query;
	}

	@Override
	public IEditorMatchAdapter getEditorMatchAdapter() {
		return null;
	}

	/**
	 * This function always returns <code>null</code>, as the matches for this implementation of
	 * <code>AbstractTextSearchResult</code> never contain files.
	 * 
	 * @see org.eclipse.search.ui.text.AbstractTextSearchResult#getFileMatchAdapter()
	 */
	@Override
	public IFileMatchAdapter getFileMatchAdapter() {
		return null;
	}

	public String getLabel() {
		return getMatchCount() == 1 ? getSingularLabel() : getPluralLabel();
	}

	/**
	 * Get the singular label for the number of results
	 * 
	 * @return The singular label
	 */
	protected String getSingularLabel() {
		return "Task search - 1 match";
	}

	/**
	 * Get the plural label for the number of results
	 * 
	 * @return The plural label
	 */
	protected String getPluralLabel() {
		return "Task search - " + getMatchCount() + " matches";
	}

	public String getTooltip() {
		return getLabel();
	}

	public ImageDescriptor getImageDescriptor() {
		return SearchPluginImages.DESC_OBJ_TSEARCH_DPDN;
	}

	public ISearchQuery getQuery() {
		return repositoryQuery;
	}

}
