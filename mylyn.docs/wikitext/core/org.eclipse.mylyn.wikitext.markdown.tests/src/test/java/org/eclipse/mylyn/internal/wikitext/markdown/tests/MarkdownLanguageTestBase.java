/*******************************************************************************
 * Copyright (c) 2012, 2024 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.tests;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.util.List;

import org.eclipse.mylyn.wikitext.markdown.MarkdownLanguage;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.toolkit.RecordingDocumentBuilder;
import org.eclipse.mylyn.wikitext.toolkit.RecordingDocumentBuilder.Event;
import org.junit.Before;

/**
 * Test base for markdown language tests.
 *
 * @author Stefan Seelmann
 */
@SuppressWarnings({ "nls", "restriction" })
public abstract class MarkdownLanguageTestBase {

	private MarkupParser parser;

	@Before
	public void setUp() {
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
