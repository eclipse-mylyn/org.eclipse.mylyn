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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.service.MilestoneService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests of {@link MilestoneService}
 */
@RunWith(MockitoJUnitRunner.class)
public class MilestoneServiceTest {

	@Mock
	private GitHubClient gitHubClient;

	@Mock
	private GitHubResponse response;

	private MilestoneService milestoneService;

	/**
	 * Test case set up
	 *
	 * @throws IOException
	 */
	@Before
	public void before() throws IOException {
		doReturn(response).when(gitHubClient).get(any(GitHubRequest.class));
		milestoneService = new MilestoneService(gitHubClient);
	}

	/**
	 * Create milestone service with null client
	 */
	@Test(expected = IllegalArgumentException.class)
	public void constructorNullArgument() {
		new MilestoneService(null);
	}

	/**
	 * Create service using default constructor
	 */
	@Test
	public void defaultConstructor() {
		assertNotNull(new MilestoneService().getClient());
	}

	/**
	 * Get milestones with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getMilestonesNullUser() throws IOException {
		milestoneService.getMilestones(null, "not null", "not null");
	}

	/**
	 * Get milestones with empty user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getMilestonesEmptyUser() throws IOException {
		milestoneService.getMilestones("", "not null", "not null");
	}

	/**
	 * Get milestones with null repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getMilestonesNullRepositoryName() throws IOException {
		milestoneService.getMilestones("not null", null, "not null");
	}

	/**
	 * Get milestones with empty repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getMilestonesEmptyRepositoryName() throws IOException {
		milestoneService.getMilestones("not null", "", "not null");
	}

	/**
	 * Get milestones with null state
	 *
	 * @throws IOException
	 */
	@Test
	public void getMilestonesNullState() throws IOException {
		milestoneService.getMilestones("mu", "mr", null);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/mu/mr/milestones"));
		verify(gitHubClient).get(request);
	}

	/**
	 * Get milestones with open state
	 *
	 * @throws IOException
	 */
	@Test
	public void getMilestonesOpenState() throws IOException {
		milestoneService.getMilestones("mu", "mr", "open");
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/mu/mr/milestones?state=open"));
		verify(gitHubClient).get(request);
	}

	/**
	 * Get milestones with open state
	 *
	 * @throws IOException
	 */
	@Test
	public void getMilestonesOpenStateWithRepositoryId() throws IOException {
		RepositoryId id = new RepositoryId("mu", "mr");
		milestoneService.getMilestones(id, "open");
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/mu/mr/milestones?state=open"));
		verify(gitHubClient).get(request);
	}

	/**
	 * Create milestone with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createMilestoneNullUser() throws IOException {
		milestoneService.createMilestone(null, "repo", new Milestone());
	}

	/**
	 * Create milestone with empty user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createMilestoneEmptyUser() throws IOException {
		milestoneService.createMilestone("", "repo", new Milestone());
	}

	/**
	 * Create milestone with null repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createMilestoneNullRepositoryName() throws IOException {
		milestoneService.createMilestone("user", null, new Milestone());
	}

	/**
	 * Create milestone with empty repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createMilestoneEmptyRepositoryName() throws IOException {
		milestoneService.createMilestone("user", "", new Milestone());
	}

	/**
	 * Create milestone with null milestone
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createMilestoneNullMilestone() throws IOException {
		milestoneService.createMilestone("user", "repo", null);
	}

	/**
	 * Create valid milestone
	 *
	 * @throws IOException
	 */
	@Test
	public void createMilestone() throws IOException {
		Milestone milestone = new Milestone();
		milestoneService.createMilestone("user", "repo", milestone);
		verify(gitHubClient).post("/repos/user/repo/milestones", milestone,
				Milestone.class);
	}

	/**
	 * Create valid milestone
	 *
	 * @throws IOException
	 */
	@Test
	public void createMilestoneWithRepositoryId() throws IOException {
		RepositoryId id = new RepositoryId("user", "repo");
		Milestone milestone = new Milestone();
		milestoneService.createMilestone(id, milestone);
		verify(gitHubClient).post("/repos/user/repo/milestones", milestone,
				Milestone.class);
	}

	/**
	 * Delete milestone with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void deleteMilestoneNullUser() throws IOException {
		milestoneService.deleteMilestone(null, "repo", 1);
	}

	/**
	 * Delete milestone with empty user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void deleteMilestoneEmptyUser() throws IOException {
		milestoneService.deleteMilestone("", "repo", 2);
	}

	/**
	 * Delete milestone with null repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void deleteMilestoneNullRepositoryName() throws IOException {
		milestoneService.deleteMilestone("user", null, 3);
	}

	/**
	 * Delete milestone with empty repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void deleteMilestoneEmptyRepositoryName() throws IOException {
		milestoneService.deleteMilestone("user", "", 4);
	}

	/**
	 * Delete milestone with null milestone
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void deleteMilestoneNullMilestone() throws IOException {
		milestoneService.deleteMilestone("user", "repo", null);
	}

	/**
	 * Delete milestone with empty milestone
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void deleteMilestoneEmptyMilestone() throws IOException {
		milestoneService.deleteMilestone("user", "repo", "");
	}

	/**
	 * Delete valid milestone
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteMilestone() throws IOException {
		milestoneService.deleteMilestone("user", "repo", 40);
		verify(gitHubClient).delete("/repos/user/repo/milestones/40");
	}

	/**
	 * Delete valid milestone
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteMilestoneWithRepositoryId() throws IOException {
		RepositoryId id = new RepositoryId("user", "repo");
		milestoneService.deleteMilestone(id, 40);
		verify(gitHubClient).delete("/repos/user/repo/milestones/40");
	}

	/**
	 * Get milestone with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getMilestoneNullUser() throws IOException {
		milestoneService.getMilestone(null, "repo", 1);
	}

	/**
	 * Get milestone with empty user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getMilestoneEmptyUser() throws IOException {
		milestoneService.getMilestone("", "repo", 2);
	}

	/**
	 * Get milestone with null repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getMilestoneNullRepositoryName() throws IOException {
		milestoneService.getMilestone("user", null, 3);
	}

	/**
	 * Get milestone with empty repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getMilestoneEmptyRepositoryName() throws IOException {
		milestoneService.getMilestone("user", "", 4);
	}

	/**
	 * Get milestone with null id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getMilestoneNullId() throws IOException {
		milestoneService.getMilestone("user", "repo", null);
	}

	/**
	 * Get milestone with empty id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getMilestoneEmptyId() throws IOException {
		milestoneService.getMilestone("user", "repo", "");
	}

	/**
	 * Get milestone with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void getMilestone() throws IOException {
		milestoneService.getMilestone("user", "repo", 15);
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/user/repo/milestones/15");
		verify(gitHubClient).get(request);
	}

	/**
	 * Get milestone with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void getMilestoneWithRepositoryId() throws IOException {
		RepositoryId id = new RepositoryId("user", "repo");
		milestoneService.getMilestone(id, 15);
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/user/repo/milestones/15");
		verify(gitHubClient).get(request);
	}

	/**
	 * Edit milestone with null milestone
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void editMilestoneNullUser() throws IOException {
		milestoneService.editMilestone(RepositoryId.create("a", "b"), null);
	}

	/**
	 * Edit milestone
	 *
	 * @throws IOException
	 */
	@Test
	public void editMilestone() throws IOException {
		Milestone milestone = new Milestone();
		milestone.setNumber(1234);
		milestone.setTitle("a milestone");
		milestoneService
				.editMilestone(RepositoryId.create("a", "b"), milestone);
		verify(gitHubClient).post("/repos/a/b/milestones/1234", milestone,
				Milestone.class);
	}
}
