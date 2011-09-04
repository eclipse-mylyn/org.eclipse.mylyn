/*******************************************************************************
 *  Copyright (c) 2011 Christian Trutz
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Christian Trutz - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.service.MilestoneService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
	 * Get milestones with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getMilestonesNullUser() throws IOException {
		milestoneService.getMilestones(null, "not null", "not null");
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
	 * Get milestones with null state
	 *
	 * @throws IOException
	 */
	@Test
	public void getMilestonesNullState() throws IOException {
		milestoneService.getMilestones("test_user", "test_repository", null);
		verify(gitHubClient).get(any(GitHubRequest.class));
	}

	/**
	 * Create milestones with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void getMilestonesOK() throws IOException {
		milestoneService.getMilestones("test_user", "test_repository",
				"test_state");
		verify(gitHubClient).get(any(GitHubRequest.class));
	}
}
