/*******************************************************************************
 * Copyright (c) 2011, 2013 Tasktop Technologies
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.mediawiki.core;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.wikitext.confluence.core.ConfluenceDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.tests.TestUtil;

/**
 * @author David Green
 * @see ConfluenceDocumentBuilder
 */
public class ConfluenceDocumentBuilderTest extends TestCase {

	private DocumentBuilder builder;

	private StringWriter out;

	@Override
	protected void setUp() throws Exception {
		out = new StringWriter();
		builder = new ConfluenceDocumentBuilder(out);
		super.setUp();
	}

	public void testParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("text  more text\n\n", markup);
	}

	public void testParagraphWithCssClass() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes(null, "test", null, null));
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("text  more text\n\n", markup);
	}

	public void testParagraphWithCssStyle() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes(null, null, "x-test: foo;", null));
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("text  more text\n\n", markup);
	}

	public void testParagraphWithId() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes("123", null, null, null));
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("text  more text\n\n", markup);
	}

	public void testParagraphWithIdAndClass() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes("123", "test", null, null));
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("text  more text\n\n", markup);
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

		assertEquals("text  more text [baz | http://example.com/foo+bar/baz.gif] test\n\n", markup);
	}

	public void testBlockCode() {
		builder.beginDocument();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("{code}text\n\nmore text{code}\n\n\n", markup);
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

		assertEquals("{code}text\n\nmore text{code}\n\n\ntext\n\ntext2\n\n", markup);
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

		assertEquals("h1. text  more text\n\ntext\n\n", markup);
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

		assertEquals("*text2* _text3_\n\n", markup);
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

		assertEquals("prefix  suffix\n\n", markup);
	}

	public void testMonospaceSpan() {
		builder.beginDocument();

		builder.characters("prefix ");

		builder.beginSpan(SpanType.MONOSPACE, new Attributes());
		builder.characters("test");
		builder.endSpan();

		builder.characters(" suffix");

		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("prefix {{test}} suffix\n\n", markup);
	}

	public void testLinkSpan() {
		builder.beginDocument();

		builder.characters("prefix ");

		LinkAttributes attributes = new LinkAttributes();
		attributes.setHref("http://example.com/target");
		builder.beginSpan(SpanType.LINK, attributes);
		builder.characters("test");
		builder.endSpan();

		builder.characters(" suffix");

		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("prefix [test | http://example.com/target] suffix\n\n", markup);
	}

	public void testLinkSpanWithTitle() {
		builder.beginDocument();

		builder.characters("prefix ");

		LinkAttributes attributes = new LinkAttributes();
		attributes.setHref("http://example.com/target");
		attributes.setTitle("Title Words");
		builder.beginSpan(SpanType.LINK, attributes);
		builder.characters("test");
		builder.endSpan();

		builder.characters(" suffix");

		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("prefix [test | http://example.com/target | Title Words] suffix\n\n", markup);
	}

	public void testLinkSpanNoText() {
		builder.beginDocument();

		builder.characters("prefix ");

		LinkAttributes attributes = new LinkAttributes();
		attributes.setHref("http://example.com/target");
		builder.beginSpan(SpanType.LINK, attributes);
		builder.endSpan();

		builder.characters(" suffix");

		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("prefix [http://example.com/target] suffix\n\n", markup);
	}

	public void testLinkAlternate() {
		builder.beginDocument();

		builder.characters("prefix ");

		builder.link("#test", "Test 123");

		builder.characters(" suffix");

		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("prefix [Test 123 | #test] suffix\n\n", markup);
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

	public void testItalics() {
		builder.beginDocument();

		builder.characters("prefix ");

		builder.beginSpan(SpanType.ITALIC, new Attributes());
		builder.characters("italic phrase");
		builder.endBlock();

		builder.characters(" suffix");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("prefix _italic phrase_ suffix\n\n", markup);
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

	public void testParagraphWithSingleNewline() {
		builder.beginDocument();

		builder.characters("first");
		builder.lineBreak();
		builder.characters("second");

		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("first\nsecond\n\n", markup);
	}

	public void testParagraphWithMultipleNewlines() {
		builder.beginDocument();

		builder.characters("first");
		builder.lineBreak();
		builder.lineBreak();
		builder.lineBreak();
		builder.characters("second");

		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("first\n\\\\\\\\second\n\n", markup);
	}

	public void testListItemWithSingleNewline() {
		builder.beginDocument();

		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("first");
		builder.lineBreak();
		builder.characters("second");
		builder.endBlock(); // list item

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("another");
		builder.endBlock(); // list item

		builder.endBlock(); // list

		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("* first\nsecond\n* another\n\n", markup);
	}

	public void testListItemWithMultipleNewlines() {
		builder.beginDocument();

		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("first");
		builder.lineBreak();
		builder.lineBreak();
		builder.characters("second");
		builder.endBlock(); // list item

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("another");
		builder.endBlock(); // list item

		builder.endBlock(); // list

		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("* first\n\\\\second\n* another\n\n", markup);
	}

	public void testPreformattedWithMultipleNewlines() {
		builder.beginDocument();

		builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
		builder.characters("first");
		builder.lineBreak();
		builder.lineBreak();
		builder.characters("second");
		builder.endBlock(); // pre

		builder.endDocument();

		String markup = out.toString();

		TestUtil.println(markup);

		assertEquals("{noformat}first\n\nsecond{noformat}\n\n\n", markup);
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

		assertEquals("{code}// some code{code}\n\n-redacted- text\n\n", markup);
	}
}
