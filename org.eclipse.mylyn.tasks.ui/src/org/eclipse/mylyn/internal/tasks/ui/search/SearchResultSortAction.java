/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import org.eclipse.jface.action.Action;

/**
 * This class sorts search results by a supplied category.
 * 
 * @author Rob Elves (moved to tasks.ui)
 */
public class SearchResultSortAction extends Action {

	/** The category that this class sorts Bugzilla search results by. */
	private final int bugSortOrder;

	/** The view where the Bugzilla search results are displayed. */
	private final RepositorySearchResultView bugPage;

	/**
	 * Constructor
	 * 
	 * @param label
	 *            The string used as the text for the action, or null if there is no text
	 * @param page
	 *            The view where the Bugzilla search results are displayed.
	 * @param sortOrder
	 *            The category that this class sorts Bugzilla search results by
	 */
	public SearchResultSortAction(String label, RepositorySearchResultView page, int sortOrder) {
		super(label);
		bugPage = page;
		bugSortOrder = sortOrder;
	}

	/**
	 * Reorder the Bugzilla search results.
	 */
	@Override
	public void run() {
		bugPage.setSortOrder(bugSortOrder);
	}

	/**
	 * Returns the category that this class sorts Bugzilla search results by.
	 */
	public int getSortOrder() {
		return bugSortOrder;
	}
}
