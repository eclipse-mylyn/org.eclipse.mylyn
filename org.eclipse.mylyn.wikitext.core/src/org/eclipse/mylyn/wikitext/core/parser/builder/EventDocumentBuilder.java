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

package org.eclipse.mylyn.wikitext.core.parser.builder;

import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.AcronymEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.BeginBlockEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.BeginDocumentEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.BeginHeadingEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.BeginSpanEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.CharactersEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.CharactersUnescapedEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.DocumentBuilderEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.DocumentBuilderEvents;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.EndBlockEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.EndDocumentEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.EndHeadingEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.EndSpanEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.EntityReferenceEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.ImageEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.ImageLinkEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.LineBreakEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.LinkEvent;

import com.google.common.collect.Lists;

/**
 * A {@link DocumentBuilder} that produces {@link DocumentBuilderEvents} as its output.
 * 
 * @author david.green
 * @since 2.0
 */
public class EventDocumentBuilder extends DocumentBuilder {
	private final List<DocumentBuilderEvent> events = Lists.newArrayList();

	/**
	 * Provides the {@link DocumentBuilderEvents} that were created as a result of using this builder.
	 * 
	 * @return the events
	 */
	public DocumentBuilderEvents getDocumentBuilderEvents() {
		return new DocumentBuilderEvents(events);
	}

	@Override
	public void beginDocument() {
		events.add(new BeginDocumentEvent());
	}

	@Override
	public void endDocument() {
		events.add(new EndDocumentEvent());
	}

	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		events.add(new BeginBlockEvent(type, attributes));
	}

	@Override
	public void endBlock() {
		events.add(new EndBlockEvent());
	}

	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
		events.add(new BeginSpanEvent(type, attributes));
	}

	@Override
	public void endSpan() {
		events.add(new EndSpanEvent());
	}

	@Override
	public void beginHeading(int level, Attributes attributes) {
		events.add(new BeginHeadingEvent(level, attributes));
	}

	@Override
	public void endHeading() {
		events.add(new EndHeadingEvent());
	}

	@Override
	public void characters(String text) {
		events.add(new CharactersEvent(text));
	}

	@Override
	public void entityReference(String entity) {
		events.add(new EntityReferenceEvent(entity));
	}

	@Override
	public void image(Attributes attributes, String url) {
		events.add(new ImageEvent(attributes, url));
	}

	@Override
	public void link(Attributes attributes, String hrefOrHashName, String text) {
		events.add(new LinkEvent(attributes, hrefOrHashName, text));
	}

	@Override
	public void imageLink(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl) {
		events.add(new ImageLinkEvent(linkAttributes, imageAttributes, href, imageUrl));
	}

	@Override
	public void acronym(String text, String definition) {
		events.add(new AcronymEvent(text, definition));
	}

	@Override
	public void lineBreak() {
		events.add(new LineBreakEvent());
	}

	@Override
	public void charactersUnescaped(String literal) {
		events.add(new CharactersUnescapedEvent(literal));
	}

}
