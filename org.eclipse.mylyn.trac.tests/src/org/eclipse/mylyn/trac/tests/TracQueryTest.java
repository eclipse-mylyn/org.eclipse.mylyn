/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryQuery;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;

/**
 * @author Steffen Pingel
 */
public class TracQueryTest extends TestCase {

	private TracRepositoryQuery createQuery(String parameter) {
		// FIXME: remove this external depencency
		String url = "http://oss.steffenpingel.de/mylar-trac-connector";
		return new TracRepositoryQuery(url, url + ITracClient.QUERY_URL + parameter, "description");
	}

	public void testGetTracSearch() {
		String queryParameter = "&order=priority&status=new&status=assigned&status=reopened&milestone=M1&owner=%7E%C3%A4%C3%B6%C3%BC";
		TracRepositoryQuery query = createQuery(queryParameter);
		TracSearch search = query.getTracSearch();
		assertEquals(queryParameter, search.toUrl());
	}

}
