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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.event.PushPayload;
import org.junit.Test;

/**
 * Unit tests of {@link PushPayload}
 */
public class PushPayloadTest {

	/**
	 * Test default state of PushPayload
	 */
	@Test
	public void defaultState() {
		PushPayload payload = new PushPayload();
		assertNull(payload.getHead());
		assertNull(payload.getRef());
		assertEquals(0, payload.getSize());
		assertEquals(0, payload.getDistinctSize());
		assertNull(payload.getCommits());
		assertNull(payload.getBefore());
		assertNull(payload.getAfter());
		assertFalse(payload.isCreated());
		assertFalse(payload.isDeleted());
		assertFalse(payload.isForced());
		assertNull(payload.getBaseRef());
		assertNull(payload.getCompare());
		assertNull(payload.getHeadCommit());
		assertNull(payload.getRepository());
		assertNull(payload.getPusher());
		assertNull(payload.getOrganization());
		assertNull(payload.getSender());
	}

	/**
	 * Test updating PushPayload fields
	 */
	@Test
	public void updateFields() {
		PushPayload payload = new PushPayload();
		List<Commit> commits = new ArrayList<>();
		commits.add(new Commit().setSha("000"));
		assertEquals("head", payload.setHead("head").getHead());
		assertEquals("ref", payload.setRef("ref").getRef());
		assertEquals(9000, payload.setSize(9000).getSize());
		assertEquals(8000, payload.setDistinctSize(8000).getDistinctSize());
		assertEquals(commits, payload.setCommits(commits).getCommits());
		assertEquals("a1b2", payload.setBefore("a1b2").getBefore());
		assertEquals("2b1a", payload.setAfter("2b1a").getAfter());
		assertTrue(payload.setCreated(true).isCreated());
		assertTrue(payload.setDeleted(true).isDeleted());
		assertTrue(payload.setForced(true).isForced());
		assertEquals("base", payload.setBaseRef("base").getBaseRef());
		assertEquals("http", payload.setCompare("http").getCompare());
	}
}
