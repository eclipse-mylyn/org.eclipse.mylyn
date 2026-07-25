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
 *    See git history
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.service.OrganizationService;
import org.eclipse.egit.github.core.service.OrganizationService.RoleFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Unit test of {@link OrganizationService}
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("nls")
public class OrganizationServiceTest {

	@Mock
	private GitHubClient client;

	@Mock
	private GitHubResponse response;

	private OrganizationService service;

	/**
	 * Test case set up
	 *
	 * @throws IOException
	 */
	@BeforeEach
	public void before() throws IOException {
		doReturn(response).when(client).get(any(GitHubRequest.class));
		service = new OrganizationService(client);
	}

	/**
	 * Create service using default constructor
	 */
	@Test
	public void constructor() {
		assertNotNull(new OrganizationService().getClient());
	}

	/**
	 * Get organizations
	 *
	 * @throws IOException
	 */
	@Test
	public void getCurrentUserOrganizations() throws IOException {
		service.getOrganizations();
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/user/orgs"));
		verify(client).get(request);
	}

	/**
	 * Get organizations with null user
	 *
	 * @throws IOException
	 */
	@Test
	public void getOrganizationsNullUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.getOrganizations(null));
	}

	/**
	 * Get organization with empty user
	 *
	 * @throws IOException
	 */
	@Test
	public void getOrganizationsEmptyUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.getOrganizations(""));
	}

	/**
	 * Get organizations
	 *
	 * @throws IOException
	 */
	@Test
	public void getOrganizations() throws IOException {
		service.getOrganizations("auser");
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/users/auser/orgs"));
		verify(client).get(request);
	}

	/**
	 * Get organization with null name
	 *
	 * @throws IOException
	 */
	@Test
	public void getOrganizationNullName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.getOrganization(null));
	}

	/**
	 * Get organization with empty name
	 *
	 * @throws IOException
	 */
	@Test
	public void getOrganizationEmptyName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.getOrganization(""));
	}

	/**
	 * Get organization
	 *
	 * @throws IOException
	 */
	@Test
	public void getOrganization() throws IOException {
		service.getOrganization("group");
		GitHubRequest request = new GitHubRequest();
		request.setUri("/orgs/group");
		verify(client).get(request);
	}

	/**
	 * Edit organization with null org.
	 *
	 * @throws IOException
	 */
	@Test
	public void editOrganizationNullOrg() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.editOrganization(null));
	}

	/**
	 * Edit organization with null org. name
	 *
	 * @throws IOException
	 */
	@Test
	public void editOrganizationNullOrgName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.editOrganization(new User().setLogin(null)));
	}

	/**
	 * Edit organization with empty org. name
	 *
	 * @throws IOException
	 */
	@Test
	public void editOrganizationEmptyOrgName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.editOrganization(new User().setLogin("")));
	}

	/**
	 * Edit organization
	 *
	 * @throws IOException
	 */
	@Test
	public void editOrganization() throws IOException {
		User org = new User().setLogin("group");
		service.editOrganization(org);
		verify(client).post("/orgs/group", org, User.class);
	}

	/**
	 * Get members with null name
	 *
	 * @throws IOException
	 */
	@Test
	public void getMembersNullName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.getMembers(null));
	}

	/**
	 * Get members with empty name
	 *
	 * @throws IOException
	 */
	@Test
	public void getMembersEmptyName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.getMembers(""));
	}

	/**
	 * Get members
	 *
	 * @throws IOException
	 */
	@Test
	public void getMembers() throws IOException {
		service.getMembers("group");
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/orgs/group/members"));
		verify(client).get(request);
	}

	/**
	 * Get members with role filter "all"
	 *
	 * @throws IOException
	 */
	@Test
	public void getMembersAll() throws IOException {
		testMembersByRole(RoleFilter.all);
	}

	/**
	 * Get members with role filter "all"
	 *
	 * @throws IOException
	 */
	@Test
	public void getMembersAdmin() throws IOException {
		testMembersByRole(RoleFilter.admin);
	}

	/**
	 * Get members with role filter "all"
	 *
	 * @throws IOException
	 */
	@Test
	public void getMembersMember() throws IOException {
		testMembersByRole(RoleFilter.member);
	}

	private void testMembersByRole(RoleFilter roleFilter) throws IOException {
		service.getMembers("group", roleFilter);
		HashMap<String, String> params = new HashMap<>();
		params.put("role", roleFilter.toString());
		GitHubRequest request = new GitHubRequest();
		request.setParams(params);
		request.setUri(Utils.page("/orgs/group/members?role=" + roleFilter.toString()));
		verify(client).get(request);
	}

	/**
	 * Get public members with null name
	 *
	 * @throws IOException
	 */
	@Test
	public void getPublicMembersNullName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.getPublicMembers(null));
	}

	/**
	 * Get public members with empty name
	 *
	 * @throws IOException
	 */
	@Test
	public void getPublicMembersEmptyName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.getPublicMembers(""));
	}

	/**
	 * Get public members
	 *
	 * @throws IOException
	 */
	@Test
	public void getPublicMembers() throws IOException {
		service.getPublicMembers("group");
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/orgs/group/public_members"));
		verify(client).get(request);
	}

	/**
	 * Is member with null name
	 *
	 * @throws IOException
	 */
	@Test
	public void isMemberNullName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.isMember(null, "person"));
	}

	/**
	 * Is member with empty name
	 *
	 * @throws IOException
	 */
	@Test
	public void isMemberEmptyName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.isMember("", "person"));
	}

	/**
	 * Is member with null user
	 *
	 * @throws IOException
	 */
	@Test
	public void isMemberNullUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.isMember("group", null));
	}

	/**
	 * Is member with empty user
	 *
	 * @throws IOException
	 */
	@Test
	public void isMemberEmptyUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.isMember("group", ""));
	}

	/**
	 * Is member
	 *
	 * @throws IOException
	 */
	@Test
	public void isMember() throws IOException {
		service.isMember("group", "person");
		GitHubRequest request = new GitHubRequest();
		request.setUri("/orgs/group/members/person");
		verify(client).get(request);
	}

	/**
	 * Is public member with null name
	 *
	 * @throws IOException
	 */
	@Test
	public void isPublicMemberNullName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.isPublicMember(null, "person"));
	}

	/**
	 * Is public member with empty name
	 *
	 * @throws IOException
	 */
	@Test
	public void isPublicMemberEmptyName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.isPublicMember("", "person"));
	}

	/**
	 * Is public member with null user
	 *
	 * @throws IOException
	 */
	@Test
	public void isPublicMemberNullUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.isPublicMember("group", null));
	}

	/**
	 * Is public member with empty user
	 *
	 * @throws IOException
	 */
	@Test
	public void isPublicMemberEmptyUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.isPublicMember("group", ""));
	}

	/**
	 * Is public member
	 *
	 * @throws IOException
	 */
	@Test
	public void isPublicMember() throws IOException {
		service.isPublicMember("group", "person");
		GitHubRequest request = new GitHubRequest();
		request.setUri("/orgs/group/public_members/person");
		verify(client).get(request);
	}

	/**
	 * Show membership with null name
	 *
	 * @throws IOException
	 */
	@Test
	public void showMembershipNullName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.showMembership(null, "person"));
	}

	/**
	 * Show member with empty name
	 *
	 * @throws IOException
	 */
	@Test
	public void showMembershipEmptyName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.showMembership("", "person"));
	}

	/**
	 * Show membership with null user
	 *
	 * @throws IOException
	 */
	@Test
	public void showMembershipNullUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.showMembership("group", null));
	}

	/**
	 * Show membership with empty user
	 *
	 * @throws IOException
	 */
	@Test
	public void showMembershipEmptyUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.showMembership("group", ""));
	}

	/**
	 * Show membership
	 *
	 * @throws IOException
	 */
	@Test
	public void showMembership() throws IOException {
		service.showMembership("group", "person");
		verify(client).put("/orgs/group/public_members/person");
	}

	/**
	 * Hide membership with null name
	 *
	 * @throws IOException
	 */
	@Test
	public void hideMembershipNullName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.hideMembership(null, "person"));
	}

	/**
	 * Hide membership with empty name
	 *
	 * @throws IOException
	 */
	@Test
	public void hideMembershipEmptyName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.hideMembership("", "person"));
	}

	/**
	 * Hide membership with null user
	 *
	 * @throws IOException
	 */
	@Test
	public void hideMembershipNullUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.hideMembership("group", null));
	}

	/**
	 * Hide membership with empty user
	 *
	 * @throws IOException
	 */
	@Test
	public void hideMembershipEmptyUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.hideMembership("group", ""));
	}

	/**
	 * Hide membership
	 *
	 * @throws IOException
	 */
	@Test
	public void hideMembership() throws IOException {
		service.hideMembership("group", "person");
		verify(client).delete("/orgs/group/public_members/person");
	}

	/**
	 * Remove member with null name
	 *
	 * @throws IOException
	 */
	@Test
	public void removeMemberNullName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.removeMember(null, "person"));
	}

	/**
	 * Remove member with empty name
	 *
	 * @throws IOException
	 */
	@Test
	public void removeMemberEmptyName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.removeMember("", "person"));
	}

	/**
	 * Remove member with null user
	 *
	 * @throws IOException
	 */
	@Test
	public void removeMemberNullUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.removeMember("group", null));
	}

	/**
	 * Remove member with empty user
	 *
	 * @throws IOException
	 */
	@Test
	public void removeMemberEmptyUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.removeMember("group", ""));
	}

	/**
	 * Remove member
	 *
	 * @throws IOException
	 */
	@Test
	public void removeMember() throws IOException {
		service.removeMember("group", "person");
		verify(client).delete("/orgs/group/members/person");
	}
}
