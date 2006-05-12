/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.bugzilla.ui.search;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.search.internal.ui.SearchPluginImages;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;

/**
 * The collection of all the bugzilla matches.
 * 
 * @see org.eclipse.search.ui.text.AbstractTextSearchResult
 */
public class BugzillaSearchResult extends AbstractTextSearchResult {

	/**
	 * The query producing this result.
	 */
	private BugzillaSearchQuery bugQuery;

	/**
	 * Constructor for <code>BugzillaSearchResult</code> class.
	 * 
	 * @param query
	 *            <code>BugzillaSearchQuery</code> that is producing this
	 *            result.
	 */
	public BugzillaSearchResult(BugzillaSearchQuery query) {
		super();
		bugQuery = query;
	}

	@Override
	public IEditorMatchAdapter getEditorMatchAdapter() {
		IBugzillaResultEditorMatchAdapter adapter = BugzillaUiPlugin.getResultEditorMatchAdapter();
		if (adapter == null) {
			return null;
		} else {
			adapter.setResult(this);
			return adapter;
		}
	}

	/**
	 * This function always returns <code>null</code>, as the matches for
	 * this implementation of <code>AbstractTextSearchResult</code> never
	 * contain files.
	 * 
	 * @see org.eclipse.search.ui.text.AbstractTextSearchResult#getFileMatchAdapter()
	 */
	@Override
	public IFileMatchAdapter getFileMatchAdapter() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.search.ui.ISearchResult#getLabel()
	 */
	public String getLabel() {
		return getMatchCount() == 1 ? getSingularLabel() : getPluralLabel();
	}

	/**
	 * Get the singular label for the number of results
	 * 
	 * @return The singular label
	 */
	protected String getSingularLabel() {
		return "Bugzilla search - 1 match";
	}

	/**
	 * Get the plural label for the number of results
	 * 
	 * @return The plural label
	 */
	protected String getPluralLabel() {
		return "Bugzilla search - " + getMatchCount() + " matches";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.search.ui.ISearchResult#getTooltip()
	 */
	public String getTooltip() {
		return getLabel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.search.ui.ISearchResult#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return SearchPluginImages.DESC_OBJ_TSEARCH_DPDN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.search.ui.ISearchResult#getQuery()
	 */
	public ISearchQuery getQuery() {
		return bugQuery;
	}

}
