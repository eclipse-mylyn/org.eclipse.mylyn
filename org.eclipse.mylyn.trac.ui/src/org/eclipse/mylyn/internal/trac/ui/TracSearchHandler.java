/*******************************************************************************
* Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui;

import org.eclipse.mylyn.internal.tasks.core.AbstractSearchHandler;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracSearchFilter;
import org.eclipse.mylyn.internal.trac.core.model.TracSearchFilter.CompareOperator;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;

@SuppressWarnings("restriction")
public class TracSearchHandler extends AbstractSearchHandler {

	@Override
	public String getConnectorKind() {
		return TracCorePlugin.CONNECTOR_KIND;
	}

	@Override
	public boolean queryForText(TaskRepository taskRepository, IRepositoryQuery query, TaskData taskData,
			String searchString) {
		TracSearchFilter filter = new TracSearchFilter("description");
		filter.setOperator(CompareOperator.CONTAINS);
		filter.addValue(searchString);

		TracSearch search = new TracSearch();
		search.addFilter(filter);

		// TODO copied from TracQueryPage.getQueryUrl()
		StringBuilder sb = new StringBuilder();
		sb.append(taskRepository.getRepositoryUrl());
		sb.append(ITracClient.QUERY_URL);
		sb.append(search.toUrl());

		query.setUrl(sb.toString());

		return true;
	}

}
