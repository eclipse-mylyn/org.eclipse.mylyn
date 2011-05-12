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

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.IssueService;

/**
 * 
 */
public class IssueTest extends LiveTest {

	/**
	 * Test fetching an issue
	 * 
	 * @throws IOException
	 */
	public void testFetch() throws IOException {
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
	public void testFetchAll() throws IOException {
		IssueService service = new IssueService(client);
		List<Issue> issues = service.getIssues("schacon", "showoff",
				Collections.singletonMap(IssueService.FILTER_STATE,
						IssueService.STATE_OPEN));
		assertFalse(issues.isEmpty());
		for (Issue issue : issues)
			assertNotNull(issue);
	}

}
