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

package org.eclipse.mylar.bugzilla.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylar.bugzilla.ui.search.BugzillaSearchResultView;

/**
 * This class sorts Bugzilla search results by a supplied category.
 */
public class BugzillaSortAction extends Action {

	/** The category that this class sorts Bugzilla search results by. */
	private int bugSortOrder;

	/** The view where the Bugzilla search results are displayed. */
	private BugzillaSearchResultView bugPage;

	/**
	 * Constructor
	 * 
	 * @param label
	 *            The string used as the text for the action, or null if there
	 *            is no text
	 * @param page
	 *            The view where the Bugzilla search results are displayed.
	 * @param sortOrder
	 *            The category that this class sorts Bugzilla search results by
	 */
	public BugzillaSortAction(String label, BugzillaSearchResultView page, int sortOrder) {
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
