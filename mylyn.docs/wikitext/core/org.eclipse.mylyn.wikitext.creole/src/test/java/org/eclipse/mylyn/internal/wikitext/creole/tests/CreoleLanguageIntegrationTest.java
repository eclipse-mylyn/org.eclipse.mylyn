/*******************************************************************************
 * Copyright (c) 2018 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Kevin de Vlaming - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.creole.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.creole.CreoleLanguage;
import org.eclipse.mylyn.wikitext.creole.internal.CreoleDocumentBuilder;
import org.eclipse.mylyn.wikitext.html.HtmlLanguage;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.junit.Test;

/**
 * @see http://www.wikicreole.org/wiki/Elements
 * @author Kevin de Vlaming
 */
public class CreoleLanguageIntegrationTest {
	@Test
	public void testParagraph() {
		assertRoundTripExact("a paragraph\n\nanother paragraph\\\\with 2 lines\n\n");
	}

	@Test
	public void testSpansWithLineBreaks() {
		assertRoundTripExact("This is **strong**\\\\This is //italic//\\\\This is __underlined__\n\n");
	}

	@Test
	public void testHeadingsAndHorizontalRule() {
		assertRoundTripExact("= H1\n\n== H2\n\n\n----\n====== H6\n\n");
	}

	@Test
	public void testBulletList() {
		assertRoundTripExact("* item 1\n** item 1.A.\n* item 2\n** item 2.A.\n*** item 2.A.i.\n*** item 2.A.ii.\n");
	}

	@Test
	public void testNumericList() {
		assertRoundTripExact("# item 1\n## item 1.A.\n# item 2\n## item 2.A.\n### item 2.A.i.\n### item 2.A.ii.\n");
	}

	@Test
	public void testTable() {
		assertRoundTripExact("|=H Col 1|=H Col 2|\n|Cell 1 line 1\\\\Cell 1 line 2|Cell 2|\n\n");
	}

	@Test
	public void testParagraphParsedToHtml() {
		StringWriter out = new StringWriter();
		DocumentBuilder builder = new CreoleDocumentBuilder(out);
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("a paragraph");
		builder.endBlock();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("another paragraph");
		builder.lineBreak();
		builder.characters("with 2 lines");
		builder.endBlock();
		builder.endDocument();

		assertParsedHtmlMatchesRoundTrip("<p>a paragraph</p><p>another paragraph<br/>with 2 lines</p>", out.toString());
	}

	@Test
	public void testSpansWithLineBreaksParsedToHtml() {
		StringWriter out = new StringWriter();
		DocumentBuilder builder = new CreoleDocumentBuilder(out);
		builder.beginDocument();
		builder.characters("This is ");
		buildSpan(builder, SpanType.STRONG, "strong");
		builder.lineBreak();
		builder.characters("This is ");
		buildSpan(builder, SpanType.ITALIC, "italic");
		builder.lineBreak();
		builder.characters("This is ");
		buildSpan(builder, SpanType.UNDERLINED, "underlined");
		builder.endDocument();

		assertParsedHtmlMatchesRoundTrip(
				"<p>This is <b>strong</b><br/>This is <i>italic</i><br/>This is <u>underlined</u></p>", out.toString());
	}

	@Test
	public void testHeadingsAndHorizontalRuleParsedToHtml() {
		StringWriter out = new StringWriter();
		DocumentBuilder builder = new CreoleDocumentBuilder(out);
		builder.beginDocument();
		buildHeading(builder, 1, "H1");
		buildHeading(builder, 2, "H2");
		builder.horizontalRule();
		buildHeading(builder, 6, "H6");
		builder.endDocument();

		assertParsedHtmlMatchesRoundTrip("<h1 id=\"H1\">H1</h1><h2 id=\"H2\">H2</h2><hr/><h6 id=\"H6\">H6</h6>",
				out.toString());
	}

	@Test
	public void testBulletListParsedToHtml() {
		StringWriter out = new StringWriter();
		DocumentBuilder builder = new CreoleDocumentBuilder(out);
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());

		buildListItem(builder, "item 1");
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		buildListItem(builder, "item 1.A.");
		builder.endBlock();

		buildListItem(builder, "item 2");
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		buildListItem(builder, "item 2.A.");
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		buildListItem(builder, "item 2.A.i.");
		buildListItem(builder, "item 2.A.ii.");
		builder.endBlock();
		builder.endBlock();

		builder.endBlock();
		builder.endDocument();

		assertParsedHtmlMatchesRoundTrip(
				"<ul><li>item 1<ul><li>item 1.A.</li></ul></li><li>item 2<ul><li>item 2.A.<ul><li>item 2.A.i.</li><li>item 2.A.ii.</li></ul></li></ul></li></ul>",
				out.toString());
	}

	@Test
	public void testNumericListParsedToHtml() {
		StringWriter out = new StringWriter();
		DocumentBuilder builder = new CreoleDocumentBuilder(out);
		builder.beginBlock(BlockType.NUMERIC_LIST, new Attributes());

		buildListItem(builder, "item 1");
		builder.beginBlock(BlockType.NUMERIC_LIST, new Attributes());
		buildListItem(builder, "item 1.A.");
		builder.endBlock();

		buildListItem(builder, "item 2");
		builder.beginBlock(BlockType.NUMERIC_LIST, new Attributes());
		buildListItem(builder, "item 2.A.");
		builder.beginBlock(BlockType.NUMERIC_LIST, new Attributes());
		buildListItem(builder, "item 2.A.i.");
		buildListItem(builder, "item 2.A.ii.");
		builder.endBlock();
		builder.endBlock();

		builder.endBlock();
		builder.endDocument();

		assertParsedHtmlMatchesRoundTrip(
				"<ol><li>item 1<ol><li>item 1.A.</li></ol></li><li>item 2<ol><li>item 2.A.<ol><li>item 2.A.i.</li><li>item 2.A.ii.</li></ol></li></ol></li></ol>",
				out.toString());
	}

	@Test
	public void testLinkParsedToHtml() {
		StringWriter out = new StringWriter();
		DocumentBuilder builder = new CreoleDocumentBuilder(out);
		builder.beginDocument();
		builder.characters("This is a ");
		builder.link("http://example.com/", "link");
		builder.characters(".");
		builder.endDocument();
		assertParsedHtmlMatchesRoundTrip("<p>This is a <a href=\"http://example.com/\">link</a>.</p>", out.toString());
	}

	@Test
	public void testTableParsedToHtml() {
		StringWriter out = new StringWriter();
		DocumentBuilder builder = new CreoleDocumentBuilder(out);
		builder.beginDocument();
		builder.beginBlock(BlockType.TABLE, new Attributes());

		builder.beginBlock(BlockType.TABLE_ROW, new Attributes());
		buildTableCellHeader(builder, "Col 1");
		buildTableCellHeader(builder, "Col 2");
		builder.endBlock();

		builder.beginBlock(BlockType.TABLE_ROW, new Attributes());
		builder.beginBlock(BlockType.TABLE_CELL_NORMAL, new Attributes());
		builder.characters("Col 1 line 1");
		builder.lineBreak();
		builder.characters("Col 1 line 2");
		builder.endBlock();
		buildTableCellNormal(builder, "Col 2");

		builder.endBlock();
		builder.endDocument();
		assertParsedHtmlMatchesRoundTrip(
				"<table><tr><th>Col 1</th><th>Col 2</th></tr><tr><td>Col 1 line 1<br/>Col 1 line 2</td><td>Col 2</td></tr></table>",
				out.toString());
	}

	@Test
	public void testPreformattedBlockParsedToHtml() {
		StringWriter out = new StringWriter();
		DocumentBuilder builder = new CreoleDocumentBuilder(out);
		builder.beginDocument();
		builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
		builder.characters("line 1");
		builder.lineBreak();
		builder.characters("line 2");
		builder.lineBreak();
		builder.lineBreak();
		builder.characters("line 3");
		builder.endBlock();
		builder.lineBreak();
		builder.characters("line 4");
		builder.endDocument();
		assertParsedHtmlMatchesRoundTrip("<pre>line 1\\\\line 2\\\\\\\\line 3</pre><p><br/>line 4</p>", out.toString());
	}

	@Test
	public void testCodeSpanParsedToHtml() {
		StringWriter out = new StringWriter();
		DocumentBuilder builder = new CreoleDocumentBuilder(out);
		builder.beginDocument();
		builder.characters("This class is called ");
		builder.beginSpan(SpanType.CODE, new Attributes());
		builder.characters("** /nCreoleLanguageIntegrationTest/n **");
		builder.endSpan();
		builder.characters(" and it tests Creole markup.");
		builder.endDocument();
		assertParsedHtmlMatchesRoundTrip(
				"<p>This class is called <code>** /nCreoleLanguageIntegrationTest/n **</code> and it tests Creole markup.</p>",
				out.toString());
	}

	@Test
	public void testEscapedCharacters() {
		StringWriter out = new StringWriter();
		DocumentBuilder builder = new CreoleDocumentBuilder(out);
		builder.beginDocument();
		builder.characters("~ Tilde ~ * Asterix * # Number # | Pipe |");
		builder.endDocument();
		assertParsedHtmlMatchesRoundTrip("<p>~ Tilde ~ * Asterix * # Number # | Pipe |</p>", out.toString());
	}

	@Test
	public void testEscapedCharactersInCodeSpan() {
		StringWriter out = new StringWriter();
		DocumentBuilder builder = new CreoleDocumentBuilder(out);
		builder.beginDocument();
		builder.beginSpan(SpanType.CODE, new Attributes());
		builder.characters("~ Tilde ~ * Asterix * # Number # | Pipe |");
		builder.endSpan();
		builder.endDocument();
		assertParsedHtmlMatchesRoundTrip("<p><code>~ Tilde ~ * Asterix * # Number # | Pipe |</code></p>",
				out.toString());
	}

	private void assertRoundTripExact(String textile) {
		assertRoundTrip(textile, textile);
	}

	private void assertRoundTrip(String textIn, String textOut) {
		Writer creoleOut = new StringWriter();
		CreoleLanguage creoleLanguage = new CreoleLanguage();

		MarkupParser parser = new MarkupParser(creoleLanguage);
		parser.setBuilder(creoleLanguage.createDocumentBuilder(creoleOut));
		parser.parse(textIn, false);

		assertEquals(textOut, creoleOut.toString());
	}

	private void buildSpan(DocumentBuilder builder, SpanType type, String content) {
		builder.beginSpan(type, new Attributes());
		builder.characters(content);
		builder.endSpan();
	}

	private void buildHeading(DocumentBuilder builder, int headingLevel, String content) {
		builder.beginHeading(headingLevel, new Attributes());
		builder.characters(content);
		builder.endHeading();
	}

	private void buildListItem(DocumentBuilder builder, String content) {
		buildBlock(builder, BlockType.LIST_ITEM, content);
	}

	private void buildTableCellNormal(DocumentBuilder builder, String content) {
		buildBlock(builder, BlockType.TABLE_CELL_NORMAL, content);
	}

	private void buildTableCellHeader(DocumentBuilder builder, String content) {
		buildBlock(builder, BlockType.TABLE_CELL_HEADER, content);
	}

	private void buildBlock(DocumentBuilder builder, BlockType type, String content) {
		builder.beginBlock(type, new Attributes());
		builder.characters(content);
		builder.endBlock();
	}

	private void assertParsedHtmlMatchesRoundTrip(String expectedHtml, String originalCreole) {
		String html = parseCreoleToHtml(originalCreole);
		assertEquals(expectedHtml, html);
		String creoleParsedFromHtml = parseHtmlToCreole(html);
		assertEquals(originalCreole, creoleParsedFromHtml);
	}

	private String parseCreoleToHtml(String creoleText) {
		MarkupParser creoleParser = new MarkupParser(new CreoleLanguage());
		String html = creoleParser.parseToHtml(creoleText);
		return extractHtmlBody(html);
	}

	private String extractHtmlBody(String htmlDocumentText) {
		Pattern bodyTags = Pattern.compile("(?:<body>((?s).*)<\\/body>)");
		Matcher matcher = bodyTags.matcher(htmlDocumentText);
		assertTrue(matcher.find());
		return matcher.group(1);
	}

	private String parseHtmlToCreole(String htmlText) {
		HtmlLanguage htmlLanguage = new HtmlLanguage();
		MarkupParser htmlParser = new MarkupParser(htmlLanguage);
		Writer out = new StringWriter();
		DocumentBuilder creoleBuilder = new CreoleLanguage().createDocumentBuilder(out);
		htmlParser.setBuilder(creoleBuilder);
		htmlParser.parse(htmlText, true);
		return out.toString();
	}

}
