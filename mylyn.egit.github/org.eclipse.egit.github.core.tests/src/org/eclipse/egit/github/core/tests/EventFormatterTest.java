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

import static org.eclipse.egit.github.core.event.Event.TYPE_FOLLOW;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.egit.github.core.client.EventFormatter;
import org.eclipse.egit.github.core.client.GsonUtils;
import org.eclipse.egit.github.core.event.Event;
import org.eclipse.egit.github.core.event.EventPayload;
import org.eclipse.egit.github.core.event.FollowPayload;
import org.junit.Test;

/**
 * Unit tests of {@link EventFormatter}
 */
public class EventFormatterTest {

	/**
	 * Follow event payload returned as {@link FollowPayload}
	 */
	@Test
	public void followPayload() {
		Event event = GsonUtils.fromJson("{\"type\":\"" + TYPE_FOLLOW
				+ "\",\"payload\":{}}", Event.class);
		assertNotNull(event);
		assertNotNull(event.getPayload());
		assertEquals(FollowPayload.class, event.getPayload().getClass());
	}

	/**
	 * Unknown event payload returned as {@link EventPayload}
	 */
	@Test
	public void unknownPayload() {
		Event event = GsonUtils.fromJson(
				"{\"type\":\"NotAnEventType\",\"payload\":{}}", Event.class);
		assertNotNull(event);
		assertNotNull(event.getPayload());
		assertEquals(EventPayload.class, event.getPayload().getClass());
	}

	/**
	 * Event with missing type has payload returned as {@link EventPayload}
	 */
	@Test
	public void missingType() {
		Event event = GsonUtils.fromJson("{\"payload\":{}}", Event.class);
		assertNotNull(event);
		assertNotNull(event.getPayload());
		assertEquals(EventPayload.class, event.getPayload().getClass());
	}

	/**
	 * Missing payload
	 */
	@Test
	public void missingPayload() {
		Event event = GsonUtils.fromJson("{}", Event.class);
		assertNotNull(event);
		assertNull(event.getPayload());
	}
}
