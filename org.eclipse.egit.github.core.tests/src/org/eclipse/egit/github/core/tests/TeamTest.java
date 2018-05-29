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

import org.eclipse.egit.github.core.Team;
import org.junit.Test;

/**
 * Unit tests of {@link Team}
 */
public class TeamTest {

	/**
	 * Test default state of team
	 */
	@Test
	public void defaultState() {
		Team team = new Team();
		assertEquals(0, team.getId());
		assertEquals(0, team.getMembersCount());
		assertNull(team.getName());
		assertNull(team.getPermission());
		assertEquals(0, team.getReposCount());
		assertNull(team.getUrl());
	}

	/**
	 * Test updating team fields
	 */
	@Test
	public void updateFields() {
		Team team = new Team();
		assertEquals(12, team.setId(12).getId());
		assertEquals(5, team.setMembersCount(5).getMembersCount());
		assertEquals("pullers", team.setName("pullers").getName());
		assertEquals("ro", team.setPermission("ro").getPermission());
		assertEquals(50, team.setReposCount(50).getReposCount());
		assertEquals("url", team.setUrl("url").getUrl());
	}
}
