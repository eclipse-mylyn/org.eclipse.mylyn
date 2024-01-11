/*******************************************************************************
 * Copyright (c) 2011, 2024 Tasktop Technologies and others.
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

package org.eclipse.mylyn.wikitext.confluence.internal.tests;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.confluence.internal.ConfluenceDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.junit.Test;

/**
 * @author David Green
 * @see ConfluenceDocumentBuilder
 */
public class ConfluenceDocumentBuilderTest {

	private final StringWriter out = new StringWriter();

	private final DocumentBuilder builder = new ConfluenceDocumentBuilder(out);

	@Test
	public void paragraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text  more text\n\n", markup);
	}

	@Test
	public void paragraphWithCssClass() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes(null, "test", null, null));
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text  more text\n\n", markup);
	}

	@Test
	public void paragraphWithCssStyle() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes(null, null, "x-test: foo;", null));
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text  more text\n\n", markup);
	}

	@Test
	public void paragraphWithId() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes("123", null, null, null));
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text  more text\n\n", markup);
	}

	@Test
	public void paragraphWithIdAndClass() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes("123", "test", null, null));
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text  more text\n\n", markup);
	}

	@Test
	public void paragraphWithLink() {
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

		assertEquals("text  more text [baz | http://example.com/foo+bar/baz.gif] test\n\n", markup);
	}

	@Test
	public void blockCode() {
		builder.beginDocument();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("{code}text\n\nmore text{code}\n\n\n", markup);
	}

	@Test
	public void codeBlockWithLineBreaks() {
		builder.beginDocument();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("line 1");
		builder.lineBreak();
		builder.characters("line 2");
		builder.lineBreak();
		builder.lineBreak();
		builder.characters("line 3");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("{code}line 1\nline 2\n\nline 3{code}\n\n\n", markup);
	}

	@Test
	public void codeBlockCurlyBraceContent() {
		assertCodeBlock("{code}{something}{code}\n\n", "{something}");
	}

	@Test
	public void codeBlockSquareBraceContent() {
		assertCodeBlock("{code}[something]{code}\n\n", "[something]");
	}

	@Test
	public void preformattedBlockWithLineBreaks() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
		builder.characters("line 1");
		builder.lineBreak();
		builder.characters("line 2");
		builder.lineBreak();
		builder.lineBreak();
		builder.characters("line 3");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("{noformat}line 1\nline 2\n\nline 3{noformat}\n\n", markup);
	}

	@Test
	public void preformattedBlockWithCurlyBraceContent() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
		builder.characters("{somecontent}");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("{noformat}{somecontent}{noformat}\n\n", markup);
	}

	@Test
	public void paragraphFollowingExtendedBlockCode() {
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

		assertEquals("{code}text\n\nmore text{code}\n\n\ntext\n\ntext2\n\n", markup);
	}

	@Test
	public void heading1() {
		builder.beginDocument();
		builder.beginHeading(1, new Attributes());
		builder.characters("text\n\nmore text");
		builder.endHeading();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("h1. text  more text\n\ntext\n\n", markup);
	}

	@Test
	public void headingWithLineBreaks() {
		builder.beginDocument();
		builder.beginHeading(1, new Attributes());
		builder.characters("line 1 of heading");
		builder.lineBreak();
		builder.characters("line 2 of heading");
		builder.endHeading();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("h1. line 1 of heading\\\\line 2 of heading\n\ntext\n\n", markup);
	}

	@Test
	public void heading1_WithNestedMarkup() {
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
	public void implicitParagrah() {
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
	public void boldSpanNoWhitespace_spanAtLineStart() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text2");
		builder.endSpan();
		builder.characters("text3");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("*text2* text3\n\n", markup);
	}

	@Test
	public void boldSpanNoWhitespace_spanAtLineEnd() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());

		builder.characters("text3");
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text2");
		builder.endSpan();

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text3 *text2*\n\n", markup);
	}

	@Test
	public void boldSpanNoWhitespace_spanMidLine() {
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

		assertEquals("text3 *text2* text4\n\n", markup);
	}

	@Test
	public void boldSpanNoWhitespace_adjacentSpans() {
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

		assertEquals("*text2*_text3_\n\n", markup);
	}

	@Test
	public void spanWithAdjacentWhitespace() {
		builder.beginDocument();

		builder.characters("prefix ");

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("bolded");
		builder.endBlock();

		builder.characters(" suffix");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix *bolded* suffix\n\n", markup);
	}

	@Test
	public void boldSpanWithAdjacentPunctuation() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("bold");
		builder.endSpan();
		builder.characters(".");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("*bold*.\n\n", markup);
	}

	@Test
	public void boldSpanWithEscapedExclamation() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("bold");
		builder.endSpan();
		builder.characters("!");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("*bold*\\!\n\n", markup);
	}

	@Test
	public void boldSpanWithEscapedBrackets() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("bold");
		builder.endSpan();
		builder.characters("{");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("*bold*\\{\n\n", markup);
	}

	@Test
	public void boldSpanWithEscapedPipe() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("bold");
		builder.endSpan();
		builder.characters("|");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("*bold*\\|\n\n", markup);
	}

	@Test
	public void emptySpan() {
		builder.beginDocument();

		builder.characters("prefix ");

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.endBlock();

		builder.characters(" suffix");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix  suffix\n\n", markup);
	}

	@Test
	public void monospaceSpan() {
		builder.beginDocument();

		builder.characters("prefix ");

		builder.beginSpan(SpanType.MONOSPACE, new Attributes());
		builder.characters("test");
		builder.endSpan();

		builder.characters(" suffix");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix {{test}} suffix\n\n", markup);
	}

	@Test
	public void monospaceSpanWithCurlyBraceContent() {
		builder.beginDocument();

		builder.characters("prefix ");

		builder.beginSpan(SpanType.MONOSPACE, new Attributes());
		builder.characters("{test}");
		builder.endSpan();

		builder.characters(" suffix");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix {{\\{test}}} suffix\n\n", markup);
	}

	@Test
	public void linkSpan() {
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

		assertEquals("prefix [test | http://example.com/target] suffix\n\n", markup);
	}

	@Test
	public void linkSpanWithAdjacentSpans() {
		builder.beginDocument();

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("bold");
		builder.endSpan();

		LinkAttributes attributes = new LinkAttributes();
		attributes.setHref("http://example.com/target");
		builder.beginSpan(SpanType.LINK, attributes);
		builder.characters("test");
		builder.endSpan();

		builder.beginSpan(SpanType.ITALIC, new Attributes());
		builder.characters("italic");
		builder.endSpan();

		builder.endDocument();

		String markup = out.toString();

		assertEquals("*bold*[test | http://example.com/target]_italic_\n\n", markup);
	}

	@Test
	public void linkSpanWithTitle() {
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

		assertEquals("prefix [test | http://example.com/target | Title Words] suffix\n\n", markup);
	}

	@Test
	public void linkSpanNoText() {
		builder.beginDocument();

		builder.characters("prefix ");

		LinkAttributes attributes = new LinkAttributes();
		attributes.setHref("http://example.com/target");
		builder.beginSpan(SpanType.LINK, attributes);
		builder.endSpan();

		builder.characters(" suffix");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix [http://example.com/target] suffix\n\n", markup);
	}

	@Test
	public void linkSpanWithSpecialCharacters() {
		builder.beginDocument();

		LinkAttributes attributes = new LinkAttributes();
		attributes.setHref("http://example.com/target");
		attributes.setTitle("Title Words");
		builder.beginSpan(SpanType.LINK, attributes);
		builder.characters("[This][Is] a test");
		builder.endSpan();

		builder.endDocument();

		assertEquals("[\\[This\\]\\[Is\\] a test | http://example.com/target | Title Words]\n\n", out.toString());
	}

	@Test
	public void linkAlternate() {
		builder.beginDocument();

		builder.characters("prefix ");

		builder.link("#test", "Test 123");

		builder.characters(" suffix");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix [Test 123 | #test] suffix\n\n", markup);
	}

	@Test
	public void linkSpanNoHref() {
		builder.beginDocument();

		builder.characters("prefix ");

		LinkAttributes attributes = new LinkAttributes();
		builder.beginSpan(SpanType.LINK, attributes);
		builder.endSpan();

		builder.characters(" suffix");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix [] suffix\n\n", markup);
	}

	@Test
	public void linkSpanWithSurroundingItalics() {
		builder.beginDocument();

		builder.beginSpan(SpanType.ITALIC, new Attributes());
		builder.characters("prefix ");

		LinkAttributes attributes = new LinkAttributes();
		attributes.setHref("http://example.com/target");
		builder.beginSpan(SpanType.LINK, attributes);
		builder.characters("text");
		builder.endSpan();

		builder.endSpan();

		builder.endDocument();

		assertEquals("_prefix [text | http://example.com/target]_\n\n", out.toString());
	}

	@Test
	public void tableWithEmptyCells() {
		assertTableRow("| |content| |\n\n", BlockType.TABLE_CELL_NORMAL);
	}

	@Test
	public void tableWithEmptyHeaderCells() {
		assertTableRow("|| ||content|| |\n\n", BlockType.TABLE_CELL_HEADER);
	}

	@Test
	public void tableWithCellsContainingBulletedList() {
		assertTableRow("|| ||* first\n* second|| |\n\n", BlockType.TABLE_CELL_HEADER, this::emitMultiItemBulletedList);
	}

	@Test
	public void tableWithCellsContainingNumericList() {
		assertTableRow("| |# first\n# second| |\n\n", BlockType.TABLE_CELL_NORMAL, this::emitMultiItemNumericList);
	}

	@Test
	public void tableWithEmptyLineBreaks() {
		// In the case of an empty line break in a Table cell Jira expects an 'NO-BREAK SPACE' character
		assertTableRow("| |abc\n\u00A0\ndef| |\n\n", BlockType.TABLE_CELL_NORMAL, () -> {
			builder.characters("abc");
			builder.lineBreak();
			builder.lineBreak();
			builder.characters("def");
		});
	}

	@Test
	public void tableWithMultipleLineBreaks() {
		assertTableRow("| |a\nb\nc| |\n\n", BlockType.TABLE_CELL_NORMAL, () -> {
			builder.characters("a");
			builder.lineBreak();
			builder.characters("b");
			builder.lineBreak();
			builder.characters("c");
		});
	}

	@Test
	public void tableWithWhitespaceAndNewlinesInContent() {
		assertTableRow("| |abc  def| |\n\n", BlockType.TABLE_CELL_NORMAL, () -> {
			builder.characters(" \n\nabc\n\ndef");
		});
	}

	@Test
	public void tableWithParagraphs() {
		assertTableRow("| |abc\ndef| |\n\n", BlockType.TABLE_CELL_NORMAL, () -> {
			builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
			builder.characters("abc");
			builder.endBlock();
			builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
			builder.endBlock();
			builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
			builder.characters("def");
			builder.endBlock();
		});
	}

	@Test
	public void nestTablesAfterBlockQuote() {
		builder.beginDocument();
		builder.beginBlock(BlockType.QUOTE, new Attributes());
		builder.beginBlock(BlockType.TABLE, new Attributes());
		builder.beginBlock(BlockType.TABLE_ROW, new Attributes());
		builder.beginBlock(BlockType.TABLE_CELL_NORMAL, new Attributes());
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		emitListItemHavingParagraphAndContent("first");
		emitListItemHavingParagraphAndContent("second");
		builder.endBlock();
		builder.endBlock();
		builder.endBlock();
		builder.endBlock();
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("{quote}|* first\n* second|\n\n{quote}\n\n", markup);
	}

	@Test
	public void tableWithNestedList() {
		assertTableRow("| |* first\n" + //
				"* second| |\n\n", //
				BlockType.TABLE_CELL_NORMAL, () -> {
					builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
					emitListItemHavingParagraphAndContent("first");
					emitListItemHavingParagraphAndContent("second");
					builder.endBlock();
				});
	}

	@Test
	public void divAfterImplicitParagraph() {
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

		assertEquals("test\n\nmore *text*\n\n# text2\n", markup);
	}

	@Test
	public void divWithinTableCell() {
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

		assertEquals("|first|content content2|\n\n", markup);
	}

	@Test
	public void italics() {
		builder.beginDocument();

		builder.characters("prefix ");

		builder.beginSpan(SpanType.ITALIC, new Attributes());
		builder.characters("italic phrase");
		builder.endBlock();

		builder.characters(" suffix");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix _italic phrase_ suffix\n\n", markup);
	}

	@Test
	public void mark() {
		builder.beginDocument();

		builder.characters("prefix ");

		builder.beginSpan(SpanType.MARK, new Attributes());
		builder.characters("italic phrase");
		builder.endBlock();

		builder.characters(" suffix");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix _italic phrase_ suffix\n\n", markup);
	}

	@Test
	public void emptyBoldSpan() {
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
	public void paragraphWithSingleNewline() {
		builder.beginDocument();

		builder.characters("first");
		builder.lineBreak();
		builder.characters("second");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("first\nsecond\n\n", markup);
	}

	@Test
	public void paragraphWithMultipleNewlines() {
		builder.beginDocument();

		builder.characters("first");
		builder.lineBreak();
		builder.lineBreak();
		builder.lineBreak();
		builder.characters("second");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("first\n\\\\ \\\\second\n\n", markup);
	}

	@Test
	public void textBetweenSingleLineBreaksDoesNotUseDoubleSlashLineBreak() {
		builder.beginDocument();

		builder.characters("first");
		builder.lineBreak();
		builder.characters("second");
		builder.lineBreak();
		builder.characters("third");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("first\nsecond\nthird\n\n", markup);
	}

	@Test
	public void paragraphWithSingleNewLineAndEscapedContent() {
		builder.beginDocument();

		builder.characters("first");
		builder.lineBreak();
		builder.characters("[second]");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("first\n\\[second\\]\n\n", markup);
	}

	@Test
	public void paragraphWithMultipleNewlinesAndEscapedContent() {
		builder.beginDocument();

		builder.characters("first");
		builder.lineBreak();
		builder.lineBreak();
		builder.lineBreak();
		builder.characters("[second]");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("first\n\\\\ \\\\ \\[second\\]\n\n", markup);
	}

	@Test
	public void paragraphWithCurlyBraceContent() {
		assertParagraphWithContent("\\{something}\n\n", "{something}");
	}

	@Test
	public void paragraphWithSquareBraceContent() {
		assertParagraphWithContent("\\[something\\]\n\n", "[something]");
	}

	@Test
	public void paragraphWithSlashContent() {
		assertParagraphWithContent("here is &#92; a slash\n\n", "here is \\ a slash");
	}

	@Test
	public void listItemWithSingleNewline() {
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

		assertEquals("* first\\\\second\n* another\n", markup);
	}

	@Test
	public void listItemWithMultipleNewlines() {
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

		assertEquals("* first\\\\ \\\\second\n* another\n", markup);
	}

	@Test
	public void listWithMultipleItems() {
		builder.beginDocument();
		emitMultiItemBulletedList();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("* first\n* second\n", markup);
	}

	@Test
	public void listItemWithSingleParagraph() {
		builder.beginDocument();

		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());

		emitListItemHavingParagraphAndContent("first");

		builder.endBlock(); // list

		builder.endDocument();

		assertEquals("* first\n", out.toString());
	}

	@Test
	public void listItemWithMultipleParagraphs() {
		builder.beginDocument();

		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());

		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("first");
		builder.endBlock(); // paragraph

		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("second");
		builder.endBlock(); // paragraph

		builder.endBlock(); // list item
		builder.endBlock(); // list

		builder.endDocument();

		assertEquals("* first\nsecond\n", out.toString());
	}

	@Test
	public void listItemWithMultipleMixedParagraphs() {
		builder.beginDocument();

		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("first");

		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("second");
		builder.endBlock(); // paragraph

		builder.characters("third");

		builder.endBlock(); // list item
		builder.endBlock(); // list

		builder.endDocument();

		assertEquals("* first\nsecond\nthird\n", out.toString());
	}

	private void emitMultiItemBulletedList() {
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());

		emitListItem("first");
		emitListItem("second");

		builder.endBlock();
	}

	private void emitMultiItemNumericList() {
		builder.beginBlock(BlockType.NUMERIC_LIST, new Attributes());

		emitListItem("first");
		emitListItem("second");

		builder.endBlock();
	}

	private void emitListItem(String text) {
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters(text);
		builder.endBlock(); // list item
	}

	@Test
	public void listWithNestedSublist() {
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
	public void listWithNestedSublist2() {
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
	public void listWithNestedListsWihoutListItem() {
		builder.beginDocument();

		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());

		emitListItemHavingParagraphAndContent("content 1");

		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		emitListItemHavingParagraphAndContent("content 2");
		builder.endBlock(); // list
		builder.endBlock(); // list

		builder.endBlock(); // list

		builder.endDocument();
		assertEquals("* content 1\n" + //
				"*** content 2\n", out.toString());
	}

	@Test
	public void preformattedWithMultipleNewlines() {
		builder.beginDocument();

		builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
		builder.characters("first");
		builder.lineBreak();
		builder.lineBreak();
		builder.characters("second");
		builder.endBlock(); // pre

		builder.endDocument();

		String markup = out.toString();

		assertEquals("{noformat}first\n\nsecond{noformat}\n\n", markup);
	}

	@Test
	public void implicitParagrahWithSpan() {
		builder.beginDocument();
		builder.beginSpan(SpanType.BOLD, new Attributes());
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
	public void spanOpensImplicitParagraph() {
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

		assertEquals("{code}// some code{code}\n\n-redacted- text\n\n", markup);
	}

	@Test
	public void blockQuote() {
		builder.beginDocument();
		builder.beginBlock(BlockType.QUOTE, new Attributes());
		builder.characters("block text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("{quote}block text{quote}\n\n", markup);
	}

	@Test
	public void blockQuoteContainingMarkup() {
		builder.beginDocument();
		builder.beginBlock(BlockType.QUOTE, new Attributes());
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("bold");
		builder.endSpan();
		builder.characters("text");
		builder.beginSpan(SpanType.ITALIC, new Attributes());
		builder.characters("italic");
		builder.endSpan();
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("{quote}*bold* text _italic_{quote}\n\n", markup);
	}

	@Test
	public void citationAfterBlockQuote() {
		builder.beginDocument();
		builder.beginBlock(BlockType.QUOTE, new Attributes());
		builder.beginSpan(SpanType.CITATION, new Attributes());
		builder.characters("citation");
		builder.endSpan();
		builder.endBlock();
		builder.beginSpan(SpanType.CITATION, new Attributes());
		builder.characters("citation");
		builder.endSpan();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("{quote}??citation??{quote}\n\n??citation??\n\n", markup);
	}

	@Test
	public void imageLink() {
		builder.beginDocument();
		builder.characters("a ");
		builder.imageLink(new LinkAttributes(), new ImageAttributes(), "#foo", "fooImage.png");
		builder.characters(" test");
		builder.endDocument();

		String markup = out.toString();

		assertEquals("a !fooImage.png!:#foo test\n\n", markup);
	}

	@Test
	public void image() {
		builder.beginDocument();
		builder.characters("a ");
		builder.image(new ImageAttributes(), "fooImage.png");
		builder.characters(" test");
		builder.endDocument();

		String markup = out.toString();

		assertEquals("a !fooImage.png! test\n\n", markup);
	}

	@Test
	public void imageWithAlt() {
		builder.beginDocument();
		builder.characters("a ");
		ImageAttributes attributes = new ImageAttributes();
		attributes.setAlt("a value");
		builder.image(attributes, "fooImage.png");
		builder.characters(" test");
		builder.endDocument();

		String markup = out.toString();

		assertEquals("a !fooImage.png|alt=\"a value\"! test\n\n", markup);
	}

	@Test
	public void imageWithTitle() {
		builder.beginDocument();
		builder.characters("a ");
		ImageAttributes attributes = new ImageAttributes();
		attributes.setTitle("a value");
		builder.image(attributes, "fooImage.png");
		builder.characters(" test");
		builder.endDocument();

		String markup = out.toString();

		assertEquals("a !fooImage.png|title=\"a value\"! test\n\n", markup);
	}

	@Test
	public void imageWithAltAndTitle() {
		builder.beginDocument();
		builder.characters("a ");
		ImageAttributes attributes = new ImageAttributes();
		attributes.setAlt("first value");
		attributes.setTitle("second value");
		builder.image(attributes, "fooImage.png");
		builder.characters(" test");
		builder.endDocument();

		String markup = out.toString();

		assertEquals("a !fooImage.png|alt=\"first value\",title=\"second value\"! test\n\n", markup);
	}

	@Test
	public void imageNoUrl() {
		builder.beginDocument();
		builder.characters("a ");
		builder.image(new ImageAttributes(), null);
		builder.characters(" test");
		builder.endDocument();

		String markup = out.toString();

		assertEquals("a  test\n\n", markup);
	}

	@Test
	public void spanSuperscript() {
		assertSpan("begin ^span text^ end\n\n", SpanType.SUPERSCRIPT);
	}

	@Test
	public void spanSubscript() {
		assertSpan("begin ~span text~ end\n\n", SpanType.SUBSCRIPT);
	}

	@Test
	public void spanBold() {
		assertSpan("begin *span text* end\n\n", SpanType.BOLD);
	}

	@Test
	public void spanCitation() {
		assertSpan("begin ??span text?? end\n\n", SpanType.CITATION);
	}

	@Test
	public void spanCode() {
		assertSpan("begin @span text@ end\n\n", SpanType.CODE);
	}

	@Test
	public void spanDeleted() {
		assertSpan("begin -span text- end\n\n", SpanType.DELETED);
	}

	@Test
	public void spanEmphasis() {
		assertSpan("begin _span text_ end\n\n", SpanType.EMPHASIS);
	}

	@Test
	public void spanInserted() {
		assertSpan("begin +span text+ end\n\n", SpanType.INSERTED);
	}

	@Test
	public void spanItalic() {
		assertSpan("begin _span text_ end\n\n", SpanType.ITALIC);
	}

	@Test
	public void spanMonospace() {
		assertSpan("begin {{span text}} end\n\n", SpanType.MONOSPACE);
	}

	@Test
	public void spanQuote() {
		assertSpan("beginspan textend\n\n", SpanType.QUOTE);
	}

	@Test
	public void spanSpan() {
		assertSpan("beginspan textend\n\n", SpanType.SPAN);
	}

	@Test
	public void spanStrong() {
		assertSpan("begin *span text* end\n\n", SpanType.STRONG);
	}

	@Test
	public void spanUnderlined() {
		assertSpan("begin +span text+ end\n\n", SpanType.UNDERLINED);
	}

	@Test
	public void nestedBoldItalic() {
		assertNestedSpan("begin *_span_* end\n\n", SpanType.BOLD, SpanType.ITALIC);
	}

	@Test
	public void nestedItalicBold() {
		assertNestedSpan("begin _*span*_ end\n\n", SpanType.ITALIC, SpanType.BOLD);
	}

	@Test
	public void nestedDeletedSuperscript() {
		assertNestedSpan("begin -^span^- end\n\n", SpanType.DELETED, SpanType.SUPERSCRIPT);
	}

	@Test
	public void nestedSuperscriptDeleted() {
		assertNestedSpan("begin ^-span-^ end\n\n", SpanType.SUPERSCRIPT, SpanType.DELETED);
	}

	@Test
	public void nestedUnderlineSubscript() {
		assertNestedSpan("begin +~span~+ end\n\n", SpanType.UNDERLINED, SpanType.SUBSCRIPT);
	}

	@Test
	public void nestedSubscriptUnderline() {
		assertNestedSpan("begin ~+span+~ end\n\n", SpanType.SUBSCRIPT, SpanType.UNDERLINED);
	}

	@Test
	public void nestedMonospaceCitation() {
		assertNestedSpan("begin {{??span??}} end\n\n", SpanType.MONOSPACE, SpanType.CITATION);
	}

	@Test
	public void nestedCitationMonospace() {
		assertNestedSpan("begin ??{{span}}?? end\n\n", SpanType.CITATION, SpanType.MONOSPACE);
	}

	@Test
	public void nestedLinkCode() {
		assertNestedSpan("begin %@span@% end\n\n", SpanType.LINK, SpanType.CODE);
	}

	@Test
	public void nestedCodeLink() {
		assertNestedSpan("begin @%span%@ end\n\n", SpanType.CODE, SpanType.LINK);
	}

	@Test
	public void adjacentBoldItalic() {
		assertAdjacentSpan("begin *left*_right_ end\n\n", SpanType.BOLD, SpanType.ITALIC);
	}

	@Test
	public void adjacentItalicBold() {
		assertAdjacentSpan("begin _left_*right* end\n\n", SpanType.ITALIC, SpanType.BOLD);
	}

	@Test
	public void adjacentDeletedSuperscript() {
		assertAdjacentSpan("begin -left-^right^ end\n\n", SpanType.DELETED, SpanType.SUPERSCRIPT);
	}

	@Test
	public void adjacentSuperscriptDeleted() {
		assertAdjacentSpan("begin ^left^-right- end\n\n", SpanType.SUPERSCRIPT, SpanType.DELETED);
	}

	@Test
	public void adjacentUnderlineSubscript() {
		assertAdjacentSpan("begin +left+~right~ end\n\n", SpanType.UNDERLINED, SpanType.SUBSCRIPT);
	}

	@Test
	public void adjacentSubscriptUnderline() {
		assertAdjacentSpan("begin ~left~+right+ end\n\n", SpanType.SUBSCRIPT, SpanType.UNDERLINED);
	}

	@Test
	public void adjacentMonospaceCitation() {
		assertAdjacentSpan("begin {{left}}??right?? end\n\n", SpanType.MONOSPACE, SpanType.CITATION);
	}

	@Test
	public void adjacentCitationMonospace() {
		assertAdjacentSpan("begin ??left??{{right}} end\n\n", SpanType.CITATION, SpanType.MONOSPACE);
	}

	@Test
	public void adjacentLinkCode() {
		assertAdjacentSpan("begin %left%@right@ end\n\n", SpanType.LINK, SpanType.CODE);
	}

	@Test
	public void adjacentCodeLink() {
		assertAdjacentSpan("begin @left@%right% end\n\n", SpanType.CODE, SpanType.LINK);
	}

	@Test
	public void paragraphWithKnownEntityReference() {
		assertParagraphWithEntityReference("–\n\n", "#8211");
	}

	@Test
	public void paragraphWithUnknownEntityReference() {
		assertParagraphWithEntityReference("&unknown;\n\n", "unknown");
	}

	@Test
	public void parapgraphWithUnknownEntityReferenceHavingEscapableChars() {
		assertParagraphWithEntityReference("&#unknown;\n\n", "#unknown");
	}

	@Test
	public void parapgraphWithEscapableChars() {
		assertParagraphWithContent("&\\#unknown\n\n", "&#unknown");
	}

	@Test
	public void paragraphWithEscapedExclamation() {
		assertParagraphWithContent("Exclamation\\!\n\n", "Exclamation!");
	}

	@Test
	public void paragraphWithEscapedPipe() {
		assertParagraphWithContent("Pipe\\|\n\n", "Pipe|");
	}

	@Test
	public void italicBoldItalic() {
		builder.beginDocument();
		builder.beginSpan(SpanType.ITALIC, new Attributes());
		builder.characters("italic");
		builder.endSpan();
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.beginSpan(SpanType.ITALIC, new Attributes());
		builder.characters("bolditalic");
		builder.endSpan();
		builder.endSpan();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("_italic_*_bolditalic_*\n\n", markup);
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

	private void assertNestedSpan(String expected, SpanType outerSpan, SpanType innerSpan) {
		builder.beginDocument();
		builder.characters("begin");
		builder.beginSpan(outerSpan, new Attributes());
		builder.beginSpan(innerSpan, new Attributes());
		builder.characters("span");
		builder.endSpan();
		builder.endSpan();
		builder.characters("end");
		builder.endDocument();

		String markup = out.toString();

		assertEquals(expected, markup);
	}

	private void assertAdjacentSpan(String expected, SpanType leftSpan, SpanType rightSpan) {
		builder.beginDocument();
		builder.characters("begin");
		builder.beginSpan(leftSpan, new Attributes());
		builder.characters("left");
		builder.endSpan();
		builder.beginSpan(rightSpan, new Attributes());
		builder.characters("right");
		builder.endSpan();
		builder.characters("end");
		builder.endDocument();

		String markup = out.toString();

		assertEquals(expected, markup);
	}

	private void assertTableRow(String expectedMarkup, BlockType cellType) {
		assertTableRow(expectedMarkup, cellType, () -> builder.characters("content"));
	}

	private void assertTableRow(String expectedMarkup, BlockType cellType, Runnable cellContentProvider) {
		builder.beginDocument();
		builder.beginBlock(BlockType.TABLE, new Attributes());

		builder.beginBlock(BlockType.TABLE_ROW, new Attributes());

		builder.beginBlock(cellType, new Attributes());
		builder.endBlock();

		builder.beginBlock(cellType, new Attributes());
		cellContentProvider.run();
		builder.endBlock();

		builder.beginBlock(cellType, new Attributes());
		builder.endBlock();

		builder.endBlock();
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals(expectedMarkup, markup);
	}

	private void assertParagraphWithContent(String expected, String characters) {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters(characters);
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals(expected, markup);
	}

	private void assertParagraphWithEntityReference(String expected, String characters) {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.entityReference(characters);
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals(expected, markup);
	}

	private void emitListItemHavingParagraphAndContent(String content) {
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters(content);
		builder.endBlock(); // paragraph
		builder.endBlock(); // list item
	}

	private void assertCodeBlock(String expected, String content) {
		builder.beginDocument();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters(content);
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals(expected, markup);
	}
}
