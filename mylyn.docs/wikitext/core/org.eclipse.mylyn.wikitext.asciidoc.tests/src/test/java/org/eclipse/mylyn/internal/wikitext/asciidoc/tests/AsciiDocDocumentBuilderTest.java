/*******************************************************************************
 * Copyright (c) 2016, 2024 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeremie Bresson - copied from MarkdownDocumentBuilderTest and adapted to AsciiDoc
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.asciidoc.internal.AsciiDocDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.junit.Before;
import org.junit.Test;

public class AsciiDocDocumentBuilderTest {

	private DocumentBuilder builder;

	private StringWriter out;

	@Before
	public void setUp() throws Exception {
		out = new StringWriter();
		builder = new AsciiDocDocumentBuilder(out);
	}

	@Test
	public void testUnsupportedBlock() {
		builder.beginDocument();
		builder.beginBlock(BlockType.FOOTNOTE, new Attributes());
		builder.characters("unsupported");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("unsupported\n\n");
	}

	@Test
	public void testEmptyBlock() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.endBlock();
		builder.endDocument();
		assertMarkup("");
	}

	@Test
	public void testParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("A paragraph ends when a blank line begins!");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("A paragraph ends when a blank line begins!\n\n");
	}

	@Test
	public void testParagraphConsecutive() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("Paragraph 1");
		builder.endBlock();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("Paragraph 2");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("Paragraph 1\n\nParagraph 2\n\n");
	}

	@Test
	public void testParagraphWithStrongEmphasis() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("some ");
		builder.beginSpan(SpanType.STRONG, new Attributes());
		builder.characters("strong");
		builder.endSpan();
		builder.characters(" and ");
		builder.beginSpan(SpanType.EMPHASIS, new Attributes());
		builder.characters("emphasis");
		builder.endSpan();
		builder.endBlock();
		builder.endDocument();
		assertMarkup("some *strong* and _emphasis_\n\n");
	}

	@Test
	public void testImplicitParagraph() {
		builder.beginDocument();
		builder.characters("text1");
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text2");
		builder.endBlock();
		builder.characters("text3");
		builder.endDocument();
		assertMarkup("text1\n\ntext2\n\ntext3\n\n");
	}

	@Test
	public void testImplicitParagraphWithStrongEmphasis() {
		builder.beginDocument();
		builder.characters("some ");
		builder.beginSpan(SpanType.STRONG, new Attributes());
		builder.characters("strong");
		builder.endSpan();
		builder.characters(" and ");
		builder.beginSpan(SpanType.EMPHASIS, new Attributes());
		builder.characters("emphasis");
		builder.endSpan();
		builder.endDocument();
		assertMarkup("some *strong* and _emphasis_\n\n");
	}

	@Test
	public void testLineBreak() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("line");
		builder.lineBreak();
		builder.characters("break");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("line\nbreak\n\n");
	}

	@Test
	public void testLineBreakImplicitParagraph() {
		builder.beginDocument();
		builder.characters("line");
		builder.lineBreak();
		builder.characters("break");
		builder.endDocument();
		assertMarkup("line\nbreak\n\n");
	}

	@Test
	public void testDiv() {
		builder.beginDocument();
		builder.beginBlock(BlockType.DIV, new Attributes());
		builder.characters("A div.");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("A div.");
	}

	@Test
	public void testHeadings() {
		builder.beginDocument();
		builder.beginHeading(1, new Attributes());
		builder.characters("This is an H1");
		builder.endHeading();
		builder.beginHeading(2, new Attributes());
		builder.characters("This is an H2");
		builder.endHeading();
		builder.beginHeading(6, new Attributes());
		builder.characters("This is an H6");
		builder.endHeading();
		builder.endDocument();
		assertMarkup("= This is an H1\n\n== This is an H2\n\n====== This is an H6\n\n");
	}

	@Test
	public void testHeadingsAfterParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("Some paragraph.");
		builder.endBlock();
		builder.beginHeading(2, new Attributes());
		builder.characters("Title");
		builder.endHeading();
		builder.endDocument();
		assertMarkup("Some paragraph.\n\n== Title\n\n");
	}

	@Test
	public void testHeadingsAfterList() {
		builder.beginDocument();
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("lorem");
		builder.endBlock();
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("ipsum");
		builder.endBlock();
		builder.endBlock();
		builder.beginHeading(2, new Attributes());
		builder.characters("Title");
		builder.endHeading();
		builder.endDocument();
		assertMarkup("* lorem\n* ipsum\n\n== Title\n\n");
	}

	@Test
	public void testBlockQuote() {
		builder.beginDocument();
		builder.beginBlock(BlockType.QUOTE, new Attributes());
		builder.characters("A quote by someone important.");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("[quote]\n----\nA quote by someone important.\n----\n");
	}

	@Test
	public void testBlockQuoteConsecutive() {
		builder.beginDocument();
		builder.beginBlock(BlockType.QUOTE, new Attributes());
		builder.characters("Quote 1");
		builder.endBlock();
		builder.beginBlock(BlockType.QUOTE, new Attributes());
		builder.characters("Quote 2");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("[quote]\n----\nQuote 1\n----\n[quote]\n----\nQuote 2\n----\n");
	}

	@Test
	public void testBlockQuoteWithParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.QUOTE, new Attributes());
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("First paragraph.");
		builder.endBlock();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("Second paragraph.");
		builder.endBlock();
		builder.endBlock();
		builder.endDocument();

		assertMarkup("[quote]\n----\nFirst paragraph.\n\nSecond paragraph.\n\n\n----\n");
	}

	@Test
	public void testBlockQuoteNestedList() {
		builder.beginDocument();
		builder.beginBlock(BlockType.QUOTE, new Attributes());
		builder.beginHeading(2, new Attributes());
		builder.characters("Header.");
		builder.endHeading();
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("First item.");
		builder.endBlock();
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("Second item.");
		builder.endBlock();
		builder.endBlock();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("Lorem Ipsum");
		builder.endBlock();
		builder.endBlock();
		builder.endDocument();
		assertMarkup("[quote]\n----\n== Header.\n\n* First item.\n* Second item.\n\nLorem Ipsum\n\n\n----\n");
	}

	@Test
	public void testBlockQuoteWithLineBreak() {
		builder.beginDocument();
		builder.beginBlock(BlockType.QUOTE, new Attributes());
		builder.characters("Some text...");
		builder.lineBreak();
		builder.characters("...with a line break.");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("[quote]\n----\nSome text...\n...with a line break.\n----\n");
	}

	@Test
	public void testBlockQuoteWithParagraphLineBreak() {
		builder.beginDocument();
		builder.beginBlock(BlockType.QUOTE, new Attributes());
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("Some text...");
		builder.lineBreak();
		builder.characters("...with a line break.");
		builder.endBlock();
		builder.endBlock();
		builder.endDocument();
		assertMarkup("[quote]\n----\nSome text...\n...with a line break.\n\n\n----\n");
	}

	@Test
	public void testListBulleted() {
		builder.beginDocument();
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("X");
		builder.endBlock();
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("Y");
		builder.endBlock();
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("Z");
		builder.endBlock();
		builder.endBlock();
		builder.endDocument();
		assertMarkup("* X\n* Y\n* Z\n");
	}

	@Test
	public void testListNumeric() {
		builder.beginDocument();
		builder.beginBlock(BlockType.NUMERIC_LIST, new Attributes());
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("One");
		builder.endBlock();
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("Two");
		builder.endBlock();
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("Three");
		builder.endBlock();
		builder.endBlock();
		builder.endDocument();
		assertMarkup("1. One\n2. Two\n3. Three\n");
	}

	@Test
	public void testListConsecutive() {
		builder.beginDocument();
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("Food");
		builder.endBlock();
		builder.endBlock();
		builder.beginBlock(BlockType.NUMERIC_LIST, new Attributes());
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("Drink");
		builder.endBlock();
		builder.endBlock();
		builder.endDocument();
		assertMarkup("* Food\n\n1. Drink\n");
	}

	@Test
	public void testListWithParagraphs() {
		builder.beginDocument();
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("X");
		builder.endBlock();
		builder.endBlock();
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("Y");
		builder.endBlock();
		builder.endBlock();
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("Z");
		builder.endBlock();
		builder.endBlock();
		builder.endBlock();
		builder.endDocument();
		assertMarkup("* X\n\n* Y\n\n* Z\n\n");
	}

	@Test
	public void testListItemWithHangingParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("List item with two paragraphs.");
		builder.endBlock();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("Second paragraph.");
		builder.endBlock();
		builder.endBlock();
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("Simple list item.");
		builder.endBlock();
		builder.endBlock();
		builder.endDocument();
		assertMarkup("* List item with two paragraphs.\n\nSecond paragraph.\n\n* Simple list item.\n");
	}

	@Test
	public void testListItemWithBlockQuote() {
		builder.beginDocument();
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("A list item with a blockquote:");
		builder.endBlock();
		builder.beginBlock(BlockType.QUOTE, new Attributes());
		builder.characters("This is a blockquote");
		builder.lineBreak();
		builder.characters("inside a list item.");
		builder.endBlock();
		builder.endBlock();
		builder.endBlock();
		builder.endDocument();
		assertMarkup(
				"* A list item with a blockquote:\n\n[quote]\n----\nThis is a blockquote\ninside a list item.\n----\n");
	}

	@Test
	public void testListItemWithCodeBlock() {
		builder.beginDocument();
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("A list item with a code block:");
		builder.endBlock();

		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("code goes here");
		builder.lineBreak();
		builder.characters("code second line");
		builder.endBlock();

		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("another para");
		builder.endBlock();

		builder.endBlock();
		builder.endBlock();
		builder.endDocument();
		assertMarkup(
				"* A list item with a code block:\n\n[listing]\n----\ncode goes here\ncode second line\n----\n\nanother para\n\n");
	}

	@Test
	public void testCodeBlock() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("Paragraph:");
		builder.endBlock();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("Code block.");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("Paragraph:\n\n[listing]\n----\nCode block.\n----\n\n");
	}

	@Test
	public void testCodeBlockConsecutive() {
		builder.beginDocument();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("// comment");
		builder.endBlock();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("code();");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("[listing]\n----\n// comment\n----\n\n[listing]\n----\ncode();\n----\n\n");
	}

	@Test
	public void testCodeBlockIndented() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("Below is code:");
		builder.endBlock();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("open brace {\n    code\n} end brace");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("Below is code:\n\n[listing]\n----\nopen brace {\n    code\n} end brace\n----\n\n");
	}

	@Test
	public void testCodeBlockWhiteSpace() {
		builder.beginDocument();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("foo() {\n    bar();\n\n    baz();\n}");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("[listing]\n----\nfoo() {\n    bar();\n\n    baz();\n}\n----\n\n");
	}

	@Test
	public void testCodeBlockEscapedHtml() {
		builder.beginDocument();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.entityReference("lt");
		builder.characters("body");
		builder.entityReference("gt");
		builder.characters("\n    UNIX was created at AT");
		builder.entityReference("amp");
		builder.characters("T.\n");
		builder.entityReference("lt");
		builder.characters("/body");
		builder.entityReference("gt");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("[listing]\n----\n<body>\n    UNIX was created at AT&T.\n</body>\n----\n\n");
	}

	@Test
	public void testPreformattedBlock() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("Paragraph:");
		builder.endBlock();
		builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
		builder.characters("Code block.");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("Paragraph:\n\n[listing]\n----\nCode block.\n----\n\n");
	}

	@Test
	public void testUnsupportedSpan() {
		builder.beginDocument();
		builder.beginSpan(SpanType.UNDERLINED, new Attributes());
		builder.characters("unsupported");
		builder.endSpan();
		builder.endDocument();
		assertMarkup("unsupported\n\n");
	}

	@Test
	public void testEmptySpan() {
		builder.beginDocument();
		builder.characters("prefix");
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.endSpan();
		builder.characters(" suffix");
		builder.endDocument();
		assertMarkup("prefix suffix\n\n");
	}

	@Test
	public void testSpan() {
		builder.beginDocument();
		builder.characters("prefix ");
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("bold");
		builder.endSpan();
		builder.characters(" suffix");
		builder.endDocument();
		assertMarkup("prefix *bold* suffix\n\n");
	}

	@Test
	public void testSpanImplicitParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("Paragraph");
		builder.endBlock();
		builder.beginSpan(SpanType.ITALIC, new Attributes());
		builder.characters("Implicit");
		builder.endSpan();
		builder.characters(" paragraph");
		builder.endDocument();
		assertMarkup("Paragraph\n\n_Implicit_ paragraph\n\n");
	}

	@Test
	public void testLink() {
		builder.beginDocument();
		builder.characters("This ");
		builder.link("http://example.net/", "link");
		builder.characters(" has no title attribute.");
		builder.endDocument();
		assertMarkup("This link:http://example.net/[link] has no title attribute.\n\n");
	}

	@Test
	public void testLinkWithoutText() {
		builder.beginDocument();
		builder.characters("This ");
		builder.link("http://example.net/", null);
		builder.characters(" has no title attribute.");
		builder.endDocument();
		assertMarkup("This link:http://example.net/[http://example.net/] has no title attribute.\n\n");
	}

	@Test
	public void testLinkWithTitle() {
		//The title properties is ignored.
		builder.beginDocument();
		builder.characters("This is ");
		LinkAttributes attr = new LinkAttributes();
		attr.setTitle("Title");
		builder.link(attr, "http://example.com/", "an example");
		builder.characters(" inline link.");
		builder.endDocument();
		assertMarkup("This is link:http://example.com/[an example] inline link.\n\n");
	}

	@Test
	public void testLinkWithEmptyAttributes() {
		builder.beginDocument();
		builder.characters("This is ");
		builder.link(new Attributes(), "http://example.com/", "an example");
		builder.characters(" inline link.");
		builder.endDocument();
		assertMarkup("This is link:http://example.com/[an example] inline link.\n\n");
	}

	@Test
	public void testLinkImplicitParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("A paragraph.");
		builder.endBlock();
		builder.link("http://example.com/", "A link");
		builder.characters(" opens an implicit paragraph.");
		builder.endDocument();
		assertMarkup("A paragraph.\n\nlink:http://example.com/[A link] opens an implicit paragraph.\n\n");
	}

	@Test
	public void testLinkSpanEmptyAttributes() {
		builder.beginDocument();
		builder.beginSpan(SpanType.LINK, new Attributes());
		builder.characters("http://example.com");
		builder.endSpan();
		builder.endDocument();
		assertMarkup("http://example.com\n\n");
	}

	@Test
	public void testEmphasis() {
		builder.beginDocument();
		builder.beginSpan(SpanType.EMPHASIS, new Attributes());
		builder.characters("emphasis");
		builder.endSpan();
		builder.endDocument();
		assertMarkup("_emphasis_\n\n");
	}

	@Test
	public void testItalic() {
		builder.beginDocument();
		builder.beginSpan(SpanType.ITALIC, new Attributes());
		builder.characters("italic");
		builder.endSpan();
		builder.endDocument();
		assertMarkup("_italic_\n\n");
	}

	@Test
	public void testStrong() {
		builder.beginDocument();
		builder.beginSpan(SpanType.STRONG, new Attributes());
		builder.characters("strong");
		builder.endSpan();
		builder.endDocument();
		assertMarkup("*strong*\n\n");
	}

	@Test
	public void testBold() {
		builder.beginDocument();
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("bold");
		builder.endSpan();
		builder.endDocument();
		assertMarkup("*bold*\n\n");
	}

	@Test
	public void testHighlight() {
		builder.beginDocument();
		builder.beginSpan(SpanType.MARK, new Attributes());
		builder.characters("highlight");
		builder.endSpan();
		builder.endDocument();
		assertMarkup("#highlight#\n\n");
	}

	@Test
	public void testCodeSpan() {
		builder.beginDocument();
		builder.characters("Here's a ");
		builder.beginSpan(SpanType.CODE, new Attributes());
		builder.characters("code()");
		builder.endSpan();
		builder.characters(" span.");
		builder.endBlock();
		assertMarkup("Here's a `code()` span.\n\n");
	}

	@Test
	public void testCodeSpanEscaped() {
		builder.beginDocument();
		builder.characters("A single backtick in a code span: ");
		builder.beginSpan(SpanType.CODE, new Attributes());
		builder.characters("`");
		builder.endSpan();
		builder.lineBreak();
		builder.characters("A backtick-delimited string in a code span: ");
		builder.beginSpan(SpanType.CODE, new Attributes());
		builder.characters("`foo`");
		builder.endSpan();
		builder.endDocument();
		assertMarkup(
				"A single backtick in a code span: `` ` ``\nA backtick-delimited string in a code span: `` `foo` ``\n\n");
	}

	@Test
	public void testSpanSimple() {
		builder.beginDocument();
		builder.characters("Here's a ");
		builder.beginSpan(SpanType.SPAN, new Attributes());
		builder.characters("plain");
		builder.endSpan();
		builder.characters(" span.");
		builder.endBlock();
		assertMarkup("Here's a plain span.\n\n");
	}

	@Test
	public void testSpanWithCssClass() {
		builder.beginDocument();
		builder.characters("Here's a ");
		builder.beginSpan(SpanType.SPAN, new Attributes(null, "css-class", null, null));
		builder.characters("css styled");
		builder.endSpan();
		builder.characters(" span.");
		builder.endBlock();
		assertMarkup("Here's a [css-class]#css styled# span.\n\n");
	}

	@Test
	public void testSuperscript() {
		builder.beginDocument();
		builder.beginSpan(SpanType.SUPERSCRIPT, new Attributes());
		builder.characters("superscript");
		builder.endSpan();
		builder.endDocument();
		assertMarkup("^superscript^\n\n");
	}

	@Test
	public void testSubscript() {
		builder.beginDocument();
		builder.beginSpan(SpanType.SUBSCRIPT, new Attributes());
		builder.characters("subscript");
		builder.endSpan();
		builder.endDocument();
		assertMarkup("~subscript~\n\n");
	}

	@Test
	public void testImage() {
		builder.beginDocument();
		builder.image(new ImageAttributes(), "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("image:/path/to/img.jpg[]\n\n");
	}

	@Test
	public void testImageWithTitle() {
		builder.beginDocument();
		ImageAttributes attr = new ImageAttributes();
		attr.setAlt("Alt text");
		attr.setTitle("Optional title");
		builder.image(attr, "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("image:/path/to/img.jpg[Alt text, title=\"Optional title\"]\n\n");
	}

	@Test
	public void testImageWithEmptyAttributes() {
		builder.beginDocument();
		builder.image(new Attributes(), "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("image:/path/to/img.jpg[]\n\n");
	}

	@Test
	public void testImageNoUrl() {
		builder.beginDocument();
		ImageAttributes attr = new ImageAttributes();
		attr.setAlt("Alt text");
		attr.setTitle("Optional title");
		builder.image(attr, null);
		builder.endDocument();
		assertMarkup("image:[Alt text, title=\"Optional title\"]\n\n");
	}

	@Test
	public void testImageImplicitParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("Below is an image:");
		builder.endBlock();
		builder.image(new ImageAttributes(), "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("Below is an image:\n\nimage:/path/to/img.jpg[]\n\n");
	}

	@Test
	public void testImageLink() {
		builder.beginDocument();
		builder.imageLink("http://example.net/", "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("link:http://example.net/[image:/path/to/img.jpg[]]\n\n");
	}

	@Test
	public void testImageLinkWithSingleEmptyAttributes() {
		builder.beginDocument();
		builder.imageLink(new Attributes(), "http://example.net/", "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("link:http://example.net/[image:/path/to/img.jpg[]]\n\n");
	}

	@Test
	public void testImageLinkWithBothEmptyAttributes() {
		builder.beginDocument();
		builder.imageLink(new Attributes(), new Attributes(), "http://example.net/", "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("link:http://example.net/[image:/path/to/img.jpg[]]\n\n");
	}

	@Test
	public void testImageLinkWithImageAttributes() {
		builder.beginDocument();
		ImageAttributes attr = new ImageAttributes();
		attr.setAlt("Alt text");
		attr.setTitle("Optional title");
		builder.imageLink(attr, "http://example.net/", "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("link:http://example.net/[image:/path/to/img.jpg[Alt text, title=\"Optional title\"]]\n\n");
	}

	@Test
	public void testImageLinkWithLinkAttributes() {
		builder.beginDocument();
		LinkAttributes linkAttr = new LinkAttributes();
		linkAttr.setTitle("Optional link title");
		ImageAttributes imageAttr = new ImageAttributes();
		imageAttr.setAlt("Alt text");
		imageAttr.setTitle("Optional image title");
		builder.imageLink(linkAttr, imageAttr, "http://example.net/", "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("link:http://example.net/[image:/path/to/img.jpg[Alt text, title=\"Optional image title\"]]\n\n");
	}

	@Test
	public void testImplicitParagrahWithSpan() {
		builder.beginDocument();
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text1");
		builder.endSpan();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text2");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("*text1*\n\ntext2\n\n");
	}

	@Test
	public void testSpanOpensImplicitParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text");
		builder.endBlock();
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("bold");
		builder.endSpan();
		builder.characters(" text");
		builder.endDocument();
		assertMarkup("text\n\n*bold* text\n\n");
	}

	@Test
	public void testTable() {
		builder.beginDocument();
		builder.beginBlock(BlockType.TABLE, new Attributes());
		builder.beginBlock(BlockType.TABLE_ROW, new Attributes());
		builder.beginBlock(BlockType.TABLE_CELL_NORMAL, new Attributes());
		builder.characters("cell 1");
		builder.endBlock();
		builder.beginBlock(BlockType.TABLE_CELL_NORMAL, new Attributes());
		builder.characters("cell 2");
		builder.endBlock();
		builder.endBlock();
		builder.beginBlock(BlockType.TABLE_ROW, new Attributes());
		builder.beginBlock(BlockType.TABLE_CELL_NORMAL, new Attributes());
		builder.characters("cell a");
		builder.endBlock();
		builder.beginBlock(BlockType.TABLE_CELL_NORMAL, new Attributes());
		builder.characters("cell b");
		builder.endBlock();
		builder.endBlock();
		builder.beginBlock(BlockType.TABLE_ROW, new Attributes());
		builder.beginBlock(BlockType.TABLE_CELL_NORMAL, new Attributes());
		builder.characters("cell A");
		builder.endBlock();
		builder.beginBlock(BlockType.TABLE_CELL_NORMAL, new Attributes());
		builder.characters("cell B");
		builder.endBlock();
		builder.endBlock();
		builder.endBlock();
		builder.endDocument();
		assertMarkup("|===\n|cell 1 |cell 2 \n\n|cell a |cell b \n\n|cell A |cell B \n|===\n");
	}

	@Test
	public void testEntityReference() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("5 ");
		builder.entityReference("gt");
		builder.characters(" 4");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("5 > 4\n\n");
	}

	@Test
	public void testEntityReferenceImplicitParagraph() {
		builder.beginDocument();
		builder.characters("4 ");
		builder.entityReference("lt");
		builder.characters(" 5");
		builder.endDocument();
		assertMarkup("4 < 5\n\n");
	}

	@Test
	public void testEntityCopyright() {
		builder.beginDocument();
		builder.entityReference("copy");
		builder.characters(" XY");
		builder.entityReference("amp");
		builder.characters("Z 2014");
		builder.endDocument();
		assertMarkup("(C) XY&Z 2014\n\n");
	}

	@Test
	public void testAcronym() {
		builder.beginDocument();
		builder.acronym("world", "This is a definition");
		builder.endDocument();
		assertMarkup("world(This is a definition)\n\n");
	}

	private void assertMarkup(String expected) {
		String markup = out.toString();
		assertEquals(expected, markup);
	}

}
