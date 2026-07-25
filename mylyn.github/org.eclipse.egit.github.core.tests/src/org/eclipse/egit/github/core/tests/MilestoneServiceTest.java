/*******************************************************************************
 * *  Copyright (c) 2011, 2019 Christian Trutz and others.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Christian Trutz - initial API and implementation
 *    See git history
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.client.GsonUtils;
import org.eclipse.egit.github.core.service.MilestoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Unit tests of {@link MilestoneService}
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("nls")
public class MilestoneServiceTest {

	@Mock
	private GitHubClient gitHubClient;

	@Mock
	private GitHubResponse response;

	@Captor
	private ArgumentCaptor<Object> dtoCaptor;

	private MilestoneService milestoneService;

	/**
	 * Test case set up
	 *
	 * @throws IOException
	 */
	@BeforeEach
	public void before() throws IOException {
		doReturn(response).when(gitHubClient).get(any(GitHubRequest.class));
		milestoneService = new MilestoneService(gitHubClient);
	}

	/**
	 * Create milestone service with null client
	 */
	@Test
	public void constructorNullArgument() {
		assertThrows(IllegalArgumentException.class, () -> new MilestoneService(null));
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
	@Test
	public void getMilestonesNullUser() throws IOException {
		assertThrows(IllegalArgumentException.class,
				() -> milestoneService.getMilestones(null, "not null", "not null"));
	}

	/**
	 * Get milestones with empty user
	 *
	 * @throws IOException
	 */
	@Test
	public void getMilestonesEmptyUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> milestoneService.getMilestones("", "not null", "not null"));
	}

	/**
	 * Get milestones with null repository name
	 *
	 * @throws IOException
	 */
	@Test
	public void getMilestonesNullRepositoryName() throws IOException {
		assertThrows(IllegalArgumentException.class,
				() -> milestoneService.getMilestones("not null", null, "not null"));
	}

	/**
	 * Get milestones with empty repository name
	 *
	 * @throws IOException
	 */
	@Test
	public void getMilestonesEmptyRepositoryName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> milestoneService.getMilestones("not null", "", "not null"));
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
	@Test
	public void createMilestoneNullUser() throws IOException {
		assertThrows(IllegalArgumentException.class,
				() -> milestoneService.createMilestone(null, "repo", new Milestone()));
	}

	/**
	 * Create milestone with empty user
	 *
	 * @throws IOException
	 */
	@Test
	public void createMilestoneEmptyUser() throws IOException {
		assertThrows(IllegalArgumentException.class,
				() -> milestoneService.createMilestone("", "repo", new Milestone()));
	}

	/**
	 * Create milestone with null repository name
	 *
	 * @throws IOException
	 */
	@Test
	public void createMilestoneNullRepositoryName() throws IOException {
		assertThrows(IllegalArgumentException.class,
				() -> milestoneService.createMilestone("user", null, new Milestone()));
	}

	/**
	 * Create milestone with empty repository name
	 *
	 * @throws IOException
	 */
	@Test
	public void createMilestoneEmptyRepositoryName() throws IOException {
		assertThrows(IllegalArgumentException.class,
				() -> milestoneService.createMilestone("user", "", new Milestone()));
	}

	/**
	 * Create milestone with null milestone
	 *
	 * @throws IOException
	 */
	@Test
	public void createMilestoneNullMilestone() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> milestoneService.createMilestone("user", "repo", null));
	}

	/**
	 * Create milestone with an empty milestone
	 *
	 * @throws IOException
	 */
	@Test
	public void createMilestoneEmptyMilestone() throws IOException {
		assertThrows(IllegalArgumentException.class,
				() -> milestoneService.createMilestone("user", "repo", new Milestone()));
	}

	/**
	 * Create valid milestone
	 *
	 * @throws IOException
	 */
	@Test
	public void createMilestone() throws IOException {
		Milestone milestone = new Milestone();
		milestone.setTitle("A title");
		milestoneService.createMilestone("user", "repo", milestone);
		verify(gitHubClient).post(eq("/repos/user/repo/milestones"), dtoCaptor.capture(), eq(Milestone.class));
		Object dto = dtoCaptor.getValue();
		assertEquals("{\"title\":\"A title\"}", GsonUtils.getGson().toJson(dto));
	}

	/**
	 * Create invalid milestone without title.
	 *
	 * @throws IOException
	 */
	@Test
	public void createMilestoneNoTitle() throws IOException {
		Milestone milestone = new Milestone();
		milestone.setDescription("A description");
		assertThrows(IllegalArgumentException.class, () -> milestoneService.createMilestone("user", "repo", milestone));
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
		milestone.setTitle("A title");
		milestone.setDescription("A description");
		milestoneService.createMilestone(id, milestone);
		verify(gitHubClient).post(eq("/repos/user/repo/milestones"), dtoCaptor.capture(), eq(Milestone.class));
		Object dto = dtoCaptor.getValue();
		assertEquals("{\"title\":\"A title\",\"description\":\"A description\"}", GsonUtils.getGson().toJson(dto));
	}

	/**
	 * Create an invalid milestone with a wrong state.
	 *
	 * @throws IOException
	 */
	@Test
	public void createMilestoneInvalidState() throws IOException {
		RepositoryId id = new RepositoryId("user", "repo");
		Milestone milestone = new Milestone();
		milestone.setTitle("A title");
		milestone.setState("bogus");
		assertThrows(IllegalArgumentException.class, () -> milestoneService.createMilestone(id, milestone));
	}

	/**
	 * Create valid closed milestone.
	 *
	 * @throws IOException
	 */
	@Test
	public void createMilestoneClosed() throws IOException {
		RepositoryId id = new RepositoryId("user", "repo");
		Milestone milestone = new Milestone();
		milestone.setTitle("A title");
		milestone.setState("closed");
		milestoneService.createMilestone(id, milestone);
		verify(gitHubClient).post(eq("/repos/user/repo/milestones"), dtoCaptor.capture(), eq(Milestone.class));
		Object dto = dtoCaptor.getValue();
		assertEquals("{\"title\":\"A title\",\"state\":\"closed\"}", GsonUtils.getGson().toJson(dto));
	}

	/**
	 * Create valid open milestone.
	 *
	 * @throws IOException
	 */
	@Test
	public void createMilestoneOpen() throws IOException {
		RepositoryId id = new RepositoryId("user", "repo");
		Milestone milestone = new Milestone();
		milestone.setTitle("A title");
		milestone.setState("open");
		milestoneService.createMilestone(id, milestone);
		verify(gitHubClient).post(eq("/repos/user/repo/milestones"), dtoCaptor.capture(), eq(Milestone.class));
		Object dto = dtoCaptor.getValue();
		assertEquals("{\"title\":\"A title\",\"state\":\"open\"}", GsonUtils.getGson().toJson(dto));
	}

	/**
	 * Create valid open milestone.
	 *
	 * @throws IOException
	 */
	@Test
	public void createMilestoneFull() throws IOException {
		RepositoryId id = new RepositoryId("user", "repo");
		Milestone milestone = new Milestone();
		milestone.setTitle("A title");
		milestone.setDescription("A description");
		milestone.setState("open");
		Calendar cal = Calendar.getInstance();
		cal.set(2019, 1, 1, 12, 0, 0);
		Date date = Date.from(cal.toInstant());
		milestone.setDueOn(date);
		milestone.setCreatedAt(date);
		String serializedDate = GsonUtils.getGson().toJson(date);
		milestoneService.createMilestone(id, milestone);
		verify(gitHubClient).post(eq("/repos/user/repo/milestones"), dtoCaptor.capture(), eq(Milestone.class));
		Object dto = dtoCaptor.getValue();
		assertEquals("{\"title\":\"A title\",\"state\":\"open\",\"description\":\"A description\",\"due_on\":"
				+ serializedDate + "}", GsonUtils.getGson().toJson(dto));
	}

	/**
	 * Delete milestone with null user
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteMilestoneNullUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> milestoneService.deleteMilestone(null, "repo", 1));
	}

	/**
	 * Delete milestone with empty user
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteMilestoneEmptyUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> milestoneService.deleteMilestone("", "repo", 2));
	}

	/**
	 * Delete milestone with null repository name
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteMilestoneNullRepositoryName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> milestoneService.deleteMilestone("user", null, 3));
	}

	/**
	 * Delete milestone with empty repository name
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteMilestoneEmptyRepositoryName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> milestoneService.deleteMilestone("user", "", 4));
	}

	/**
	 * Delete milestone with null milestone
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteMilestoneNullMilestone() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> milestoneService.deleteMilestone("user", "repo", null));
	}

	/**
	 * Delete milestone with empty milestone
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteMilestoneEmptyMilestone() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> milestoneService.deleteMilestone("user", "repo", ""));
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
	@Test
	public void getMilestoneNullUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> milestoneService.getMilestone(null, "repo", 1));
	}

	/**
	 * Get milestone with empty user
	 *
	 * @throws IOException
	 */
	@Test
	public void getMilestoneEmptyUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> milestoneService.getMilestone("", "repo", 2));
	}

	/**
	 * Get milestone with null repository name
	 *
	 * @throws IOException
	 */
	@Test
	public void getMilestoneNullRepositoryName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> milestoneService.getMilestone("user", null, 3));
	}

	/**
	 * Get milestone with empty repository name
	 *
	 * @throws IOException
	 */
	@Test
	public void getMilestoneEmptyRepositoryName() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> milestoneService.getMilestone("user", "", 4));
	}

	/**
	 * Get milestone with null id
	 *
	 * @throws IOException
	 */
	@Test
	public void getMilestoneNullId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> milestoneService.getMilestone("user", "repo", null));
	}

	/**
	 * Get milestone with empty id
	 *
	 * @throws IOException
	 */
	@Test
	public void getMilestoneEmptyId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> milestoneService.getMilestone("user", "repo", ""));
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
	@Test
	public void editMilestoneNullMilestone() throws IOException {
		assertThrows(IllegalArgumentException.class,
				() -> milestoneService.editMilestone(RepositoryId.create("a", "b"), null));
	}

	/**
	 * Edit milestone with empty milestone
	 *
	 * @throws IOException
	 */
	@Test
	public void editMilestoneEmptyMilestone() throws IOException {
		assertThrows(IllegalArgumentException.class,
				() -> milestoneService.editMilestone(RepositoryId.create("a", "b"), new Milestone()));
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
		milestoneService.editMilestone(RepositoryId.create("a", "b"), milestone);
		verify(gitHubClient).post(eq("/repos/a/b/milestones/1234"), dtoCaptor.capture(), eq(Milestone.class));
		Object dto = dtoCaptor.getValue();
		assertEquals("{\"title\":\"a milestone\"}", GsonUtils.getGson().toJson(dto));
	}

	/**
	 * Edit milestone description
	 *
	 * @throws IOException
	 */
	@Test
	public void editMilestoneDescription() throws IOException {
		Milestone milestone = new Milestone();
		milestone.setNumber(1234);
		milestone.setDescription("A description");
		milestoneService.editMilestone(RepositoryId.create("a", "b"), milestone);
		verify(gitHubClient).post(eq("/repos/a/b/milestones/1234"), dtoCaptor.capture(), eq(Milestone.class));
		Object dto = dtoCaptor.getValue();
		assertEquals("{\"description\":\"A description\"}", GsonUtils.getGson().toJson(dto));
	}

	/**
	 * Edit milestone with all fields
	 *
	 * @throws IOException
	 */
	@Test
	public void editMilestoneFull() throws IOException {
		Milestone milestone = new Milestone();
		milestone.setNumber(1234);
		milestone.setTitle("a milestone");
		milestone.setDescription("a description");
		milestone.setState("closed");
		Calendar cal = Calendar.getInstance();
		cal.set(2019, 1, 1, 12, 0, 0);
		Date date = Date.from(cal.toInstant());
		milestone.setDueOn(date);
		milestone.setCreatedAt(date);
		String serializedDate = GsonUtils.getGson().toJson(date);
		milestoneService.editMilestone(RepositoryId.create("a", "b"), milestone);
		verify(gitHubClient).post(eq("/repos/a/b/milestones/1234"), dtoCaptor.capture(), eq(Milestone.class));
		Object dto = dtoCaptor.getValue();
		assertEquals("{\"title\":\"a milestone\",\"state\":\"closed\",\"description\":\"a description\",\"due_on\":"
				+ serializedDate + "}", GsonUtils.getGson().toJson(dto));
	}
}
