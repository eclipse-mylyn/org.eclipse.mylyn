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

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.ui.search.messages"; //$NON-NLS-1$

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String CreateQueryFromSearchAction_CLEAR_QUERY;

	public static String CreateQueryFromSearchAction_Name_of_query_to_be_added_to_the_X;

	public static String RepositorySearchResult_Task_search_X_matches;

	public static String RepositorySearchResult_Task_search_1_match;

	public static String RepositorySearchResultView_Add_to_X_Category;

	public static String RepositorySearchResultView_Create_Query_from_Search_;

	public static String RepositorySearchResultView_Filter_Completed_Tasks;

	public static String RepositorySearchResultView_Group_By_Owner;

	public static String RepositorySearchResultView_Open_in_Editor;

	public static String RepositorySearchResultView_Open_Search_with_Browser_Label;

	public static String RepositorySearchResultView_Refine_Search_;

	public static String SearchHitCollector_Max_allowed_number_of_hits_returned_exceeded;

	public static String SearchHitCollector_Querying_Repository_;

	public static String SearchHitCollector_Repository_connector_could_not_be_found;

	public static String SearchHitCollector_Search_failed;

	public static String SearchHitCollector_Search_cancelled;

	public static String SearchHitCollector_Search_returned_maximum_number_of_hits;

	public static String SearchResultsLabelProvider_OF;

	public static String SearchResultTreeContentProvider_Complete;

	public static String SearchResultTreeContentProvider_Incomplete;

	public static String SearchResultTreeContentProvider__unknown_;

	public static String SearchResultSortAction_Sort_Label;
}
