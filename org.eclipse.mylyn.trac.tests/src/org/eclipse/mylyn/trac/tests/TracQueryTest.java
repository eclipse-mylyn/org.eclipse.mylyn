/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.trac.tests;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.TracRepositoryQuery;
import org.eclipse.mylar.internal.trac.core.model.TracSearch;

/**
 * @author Steffen Pingel
 */
public class TracQueryTest extends TestCase {

	private TracRepositoryQuery createQuery(String parameter) {
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
