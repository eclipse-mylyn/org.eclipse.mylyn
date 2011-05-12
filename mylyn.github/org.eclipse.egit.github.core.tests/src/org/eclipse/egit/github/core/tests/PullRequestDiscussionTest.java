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

import static org.junit.Assert.assertTrue;

import org.eclipse.egit.github.core.PullRequestDiscussion;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Tests for {@link PullRequestDiscussion}
 */
public class PullRequestDiscussionTest {

	private static final Gson gson = new GsonBuilder().setDateFormat(
			"yyyy-MM-dd").create();

	@Test
	public void getCreatedAt_ReferenceMutableObject() {
		PullRequestDiscussion pullRequestDiscussion = gson.fromJson(
				"{createdAt : '2003-10-10'}", PullRequestDiscussion.class);
		pullRequestDiscussion.getCreatedAt().setTime(0);
		assertTrue(pullRequestDiscussion.getCreatedAt().getTime() != 0);
	}

	@Test
	public void getUpdatedAt_ReferenceMutableObject() {
		PullRequestDiscussion pullRequestDiscussion = gson.fromJson(
				"{updatedAt : '2003-10-10'}", PullRequestDiscussion.class);
		pullRequestDiscussion.getUpdatedAt().setTime(0);
		assertTrue(pullRequestDiscussion.getUpdatedAt().getTime() != 0);
	}

	@Test
	public void getCommitedDate_ReferenceMutableObject() {
		PullRequestDiscussion pullRequestDiscussion = gson.fromJson(
				"{commitedDate : '2003-10-10'}", PullRequestDiscussion.class);
		pullRequestDiscussion.getCommitedDate().setTime(0);
		assertTrue(pullRequestDiscussion.getCommitedDate().getTime() != 0);
	}

	@Test
	public void getAuthoredDate_ReferenceMutableObject() {
		PullRequestDiscussion pullRequestDiscussion = gson.fromJson(
				"{authoredDate : '2003-10-10'}", PullRequestDiscussion.class);
		pullRequestDiscussion.getAuthoredDate().setTime(0);
		assertTrue(pullRequestDiscussion.getAuthoredDate().getTime() != 0);
	}

}
