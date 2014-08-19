/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.textile.core;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.tests.TestUtil;

/**
 * @author David Green
 * @see TextileDocumentBuilder
 */
public class TextileDocumentBuilderTest extends TestCase {

	private static final String[] PLATFORM_NEWLINES = new String[] {//
	"\r\n", // Windows
			"\r", // Mac
			"\n", // Unix, Linux
	};

	private TextileDocumentBuilder builder;

	private StringWriter out;

	@Override
	protected void setUp() throws Exception {
		out = new StringWriter();
		builder = new TextileDocumentBuilder(out);
		super.setUp();
	}

	public void testParagraph_MultipleNewlinesInParagraph() throws Exception {
		for (String newline : PLATFORM_NEWLINES) {
			setUp();
			builder.beginDocument();
			builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
			builder.characters("text" + newline + newline + "more text");
			builder.endBlock();
			builder.endDocument();

			String markup = out.toString();

			TestUtil.println(markup);

			assertEquals("text more text\n\n", markup);
		}
	}

	public void testParagraph_MultipleNewlinesInImplicitParagraph() throws Exception {
		for (String newline : PLATFORM_NEWLINES) {
			setUp();
			builder.beginDocument();
			builder.characters("a" + newline + newline + "b");
			builder.endDocument();

			String markup = out.toString();

			TestUtil.println(markup);

			assertEquals("a b\n\n", markup);
		}
	}

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

		TestUtil.println(markup);

		assertEquals("text\nmore text\n\n", markup);
	}

	public void testParagraph_MultipleLineBreaksInImplicitParagraph() {
		builder.beginDocument();
		builder.characters("text");
		builder.lineBreak();
		builder.lineBreak();
		builder.characters("more text");
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("text\nmore text\n\n", markup);
	}

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

		TestUtil.println(markup);

		assertEquals("text\n more\n\tmore2 text\n\n", markup);
	}

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

		TestUtil.println(markup);

		assertEquals("first paragraph\n\nsecond paragraph\n\n", markup);
	}

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

		TestUtil.println(markup);

		assertEquals("some *bold* and _emphasis_\n\n", markup);
	}

	public void testParagraphWithCssClass() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes(null, "test", null, null));
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("p(test). text more text\n\n", markup);
	}

	public void testParagraphWithCssStyle() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes(null, null, "x-test: foo;", null));
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("p{x-test: foo;}. text more text\n\n", markup);
	}

	public void testParagraphWithId() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes("123", null, null, null));
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("p(#123). text more text\n\n", markup);
	}

	public void testParagraphWithIdAndClass() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes("123", "test", null, null));
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("p(test#123). text more text\n\n", markup);
	}

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

		TestUtil.println(markup);

		assertEquals("text more text \"baz\":http://example.com/foo+bar/baz.gif test\n\n", markup);
	}

	public void testBlockCode() {
		builder.beginDocument();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("bc.. text\n\nmore text\n\n\n", markup);
	}

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

		TestUtil.println(markup);

		assertEquals("bc.. text\n\nmore text\n\n\np. text\n\ntext2\n\n", markup);
	}

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

		TestUtil.println(markup);

		assertEquals("h1. text more text\n\ntext\n\n", markup);
	}

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

		TestUtil.println(markup);

		assertEquals("h1. text _emphasized_\n\ntext\n\n", markup);
	}

	public void testHeading1_WithAttributes() {
		String markup = doHeadingAttributesTest();

		assertEquals("h1(classTest#idTest). text more text\n\n", markup);
	}

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

		TestUtil.println(markup);
		return markup;
	}

	public void testImplicitParagrah() {
		builder.beginDocument();
		builder.characters("text1");
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text2");
		builder.endBlock();
		builder.characters("text3");
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("text1\n\ntext2\n\ntext3\n\n", markup);
	}

	public void testImplicitParagrahWithSpan() {
		builder.beginDocument();
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text1");
		builder.endSpan();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text2");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("*text1*\n\ntext2\n\n", markup);
	}

	public void testBoldSpanNoWhitespace_spanAtLineStart() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text2");
		builder.endSpan();
		builder.characters("text3");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("*text2* text3\n\n", markup);
	}

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

		TestUtil.println(markup);

		assertEquals("text3 *text2*\n\n", markup);
	}

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

		TestUtil.println(markup);

		assertEquals("text3 *text2* text4\n\n", markup);
	}

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

		TestUtil.println(markup);

		assertEquals("*text2* __text3__\n\n", markup);
	}

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

		TestUtil.println(markup);

		assertEquals("*text2*!\n\n", markup);
	}

	public void testBulletedList() {
		builder.beginDocument();
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("text2");
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text3");
		builder.endSpan();
		builder.endBlock();

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("text4");
		builder.endBlock();

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("* text2 *text3*\n* text4\n\n", markup);
	}

	public void testBulletedList_TwoLevels() {
		builder.beginDocument();
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("text2");
		builder.endSpan();

		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("text3");
		builder.endBlock();

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("text4");
		builder.endBlock();

		builder.endBlock();

		builder.endBlock();

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("* text2\n** text3\n** text4\n\n\n", markup);
	}

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

		TestUtil.println(markup);

		assertEquals("prefix *bolded* suffix\n\n", markup);
	}

	public void testEmptySpan() {
		builder.beginDocument();

		builder.characters("prefix ");

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.endBlock();

		builder.characters(" suffix");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("prefix suffix\n\n", markup);
	}

	public void testMonospaceSpan() {
		builder.beginDocument();

		builder.characters("prefix ");

		builder.beginSpan(SpanType.MONOSPACE, new Attributes());
		builder.characters("text");
		builder.endSpan();

		builder.characters(" suffix");

		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("prefix %{font-family:monospace;}text% suffix\n\n", markup);
	}

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

		TestUtil.println(markup);

		assertEquals("|content| |\n\n", markup);
	}

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

		TestUtil.println(markup);

		assertEquals("test\n\nmore *text*\n# text2\n\n", markup);
	}

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

		builder.endBlock(); // cell

		builder.endBlock(); // row

		builder.endBlock(); // table

		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("|first|content|\n\n", markup);
	}

	public void testEmptyBoldSpan() {
		builder.beginDocument();

		builder.characters("first ");

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.endSpan();

		builder.characters("second");

		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("first second\n\n", markup);
	}

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

		TestUtil.println(markup);

		assertEquals("%{color:blue;}first second%\n\n", markup);
	}

	public void testLineBreak() {
		builder.beginDocument();
		builder.characters("line");
		builder.lineBreak();
		builder.characters("break");
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("line\nbreak\n\n", markup);
	}

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

		TestUtil.println(markup);

		assertEquals("bc.. one\n\n\ntwo\n\n\np. line\nbreak\n\n", markup);
	}

	public void testLineBreakInFootnote() {
		builder.beginDocument();
		builder.beginBlock(BlockType.FOOTNOTE, new Attributes());
		builder.characters("line");
		builder.lineBreak();
		builder.characters("break");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("fn1. line\nbreak\n\n", markup);
	}

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

		TestUtil.println(markup);

		assertEquals("pre.. line\n\nbreak\n\n\n", markup);
	}

	public void testLineBreakInPreformatted() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
		builder.characters("line");
		builder.lineBreak();
		builder.characters("break");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("pre. line\nbreak\n\n", markup);
	}

	public void testLink() {
		builder.beginDocument();

		builder.characters("a ");
		builder.link(new LinkAttributes(), "#foo", "link to foo");
		builder.characters(" test");

		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("a \"link to foo\":#foo test\n\n", markup);
	}

	public void testImageLink() {
		builder.beginDocument();

		builder.characters("a ");
		builder.imageLink(new LinkAttributes(), new ImageAttributes(), "#foo", "fooImage.png");
		builder.characters(" test");

		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("a !fooImage.png!:#foo test\n\n", markup);
	}

	public void testImage() {
		builder.beginDocument();

		builder.characters("a ");
		builder.image(new ImageAttributes(), "fooImage.png");
		builder.characters(" test");

		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("a !fooImage.png! test\n\n", markup);
	}

	public void testEntityReference() {
		builder.beginDocument();

		builder.characters("a ");
		builder.entityReference("copy");
		builder.characters(" test");

		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("a (c) test\n\n", markup);
	}

	public void testEntityCopyright() {
		builder.beginDocument();

		builder.characters("a \u00A9 test");

		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("a (c) test\n\n", markup);
	}

	public void testEntityReg() {
		builder.beginDocument();

		builder.characters("a \u00AE test");

		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("a (r) test\n\n", markup);
	}

	public void testNonBreakingSpace() {
		builder.beginDocument();

		builder.characters("a \u00A0 test");

		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("a test\n\n", markup);
	}

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
		TestUtil.println(markup);

		assertEquals("bc. // some code\n\n-redacted- text\n\n", markup);
	}
}
