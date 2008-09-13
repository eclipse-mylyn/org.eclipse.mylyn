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

package org.eclipse.mylyn.trac.tests.client;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.trac.core.model.TracSearch;

/**
 * @author Steffen Pingel
 */
public class TracSearchTest extends TestCase {

	private static final String QUERY1 = "&status=new|assigned|reopened&milestone~=0.1";

	private static final String URL1 = "&status=new&status=assigned&status=reopened&milestone=%7E0.1";

	private TracSearch search1;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		search1 = new TracSearch();
		search1.addFilter("status", "new");
		search1.addFilter("status", "assigned");
		search1.addFilter("status", "reopened");
		search1.addFilter("milestone", "~0.1");
	}

	public void testToQuery() {
		assertEquals(QUERY1, search1.toQuery());
	}

	public void testToQueryEmpty() {
		assertEquals("", new TracSearch().toQuery());
	}

	public void testToQueryOperators1() {
		TracSearch search = new TracSearch();
		search.addFilter("is", "a");
		search.addFilter("contains", "~b");
		search.addFilter("starts", "^c");
		search.addFilter("ends", "$d");
		search.addFilter("nis", "!e");
		search.addFilter("ncontains", "!~f");
		search.addFilter("nstarts", "!^g");
		search.addFilter("nends", "!$h");

		assertEquals("&is=a&contains~=b&starts^=c&ends$=d&nis!=e&ncontains!~=f&nstarts!^=g&nends!$=h", search.toQuery());
	}

	public void testToQueryOperators2() {
		TracSearch search = new TracSearch();
		search.addFilter("nstarts", "!^g");
		search.addFilter("nis", "!e");
		search.addFilter("is", "a");

		assertEquals("&nstarts!^=g&nis!=e&is=a", search.toQuery());
	}

	public void testToQuerySortOrder() {
		search1.setOrderBy("id");
		assertEquals("&order=id" + QUERY1, search1.toQuery());

		search1.setAscending(false);
		assertEquals("&order=id&desc=1" + QUERY1, search1.toQuery());

		search1.setOrderBy("summary");
		search1.setAscending(true);
		assertEquals("&order=summary" + QUERY1, search1.toQuery());
	}

	public void testToUrl() {
		assertEquals(URL1, search1.toUrl());
	}

	public void testToUrlEmpty() {
		// assertEquals("", new TracSearch().toUrl());
		// returns non-empty string to work around a strange Trac behaviour, see
		// TracSearch.toUrl()
		assertEquals("&order=id", new TracSearch().toUrl());
	}

	public void testToUrlEncoding() {
		search1.addFilter("encoded", "&");
		assertEquals(URL1 + "&encoded=%26", search1.toUrl());
	}

	public void testToUrlOperators1() {
		TracSearch search = new TracSearch();
		search.addFilter("is", "a");
		search.addFilter("contains", "~b");
		search.addFilter("starts", "^c");
		search.addFilter("ends", "$d");
		search.addFilter("nis", "!e");
		search.addFilter("ncontains", "!~f");
		search.addFilter("nstarts", "!^g");
		search.addFilter("nends", "!$h");

		assertEquals(
				"&is=a&contains=%7Eb&starts=%5Ec&ends=%24d&nis=%21e&ncontains=%21%7Ef&nstarts=%21%5Eg&nends=%21%24h",
				search.toUrl());
	}

	public void testToUrlOperators2() {
		TracSearch search = new TracSearch();
		search.addFilter("nstarts", "!^g");
		search.addFilter("nis", "!e");
		search.addFilter("is", "a");

		assertEquals("&nstarts=%21%5Eg&nis=%21e&is=a", search.toUrl());
	}

	public void testToUrlSortOrder() {
		search1.setOrderBy("id");
		assertEquals("&order=id" + QUERY1, search1.toQuery());

		search1.setAscending(false);
		assertEquals("&order=id&desc=1" + QUERY1, search1.toQuery());

		search1.setOrderBy("summary");
		search1.setAscending(true);
		assertEquals("&order=summary" + QUERY1, search1.toQuery());
	}

}
