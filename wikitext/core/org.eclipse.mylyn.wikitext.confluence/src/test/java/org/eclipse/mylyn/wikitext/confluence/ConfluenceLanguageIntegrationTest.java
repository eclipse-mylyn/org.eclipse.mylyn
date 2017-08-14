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
import org.eclipse.mylyn.wikitext.parser.builder.EventDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.event.CharactersEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.DocumentBuilderEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.DocumentBuilderEvents;
import org.junit.Test;

public class ConfluenceLanguageIntegrationTest {

	@Test
	public void htmlToConfluence() {
		assertHtmlToConfluence(true);
		assertHtmlToConfluence(false);
	}

	@Test
	public void htmlColorToConfluence() {
		assertHtmlToConfluenceColor("rgb(255, 0, 0);", "#ff0000");
		assertHtmlToConfluenceColor("rgb(255, 0, 0)", "#ff0000");
		assertHtmlToConfluenceColor("rgb(255,    0,    0);", "#ff0000");
		assertHtmlToConfluenceColor("rgb(255,0,0)", "#ff0000");
		assertHtmlToConfluenceColor("#ff0000", "#ff0000");
	}

	@Test
	public void listsInTables() {
		assertRoundTripExact("|* item 1\n* item 2|# item 3\n# item 4|\n\n");
	}

	@Test
	public void tableWithMultiLineCellContent() {
		String table = "|line one\\\\line two\\\\line three|\n\n";
		assertRoundTripExact(table);
		assertRoundTrip("|line one\nline two\nline three|", table);
	}

	@Test
	public void preformattedWithCurlyBraces() {
		assertRoundTrip("{noformat}{something}{noformat}\n\n", "{noformat}{something}\n{noformat}\n\n");
	}

	@Test
	public void codeBlockWithCurlyBraces() {
		assertRoundTrip("{code}{something}{code}\n\n", "{code}{something}\n{code}\n\n");
	}

	@Test
	public void builderParserSymmetricalWithProblemCharacters() {
		String characterContent = "\"`&amp;{}!@$%^&*()_-+=[]\\|;:',.<>/?~/+&#160;ˇ";

		String markup = toMarkup(characterContent);
		String text = toText(parseToEvents(markup));

		assertEquals(characterContent, text);
	}

	private String toText(DocumentBuilderEvents events) {
		String text = "";
		for (DocumentBuilderEvent event : events.getEvents()) {
			if (event instanceof CharactersEvent) {
				text += ((CharactersEvent) event).getText();
			}
		}
		return text;
	}

	private DocumentBuilderEvents parseToEvents(String markup) {
		EventDocumentBuilder eventBuilder = new EventDocumentBuilder();
		MarkupParser parser = new MarkupParser(new ConfluenceLanguage(), eventBuilder);
		parser.parse(markup);
		DocumentBuilderEvents events = eventBuilder.getDocumentBuilderEvents();
		return events;
	}

	private String toMarkup(String characterContent) {
		StringWriter writer = new StringWriter();
		DocumentBuilder builder = new ConfluenceLanguage().createDocumentBuilder(writer);
		builder.beginDocument();
		builder.characters(characterContent);
		builder.endDocument();
		return writer.toString().trim();
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

	private void assertHtmlToConfluenceColor(String color, String hex) {
		HtmlLanguage htmlLanguage = HtmlLanguage.builder()
				.add(BlockType.PARAGRAPH)
				.add(SpanType.SPAN)
				.name("Test")
				.create();
		MarkupParser parser = new MarkupParser(htmlLanguage);
		Writer confluenceOut = new StringWriter();
		DocumentBuilder confuenceBuilder = new ConfluenceLanguage().createDocumentBuilder(confluenceOut);
		parser.setBuilder(confuenceBuilder);
		parser.parse("<html><body><span style=\"color: " + color + "\"><del>this text here</del></span></body></html>",
				true);

		assertEquals("{color:" + hex + "}-this text here-{color}\n\n", confluenceOut.toString());
	}

	private void assertRoundTripExact(String textile) {
		assertRoundTrip(textile, textile);
	}

	private void assertRoundTrip(String textileIn, String textileOut) {
		Writer confluenceOut = new StringWriter();
		ConfluenceLanguage confluenceLanguage = new ConfluenceLanguage();

		MarkupParser parser = new MarkupParser(confluenceLanguage);
		parser.setBuilder(confluenceLanguage.createDocumentBuilder(confluenceOut));
		parser.parse(textileIn, false);

		assertEquals(textileOut, confluenceOut.toString());
	}
}
