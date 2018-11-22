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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.event.Event;
import org.eclipse.egit.github.core.service.EventService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests of {@link EventService}
 */
@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest {

	@Mock
	private GitHubClient gitHubClient;

	@Mock
	private GitHubResponse response;

	private EventService eventService;

	/**
	 * Test case set up
	 *
	 * @throws IOException
	 */
	@Before
	public void before() throws IOException {
		eventService = new EventService(gitHubClient);
	}

	/**
	 * Create service with null client
	 */
	@Test(expected = IllegalArgumentException.class)
	public void constructorNullArgument() {
		new EventService(null);
	}

	/**
	 * Create default service
	 */
	@Test
	public void defaultConstructor() {
		assertNotNull(new EventService().getClient());
	}

	/**
	 * Page public events
	 *
	 * @throws IOException
	 */
	@Test
	public void pagePublicEvents() throws IOException {
		PageIterator<Event> iterator = eventService.pagePublicEvents();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(Utils.page("/events"), iterator
				.getRequest().generateUri());
	}

	/**
	 * Page events for repository
	 *
	 * @throws IOException
	 */
	@Test
	public void pageRepsitoryEvents() throws IOException {
		RepositoryId repo = new RepositoryId("user", "repo");
		PageIterator<Event> iterator = eventService.pageEvents(repo);
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(Utils.page("/repos/user/repo/events"), iterator
				.getRequest().generateUri());
	}

	/**
	 * Page events for network of repositories
	 *
	 * @throws IOException
	 */
	@Test
	public void pageNetworkEvents() throws IOException {
		RepositoryId repo = new RepositoryId("user", "repo");
		PageIterator<Event> iterator = eventService.pageNetworkEvents(repo);
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(Utils.page("/networks/user/repo/events"), iterator
				.getRequest().generateUri());
	}

	/**
	 * Page events for org
	 *
	 * @throws IOException
	 */
	@Test
	public void pageOrgEvents() throws IOException {
		PageIterator<Event> iterator = eventService.pageOrgEvents("org");
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(Utils.page("/orgs/org/events"), iterator
				.getRequest().generateUri());
	}

	/**
	 * Page received events for user
	 *
	 * @throws IOException
	 */
	@Test
	public void pageUserReceivedEvents() throws IOException {
		PageIterator<Event> iterator = eventService.pageUserReceivedEvents("user");
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(Utils.page("/users/user/received_events"), iterator
				.getRequest().generateUri());
	}

	/**
	 * Page public received events for user
	 *
	 * @throws IOException
	 */
	@Test
	public void pagePublicUserReceivedEvents() throws IOException {
		PageIterator<Event> iterator = eventService.pageUserReceivedEvents("user", true);
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(Utils.page("/users/user/received_events/public"), iterator
				.getRequest().generateUri());
	}

	/**
	 * Page events for user
	 *
	 * @throws IOException
	 */
	@Test
	public void pageUserEvents() throws IOException {
		PageIterator<Event> iterator = eventService.pageUserEvents("user");
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(Utils.page("/users/user/events"), iterator
				.getRequest().generateUri());
	}

	/**
	 * Page public events for user
	 *
	 * @throws IOException
	 */
	@Test
	public void pagePublicUserEvents() throws IOException {
		PageIterator<Event> iterator = eventService.pageUserEvents("user", true);
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(Utils.page("/users/user/events/public"), iterator
				.getRequest().generateUri());
	}

	/**
	 * Page org events for user
	 *
	 * @throws IOException
	 */
	@Test
	public void pageUserOrgEvents() throws IOException {
		PageIterator<Event> iterator = eventService.pageUserOrgEvents("user", "org");
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(Utils.page("/users/user/events/orgs/org"), iterator
				.getRequest().generateUri());
	}
}
