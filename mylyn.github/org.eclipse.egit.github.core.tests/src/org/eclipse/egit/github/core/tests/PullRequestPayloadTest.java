/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Jason Tsay (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.event.PullRequestPayload;
import org.junit.Test;

/**
 * Unit tests of {@link PullRequestPayload}
 */
public class PullRequestPayloadTest {

	/**
	 * Test default state of PullRequestPayload
	 */
	@Test
	public void defaultState() {
		PullRequestPayload payload = new PullRequestPayload();
		assertNull(payload.getAction());
		assertEquals(0, payload.getNumber());
		assertNull(payload.getPullRequest());
	}

	/**
	 * Test updating PullRequestPayload fields
	 */
	@Test
	public void updateFields() {
		PullRequestPayload payload = new PullRequestPayload();
		PullRequest pullRequest = new PullRequest().setTitle("pull");
		assertEquals("create", payload.setAction("create").getAction());
		assertEquals(9000, payload.setNumber(9000).getNumber());
		assertEquals(pullRequest, payload.setPullRequest(pullRequest).getPullRequest());
	}
}
