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

package org.eclipse.mylyn.trac.tests;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracSearchFilter;
import org.eclipse.mylyn.internal.trac.core.util.TracUtil;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.trac.tests.support.TracTestConstants;

/**
 * @author Steffen Pingel
 */
public class TracUtilTest extends TestCase {

	private TaskRepository taskRepository;

	@Override
	protected void setUp() throws Exception {
		taskRepository = new TaskRepository(TracCorePlugin.CONNECTOR_KIND, TracTestConstants.TEST_TRAC_010_URL);
	}

	public void testToTracSearch() {
		String queryParameter = "&order=priority&status=new&status=assigned&status=reopened&milestone=M1&owner=%7E%C3%A4%C3%B6%C3%BC";
		IRepositoryQuery query = TasksUi.getRepositoryModel().createRepositoryQuery(taskRepository);
		query.setUrl(taskRepository.getRepositoryUrl() + ITracClient.QUERY_URL + queryParameter);

		TracSearch search = TracUtil.toTracSearch(query);
		assertNotNull(search);
		assertEquals(queryParameter, search.toUrl());
	}

	public void testToTracSearchFilterList() {
		String parameterUrl = "&status=new&status=assigned&status=reopened&milestone=0.1";
		String queryUrl = taskRepository.getRepositoryUrl() + ITracClient.QUERY_URL + parameterUrl;
		IRepositoryQuery query = TasksUi.getRepositoryModel().createRepositoryQuery(taskRepository);
		query.setUrl(queryUrl);

		TracSearch filterList = TracUtil.toTracSearch(query);
		assertEquals(parameterUrl, filterList.toUrl());
		assertEquals("&status=new|assigned|reopened&milestone=0.1", filterList.toQuery());

		List<TracSearchFilter> list = filterList.getFilters();
		TracSearchFilter filter = list.get(0);
		assertEquals("status", filter.getFieldName());
		assertEquals(Arrays.asList("new", "assigned", "reopened"), filter.getValues());
		filter = list.get(1);
		assertEquals("milestone", filter.getFieldName());
		assertEquals(Arrays.asList("0.1"), filter.getValues());
	}

	public void testEncodeUrl() {
		assertEquals("encode", TracUtil.encodeUrl("encode"));
		assertEquals("sp%20ace%20", TracUtil.encodeUrl("sp ace "));
		assertEquals("%2B%2B", TracUtil.encodeUrl("++"));
		assertEquals("%2520", TracUtil.encodeUrl("%20"));
		assertEquals("%2Fslash", TracUtil.encodeUrl("/slash"));
	}

}
