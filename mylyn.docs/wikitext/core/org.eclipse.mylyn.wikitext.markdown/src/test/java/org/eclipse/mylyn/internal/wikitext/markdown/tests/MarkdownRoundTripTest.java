/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.tests;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.markdown.MarkdownLanguage;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.junit.Test;

public class MarkdownRoundTripTest {

	private final MarkdownLanguage language = new MarkdownLanguage();

	@Test
	public void roundTripCharacterEscaping() {
		assertCharactersRoundTrip("<p>abc</p>", "abc\n\n", "abc");
		assertCharactersRoundTrip("<p>a&amp;b</p>", "a&amp;b\n\n", "a&b");
		assertCharactersRoundTrip("<p>a&amp;amp;b</p>", "a&amp;amp;b\n\n", "a&amp;b");
		assertCharactersRoundTrip("<p>a&amp;#160;b</p>", "a&amp;#160;b\n\n", "a&#160;b");
	}

	private void assertCharactersRoundTrip(String expectedHtml, String expectedMarkdown, String characters) {
		String markdownContent = emitAsMarkdown(characters);
		assertEquals(expectedMarkdown, markdownContent);

		String html = parseMarkdownToHtml(markdownContent);
		assertEquals(expectedHtml, html);
	}

	private String parseMarkdownToHtml(String markupContent) {
		StringWriter htmlWriter = new StringWriter();
		HtmlDocumentBuilder htmlDocumentBuilder = new HtmlDocumentBuilder(htmlWriter);
		htmlDocumentBuilder.setEmitAsDocument(false);

		MarkupParser markupParser = new MarkupParser(language, htmlDocumentBuilder);
		markupParser.parse(markupContent);

		return htmlWriter.toString();
	}

	private String emitAsMarkdown(String characters) {
		StringWriter markupWriter = new StringWriter();
		DocumentBuilder documentBuilder = language.createDocumentBuilder(markupWriter);

		documentBuilder.characters(characters);
		documentBuilder.flush();
		return markupWriter.toString();
	}
}
