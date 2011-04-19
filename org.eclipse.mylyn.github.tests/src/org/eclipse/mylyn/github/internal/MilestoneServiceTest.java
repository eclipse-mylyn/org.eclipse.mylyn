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

import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.AssertionFailedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.gson.reflect.TypeToken;

/**
 * Unit tests for {@link MilestoneService}
 */
@SuppressWarnings("restriction")
@RunWith(MockitoJUnitRunner.class)
public class MilestoneServiceTest {

	@Mock
	private GitHubClient gitHubClient;

	private MilestoneService milestoneService;

	@Before
	public void before() {
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
		verify(gitHubClient).get(
				"/repos/test_user/test_repository/milestones.json", null,
				milestonesToken.getType());
	}

	@Test
	public void getMilestones_OK() throws IOException {
		milestoneService.getMilestones("test_user", "test_repository",
				"test_state");
		TypeToken<List<Milestone>> milestonesToken = new TypeToken<List<Milestone>>() {
		};
		verify(gitHubClient).get(
				"/repos/test_user/test_repository/milestones.json",
				Collections.singletonMap(IssueService.FILTER_STATE,
						"test_state"), milestonesToken.getType());
	}

}
