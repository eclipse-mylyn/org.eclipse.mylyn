/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui.editor;

import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractLegacyDuplicateDetector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryQuery;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracSearchFilter;
import org.eclipse.mylyn.internal.trac.core.model.TracSearchFilter.CompareOperator;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public class TracDuplicateDetector extends AbstractLegacyDuplicateDetector {

	@Override
	public RepositoryQuery getDuplicatesQuery(TaskRepository repository, RepositoryTaskData taskData) {
		TracSearchFilter filter = new TracSearchFilter("description");
		filter.setOperator(CompareOperator.CONTAINS);

		String searchString = AbstractLegacyDuplicateDetector.getStackTraceFromDescription(taskData.getDescription());

		filter.addValue(searchString);

		TracSearch search = new TracSearch();
		search.addFilter(filter);

		// TODO copied from TracCustomQueryPage.getQueryUrl()
		StringBuilder sb = new StringBuilder();
		sb.append(repository.getRepositoryUrl());
		sb.append(ITracClient.QUERY_URL);
		sb.append(search.toUrl());

		TracRepositoryQuery query = new TracRepositoryQuery(repository.getRepositoryUrl(), sb.toString(),
				"<Duplicate Search>");
		return query;
	}

}
