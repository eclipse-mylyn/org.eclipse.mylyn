/*******************************************************************************
 * Copyright (c) 2015, 2016 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Max Rydahl Andersen - copied from markdown to get base for asciidoc, Bug 474084
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.util.List;

import org.eclipse.mylyn.wikitext.asciidoc.AsciiDocLanguage;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguageConfiguration;
import org.eclipse.mylyn.wikitext.toolkit.RecordingDocumentBuilder;
import org.eclipse.mylyn.wikitext.toolkit.RecordingDocumentBuilder.Event;
import org.junit.Before;

/**
 * Test base for asciidoc language tests. provides base set-up functionalities, like parsing markup to html
 *
 * @author Max Rydahl Andersen
 */
public abstract class AsciiDocLanguageTestBase {

	private MarkupParser parser;

	@Before
	public void setUp() throws Exception {
		parser = createParserWithConfiguration(null);
	}

	public String parseToHtml(String markup) {
		return parseAsciiDocToHtml(markup, parser);
	}

	public List<Event> parseToEvents(String markup) {
		RecordingDocumentBuilder builder = new RecordingDocumentBuilder();
		parser.setBuilder(builder);
		parser.parse(markup);
		return builder.getEvents();
	}

	protected static MarkupParser createParserWithConfiguration(MarkupLanguageConfiguration configuration) {
		MarkupLanguage markupLanguage = new AsciiDocLanguage();
		if (configuration != null) {
			markupLanguage.configure(configuration);
		}
		return new MarkupParser(markupLanguage);
	}

	protected static String parseAsciiDocToHtml(String markup, MarkupParser parser) {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.setEmitAsDocument(false);
		parser.setBuilder(builder);
		parser.parse(markup);
		return out.toString();
	}

	protected static void assertBlockRange(Event event, BlockType type, int start, int end) {
		assertEquals(type, event.blockType);
		assertEquals(start, event.locator.getLineCharacterOffset());
		assertEquals(end, event.locator.getLineSegmentEndOffset());
	}

	protected static void assertBlockRange(Event event, String text, int start, int end) {
		assertEquals(text, event.text);
		assertEquals(start, event.locator.getLineCharacterOffset());
		assertEquals(end, event.locator.getLineSegmentEndOffset());
	}

}
