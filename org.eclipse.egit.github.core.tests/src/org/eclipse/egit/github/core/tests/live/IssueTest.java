/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests.live;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.IssueService;
import org.junit.Test;

/**
 * 
 */
public class IssueTest extends LiveTest {

	/**
	 * Test fetching an issue
	 * 
	 * @throws IOException
	 */
	@Test
	public void fetchIssue() throws IOException {
		IssueService service = new IssueService(client);
		Issue issue = service.getIssue("schacon", "showoff", "1");
		assertNotNull(issue);
		assertNotNull(issue.getUpdatedAt());
		assertNotNull(issue.getCreatedAt());
		assertTrue(issue.getNumber() > 0);
		assertNotNull(issue.getBody());
		assertNotNull(issue.getTitle());
		assertNotNull(issue.getHtmlUrl());
		assertTrue(issue.getNumber() >= 0);
		assertNotNull(issue.getUser());
	}

	/**
	 * Test fetching multiple issues
	 * 
	 * @throws IOException
	 */
	@Test
	public void fetchAllIssues() throws IOException {
		IssueService service = new IssueService(client);
		List<Issue> issues = service.getIssues("schacon", "showoff",
				Collections.singletonMap(IssueService.FILTER_STATE,
						IssueService.STATE_OPEN));
		assertFalse(issues.isEmpty());
		for (Issue issue : issues)
			assertNotNull(issue);
	}

	/**
	 * Test paging of requests
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPaging() throws Exception {
		IssueService service = new IssueService(client);
		Map<String, String> params = new HashMap<String, String>();
		params.put(IssueService.FILTER_STATE, IssueService.STATE_CLOSED);
		PageIterator<Issue> iterator = service.pageIssues("schacon", "showoff",
				params, 1);
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		Collection<Issue> page = iterator.next();
		int pages = iterator.getLastPage();
		int read = 1;
		assertNotNull(page);
		assertEquals(1, page.size());
		assertNotNull(iterator.getRequest());
		assertNotNull(iterator.getNextUri());
		assertTrue(iterator.getNextPage() > 1);
		assertNotNull(iterator.getLastUri());
		assertTrue(iterator.getLastPage() > iterator.getNextPage());
		assertTrue(iterator.hasNext());
		Collection<Issue> page2 = iterator.next();
		read++;
		assertNotNull(page2);
		assertFalse(page.equals(page2));
		while (iterator.hasNext()) {
			iterator.next();
			read++;
		}
		assertEquals(pages, read);
	}

}
