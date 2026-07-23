/*******************************************************************************
 *  Copyright (c) 2011 Christian Trutz
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Christian Trutz - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.eclipse.egit.github.core.GistChangeStatus;
import org.eclipse.egit.github.core.GistRevision;
import org.eclipse.egit.github.core.User;
import org.junit.Test;

/**
 * Unit tests of {@link GistRevision}
 */
public class GistRevisionTest {

	/**
	 * Test default state of gist revision
	 */
	@Test
	public void defaultState() {
		GistRevision revision = new GistRevision();
		assertNull(revision.getChangeStatus());
		assertNull(revision.getCommittedAt());
		assertNull(revision.getUrl());
		assertNull(revision.getUser());
		assertNull(revision.getVersion());
	}

	/**
	 * Test updating fields of a gist revision
	 */
	@Test
	public void updateFields() {
		GistRevision revision = new GistRevision();
		GistChangeStatus status = new GistChangeStatus();
		assertEquals(status, revision.setChangeStatus(status).getChangeStatus());
		assertEquals(new Date(5000), revision.setCommittedAt(new Date(5000))
				.getCommittedAt());
		assertEquals("url", revision.setUrl("url").getUrl());
		User user = new User().setLogin("testuser");
		assertEquals(user, revision.setUser(user).getUser());
		assertEquals("abc", revision.setVersion("abc").getVersion());
	}

	/**
	 * Test non-mutable committed at date
	 */
	@Test
	public void getCreatedAReferenceMutableObject() {
		GistRevision gistRevision = new GistRevision();
		Date date = new Date(10000);
		gistRevision.setCommittedAt(date);
		gistRevision.getCommittedAt().setTime(0);
		assertTrue(gistRevision.getCommittedAt().getTime() != 0);
		date.setTime(1000);
		assertEquals(10000, gistRevision.getCommittedAt().getTime());
	}
}
