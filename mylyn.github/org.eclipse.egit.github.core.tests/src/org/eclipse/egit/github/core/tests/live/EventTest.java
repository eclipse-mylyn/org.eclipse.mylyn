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
package org.eclipse.egit.github.core.tests.live;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.event.Event;
import org.eclipse.egit.github.core.service.EventService;
import org.junit.Test;

/**
 * Unit tests of {@link EventService}
 */
public class EventTest extends LiveTest {

	/**
	 * Test paging through public gists
	 */
	@Test
	public void twoPublicEventPages() {
		EventService service = new EventService(client);
		PageIterator<Event> pages = service.pagePublicEvents(10);
		assertNotNull(pages);
		assertTrue(pages.hasNext());
		Collection<Event> events = pages.next();
		assertNotNull(events);
		assertTrue(events.size() > 0);
		for (Event event : events) {
			assertNotNull(event);
			assertNotNull(event.getCreatedAt());
			assertNotNull(event.getPayload());
		}
		assertTrue(pages.hasNext());
		events = pages.next();
		assertNotNull(events);
		assertTrue(events.size() > 0);
		for (Event event : events) {
			assertNotNull(event);
			assertNotNull(event.getCreatedAt());
			assertNotNull(event.getPayload());
		}
	}

	/**
	 * Test paging current user's events
	 *
	 * @throws Exception
	 */
	@Test
	public void pageCurrentUsersEvents() throws Exception {
		checkUser();
		EventService service = new EventService(client);
		Collection<Event> events = service.pageUserEvents(client.getUser(), false, 1).next();
		assertNotNull(events);
		assertTrue(events.size() > 0);
		assertNotNull(events.toArray()[0]);
	}

	/**
	 * Test paging current user's public events
	 */
	@Test
	public void pageCurrentUsersPublicEvents() {
		checkUser();
		EventService service = new EventService(client);
		Collection<Event> events = service.pageUserEvents(client.getUser(), true, 1).next();
		assertNotNull(events);
		assertTrue(events.size() > 0);
		for (Event event : events) {
			assertNotNull(event);
			assertNotNull(event.getCreatedAt());
			assertNotNull(event.getPayload());
		}
	}

	/**
	 * Test paging current user's received events
	 */
	@Test
	public void pageCurrentUsersReceivedEvents() {
		checkUser();
		EventService service = new EventService(client);
		Collection<Event> events = service.pageUserReceivedEvents(client.getUser(),
				false, 1).next();
		assertNotNull(events);
		assertTrue(events.size() > 0);
		assertNotNull(events.toArray()[0]);
	}

	/**
	 * Test paging current user's public received events
	 */
	@Test
	public void pageCurrentUsersPublicReceivedEvents() {
		checkUser();
		EventService service = new EventService(client);
		Collection<Event> events = service.pageUserReceivedEvents(client.getUser(),
				true, 1).next();
		assertNotNull(events);
		assertTrue(events.size() > 0);
		for (Event event : events) {
			assertNotNull(event);
			assertNotNull(event.getCreatedAt());
			assertNotNull(event.getPayload());
		}
	}
}
