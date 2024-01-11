/*******************************************************************************
 * Copyright (c) 2015, 2024 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.tests;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.util.List;

import org.eclipse.mylyn.wikitext.markdown.MarkdownLanguage;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.Block;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests without paragraph breaking blocks.
 * <p>
 * Paragraph breaking blocks are disabled by default in some dialects, e.g. Pandoc.
 */
public class MarkdownLanguageParagraphBreakingBlocksDisabledTest {

	private static class Language extends MarkdownLanguage {

		@Override
		protected void addBlockExtensions(List<Block> blocks, List<Block> paragraphBreakingBlocks) {
			super.addBlockExtensions(blocks, paragraphBreakingBlocks);

			paragraphBreakingBlocks.clear();
		}

	}

	private MarkupParser parser;

	@Before
	public void setUp() {
		parser = new MarkupParser(new Language());
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

	@Test
	public void testParagraphsBrokenByHorizontalRuleBlock() {
		String markup = "a paragraph\nfollowed by a horizontal rule\n---";
		String expectedHtml = "<p>a paragraph\nfollowed by a horizontal rule\n---</p>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testParagraphsBrokenByHeadingBlock() {
		String markup = "a paragraph\n# A header";
		String expectedHtml = "<p>a paragraph\n# A header</p>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testParagraphsBrokenByQuoteBlock() {
		String markup = "a paragraph\n> a quote block paragraph";
		String expectedHtml = "<p>a paragraph\n&gt; a quote block paragraph</p>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testParagraphsBrokenByUListBlock() {
		String markup = "a paragraph\n- a list item";
		String expectedHtml = "<p>a paragraph\n- a list item</p>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testParagraphsBrokenByOListBlock() {
		String markup = "a paragraph\n1. a list item";
		String expectedHtml = "<p>a paragraph\n1. a list item</p>";
		parseAndAssert(markup, expectedHtml);
	}

}
