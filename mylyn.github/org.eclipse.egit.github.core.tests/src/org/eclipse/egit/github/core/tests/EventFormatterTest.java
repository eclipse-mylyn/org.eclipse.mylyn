/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Jason Tsay (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.egit.github.core.client.EventFormatter;
import org.eclipse.egit.github.core.event.EventPayload;
import org.junit.Test;

/**
 * Unit tests of {@link EventFormatter} and subclasses
 */
public class EventFormatterTest {

	/**
	 * Create instance of EventFormatter
	 */
	@Test
	public void createEventFormatterInstance() {
		EventFormatter formatter = new EventFormatter();
		assertNotNull(formatter.getEventCreator());
		assertNotNull(formatter.getPayloadDeserializer());
	}

	/**
	 * Create instance of Event
	 */
	@Test
	public void createEventInstance() {
		EventFormatter formatter = new EventFormatter();
		assertNotNull(formatter.getEventCreator().createInstance(null));
	}

	/**
	 * Unknown event payload returned as EventPayload
	 */
	@Test
	public void unknownPayload() {
		EventFormatter formatter = new EventFormatter();
		formatter.getEventCreator().createInstance(null);
		EventPayload payload = formatter.getPayloadDeserializer().deserialize(null, null, null);
		assertTrue(payload instanceof EventPayload);
	}
}
