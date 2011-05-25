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

import org.eclipse.egit.github.core.IResourceCollector;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.eclipse.egit.github.core.client.PagedRequest;
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
	 * Test limiting result size
	 * 
	 * @throws IOException
	 */
	@Test
	public void limit() throws IOException {
		IssueService service = new IssueService(client) {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			protected <V> PagedRequest<V> createPagedRequest(
					final IResourceCollector<V> collector) {
				return super.createPagedRequest(new IResourceCollector() {

					public boolean accept(int page, Collection response) {
						collector.accept(page, response);
						return false;
					}
				});
			}

		};
		Map<String, String> params = new HashMap<String, String>();
		params.put(IssueService.FILTER_STATE, IssueService.STATE_CLOSED);
		List<Issue> issues = service.getIssues("schacon", "showoff", params);
		assertNotNull(issues);
		assertTrue(issues.size() > 2);
		params.put(IGitHubConstants.PARAM_PER_PAGE, "1");
		issues = service.getIssues("schacon", "showoff", params);
		assertNotNull(issues);
		assertEquals(1, issues.size());
		params.put(IGitHubConstants.PARAM_PER_PAGE, "2");
		issues = service.getIssues("schacon", "showoff", params);
		assertNotNull(issues);
		assertEquals(2, issues.size());
	}

}
