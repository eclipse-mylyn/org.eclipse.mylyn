/*******************************************************************************
 * Copyright (c) 2011, 2024 Tasktop Technologies
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

package org.eclipse.mylyn.wikitext.textile.internal.tests;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.textile.internal.TextileDocumentBuilder;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David Green
 * @see TextileDocumentBuilder
 */
@SuppressWarnings("nls")
public class TextileDocumentBuilderTest {

	private static final String[] PLATFORM_NEWLINES = { //
			"\r\n", // Windows
			"\r", // Mac
			"\n", // Unix, Linux
	};

	private TextileDocumentBuilder builder;

	private StringWriter out;

	@Before
	public void setUp() throws Exception {
		out = new StringWriter();
		builder = new TextileDocumentBuilder(out);
	}

	@Test
	public void testParagraph_MultipleNewlinesInParagraph() throws Exception {
		for (String newline : PLATFORM_NEWLINES) {
			setUp();
			builder.beginDocument();
			builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
			builder.characters("text" + newline + newline + "more text");
			builder.endBlock();
			builder.endDocument();

			String markup = out.toString();

			assertEquals("text more text\n\n", markup);
		}
	}

	@Test
	public void testParagraph_MultipleNewlinesInImplicitParagraph() throws Exception {
		for (String newline : PLATFORM_NEWLINES) {
			setUp();
			builder.beginDocument();
			builder.characters("a" + newline + newline + "b");
			builder.endDocument();

			String markup = out.toString();

			assertEquals("a b\n\n", markup);
		}
	}

	@Test
	public void testParagraph_MultipleLineBreaksInParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text");
		builder.lineBreak();
		builder.lineBreak();
		builder.characters("more text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text\nmore text\n\n", markup);
	}

	@Test
	public void testParagraph_MultipleLineBreaksInImplicitParagraph() {
		builder.beginDocument();
		builder.characters("text");
		builder.lineBreak();
		builder.lineBreak();
		builder.characters("more text");
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text\nmore text\n\n", markup);
	}

	@Test
	public void testParagraph_NewlineFollowedBySpaceOrTabInParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text");
		builder.lineBreak();
		builder.characters(" more");
		builder.lineBreak();
		builder.characters("\tmore2 text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text\n more\n\tmore2 text\n\n", markup);
	}

	@Test
	public void testMultipleParagraphs() {
		builder.beginDocument();

		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("first paragraph");
		builder.endBlock();

		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("second paragraph");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("first paragraph\n\nsecond paragraph\n\n", markup);
	}

	@Test
	public void testParagraphWithBoldEmphasis() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("some ");
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("bold");
		builder.endSpan();
		builder.characters(" and ");
		builder.beginSpan(SpanType.EMPHASIS, new Attributes());
		builder.characters("emphasis");
		builder.endSpan();
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("some **bold** and _emphasis_\n\n", markup);
	}

	@Test
	public void testParagraphWithCssClass() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes(null, "test", null, null));
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("p(test). text more text\n\n", markup);
	}

	@Test
	public void testParagraphWithCssStyle() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes(null, null, "x-test: foo;", null));
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("p{x-test: foo;}. text more text\n\n", markup);
	}

	@Test
	public void testParagraphWithId() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes("123", null, null, null));
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("p(#123). text more text\n\n", markup);
	}

	@Test
	public void testParagraphWithIdAndClass() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes("123", "test", null, null));
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("p(test#123). text more text\n\n", markup);
	}

	@Test
	public void testParagraphWithLink() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text\n\nmore text ");
		final LinkAttributes attributes = new LinkAttributes();
		attributes.setHref("http://example.com/foo+bar/baz.gif");
		builder.beginSpan(SpanType.LINK, attributes);
		builder.characters("baz");
		builder.endSpan();
		builder.characters(" test");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text more text \"baz\":http://example.com/foo+bar/baz.gif test\n\n", markup);
	}

	@Test
	public void testBlockCode() {
		builder.beginDocument();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("bc.. text\n\nmore text\n\n", markup);
	}

	@Test
	public void testParagraphFollowingExtendedBlockCode() {
		builder.beginDocument();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text");
		builder.endBlock();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text2");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("bc.. text\n\nmore text\n\np. text\n\ntext2\n\n", markup);
	}

	@Test
	public void testHeading1() {
		builder.beginDocument();
		builder.beginHeading(1, new Attributes());
		builder.characters("text\n\nmore text");
		builder.endHeading();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("h1. text more text\n\ntext\n\n", markup);
	}

	@Test
	public void testHeading1_WithNestedMarkup() {
		builder.beginDocument();
		builder.beginHeading(1, new Attributes());
		builder.characters("text ");
		builder.beginSpan(SpanType.EMPHASIS, new Attributes());
		builder.characters("emphasized");
		builder.endSpan();
		builder.endHeading();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("h1. text _emphasized_\n\ntext\n\n", markup);
	}

	@Test
	public void testHeading1_WithAttributes() {
		String markup = doHeadingAttributesTest();

		assertEquals("h1(classTest#idTest). text more text\n\n", markup);
	}

	@Test
	public void testHeading1_WithoutAttributes() {
		builder.setEmitAttributes(false);

		String markup = doHeadingAttributesTest();

		assertEquals("h1. text more text\n\n", markup);
	}

	private String doHeadingAttributesTest() {
		builder.beginDocument();
		Attributes headingAttributes = new Attributes();
		headingAttributes.setCssClass("classTest");
		headingAttributes.setId("idTest");
		builder.beginHeading(1, headingAttributes);
		builder.characters("text\n\nmore text");
		builder.endHeading();
		builder.endDocument();

		String markup = out.toString();

		return markup;
	}

	@Test
	public void testImplicitParagrah() {
		builder.beginDocument();
		builder.characters("text1");
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text2");
		builder.endBlock();
		builder.characters("text3");
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text1\n\ntext2\n\ntext3\n\n", markup);
	}

	@Test
	public void testImplicitParagrahWithSpan() {
		builder.beginDocument();
		builder.beginSpan(SpanType.STRONG, new Attributes());
		builder.characters("text1");
		builder.endSpan();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text2");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("*text1*\n\ntext2\n\n", markup);
	}

	@Test
	public void testBoldSpanNoWhitespace_spanAtLineStart() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());

		builder.beginSpan(SpanType.STRONG, new Attributes());
		builder.characters("text2");
		builder.endSpan();
		builder.characters("text3");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("*text2* text3\n\n", markup);
	}

	@Test
	public void testBoldSpanNoWhitespace_spanAtLineEnd() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());

		builder.characters("text3");
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text2");
		builder.endSpan();

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text3 **text2**\n\n", markup);
	}

	@Test
	public void testBoldSpanNoWhitespace_spanMidLine() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());

		builder.characters("text3");
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text2");
		builder.endSpan();
		builder.characters("text4");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text3 **text2** text4\n\n", markup);
	}

	@Test
	public void testBoldSpanNoWhitespace_adjacentSpans() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text2");
		builder.endSpan();
		builder.beginSpan(SpanType.ITALIC, new Attributes());
		builder.characters("text3");
		builder.endSpan();

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("**text2** __text3__\n\n", markup);
	}

	@Test
	public void testBoldSpanWithAdjacentPunctuation() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text2");
		builder.endSpan();
		builder.characters("!");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("**text2**!\n\n", markup);
	}

	@Test
	public void testBulletedList() {
		builder.beginDocument();
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("text2");
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text3");
		builder.endSpan();
		builder.endBlock();

		emitListItem("text4");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("* text2 **text3**\n* text4\n", markup);
	}

	@Test
	public void testBulletedListWithNestedSublist() {
		builder.beginDocument();

		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("first");

		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		emitListItem("first.1");
		emitListItem("first.2");
		builder.endBlock(); // list
		builder.endBlock(); // list item

		emitListItem("second");

		builder.endBlock(); // list

		builder.endDocument();

		String markup = out.toString();

		assertEquals("* first\n** first.1\n** first.2\n* second\n", markup);
	}

	@Test
	public void testBulletedListWithNestedSublist2() {
		builder.beginDocument();

		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());

		emitListItem("first");

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("second");

		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		emitListItem("second.1");
		builder.endBlock(); // list
		builder.endBlock(); // list item
		builder.endBlock(); // list

		builder.beginBlock(BlockType.NUMERIC_LIST, new Attributes());
		emitListItem("third");

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("fourth");
		builder.beginBlock(BlockType.NUMERIC_LIST, new Attributes());
		emitListItem("fourth.1");
		builder.endBlock(); // list
		builder.endBlock(); // list item

		builder.endBlock(); // list

		builder.endDocument();

		String markup = out.toString();

		assertEquals("* first\n* second\n** second.1\n\n# third\n# fourth\n## fourth.1\n", markup);
	}

	@Test
	public void testParagraphListParagraph() {
		builder.beginDocument();
		paragraph("para 1");
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		emitListItem("list item 1");
		emitListItem("list item 2");
		builder.endBlock(); // list
		paragraph("para 2");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("para 1\n\n* list item 1\n* list item 2\n\npara 2\n\n", markup);
	}

	private void paragraph(String text) {
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters(text);
		builder.endBlock();
	}

	private void emitListItem(String text) {
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters(text);
		builder.endBlock();
	}

	@Test
	public void testSpanWithAdjacentWhitespace() {
		builder.beginDocument();

		builder.characters("prefix ");

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("bolded");
		builder.endBlock();

		builder.characters(" suffix");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix **bolded** suffix\n\n", markup);
	}

	@Test
	public void testEmptySpan() {
		builder.beginDocument();

		builder.characters("prefix ");

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.endBlock();

		builder.characters(" suffix");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix suffix\n\n", markup);
	}

	@Test
	public void testMonospaceSpan() {
		builder.beginDocument();

		builder.characters("prefix ");

		builder.beginSpan(SpanType.MONOSPACE, new Attributes());
		builder.characters("text");
		builder.endSpan();

		builder.characters(" suffix");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix %{font-family:monospace;}text% suffix\n\n", markup);
	}

	@Test
	public void testTableWithEmptyCells() {
		builder.beginDocument();
		builder.beginBlock(BlockType.TABLE, new Attributes());

		builder.beginBlock(BlockType.TABLE_ROW, new Attributes());

		builder.beginBlock(BlockType.TABLE_CELL_NORMAL, new Attributes());
		builder.characters("content");
		builder.endBlock();
		builder.beginBlock(BlockType.TABLE_CELL_NORMAL, new Attributes());
		builder.endBlock();

		builder.endBlock();

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("|content| |\n\n", markup);
	}

	@Test
	public void testDivAfterImplicitParagraph() {
		builder.beginDocument();

		builder.characters("test");

		builder.beginBlock(BlockType.DIV, new Attributes());
		builder.characters("more ");
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text");
		builder.endSpan();
		builder.endBlock();

		builder.beginBlock(BlockType.NUMERIC_LIST, new Attributes());

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("text2");
		builder.endSpan();

		builder.endBlock();

		builder.endDocument();

		String markup = out.toString();

		assertEquals("test\n\nmore **text**\n\n# text2\n", markup);
	}

	@Test
	public void testDivWithinTableCell() {
		builder.beginDocument();

		builder.beginBlock(BlockType.TABLE, new Attributes());

		builder.beginBlock(BlockType.TABLE_ROW, new Attributes());

		builder.beginBlock(BlockType.TABLE_CELL_NORMAL, new Attributes());
		builder.characters("first");
		builder.endBlock();

		builder.beginBlock(BlockType.TABLE_CELL_NORMAL, new Attributes());

		builder.beginBlock(BlockType.DIV, new Attributes());
		builder.characters("content");
		builder.endBlock(); // div

		builder.beginBlock(BlockType.DIV, new Attributes());
		builder.characters("content2");
		builder.endBlock(); // div

		builder.endBlock(); // cell

		builder.endBlock(); // row

		builder.endBlock(); // table

		builder.endDocument();

		String markup = out.toString();

		assertEquals("|first| content content2|\n\n", markup);
	}

	@Test
	public void testEmptyBoldSpan() {
		builder.beginDocument();

		builder.characters("first ");

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.endSpan();

		builder.characters("second");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("first second\n\n", markup);
	}

	@Test
	public void testProtectionAgainstNestedSpans() {
		builder.beginDocument();

		builder.beginSpan(SpanType.SPAN, new Attributes(null, null, "color:blue;", null));

		builder.characters("first");

		builder.beginSpan(SpanType.SPAN, new Attributes(null, null, "text-decoration:underline;", null));
		builder.characters(" second");
		builder.endSpan();
		builder.endSpan();

		builder.endDocument();

		String markup = out.toString();

		assertEquals("%{color:blue;}first second%\n\n", markup);
	}

	@Test
	public void testLineBreak() {
		builder.beginDocument();
		builder.characters("line");
		builder.lineBreak();
		builder.characters("break");
		builder.endDocument();

		String markup = out.toString();

		assertEquals("line\nbreak\n\n", markup);
	}

	@Test
	public void testLineBreak_ExplicitBlockAfterExtendedBlock() {
		builder.beginDocument();

		// make sure paragraph is preceded by an extended block
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("one\n\n\ntwo"); // multiple newlines makes this an extended block
		builder.endBlock();

		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("line");
		builder.lineBreak();
		builder.characters("break");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("bc.. one\n\n\ntwo\n\np. line\nbreak\n\n", markup);
	}

	@Test
	public void testLineBreakInFootnote() {
		builder.beginDocument();
		builder.beginBlock(BlockType.FOOTNOTE, new Attributes());
		builder.characters("line");
		builder.lineBreak();
		builder.characters("break");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("fn1. line\nbreak\n\n", markup);
	}

	@Test
	public void testLineBreakInPreformatted_Extended() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
		builder.characters("line");
		builder.lineBreak();
		builder.lineBreak();
		builder.characters("break");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("pre.. line\n\nbreak\n\n", markup);
	}

	@Test
	public void testLineBreakInPreformatted() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
		builder.characters("line");
		builder.lineBreak();
		builder.characters("break");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("pre. line\nbreak\n\n", markup);
	}

	@Test
	public void testLink() {
		builder.beginDocument();

		builder.characters("a ");
		builder.link(new LinkAttributes(), "#foo", "link to foo");
		builder.characters(" test");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("a \"link to foo\":#foo test\n\n", markup);
	}

	@Test
	public void testExternalLink() {
		builder.beginDocument();

		builder.characters("an ");
		builder.link(new LinkAttributes(), "http://example.com/", "external link");
		builder.characters(" test");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("an \"external link\":http://example.com/ test\n\n", markup);
	}

	@Test
	public void testLinkWithNullHref() {
		builder.beginDocument();

		builder.characters("a ");
		builder.link(new LinkAttributes(), null, "link text");
		builder.characters(" test");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("a \"link text\": test\n\n", markup);
	}

	@Test
	public void testSpanLinkWithNullHref() {
		builder.beginDocument();

		builder.characters("a ");
		builder.beginSpan(SpanType.LINK, new LinkAttributes());
		builder.characters("link text");
		builder.endSpan();
		builder.characters(" test");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("a \"link text\": test\n\n", markup);
	}

	@Test
	public void testImageLink() {
		builder.beginDocument();

		builder.characters("a ");
		builder.imageLink(new LinkAttributes(), new ImageAttributes(), "#foo", "fooImage.png");
		builder.characters(" test");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("a !fooImage.png!:#foo test\n\n", markup);
	}

	@Test
	public void testImage() {
		builder.beginDocument();

		builder.characters("a ");
		builder.image(new ImageAttributes(), "fooImage.png");
		builder.characters(" test");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("a !fooImage.png! test\n\n", markup);
	}

	@Test
	public void testImageNoUrl() {
		builder.beginDocument();
		builder.characters("a ");
		builder.image(new ImageAttributes(), null);
		builder.characters(" test");
		builder.endDocument();

		String markup = out.toString();

		assertEquals("a test\n\n", markup);
	}

	@Test
	public void testSpanOpensImplicitParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("// some code");
		builder.endBlock();
		builder.beginSpan(SpanType.DELETED, new Attributes());
		builder.characters("redacted");
		builder.endSpan();
		builder.characters(" text");
		builder.endDocument();

		String markup = out.toString();

		assertEquals("bc. // some code\n\n-redacted- text\n\n", markup);
	}

	@Test
	public void testSpanSuperscript() {
		assertSpan("begin ^span text^ end\n\n", SpanType.SUPERSCRIPT);
	}

	@Test
	public void testSpanSubscript() {
		assertSpan("begin ~span text~ end\n\n", SpanType.SUBSCRIPT);
	}

	@Test
	public void testSpanBold() {
		assertSpan("begin **span text** end\n\n", SpanType.BOLD);
	}

	@Test
	public void testSpanCitation() {
		assertSpan("begin??span text??end\n\n", SpanType.CITATION);
	}

	@Test
	public void testSpanCode() {
		assertSpan("begin @span text@ end\n\n", SpanType.CODE);
	}

	@Test
	public void testSpanDeleted() {
		assertSpan("begin -span text- end\n\n", SpanType.DELETED);
	}

	@Test
	public void testSpanEmphasis() {
		assertSpan("begin _span text_ end\n\n", SpanType.EMPHASIS);
	}

	@Test
	public void testSpanMark() {
		assertSpan("begin _span text_ end\n\n", SpanType.MARK);
	}

	@Test
	public void testSpanInserted() {
		assertSpan("begin +span text+ end\n\n", SpanType.INSERTED);
	}

	@Test
	public void testSpanItalic() {
		assertSpan("begin __span text__ end\n\n", SpanType.ITALIC);
	}

	@Test
	public void testSpanMonospace() {
		assertSpan("begin %{font-family:monospace;}span text% end\n\n", SpanType.MONOSPACE);
	}

	@Test
	public void testSpanQuote() {
		assertSpan("begin %span text% end\n\n", SpanType.QUOTE);
	}

	@Test
	public void testSpanSpan() {
		assertSpan("begin %span text% end\n\n", SpanType.SPAN);
	}

	@Test
	public void testSpanStrong() {
		assertSpan("begin *span text* end\n\n", SpanType.STRONG);
	}

	@Test
	public void testSpanUnderlined() {
		assertSpan("begin %{text-decoration:underline;}span text% end\n\n", SpanType.UNDERLINED);
	}

	private void assertSpan(String expected, SpanType spanType) {
		builder.beginDocument();
		builder.characters("begin");
		builder.beginSpan(spanType, new Attributes());
		builder.characters("span text");
		builder.endSpan();
		builder.characters("end");
		builder.endDocument();

		String markup = out.toString();

		assertEquals(expected, markup);
	}
}
