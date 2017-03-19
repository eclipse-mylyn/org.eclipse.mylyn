/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.confluence;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.io.Writer;

import org.eclipse.mylyn.wikitext.html.HtmlLanguage;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.junit.Test;

public class ConfluenceLanguageIntegrationTest {

	@Test
	public void htmlToConfluence() {
		assertHtmlToConfluence(true);
		assertHtmlToConfluence(false);
	}

	@Test
	// test for bug# 513661
	public void listsInTables() {
		String textile = "|* item 1\n* item 2|# item 3\n# item 4|";
		assertRoundTrip(textile, textile);
	}

	private void assertHtmlToConfluence(boolean parseAsDocument) {
		HtmlLanguage htmlLanguage = HtmlLanguage.builder()
				.add(BlockType.PARAGRAPH)
				.add(SpanType.BOLD)
				.name("Test")
				.create();
		MarkupParser parser = new MarkupParser(htmlLanguage);
		Writer confluenceOut = new StringWriter();
		DocumentBuilder confuenceBuilder = new ConfluenceLanguage().createDocumentBuilder(confluenceOut);
		parser.setBuilder(confuenceBuilder);
		parser.parse("<html><body>some text <b>bold here</b> more text</body></html>", parseAsDocument);

		assertEquals("some text *bold here* more text\n\n", confluenceOut.toString());
	}

	private void assertRoundTrip(String textileIn, String textileOut) {
		Writer confluenceOut = new StringWriter();
		ConfluenceLanguage confluenceLanguage = new ConfluenceLanguage();

		MarkupParser parser = new MarkupParser(confluenceLanguage);
		parser.setBuilder(confluenceLanguage.createDocumentBuilder(confluenceOut));
		parser.parse(textileIn, false);

		assertEquals(textileOut, confluenceOut.toString().trim());
	}
}
