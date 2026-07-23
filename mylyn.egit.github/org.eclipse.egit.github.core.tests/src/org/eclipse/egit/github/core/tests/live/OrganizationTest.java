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
package org.eclipse.egit.github.core.tests.live;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.OrganizationService;
import org.junit.Test;

/**
 * 
 */
public class OrganizationTest extends LiveTest {

	/**
	 * Test fetching authenticated user's orgs
	 * 
	 * @throws Exception
	 */
	@Test
	public void authenticatedUsersOrgs() throws Exception {
		checkUser();

		OrganizationService service = new OrganizationService(client);
		List<User> orgs = service.getOrganizations();
		assertNotNull(orgs);
		assertFalse(orgs.isEmpty());
		for (User org : orgs) {
			assertNotNull(org);
			assertNotNull(org.getAvatarUrl());
			assertTrue(org.getId() > 0);
			assertNotNull(org.getLogin());
			assertNotNull(org.getUrl());
			User fetched = service.getOrganization(org.getLogin());
			assertNotNull(fetched);
			assertEquals(org.getLogin(), fetched.getLogin());
		}
	}

	/**
	 * Test fetching given user's orgs
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenUsersOrgs() throws Exception {
		checkUser();

		OrganizationService service = new OrganizationService(client);
		List<User> orgs = service.getOrganizations(client.getUser());
		assertNotNull(orgs);
		assertFalse(orgs.isEmpty());
		for (User org : orgs) {
			assertNotNull(org);
			assertNotNull(org.getAvatarUrl());
			assertTrue(org.getId() > 0);
			assertNotNull(org.getLogin());
			assertNotNull(org.getUrl());
			User fetched = service.getOrganization(org.getLogin());
			assertNotNull(fetched);
			assertEquals(org.getLogin(), fetched.getLogin());
		}
	}

	/**
	 * Test fetching members of first org that the currently authenticated user
	 * is in.
	 * 
	 * @throws Exception
	 */
	@Test
	public void membersInFirstOrg() throws Exception {
		checkUser();

		OrganizationService service = new OrganizationService(client);
		List<User> orgs = service.getOrganizations(client.getUser());
		assertNotNull(orgs);
		assertFalse(orgs.isEmpty());
		String orgName = orgs.get(0).getLogin();
		assertNotNull(orgName);
		List<User> members = service.getMembers(orgName);
		assertNotNull(members);
		assertFalse(members.isEmpty());
		boolean selfFound = false;
		for (User member : members) {
			assertNotNull(member);
			assertNotNull(member.getLogin());
			assertTrue(service.isMember(orgName, member.getLogin()));
			if (client.getUser().equals(member.getLogin()))
				selfFound = true;
		}
		assertTrue(selfFound);
	}

	/**
	 * Test checking member in organization for specific user
	 * 
	 * @throws Exception
	 */
	@Test
	public void membershipInOrg() throws Exception {
		checkUser();

		OrganizationService service = new OrganizationService(client);
		List<User> orgs = service.getOrganizations(client.getUser());
		assertNotNull(orgs);
		assertFalse(orgs.isEmpty());
		String orgName = orgs.get(0).getLogin();
		assertNotNull(orgName);
		assertTrue(service.isMember(orgName, client.getUser()));
		assertFalse(service.isMember(orgName,
				"notarealuserintheorg" + System.nanoTime()));
		assertFalse(service.isPublicMember(orgName, "notarealuserintheorg"
				+ System.nanoTime()));
	}
}
