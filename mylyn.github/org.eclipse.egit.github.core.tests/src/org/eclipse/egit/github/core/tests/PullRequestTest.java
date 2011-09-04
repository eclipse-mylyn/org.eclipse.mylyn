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

import java.util.Date;

import org.eclipse.egit.github.core.PullRequest;
import org.junit.Test;

/**
 * Unit tests of {@link PullRequest}
 */
public class PullRequestTest {

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
