/*******************************************************************************
 * Copyright (c) 2014, 2015 Benjamin Muskalla and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Benjamin Muskalla - initial API and implementation
 *     David Green - bug 457648
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.HeadingAttributes;
import org.eclipse.mylyn.wikitext.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.parser.Locator;
import org.eclipse.mylyn.wikitext.parser.builder.event.AcronymEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.BeginBlockEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.BeginDocumentEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.BeginHeadingEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.BeginSpanEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.CharactersEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.CharactersUnescapedEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.DocumentBuilderEvent;
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
import org.eclipse.mylyn.wikitext.parser.markup.ContentState;
import org.junit.Test;

public class MultiplexingDocumentBuilderTest {

	private final EventDocumentBuilder delegate1 = new EventDocumentBuilder();

	private final EventDocumentBuilder delegate2 = new EventDocumentBuilder();

	private MultiplexingDocumentBuilder multiplexer = new MultiplexingDocumentBuilder(delegate1, delegate2);

	@Test
	public void beginDocument() {
		multiplexer.beginDocument();
		assertEvents(new BeginDocumentEvent());
	}

	@Test
	public void endDocument() {
		multiplexer.endDocument();
		assertEvents(new EndDocumentEvent());
	}

	@Test
	public void beginBlock() {
		multiplexer.beginBlock(BlockType.PREFORMATTED, new Attributes());
		assertEvents(new BeginBlockEvent(BlockType.PREFORMATTED, new Attributes()));
	}

	@Test
	public void endBlock() {
		multiplexer.endBlock();
		assertEvents(new EndBlockEvent());
	}

	@Test
	public void beginSpan() {
		multiplexer.beginSpan(SpanType.DELETED, new Attributes());
		assertEvents(new BeginSpanEvent(SpanType.DELETED, new Attributes()));
	}

	@Test
	public void endSpan() {
		multiplexer.endSpan();
		assertEvents(new EndSpanEvent());
	}

	@Test
	public void beginHeading() {
		multiplexer.beginHeading(3, new HeadingAttributes());
		assertEvents(new BeginHeadingEvent(3, new HeadingAttributes()));
	}

	@Test
	public void endHeading() {
		multiplexer.endHeading();
		assertEvents(new EndHeadingEvent());
	}

	@Test
	public void characters() {
		multiplexer.characters("test 123");
		assertEvents(new CharactersEvent("test 123"));
	}

	@Test
	public void charactersUnescaped() {
		multiplexer.charactersUnescaped("test 123");
		assertEvents(new CharactersUnescapedEvent("test 123"));
	}

	@Test
	public void horizontalRule() {
		multiplexer.horizontalRule();
		assertEvents(new HorizontalRuleEvent());
	}

	@Test
	public void acronym() {
		multiplexer.acronym("one", "two");
		assertEvents(new AcronymEvent("one", "two"));
	}

	@Test
	public void entityReference() {
		multiplexer.entityReference("amp");
		assertEvents(new EntityReferenceEvent("amp"));
	}

	@Test
	public void image() {
		multiplexer.image(new ImageAttributes(), "http://example.com/foo.png");
		assertEvents(new ImageEvent(new ImageAttributes(), "http://example.com/foo.png"));
	}

	@Test
	public void imageLink() {
		multiplexer.imageLink(new LinkAttributes(), new ImageAttributes(), "https://example.com",
				"http://example.com/foo.png");
		assertEvents(new ImageLinkEvent(new LinkAttributes(), new ImageAttributes(), "https://example.com",
				"http://example.com/foo.png"));
	}

	@Test
	public void link() {
		multiplexer.link(new LinkAttributes(), "https://example.com", "test");
		assertEvents(new LinkEvent(new LinkAttributes(), "https://example.com", "test"));
	}

	@Test
	public void lineBreak() {
		multiplexer.lineBreak();
		assertEvents(new LineBreakEvent());
	}

	@Test
	public void setLocator() {
		DocumentBuilder delegateOne = new NoOpDocumentBuilder();
		DocumentBuilder delegateTwo = new NoOpDocumentBuilder();
		Locator locator = new ContentState();

		multiplexer = new MultiplexingDocumentBuilder(delegateOne, delegateTwo);
		multiplexer.setLocator(locator);
		assertSame(locator, delegateOne.getLocator());
		assertSame(locator, delegateTwo.getLocator());
	}

	@Test
	public void newDelegateAfterInstatiation() {
		multiplexer.lineBreak();
		assertEvents(new LineBreakEvent());

		EventDocumentBuilder delegate3 = new EventDocumentBuilder();

		multiplexer.addDocumentBuilder(delegate3);
		multiplexer.acronym("a", "b");

		assertEvents(new LineBreakEvent(), new AcronymEvent("a", "b"));
		assertEquals(Arrays.asList(new AcronymEvent("a", "b")), delegate3.getDocumentBuilderEvents().getEvents());
	}

	@Test
	public void flush() {
		final AtomicBoolean flushed = new AtomicBoolean();
		DocumentBuilder delegate = new NoOpDocumentBuilder() {
			@Override
			public void flush() {
				flushed.set(true);
			}
		};
		multiplexer = new MultiplexingDocumentBuilder(delegate);
		multiplexer.flush();
		assertTrue(flushed.get());
	}

	private void assertEvents(DocumentBuilderEvent... events) {
		List<DocumentBuilderEvent> expectedEvents = Arrays.asList(events);

		assertEquals(expectedEvents, delegate1.getDocumentBuilderEvents().getEvents());
		assertEquals(expectedEvents, delegate2.getDocumentBuilderEvents().getEvents());
	}
}
