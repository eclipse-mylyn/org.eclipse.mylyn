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
package org.eclipse.mylyn.github.internal;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.AssertionFailedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link MilestoneService}
 */
@SuppressWarnings("restriction")
@RunWith(MockitoJUnitRunner.class)
public class MilestoneServiceTest {

	@Mock
	private GitHubClient gitHubClient;

	@Mock
	private GitHubResponse response;

	private MilestoneService milestoneService;

	@Before
	public void before() throws IOException {
		doReturn(response).when(gitHubClient).get(any(GitHubRequest.class));
		milestoneService = new MilestoneService(gitHubClient);
	}

	@Test(expected = AssertionFailedException.class)
	public void constructor_NullArgument() {
		new MilestoneService(null);
	}

	@Test(expected = AssertionFailedException.class)
	public void getMilestones_NullUser() throws IOException {
		milestoneService.getMilestones(null, "not null", "not null");
	}

	@Test(expected = AssertionFailedException.class)
	public void getMilestones_NullRepository() throws IOException {
		milestoneService.getMilestones("not null", null, "not null");
	}

	@Test
	public void getMilestones_NullState() throws IOException {
		milestoneService.getMilestones("test_user", "test_repository", null);
		TypeToken<List<Milestone>> milestonesToken = new TypeToken<List<Milestone>>() {
		};
		verify(gitHubClient).get(any(GitHubRequest.class));
	}

	@Test
	public void getMilestones_OK() throws IOException {
		milestoneService.getMilestones("test_user", "test_repository",
				"test_state");
		TypeToken<List<Milestone>> milestonesToken = new TypeToken<List<Milestone>>() {
		};
		verify(gitHubClient).get(any(GitHubRequest.class));
	}

}
