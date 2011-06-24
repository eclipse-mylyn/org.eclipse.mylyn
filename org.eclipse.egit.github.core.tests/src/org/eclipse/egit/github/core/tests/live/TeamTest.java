/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests.live;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.egit.github.core.Team;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.OrganizationService;
import org.eclipse.egit.github.core.service.TeamService;
import org.junit.Test;

/**
 * 
 */
public class TeamTest extends LiveTest {

	/**
	 * Test fetching teams in an org
	 * 
	 * @throws Exception
	 */
	@Test
	public void fetchTeamsFromOrg() throws Exception {
		checkUser();

		OrganizationService orgService = new OrganizationService(client);
		List<User> orgs = orgService.getOrganizations();
		assertNotNull(orgs);
		assertFalse(orgs.isEmpty());
		User org = orgs.get(0);
		assertNotNull(org);
		assertNotNull(org.getLogin());
		TeamService teamService = new TeamService(client);
		List<Team> teams = teamService.getTeams(org.getLogin());
		assertNotNull(teams);
		assertFalse(teams.isEmpty());
		for (Team team : teams) {
			assertNotNull(team);
			assertNotNull(team.getName());
			assertNotNull(team.getUrl());
			assertTrue(team.getReposCount() >= 0);
			assertTrue(team.getMembersCount() >= 0);
			assertTrue(team.getId() >= 0);
			assertNotNull(teamService.getTeam(team.getId()));
		}
	}

	/**
	 * Test checking membership in org
	 * 
	 * @throws Exception
	 */
	@Test
	public void membershipInTeam() throws Exception {
		checkUser();

		OrganizationService orgService = new OrganizationService(client);
		List<User> orgs = orgService.getOrganizations();
		assertNotNull(orgs);
		assertFalse(orgs.isEmpty());
		User org = orgs.get(0);
		assertNotNull(org);
		assertNotNull(org.getLogin());
		TeamService teamService = new TeamService(client);
		List<Team> teams = teamService.getTeams(org.getLogin());
		assertNotNull(teams);
		assertFalse(teams.isEmpty());
		Team first = teams.get(0);
		assertNotNull(first);
		List<User> members = teamService.getMembers(first.getId());
		assertNotNull(members);
		assertFalse(members.isEmpty());
		for (User member : members) {
			assertNotNull(member);
			assertNotNull(member.getLogin());
			assertTrue(teamService.isMember(first.getId(), member.getLogin()));
		}
	}
}
