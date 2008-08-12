/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.parser.builder;

import java.util.ArrayList;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.util.LocatorImpl;

/**
 * 
 * 
 * @author David Green
 */
public class RecordingDocumentBuilder extends DocumentBuilder {

	public static class Event {
		public Attributes attributes;

		public BlockType blockType;

		public String text;

		public Integer headingLevel;

		public SpanType spanType;

		public LocatorImpl locator;

		Event(String text) {
			this.text = text;
		}

		public Event(BlockType type, Attributes attributes2) {
			blockType = type;
			attributes = attributes2;
		}

		public Event(int level, Attributes attributes2) {
			headingLevel = level;
			attributes = attributes2;
		}

		public Event(SpanType type, Attributes attributes2) {
			this.spanType = type;
			attributes = attributes2;
		}

		@Override
		public String toString() {
			StringBuilder buf = new StringBuilder();
			if (blockType != null) {
				buf.append("Block(");
				buf.append(blockType);
			} else if (spanType != null) {
				buf.append("Span(");
				buf.append(spanType);
			} else if (headingLevel != null) {
				buf.append("Heading(");
				buf.append(headingLevel);
			} else {
				buf.append("Text?(");
			}
			if (text != null) {
				buf.append(text.length() > 6 ? text.substring(0, 6) + "..." : text);
			}

			buf.append(",lineNumber=");
			buf.append(locator.getLineNumber());
			buf.append(",lineCharacterOffset=");
			buf.append(locator.getLineCharacterOffset());
			buf.append(",lineSegmentEnd=");
			buf.append(locator.getLineSegmentEndOffset());
			buf.append(",lineLength=");
			buf.append(locator.getLineLength());
			buf.append(",lineDocumentOffset=");
			buf.append(locator.getLineDocumentOffset());
			buf.append(",documentOffset=");
			buf.append(locator.getDocumentOffset());
			buf.append(")");
			return buf.toString();
		}
	}

	private final java.util.List<Event> events = new ArrayList<Event>();

	private void add(Event event) {
		event.locator = new LocatorImpl(getLocator());
		events.add(event);
	}

	@Override
	public void acronym(String text, String definition) {
		add(new Event(text));
	}

	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		add(new Event(type, attributes));
	}

	@Override
	public void beginDocument() {
	}

	@Override
	public void beginHeading(int level, Attributes attributes) {
		add(new Event(level, attributes));
	}

	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
		add(new Event(type, attributes));
	}

	@Override
	public void characters(String text) {
		add(new Event(text));
	}

	@Override
	public void charactersUnescaped(String literal) {
		add(new Event(literal));
	}

	@Override
	public void endBlock() {

	}

	@Override
	public void endDocument() {
	}

	@Override
	public void endHeading() {
	}

	@Override
	public void endSpan() {
	}

	@Override
	public void entityReference(String entity) {
	}

	@Override
	public void image(Attributes attributes, String url) {
	}

	@Override
	public void imageLink(Attributes linkAttributes, Attributes attributes, String href, String imageUrl) {
	}

	@Override
	public void lineBreak() {
	}

	@Override
	public void link(Attributes attributes, String hrefOrHashName, String text) {
	}

	public java.util.List<Event> getEvents() {
		return events;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(getClass().getSimpleName());
		buf.append("(");
		for (Event event : events) {
			buf.append("\n\t");
			buf.append(event);
		}
		buf.append("\n)");
		return buf.toString();
	}
}
