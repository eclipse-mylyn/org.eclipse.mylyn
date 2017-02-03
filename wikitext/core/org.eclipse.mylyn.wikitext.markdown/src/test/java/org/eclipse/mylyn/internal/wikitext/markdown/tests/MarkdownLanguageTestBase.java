/*******************************************************************************
 * Copyright (c) 2012, 2014 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.tests;

import java.io.StringWriter;
import java.util.List;

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.markdown.MarkdownLanguage;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.toolkit.RecordingDocumentBuilder;
import org.eclipse.mylyn.wikitext.toolkit.RecordingDocumentBuilder.Event;

import junit.framework.TestCase;

/**
 * Test base for markdown language tests.
 *
 * @author Stefan Seelmann
 */
public abstract class MarkdownLanguageTestBase extends TestCase {

	private MarkupParser parser;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		parser = new MarkupParser(new MarkdownLanguage());
	}

	public String parseToHtml(String markup) {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.setEmitAsDocument(false);
		parser.setBuilder(builder);
		parser.parse(markup);
		return out.toString();
	}

	public void parseAndAssert(String markup, String expectedHtml) {
		String html = parseToHtml(markup);

		assertEquals(expectedHtml, html);
	}

	protected List<Event> parseToEvents(String markupContent) {
		RecordingDocumentBuilder builder = new RecordingDocumentBuilder();
		parser.setBuilder(builder);
		parser.parse(markupContent);
		return builder.getEvents();
	}

	protected Event findEvent(List<Event> events, SpanType type) {
		for (Event event : events) {
			if (event.spanType == type) {
				return event;
			}
		}
		throw new AssertionError(String.format("Expected span %s but found %s", type, events));
	}

}
