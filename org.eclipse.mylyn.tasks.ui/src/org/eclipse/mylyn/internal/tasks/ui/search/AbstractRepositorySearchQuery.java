/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import org.eclipse.mylyn.tasks.ui.search.RepositorySearchResult;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;

/**
 * @author Rob Elves
 */
public abstract class AbstractRepositorySearchQuery implements ISearchQuery {

	/** The collection of all the matches. */
	private RepositorySearchResult searchResult;

	public String getLabel() {
		return "Querying Repository...";
	}

	public boolean canRerun() {
		return true;
	}

	public boolean canRunInBackground() {
		return true;
	}

	public ISearchResult getSearchResult() {
		if (searchResult == null) {
			searchResult = new RepositorySearchResult(this);
		}
		return searchResult;
	}

}
