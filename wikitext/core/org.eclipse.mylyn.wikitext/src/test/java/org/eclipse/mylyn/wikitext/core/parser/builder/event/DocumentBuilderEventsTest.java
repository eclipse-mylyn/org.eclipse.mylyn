/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser.builder.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.Lists;

public class DocumentBuilderEventsTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void createNull() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Must provide events");
		new DocumentBuilderEvents(null);
	}

	@Test
	public void createsWithProvidedEvents() {
		BeginDocumentEvent event = new BeginDocumentEvent();
		List<DocumentBuilderEvent> allEvents = Lists.<DocumentBuilderEvent> newArrayList(event);
		DocumentBuilderEvents events = new DocumentBuilderEvents(allEvents);
		assertEquals(allEvents, events.getEvents());
	}

	@Test
	public void getEventsImmutable() {
		DocumentBuilderEvents events = new DocumentBuilderEvents(Lists.<DocumentBuilderEvent> newArrayList());
		thrown.expect(UnsupportedOperationException.class);
		events.getEvents().clear();
	}

	@Test
	public void testToString() {
		BeginDocumentEvent event = new BeginDocumentEvent();
		DocumentBuilderEvents events = new DocumentBuilderEvents(Lists.<DocumentBuilderEvent> newArrayList(event));
		assertNotNull(events.toString());
		assertTrue(events.toString().contains(event.toString()));
	}
}
