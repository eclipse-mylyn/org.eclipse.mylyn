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
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests.live;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.MilestoneService;
import org.junit.Test;

/**
 * Unit tests of {@link MilestoneService}
 */
public class MilestoneTest extends LiveTest {

	/**
	 * Test creating and deleting a milestone
	 *
	 * @throws Exception
	 */
	@Test
	public void createDelete() throws Exception {
		assertNotNull("User is required for test", client.getUser());
		assertNotNull("Repo is required for test", writableRepo);

		Milestone m = new Milestone();
		m.setDescription("desc " + System.currentTimeMillis());
		m.setTitle("Title " + System.currentTimeMillis());
		m.setDueOn(new Date((System.currentTimeMillis() / 1000) * 1000));
		MilestoneService service = new MilestoneService(client);
		Milestone created = service.createMilestone(client.getUser(),
				writableRepo, m);
		assertNotNull(created);
		assertEquals(m.getDescription(), created.getDescription());
		assertEquals(m.getTitle(), created.getTitle());
		assertEquals(m.getDueOn().getTime(), created.getDueOn().getTime());
		List<Milestone> milestones = service.getMilestones(client.getUser(),
				writableRepo, m.getState());
		Milestone fetched = null;
		assertNotNull(milestones);
		for (Milestone milestone : milestones)
			if (created.getNumber() == milestone.getNumber()) {
				fetched = milestone;
				break;
			}
		assertNotNull(fetched);
		assertEquals(created.getClosedIssues(), fetched.getClosedIssues());
		assertEquals(created.getDescription(), fetched.getDescription());
		assertEquals(created.getNumber(), fetched.getNumber());
		assertEquals(created.getOpenIssues(), fetched.getOpenIssues());
		assertEquals(created.getState(), fetched.getState());
		assertEquals(created.getTitle(), fetched.getTitle());
		assertEquals(created.getUrl(), fetched.getUrl());
		assertEquals(created.getCreatedAt(), fetched.getCreatedAt());
		assertEquals(created.getDueOn(), fetched.getDueOn());
		assertEquals(created.getCreator().getLogin(), fetched.getCreator()
				.getLogin());

		fetched = service.getMilestone(client.getUser(), writableRepo,
				Integer.toString(created.getNumber()));
		assertNotNull(fetched);
		assertEquals(created.getClosedIssues(), fetched.getClosedIssues());
		assertEquals(created.getDescription(), fetched.getDescription());
		assertEquals(created.getNumber(), fetched.getNumber());
		assertEquals(created.getOpenIssues(), fetched.getOpenIssues());
		assertEquals(created.getState(), fetched.getState());
		assertEquals(created.getTitle(), fetched.getTitle());
		assertEquals(created.getUrl(), fetched.getUrl());
		assertEquals(created.getCreatedAt(), fetched.getCreatedAt());
		assertEquals(created.getDueOn(), fetched.getDueOn());
		assertEquals(created.getCreator().getLogin(), fetched.getCreator()
				.getLogin());

		service.deleteMilestone(client.getUser(), writableRepo,
				Integer.toString(created.getNumber()));
		try {
			service.getMilestone(client.getUser(), writableRepo,
					Integer.toString(created.getNumber()));
			fail("Fetch did not throw exception");
		} catch (RequestException e) {
			assertEquals(HTTP_NOT_FOUND, e.getStatus());
		}
	}
}
