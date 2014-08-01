/*******************************************************************************
 *  Copyright (c) 2011 Christian Trutz
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Christian Trutz - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.PullRequestMarker;
import org.eclipse.egit.github.core.User;
import org.junit.Test;

/**
 * Unit tests of {@link PullRequest}
 */
public class PullRequestTest {

	/**
	 * Test default state of pull request
	 */
	@Test
	public void defaultState() {
		PullRequest request = new PullRequest();
		assertFalse(request.isMergeable());
		assertFalse(request.isMerged());
		assertEquals(0, request.getAdditions());
		assertNull(request.getBase());
		assertNull(request.getBody());
		assertNull(request.getBodyHtml());
		assertNull(request.getBodyText());
		assertEquals(0, request.getChangedFiles());
		assertNull(request.getClosedAt());
		assertEquals(0, request.getComments());
		assertEquals(0, request.getReviewComments());
		assertEquals(0, request.getCommits());
		assertNull(request.getCreatedAt());
		assertEquals(0, request.getDeletions());
		assertNull(request.getDiffUrl());
		assertNull(request.getHead());
		assertNull(request.getHtmlUrl());
		assertNull(request.getIssueUrl());
		assertNull(request.getMergedAt());
		assertNull(request.getMergedBy());
		assertEquals(0, request.getNumber());
		assertNull(request.getPatchUrl());
		assertNull(request.getState());
		assertNull(request.getTitle());
		assertNull(request.getUpdatedAt());
		assertNull(request.getUrl());
		assertNull(request.getUser());
		assertNotNull(request.toString());
		assertEquals(0, request.getId());
		assertNull(request.getMilestone());
		assertNull(request.getAssignee());
	}

	/**
	 * Test updating pull request fields
	 */
	@Test
	public void updateFields() {
		PullRequest request = new PullRequest();
		assertTrue(request.setMerged(true).isMerged());
		assertTrue(request.setMergeable(true).isMergeable());
		assertEquals(15, request.setAdditions(15).getAdditions());
		PullRequestMarker base = new PullRequestMarker();
		assertEquals(base, request.setBase(base).getBase());
		assertEquals("a pr", request.setBody("a pr").getBody());
		assertEquals("<body>", request.setBodyHtml("<body>").getBodyHtml());
		assertEquals("text", request.setBodyText("text").getBodyText());
		assertEquals(20, request.setChangedFiles(20).getChangedFiles());
		assertEquals(new Date(1000), request.setClosedAt(new Date(1000))
				.getClosedAt());
		assertEquals(30, request.setComments(30).getComments());
		assertEquals(35, request.setReviewComments(35).getReviewComments());
		assertEquals(40, request.setCommits(40).getCommits());
		assertEquals(new Date(2000), request.setCreatedAt(new Date(2000))
				.getCreatedAt());
		assertEquals(50, request.setDeletions(50).getDeletions());
		assertEquals("/diff/url", request.setDiffUrl("/diff/url").getDiffUrl());
		PullRequestMarker head = new PullRequestMarker();
		assertEquals(head, request.setHead(head).getHead());
		assertEquals("/html/url", request.setHtmlUrl("/html/url").getHtmlUrl());
		assertEquals("/issue/url", request.setIssueUrl("/issue/url")
				.getIssueUrl());
		assertEquals(new Date(3000), request.setMergedAt(new Date(3000))
				.getMergedAt());
		User merge = new User().setLogin("merge user");
		assertEquals(merge, request.setMergedBy(merge).getMergedBy());
		assertEquals(60, request.setNumber(60).getNumber());
		assertEquals("/patch/url", request.setPatchUrl("/patch/url")
				.getPatchUrl());
		assertEquals("open", request.setState("open").getState());
		assertEquals("pull title", request.setTitle("pull title").getTitle());
		assertEquals(new Date(4000), request.setUpdatedAt(new Date(4000))
				.getUpdatedAt());
		assertEquals("/url", request.setUrl("/url").getUrl());
		User user = new User().setLogin("cuser");
		assertEquals(user, request.setUser(user).getUser());
		assertEquals(70, request.setId(70).getId());

		User assignee = new User().setLogin("assignee");
		assertEquals(assignee, request.setAssignee(assignee).getAssignee());
		Milestone milestone = new Milestone().setNumber(456);
		assertEquals(milestone, request.setMilestone(milestone).getMilestone());
	}

	/**
	 * Test non-mutable created at date
	 */
	@Test
	public void getCreatedAtReferenceMutableObject() {
		PullRequest pullRequest = new PullRequest();
		pullRequest.setCreatedAt(new Date(10000));
		pullRequest.getCreatedAt().setTime(0);
		assertTrue(pullRequest.getCreatedAt().getTime() != 0);
	}

	/**
	 * Test non-mutable merged at date
	 */
	@Test
	public void getMergedAtReferenceMutableObject() {
		PullRequest pullRequest = new PullRequest();
		pullRequest.setMergedAt(new Date(20000));
		pullRequest.getMergedAt().setTime(0);
		assertTrue(pullRequest.getMergedAt().getTime() != 0);
	}

	/**
	 * Test non-mutable updated at date
	 */
	@Test
	public void getUpdatedAtReferenceMutableObject() {
		PullRequest pullRequest = new PullRequest();
		pullRequest.setUpdatedAt(new Date(30000));
		pullRequest.getUpdatedAt().setTime(0);
		assertTrue(pullRequest.getUpdatedAt().getTime() != 0);
	}

	/**
	 * Test non-mutable closed at date
	 */
	@Test
	public void getClosedAtReferenceMutableObject() {
		PullRequest pullRequest = new PullRequest();
		pullRequest.setClosedAt(new Date(40000));
		pullRequest.getClosedAt().setTime(0);
		assertTrue(pullRequest.getClosedAt().getTime() != 0);
	}
}
