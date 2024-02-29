/*******************************************************************************
 * Copyright (c) 2013, 2024 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser.builder.event.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.mylyn.wikitext.parser.builder.event.BeginDocumentEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.DocumentBuilderEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.DocumentBuilderEvents;
import org.junit.Test;

@SuppressWarnings({ "nls", "restriction" })
public class DocumentBuilderEventsTest {

	@Test
	public void createNull() {
		NullPointerException e = assertThrows(NullPointerException.class, () -> new DocumentBuilderEvents(null));
		assertTrue(e.getMessage().contains("Must provide events"));
	}

	@Test
	public void createsWithProvidedEvents() {
		BeginDocumentEvent event = new BeginDocumentEvent();
		List<DocumentBuilderEvent> allEvents = new ArrayList<>(Arrays.asList(event));
		DocumentBuilderEvents events = new DocumentBuilderEvents(allEvents);
		assertEquals(allEvents, events.getEvents());
	}

	@Test
	public void getEventsImmutable() {
		DocumentBuilderEvents events = new DocumentBuilderEvents(new ArrayList<>());
		assertThrows(UnsupportedOperationException.class, () -> events.getEvents().clear());
	}

	@Test
	public void testToString() {
		BeginDocumentEvent event = new BeginDocumentEvent();
		DocumentBuilderEvents events = new DocumentBuilderEvents(new ArrayList<>(Arrays.asList(event)));
		assertNotNull(events.toString());
		assertTrue(events.toString().contains(event.toString()));
	}
}
