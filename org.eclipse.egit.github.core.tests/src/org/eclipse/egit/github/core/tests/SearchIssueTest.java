/******************************************************************************
 *  Copyright (c) 2012 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.egit.github.core.SearchIssue;
import org.junit.Test;

/**
 * Unit tests of {@link SearchIssue}
 */
public class SearchIssueTest {

	/**
	 * Test default state of search issue
	 */
	@Test
	public void defaultState() {
		SearchIssue issue = new SearchIssue();
		assertNull(issue.getBody());
		assertNull(issue.getCreatedAt());
		assertNull(issue.getGravatarId());
		assertNull(issue.getHtmlUrl());
		assertNull(issue.getLabels());
		assertEquals(0, issue.getComments());
		assertEquals(0, issue.getPosition());
		assertNull(issue.getState());
		assertNull(issue.getTitle());
		assertNull(issue.getUpdatedAt());
		assertNull(issue.getUser());
		assertEquals(0, issue.getVotes());
		assertEquals(0, issue.getNumber());
	}

	/**
	 * Test updating search issue fields
	 */
	@Test
	public void updateFields() {
		SearchIssue issue = new SearchIssue();
		assertEquals("body", issue.setBody("body").getBody());
		assertEquals(new Date(1234), issue.setCreatedAt(new Date(1234))
				.getCreatedAt());
		assertEquals("gravatar", issue.setGravatarId("gravatar")
				.getGravatarId());
		assertEquals("html", issue.setHtmlUrl("html").getHtmlUrl());
		List<String> labels = Arrays.asList("a", "b");
		assertEquals(labels, issue.setLabels(labels).getLabels());
		assertEquals(5, issue.setComments(5).getComments());
		assertEquals(6, issue.setPosition(6).getPosition());
		assertEquals("open", issue.setState("open").getState());
		assertEquals("title", issue.setTitle("title").getTitle());
		assertEquals(new Date(2345), issue.setUpdatedAt(new Date(2345))
				.getUpdatedAt());
		assertEquals("defunkt", issue.setUser("defunkt").getUser());
		assertEquals(10, issue.setVotes(10).getVotes());
		assertEquals(500, issue.setNumber(500).getNumber());
	}
}
