/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
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

package org.eclipse.mylyn.wikitext.textile.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.textile.TextileLanguage;
import org.eclipse.mylyn.wikitext.textile.internal.TextileDocumentBuilder;
import org.eclipse.mylyn.wikitext.toolkit.RecordingDocumentBuilder;
import org.eclipse.mylyn.wikitext.toolkit.RecordingDocumentBuilder.Event;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("nls")
public class TextileLanguageDefinitionListTest {

	private MarkupParser parser;

	private TextileLanguage markupLanguage;

	@Before
	public void initParser() throws IOException {
		parser = new MarkupParser();
		markupLanguage = new TextileLanguage();
		parser.setMarkupLanguage(markupLanguage);
	}

	private String parseToHtml(String markup) {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.setEmitAsDocument(false);
		parser.setBuilder(builder);
		parser.parse(markup);
		return out.toString();
	}

	private String textileToTextileRoundTrip(String testContent) {
		StringWriter out = new StringWriter();
		TextileDocumentBuilder builder = new TextileDocumentBuilder(out);
		parser.setBuilder(builder);
		parser.parse(testContent);
		return out.toString();
	}

	@Test
	public void simple() {
		String html = parseToHtml("- one := two");
		assertEquals("<dl><dt>one</dt><dd>two</dd></dl>", html);
	}

	@Test
	public void withSpans() {
		String html = parseToHtml("- one := *two* and _three_ %{color:red;}laskdf as d%");
		assertEquals(
				"<dl><dt>one</dt><dd><strong>two</strong> and <em>three</em> <span style=\"color:red;\">laskdf as d</span></dd></dl>",
				html);
	}

	@Test
	public void multipleItems() {
		String html = parseToHtml("- one := two\n- three := four");
		assertEquals("<dl><dt>one</dt><dd>two</dd><dt>three</dt><dd>four</dd></dl>", html);
	}

	@Test
	public void terminatedSameLine() {
		String html = parseToHtml("- one := two three =:");
		assertEquals("<dl><dt>one</dt><dd>two three</dd></dl>", html);
	}

	@Test
	public void terminatedSameLineParaStartNoBlankLine() {
		String html = parseToHtml("- one := two three =:\npara start");
		assertEquals("<dl><dt>one</dt><dd>two three</dd></dl><p>para start</p>", html);
	}

	@Test
	public void terminatedMultiLineNoTermination() {
		String html = parseToHtml("- one := two three\n\tfour\nfive six");
		assertEquals("<dl><dt>one</dt><dd>two three<br/>\tfour<br/>five six</dd></dl>", html);
	}

	@Test
	public void terminatedMultiLineImproperTermination() {
		String html = parseToHtml("- one := two three\n\nfour");
		assertEquals("<dl><dt>one</dt><dd>two three</dd></dl><p>four</p>", html);
	}

	@Test
	public void terminatedMultiLine() {
		String html = parseToHtml("- one := two three\n\tfour\nfive six =:");
		assertEquals("<dl><dt>one</dt><dd>two three<br/>\tfour<br/>five six</dd></dl>", html);
	}

	@Test
	public void multiLineNotTerminatedWithBulletedList() {
		String html = parseToHtml("- one := two three\n* one =:");
		assertEquals("<dl><dt>one</dt><dd>two three<br/>* one</dd></dl>", html);
	}

	@Test
	public void terminatedMultiLineTerminatorLineEmpty() {
		String html = parseToHtml("- one := two three\n\tfour\n   =:\ntest");
		assertEquals("<dl><dt>one</dt><dd>two three<br/>\tfour</dd></dl><p>test</p>", html);
	}

	@Test
	public void terminatedMultiLineTerminatorLineEmpty2() {
		String html = parseToHtml("- one := two three\n\tfour\n=:\ntest");
		assertEquals("<dl><dt>one</dt><dd>two three<br/>\tfour</dd></dl><p>test</p>", html);
	}

	@Test
	public void semicolon() {
		String html = parseToHtml("; one");
		assertEquals("<dl><dt>one</dt></dl>", html);
	}

	@Test
	public void semicolonWithDefinition() {
		String html = parseToHtml("; one\n: two");
		assertEquals("<dl><dt>one</dt><dd>two</dd></dl>", html);
	}

	@Test
	public void semicolonWithMultipleDefinitions() {
		String html = parseToHtml("; one\n: two\n: three four");
		assertEquals("<dl><dt>one</dt><dd>two</dd><dd>three four</dd></dl>", html);
	}

	@Test
	public void semicolonWithMultipleItemsAndDefinitions() {
		String html = parseToHtml("; one\n: two\n: three four\n; five\n: six");
		assertEquals("<dl><dt>one</dt><dd>two</dd><dd>three four</dd><dt>five</dt><dd>six</dd></dl>", html);
	}

	@Test
	public void semicolonWithDefinitionAndSpans() {
		String html = parseToHtml("; one\n: *two* _three_ -four five-");
		assertEquals("<dl><dt>one</dt><dd><strong>two</strong> <em>three</em> <del>four five</del></dd></dl>", html);
	}

	@Test
	public void definitionListInterruptsParagraph() {
		String html = parseToHtml("one\n- two := three four");
		assertEquals("<p>one</p><dl><dt>two</dt><dd>three four</dd></dl>", html);
	}

	@Test
	public void definitionListToTextile() {
		String testContent = "; a";
		String result = textileToTextileRoundTrip(testContent);
		assertEquals("- a\n", result);
	}

	@Test
	public void definitionListToHtml() {
		String testContent = "; a";
		String html = parser.parseToHtml(testContent);
		assertTrue(html.contains("<body><dl><dt>a</dt></dl></body>"));
	}

	@Test
	public void paragraphContainingOnlyDefinitionListSyntaxToTextile() {
		String testContent = "p. ; a";
		String result = textileToTextileRoundTrip(testContent);
		assertEquals("- a\n", result);
	}

	@Test
	public void paragraphContainingOnlyDefinitionListSyntaxToHtml() {
		String testContent = "p. ; a";
		String html = parser.parseToHtml(testContent);
		assertTrue(html.contains("<body><p></p><dl><dt>a</dt></dl></body>"));
	}

	@Test
	public void paragraphContainingDefinitionListSyntaxToTextile() {
		String testContent = "p. abc ; a";
		String result = textileToTextileRoundTrip(testContent);
		assertEquals("abc ; a\n\n", result);
	}

	@Test
	public void offsets() {
		RecordingDocumentBuilder builder = new RecordingDocumentBuilder();
		MarkupParser markupParser = new MarkupParser(markupLanguage);
		markupParser.setBuilder(builder);
		markupParser.parse("- one := two");

		Event event = findEvent(builder.getEvents(), BlockType.DEFINITION_TERM);
		assertEquals(1, event.locator.getLineNumber());
		assertEquals(0, event.locator.getLineCharacterOffset());
		assertEquals(6, event.locator.getLineSegmentEndOffset());

		Event itemEvent = findEvent(builder.getEvents(), BlockType.DEFINITION_ITEM);
		assertEquals(1, itemEvent.locator.getLineNumber());
		assertEquals(6, itemEvent.locator.getLineCharacterOffset());
	}

	private Event findEvent(List<Event> events, BlockType blockType) {
		for (Event event : events) {
			if (event.blockType == blockType) {
				return event;
			}
		}
		fail(String.format("Expected block %s but found %s", blockType, events));
		throw new IllegalStateException();
	}
}
