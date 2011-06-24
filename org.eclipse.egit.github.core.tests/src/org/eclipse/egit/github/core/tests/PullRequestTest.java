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

import org.eclipse.egit.github.core.PullRequest;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Tests for {@link PullRequest}
 */
public class PullRequestTest {

	private static final Gson gson = new GsonBuilder().setDateFormat(
			"yyyy-MM-dd").create();

	@Test
	public void getCreatedAt_ReferenceMutableObject() {
		PullRequest pullRequest = gson.fromJson("{createdAt : '2003-10-10'}",
				PullRequest.class);
		pullRequest.getCreatedAt().setTime(0);
		assertTrue(pullRequest.getCreatedAt().getTime() != 0);
	}

	@Test
	public void getMergedAt_ReferenceMutableObject() {
		PullRequest pullRequest = gson.fromJson("{mergedAt : '2003-10-10'}",
				PullRequest.class);
		pullRequest.getMergedAt().setTime(0);
		assertTrue(pullRequest.getMergedAt().getTime() != 0);
	}

	@Test
	public void getUpdatedAt_ReferenceMutableObject() {
		PullRequest pullRequest = gson.fromJson("{updatedAt : '2003-10-10'}",
				PullRequest.class);
		pullRequest.getUpdatedAt().setTime(0);
		assertTrue(pullRequest.getUpdatedAt().getTime() != 0);
	}

	@Test
	public void getClosedAt_ReferenceMutableObject() {
		PullRequest pullRequest = gson.fromJson("{closedAt : '2003-10-10'}",
				PullRequest.class);
		pullRequest.getClosedAt().setTime(0);
		assertTrue(pullRequest.getClosedAt().getTime() != 0);
	}

}
