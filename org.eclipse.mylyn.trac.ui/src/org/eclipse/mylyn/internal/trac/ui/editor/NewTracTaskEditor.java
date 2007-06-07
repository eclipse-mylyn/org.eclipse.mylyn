/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.ui.editor;

import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.TracRepositoryQuery;
import org.eclipse.mylar.internal.trac.core.model.TracSearch;
import org.eclipse.mylar.internal.trac.core.model.TracSearchFilter;
import org.eclipse.mylar.internal.trac.core.model.TracSearchFilter.CompareOperator;
import org.eclipse.mylar.tasks.ui.TaskFactory;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.tasks.ui.editors.AbstractNewRepositoryTaskEditor;
import org.eclipse.mylar.tasks.ui.search.SearchHitCollector;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * @author Steffen Pingel
 */
public class NewTracTaskEditor extends AbstractNewRepositoryTaskEditor {

	public NewTracTaskEditor(FormEditor editor) {
		super(editor);
	}

	@Override
	public SearchHitCollector getDuplicateSearchCollector(String searchString) {
		TracSearchFilter filter = new TracSearchFilter("description");
		filter.setOperator(CompareOperator.CONTAINS);
		filter.addValue(searchString);

		TracSearch search = new TracSearch();
		search.addFilter(filter);

		// TODO copied from TracCustomQueryPage.getQueryUrl()
		StringBuilder sb = new StringBuilder();
		sb.append(repository.getUrl());
		sb.append(ITracClient.QUERY_URL);
		sb.append(search.toUrl());

		TracRepositoryQuery query = new TracRepositoryQuery(repository.getUrl(), sb.toString(), "<Duplicate Search>");

		SearchHitCollector collector = new SearchHitCollector(TasksUiPlugin.getTaskListManager().getTaskList(),
				repository, query, new TaskFactory(repository));
		return collector;
	}

}
