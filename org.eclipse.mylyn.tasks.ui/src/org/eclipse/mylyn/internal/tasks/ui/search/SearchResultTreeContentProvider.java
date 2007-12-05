/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.search.RepositorySearchResult;

/**
 * This implementation of <code>SearchResultContentProvider</code> is used for the table view of a Bugzilla search
 * result.
 * 
 * @author Rob Elves (moved into task.ui)
 */
public class SearchResultTreeContentProvider extends SearchResultContentProvider {

	/** The page the Bugzilla search results are displayed in */
	private RepositorySearchResultView searchResultsPage;

	private List<Object> elements = new ArrayList<Object>();

	/**
	 * Constructor
	 * 
	 * @param page
	 *            The page the Bugzilla search results are displayed in
	 */
	public SearchResultTreeContentProvider(RepositorySearchResultView page) {
		searchResultsPage = page;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof RepositorySearchResult) {
			searchResult = (RepositorySearchResult) newInput;
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof RepositorySearchResult) {
			return elements.toArray();
		} else {
			return EMPTY_ARR;
		}
	}

	public Object[] getChildren(Object parentElement) {
		return EMPTY_ARR;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return !(element instanceof AbstractTask);
	}

	@Override
	public void elementsChanged(Object[] updatedElements) {
		for (Object object : updatedElements) {
			elements.add(object);
		}

		searchResultsPage.getViewer().refresh();
////		boolean tableLimited = SearchPreferencePage.isTableLimited();
//		for (int i = 0; i < updatedElements.length; i++) {
//			if (searchResult.getMatchCount(updatedElements[i]) > 0) {
//				if (viewer.testFindItem(updatedElements[i]) != null)
//					viewer.update(updatedElements[i], null);
//				else {
////					if (!tableLimited || viewer.getTable().getItemCount() < SearchPreferencePage.getTableLimit())
//					viewer.add(updatedElements[i]);
//				}
//			} else
//				viewer.remove(updatedElements[i]);
//		}
	}

	@Override
	public void clear() {
		elements.clear();
		searchResultsPage.getViewer().refresh();
	}
}
