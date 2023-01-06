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

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.Team;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.event.TeamAddPayload;
import org.junit.Test;

/**
 * Unit tests of {@link TeamAddPayload}
 */
public class TeamAddPayloadTest {

	/**
	 * Test default state of TeamAddPayload
	 */
	@Test
	public void defaultState() {
		TeamAddPayload payload = new TeamAddPayload();
		assertNull(payload.getTeam());
		assertNull(payload.getUser());
		assertNull(payload.getRepo());
	}

	/**
	 * Test updating TeamAddPayload fields
	 */
	@Test
	public void updateFields() {
		TeamAddPayload payload = new TeamAddPayload();
		Team team = new Team().setName("team");
		User user = new User().setLogin("user");
		Repository repo = new Repository().setName("repo");
		assertEquals(team, payload.setTeam(team).getTeam());
		assertEquals(user, payload.setUser(user).getUser());
		assertEquals(repo, payload.setRepo(repo).getRepo());
	}
}
