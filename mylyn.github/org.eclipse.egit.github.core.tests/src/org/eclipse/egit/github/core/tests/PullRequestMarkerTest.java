/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
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

import org.eclipse.egit.github.core.PullRequestMarker;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.junit.Test;

/**
 * Unit tests of {@link PullRequestMarker}
 */
public class PullRequestMarkerTest {

	/**
	 * Test default state of pull request marker
	 */
	@Test
	public void defaultState() {
		PullRequestMarker marker = new PullRequestMarker();
		assertNull(marker.getLabel());
		assertNull(marker.getRef());
		assertNull(marker.getRepo());
		assertNull(marker.getSha());
		assertNull(marker.getUser());
	}

	/**
	 * Test updating pull request marker fields
	 */
	@Test
	public void updateFields() {
		PullRequestMarker marker = new PullRequestMarker();
		assertEquals("lab1", marker.setLabel("lab1").getLabel());
		assertEquals("master", marker.setRef("master").getRef());
		Repository repo = new Repository().setName("trepo");
		assertEquals(repo, marker.setRepo(repo).getRepo());
		assertEquals("000", marker.setSha("000").getSha());
		User user = new User().setLogin("tuser");
		assertEquals(user, marker.setUser(user).getUser());
	}
}
