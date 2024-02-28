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

package org.eclipse.mylyn.wikitext.parser.builder.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.HeadingAttributes;
import org.eclipse.mylyn.wikitext.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.parser.builder.EventDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.event.AcronymEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.BeginBlockEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.BeginDocumentEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.BeginHeadingEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.BeginSpanEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.CharactersEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.CharactersUnescapedEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.DocumentBuilderEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.DocumentBuilderEvents;
import org.eclipse.mylyn.wikitext.parser.builder.event.EndBlockEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.EndDocumentEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.EndHeadingEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.EndSpanEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.EntityReferenceEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.HorizontalRuleEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.ImageEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.ImageLinkEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.LineBreakEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.LinkEvent;
import org.junit.Test;

@SuppressWarnings("nls")
public class EventDocumentBuilderTest {

	private final EventDocumentBuilder builder = new EventDocumentBuilder();

	@Test
	public void create() {
		DocumentBuilderEvents events = builder.getDocumentBuilderEvents();
		assertNotNull(events);
		assertTrue(events.getEvents().isEmpty());
	}

	@Test
	public void beginDocument() {
		builder.beginDocument();
		assertEvents(new BeginDocumentEvent());
	}

	@Test
	public void endDocument() {
		builder.endDocument();
		assertEvents(new EndDocumentEvent());
	}

	@Test
	public void beginBlock() {
		builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
		assertEvents(new BeginBlockEvent(BlockType.PREFORMATTED, new Attributes()));
	}

	@Test
	public void endBlock() {
		builder.endBlock();
		assertEvents(new EndBlockEvent());
	}

	@Test
	public void beginSpan() {
		builder.beginSpan(SpanType.DELETED, new Attributes());
		assertEvents(new BeginSpanEvent(SpanType.DELETED, new Attributes()));
	}

	@Test
	public void endSpan() {
		builder.endSpan();
		assertEvents(new EndSpanEvent());
	}

	@Test
	public void beginHeading() {
		builder.beginHeading(3, new HeadingAttributes());
		assertEvents(new BeginHeadingEvent(3, new HeadingAttributes()));
	}

	@Test
	public void endHeading() {
		builder.endHeading();
		assertEvents(new EndHeadingEvent());
	}

	@Test
	public void characters() {
		builder.characters("test 123");
		assertEvents(new CharactersEvent("test 123"));
	}

	@Test
	public void charactersUnescaped() {
		builder.charactersUnescaped("test 123");
		assertEvents(new CharactersUnescapedEvent("test 123"));
	}

	@Test
	public void acronym() {
		builder.acronym("one", "two");
		assertEvents(new AcronymEvent("one", "two"));
	}

	@Test
	public void entityReference() {
		builder.entityReference("amp");
		assertEvents(new EntityReferenceEvent("amp"));
	}

	@Test
	public void image() {
		builder.image(new ImageAttributes(), "http://example.com/foo.png");
		assertEvents(new ImageEvent(new ImageAttributes(), "http://example.com/foo.png"));
	}

	@Test
	public void imageLink() {
		builder.imageLink(new LinkAttributes(), new ImageAttributes(), "https://example.com",
				"http://example.com/foo.png");
		assertEvents(new ImageLinkEvent(new LinkAttributes(), new ImageAttributes(), "https://example.com",
				"http://example.com/foo.png"));
	}

	@Test
	public void link() {
		builder.link(new LinkAttributes(), "https://example.com", "test");
		assertEvents(new LinkEvent(new LinkAttributes(), "https://example.com", "test"));
	}

	@Test
	public void lineBreak() {
		builder.lineBreak();
		assertEvents(new LineBreakEvent());
	}

	@Test
	public void horizontalRule() {
		builder.horizontalRule();
		assertEvents(new HorizontalRuleEvent());
	}

	private void assertEvents(DocumentBuilderEvent... events) {
		List<DocumentBuilderEvent> expectedEvents = Arrays.asList(events);

		assertEquals(expectedEvents, builder.getDocumentBuilderEvents().getEvents());

		EventDocumentBuilder builder2 = new EventDocumentBuilder();
		for (DocumentBuilderEvent event : events) {
			event.invoke(builder2);
		}
		assertEquals(expectedEvents, builder2.getDocumentBuilderEvents().getEvents());
	}
}
